package chessserver.Net.ServerMessageHandlers;

import chessserver.ActiveClientManager;
import chessserver.ChessEndpoint;
import chessserver.ClientHandling.WaitingPool;
import chessserver.Communication.OutputMessage;
import chessserver.Enums.ServerResponseType;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.ChessGameMessageTypes;
//import chessserver.Net.MessageTypes.SkeletonMessageTypes;
import chessserver.Net.PayloadTypes.ChessGamePayloadTypes;
import chessserver.User.BackendClient;
import chessserver.User.Client;
import chessserver.User.UserInfo;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ServerChessGameMessageHandler extends MessageHandler<ChessGameMessageTypes.ClientRequest> {
    public final WaitingPool pool = new WaitingPool();


    public ServerChessGameMessageHandler(WebSocketConnection webConnection) {
        super(true, webConnection, ChessGameMessageTypes.ClientRequest.class, MessagePath.Endpoint.SERVER);
    }

    @Override
    protected void handleMessage(ChessGameMessageTypes.ClientRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
        BackendClient client = ActiveClientManager.getClient(jwtUUID, message.getSendingSession());

        switch (messageOption) {
            case MAKEMOVE -> {
                Payload.StringPayload pgn = message.getTypedMessagePayload();

                if(client.isInGame()){
                    client.getCurrentGame().makeMove(pgn.payload(), client);
                }
                else{
                    message.sendResponse(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("not currently in game, cannot make a move"));
                }

            }
            case SENDCHAT -> {
                Payload.StringPayload chatMessage = message.getTypedMessagePayload();

                if(client.isInGame()){
                    client.getCurrentGame().sendChat(chatMessage.payload(), client);
                }
                else{
                    message.sendResponse(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("not currently in game, cannot send a message"));
                }
            }
            case CREATEGAME -> {
                ChessGamePayloadTypes.GameTypePayload gameType = message.getTypedMessagePayload();
                if(client.isInGame()){
                    message.sendResponse(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("Client is already in a game"));
                }
                else {
                    pool.matchClient(client, gameType.gameType());
                }
            }
            case LEAVEWAITINGPOOL -> {
                pool.tryRemoveClient(client);
            }
            case REQUESTDRAW -> {
                if (client.isInGame()) {
                    client.handleDrawRequest();
                } else {
                    message.sendResponse(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("Cannot send draw request as not currently in game"));
                }
            }
            case DRAWACCEPTANCEUPDATE -> {
                Payload.BooleanPayload drawAccepted = message.getTypedMessagePayload();
                if(client.isInGame()){
                    client.handleDrawUpdate(drawAccepted.payload());
                }
                else{
                    message.sendResponse(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("Cannot send draw update as not currently in game"));
                }
            }
            case LEAVEGAME -> {
                if (client.isInGame()) {
                    client.endGame(false, false, true);
                } else {
                    message.sendResponse(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("cannot leave game as not currently in a game"));
                }
            }

        }
    }


}
