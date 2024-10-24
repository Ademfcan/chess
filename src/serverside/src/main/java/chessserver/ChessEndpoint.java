package chessserver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
            dataSource = initDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HikariDataSource initDbConnection(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/chessDB");
        config.setUsername("admin");
        config.setPassword("chess");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("autoReconnect", "true");
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(TimeUnit.HOURS.toMillis(1));
        config.setMaximumPoolSize(30);

        return new HikariDataSource(config);
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
