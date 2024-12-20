package chessengine.Computation;

import chessserver.ChessRepresentations.ChessMove;

public class ComputerOutput {
    public final ChessMove move;
    public final double advantage;

    public ComputerOutput(ChessMove move, double advantage) {
        this.move = move;
        this.advantage = advantage;
    }
}
