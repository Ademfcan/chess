package chessserver.Net.ServerMessageHandlers;

import chessserver.ActiveClientManager;
import chessserver.JWTGenerator;
import chessserver.Misc.Tuple;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.UserMessageTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ServerUserMessageHandler extends MessageHandler<UserMessageTypes.ClientRequest> {

    public ServerUserMessageHandler(WebSocketConnection webConnection) {
        super(false, webConnection, UserMessageTypes.ClientRequest.class, MessagePath.Endpoint.SERVER);
    }


    public UserInfo getInfoFromUUID(UUID uuid) throws SQLException {
        return DatabaseConnection.executeQueryReturn(
                String.format("Select %s from %s where %s = ?", ChessDBNames.usersUserInfo, ChessDBNames.usersTable, ChessDBNames.usersUUID),
                rs -> {
                    if(rs.next()){
                        return simpleUnpack(rs.getString(ChessDBNames.usersUserInfo), UserInfo.class);
                    }
                    else{
                        return null;
                    }
                }, DatabaseConnection.uuidToBytes(uuid)
        );
    }


    private boolean doesUserExist(String userName) throws SQLException {
        // check if user already exists
        return DatabaseConnection.executeQueryReturn(
                String.format("Select 1 from %s where %s = ? Limit 1", ChessDBNames.usersTable, ChessDBNames.usersName),
                ResultSet::next, userName);
    }

    private boolean signUp(String userName, String userEmail, String passwordPlainText) throws SQLException {
        // check if user already exists
        if(doesUserExist(userName)) {
            // user already exists
            return false;
        }

        // hash the password
        String hashedPassword = BCrypt.hashpw(passwordPlainText, BCrypt.gensalt());

        UUID randomUUID = UUID.randomUUID();
        UserInfo info = UserInfo.getPartiallyDefaultUserInfo(userName, userEmail, randomUUID);
        UserPreferences defaultPreferences = UserPreferences.getDefaultPreferences();

        // insert the new user into the database
        DatabaseConnection.executeUpdate(
                String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        ChessDBNames.usersTable,
                        ChessDBNames.usersName, ChessDBNames.usersEmail, ChessDBNames.usersPassword, ChessDBNames.usersUUID, ChessDBNames.usersElo,
                        ChessDBNames.usersUserInfo, ChessDBNames.usersUserPreferences),
                userName, userEmail, hashedPassword, DatabaseConnection.uuidToBytes(randomUUID), info.getUserelo(),
                toJson(info), toJson(defaultPreferences));

        return true;
    }

    private void deleteExpired(String device_ID) throws SQLException {
        // delete any possibly expired refresh tokens for this device
        DatabaseConnection.executeUpdate(
                String.format("DELETE FROM %s WHERE %s = ? AND %s < CURRENT_DATE()", ChessDBNames.refreshTokensTable, ChessDBNames.refreshTokenDeviceID, ChessDBNames.refreshTokenExpiresAt), device_ID);
    }

    private @Nullable UUID verifyRefreshToken(String refreshToken, String device_ID) throws SQLException {
        // delete any possibly expired refresh tokens for this device
        deleteExpired(device_ID);

        return DatabaseConnection.executeQueryReturn(
                String.format("Select %s from %s where %s = ? and %s = ?", ChessDBNames.refreshTokenUUID, ChessDBNames.refreshTokensTable, ChessDBNames.refreshToken, ChessDBNames.refreshTokenDeviceID),
                rs -> {
                    if(rs.next()){
                        return DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.refreshTokenUUID));
                    }
                    else{
                        return null;
                    }
                },
                refreshToken, device_ID);
    }


    private void clearRefreshTokensForDeviceAndUUID(UUID userUUID, String deviceID) throws SQLException {
        // clear all refresh tokens for the given user and device
        DatabaseConnection.executeUpdate(
                String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", ChessDBNames.refreshTokensTable, ChessDBNames.refreshTokenUUID, ChessDBNames.refreshTokenDeviceID),
                DatabaseConnection.uuidToBytes(userUUID), deviceID);
    }

    private String createAndAddNewRefreshToken(UUID userUUID, String deviceID) throws SQLException {
        // generate a new refresh token
        String newRefreshToken = TokenGenerator.generateSecureToken();
        assert newRefreshToken.length() == 43 : "Refresh token length is not 43 characters!";

        // add the new refresh token to the database
        DatabaseConnection.executeUpdate(
                String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", ChessDBNames.refreshTokensTable, ChessDBNames.refreshTokenUUID, ChessDBNames.refreshTokenDeviceID, ChessDBNames.refreshToken),
                DatabaseConnection.uuidToBytes(userUUID), deviceID, newRefreshToken);

        return newRefreshToken;
    }



    private @Nullable String login(String userName, String passwordPlainText, String deviceID) throws SQLException {
        Tuple<String, UUID> query = DatabaseConnection.executeQueryReturn(
                String.format("Select %s, %s from %s where %s = ?", ChessDBNames.usersPassword, ChessDBNames.usersUUID, ChessDBNames.usersTable, ChessDBNames.usersName),
                rs -> rs.next()
                        ? new Tuple<>(rs.getString(ChessDBNames.usersPassword), DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.usersUUID)))
                        : null
                , userName);


        if(query != null && BCrypt.checkpw(passwordPlainText, query.first())){
            // password is correct, return a Refresh token, (and update in database)

            // clear all previous refresh tokens for this user and device
            clearRefreshTokensForDeviceAndUUID(query.second(), deviceID);

            // create a new refresh token for this user and device
            return createAndAddNewRefreshToken(query.second(), deviceID);
        }
        else{
            // password is incorrect, return null
            return null;
        }

    }




    @Override
    protected void handleMessage(UserMessageTypes.ClientRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
        try{
            switch (messageOption){
                case LOGIN -> {
                    UserMessagePayloadTypes.LoginPayload loginPayload = message.getTypedMessagePayload();
                    UserMessagePayloadTypes.RefreshAcessTokenPayload refreshToken = new UserMessagePayloadTypes.RefreshAcessTokenPayload(
                            login(loginPayload.userName(), loginPayload.passwordPlaintext(), loginPayload.deviceID()), loginPayload.deviceID()
                    );

                    message.sendExpectedResponse(refreshToken);

                }
                case LOGOUT -> {
                    UserMessagePayloadTypes.RefreshAcessTokenPayload logoutPayload = message.getTypedMessagePayload();
                    // clear all refresh tokens for this user and device
                    UUID userUUID = verifyRefreshToken(logoutPayload.refreshToken(), logoutPayload.deviceID());
                    if(userUUID != null){
                        clearRefreshTokensForDeviceAndUUID(userUUID, logoutPayload.deviceID());
                        message.sendExpectedResponse(new Payload.BooleanPayload(true));
                    }
                    else{
                        message.sendExpectedResponse(new Payload.BooleanPayload(false));
                    }
                }
                case SIGNUP -> {
                    UserMessagePayloadTypes.SignUpPayload signUpPayload = message.getTypedMessagePayload();
                    message.sendExpectedResponse(new Payload.BooleanPayload(
                        signUp(signUpPayload.userName(), signUpPayload.userEmail(), signUpPayload.passwordPlaintext())
                    ));
                }
                case GETJWTTOKEN -> {
                    UserMessagePayloadTypes.RefreshAcessTokenPayload refreshTokenPayload = message.getTypedMessagePayload();
                    UUID userUUID = verifyRefreshToken(refreshTokenPayload.refreshToken(), refreshTokenPayload.deviceID());

                    if(userUUID != null){
                        String jwtToken = JWTGenerator.createAccessToken(userUUID);
                        message.sendExpectedResponse(new UserMessagePayloadTypes.JWTAcessTokenPayload(jwtToken));


                        // not the best place to put this, but now add the client as a logged in user
                        ActiveClientManager.addLoggedInUser(userUUID, message.getSendingSession());
                    }
                    else{
                        message.sendExpectedResponse(new UserMessagePayloadTypes.JWTAcessTokenPayload(null));
                    }
                }
                case REFRESHVALID -> {
                    UserMessagePayloadTypes.RefreshAcessTokenPayload refreshTokenPayload = message.getTypedMessagePayload();
                    message.sendExpectedResponse(new Payload.BooleanPayload(
                            verifyRefreshToken(refreshTokenPayload.refreshToken(), refreshTokenPayload.deviceID()) != null));
                }
                case CHECKUSERNAMEOPEN -> {
                    String userName = ((Payload.StringPayload) message.getTypedMessagePayload()).payload();
                    DatabaseConnection.SqlErroringConsumer<ResultSet> onResult = (ResultSet rs) -> {
                        rs.next(); // always one row
                        boolean exists = rs.getBoolean(1);
                        // send true if username is open (i.e., does not exist)
                        message.sendExpectedResponse(new Payload.BooleanPayload(!exists));
                    };

                    String selectQuery = String.format("SELECT EXISTS (SELECT 1 FROM %s WHERE %s = ?)", ChessDBNames.usersTable, ChessDBNames.usersName);
                    DatabaseConnection.executeQuery(selectQuery, onResult, userName);
                }
            }

        } catch (SQLException e) {
            message.sendResponse(GenericServerResponses.SQL_ERROR,
                    new Payload.StringPayload("SQL Error: " + e.getMessage()));
            logger.error("SQL Error: ", e);
        }
    }

    @Override
    protected void onClose(CloseReason closeReason, Session closedSession) {
        // remove the user from the active client manager
        ActiveClientManager.removeLoggedInSession(closedSession);
        super.onClose(closeReason, closedSession);
    }

}
