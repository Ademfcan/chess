package chessengine.Async;

import chessengine.*;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.Computer;
import chessengine.Functions.PgnFunctions;
import chessengine.Misc.ChessConstants;
import chessserver.ComputerDifficulty;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetComputerMoveTask extends Task<Void> {
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

    private SimulationTask simTaskForStockfish;


    public GetComputerMoveTask(Computer c, mainScreenController controller,SimulationTask simtaskForStockfish) {
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.executor = Executors.newFixedThreadPool(6);
        this.controller = controller;
        this.simTaskForStockfish = simtaskForStockfish;

    }

    public void evalRequest() {
        logger.info("Called Evaluation Request");
        if (isCurrentlyEvaluating) {
            stop();
        }
        evaluationRequest = true;


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
                    Thread.sleep(800);
                    getComputerMove();
                    isCurrentlyEvaluating = false;

                }

                Thread.sleep(100);
            } catch (Exception e) {
                logger.error("Error on get comp move task",e);
            }
        }
        return null;

    }


    private void getComputerMove() {
        logger.info("Starting a best move evaluation");
        // handle stockfish
        if(c.currentDifficulty.equals(ComputerDifficulty.STOCKFISHLOL)){
            logger.debug("Getting stockfish move");
            String moveUci = simTaskForStockfish.stockfish.getBestMove(PgnFunctions.positionToFEN(currentPosition,currentGameState,currentIsWhite), 3200,1000);

//            logger.debug("Stockfish uci: " + moveUci);
            ChessMove move = PgnFunctions.uciToChessMove(moveUci, currentIsWhite, currentPosition.board);

            Platform.runLater(()->{
                controller.makeComputerMove(move);

            });
        }
        else{
            // if not random move then now we need to customize how we get the move
            ChessMove bestMove = c.getComputerMove(currentIsWhite, currentPosition, currentGameState);
            if (bestMove != ChessConstants.emptyOutput.move) {
                Platform.runLater(()->{
                    controller.makeComputerMove(bestMove);

                });
            }
        }



    }


    public void endThread() {
        running = false;
        executor.shutdown();
    }


}
