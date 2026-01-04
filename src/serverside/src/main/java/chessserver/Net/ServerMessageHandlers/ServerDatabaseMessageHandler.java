package chessserver.Net.ServerMessageHandlers;

import chessserver.ActiveClientManager;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.ChessRepresentations.PlayerInfo;
import chessserver.Communication.User;
import chessserver.Friends.ServerFriendData;
import chessserver.Friends.FriendDataResponse;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.DatabaseMessageTypes;
import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;

import java.sql.*;
import java.util.*;

public class ServerDatabaseMessageHandler extends MessageHandler<DatabaseMessageTypes.ClientRequest> {

    public ServerDatabaseMessageHandler(WebSocketConnection webConnection) {
        super(true, webConnection, DatabaseMessageTypes.ClientRequest.class, MessagePath.Endpoint.SERVER);
    }


    private void addFriendRequest(UUID fromUUID, UUID toUUID) throws SQLException {
        String query = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, ?)",
                ChessDBNames.friendRequestsTable,
                ChessDBNames.fromUUID,
                ChessDBNames.toUUID
        );
        DatabaseConnection.executeUpdate(query, DatabaseConnection.uuidToBytes(fromUUID), DatabaseConnection.uuidToBytes(toUUID));
    }

    private void removeFriendRequest(UUID fromUUID, UUID toUUID) throws SQLException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ? AND %s = ?",
                ChessDBNames.friendRequestsTable,
                ChessDBNames.fromUUID,
                ChessDBNames.toUUID
        );
        DatabaseConnection.executeUpdate(query, DatabaseConnection.uuidToBytes(fromUUID), DatabaseConnection.uuidToBytes(toUUID));
    }

    private boolean isFriendRequest(UUID fromUUID, UUID toUUID) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ?",
                ChessDBNames.friendRequestsTable,
                ChessDBNames.fromUUID,
                ChessDBNames.toUUID
        );
        return DatabaseConnection.executeQueryReturn(query, ResultSet::next, DatabaseConnection.uuidToBytes(fromUUID), DatabaseConnection.uuidToBytes(toUUID));
    }

    private List<UUID> getAllIncomingFriendRequests(UUID toUUID) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                ChessDBNames.fromUUID,
                ChessDBNames.friendRequestsTable,
                ChessDBNames.toUUID
        );
        return DatabaseConnection.executeQueryReturn(query, rs -> {
            List<UUID> results = new ArrayList<>();
            while (rs.next()) {
                results.add(DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.fromUUID)));
            }
            return results;
        }, DatabaseConnection.uuidToBytes(toUUID));
    }

    private List<UUID> getAllOutgoingFriendRequests(UUID fromUUID) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                ChessDBNames.toUUID,
                ChessDBNames.friendRequestsTable,
                ChessDBNames.fromUUID
        );
        return DatabaseConnection.executeQueryReturn(query, rs -> {
            List<UUID> results = new ArrayList<>();
            while (rs.next()) {
                results.add(DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.toUUID)));
            }
            return results;
        }, DatabaseConnection.uuidToBytes(fromUUID));
    }

    private void addFriends(UUID friendAUUID, UUID friendBUUID) throws SQLException {
        UUID lesser = friendAUUID.compareTo(friendBUUID) < 0 ? friendAUUID : friendBUUID;
        UUID greater = friendAUUID.compareTo(friendBUUID) < 0 ? friendBUUID : friendAUUID;

        String query = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, ?)",
                ChessDBNames.friendshipsTable,
                ChessDBNames.friendAUUID,
                ChessDBNames.friendBUUID
        );
        DatabaseConnection.executeUpdate(query, DatabaseConnection.uuidToBytes(lesser), DatabaseConnection.uuidToBytes(greater));
    }

    private void removeFriends(UUID friendAUUID, UUID friendBUUID) throws SQLException {
        UUID lesser = friendAUUID.compareTo(friendBUUID) < 0 ? friendAUUID : friendBUUID;
        UUID greater = friendAUUID.compareTo(friendBUUID) < 0 ? friendBUUID : friendAUUID;

        String query = String.format(
                "DELETE FROM %s WHERE %s = ? AND %s = ?",
                ChessDBNames.friendshipsTable,
                ChessDBNames.friendAUUID,
                ChessDBNames.friendBUUID
        );
        DatabaseConnection.executeUpdate(query, DatabaseConnection.uuidToBytes(lesser), DatabaseConnection.uuidToBytes(greater));
    }

    private boolean isFriends(UUID friendAUUID, UUID friendBUUID) throws SQLException {
        UUID lesser = friendAUUID.compareTo(friendBUUID) < 0 ? friendAUUID : friendBUUID;
        UUID greater = friendAUUID.compareTo(friendBUUID) < 0 ? friendBUUID : friendAUUID;

        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ?",
                ChessDBNames.friendshipsTable,
                ChessDBNames.friendAUUID,
                ChessDBNames.friendBUUID
        );
        return DatabaseConnection.executeQueryReturn(query, ResultSet::next, DatabaseConnection.uuidToBytes(lesser), DatabaseConnection.uuidToBytes(greater));
    }

    private List<UUID> getAllFriends(UUID userUUID) throws SQLException {
        String query = String.format(
                "SELECT %s AS FriendUUID FROM %s WHERE %s = ? UNION SELECT %s AS FriendUUID FROM %s WHERE %s = ?",
                ChessDBNames.friendBUUID, ChessDBNames.friendshipsTable, ChessDBNames.friendAUUID,
                ChessDBNames.friendAUUID, ChessDBNames.friendshipsTable, ChessDBNames.friendBUUID
        );

        return DatabaseConnection.executeQueryReturn(query, rs -> {
            List<UUID> friends = new ArrayList<>();
            while (rs.next()) {
                friends.add(DatabaseConnection.bytesToUUID(rs.getBytes("FriendUUID")));
            }
            return friends;
        }, DatabaseConnection.uuidToBytes(userUUID), DatabaseConnection.uuidToBytes(userUUID));
    }


    private <T> List<UserMessagePayloadTypes.Friend> getUUIDsOrUsernames(Class<T> clazz, List<T> uuidsOrUsernames) throws SQLException{
        if (uuidsOrUsernames.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(", ", Collections.nCopies(uuidsOrUsernames.size(), "?"));
        String query = String.format(
                "SELECT %s, %s FROM %s WHERE %s IN (%s)",
                ChessDBNames.usersUUID,
                ChessDBNames.usersName,
                ChessDBNames.usersTable,
                ChessDBNames.usersName,
                placeholders
        );

        Object[] arg;
        if(clazz.isInstance(UUID.class)){
            arg = ((List<UUID>) uuidsOrUsernames).stream().map(DatabaseConnection::uuidToBytes).toArray();
        }
        else{
            arg = uuidsOrUsernames.toArray();
        }

        return DatabaseConnection.executeQueryReturn(query, rs -> {
            List<UserMessagePayloadTypes.Friend> users = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString(ChessDBNames.usersName);
                byte[] uuidBytes = rs.getBytes(ChessDBNames.usersUUID);
                users.add(new UserMessagePayloadTypes.Friend(name, DatabaseConnection.bytesToUUID(uuidBytes)));
            }
            return users;
        }, arg);
    }


    private List<GameInfo> getGamesForUUID(UUID user, boolean whitePlayer, int maxCount) throws SQLException{
        return DatabaseConnection.executeQueryReturn(
                String.format("Select * from %s where %s=? Order By %s DESC Limit ?",
                        ChessDBNames.chessGamesTable,
                        whitePlayer ? ChessDBNames.whitePlayerUUID : ChessDBNames.blackPlayerUUID,
                        ChessDBNames.gameCreatedAt),
                this::parseGameResults,
                DatabaseConnection.uuidToBytes(user),
                maxCount);
    }

    private List<GameInfo> getGamesForUUID(UUID user, int maxCount) throws SQLException{
        return DatabaseConnection.executeQueryReturn(
                String.format("Select * from %s where %s=? or %s=? Order By %s DESC Limit ?",
                        ChessDBNames.chessGamesTable,
                        ChessDBNames.whitePlayerUUID,
                        ChessDBNames.blackPlayerUUID,
                        ChessDBNames.gameCreatedAt),
                this::parseGameResults,
                DatabaseConnection.uuidToBytes(user),
                DatabaseConnection.uuidToBytes(user),
                maxCount);
    }

    private List<GameInfo> parseGameResults(ResultSet rs) throws SQLException {
        List<GameInfo> games = new ArrayList<>();
        while (rs.next()) {
            GameInfo info = new GameInfo(
                    DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.gameUUID)),
                    rs.getString(ChessDBNames.gameName),
                    simpleUnpack(rs.getString(ChessDBNames.whitePlayer), PlayerInfo.class),
                    simpleUnpack(rs.getString(ChessDBNames.blackPlayer), PlayerInfo.class),
                    rs.getString(ChessDBNames.gamePGN)
            );
            games.add(info);
        }
        return games;
    }


    public void addSavedGame(GameInfo gameToSave, boolean isLocal) throws SQLException{
        DatabaseConnection.executeUpdate(
                String.format("Insert into %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    ChessDBNames.chessGamesTable,
                    ChessDBNames.gameUUID,
                    ChessDBNames.gameName,
                    ChessDBNames.gamePGN,
                    ChessDBNames.whitePlayer,
                    ChessDBNames.whitePlayerUUID,
                    ChessDBNames.blackPlayer,
                    ChessDBNames.blackPlayerUUID,
                    ChessDBNames.localGame
                ),
                DatabaseConnection.uuidToBytes(gameToSave.gameUUID()),
                gameToSave.gameName(),
                gameToSave.gamePgn(),
                toJson(gameToSave.whitePlayer()),
                DatabaseConnection.uuidToBytes(gameToSave.whitePlayer().playerID()),
                toJson(gameToSave.blackPlayer()),
                DatabaseConnection.uuidToBytes(gameToSave.blackPlayer().playerID()),
                isLocal
        );
    }






    @Override
    protected void handleMessage(DatabaseMessageTypes.ClientRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
        try{
            switch (messageOption) {
                case LOADFULLUSER -> {
                    Payload.IntegerPayload maxGames = message.getTypedMessagePayload();

                    List<GameInfo> games = getGamesForUUID(jwtUUID, maxGames.payload());

                    DatabaseConnection.SqlErroringConsumer<ResultSet> onResult = (rs) -> {
                        if (rs.next()) {
                            message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UserWGamesPayload(
                                    new UserWGames(
                                            new User(simpleUnpack(rs.getString(ChessDBNames.usersUserInfo), UserInfo.class),
                                                simpleUnpack(rs.getString(ChessDBNames.usersUserPreferences), UserPreferences.class)),
                                        games
                                    )));

                        } else {
                            message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UserWGamesPayload(null));
                        }
                    };

                    String readQuery = String.format("SELECT %s, %s FROM %s WHERE %s = ?",
                            ChessDBNames.usersUserPreferences, ChessDBNames.usersUserInfo,
                            ChessDBNames.usersTable,
                            ChessDBNames.usersUUID);

                    DatabaseConnection.executeQuery(readQuery, onResult, DatabaseConnection.uuidToBytes(jwtUUID));
                }
                case GETUSER -> {
                    DatabaseConnection.SqlErroringConsumer<ResultSet> onResult = (rs) -> {
                        if (rs.next()) {
                            message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UserPayload(
                                    new User(simpleUnpack(rs.getString(ChessDBNames.usersUserInfo), UserInfo.class),
                                            simpleUnpack(rs.getString(ChessDBNames.usersUserPreferences), UserPreferences.class))));

                        } else {
                            message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UserPayload(null));
                        }
                    };

                    String readQuery = String.format("SELECT %s, %s FROM %s WHERE %s = ?",
                            ChessDBNames.usersUserPreferences, ChessDBNames.usersUserInfo,
                            ChessDBNames.usersTable,
                            ChessDBNames.usersUUID);

                    DatabaseConnection.executeQuery(readQuery, onResult, DatabaseConnection.uuidToBytes(jwtUUID));
                }

                case UPDATEUSER -> {
                    DatabaseMessagePayloadTypes.UserPayload databaseRequestPayload = message.getTypedMessagePayload();
                    String dataUpdateQuery = String.format(
                            "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                            ChessDBNames.usersTable,
                            ChessDBNames.usersName,
                            ChessDBNames.usersElo,
                            ChessDBNames.usersEmail,
                            ChessDBNames.usersUserPreferences,
                            ChessDBNames.usersUserInfo,
                            ChessDBNames.usersUUID
                    );

                    int rowsUpdated = DatabaseConnection.executeUpdate(dataUpdateQuery,
                            databaseRequestPayload.user().userInfo().getUserName(),
                            databaseRequestPayload.user().userInfo().getUserelo(),
                            databaseRequestPayload.user().userInfo().getUserEmail(),
                            toJson(databaseRequestPayload.user().preferences()),
                            toJson(databaseRequestPayload.user().userInfo()),
                            DatabaseConnection.uuidToBytes(databaseRequestPayload.user().userInfo().getUuid()));

                    if (rowsUpdated > 0) {
                        message.sendResponse(DatabaseMessageTypes.ServerRequest.SQLMESSAGE, new Payload.StringPayload(("User updated successfully")));
                    }
                }

                case UPDATEUSERINFO -> {
                    DatabaseMessagePayloadTypes.UserInfoPayload userInfoRequestPayload = message.getTypedMessagePayload();
                    String dataUpdateQuery = String.format(
                            "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                            ChessDBNames.usersTable,
                            ChessDBNames.usersName,
                            ChessDBNames.usersElo,
                            ChessDBNames.usersEmail,
                            ChessDBNames.usersUserInfo,
                            ChessDBNames.usersUUID
                    );

                    int rowsUpdated = DatabaseConnection.executeUpdate(dataUpdateQuery,
                            userInfoRequestPayload.userInfo().getUserName(),
                            userInfoRequestPayload.userInfo().getUserelo(),
                            userInfoRequestPayload.userInfo().getUserEmail(),
                            toJson(userInfoRequestPayload.userInfo()),
                            DatabaseConnection.uuidToBytes(jwtUUID));

                    if (rowsUpdated > 0) {
                        message.sendResponse(DatabaseMessageTypes.ServerRequest.SQLMESSAGE, new Payload.StringPayload(("User updated successfully")));
                    }
                }

                case UPDATEUSERPREF -> {
                    DatabaseMessagePayloadTypes.UserPrefPayload userPrefRequestPayload = message.getTypedMessagePayload();
                    String dataUpdateQuery = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                            ChessDBNames.usersTable,
                            ChessDBNames.usersUserPreferences,
                            ChessDBNames.usersUUID);

                    int rowsUpdated = DatabaseConnection.executeUpdate(dataUpdateQuery,
                            toJson(userPrefRequestPayload.userPreferences()),
                            DatabaseConnection.uuidToBytes(jwtUUID));

                    if (rowsUpdated > 0) {
                        message.sendResponse(DatabaseMessageTypes.ServerRequest.SQLMESSAGE, new Payload.StringPayload(("User updated successfully")));
                    }

                    System.out.println("RoWS UPDATEDDDDD: " + rowsUpdated);
                }


                case SENDFRIENDREQUEST -> {
                    Payload.StringPayload incomingUserNamePayload = message.getTypedMessagePayload();
                    String incomingUserName = incomingUserNamePayload.payload();

                    String findUserUUIDQuery = String.format("SELECT %s FROM %s WHERE %s = ?", ChessDBNames.usersUUID, ChessDBNames.usersTable, ChessDBNames.usersName);

                    UUID incomingUUID = DatabaseConnection.executeQueryReturn(findUserUUIDQuery,
                            rs -> rs.next() ? DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.usersUUID)) : null,
                            incomingUserName
                    );

                    if (incomingUUID != null) {
                        // handle user found
                        addFriendRequest(jwtUUID, incomingUUID);

                        if (ActiveClientManager.isClientActive(incomingUUID)) {
                            // requested friend is online
                            sendMessage(new MessageConfig(new Message(DatabaseMessageTypes.ServerRequest.UPDATEFRIENDS,
                                            new Payload.Empty()))
                                                .to(ActiveClientManager.getLoggedInSession(incomingUUID)));
                        }

                        message.sendResponse(DatabaseMessageTypes.ServerRequest.UPDATEFRIENDS, new Payload.Empty());

                    }

                }


                case READINCOMINGFRIENDREQUESTS -> {
                    List<UUID> friendRequests = getAllIncomingFriendRequests(jwtUUID);
                    message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UUIDSPayload(friendRequests));
                }

                case READOUTGOINGFRIENDREQUESTS -> {
                    List<UUID> friendRequests = getAllOutgoingFriendRequests(jwtUUID);
                    message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UUIDSPayload(friendRequests));
                }

                case READFRIENDS -> {
                    List<UUID> friends = getAllFriends(jwtUUID);
                    message.sendExpectedResponse(new DatabaseMessagePayloadTypes.UUIDSPayload(friends));
                }

                case GETUUIDS -> {
                    Payload.StringListPayload friendUsernamesPayload = message.getTypedMessagePayload();
                    List<UserMessagePayloadTypes.Friend> users = getUUIDsOrUsernames(String.class, friendUsernamesPayload.payload());
                    message.sendExpectedResponse(new UserMessagePayloadTypes.FriendListPayload(users));
                }

                case GETUSERNAMES -> {
                    DatabaseMessagePayloadTypes.UUIDSPayload friendUUIDSPayload = message.getTypedMessagePayload();
                    List<UserMessagePayloadTypes.Friend> users = getUUIDsOrUsernames(UUID.class, friendUUIDSPayload.uuids());
                    message.sendExpectedResponse(new UserMessagePayloadTypes.FriendListPayload(users));
                }

                case GETFRIENDDATA -> {
                    DatabaseMessagePayloadTypes.UUIDSPayload friendUUIDSPayload = message.getTypedMessagePayload();
                    List<UUID> friendUUIDs = friendUUIDSPayload.uuids();

                    if (friendUUIDs.isEmpty()) {
                        message.sendExpectedResponse(new UserMessagePayloadTypes.FriendDataResponsePayload(
                                new FriendDataResponse(new ArrayList<>())));
                        return;
                    }

                    String placeholders = String.join(", ", Collections.nCopies(friendUUIDs.size(), "?"));
                    String query = String.format(
                            "SELECT %s, %s, %s, %s FROM %s WHERE %s IN (%s)",
                            ChessDBNames.usersUUID,
                            ChessDBNames.usersName,
                            ChessDBNames.usersUserPreferences,
                            ChessDBNames.usersUserInfo,
                            ChessDBNames.usersTable,
                            ChessDBNames.usersUUID,
                            placeholders
                    );

                    // Convert UUIDs to byte[] array for prepared statement
                    Object[] uuidBytesArgs = friendUUIDs.stream()
                            .map(DatabaseConnection::uuidToBytes)
                            .toArray();

                    List<ServerFriendData> results = DatabaseConnection.executeQueryReturn(query, rs -> {
                        List<ServerFriendData> friendData = new ArrayList<>();
                        while (rs.next()) {
                            UUID uuid = DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.usersUUID));
                            List<GameInfo> shortPreview = getGamesForUUID(uuid, 5);

                            boolean isOnline = ActiveClientManager.isClientActive(uuid);
                            String userName = rs.getString(ChessDBNames.usersName);
                            UserPreferences userPreferences = simpleUnpack(rs.getString(ChessDBNames.usersUserPreferences), UserPreferences.class);
                            UserInfo userInfo = simpleUnpack(rs.getString(ChessDBNames.usersUserInfo), UserInfo.class);

                            friendData.add(new ServerFriendData(isOnline, uuid, userName, new UserWGames(new User(userInfo, userPreferences), shortPreview)));
                        }
                        return friendData;
                    }, uuidBytesArgs);

                    message.sendExpectedResponse(new UserMessagePayloadTypes.FriendDataResponsePayload(
                            new FriendDataResponse(results)));
                }


                case GETRANK -> {
                    Payload.IntegerPayload UUIDPayload = message.getTypedMessagePayload();
                    int UUID = UUIDPayload.payload();

                    String query = String.format(
                            "SELECT 1 + COUNT(*) AS rank FROM %s WHERE %s > (SELECT %s FROM users WHERE %s = ?)",
                            ChessDBNames.usersTable, ChessDBNames.usersElo, ChessDBNames.usersElo, ChessDBNames.usersUUID);


                    int rank = DatabaseConnection.executeQueryReturn(query, rs -> {
                        if (rs.next()) {
                            return rs.getInt("rank");
                        }
                        return -1;
                    }, UUID);

                    message.sendExpectedResponse(new Payload.IntegerPayload(rank));

                }

                case MATCHALLUSERNAMES -> {
                    Payload.StringPayload usernameSnippetPayload = message.getTypedMessagePayload();
                    String usernameSnippet = usernameSnippetPayload.payload();

                    String query = String.format(
                            "SELECT %s, %s, %s, %s FROM %s WHERE LOWER(%s) LIKE LOWER(?)",
                            ChessDBNames.usersUUID,
                            ChessDBNames.usersName,
                            ChessDBNames.usersUserPreferences,
                            ChessDBNames.usersUserInfo,
                            ChessDBNames.usersTable,
                            ChessDBNames.usersName
                    );


                    List<ServerFriendData> results = DatabaseConnection.executeQueryReturn(query, rs -> {
                        List<ServerFriendData> friendData = new ArrayList<>();
                        while (rs.next()) {
                            UUID uuid = DatabaseConnection.bytesToUUID(rs.getBytes(ChessDBNames.usersUUID));
                            List<GameInfo> shortPreview = getGamesForUUID(uuid, 5);
                            boolean isOnline = ActiveClientManager.isClientActive(uuid);
                            String userName = rs.getString(ChessDBNames.usersName);
                            UserPreferences userPreferences = simpleUnpack(rs.getString(ChessDBNames.usersUserPreferences), UserPreferences.class);
                            UserInfo userInfo = simpleUnpack(rs.getString(ChessDBNames.usersUserInfo), UserInfo.class);

                            friendData.add(new ServerFriendData(isOnline, uuid, userName, new UserWGames(new User(userInfo, userPreferences),shortPreview)));
                        }
                        return friendData;
                    }, "%"+usernameSnippet+"%");

                    message.sendExpectedResponse(new UserMessagePayloadTypes.FriendDataResponsePayload(
                            new FriendDataResponse(results)));

                }

                case SENDACCEPTEDFRIENDREQUEST -> {
                    DatabaseMessagePayloadTypes.UUIDPayload acceptedUUIDPayload = message.getTypedMessagePayload();
                    UUID acceptedUUID = acceptedUUIDPayload.uuid();

                    boolean foundUUID = DatabaseConnection.executeQueryReturn(
                            String.format("SELECT 1 FROM %s WHERE %s = ?", ChessDBNames.usersTable, ChessDBNames.usersUUID),
                            ResultSet::next,
                            acceptedUUID
                    );

                    if (foundUUID) {
                        removeFriendRequest(acceptedUUID, jwtUUID);
                        addFriends(acceptedUUID, jwtUUID);


                        if (ActiveClientManager.isClientActive(acceptedUUID)) {
                            // Friend is online, notify them
                            sendMessage(new MessageConfig(
                                    new Message(DatabaseMessageTypes.ServerRequest.UPDATEFRIENDS, new Payload.Empty())
                                    ).to(ActiveClientManager.getLoggedInSession(acceptedUUID))
                            );
                        }

                        message.sendResponse(
                                DatabaseMessageTypes.ServerRequest.UPDATEFRIENDS,
                                new Payload.Empty());


                    } else {
                        // User with acceptedUUID not found
                        message.sendResponse(DatabaseMessageTypes.ServerRequest.SQLMESSAGE, new Payload.StringPayload("User no longer exists!"));
                    }

                }
                case GETCHESSGAMES -> {
                    Payload.IntegerPayload maxGames = message.getTypedMessagePayload();
                    message.sendExpectedResponse(new DatabaseMessagePayloadTypes.GamesPayload(getGamesForUUID(jwtUUID, maxGames.payload())));
                }
                case SaveChessGame -> {
                    DatabaseMessagePayloadTypes.SaveGamePayload gamePayload = message.getTypedMessagePayload();
                    addSavedGame(gamePayload.game(), gamePayload.isLocal());
                }
                case SaveChessGames -> {
                    DatabaseMessagePayloadTypes.SaveGamesPayload gamesPayload = message.getTypedMessagePayload();
                    for(DatabaseMessagePayloadTypes.SaveGamePayload gamePayload : gamesPayload.games()) {
                        addSavedGame(gamePayload.game(), gamePayload.isLocal());
                    }
                }
            }
        }
        catch (SQLException e){
            message.sendResponse(GenericServerResponses.SQL_ERROR, new Payload.StringPayload(e.getMessage()));
            logger.error("Sql error: ", e);
        }
    }

}
