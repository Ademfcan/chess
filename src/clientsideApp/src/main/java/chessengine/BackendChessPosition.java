package chessengine;

public class BackendChessPosition extends ChessPosition{
    public ChessStates gameState;

    private boolean isDrawRepetition;

    public BackendChessPosition(ChessPosition pos,ChessStates gameState, int peiceType, boolean isWhite, boolean isCastle, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex){
        super( pos, gameState,  peiceType,  isWhite,  isCastle,  isPawnPromo,  oldX,  oldY,  newX,  newY,  promoIndex,false);
        this.gameState = gameState;
        isDrawRepetition = this.gameState.makeNewMoveAndCheckDrawRep(this,false);
    }
    public boolean isDrawByRepetition() {
        if(isDrawRepetition){
            System.out.println("Draw position");
        }
        return isDrawRepetition;
    }


}
