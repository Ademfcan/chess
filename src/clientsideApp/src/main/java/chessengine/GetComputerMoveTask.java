package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetComputerMoveTask extends Task<Void> {
    private boolean running = true;
    private volatile boolean evaluationRequest = false;

    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;

    private mainScreenController controller;

    private Computer c;

    private ExecutorService executor;

    private Logger logger;
    public GetComputerMoveTask(Computer c, mainScreenController controller){
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;

    }


    public void evalRequest(){
        logger.info("Called Evaluation Request");
        if(!isCurrentlyEvaluating){
            evaluationRequest = true;
        }
        else{
            stop();
            evaluationRequest = true;
        }


    }

    public void stop(){
        if(c.isRunning()){
            c.stop.set(true);
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
                    logger.info("Starting a best move evaluation");
//                  BestMoveCallable evalCallable = new BestMoveCallable(c, currentIsWhite,currentWhiteBoard, currentBlackBoard);
//                  Future<chessMove> eval = executor.submit(evalCallable);
                    ChessMove bestMove = c.getComputerMove(currentIsWhite, currentPosition,currentGameState);
                    if(bestMove != ChessConstants.emptyOutput.move){
                        Platform.runLater(()->{
                            controller.makeComputerMove(bestMove); // blocking call, consider timeout
                        });
                    }

                    isCurrentlyEvaluating = false;

                }

                Thread.sleep(100);
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
