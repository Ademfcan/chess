package chessengine;

public class minimaxOutput {


    private final double advantage;

    private int outputDepth;

    public minimaxOutput(double advantage){
        this.advantage =advantage;
        this.outputDepth = 1;
    }

    public minimaxOutput incrementAndReturn(){
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
