package chessserver.Net;

import jakarta.websocket.Session;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MessageConfig {
    private final Message message;

    private @Nullable Consumer<WebSocketConnection.MessageResponseStatus> onStatusResponse;
    private @Nullable Consumer<? extends Payload> onDataResponse;

    private @Nullable TypedRequest responseType;

    private @Nullable Session session;

    public MessageConfig(Message message) {
        this.message = message;


    }

    public MessageConfig to(Session session) {
        this.session = session;
        return this;
    }

    public MessageConfig onDataResponse(Consumer<? extends Payload> onDataResponse) {
        this.onDataResponse = onDataResponse;
        return this;
    }

    public MessageConfig onStatusResponse(Consumer<WebSocketConnection.MessageResponseStatus> onStatusResponse) {
        this.onStatusResponse = onStatusResponse;
        return this;
    }

    public MessageConfig setDirectResponseType(TypedRequest responseType) {
        if (!responseType.expectsResponse()) {
            throw new IllegalArgumentException("Response type does not expect a response! (Means Not actually a direct response expecting type)");
        }

        this.responseType = responseType;
        return this;
    }

    public boolean isDirectResponse() {
        return responseType != null;
    }

    @Nullable
    public TypedRequest getResponseType() {
        return responseType;
    }

    @Nullable
    public Session getSession() {
        return session;
    }

    @Nullable
    public Consumer<? extends Payload> getOnDataResponse() {
        return onDataResponse;
    }

    @Nullable
    public Consumer<WebSocketConnection.MessageResponseStatus> getOnStatusResponse() {
        return onStatusResponse;
    }

    public Message getMessage() {
        return message;
    }

}
