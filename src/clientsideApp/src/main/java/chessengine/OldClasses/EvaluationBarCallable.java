//package chessengine.Async;
//
//import chessengine.ChessRepresentations.ChessPosition;
//import chessengine.ChessRepresentations.ChessStates;
//import chessengine.Computation.Computer;
//import chessengine.Computation.EvalOutput;
//
//import java.util.concurrent.Callable;
//
//public class EvaluationBarCallable implements Callable<EvalOutput> {
//    final Computer c;
//    final int d;
//    final boolean isWhite;
//    ChessPosition position;
//    ChessStates gameState;
//
//    public EvaluationBarCallable(Computer c, ChessPosition board, ChessStates gameState, int depth, boolean isWhite) {
//        this.c = c;
//        this.position = board;
//        this.d = depth;
//        this.isWhite = isWhite;
//        this.gameState = gameState;
//    }
//
//    @Override
//    public EvalOutput call() throws Exception {
//        return c.getFullEvalMinimax(position, gameState, d, isWhite);
//    }
//}
