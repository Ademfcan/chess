package chessserver;

public class GameStatus {
    boolean isClient1Turn;
    boolean isGameOver;

    public GameStatus(boolean doesClient1StartFirst) {
        this.isClient1Turn = doesClient1StartFirst;
        this.isGameOver = false;
    }
}
