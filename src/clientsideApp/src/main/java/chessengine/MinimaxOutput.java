package chessengine;

public class MinimaxOutput {


    private final double advantage;

    private int outputDepth;

    public MinimaxOutput(double advantage) {
        this.advantage = advantage;
        this.outputDepth = 1;
    }

    public MinimaxOutput incrementAndReturn() {
        this.outputDepth++;
        return this;
    }

    public int getOutputDepth() {
        return outputDepth;
    }

    public double getAdvantage() {
        return advantage;
    }
}
