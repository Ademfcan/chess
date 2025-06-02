package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.Computation.Computer;
import chessserver.ChessRepresentations.ChessMove;
import chessserver.ChessRepresentations.ChessPosition;
import chessserver.ChessRepresentations.ChessGameState;
import chessengine.Computation.CustomMultiSearcher;
import chessengine.Computation.Searcher;
import chessserver.Functions.PgnFunctions;
import chessserver.Misc.ChessConstants;
import chessengine.Records.SearchResult;
import chessserver.Enums.ComputerDifficulty;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetComputerMoveTask extends Task<Void> {
    private final ChessCentralControl control;
    private final Logger logger;
    public ComputerDifficulty difficulty;
    public volatile ChessPosition currentPosition;
    public volatile ChessGameState currentGameState;
    public volatile boolean currentIsWhite;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;
    private boolean isCurrentlyEvaluating = false;

    private final Computer computer;


    public GetComputerMoveTask(ComputerDifficulty difficulty, ChessCentralControl control) {
        this.logger = LogManager.getLogger(this.toString());
        this.difficulty = difficulty;
        this.control = control;

        this.computer = new Computer(App.getMoveStockfish);

    }

    public void evaluationRequest() {
        logger.info("Called Evaluation Request");
        if (isCurrentlyEvaluating) {
            stop();
        }
        evaluationRequest = true;


    }

    public void stop() {
        isCurrentlyEvaluating = false;
    }

    @Override
    public Void call() {
        while (running) {
//            System.out.println("comp move");
            try {
                if (evaluationRequest) {
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;
                    makeComputerMove();
                    isCurrentlyEvaluating = false;

                }
                Thread.sleep(50);
            } catch (Exception e) {
                logger.error("Error on get comp move task", e);
            }
        }
        logger.debug("Computer task ending");
        return null;

    }


    private void makeComputerMove() {
        logger.info("Starting a best move evaluation");

        ChessMove move = computer.getMove(difficulty, currentPosition.toBackend(currentGameState, currentIsWhite));
        if (isCurrentlyEvaluating && move != null) {
            Platform.runLater(() -> {
                control.mainScreenController.makeComputerMove(move);
            });
        }

    }


    public void endThread() {
        running = false;
    }


}
