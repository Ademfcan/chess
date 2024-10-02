//package chessengine.Async;
//
//import chessengine.App;
//import chessengine.CentralControlComponents.ChessCentralControl;
//import chessengine.Computation.Computer;
//import chessengine.Computation.EvalOutput;
//import chessengine.Records.SearchResult;
//import chessengine.Computation.Searcher;
//import chessengine.Misc.ChessConstants;
//import javafx.application.Platform;
//import javafx.concurrent.Task;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public class EvaluationBarTask extends Task<Void> {
//
//    private final Logger logger = LogManager.getLogger(this.toString());
//    private final Computer c;
//    private final ChessCentralControl myControl;
//    private final int iterCount = 6;
//    private volatile boolean evalRequest = false;
//    private volatile boolean endEval = false;
//    private volatile boolean running = true;
//    private int lastIndex = ChessConstants.EMPTYINDEX;
//
//    private final Searcher searcher = new Searcher();
//    public EvaluationBarTask(Computer c, ChessCentralControl myControl) {
//        this.c = c;
//        this.myControl = myControl;
//
//    }
//
//    public void evalRequest() {
//        stop();
//        evalRequest = true;
//    }
//
//
//    public void stop() {
//        if (c.isRunning()) {
//            c.stop.set(true);
//        }
//        if (App.stockfishForNmoves.isCalling()) {
//            App.stockfishForNmoves.stop.set(true);
//        }
//
//        endEval = true;
//    }
//
//    @Override
//    public Void call() {
//        while (running) {
//            if (evalRequest) {
//                evalRequest = false;
//                endEval = false;
//                c.clearFlags();
//                if (App.userPreferenceManager.getEvalStockfishBased()) {
//                    for (int i = 1; i <= iterCount; i++) {
//                        if (endEval || myControl.gameHandler.currentGame == null) {
//                            break;
//                        }
//                        lastIndex = myControl.gameHandler.currentGame.curMoveIndex;
//                        EvalOutput output = App.stockfishForNmoves.getEvalScore(myControl.gameHandler.currentGame.getCurrentFen(), myControl.gameHandler.currentGame.isWhiteTurn(), (ChessConstants.DefaultWaitTime / iterCount) * i);
//                        if (output != null && lastIndex == myControl.gameHandler.currentGame.curMoveIndex && !App.isStartScreen && !endEval) {
//                            Platform.runLater(() -> myControl.mainScreenController.setEvalBar(output.getAdvantage(), output.getOutputDepth(), false));
//                        }
//                    }
//                } else {
//                    for (int i = ChessConstants.evalDepth - iterCount + 1; i <= ChessConstants.evalDepth; i++) {
//                        if (endEval || myControl.gameHandler.currentGame == null) {
//                            break;
//                        }
//                        lastIndex = myControl.gameHandler.currentGame.curMoveIndex;
//                        SearchResult output = searcher.search(myControl.gameHandler.currentGame.currentPosition.toBackend(myControl.gameHandler.currentGame.gameState, myControl.gameHandler.currentGame.isWhiteTurn()),(ChessConstants.DefaultWaitTime/iterCount)*i,1);
//                        if (output.results()[0].evaluation() != Integer.MIN_VALUE+1 && lastIndex == myControl.gameHandler.currentGame.curMoveIndex && !App.isStartScreen && !endEval) {
//                            Platform.runLater(() -> myControl.mainScreenController.setEvalBar(((double) output.results()[0].evaluation() /100)*(myControl.gameHandler.currentGame.isWhiteTurn() ? 1 : -1), output.depth(), false));
//                        }
//                    }
//                }
//
//
//            }
//            try {
//                Thread.sleep(100);
//
//            } catch (Exception e) {
//                logger.error("Error best eval task sleep:", e);
//            }
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
