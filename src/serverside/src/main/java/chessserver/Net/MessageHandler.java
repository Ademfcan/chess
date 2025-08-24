package chessserver.Net;

import chessserver.JWTGenerator;
import chessserver.JWTUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public abstract class MessageHandler<T extends Enum<T> & DirectionalMesageType & TypedRequest> {
    public enum GenericServerResponses implements DirectionalMesageType, TypedRequest {
        EXPECTS_RESPONSE_HANDLER(Payload.class),
        PRINT_MESSAGE(Payload.StringPayload.class),
        AUTH_INVALID(Payload.StringPayload.class),
        SQL_ERROR(Payload.StringPayload.class);

        private final Class<? extends Payload> payloadClass;

        GenericServerResponses(Class<? extends Payload> payloadClass){
            this.payloadClass = payloadClass;
        }

        @Override
        public MessagePath getDirection() {
            return MessagePath.BOTH;
        }

        @Override
        public Class<? extends Payload> getPayloadClass() {
            return payloadClass;
        }

        @Override
        public Class<? extends Payload> getResponseClass() {
            return null;
        }
    }


    // no other message options should use any of the following enum names, as they will be shadowed
    private static final Map<UUID, ResponseHandler<?>> responseMap = Collections.synchronizedMap(new HashMap<>());
    public static GenericServerResponses directResponseClass = MessageHandler.GenericServerResponses.EXPECTS_RESPONSE_HANDLER;
    public static String directResponseClassStr = Message.messageOptionToStr(directResponseClass);

    protected static final Logger logger = LogManager.getLogger(MessageHandler.class.getSimpleName());
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected <e> e simpleUnpack(String obj, Class<e> objClass) {
        try{
            return objectMapper.readValue(obj, objClass);
        }
        catch (JsonProcessingException e){
            logger.error("Error occurred while parsing json", e);
            return null;
        }
    }

    protected String toJson(Object object) {
        try{
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private final WebSocketConnection webConnection;
    private final Class<T> messageOptionsClass;
    private final boolean requiresJWT;

    protected abstract void handleMessage(T messageOption, Message message, boolean validJWT, UUID jwtUUID) throws IOException;
    protected void onClose(CloseReason closeReason, Session closedSession) {
        logger.info("{} closed! due to: {}", closedSession.getId(), closeReason.getReasonPhrase());
    };

    public MessageHandler(boolean requiresJWT, WebSocketConnection webConnection, Class<T> messageOptionsClass, MessagePath.Endpoint handlerEndpoint) {
        this.requiresJWT = requiresJWT;
        this.webConnection = webConnection;
        this.messageOptionsClass = messageOptionsClass;

        webConnection.registerHandler(getHandlerType(messageOptionsClass), handlerEndpoint, this);
    }

    public void sendMessage(
            MessageConfig messageConfig)  {

        if(messageConfig.isDirectResponse()){
            // different checks when sending direct response
            if(!Objects.equals(messageConfig.getMessage().getMessageOption(), directResponseClassStr)){
                throw new IllegalStateException("Message option is not of type " + directResponseClassStr);
            }

            ensurePayload(Objects.requireNonNull(Objects.requireNonNull(messageConfig.getResponseType()).getResponseClass()), messageConfig.getMessage().getMessagePayload());

            // this implicitly checks if the response handler is not set, as any direct response cannot have another direct response to avoid ping pong loops
            ensureResponse(messageConfig.getMessage().getMessageTypeRaw().expectsResponse(), messageConfig.getOnDataResponse() != null);

        }
        else{
            ensurePayload(messageConfig.getMessage().getMessageTypeRaw().getPayloadClass(), messageConfig.getMessage().getMessagePayload());
            ensureResponse(messageConfig.getMessage().getMessageTypeRaw().expectsResponse(), messageConfig.getOnDataResponse() != null);
        }

        boolean messageSent = webConnection.sendMessage(messageConfig.getSession(), messageConfig.getMessage(), messageConfig.getOnStatusResponse());

        if(messageSent && messageConfig.getOnDataResponse() != null){
            ResponseHandler<? extends Payload> responseHandler = new ResponseHandler<>(messageConfig.getOnDataResponse());
            responseMap.put(messageConfig.getMessage().getMessageID(), responseHandler);
        }

    }

    public static WebSocketConnection.MessageResponseStatus tryHandleStatic(Message message) throws IllegalArgumentException{
        String messageOptionS = message.getMessageOption();

            GenericServerResponses specialOption = Enum.valueOf(GenericServerResponses.class, messageOptionS);

            // special option found
            switch (specialOption){
                case EXPECTS_RESPONSE_HANDLER ->{

                    if(responseMap.containsKey(message.getMessageID())){
                        // has response
                        ResponseHandler<?> handler = responseMap.remove(message.getMessageID());
                        handler.handleResponse(message.getTypedMessagePayload());
                        return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.PROCESSED_STATIC,
                                "The message was processed by a data response handler");
                    }
                    else{
                        logger.error("Message expected response handler, yet ended up in regular message handling!!: {}", message.getMessagePath());
                        return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.MISSING_HANDLER,
                                "The message expected a data response handler, but none was found");
                    }

                }
                case PRINT_MESSAGE ->{
                    String globalMessage = ((Payload.StringPayload) message.getMessagePayload()).payload();
                    logger.info(globalMessage);
                    return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.PROCESSED_STATIC,
                            "The message %s was printed (logged)".formatted(globalMessage));
                }
                case SQL_ERROR -> {
                    logger.error("Sql error: \n{}", ((Payload.StringPayload) message.getMessagePayload()).payload());
                    return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.PROCESSED_STATIC,
                            "Sql error logged");
                }
                case AUTH_INVALID -> {
                    logger.error("Auth invalid: \n{}", ((Payload.StringPayload) message.getMessagePayload()).payload());
                    return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.PROCESSED_STATIC,
                            "Auth invalid logged");
                }
            }


            return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.MISSING_HANDLER,
                    "The message option %s is not handled by any static handler!".formatted(messageOptionS));

    }

    protected WebSocketConnection.MessageResponseStatus handleMessage(Message message) {
        String messageOptionS = message.getMessageOption();
        T messageOption = parseMessageOption(messageOptionS);

        message.setRecievingMessageHandler(this);
        message.setTypedMessageOption(messageOption);
        message.setHandled();

        AuthenticationPayload authPayload = message.getAuthentication();
        boolean hasAuth = false;
        boolean isValidJWT = false;
        UUID userUUID = null;

        // Check if authentication was provided and is a JWT
        if (authPayload instanceof AuthenticationPayload.JWTAuthentication jwtPayload) {
            hasAuth = true;

            String token = jwtPayload.JwtToken();
            isValidJWT = JWTGenerator.verifySignature(token) && !JWTUtil.isExpired(token);
            userUUID = JWTUtil.getTokenUUID(token);
        }

        if (requiresJWT) {
            if (!hasAuth) {
                logger.debug("Missing authentication for a message that requires JWT.");
                return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.AUTHENTICATION_FAILED,
                        "Missing authentication for a message that requires JWT");
            }
            if (!isValidJWT) {
                logger.debug("Invalid JWT token received. Token UUID: {}", userUUID);
                return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.AUTHENTICATION_FAILED,
                        "Requires authentication but invalid JWT token received. Token UUID: " + userUUID);
            }
        } else if (hasAuth && !isValidJWT) {
            // JWT was provided but is invalid — log it even if not strictly required
            logger.warn("Invalid JWT token received for non-required auth. Token UUID: {}", userUUID);
        }


        try {
            handleMessage(messageOption, message, isValidJWT, userUUID);

            // check message option expected response and we didint respond.
            // similarly check other way
            if(message.hasResponded() != messageOption.expectsResponse()){
                throw new IllegalStateException(
                        "Message responded:" + message.hasResponded() + "!= Message expectsResponse: " + messageOption.expectsResponse() +
                                "\nMessage Type: " + message.getMessageOption());
            }

            return new WebSocketConnection.MessageResponseStatus(WebSocketConnection.MessageResponse.PROCESSED_HANDLER,
                    "Message was processed by handler: " + getHandlerType(messageOption) + " with option: " + messageOption.name());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void handleOnClose(CloseReason closeReason, Session closedSession){
        onClose(closeReason, closedSession);
    }

    private T parseMessageOption(String messageOptionS) {
        return Enum.valueOf(messageOptionsClass, messageOptionS);
    }

    public static  <e extends Payload> void ensurePayload(Class<e> responseClass, Payload payload) {
        // NOTE: payload's class will be its actual runtime class, not just payload
        if(!responseClass.isInstance(payload)){
            throw new IllegalArgumentException("Message option does not match expected payload class!:\n" +
                    responseClass + " != " + payload.getClass());
        };
    }

    public static void ensureResponse(boolean requiresResponseHandler, boolean hasResponseHandler) {
        // NOTE: payload's class will be its actual runtime class, not just payload
        if(requiresResponseHandler != hasResponseHandler){
            throw new IllegalStateException("Message expecting response does not match message having response handler!:\nexpects response: " +
                    requiresResponseHandler + " != has handler: " + hasResponseHandler);
        };
    }

    public static < e extends Enum<e> & DirectionalMesageType & TypedRequest> String getHandlerType(e messageOption) {
        return messageOption.getClass().getDeclaringClass().getSimpleName() + "." + messageOption.getClass().getSimpleName();
    }

    private String getHandlerType(Class<?> messageOptionsClass) {
        return messageOptionsClass.getDeclaringClass().getSimpleName() + "." + messageOptionsClass.getSimpleName();
    }

}
