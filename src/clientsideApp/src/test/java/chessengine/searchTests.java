package chessengine;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.*;
import chessengine.Functions.EvaluationFunctions;
import chessengine.Misc.ChessConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class searchTests {

    @Test void searchTest(){
        Searcher searcher = new Searcher();
        BackendChessPosition testPosition = ChessConstants.startBoardState.toBackend(new ChessStates(),true);
        System.out.println(searcher.search(testPosition,1000));
        System.out.println(searcher.searchInfo.getNumTranspositionUses());
        System.out.println(searcher.searchInfo.getUniquePositionsSearched());
        System.out.println(searcher.searchInfo.getNumBetaCutoffs());
    }

    @Test void staticEvaluationTest(){
        String[] pgns = {
                // Good test cases
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4", // Ruy Lopez, Berlin Defense
                "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7", // Queen's Gambit Declined
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 f5", // Schliemann Defense
                // Common openings
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Bxc6 dxc6", // Ruy Lopez, Exchange Variation
                "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bf4", // Queen's Gambit Declined, Exchange Variation
                "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6", // Sicilian Defense, Accelerated Dragon
                // Edge cases
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 Qe7", // Uncommon move order in Ruy Lopez
                "1.d4 Nf6 2.c4 c5 3.d5 b5", // Benko Gambit
                "1.e4 d6 2.d4 Nf6 3.Nc3 g6 4.Bg5 Bg7 5.Qd2 h6 6.Bf4 g5 7.Be3 Ng4", // Pirc Defense, Byrne Variation
                "1.e4 d5 2.Nf3 Nf6 3.Bb5+ Bd7 4.Nc3 e6 5.a3 Bc5 6.O-O O-O 7.Re1 Re8 8.Re3 Re7"
        };
        for(String pgn : pgns) {
            ChessGame testGame = ChessGame.createTestGame(pgn, false);
            testGame.moveToEndOfGame(false);
            System.out.println(EvaluationFunctions.getStaticEvaluation(testGame.currentPosition.toBackend(testGame.gameState,testGame.isWhiteTurn())));
        }


    }

    @Test void moveOrderingTest(){
        BackendChessPosition testPosition = ChessConstants.startBoardState.toBackend(new ChessStates(),true);
        MoveGenerator generator = new MoveGenerator();
        MoveOrderer orderer = new MoveOrderer();
        ChessMove[] moves = generator.generateMoves(testPosition,false, PromotionType.ALL);
        orderer.sortMoves(null,testPosition.board,moves,null,null);
    }

    @Test void moveGenerationTest(){
        BackendChessPosition testPosition = ChessConstants.startBoardState.toBackend(new ChessStates(),true);
        MoveGenerator generator = new MoveGenerator();
        MoveOrderer orderer = new MoveOrderer();
        ChessMove[] moves = generator.generateMoves(testPosition,false, PromotionType.ALL);
        orderer.sortMoves(null,testPosition.board,moves,null,null);
        System.out.println(moves[0]);
    }

    @Test void transpositionTest(){
        TranspositionTable transpositionTable = new TranspositionTable(100000);
        long key1 = 127747477;
        long key2 = 245123412;
        long key3 = 1243535774;
        long key4 = 274324747;
        long key5 = 122774;
        transpositionTable.recordHash(key1,4,60,ChessConstants.startMove,Flag.EXACT);
        transpositionTable.recordHash(key2,4,80,ChessConstants.startMove,Flag.EXACT);
        transpositionTable.recordHash(key3,4,-130,ChessConstants.startMove,Flag.EXACT);
        transpositionTable.recordHash(key4,4,-180,ChessConstants.startMove,Flag.EXACT);
        transpositionTable.recordHash(key5,4,200,ChessConstants.startMove,Flag.EXACT);
        int out1 = transpositionTable.probeHash(key1,3,100,60);
        int out2 = transpositionTable.probeHash(key2,3,100,60);
        System.out.println(out1);
        System.out.println(out2);
        Assertions.assertNotEquals(out1,out2);
    }

    @Test void currentTimeMsTest(){
        for(int i = 0;i<10000;i++){
            System.out.println(System.currentTimeMillis());
            try
            {
                Thread.sleep(1);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
