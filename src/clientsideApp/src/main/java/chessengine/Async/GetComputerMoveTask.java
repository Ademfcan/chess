package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
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
        logger.debug("Computer task ending");
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
