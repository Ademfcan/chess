package chessengine;

public class ComputerOutput {
    ChessMove move;
    double advantage;

    public ComputerOutput(ChessMove move, double advantage) {
        this.move = move;
        this.advantage = advantage;
    }
}
