package chessengine;

public class stateHandler {
    private final boolean doesWhiteStartFirst;
    private boolean isCurrentWhiteTurn;


    public stateHandler(boolean doesWhiteStartFirst) {
        this.doesWhiteStartFirst = doesWhiteStartFirst;
        this.isCurrentWhiteTurn = this.doesWhiteStartFirst;
    }

    public void moveMade() {
        // change turn
        this.isCurrentWhiteTurn = !this.isCurrentWhiteTurn;
    }

    public void resetTurns() {
        this.isCurrentWhiteTurn = this.doesWhiteStartFirst;
    }

    public boolean isCurrentWhiteTurn() {
        return this.isCurrentWhiteTurn;
    }

    public void changingMove() {
        moveMade();
    }


}
