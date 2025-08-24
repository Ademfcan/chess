package simulatedplayer;
import jakarta.websocket.*;

import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/Test")
public class EchoWebSocket {
    @OnOpen
    public void onConnect(Session session) {
        System.out.println("Connected: " + session.getBasicRemote().toString());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws Exception {
        session.getBasicRemote().sendText("Echo: " + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Closed: " + reason);
    }
}
