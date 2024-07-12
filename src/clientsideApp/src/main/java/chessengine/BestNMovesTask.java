package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BestNMovesTask extends Task<Void> {
    private int nSuggestions;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;

    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;

    private mainScreenController controller;

    private Computer c;

    private ExecutorService executor;

    private Logger logger;
    public BestNMovesTask(Computer c, mainScreenController controller,int nSuggestions){
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.nSuggestions = nSuggestions;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;

    }


    public void evalRequest(){
        logger.info("Called Evaluation Request");
        if(!isCurrentlyEvaluating){
            evaluationRequest = true;
        }
        else{
            c.stop.set(true);
            evaluationRequest = true;
        }


    }

    public void stop(){
        if(c.isRunning()){
            c.stop.set(true);
        }
    }
    private boolean isCurrentlyEvaluating = false;

    public volatile boolean isInvalidated;
    @Override
    public Void call(){
        while (running){
            try {
                if (evaluationRequest) {
                    isInvalidated = false;
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;
                    logger.info("Starting a best n moves evaluation");
                    List<ComputerOutput> nmoves = c.getNMoves(currentIsWhite, currentPosition,currentGameState,nSuggestions);
                    if(nmoves != null && !nmoves.isEmpty()){
                        Platform.runLater(()->{
                            // Update UI elements on the JavaFX Application Thread
                            controller.getChessCentralControl().chessActionHandler.addBestMovesToViewer(nmoves);

                        });
                    }
                    else{
                        System.out.println("null best moves");
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
