package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.Computer;
import chessengine.Computation.MoveOutput;
import chessengine.Functions.PgnFunctions;
import chessengine.Misc.ChessConstants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetComputerMoveTask extends Task<Void> {
    private final ChessCentralControl control;
    private final Computer c;
    private final Logger logger;
    public volatile ChessPosition currentPosition;
    public volatile ChessStates currentGameState;
    public volatile boolean currentIsWhite;
    private boolean running = true;
    private volatile boolean evaluationRequest = false;
    private boolean isCurrentlyEvaluating = false;



    public GetComputerMoveTask(Computer c, ChessCentralControl control) {
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
        this.control = control;

    }

    public void evaluationRequest() {
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
//            System.out.println("comp move");
            try {
                if (evaluationRequest) {
                    c.clearFlags();
                    evaluationRequest = false;
                    isCurrentlyEvaluating = true;
                    makeComputerMove();
                    isCurrentlyEvaluating = false;

                }
                Thread.sleep(50);
            } catch (Exception e) {
                logger.error("Error on get comp move task",e);
            }
        }
        return null;

    }


    private void makeComputerMove() {
        logger.info("Starting a best move evaluation");
        if(c.currentDifficulty.isStockfishBased){
            logger.debug("Getting stockfish move");
            String moveUci = App.stockfishForNmoves.getBestMove(PgnFunctions.positionToFEN(currentPosition,currentGameState,currentIsWhite), c.currentDifficulty.stockfishElo, ChessConstants.DefaultWaitTime);
            if(moveUci != null){
                Platform.runLater(()->{
                    control.mainScreenController.makeComputerMove(PgnFunctions.uciToChessMove(moveUci, currentIsWhite, currentPosition.board));
                });
            }

        }
        else{
            MoveOutput bestMove =  c.getComputerMoveWithFlavors(currentIsWhite, currentPosition, currentGameState);
            if (bestMove != null) {
                Platform.runLater(()->{
                    control.mainScreenController.makeComputerMove(bestMove.getMove());
                });
            }

        }



    }


    public void endThread() {
        running = false;
    }


}
