package chessengine;

import chessengine.Computation.*;
import chessserver.Functions.*;
import chessserver.Misc.ChessConstants;
import chessserver.ChessRepresentations.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ComputerTests {
    @Test void getFullEvalTests(){

        ChessGame equalGame = ChessGame.createTestGame("1.e4 e5");
        System.out.println(ComputerHelperFunctions.getFullEval(equalGame.getCurrentPosition(), equalGame.getGameState(),false,false));

    }



    @Test
    void evaluateGamesWithStockfish() {
        // Array of PGNs (example games or real games)
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
                "1.e4 d6 2.d4 Nf6 3.Nc3 g6 4.Bg5 Bg7 5.Qd2 h6 6.Bf4 g5 7.Be3 Ng4" // Pirc Defense, Byrne Variation
        };

        Stockfish stockfish = new Stockfish();
        Searcher computer = new Searcher();

        if (stockfish.startEngine()) {
            for (String pgn : pgns) {
                ChessGame game = ChessGame.createTestGame(pgn); // Create game from PGN
                game.moveToEndOfGame();
                String fen = PgnFunctions.positionToFEN(game.getCurrentPosition(), game.getGameState(), game.isWhiteTurn());

                // Calculate evaluation with Computer
                float computerEval = (float) computer.search(game.getCurrentPosition().toBackend(game.getGameState(), game.isWhiteTurn()),1000).evaluation();

                // Calculate evaluation with Stockfish
                double stockfishEval = stockfish.getEvalScore(fen, game.isWhiteTurn(),1000).getAdvantage(); // 1000 milliseconds time limit

                // Compare evaluations within a percentage difference (10%)
                double tolerance = 0.1d; // 10%
                double difference = Math.abs(computerEval - stockfishEval);
                double average = (computerEval + stockfishEval) / 2;
                double percentageDifference = (difference / average) * 100;

                System.out.println("PGN: " + pgn);
                System.out.println("FEN: " + fen);
                System.out.println("Computer Eval: " + computerEval);
                System.out.println("Stockfish Eval: " + stockfishEval);
                System.out.println("Percentage Difference: " + percentageDifference + "%");

                // Assert that the percentage difference is within tolerance
//                Assertions.assertTrue(percentageDifference <= tolerance * 100);
            }

            stockfish.stopEngine();
        } else {
            Assertions.fail("Failed to start Stockfish engine");
        }
    }
    private static HashMap<Long, TestContainer> zobristMap = new HashMap<>();
    private static HashMap<Integer, TestContainer> objectsHashMap = new HashMap<>();

    private static int zobristCollisionCount;
    private static int objectHashCollisionCount;
    private static int totalUniquePositionCount;


    private static final ZobristHasher hasher = new ZobristHasher();
//    @Test void hashCollisionTest(){
//        BackendChessPosition startPos = ChessConstants.startBoardState.clonePosition().toBackend(new ChessStates(),false);
//        // going to see how many collisions each hash method has
//        miniMaxForHashTest(startPos,startPos.getGameState(),6,true);
//        System.out.println("Zobrist Collision Count: " + zobristCollisionCount);
//        System.out.println("Objects Hash Collision Count: " + objectHashCollisionCount);
//        System.out.println("Total unique positions created: " + totalUniquePositionCount/2);
//    }
    // creating lots of new positions and checking for hash collisons
    private void miniMaxForHashTest(BackendChessPosition position, ChessGameState gameState, int depth, boolean isWhiteTurn){
        // all recursive stop cases
//        System.out.println(cnt++);

        int objKey = Objects.hash(position.hashCode(),isWhiteTurn);
        long zobKey = hasher.computeHash(position.board);
        if(zobristMap.containsKey(zobKey)){
//            logger.info("Transtable value being used");
            TestContainer oldPos = zobristMap.get(zobKey);
            if(!oldPos.board.equals(position.board) || oldPos.isWhiteTurn != isWhiteTurn) {
                zobristMap.put(zobKey,new TestContainer(position.board,isWhiteTurn));
                zobristCollisionCount++;
            }
        }
        else{
            totalUniquePositionCount++;
            zobristMap.put(zobKey,new TestContainer(position.board,isWhiteTurn));
        }

        if(objectsHashMap.containsKey(objKey)){
//            logger.info("Transtable value being used");
            TestContainer oldPos = objectsHashMap.get(objKey);
            if(!oldPos.board.equals(position.board) || oldPos.isWhiteTurn != isWhiteTurn) {
                objectsHashMap.put(objKey,new TestContainer(position.board,isWhiteTurn));
                objectHashCollisionCount++;
            }
        }
        else{
            totalUniquePositionCount++;
            objectsHashMap.put(objKey,new TestContainer(position.board,isWhiteTurn));
        }
//        System.out.println(totalUniquePositionCount/2);


        if(position.isDraw()){
            // break case
            return;
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(true,position,gameState)){
            // break case
            return;
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(false,position,gameState)){
            // also break case
            return;
        }

        if(depth == 0){
            return;
        }
        if(isWhiteTurn){
            EvalOutput maxEval = new EvalOutput(Double.NEGATIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(true,gameState);
            for(int i = 0;i<childMoves.size();i++){
                ChessMove c = childMoves.get(i);
                position.makeLocalPositionMove(c);
                miniMaxForHashTest(position,position.getGameState(), depth - 1, false);
                position.undoLocalPositionMove();


            }
        }
        else{
            EvalOutput minEval = new EvalOutput(Double.POSITIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(false,gameState);

            for(int i = 0;i<childMoves.size();i++){
                ChessMove c = childMoves.get(i);
//
                position.makeLocalPositionMove(c);
                miniMaxForHashTest(position,position.getGameState(), depth - 1, true);
                position.undoLocalPositionMove();



            }
        }



    }

    @Test void castlingTest(){
        ChessGame game = ChessGame.createTestGame("1.e4 e5 2.Nf3 Nf6 3.Bc4 Bc5 4.h4 h5");
        game.moveToEndOfGame();
        BackendChessPosition castleTest = game.getCurrentPosition().clonePosition().toBackend(game.getGameState(), game.isWhiteTurn());
        GeneralChessFunctions.printBoardDetailed(castleTest.board);
        ChessMove castleMove = castleTest.getAllChildMoves(game.isWhiteTurn(),castleTest.getGameState()).stream().filter(ChessMove::isCastleMove).toList().get(0);
        castleTest.makeLocalPositionMove(castleMove);
        GeneralChessFunctions.printBoardDetailed(castleTest.board);
        ChessMove rookMove = castleTest.getAllChildMoves(game.isWhiteTurn(),castleTest.getGameState()).stream().filter(m->m.getBoardIndex() == ChessConstants.ROOKINDEX).toList().get(0);
        castleTest.makeLocalPositionMove(rookMove);
        GeneralChessFunctions.printBoardDetailed(castleTest.board);
        castleTest.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(castleTest.board);


        castleTest.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(castleTest.board);
        ChessMove rookMove2 = castleTest.getAllChildMoves(game.isWhiteTurn(),castleTest.getGameState()).stream().filter(m->m.getBoardIndex() == ChessConstants.ROOKINDEX).toList().get(0);
        castleTest.makeLocalPositionMove(rookMove2);
        GeneralChessFunctions.printBoardDetailed(castleTest.board);
        castleTest.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(castleTest.board);


    }



    private class TestContainer {
        BitBoardWrapper board;





        boolean isWhiteTurn;
        public TestContainer(BitBoardWrapper board, boolean isWhiteTurn){
            this.isWhiteTurn = isWhiteTurn;
            this.board = board;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestContainer that = (TestContainer) o;
            return isWhiteTurn == that.isWhiteTurn && Objects.equals(board, that.board);
        }

    }



    @Test void isCheckmatedTest(){
        ChessGame game = ChessGame.createTestGame("1.e4 e6 2.f4 Bc5 3.d4 Qh4+ 4.g3 Qe7 5.Qf3 Bxd4 6.Be3 Bxb2 7.Nc3 Bxc3+ 8.Bd2 Bxa1 9.Qd1 Bd4 10.Bc3 Bxc3+ 11.Ke2 Qd6 12.Qd3 Qxd3+ 13.xd3 d5 14.Ke3 e5 15.d4 Bxd4+ 16.Kd3 xe4+ 17.Kxe4 Nf6+ 18.Kd3 Bxg1 19.Kc4 xf4 20.Kb5 xg3 21.Kc4 xh2 22.Kd3 Bf5+ 23.Ke2 Be4 24.Ke1 Bxh1 25.Ke2 Bf3+ 26.Kxf3 h1=Q+ 27.Ke2 Bc5 28.Kd2 Qxf1 29.Kc3 Qe2 30.Kb3 Kd7 31.Ka4 Qc4");
        game.moveToEndOfGame();
        GeneralChessFunctions.printBoardDetailed(game.getCurrentPosition().board);
        System.out.println(AdvancedChessFunctions.isCheckmated(game.getCurrentPosition(),game.getGameState()));
    }



}
