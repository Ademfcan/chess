package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private Logger logger;
    public chessComputerTask(Computer c, mainScreenController controller){
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;

    }

    public void updateBoard(long[] newWhites, long[] newBlacks,boolean isWhite){
        logger.info("Updating board");
        this.currentWhiteBoard = newWhites;
        this.currentBlackBoard = newBlacks;
        this.currentIsWhite = isWhite;
    }

    public void evalRequest(){
        logger.info("Called Evaluation Request");
        if(!isCurrentlyEvaluating){
            evaluationRequest = true;
        }


    }
    private boolean isCurrentlyEvaluating = false;
    @Override
    public Void call(){
        while (running){
            try {
                if (evaluationRequest) {
                    isCurrentlyEvaluating = true;
                    logger.info("Starting a best move evaluation");
//                  BestMoveCallable evalCallable = new BestMoveCallable(c, currentIsWhite,currentWhiteBoard, currentBlackBoard);
//                  Future<chessMove> eval = executor.submit(evalCallable);
                    controller.makeComputerMove(c.getComputerMove(currentIsWhite,currentWhiteBoard,currentBlackBoard)); // blocking call, consider timeout
                    // Update UI elements on the JavaFX Application Thread
                    evaluationRequest = false;
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


    public void stopThread(){
        running = false;
        executor.shutdown();
    }




}
