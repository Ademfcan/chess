package chessserver.Net;

import chessserver.User.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class WebSocketConnection {
    private static final int RetryCount = 3;
    protected static final Logger logger = LogManager.getLogger(WebSocketConnection.class.getSimpleName());

    private static final Consumer<MessageResponseStatus> defaultHandler = status -> {
        logger.info("Response: {} | Message: {}", status.response(), status.message());
    };


    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<HandlerConfig, MessageHandler<?>> handlers = Collections.synchronizedMap(new HashMap<>());
    private final Map<UUID, Consumer<MessageResponseStatus>> responseHandlers = Collections.synchronizedMap(new HashMap<>());
    public WebSocketConnection(){
    }

    public abstract void connect() throws DeploymentException, IOException;

    public abstract void disconnect() throws IOException;
    protected abstract Session getDefaultSession();

    public boolean isDefaultConnected() {
        return getDefaultSession() != null && getDefaultSession().isOpen();
    }

    public boolean tryConnectServer(){
        try {
            if (isDefaultConnected()) {
                disconnect();
            }
            connect();
            return true;
        } catch (Exception e) {

            logger.warn("Reconnect failed");
            return false;
        }

    }

    public void registerHandler(String handlerType, MessagePath.Endpoint handlerEndpoint, MessageHandler<?> handler) {
        HandlerConfig config = new HandlerConfig(handlerType, handlerEndpoint);
        if(!handlers.containsKey(config)){
            this.handlers.put(config, handler);
        }
        else{
            throw new IllegalArgumentException("Handler with: Type: %s Endpoint: %s already registered".formatted(handlerType, handlerEndpoint));
        }
    }

    private void ensureHandled(Message message){
        if(!message.isHandled()){
            throw new IllegalStateException("Message was not handled, cannot proceed!");
        }
    }


    // receiving messages



    // general receiving method for incoming bytes

    public void onIncomingBytes(ByteBuffer bytes, Session sendingSession) {
        // handle incoming bytes as a message
        if (bytes == null || bytes.remaining() == 0) {
            throw new RuntimeException("Received empty message from " + sendingSession.getId());
        }

        MessageFlag.PayloadWFlag payloadWFlag = MessageFlag.read(bytes);

        switch (payloadWFlag.flag()){
            case MESSAGE -> {
                onIncomingMessage(payloadWFlag.payload(), sendingSession);
            }
            case STATUS -> {
                onIncomingStatus(payloadWFlag.payload());
            }
        }
    }


    // receiving "Message" messages

    public void onIncomingMessage(byte[] messageB, Session sendingSession) {
        try {
            Message message = mapper.readValue(messageB, Message.class);

            System.out.println("Received message: " + message);

            // link the message to the session it was sent from
            message.setSendingSession(sendingSession);

            // if try handle static suceeds, then the message was handled by a static handler
            MessageResponseStatus statusResponse;
            try{
                statusResponse = MessageHandler.tryHandleStatic(message);
            }
            catch(IllegalArgumentException ignored){
                String handlerType = message.getHandlerType();
                MessagePath.Endpoint toEndpoint = message.getMessagePath().to;
                HandlerConfig config = new HandlerConfig(handlerType, toEndpoint);

                if(handlers.containsKey(config)){
                    MessageHandler<?> messageHandler = handlers.get(config);
                    statusResponse = messageHandler.handleMessage(message);

                }
                else{
                    throw new IllegalStateException("Missing Handler for: Type: %s Endpoint: %s".formatted(handlerType, toEndpoint));
                }
            }

            sendStatusResponse(message, statusResponse);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // receiving "Status" responses
    public void onIncomingStatus(byte[] statusB) {
        try {


            StatusResponse response = mapper.readValue(statusB, StatusResponse.class);
            System.out.println("Incoming status: " + response);

            defaultHandler.accept(response.status());

            if(responseHandlers.containsKey(response.responseID())){
                responseHandlers.remove(response.responseID()).accept(response.status());
            }

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // sending messages





    // sending "Message" messages

    public boolean sendMessage(@Nullable Session session, Message message, @Nullable Consumer<MessageResponseStatus> responseHandler) {
        try {
            if(session == null){
                session = getDefaultSession();
            }

            System.out.println("Sending message: " + message);

            byte[] messageB = mapper.writeValueAsBytes(message);
            ByteBuffer messageBuffer = MessageFlag.write(MessageFlag.Flags.MESSAGE, messageB);

            if(sendBytes(session, messageBuffer, RetryCount)){
                // only register response handler if message was sent
                if(responseHandler != null) {
                    // ensure message is handled before registering response handler
                    responseHandlers.put(message.getMessageID(), responseHandler);
                }
                return true;
            }
            else{
                if(responseHandler != null){
                    responseHandler.accept(new MessageResponseStatus(MessageResponse.FAILED_TO_SEND, "Failed to send message to session: %s after %d attempts!".formatted(session, RetryCount)));
                }
                logger.error("Failed to send message after " + RetryCount + " attempts!");
                return false;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // sending "Status" responses

    public void sendStatusResponse(Message message, MessageResponseStatus status) {
        try {
            System.out.println("Sending status response: " + status);
            byte[] statusB = mapper.writeValueAsBytes(new StatusResponse(message.getMessageID(), status));
            ByteBuffer statusBuffer = MessageFlag.write(MessageFlag.Flags.STATUS, statusB);

            if(!sendBytes(message.getSendingSession(), statusBuffer, RetryCount)){
                logger.error("Failed to send status response!!");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    // sending bytes to a session
    private boolean sendBytes(Session session, ByteBuffer message, int retryCount) throws IOException {
        if (session != null && session.isOpen()) {
            logger.debug("Sending bytes to " + session.getId() + " with retry count " + retryCount);
            // duplicate buffer so original stays intact for possible retries
            ByteBuffer copy = message.asReadOnlyBuffer();
            copy.rewind(); // reset position to 0 just in case
            session.getBasicRemote().sendBinary(copy);
            return true;
        } else if (retryCount > 0) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
            } catch (InterruptedException ignored) {}

            tryConnectServer();
            return sendBytes(session, message, retryCount - 1);
        } else {
            return false;
        }
    }


    // closure handling

    public void onClose(CloseReason reason, Session closingSession){
        // ensure each handler knows about closing
        for(MessageHandler<?> messageHandler : handlers.values()) {
            messageHandler.handleOnClose(reason, closingSession);
        }

        // ensure all response handlers are marked as failed to send
        for(Consumer<MessageResponseStatus> responseHandler : responseHandlers.values()) {
            responseHandler.accept(new MessageResponseStatus(MessageResponse.FAILED_TO_SEND, "Connection closed: %s".formatted(reason.getReasonPhrase())));
        }
    }



    public enum MessageResponse {
        PROCESSED_STATIC,
        PROCESSED_HANDLER,

        AUTHENTICATION_FAILED,
        FAILED_TO_SEND,
        MISSING_HANDLER;

        public boolean isErrorStatus(){
            return this != PROCESSED_STATIC && this != PROCESSED_HANDLER;
        }
    }

    public record MessageResponseStatus(MessageResponse response, String message) {}

    private record HandlerConfig(String handlerType, MessagePath.Endpoint handlerEndpoint) {}
    private record StatusResponse(UUID responseID, MessageResponseStatus status) {}

}
