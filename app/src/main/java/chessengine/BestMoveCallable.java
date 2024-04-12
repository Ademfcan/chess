package chessengine;

import java.util.concurrent.Callable;

public class BestMoveCallable implements Callable<chessMove> {
    final Computer c;
    final boolean isWhite;
    final long[] whitePieces;
    final long[] blackPieces;
    public BestMoveCallable(Computer c, boolean isWhite, long[] whitePieces, long[] blackPieces){
        this.c = c;
        this.isWhite = isWhite;
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
    }

    @Override
    public chessMove call(){
        return c.getComputerMove(isWhite,whitePieces,blackPieces);
    }
}
