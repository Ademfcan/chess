package chessserver;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import javax.sql.DataSource;
import javax.naming.InitialContext;

@ServerEndpoint("/home")
public class ChessEndpoint {
    private Session session;

    private DataSource dataSource;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        try {
            // Lookup DataSource using JNDI
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/chessDB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Echo the message back to the client
        try {
            ClientHandler.handleMessage(message, session,dataSource);

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
