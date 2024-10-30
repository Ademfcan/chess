package chessserver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import javax.naming.InitialContext;

@ServerEndpoint("/home")
public class ChessEndpoint {
    private static final Logger logger = LogManager.getLogger("Chess_Endpoint");
    private Session session;

    private static HikariDataSource dataSource;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        if (dataSource == null) {
            try {
                dataSource = initDbConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(10));
        config.setIdleTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setMaxLifetime(TimeUnit.HOURS.toMillis(1));
        config.setMaximumPoolSize(30);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10)); // logs if a connection is held for >10 seconds

        return new HikariDataSource(config);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Echo the message back to the client
        try {
            if(message.equals("metrics")){
                // Access metrics
                int totalConnections = dataSource.getHikariPoolMXBean().getTotalConnections();
                int activeConnections = dataSource.getHikariPoolMXBean().getActiveConnections();
                int idleConnections = dataSource.getHikariPoolMXBean().getIdleConnections();
                ClientHandler.sendMessage(session,ServerResponseType.SQLMESSAGE,String.format("Total Connections: %d | Active Connections: %d | Idle Connections: %d ",totalConnections,activeConnections,idleConnections),Integer.MAX_VALUE);
            }
            ClientHandler.handleMessage(message, session,dataSource);

        } catch (Exception e) {
            ClientHandler.sendMessage(session,ServerResponseType.INVALIDOPERATION,"Invalid request!",Integer.MAX_VALUE);
            logger.error(e);
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
