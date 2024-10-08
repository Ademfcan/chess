package chessengine;

import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.Stockfish;
import chessengine.Functions.PgnFunctions;
import chessengine.Misc.ChessConstants;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class StockfishTests {
    @Test void nmovesOutputVerification(){
        Stockfish s = new Stockfish();
        s.startEngine();
        System.out.println(Arrays.toString(s.getBestNMoves(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessStates(), true),true,ChessConstants.startBoardState.board,1000, 4)));
    }

    @Test void evalOutputVerification(){
        Stockfish s = new Stockfish();
        s.startEngine();
        System.out.println(s.getEvalScore(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessStates(), true), true,1000));
    }

    @Test void multipleStockfishTests(){
        Stockfish s = new Stockfish();
        s.startEngine();

        Stockfish s2 = new Stockfish();
        s2.startEngine();
        System.out.println(s.getEvalScore(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessStates(), true), true,1000));
        System.out.println(s2.getEvalScore(PgnFunctions.positionToFEN(ChessConstants.startBoardState, new ChessStates(), true),true, 1000));

    }
}
