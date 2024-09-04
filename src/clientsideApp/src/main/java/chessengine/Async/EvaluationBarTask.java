package chessengine.Async;

import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.Computer;
import chessengine.Computation.MinimaxEvalOutput;
import chessengine.mainScreenController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EvaluationBarTask extends Task<Void> {
    private final Computer c;
    private final mainScreenController controller;
    private final ExecutorService executor;
    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;
    private int maxD;
    private boolean isCurrentlyEvaluating = false;

    private static final Logger logger = LogManager.getLogger("Eval_Task");

    public EvaluationBarTask(Computer c, mainScreenController controller, int maxDepth) {
        this.c = c;
        this.controller = controller;
        this.maxD = maxDepth;
        executor = Executors.newFixedThreadPool(4);
    }

    public void setDepth(int maxD) {
        this.maxD = maxD;
        c.setEvalDepth(maxD);
    }

    public void evalRequest() {
        // stop old minimax
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
        while (running && !executor.isShutdown()) {
            try {
                if (evaluationRequest) {
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;

                    for (int i = Math.max(maxD-2,1); i < maxD; i++) {
                        EvaluationBarCallable evalCallable = new EvaluationBarCallable(c, currentPosition, currentGameState, i, currentIsWhite);
                        Future<MinimaxEvalOutput> evalOut = executor.submit(evalCallable);
                        MinimaxEvalOutput evaluationOutput = evalOut.get(); // blocking call, consider timeout
                        // Update UI elements on the JavaFX Application Thread
                        if (evaluationOutput != Computer.Stopped) {
                            // output might have been stopped and output will be useless
                            Platform.runLater(() -> {
                                controller.setEvalBar(evaluationOutput.getAdvantage(), evaluationOutput.getOutputDepth(), false);
                            });
                        }

                    }
                    isCurrentlyEvaluating = false;


                }


                Thread.sleep(50);
            } catch (Exception e) {
                logger.error("Error on eval call",e);
            }
        }
        logger.debug("Eval callable ending");
        return null;

    }


    public void endThread() {
        running = false;
        executor.shutdown();
    }


}
