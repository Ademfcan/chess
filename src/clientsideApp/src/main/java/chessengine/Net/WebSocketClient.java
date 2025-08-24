package chessengine.Net;

import chessengine.App;
import chessengine.FXInitQueue;
import chessengine.TriggerManager;
import chessengine.Triggers.Onlineable;
import chessserver.Net.WebSocketConnection;
import jakarta.websocket.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ClientEndpoint
public class WebSocketClient{
    private static final WebSocketClientConnection webSocketConnection = new WebSocketClientConnection(WebSocketClient.class);
    private static final Logger logger = LogManager.getLogger("WebSocketClient");


    public static WebSocketConnection getWebSocketConnection() {
        return webSocketConnection;
    }


    public static boolean isConnected() {
        return webSocketConnection.isDefaultConnected();
    }

    public static boolean tryConnectServer(){
        boolean reconnect = webSocketConnection.tryConnectServer();
        if(!reconnect){
            App.setOffline();
        }

        return reconnect;
    }

    // lifecycle methods
    @OnOpen
    public void onOpen(Session session) {
        webSocketConnection.setServerSession(session);
        FXInitQueue.runAfterInit(App::setOnline);
    }

    @OnMessage
    public void onMessage(ByteBuffer messageB, Session session){
        webSocketConnection.onIncomingBytes(messageB, session);
    }

    @OnClose
    public void onClose(CloseReason closeReason, Session session) {
        webSocketConnection.onClose(closeReason, session);
        FXInitQueue.runAfterInit(App::setOffline);
    }

    @OnError
    public void onError(Throwable t) {
        logger.error("Webclient error occurred", t);
    }


}
