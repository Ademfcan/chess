package chessengine;

import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import chessserver.ChessRepresentations.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Stack;


public class ChessPositionTests {
    @Test void localPositionMoveTest(){
        // simple pawn move
        BackendChessPosition pos = ChessConstants.startBoardState.clonePosition().toBackend(new ChessGameState(), true);
        GeneralChessFunctions.printBoardDetailed(pos.board);
        ChessMove simplePawnMove = new ChessMove(4,6,4,1,-10,0,true,false,true,0,false,false);
        pos.makeLocalPositionMove(simplePawnMove);
        GeneralChessFunctions.printBoardDetailed(pos.board);
        pos.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(pos.board);


        // en passant
        ChessGame passant = ChessGame.createTestGame("1.e4 e5 2.d4 exd4 3.c3 dxc3 4.Nxc3 Nf6 5.e5 Ng8 6.f4 d5 7.exd6");
        passant.moveToEndOfGame();
        ChessMove passantMove = passant.getCurrentPosition().getMoveThatCreatedThis();
        System.out.println(passantMove.isEnPassant());
        passant.changeToDifferentMove(-1);
        BackendChessPosition prePassant = passant.getCurrentPosition().toBackend(passant.getGameState(), false);
        GeneralChessFunctions.printBoardDetailed(prePassant.board);
        prePassant.makeLocalPositionMove(passantMove);
        GeneralChessFunctions.printBoardDetailed(prePassant.board);
        prePassant.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(prePassant.board);


        // general test start state
        ChessGameState start = new ChessGameState();
        BackendChessPosition posGeneral = ChessConstants.startBoardState.clonePosition().toBackend(start, true);
        GeneralChessFunctions.printBoardDetailed(posGeneral.board);
        for(ChessMove c : posGeneral.getAllChildMoves(true,start)){
            System.out.println(c.toString());
            posGeneral.makeLocalPositionMove(c);
            GeneralChessFunctions.printBoardDetailed(posGeneral.board);
            posGeneral.undoLocalPositionMove();
            GeneralChessFunctions.printBoardDetailed(posGeneral.board);

        }





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
        for(String pgn : pgns){

            // general test midgame state
            ChessGame generaltest = ChessGame.createTestGame(pgn);
            generaltest.moveToEndOfGame();
//            GeneralChessFunctions.printBoardDetailed(generaltest.getCurrentPosition().board);
            BackendChessPosition passantBackend = generaltest.getCurrentPosition().toBackend(generaltest.getGameState(), generaltest.isWhiteTurn());
            Assertions.assertEquals(passantBackend.getGameState().toString(),generaltest.getGameState().toString());
            List<BackendChessPosition> childPositions = generaltest.getCurrentPosition().getAllChildPositions(generaltest.isWhiteTurn(),generaltest.getGameState());
            List<ChessMove> childMoves = generaltest.getCurrentPosition().getAllChildMoves(generaltest.isWhiteTurn(),generaltest.getGameState());
            assert childPositions.size() == childMoves.size();

            for(int i = 0;i<childPositions.size();i++){
//                System.out.println("Depth 1 Child Pos # " + (i+1));
                BackendChessPosition childPos = childPositions.get(i);
//                System.out.println("Deep Clone Board:");
//                GeneralChessFunctions.printBoardDetailed(childPos.board);
                ChessMove childMove = childMoves.get(i);
                passantBackend.makeLocalPositionMove(childMove);
//                System.out.println("Local Move Played: " + childMove.toString());
//                System.out.println("Local Move Board:");
//                GeneralChessFunctions.printBoardDetailed(passantBackend.board);

                List<BackendChessPosition> childPositions2 = childPos.getAllChildPositions(!generaltest.isWhiteTurn(),childPos.getGameState());
                List<ChessMove> childMoves2 = childPos.getAllChildMoves(!generaltest.isWhiteTurn(),childPos.getGameState());
                Assertions.assertEquals(childPositions2.size(),childMoves2.size());

                for(int j = 0;j<childPositions2.size();j++){

                    BackendChessPosition childPos2 = childPositions2.get(j);





                    ChessMove childMove2 = childMoves2.get(j);
                    String ogPassant = GeneralChessFunctions.getBoardDetailedString(passantBackend.board);
                    Assertions.assertEquals(ogPassant,GeneralChessFunctions.getBoardDetailedString(childPos.board));
                    passantBackend.makeLocalPositionMove(childMove2);
                    // todo fix this mf

                    String childPosStr2 = GeneralChessFunctions.getBoardDetailedString(childPos2.board);
                    String childMoveStr2 = GeneralChessFunctions.getBoardDetailedString(passantBackend.board);
                    if(!childMoveStr2.equals(childPosStr2)){
                        System.out.println("Depth 2 Child Pos # " + (j+1));
                        System.out.println("\n\n\n");
                        System.out.println("Original Local Board:");
                        GeneralChessFunctions.printBoardDetailed(childPos.board);
                        System.out.println("\n\n\n");
                        System.out.println("Original New Board:");
                        System.out.println(ogPassant);
                        System.out.println("\n\n\n");

                        System.out.println("Deep Clone Board2:");
                        GeneralChessFunctions.printBoardDetailed(childPos2.board);
                        System.out.println("\n\n\n");

                        System.out.println("Local Move Played2: " + childMove2.toString());
                        System.out.println("Local Move Board2:");
                        GeneralChessFunctions.printBoardDetailed(passantBackend.board);


                        System.out.println("ERROR NO MATCH DEPTH 2");


                    }

                    Assertions.assertEquals(childPos2.getMoveThatCreatedThis(),passantBackend.getMoveThatCreatedThis());
                    Assertions.assertEquals(childPos2.getGameState().toString(),passantBackend.getGameState().toString());
                    passantBackend.undoLocalPositionMove();
                    if(!childMoveStr2.equals(childPosStr2)){
                        System.out.println("Undo'd Local Board:");
                        GeneralChessFunctions.printBoardDetailed(passantBackend.board);
                        System.out.println("\n\n\n");
                    }
                    Assertions.assertEquals(childPos.getMoveThatCreatedThis(),passantBackend.getMoveThatCreatedThis());

                    Assertions.assertEquals(childPosStr2,childMoveStr2);
                    String childPosOg = GeneralChessFunctions.getBoardDetailedString(childPos.board);
                    String childMoveStrRev2 = GeneralChessFunctions.getBoardDetailedString(passantBackend.board);
                    Assertions.assertEquals(childPosOg,childMoveStrRev2);
                    if(!childPosOg.equals(childMoveStrRev2)){
                        System.out.println("AAAAAAAAAAAAAAAAA POST1!!!!!" + childMove2.toString());
                        System.out.println("AAAAAAAAAAAAAAAAA POST2!!!!!" + childPos2.getMoveThatCreatedThis().toString());
                        System.out.println(generaltest.gameToPgn());
                        System.out.println("OG: \n" + childPosOg);
                        System.out.println("Move: \n" + childMoveStr2);
                        System.out.println("Expected Move: \n" + childPosStr2);
                        System.out.println("Rev: \n" + childMoveStrRev2);
                    }

                }


                String childPosStr = GeneralChessFunctions.getBoardDetailedString(childPos.board);
                String childMoveStr = GeneralChessFunctions.getBoardDetailedString(passantBackend.board);
                Assertions.assertEquals(childPosStr,childMoveStr);
                Assertions.assertEquals(childPos.getGameState().toString(),passantBackend.getGameState().toString());

                passantBackend.undoLocalPositionMove();
            }

        }






    }


    @Test void promoTest(){
        ChessGame testGame1 = ChessGame.createTestGame("1.e4 e5 2.Nf3 Qf6 3.Bc4 Bc5 4.d3 b5 5.Bb3 h6 6.O-O Bb7 7.Nc3 Bc6 8.a4 xa4 9.Nxa4 Bd6 10.c4 Na6 11.d4 xd4 12.Nxd4 Bxe4 13.Nb5 Rb8 14.Nxd6+ Qxd6 15.Qxd6 xd6 16.Ba2 Rb4 17.Nc3 Bd3 18.Rd1 Nc5 19.Bf4 Rb6 20.Nd5 Rxb2 21.Bxd6 Ne4 22.Bc7 Nxf2 23.Rde1+ Ne4 24.Rad1 Bc2 25.Rd4 f5 26.Be5 Rxa2 27.Bxg7 Rh7 28.Be5 d6 29.Bxd6 Rd7 30.c5 Ra4 31.Rxa4 Bxa4 32.Nc7+ Kd8 33.Ne6+ Kc8 34.Ra1 Nxd6 35.xd6 Rxd6 36.Rxa4 Rd1+ 37.Kf2 Nf6 38.Rxa7 Ne4+ 39.Kf3 Rf1+ 40.Ke3 Re1+ 41.Kf4 Nd6 42.Ng7 Rb1 43.Nxf5 Nxf5 44.Kxf5 Kb8 45.Rh7 Rb2 46.Rxh6 Ka7 47.Rg6 Rb5+ 48.Kf6 Rh5 49.h3 Kb8 50.Kg7 Rd5 51.Kh6 Ka7 52.g4 Rd8 53.Kh7 Kb7 54.h4 Rd3 55.h5 Rd7+ 56.Rg7 Rxg7+ 57.Kxg7 Kc7 58.h6 Kb7 59.h7 Kb6 60.h8=Q Ka5 61.Qe8 Kb4 62.Kf6 Ka3 63.g5 Ka2 64.g6 Kb2");
        testGame1.moveToEndOfGame();
//        List<ChessMove>
    }

    @Test void inlinePositionTest(){
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
        for(String pgn : pgns){
            ChessGame testGame = ChessGame.createTestGame(pgn);
            // follower possition will make all the moves and compare gamestates + positions
            BackendChessPosition follower = ChessConstants.startBoardState.clonePosition().toBackend(new ChessGameState(), true);
            BackendChessPosition followerTruth = ChessConstants.startBoardState.clonePosition().toBackend(new ChessGameState(), true);
            List<ChessMove> moves = testGame.getMoves();

            Stack<BackendChessPosition> truthPositions = new Stack<>();
            Stack<ChessGameState> truthGameStates = new Stack<>();
            // forward step
            System.out.println("Forward-------------------");
            int j = 0;
            for(ChessMove move : moves){
                follower.makeLocalPositionMove(move);
                truthPositions.push(followerTruth);
                truthGameStates.push(followerTruth.getGameState().cloneState());
                followerTruth = new BackendChessPosition(followerTruth,move);
                equals(followerTruth.getGameState().toString(),(follower.getGameState().toString()),j);
                equals(GeneralChessFunctions.getBoardDetailedString(followerTruth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),j);
                j++;
            }
            System.out.println("Backward-------------------");
            // going backward
            for(int i = moves.size()-1;i>=0;i--){
                follower.undoLocalPositionMove();
                BackendChessPosition truth = truthPositions.pop();
                ChessGameState truthState = truthGameStates.pop();
                equals(truthState.toString(),(follower.getGameState().toString()),i);
                equals(GeneralChessFunctions.getBoardDetailedString(truth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),i);
            }

        }

    }

    @Test void moveForwardAndBackwardTests(){
        ChessGame testGame = ChessGame.createTestGame("1.e4 d5 2.Nf3 Nf6 3.Bb5+ Bd7 4.Nc3 e6 5.a3 Bc5 6.O-O O-O 7.Re1 Re8 8.Re3 Re7");
        // follower possition will make all the moves and compare gamestates + positions
        BackendChessPosition follower = ChessConstants.startBoardState.clonePosition().toBackend(new ChessGameState(), true);
        BackendChessPosition followerTruth = ChessConstants.startBoardState.clonePosition().toBackend(new ChessGameState(), true);
        List<ChessMove> moves = testGame.getMoves();

        Stack<BackendChessPosition> truthPositions = new Stack<>();
        Stack<ChessGameState> truthGameStates = new Stack<>();
        // forward step
        System.out.println("Forward-------------------");
        int j = 0;
        for(ChessMove move : moves){
            follower.makeLocalPositionMove(move);
            truthPositions.push(followerTruth);
            truthGameStates.push(followerTruth.getGameState().cloneState());
            followerTruth = new BackendChessPosition(followerTruth,move);
            equals(followerTruth.getGameState().toString(),(follower.getGameState().toString()),j);
            equals(GeneralChessFunctions.getBoardDetailedString(followerTruth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),j);
            j++;
        }
        System.out.println("Backward-------------------");
        // going backward
        for(int i = moves.size()-1;i>=0;i--){
            follower.undoLocalPositionMove();
            BackendChessPosition truth = truthPositions.pop();
            ChessGameState truthState = truthGameStates.pop();
            equals(truthState.toString(),(follower.getGameState().toString()),i);
            equals(GeneralChessFunctions.getBoardDetailedString(truth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),i);
        }
    }

    void equals(String expected, String actual,int currentIndex){
        if(!expected.equals(actual)){
            System.out.println("Not Equal -----------------");
            System.out.println("Current Index: " + currentIndex);
            System.out.println("expected-------------------");
            System.out.println(expected);
            System.out.println("actual----------------------");
            System.out.println(actual);
        }
        Assertions.assertEquals(expected,actual);
    }


    @Test void gameStateCloneTest(){
        ChessGameState g1 = new ChessGameState();
        System.out.println("g1 hash: " + System.identityHashCode(g1));
        g1.removeCastlingRight(true);
        System.out.println("g1 hash post: " + System.identityHashCode(g1));
        ChessGameState g2 = g1.cloneState();
        ChessGameState g3 = new ChessGameState();

        System.out.println("g2 hash: " + System.identityHashCode(g2));
        System.out.println("g3 hash: " + System.identityHashCode(g3));


        ChessPosition p1 = ChessConstants.startBoardState.clonePosition();
        ChessPosition p3 = ChessConstants.startBoardState.clonePosition();
        ChessPosition p2 = p1.clonePosition();

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
    }

    @Test void rookEdgeCaseTest(){
        ChessGame game = ChessGame.createTestGame("1.c4 c5 2.Nf3 Nf6 3.Qc2 Qc7 4.e4 e5 5.Bd3 Bd6 6.Nxe5 Bxe5 7.Qc3 Bxc3 8.dxc3 Qxh2");
        game.moveToEndOfGame();
        BackendChessPosition referencePosition = game.getCurrentPosition().clonePosition().toBackend(game.getGameState().cloneState(), game.isWhiteTurn());

        List<BackendChessPosition> possiblePositions = referencePosition.getAllChildPositions(game.isWhiteTurn(),referencePosition.getGameState());
        List<ChessMove> possibleMoves = referencePosition.getAllChildMoves(game.isWhiteTurn(), referencePosition.getGameState());

        List<BackendChessPosition> rookFilteredPos = possiblePositions.stream().filter(p->p.getMoveThatCreatedThis().getBoardIndex()==ChessConstants.ROOKINDEX).toList();
        List<ChessMove> rookFilteredMoves = possibleMoves.stream().filter(m->m.getBoardIndex()==ChessConstants.ROOKINDEX).toList();
        Assertions.assertEquals(rookFilteredMoves.size(),rookFilteredPos.size());
        // should be in order
        for(int i = 0;i<rookFilteredPos.size();i++){
            ChessMove rookMove = rookFilteredMoves.get(i);
            System.out.println(rookMove);
            BackendChessPosition rookPos = rookFilteredPos.get(i);
            ChessPosition validation = referencePosition.clonePosition();
            referencePosition.makeLocalPositionMove(rookMove);
            GeneralChessFunctions.printBoardDetailed(referencePosition.board);
            Assertions.assertEquals(GeneralChessFunctions.getBoardDetailedString(referencePosition.board),GeneralChessFunctions.getBoardDetailedString(rookPos.board));
            referencePosition.undoLocalPositionMove();
            Assertions.assertEquals(GeneralChessFunctions.getBoardDetailedString(referencePosition.board),GeneralChessFunctions.getBoardDetailedString(validation.board));

        }
    }


}
