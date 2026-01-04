package chessengine.Net.ClientMessageHandlers;

import chessengine.Net.WebSocketClient;
import chessserver.Net.Message;
import chessserver.Net.MessageHandler;
import chessserver.Net.MessagePath;
import chessserver.Net.MessageTypes.MetricMessageTypes;
import chessserver.Net.WebSocketConnection;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.util.UUID;

public class ClientMetricMessageHandler extends MessageHandler<MetricMessageTypes.ServerRequest> {
    public ClientMetricMessageHandler(WebSocketConnection connection) {
        super(false, connection, MetricMessageTypes.ServerRequest.class, MessagePath.Endpoint.CLIENT);
    }

    @Override
    protected void handleMessage(MetricMessageTypes.ServerRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
    }

}
