package chessengine;

import chessserver.ChessRepresentations.ChessGameState;
import chessengine.Computation.Stockfish;
import chessserver.Functions.PgnFunctions;
import chessserver.Misc.ChessConstants;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class StockfishTests {
    @Test void nmovesOutputVerification(){
        Stockfish s = new Stockfish();
        s.startEngine();
        System.out.println(Arrays.toString(s.getBestNMoves(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessGameState(), true),true,ChessConstants.startBoardState.board,1000, 4)));
    }

    @Test void evalOutputVerification(){
        Stockfish s = new Stockfish();
        s.startEngine();
        System.out.println(s.getEvalScore(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessGameState(), true), true,1000));
    }

    @Test void multipleStockfishTests(){
        Stockfish s = new Stockfish();
        s.startEngine();

        Stockfish s2 = new Stockfish();
        s2.startEngine();
        System.out.println(s.getEvalScore(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessGameState(), true), true,1000));
        System.out.println(s2.getEvalScore(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessGameState(), true),true, 1000));

    }
}
