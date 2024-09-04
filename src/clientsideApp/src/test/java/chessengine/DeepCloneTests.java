package chessengine;

import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Misc.ChessConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeepCloneTests {
    @Test void deepCloneChessState(){
        ChessStates a = new ChessStates();
        ChessStates b = a.cloneState();

        a.removeCastlingRight(true);
        Assertions.assertTrue(b.isWhiteCastleRight());

        a.updateMoveIndex(10);
        Assertions.assertEquals(b.getCurrentIndex(),-1);

        a.makeNewMoveAndCheckDraw(new ChessPosition(ChessConstants.startBoardState.board,new ChessMove(0,0,0,0,0,ChessConstants.ROOKINDEX,false,false,false,ChessConstants.EMPTYINDEX,false,false)));
        Assertions.assertEquals(b.getMovesSinceNoCheckOrNoPawn(),0);

        Assertions.assertNotEquals(System.identityHashCode(a.getPosMap()),System.identityHashCode(b.getPosMap()));
        Assertions.assertNotEquals(System.identityHashCode(a.getMovesWhenResetted()),System.identityHashCode(b.getMovesWhenResetted()));

        a.getPosMap().put(0L,0);
        Assertions.assertNotEquals(b.getPosMap().getOrDefault(0,-1),0);

        b.getMovesWhenResetted().push(10);
        a.getMovesWhenResetted().push(0);
        Assertions.assertNotEquals(b.getMovesWhenResetted().pop(),0);

    }
}
