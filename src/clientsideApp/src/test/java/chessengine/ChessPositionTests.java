package chessengine;

import chessengine.ChessRepresentations.*;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class ChessPositionTests {
    @Test void localPositionMoveTest(){
        // simple pawn move
        BackendChessPosition pos = ChessConstants.startBoardState.clonePosition().toBackend(new ChessStates(),false);
        GeneralChessFunctions.printBoardDetailed(pos.board);
        ChessMove simplePawnMove = new ChessMove(4,6,4,1,-10,0,true,false,true,0,false,false);
        pos.makeLocalPositionMove(simplePawnMove);
        GeneralChessFunctions.printBoardDetailed(pos.board);
        pos.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(pos.board);


        // en passant
        ChessGame passant = ChessGame.createTestGame("1.e4 e5 2.d4 exd4 3.c3 dxc3 4.Nxc3 Nf6 5.e5 Ng8 6.f4 d5 7.exd6",false);
        passant.moveToEndOfGame(true);
        ChessMove passantMove = passant.currentPosition.getMoveThatCreatedThis();
        System.out.println(passantMove.isEnPassant());
        passant.changeToDifferentMove(-1,true,true);
        BackendChessPosition prePassant = passant.currentPosition.toBackend(passant.gameState,false);
        GeneralChessFunctions.printBoardDetailed(prePassant.board);
        prePassant.makeLocalPositionMove(passantMove);
        GeneralChessFunctions.printBoardDetailed(prePassant.board);
        prePassant.undoLocalPositionMove();
        GeneralChessFunctions.printBoardDetailed(prePassant.board);


        // general test start state
        ChessStates start = new ChessStates();
        BackendChessPosition posGeneral = ChessConstants.startBoardState.clonePosition().toBackend(start,false);
        GeneralChessFunctions.printBoardDetailed(posGeneral.board);
        for(ChessMove c : posGeneral.getAllChildMoves(true,start,new HashMap<>())){
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
            ChessGame generaltest = ChessGame.createTestGame(pgn,false);
            generaltest.moveToEndOfGame(false);
//            GeneralChessFunctions.printBoardDetailed(generaltest.currentPosition.board);
            BackendChessPosition passantBackend = generaltest.currentPosition.toBackend(generaltest.gameState,false);
            Assertions.assertEquals(passantBackend.gameState.toString(),generaltest.gameState.toString());
            List<BackendChessPosition> childPositions = generaltest.currentPosition.getAllChildPositions(generaltest.isWhiteTurn(),generaltest.gameState);
            List<ChessMove> childMoves = generaltest.currentPosition.getAllChildMoves(generaltest.isWhiteTurn(),generaltest.gameState,new HashMap<>());
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

                List<BackendChessPosition> childPositions2 = childPos.getAllChildPositions(!generaltest.isWhiteTurn(),childPos.gameState);
                List<ChessMove> childMoves2 = childPos.getAllChildMoves(!generaltest.isWhiteTurn(),childPos.gameState,new HashMap<>());
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
                    Assertions.assertEquals(childPos2.gameState.toString(),passantBackend.gameState.toString());
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
                Assertions.assertEquals(childPos.gameState.toString(),passantBackend.gameState.toString());

                passantBackend.undoLocalPositionMove();
            }

        }






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
            ChessGame testGame = ChessGame.createTestGame(pgn,false);
            // follower possition will make all the moves and compare gamestates + positions
            BackendChessPosition follower = ChessConstants.startBoardState.clonePosition().toBackend(new ChessStates(),false);
            BackendChessPosition followerTruth = ChessConstants.startBoardState.clonePosition().toBackend(new ChessStates(),false);
            List<ChessMove> moves = testGame.getMoves();

            Stack<BackendChessPosition> truthPositions = new Stack<>();
            Stack<ChessStates> truthGameStates = new Stack<>();
            // forward step
            System.out.println("Forward-------------------");
            int j = 0;
            for(ChessMove move : moves){
                follower.makeLocalPositionMove(move);
                truthPositions.push(followerTruth);
                truthGameStates.push(followerTruth.gameState.cloneState());
                followerTruth = new BackendChessPosition(followerTruth,move);
                equals(followerTruth.gameState.toString(),(follower.gameState.toString()),j);
                equals(GeneralChessFunctions.getBoardDetailedString(followerTruth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),j);
                j++;
            }
            System.out.println("Backward-------------------");
            // going backward
            for(int i = moves.size()-1;i>=0;i--){
                follower.undoLocalPositionMove();
                BackendChessPosition truth = truthPositions.pop();
                ChessStates truthState = truthGameStates.pop();
                equals(truthState.toString(),(follower.gameState.toString()),i);
                equals(GeneralChessFunctions.getBoardDetailedString(truth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),i);
            }

        }

    }

    @Test void moveForwardAndBackwardTests(){
        ChessGame testGame = ChessGame.createTestGame("1.e4 d5 2.Nf3 Nf6 3.Bb5+ Bd7 4.Nc3 e6 5.a3 Bc5 6.O-O O-O 7.Re1 Re8 8.Re3 Re7",false);
        // follower possition will make all the moves and compare gamestates + positions
        BackendChessPosition follower = ChessConstants.startBoardState.clonePosition().toBackend(new ChessStates(),false);
        BackendChessPosition followerTruth = ChessConstants.startBoardState.clonePosition().toBackend(new ChessStates(),false);
        List<ChessMove> moves = testGame.getMoves();

        Stack<BackendChessPosition> truthPositions = new Stack<>();
        Stack<ChessStates> truthGameStates = new Stack<>();
        // forward step
        System.out.println("Forward-------------------");
        int j = 0;
        for(ChessMove move : moves){
            follower.makeLocalPositionMove(move);
            truthPositions.push(followerTruth);
            truthGameStates.push(followerTruth.gameState.cloneState());
            followerTruth = new BackendChessPosition(followerTruth,move);
            equals(followerTruth.gameState.toString(),(follower.gameState.toString()),j);
            equals(GeneralChessFunctions.getBoardDetailedString(followerTruth.board),GeneralChessFunctions.getBoardDetailedString(follower.board),j);
            j++;
        }
        System.out.println("Backward-------------------");
        // going backward
        for(int i = moves.size()-1;i>=0;i--){
            follower.undoLocalPositionMove();
            BackendChessPosition truth = truthPositions.pop();
            ChessStates truthState = truthGameStates.pop();
            equals(truthState.toString(),(follower.gameState.toString()),i);
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
        ChessStates g1 = new ChessStates();
        System.out.println("g1 hash: " + System.identityHashCode(g1));
        g1.removeCastlingRight(true);
        System.out.println("g1 hash post: " + System.identityHashCode(g1));
        ChessStates g2 = g1.cloneState();
        ChessStates g3 = new ChessStates();

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
        ChessGame game = ChessGame.createTestGame("1.c4 c5 2.Nf3 Nf6 3.Qc2 Qc7 4.e4 e5 5.Bd3 Bd6 6.Nxe5 Bxe5 7.Qc3 Bxc3 8.dxc3 Qxh2",false);
        game.moveToEndOfGame(false);
        BackendChessPosition referencePosition = game.currentPosition.clonePosition().toBackend(game.gameState.cloneState(),game.gameState.isStaleMated());

        List<BackendChessPosition> possiblePositions = referencePosition.getAllChildPositions(game.isWhiteTurn(),referencePosition.gameState);
        List<ChessMove> possibleMoves = referencePosition.getAllChildMoves(game.isWhiteTurn(), referencePosition.gameState,new HashMap<>());

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
