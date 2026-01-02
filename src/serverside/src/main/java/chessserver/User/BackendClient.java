package chessserver.User;

import chessserver.ServerChessGame;

import jakarta.websocket.Session;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BackendClient implements Comparable<BackendClient>{
    private ServerChessGame currentGame = null;
    private final UUID clientUUID;
    private final Session clientSession;
    private final UserInfo clientInfo;


    public BackendClient(UUID clientUUID, Session clientSession, UserInfo clientInfo) {
        this.clientUUID = clientUUID;
        this.clientSession = clientSession;
        this.clientInfo = clientInfo;
    }

    public boolean isInGame() {
        return Objects.nonNull(currentGame);
    }

    public ServerChessGame getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(ServerChessGame currentGame) {
        this.currentGame = currentGame;
    }

    public void endGame(boolean isClientWinner, boolean isDraw, boolean isEarlyClose) {
        currentGame.handleGameEnd(this, isClientWinner, isDraw, isEarlyClose);
    }

    public Session getClientSession() {
        return clientSession;
    }
    public UserInfo getClientInfo() {return clientInfo;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackendClient other = (BackendClient) o;
        return Objects.equals(this.clientUUID, other.clientUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientUUID);
    }

    public void handleDrawRequest() {
        currentGame.handleDrawRequest(this);
    }

    public void handleDrawUpdate(boolean isDraw) {
        currentGame.handleDrawUpdate(this,isDraw);
    }

    @Override
    public int compareTo(BackendClient o) {
        return this.clientInfo.getUserelo() - o.clientInfo.getUserelo();
    }
}
