package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

public class MoveOutput {
    private final ChessMove move;
    private final double advantage;
    private final int depth;

    public MoveOutput(ChessMove move, double advantage, int depth) {
        this.move = move;
        this.advantage = advantage;
        this.depth = depth;
    }

    public ChessMove getMove() {
        return move;
    }

    public double getAdvantage() {
        return advantage;
    }

    public int getDepth() {
        return depth;
    }
}
