package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

public class MinimaxMoveResult {
    private final ChessMove move;
    private final double advantage;
    private final double depth;

    public MinimaxMoveResult(ChessMove move, double advantage, double depth) {
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

    public double getDepth() {
        return depth;
    }
}
