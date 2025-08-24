package chessserver;
import chessserver.Net.Message;
import chessserver.Net.ServerMessageHandlers.*;
import chessserver.Net.WebSocketServerConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

@ServerEndpoint("/ChessEndpoint")
public class ChessEndpoint {

    private static final Logger logger = LogManager.getLogger("Chess_Endpoint");
    private static final WebSocketServerConnection serverConnection = new WebSocketServerConnection();;
    public static final ServerChessGameMessageHandler serverChessGameMessageHandler = new ServerChessGameMessageHandler(serverConnection);
    public static final ServerDatabaseMessageHandler serverDatabaseMessageHandler = new ServerDatabaseMessageHandler(serverConnection);
    public static final ServerMetricMessageHandler serverMetricMessageHandler = new ServerMetricMessageHandler(serverConnection);
    public static final ServerUserMessageHandler serverUserMessageHandler = new ServerUserMessageHandler(serverConnection);
    private static final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        ActiveClientManager.startHeartbeat(session);
        ActiveClientManager.markActiveSession(session);
    }



    @OnMessage
    public void onMessage(ByteBuffer messageB, Session session) {
        try{
            serverConnection.onIncomingBytes(messageB, session);
        }
        catch (Exception e){
            logger.error(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        serverConnection.onClose(closeReason, session);
        ActiveClientManager.markInactiveSession(session);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        // todo
        logger.error("Error in ChessEndpoint", t);
    }

}

