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
        orderer.sortMoves(null,testPosition.board,moves,null,null,10);
    }

    @Test void moveGenerationTest(){
        BackendChessPosition testPosition = ChessConstants.startBoardState.toBackend(new ChessStates(),true);
        MoveGenerator generator = new MoveGenerator();
        MoveOrderer orderer = new MoveOrderer();
        ChessMove[] moves = generator.generateMoves(testPosition,false, PromotionType.ALL);
        orderer.sortMoves(null,testPosition.board,moves,null,null,10);
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

    @Test void checkmateAvoidanceFix(){
        String[] pgns = {"1. e4 e5 2. Nf3 Nc6 3. Bc4 Nf6 4. Ng5 d5 5. exd5 Nxd5 6. Nxf7 Kxf7 7. Qf3+ Ke6 8. Nc3 Nce7 9. d4 c6 10. O-O Qe8 11. Re1 Ng6 12. Nxd5 cxd5 13. Qxd5+ Kf6 14. Bg5+ Kxg5 15. Rxe5+ Nxe5 16. dxe5 Bf5 17. h4+ Kh6 18. Qd2+",
                "1. e4 e5 2. Nf3 d6 3. d4 Bg4 4. dxe5 Bxf3 5. Qxf3 dxe5 6. Bc4 Nf6 7. Qb3 Qe7 8. Nc3 c6 9. Bg5 b5 10. Nxb5 cxb5 11. Bxb5+ Nbd7 12. O-O-O Rd8 13. Rxd7 Rxd7 14. Rd1 Qe6 15. Bxd7+ Nxd7 16. Qb8+",
                "1. d4 d5 2. c4 e6 3. Nc3 c6 4. e4 dxe4 5. Nxe4 Bb4+ 6. Bd2 Bxd2+ 7. Qxd2 Nf6 8. Ng3 O-O 9. Nf3 Nbd7 10. Bd3 c5 11. O-O b6 12. d5 exd5 13. cxd5 Bb7 14. Rfe1 Nxd5 15. Rad1 N7f6 16. Nf5 g6 17. Qh6 gxf5 18. Bxf5 Kh8 19. Ng5"};

        for(String pgn : pgns){
            ChessGame testGame = ChessGame.createTestGame(pgn,false);
            testGame.moveToEndOfGame(false);
            Searcher searcher = new Searcher();
            SearchResult out = searcher.search(testGame.currentPosition.toBackend(testGame.gameState,testGame.isWhiteTurn()),1000);
            System.out.println(out.evaluation());
            System.out.println(out.move());
            System.out.println(out.depth());
            System.out.println();
        }

    }

    @Test void checkmateAvoidanceFix2(){
        ChessGame testGame = ChessGame.createTestGame("1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 \n" +
                "8. c3 O-O 9. h3 Na5 10. Bc2 c5 11. d4 Qc7 12. Nbd2 cxd4 13. cxd4 exd4 \n" +
                "14. Nxd4 Re8 15. b4 Nc6 16. Nxc6 Qxc6 17. Bb2 Bb7 18. Rc1 Qb6 19. Nb3 Rac8 \n" +
                "20. Bd4 Qd8 21. Na5 Ba8 22. Bd3 Rxc1 23. Qxc1 d5 24. e5 Nd7 25. a3 Bg5 \n" +
                "26. Qd1 Bf4 27. g3 Bxe5 28. Bxe5 Rxe5 29. Rxe5 Nxe5 30. Bxh7+ Kxh7 31. Qh5+ \n" +
                "Kg8 32. Qxe5 d4 33. Nb3 d3 34. Nd2 Qd5 35. Qxd5 Bxd5 36. f4 Kf8 37. Kf2 Ke7 \n" +
                "38. Ke3 Bc4 39. Kd4 Kd6 40. Nxc4+ bxc4 41. Kxc4 d2 42. Kc3 d1=Q 43. Kb2 Qf3 \n" +
                "44. a4 Qxg3 45. b5 axb5 46. axb5 Qxf4 47. Kc3 Kc5 48. b6 Kxb6 49. Kd3 Qh4 \n" +
                "50. Ke3 Qxh3+ 51. Ke4 Qe6+ 52. Kf4 Kc5 53. Kg3 Kd4 54. Kh4 Qg6 55. Kh3\n",false);
        testGame.moveToEndOfGame(false);
        Searcher searcher = new Searcher();
//        System.out.println(searcher.search(testGame.currentPosition.toBackend(testGame.gameState,testGame.isWhiteTurn()),1000000));
    }


    @Test void pvBufferTest(){
        PvBuffer buffer = new PvBuffer(4);
        buffer.putResult(new SearchResult(ChessConstants.startMove,1,1, null));
        buffer.putResult(new SearchResult(ChessConstants.startMove,2,1, null));
        buffer.putResult(new SearchResult(ChessConstants.startMove,3,1, null));
        System.out.println(Arrays.toString(buffer.getResults()));

    }
}
