package chessengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BackendChessPosition extends ChessPosition{
    public ChessStates gameState;
    public BackendChessPosition(ChessPosition pos,ChessStates gameState, int peiceType, boolean isWhite, boolean isCastle, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex){
        super( pos, gameState,  peiceType,  isWhite,  isCastle,  isPawnPromo,  oldX,  oldY,  newX,  newY,  promoIndex,false);
        this.gameState = gameState;
        this.gameState.updateMoveIndex(this.gameState.getCurrentIndex()+1);
    }


}
