package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class chessComputerTask extends Task<Void> {
    private boolean running = true;
    private volatile boolean evaluationRequest = false;

    public volatile long[] currentWhiteBoard;
    public volatile long[] currentBlackBoard;
    public volatile boolean currentIsWhite;

    private mainScreenController controller;

    private Computer c;

    private ExecutorService executor;
    public chessComputerTask(Computer c, mainScreenController controller){
        this.c = c;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;
    }

    public void evalRequest(){
        // stop old minimax
        if(!evaluationRequest){
            evaluationRequest = true;
        }
    }
    @Override
    public Void call(){
        while (running){
            try {
                if (evaluationRequest) {
                    System.out.println("Evaluating");


                    BestMoveCallable evalCallable = new BestMoveCallable(c, currentIsWhite,currentWhiteBoard, currentBlackBoard);
                    Future<chessMove> eval = executor.submit(evalCallable);
                    controller.makeComputerMove(eval.get()); // blocking call, consider timeout
                    // Update UI elements on the JavaFX Application Thread
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
