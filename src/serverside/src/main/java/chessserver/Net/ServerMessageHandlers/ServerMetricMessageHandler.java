package chessserver.Net.ServerMessageHandlers;

import chessserver.ActiveClientManager;
import chessserver.ChessEndpoint;
import chessserver.ClientHandling.WaitingPool;
import chessserver.Enums.Gametype;
import chessserver.Enums.ServerResponseType;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.MetricMessageTypes;
import chessserver.User.BackendClient;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.util.UUID;

public class ServerMetricMessageHandler extends MessageHandler<MetricMessageTypes.ClientRequest> {

    public ServerMetricMessageHandler(WebSocketConnection webConnection) {
        super(true, webConnection, MetricMessageTypes.ClientRequest.class, MessagePath.Endpoint.SERVER);
    }

    @Override
    protected void handleMessage(MetricMessageTypes.ClientRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
        BackendClient client = ActiveClientManager.getClient(jwtUUID, message.getSendingSession());

        switch (messageOption) {
            case GETNUMACTIVE -> message.sendExpectedResponse(new Payload.IntegerPayload(ActiveClientManager.getNumActiveClients()));
            case GETNUMINPOOL -> {
                Payload.StringPayload gameType = message.getTypedMessagePayload();
                Gametype wantedType = Gametype.getType(gameType.payload());
                message.sendExpectedResponse(new Payload.IntegerPayload(ChessEndpoint.serverChessGameMessageHandler.pool.getValidWaitingClientsOfTypeCount(client, wantedType)));
            }
//            case GETNUMBEROFPOOLERS -> {
//                Gametype wantedType =
//                sendMessage(c.getClientSession(), ServerResponseType.SERVERRESPONSEACTIONREQUEST, Integer.toString(pool.getWaitingClientsOfTypeCount(c, wantedType, 1000)), input.getUniqueId());
//            }
//            case PULLTOTALPLAYERCOUNT ->
//                    sendMessage(c.getClientSession(), ServerResponseType.SERVERRESPONSEACTIONREQUEST, Integer.toString(pool.getPoolCount()), input.getUniqueId());
        }
    }

}
