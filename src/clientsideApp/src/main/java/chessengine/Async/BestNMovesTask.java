package chessengine.Async;

import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.Computer;
import chessengine.Computation.ComputerOutput;
import chessengine.Graphics.MainScreenController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BestNMovesTask extends Task<Void> {
    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;
    public volatile boolean isInvalidated;
    private final int nSuggestions;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;
    private final MainScreenController controller;
    private final Computer c;
    private final ExecutorService executor;
    private final Logger logger;
    private boolean isCurrentlyEvaluating = false;

    public BestNMovesTask(Computer c, MainScreenController controller, int nSuggestions) {
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.nSuggestions = nSuggestions;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;

    }

    public void evalRequest() {
        logger.info("Called Evaluation Request");
        if (!isCurrentlyEvaluating) {
            evaluationRequest = true;
        } else {
            stop();
            evaluationRequest = true;
        }


    }

    public void stop() {
        if (c.isRunning()) {
            c.stop.set(true);
        }
    }

    @Override
    public Void call() {
        while (running) {
            try {
                if (evaluationRequest) {
                    isInvalidated = false;
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;
                    logger.info("Starting a best n moves evaluation");
                    List<ComputerOutput> nmoves = c.getNMoves(currentIsWhite, currentPosition, currentGameState, nSuggestions);
                    if (nmoves != null && !nmoves.isEmpty()) {
                        // todo updating when should be stopped
                        djdjsk
                        Platform.runLater(() -> {
                            // Update UI elements on the JavaFX Application Thread
                            controller.getChessCentralControl().chessActionHandler.addBestMovesToViewer(nmoves);

                        });
                    } else {
                        logger.debug("Null best moves, likely stop flag");
                    }
                    isCurrentlyEvaluating = false;

                }

                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }


    public void endThread() {
        running = false;
        executor.shutdown();
    }
}
