package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EvaluationBarTask extends Task<Void> {
    private boolean running = true;
    private volatile boolean evaluationRequest = false;

    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;
    private Computer c;
    private mainScreenController controller;

    public void setDepth(int maxD) {
        this.maxD = maxD;
    }

    private int maxD;
    private ExecutorService executor;
    public EvaluationBarTask(Computer c, mainScreenController controller, int maxDepth){
        this.c = c;
        this.controller = controller;
        this.maxD = maxDepth;
        executor = Executors.newFixedThreadPool(10);
    }

    public void evalRequest(){
        // stop old minimax
        evaluationRequest = true;
        if(!isCurrentlyEvaluating){
            evaluationRequest = true;
        }
    }

    public void stop(){
        if(c.isRunning()){
            c.stop = true;
        }

    }
    private boolean isCurrentlyEvaluating = false;

    @Override
    public Void call(){
        while (running){
            try {
                if (evaluationRequest) {
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;
                    System.out.println("Evaluating");

                    for(int i = 1;i<maxD;i++){
                        EvaluationBarCallable evalCallable = new EvaluationBarCallable(c, currentPosition,currentGameState, i, currentIsWhite);
                        Future<Double> eval = executor.submit(evalCallable);
                        double evaluationOutput = eval.get(); // blocking call, consider timeout
                        int j = i;
                        // Update UI elements on the JavaFX Application Thread
                        if(evaluationOutput != c.Stopped){
                            // output might have been stopped and output will be useless
                            Platform.runLater(() -> {
                                controller.setEvalBar(evaluationOutput,j,true,false);
                            });
                        }

                    }
                    isCurrentlyEvaluating = false;


                }


                Thread.sleep(200);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;

    }


    public void endThread(){
        running = false;
        executor.shutdown();
    }




}
