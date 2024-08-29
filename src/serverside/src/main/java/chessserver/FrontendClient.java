package chessserver;

import javax.websocket.Session;

public class FrontendClient extends Client {

    public FrontendClient() {

    }

    public FrontendClient(UserInfo info) {
        super(info);
    }

    public BackendClient createBackend(Session session) {
        BackendClient b = new BackendClient(this.getInfo());
        b.setClientSession(session);
        return b;
    }
}
