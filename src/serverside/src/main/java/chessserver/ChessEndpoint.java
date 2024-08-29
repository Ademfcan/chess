package chessserver;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/home")
public class ChessEndpoint {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Echo the message back to the client
        try {
            ClientHandler.handleMessage(message, session);

        } catch (Exception e) {
            try {
                session.getBasicRemote().sendText("Invalid request!");
            } catch (IOException g) {
                g.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        ClientHandler.handleClosure(session);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.out.println("Error: " + t.toString());
    }

}
