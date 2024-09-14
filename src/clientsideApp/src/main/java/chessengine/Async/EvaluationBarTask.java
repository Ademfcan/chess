package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Computation.Computer;
import chessengine.Computation.EvalOutput;
import chessengine.Misc.ChessConstants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.Queue;
import java.util.concurrent.*;

public class EvaluationBarTask extends Task<Void> {

    private final Logger logger = LogManager.getLogger(this.toString());
    private volatile boolean evalRequest = false;
    private volatile boolean endEval = false;

    private final Computer c;
    private final ChessCentralControl myControl;

    private volatile boolean running = true;
    public EvaluationBarTask(Computer c, ChessCentralControl myControl) {
        this.c = c;
        this.myControl = myControl;

    }

    private final int iterCount = 6;

    private int lastIndex = ChessConstants.EMPTYINDEX;

    public void evalRequest(){
        stop();
        evalRequest = true;
    }


    public void stop() {
        if(c.isRunning()){
            c.stop.set(true);
        }
        if(App.stockfishForNmoves.isCalling()){
            App.stockfishForNmoves.stop.set(true);
        }

        endEval = true;
    }

    @Override
    public Void call() {
        while (running) {
            if(evalRequest){
                evalRequest = false;
                endEval = false;
                c.clearFlags();
                if(App.userPreferenceManager.getEvalStockfishBased()){
                    for(int i = 1;i<=iterCount;i++){
                        if(endEval || myControl.gameHandler.currentGame == null){
                            break;
                        }
                        lastIndex = myControl.gameHandler.currentGame.curMoveIndex;
                        EvalOutput output = App.stockfishForNmoves.getEvalScore(myControl.gameHandler.currentGame.getCurrentFen(),myControl.gameHandler.currentGame.isWhiteTurn(),(ChessConstants.DefaultWaitTime/iterCount)*i);
                        if(lastIndex == myControl.gameHandler.currentGame.curMoveIndex && output != null && !App.isStartScreen && !endEval) {
                            Platform.runLater(()-> myControl.mainScreenController.setEvalBar(output.getAdvantage(),output.getOutputDepth(),false));
                        }
                    }
                }
                else{
                    for(int i = ChessConstants.evalDepth-iterCount+1;i<=ChessConstants.evalDepth;i++){
                        if(endEval || myControl.gameHandler.currentGame == null){
                            break;
                        }
                        lastIndex = myControl.gameHandler.currentGame.curMoveIndex;
                        EvalOutput output = c.getFullEvalMinimax(myControl.gameHandler.currentGame.currentPosition,myControl.gameHandler.currentGame.gameState,i,myControl.gameHandler.currentGame.isWhiteTurn());
                        if(lastIndex == myControl.gameHandler.currentGame.curMoveIndex && output != Computer.Stopped && !App.isStartScreen && !endEval) {
                            Platform.runLater(()-> myControl.mainScreenController.setEvalBar(output.getAdvantage(),output.getOutputDepth(),false));
                        }
                    }
                }



            }
            try {
                Thread.sleep(100);

            }
            catch (Exception e){
                logger.error("Error best eval task sleep:",e);
            }
        }
        logger.debug("Eval callable ending");
        return null;

    }


    public void endThread() {
        running = false;
        stop();
    }


}
