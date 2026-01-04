package chessengine.Net;

import chessserver.Misc.ChessConstants;
import chessserver.Net.WebSocketConnection;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class WebSocketClientConnection extends WebSocketConnection {
    private Session serverSession;
//    private static final URI serverUri = URI.create("ws://20.157.72.110:8081/app/home");
    private static final URI serverUri = URI.create("wss://localhost:8443/ChessEndpoint");
    private final Class<?> handlerClass;

    public WebSocketClientConnection(Class<?> handlerClass) {
        super();
        this.handlerClass = handlerClass;
    }

    protected void setServerSession(Session serverSession) {
        this.serverSession = serverSession;
    }

    @Override
    public void connect() throws DeploymentException, IOException {
        ClientManager client = ClientManager.createClient();
        client.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, TimeUnit.SECONDS.toMillis(2));
        System.out.println("Connecting to " + serverUri);
        client.connectToServer(handlerClass, serverUri);
    }

    @Override
    public void disconnect() throws IOException {
        if (serverSession != null && serverSession.isOpen()) {
            serverSession.close();
        }
    }

    @Override
    protected Session getDefaultSession() {
        return serverSession;
    }
}
