package chessserver;

import javax.websocket.Session;
import java.util.Objects;

public class BackendClient extends Client {
    private ChessGame currentGame;
    private Session clientSession;

    public BackendClient(UserInfo info) {
        super(info);
        this.currentGame = null;
    }

    public boolean isInGame() {
        return Objects.nonNull(currentGame);
    }

    public ChessGame getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(ChessGame currentGame) {
        this.currentGame = currentGame;
    }

    public void endGame(boolean isClientWinner, boolean isDraw, boolean isEarlyClose) {
        currentGame.closeGame(this, isClientWinner, isDraw, isEarlyClose);
    }

    public Session getClientSession() {
        return clientSession;
    }

    public void setClientSession(Session clientSession) {
        this.clientSession = clientSession;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackendClient client = (BackendClient) o;
        return Objects.equals(this.clientSession, client.clientSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientSession);
    }
}
