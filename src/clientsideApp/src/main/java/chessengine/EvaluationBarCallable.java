package chessengine;

import java.util.concurrent.Callable;

public class EvaluationBarCallable implements Callable<minimaxOutput> {
    final Computer c;
    final int d;
    final boolean isWhite;
    ChessPosition position;
    ChessStates gameState;
    public EvaluationBarCallable(Computer c, ChessPosition board,ChessStates gameState, int depth, boolean isWhite){
        this.c = c;
        this.position = board;
        this.d =depth;
        this.isWhite = isWhite;
        this.gameState =gameState;
    }

    @Override
    public minimaxOutput call() throws Exception {
        return c.getFullEvalMinimax(position,gameState,d,isWhite);
    }
}
