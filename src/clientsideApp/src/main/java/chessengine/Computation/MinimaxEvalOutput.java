package chessengine.Computation;

public class MinimaxEvalOutput {


    private final double advantage;

    private int outputDepth;

    public MinimaxEvalOutput(double advantage) {
        this.advantage = advantage;
        this.outputDepth = 1;
    }

    public MinimaxEvalOutput incrementAndReturn() {
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
