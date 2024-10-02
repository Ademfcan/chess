package chessengine.Computation;

public class EvalOutput {


    private final double advantage;

    private int outputDepth;

    public EvalOutput(double advantage) {
        this.advantage = advantage;
        this.outputDepth = 1;
    }

    public EvalOutput(double advantage, int outputDepth) {
        this.advantage = advantage;
        this.outputDepth = outputDepth;
    }

    public EvalOutput incrementAndReturn() {
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
