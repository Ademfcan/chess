package chessengine;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetComputerMoveTask extends Task<Void> {
    private final Random random;
    private final mainScreenController controller;
    private final Computer c;
    private final ExecutorService executor;
    private final Logger logger;
    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;
    private boolean isCurrentlyEvaluating = false;


    public GetComputerMoveTask(Computer c, mainScreenController controller) {
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;
        this.random = new Random();

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
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;
                    getComputerMove();
                    isCurrentlyEvaluating = false;

                }

                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    private void getComputerMove() {
        logger.info("Starting a best move evaluation");

        // if not random move then now we need to customize how we get the move
        ChessMove bestMove = c.getComputerMove(currentIsWhite, currentPosition, currentGameState);
        if (bestMove != ChessConstants.emptyOutput.move) {
            Platform.runLater(() -> {
                controller.makeComputerMove(bestMove);
            });
        }


    }


    public void endThread() {
        running = false;
        executor.shutdown();
    }


}
