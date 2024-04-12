package chessengine;

import java.util.concurrent.Callable;

public class DynamicEvaluationCallable implements Callable<Double> {
    final Computer c;
    final int d;
    final boolean isWhite;
    long[] whitePieces;
    long[] blackPieces;
    public DynamicEvaluationCallable(Computer c, long[] whitePieces, long[] blackPieces,int depth,boolean isWhite){
        this.c = c;
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.d =depth;
        this.isWhite = isWhite;
    }

    @Override
    public Double call() throws Exception {
        return c.getFullEvalMinimax(whitePieces,blackPieces,d,isWhite);
    }
}
