package chessserver;

import javax.websocket.Session;

public class FrontendClient extends Client{

    public FrontendClient(){

    }

    public FrontendClient(String name, int elo) {
        super(name, elo);
    }

    public BackendClient createBackend(Session session){
        BackendClient b = new BackendClient(this.getName(),this.getElo());
        b.setClientSession(session);
        return b;
    }
}
