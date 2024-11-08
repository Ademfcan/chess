package chessengine;

import chessserver.ChessRepresentations.BitBoardWrapper;
import chessserver.ChessRepresentations.XYcoord;
import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class BoardWrappertests {
    @Test void TempChangeTest(){
        BitBoardWrapper boardWrapper = ChessConstants.startBoardState.board.cloneBoard();
        Arrays.stream(boardWrapper.getWhiteAttackTables()).forEach(m -> System.out.println(BitFunctions.getBitStr(m)));
        System.out.println(boardWrapper.getWhitePieces()[4].toString());
        boardWrapper.makeTempChange(3,7,4,4,4,true);
        Arrays.stream(boardWrapper.getWhiteAttackTables()).forEach(m -> System.out.println(BitFunctions.getBitStr(m)));
        System.out.println(boardWrapper.getWhitePieces()[4].toString());

        boardWrapper.popTempChange();
        Arrays.stream(boardWrapper.getWhiteAttackTables()).forEach(m -> System.out.println(BitFunctions.getBitStr(m)));
        System.out.println(boardWrapper.getWhitePieces()[4].toString());
//        boardWrapper.makeTempChange(4,4,3,7,4,true);
//        boardWrapper.keepTempChange();
//        Assertions.assertEquals(GeneralChessFunctions.getPieceType(GeneralChessFunctions.getBoardWithPiece(3,7,true,boardWrapper)),"Queen");
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

    @Test void pawnPromotionMaskTest(){
        BitBoardWrapper test = ChessConstants.startBoardState.board.cloneBoard();
        System.out.println(BitFunctions.isPassedPawn(4,6,false,test));


    }
}
