//package chessengine.Net.ClientMessageHandlers;
//
//import chessengine.Net.WebSocketClient;
//import chessserver.Net.Message;
//import chessserver.Net.MessageHandler;
//import chessserver.Net.MessagePath;
//import chessserver.Net.MessageTypes.SkeletonMessageTypes;
//import jakarta.websocket.CloseReason;
//
//public class ClientSkeletonMessageHandler extends MessageHandler<SkeletonMessageTypes.ServerRequest> {
//    public ClientSkeletonMessageHandler(WebSocketClient webClient) {
//        super(webClient, SkeletonMessageTypes.ServerRequest.class, MessagePath.Endpoint.CLIENT);
//    }
//
//    @Override
//    protected void handleMessage(SkeletonMessageTypes.ServerRequest messageOption, Message message) {
//    }
//
//    @Override
//    protected void onClose(CloseReason closeReason) {
//
//    }
//
//
//}
