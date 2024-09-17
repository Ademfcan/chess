package chessengine;

import chessengine.ChessRepresentations.BitBoardWrapper;
import chessengine.ChessRepresentations.XYcoord;
import chessengine.Functions.BitFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

    @Test void boardFlipTest(){
        BitBoardWrapper test = ChessConstants.startBoardState.board.cloneBoard();
        BitBoardWrapper reference = test.cloneBoard();
        XYcoord startKingLocation = ChessConstants.startBoardState.board.getWhiteKingLocation();
        // fist checks
        GeneralChessFunctions.printBoardDetailed(test);
        Assertions.assertTrue(GeneralChessFunctions.checkIfContains(startKingLocation.x,startKingLocation.y,true,test));

        test.flipBoard();
        GeneralChessFunctions.printBoardDetailed(test);


        // now back to original state
        test.flipBoard();

        GeneralChessFunctions.printBoardDetailed(test);
        Assertions.assertEquals(GeneralChessFunctions.getBoardDetailedString(test),GeneralChessFunctions.getBoardDetailedString(reference));


    }

    @Test void attackTableTests(){
        BitBoardWrapper test = ChessConstants.startBoardState.board.cloneBoard();
        for(int i = 0;i<6;i++){
            System.out.println(GeneralChessFunctions.getPieceType(i));
            System.out.println(BitFunctions.getBitStr(test.getBlackAttackTables()[i]));
        }
    }

    @Test void boardAddAndRemovePieceTests(){
        BitBoardWrapper test = ChessConstants.startBoardState.board.cloneBoard();
        test.removePiece(GeneralChessFunctions.positionToBitIndex(4,7),ChessConstants.KINGINDEX,true);
        Assertions.assertFalse(GeneralChessFunctions.checkIfContains(GeneralChessFunctions.positionToBitIndex(4,7),test.getWhitePiecesBB()[ChessConstants.KINGINDEX]));
        test.addPiece(GeneralChessFunctions.positionToBitIndex(4,7),ChessConstants.KINGINDEX,true);
        Assertions.assertTrue(GeneralChessFunctions.checkIfContains(GeneralChessFunctions.positionToBitIndex(4,7),test.getWhitePiecesBB()[ChessConstants.KINGINDEX]));
    }
}
