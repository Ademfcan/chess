package chessserver.Net;

import jakarta.websocket.Session;

import java.io.IOException;

public class WebSocketServerConnection extends WebSocketConnection {

    public WebSocketServerConnection() {
        super();
    }

    @Override
    public void connect() throws jakarta.websocket.DeploymentException, IOException {
        // nothing to do...
    }

    @Override
    public void disconnect() throws IOException {
        // nothing to do...
    }

    @Override
    protected Session getDefaultSession() {
        throw new IllegalStateException("getDefault called for server! Server has no default session!");
    }

}
