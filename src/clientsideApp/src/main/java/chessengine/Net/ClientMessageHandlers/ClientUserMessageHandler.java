package chessengine.Net.ClientMessageHandlers;

import chessengine.App;
import chessengine.Crypto.DeviceId;
import chessserver.Friends.Friend;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.UserMessageTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import javafx.application.Platform;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Handles user-related messages received from the server and provides methods
 * for client-side user actions such as login, signup, and friend management.
 */
public class ClientUserMessageHandler extends MessageHandler<UserMessageTypes.ServerRequest> {

    /**
     * Constructs a new ClientUserMessageHandler.
     *
     * @param connection The WebSocket connection to the server.
     */
    public ClientUserMessageHandler(WebSocketConnection connection) {
        super(false, connection, UserMessageTypes.ServerRequest.class, MessagePath.Endpoint.CLIENT);
    }

    /**
     * Attempts to log in to the server with the given username and password.
     * If the login is successful, the server will return a refresh token.
     * If the login fails, the server will return null.
     *
     * @param username          The username to log in with.
     * @param passwordPlaintext The plaintext password to log in with.
     * @param onLoginResult     A callback that receives an Optional containing the refresh token if login is successful, or empty if login fails.
     */
    public void login(String username, String passwordPlaintext, Consumer<Optional<String>> onLoginResult) {
        sendMessage(new MessageConfig(new Message(UserMessageTypes.ClientRequest.LOGIN, new UserMessagePayloadTypes.LoginPayload(username, passwordPlaintext, DeviceId.DEVICE_ID))).onDataResponse(
                (UserMessagePayloadTypes.RefreshAcessTokenPayload refreshTokenPayload) -> {
                    Optional<String> refreshToken = Optional.empty();
                    if (refreshTokenPayload.refreshToken() != null) {
                        refreshToken = Optional.of(refreshTokenPayload.refreshToken());
                    }

                    onLoginResult.accept(refreshToken);
                }
        ));
    }

    /**
     * Attempts to sign up a new user with the given username, email, and password.
     * If the signup is successful, the server will confirm the action.
     *
     * @param username          The username for the new account.
     * @param userEmail         The email address for the new account.
     * @param passwordPlaintext The plaintext password for the new account.
     * @param onSignupResult    A callback that receives a boolean indicating whether the signup was successful.
     */
    public void signup(String username, String userEmail, String passwordPlaintext, Consumer<Boolean> onSignupResult) {
        sendMessage(new MessageConfig(new Message(UserMessageTypes.ClientRequest.SIGNUP,
                new UserMessagePayloadTypes.SignUpPayload(username, userEmail, passwordPlaintext, DeviceId.DEVICE_ID))).onDataResponse((Payload.BooleanPayload isSuccess) -> {
            onSignupResult.accept(isSuccess.payload());
        }));
    }

    public void getJWTToken(String refreshToken, Consumer<Optional<String>> onTokenResult) {
        // Requests a JWT token from the server using the provided refresh token.

        sendMessage(new MessageConfig(new Message(UserMessageTypes.ClientRequest.GETJWTTOKEN,
                new UserMessagePayloadTypes.RefreshAcessTokenPayload(refreshToken, DeviceId.DEVICE_ID))).onDataResponse((UserMessagePayloadTypes.JWTAcessTokenPayload payload) -> {
            onTokenResult.accept(payload.JwtToken() != null ? Optional.of(payload.JwtToken()) : Optional.empty());
        }));
    }

    /**
     * Handles incoming messages from the server based on the message type.
     *
     * @param messageOption The type of the server request message.
     * @param message       The message received from the server.
     */
    @Override
    protected void handleMessage(UserMessageTypes.ServerRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
    }
}