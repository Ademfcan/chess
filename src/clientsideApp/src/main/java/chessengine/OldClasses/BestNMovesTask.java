//package chessengine.Async;
//
//import chessengine.App;
//import chessengine.CentralControlComponents.ChessCentralControl;
//import chessengine.ChessRepresentations.ChessGame;
//import chessengine.ChessRepresentations.ChessPosition;
//import chessengine.ChessRepresentations.ChessStates;
//import chessengine.Computation.*;
//import chessengine.Misc.ChessConstants;
//import javafx.application.Platform;
//import javafx.concurrent.Task;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.tools.picocli.CommandLine;
//
//import java.util.Arrays;
//import java.util.Queue;
//import java.util.concurrent.*;
//
//public class BestNMovesTask extends Task<Void> {
//
//    private final Logger logger = LogManager.getLogger(this.toString());
//    private volatile boolean evalRequest = false;
//    private volatile boolean endEval = false;
//
//    private final Computer c;
//    private final ChessCentralControl myControl;
//
//    private volatile boolean running = true;
//
//    private Searcher searcher;
//    public BestNMovesTask(Computer c, ChessCentralControl myControl) {
//        this.c = c;
//        this.myControl = myControl;
//        this.searcher = new Searcher();
//
//    }
//
//    private final int iterCount = 6;
//
//    private volatile int lastIndex = ChessConstants.EMPTYINDEX;
//
//    public void evalRequest(){
//        stop();
//        evalRequest = true;
//    }
//
//
//    public void stop() {
//        if(c.isRunning()){
//            c.stop.set(true);
//        }
//        if(App.stockfishForEval.isCalling()){
//            App.stockfishForEval.stop.set(true);
//        }
//
//        endEval = true;
//    }
//
//    @Override
//    public Void call() {
//        while (running) {
////            System.out.println("NMoves");
////            System.out.println(running);
//
//            if(evalRequest){
//                evalRequest = false;
//                endEval = false;
//                c.clearFlags();
//                if(App.userPreferenceManager.getNMovesStockfishBased()){
//                    if(!endEval && myControl.gameHandler.currentGame != null){
//                        lastIndex = myControl.gameHandler.currentGame.curMoveIndex;
//                        MoveOutput[] moveOutputs = App.stockfishForEval.getBestNMoves(myControl.gameHandler.currentGame.getCurrentFen(),myControl.gameHandler.currentGame.isWhiteTurn(),myControl.gameHandler.currentGame.currentPosition.board, ChessConstants.DefaultWaitTime,ChessConstants.NMOVES);
//                        if(lastIndex == myControl.gameHandler.currentGame.curMoveIndex && moveOutputs != null && !App.isStartScreen && !endEval) {
//                            Platform.runLater(()-> myControl.chessActionHandler.addBestMovesToViewer(moveOutputs));
//                        }
//                    }
//                }
//                else{
//                    if(!endEval && myControl.gameHandler.currentGame != null){
//                        lastIndex = myControl.gameHandler.currentGame.curMoveIndex;
//                        Result[] moveOutputs = searcher.search(myControl.gameHandler.currentGame.currentPosition.toBackend(myControl.gameHandler.currentGame.gameState, myControl.gameHandler.currentGame.isWhiteTurn()),ChessConstants.DefaultWaitTime,ChessConstants.NMOVES).results();
//                        System.out.println(Arrays.toString(moveOutputs));
//                        if(lastIndex == myControl.gameHandler.currentGame.curMoveIndex && moveOutputs != null && !App.isStartScreen && !endEval) {
//                            Platform.runLater(()-> myControl.chessActionHandler.addBestMovesToViewer(moveOutputs));
//                        }
//                    }
//
//                }
//
//
//
//            }
//
//            try {
//                Thread.sleep(100);
//
//            }
//            catch (Exception e){
//                logger.error("Error best n moves task sleep:",e);
//            }
//
//
//        }
//        logger.debug("Eval callable ending");
//        return null;
//
//    }
//
//
//    public void endThread() {
//        running = false;
//        stop();
//    }
//
//
//}
