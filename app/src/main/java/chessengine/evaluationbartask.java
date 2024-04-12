package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class evaluationbartask extends Task<Void> {
    private boolean running = true;
    private volatile boolean evaluationRequest = false;

    public volatile long[] currentWhiteBoard;
    public volatile long[] currentBlackBoard;
    public volatile boolean currentIsWhite;
    private Computer c;
    private mainScreenController controller;

    private int maxD;
    private ExecutorService executor;
    public evaluationbartask(Computer c, mainScreenController controller, int maxDepth){
        this.c = c;
        this.controller = controller;
        this.maxD = maxDepth;
        executor = Executors.newFixedThreadPool(10);
    }

    public void evalRequest(){
        // stop old minimax
        c.stop = true;
        evaluationRequest = true;
    }
    @Override
    public Void call(){
        while (running){
            try {
                if (evaluationRequest) {
                    System.out.println("Evaluating");

                    for(int i = 2;i<maxD+1;i++){
                        DynamicEvaluationCallable evalCallable = new DynamicEvaluationCallable(c, currentWhiteBoard, currentBlackBoard, i, currentIsWhite);
                        Future<Double> eval = executor.submit(evalCallable);
                        double evaluationOutput = eval.get(); // blocking call, consider timeout
                        int j = i;
                        // Update UI elements on the JavaFX Application Thread
                        if(evaluationOutput != c.Stopped){
                            // output might have been stopped and output will be useless
                            Platform.runLater(() -> {
                                controller.setEvalBar(evaluationOutput,j);
                            });
                        }

                    }


                    evaluationRequest = false;
                }


                Thread.sleep(200);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;

    }


    public void stopThread(){
        running = false;
        executor.shutdown();
    }




}
