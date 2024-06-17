package chessengine;

public class stateHandler {
    private boolean isCurrentWhiteTurn;
    private final boolean doesWhiteStartFirst;




    public stateHandler(boolean doesWhiteStartFirst){
        this.doesWhiteStartFirst = doesWhiteStartFirst;
        this.isCurrentWhiteTurn = this.doesWhiteStartFirst;
    }
    public void moveMade(){
        // change turn
        this.isCurrentWhiteTurn = !this.isCurrentWhiteTurn;
    }

    public void resetTurns(){
        this.isCurrentWhiteTurn = this.doesWhiteStartFirst;
    }

    public boolean isCurrentWhiteTurn(){
        return this.isCurrentWhiteTurn;
    }

    public void changingMove(){
        moveMade();
    }



}
