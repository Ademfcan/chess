package chessserver.Net;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.websocket.Session;

import java.util.UUID;

public class Message {

    // metadata
    private final UUID messageID;
    private final MessagePath messagePath;

    // specific information about request
    private final String handlerType;
    private final String messageOption;
    private final Payload messagePayload;
    private AuthenticationPayload authentication;

    // for message config verification only (right after creation)

    @JsonIgnore
    private TypedRequest messageTypeRaw;
    public TypedRequest getMessageTypeRaw() {
        return messageTypeRaw;
    }

    public <T extends Enum<T> & TypedRequest & DirectionalMesageType> Message(T messageOption) {
        this(messageOption, new Payload.Empty(), new AuthenticationPayload.EmptyAuthentication());
    }

    public <T extends Enum<T> & TypedRequest & DirectionalMesageType> Message(T messageOption, AuthenticationPayload authentication) {
        this(messageOption, new Payload.Empty(), authentication);
    }

    public <T extends Enum<T> & TypedRequest & DirectionalMesageType> Message(T messageOption, Payload messagePayload) {
        this(messageOption, messagePayload, new AuthenticationPayload.EmptyAuthentication());
    }

    public <T extends Enum<T> & TypedRequest & DirectionalMesageType> Message(
        T messageOption,
        Payload messagePayload,
        AuthenticationPayload authentication
    ) {
        this.messageID = UUID.randomUUID();
        this.messagePath = messageOption.getDirection();
        this.handlerType = MessageHandler.getHandlerType(messageOption);
        this.messagePayload = messagePayload;
        this.messageOption = messageOptionToStr(messageOption);
        this.authentication = authentication;

        this.messageTypeRaw = messageOption;



    }

    protected <T extends Enum<T> & DirectionalMesageType & TypedRequest> Message createResponse(T messageOption, Payload messagePayload, AuthenticationPayload authentication) {
        Message m = new Message(messageID, messagePath.responsePath(), MessageHandler.getHandlerType(messageOption), messageOptionToStr(messageOption), messagePayload, authentication);
        m.messageTypeRaw = messageOption;
        return m;
    }

    // not json saved reference to the message handler that received message

    @JsonIgnore
    private MessageHandler<?> recievingMessageHandler;
    @JsonIgnore
    private boolean hasResponded = false;

    public boolean hasResponded() {
        return hasResponded;
    }

    protected void setRecievingMessageHandler(MessageHandler<?> recievingMessageHandler) {
        this.recievingMessageHandler = recievingMessageHandler;
    }


    public void sendExpectedResponse(Payload messagePayload){
        sendExpectedResponse(messagePayload, new AuthenticationPayload.EmptyAuthentication());
    }

    public void sendExpectedResponse(Payload messagePayload, AuthenticationPayload authentication){
        hasResponded = true; // mark expected response
        sendResponse(MessageHandler.GenericServerResponses.EXPECTS_RESPONSE_HANDLER, messagePayload, authentication);
    }

    public <T extends Enum<T> & DirectionalMesageType & TypedRequest> void sendResponse(T messageOption, Payload messagePayload){
        sendResponse(messageOption, messagePayload, new AuthenticationPayload.EmptyAuthentication());
    }

    public <T extends Enum<T> & DirectionalMesageType & TypedRequest, F extends Enum<F> & DirectionalMesageType & TypedRequest> void sendResponse(T messageOption, Payload messagePayload, AuthenticationPayload authentication){
        if(recievingMessageHandler != null){
            F wrapped = (F) wrapper.t;

            boolean isDirectResponse = messageOption == MessageHandler.directResponseClass;
            Message response = createResponse(messageOption, messagePayload, authentication);

            if(isDirectResponse){
                recievingMessageHandler.sendMessage(new MessageConfig(response).to(getSendingSession()).setDirectResponseType(wrapped));
            }
            else{
                recievingMessageHandler.sendMessage(new MessageConfig(response).to(getSendingSession()));
            }
        }
        else{
            throw new IllegalStateException(
                    "The message is either not on recieving end or for whatever other reason the recievingMessageHandler is not initialized!");
        }
    }

    @JsonIgnore
    private Session sendingSession;

    protected void setSendingSession(Session sendingSession) {
        this.sendingSession = sendingSession;
    }

    public Session getSendingSession() {
        return sendingSession;
    }

    @JsonIgnore
    MessageOptionWrapper<?> wrapper;



    // ugly..., very ugly
    record MessageOptionWrapper <T extends Enum<T> & DirectionalMesageType & TypedRequest> (T t) {}

    protected <T extends Enum<T> & DirectionalMesageType & TypedRequest> void setTypedMessageOption(T messageOption) {
        this.wrapper = new MessageOptionWrapper<>(messageOption);
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public <T extends Payload> T getTypedMessagePayload() {
        return (T) (messagePayload);
    }


    @JsonCreator
    public Message
            (
                    @JsonProperty("messageID")
                    UUID messageID,
                    @JsonProperty("messagePath")
                    MessagePath messagePath,
                    @JsonProperty("handlerType")
                    String handlerType,
                    @JsonProperty("messageOption")
                    String messageOption,
                    @JsonProperty("messagePayload")
                    Payload messagePayload,
                    @JsonProperty("authentication")
                    AuthenticationPayload authentication
            ) {
        this.messageID = messageID;
        this.messagePath = messagePath;
        this.handlerType = handlerType;
        this.messagePayload = messagePayload;
        this.messageOption = messageOption;
        this.authentication = authentication;

    }


    public UUID getMessageID() {
        return messageID;
    }

    public MessagePath getMessagePath() {
        return messagePath;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public String getMessageOption() {
        return messageOption;
    }

    public Payload getMessagePayload() {
        return messagePayload;
    }

    public AuthenticationPayload getAuthentication() {
        return authentication;
    }

    public void updateAuthentication(AuthenticationPayload authentication) {
        this.authentication = authentication;
    }

    @JsonIgnore
    private boolean handled = false;

    public void setHandled() {
        handled = true;
    }

    public boolean isHandled() {
        return handled;
    }

    @Override
    public String toString() {
        return "Message:" +
                "\nmessageID=" + messageID +
                "\nhandlerType='" + handlerType + '\'' +
                "\nmessagePath=" + messagePath +
                "\nmessageOption='" + messageOption + '\'' +
                "\nmessagePayload=" + messagePayload +
                "\nauthentication=" + authentication;
    }


    public static <T extends Enum<T> & DirectionalMesageType & TypedRequest> String messageOptionToStr(T messageOption) {return messageOption.name();}

}
