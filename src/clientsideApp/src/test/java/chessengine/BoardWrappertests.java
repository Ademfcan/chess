package chessengine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoardWrappertests {
    @Test void TempChangeTest(){
        BitBoardWrapper boardWrapper = ChessConstants.startBoardState.board.cloneBoard();
        boardWrapper.makeTempChange(4,7,4,4,4,true);
        boardWrapper.keepTempChange();
        boardWrapper.makeTempChange(4,4,4,7,4,true);
        boardWrapper.keepTempChange();
        Assertions.assertEquals(GeneralChessFunctions.getPieceType(GeneralChessFunctions.getBoardWithPiece(4,7,true,boardWrapper)),"Queen");
    }
}
