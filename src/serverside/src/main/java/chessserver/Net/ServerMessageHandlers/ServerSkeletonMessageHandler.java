//package chessserver.Net.ServerMessageHandlers;
//
//import chessserver.Net.*;
//import chessserver.Net.MessageTypes.SkeletonMessageTypes;
//import jakarta.websocket.CloseReason;
//
//public class ServerSkeletonMessageHandler extends MessageHandler<SkeletonMessageTypes.ClientRequest> {
//
//    public ServerSkeletonMessageHandler(WebSocketConnection webConnection) {
//        super(webConnection, SkeletonMessageTypes.ClientRequest.class, MessagePath.Endpoint.SERVER);
//    }
//
//    @Override
//    protected void handleMessage(SkeletonMessageTypes.ClientRequest messageOption, Message message) {
//    }
//
//    @Override
//    protected void onClose(CloseReason closeReason) {
//    }
//
//}
