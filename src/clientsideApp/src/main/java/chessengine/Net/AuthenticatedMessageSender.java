package chessengine.Net;

import chessengine.Crypto.DeviceId;
import chessserver.Net.AuthenticationPayload;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageHandler;
import chessserver.Net.WebSocketConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class AuthenticatedMessageSender{
    private static final Logger logger = LogManager.getLogger("Authenticated_Message_Sender");
    private final MessageHandler<?> sender;
    private final JWTManager JWTManager;

    public AuthenticatedMessageSender(MessageHandler<?> sender, JWTManager JWTManager) {
        this.sender = sender;
        this.JWTManager = JWTManager;
    }


    private void handleAuthFailure(MessageConfig failedMessage){
        System.out.println("Authentication failed, trying to retrieve access token");
        JWTManager.requestAccessToken(true).whenComplete((accessToken, throwable) -> {
            System.out.println("Access token retrieved: " + (accessToken));
            if(accessToken != null){
                failedMessage.getMessage().updateAuthentication(new AuthenticationPayload.JWTAuthentication(accessToken, DeviceId.DEVICE_ID));
                sender.sendMessage(failedMessage);
            }
            else{
                // failed to retrieve access token, handled externally
            }
        }).exceptionally(this::throwAndExit);
    }


    public void sendAuthenticatedRetryableMessage(MessageConfig config) {
        Consumer<WebSocketConnection.MessageResponseStatus> onStatusResponse = config.getOnStatusResponse();
        Consumer<WebSocketConnection.MessageResponseStatus> retryResponse = (WebSocketConnection.MessageResponseStatus status) -> {
            if(status.response() == WebSocketConnection.MessageResponse.AUTHENTICATION_FAILED){
                handleAuthFailure(config);
            }
        };

        if(onStatusResponse != null) {
            // if there is already a status response handler, we need to chain it
            retryResponse = retryResponse.andThen(onStatusResponse);
        }
        config.onStatusResponse(retryResponse);

        sendAuthenticatedMessage(config, () -> {
            config.getOnStatusResponse().accept(new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.FAILED_TO_SEND, "Failed to get jwt token"));
        });
    }

    public void sendAuthenticatedMessage(MessageConfig config, Runnable onFailure) {
        JWTManager.requestAccessToken(false).whenComplete((accessToken, throwable) -> {
            if(accessToken != null){
                config.getMessage().updateAuthentication(new AuthenticationPayload.JWTAuthentication(accessToken, DeviceId.DEVICE_ID));
                sender.sendMessage(config);
            }
            else{
                // failed to retrieve access token, handled externally
                onFailure.run();
            }
        }).exceptionally(this::throwAndExit);
    }


    private String throwAndExit(Throwable ex){
        logger.error("Error during authenticated message", ex);
        System.exit(1);
        return null;
    }

}
