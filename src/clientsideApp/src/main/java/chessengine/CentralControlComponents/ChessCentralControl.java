package chessengine.CentralControlComponents;

import chessengine.App;
import chessengine.Computation.MultiSearcher;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.MoveRanking;
import chessengine.Graphics.MainScreenController;
import chessengine.Misc.ChessConstants;
import chessengine.Records.CachedPv;
import chessengine.Records.MultiResult;
import chessengine.Records.SearchResult;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ChessCentralControl {

    private final MultiSearcher searcher = new MultiSearcher();
    private final Set<Integer> currentlySearching = new HashSet<>();
//    public ChessboardMoveMaker chessboardMoveMaker;
    public MainScreenController mainScreenController;
    public ChessBoardGUIHandler chessBoardGUIHandler;
    public ThreadController asyncController;
    public ChessGameHandler gameHandler;
    public ChessActionHandler chessActionHandler;
    public HashMap<Integer, MultiResult> cachedResults;
    private boolean isInit = false;

    public ChessCentralControl() {


    }

    public boolean isInit() {
        return this.isInit;
    }


    public void init(MainScreenController mainScreenController, Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, TextArea gameInfo, Pane ArrowBoard, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextField chatInput, Button sendMessageButton, VBox[][] bgPanes, VBox[][] moveBoxes, StackPane[][] highlightPanes, GridPane chessBgBoard, GridPane chessHighlightBoard, GridPane chessMoveBoard, HBox movesPlayedBox, Label lineLabel, Button playPauseButton, VBox p1Indicator, VBox p2Indicator, Label p1moveClk, Label p2moveClk, ComboBox<String> player1SimSelector, ComboBox<String> player2SimSelector, TextArea currentGamePgn) {
        this.mainScreenController = mainScreenController;
        this.chessBoardGUIHandler = new ChessBoardGUIHandler(this, chessPieceBoard, eatenWhites, eatenBlacks, piecesAtLocations, ArrowBoard, bgPanes, moveBoxes, highlightPanes, chessHighlightBoard, chessBgBoard, chessMoveBoard, localInfo);
        this.gameHandler = new ChessGameHandler(this);
        this.chessActionHandler = new ChessActionHandler(this, bestmovesBox, localInfo, sandboxPieces, gameInfo, chatInput, sendMessageButton, movesPlayedBox, lineLabel, playPauseButton, p1Indicator, p2Indicator, p1moveClk, p2moveClk, player1SimSelector, player2SimSelector, currentGamePgn);
        this.asyncController = new ThreadController(this);
        this.cachedResults = new HashMap<>();
        isInit = true;

    }

    public void clearForNewGame() {
        cachedResults.clear();
    }

    public void clearForNewBranch(int branchIndex) {
        Iterator<Integer> keySetIterator = cachedResults.keySet().iterator();
        Integer key;
        while (keySetIterator.hasNext()) {
            key = keySetIterator.next();
            if (key >= branchIndex) {
                keySetIterator.remove();
            }
        }
    }


    public void checkCacheNewIndex() {
        int maxMoveIndex = gameHandler.currentGame.maxIndex;
        int currentMoveIndex = gameHandler.currentGame.curMoveIndex;
        int bottomRange = Math.max(-1, currentMoveIndex - 1);
        int topRange = Math.min(currentMoveIndex + 1, maxMoveIndex);
        for (int i = bottomRange; i <= topRange; i++) {
            addSearchRequest(i);
        }
    }

    private void addSearchRequest(int i) {
        if (!cachedResults.containsKey(i) && !currentlySearching.contains(i)) {
            currentlySearching.add(i);
            asyncController.generalTask.addTask(() -> {
                try {
                    MultiResult result = searcher.search(gameHandler.currentGame.getPos(i).clonePosition().toBackend(gameHandler.currentGame.getGameStateAtPos(i), gameHandler.currentGame.isWhiteTurn(i)), ChessConstants.DefaultWaitTime / 2, ChessConstants.NMOVES);
                    Platform.runLater(() -> {
                        cachedResults.put(i, result);
                        currentlySearching.remove(i);
                    });
                } catch (Exception e) {
                    ChessConstants.mainLogger.error("Search request exception!: ", e);
                }
            });

        }
    }

    public void getCentralEvaluation() {
        if (!App.isStartScreen && gameHandler.currentGame != null) {
            int currentIndex = gameHandler.currentGame.curMoveIndex;
            if (cachedResults.containsKey(currentIndex) && (currentIndex < 0 || cachedResults.containsKey(currentIndex - 1))) {
                setStateBasedOnResults(cachedResults.get(currentIndex), currentIndex < 0 ? null : cachedResults.get(currentIndex - 1));
            } else {
                addSearchRequest(currentIndex);
                addSearchRequest(Math.max(currentIndex - 1, -1));
                asyncController.generalTask.addTask(() -> {
                    Platform.runLater(this::getCentralEvaluation);
                });
            }
        }


    }

    private void setStateBasedOnResults(MultiResult currentResults, MultiResult previousResults) {
        SearchResult primeResult = currentResults.results()[0];

        boolean isWhiteTurn = gameHandler.currentGame.isWhiteTurn(); // for relative evaluation
        if (mainScreenController.isEvalAllowed(mainScreenController.currentState)) {
            mainScreenController.setEvalBar((primeResult.evaluation() / (double) 100) * (isWhiteTurn ? 1 : -1), primeResult.depth(), false);
            // eval over time todo
        }

        if (mainScreenController.currentState.equals(MainScreenState.VIEWER)) {
            // todo
            chessActionHandler.addBestMovesToViewer(currentResults);
            if (gameHandler.currentGame.curMoveIndex >= 0) {
                CachedPv pv = previousResults.moveValues().get(gameHandler.currentGame.currentPosition.getMoveThatCreatedThis());
                MoveRanking ranking = MoveRanking.getMoveRanking(previousResults.results()[0].evaluation(), pv.evaluation(), previousResults.results()[0].pV(), pv.pV());
                chessBoardGUIHandler.addMoveRanking(gameHandler.currentGame.currentPosition.getMoveThatCreatedThis(), ranking, gameHandler.currentGame.isWhiteOriented());
            }
        }

//        for(SearchPair linePv : primeResult.pV()){
//            if(linePv == null){
//                break;
//            }
//            chessBoardGUIHandler.addArrow(new MoveArrow(linePv.pvMove(),"Black"));
//        }
    }


    public boolean isInViewerMove() {
        return !App.isStartScreen && mainScreenController.currentState.equals(MainScreenState.VIEWER) && gameHandler.currentGame != null;
    }


}
