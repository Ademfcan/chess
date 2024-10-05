package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.CustomMultiSearcher;
import chessengine.Computation.Searcher;
import chessengine.Functions.PgnFunctions;
import chessengine.Misc.ChessConstants;
import chessengine.Records.SearchResult;
import chessserver.ComputerDifficulty;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetComputerMoveTask extends Task<Void> {
    private final ChessCentralControl control;
    private final Logger logger;
    public ComputerDifficulty difficulty;
    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;
    private boolean isCurrentlyEvaluating = false;

    private final Searcher searcher;
    private final CustomMultiSearcher multiSearcher;


    public GetComputerMoveTask(ComputerDifficulty difficulty, ChessCentralControl control) {
        this.logger = LogManager.getLogger(this.toString());
        this.difficulty = difficulty;
        this.control = control;
        this.searcher = new Searcher();
        this.multiSearcher = new CustomMultiSearcher();

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
        return null;

    }


    private void makeComputerMove() {
        logger.info("Starting a best move evaluation");
        if (difficulty.isStockfishBased) {
            logger.debug("Getting stockfish move");
            String moveUci = App.getMoveStockfish.getBestMove(PgnFunctions.positionToFEN(currentPosition, currentGameState, currentIsWhite), difficulty.stockfishElo, ChessConstants.DefaultWaitTime);
            if (isCurrentlyEvaluating && moveUci != null) {
                Platform.runLater(() -> {
                    control.mainScreenController.makeComputerMove(PgnFunctions.uciToChessMove(moveUci, currentIsWhite, currentPosition.board));
                });
            }

        } else if (difficulty == ComputerDifficulty.MaxDifficulty) {
            SearchResult searchResult = searcher.search(currentPosition.toBackend(currentGameState, currentIsWhite), ChessConstants.DefaultWaitTime);
//            System.out.println(searchResult.evaluation());
//            System.out.println(searchResult.depth());
            if (isCurrentlyEvaluating && searchResult != null) {
                Platform.runLater(() -> {
                    control.mainScreenController.makeComputerMove(searchResult.move());
                });
            }
        } else {
            ChessMove bestMove = multiSearcher.search(currentPosition.toBackend(currentGameState, currentIsWhite), ChessConstants.DefaultWaitTime, 1, difficulty).results()[0].move();
            if (isCurrentlyEvaluating && bestMove != null) {
                Platform.runLater(() -> {
                    control.mainScreenController.makeComputerMove(bestMove);
                });
            }

        }


    }


    public void endThread() {
        running = false;
    }


}
