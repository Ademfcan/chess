package chessengine;

import java.util.concurrent.Callable;

public class GetComputerMoveCallable implements Callable<ChessMove> {
    final Computer c;
    final boolean isWhite;
    final ChessPosition position;
    final ChessStates gameState;
    public GetComputerMoveCallable(Computer c, boolean isWhite, ChessPosition pos,ChessStates gameState){
        this.c = c;
        this.isWhite = isWhite;
        this.position = pos;
        this.gameState = gameState;

    }

    @Override
    public ChessMove call(){
        return c.getComputerMove(isWhite,position,gameState);
    }
}
