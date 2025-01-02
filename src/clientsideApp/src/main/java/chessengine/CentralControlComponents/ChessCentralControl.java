package chessengine.CentralControlComponents;

import chessengine.App;
import chessengine.Computation.MultiSearcher;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.MoveRanking;
import chessengine.Graphics.MainScreenController;
import chessserver.Misc.ChessConstants;
import chessengine.Records.CachedPv;
import chessengine.Records.MultiResult;
import chessengine.Records.SearchResult;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ChessCentralControl {
    private static final Logger logger = LogManager.getLogger("Chess_Central_Control_Logger");
    private final MultiSearcher searcher = new MultiSearcher();
    private final Set<Integer> currentlySearching = new HashSet<>();
//    public ChessboardMoveMaker chessboardMoveMaker;
    public MainScreenController mainScreenController;
    public ChessBoardGUIHandler chessBoardGUIHandler;
    public PuzzleGuiManager puzzleGuiManager;
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


    public void init(MainScreenController mainScreenController, Pane chessPieceBoard, HBox eatenWhites,
                     HBox eatenBlacks, ImageView[][] piecesAtLocations, TextArea gameInfo, Pane ArrowBoard,
                     VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextField chatInput,
                     Button sendMessageButton,HBox emojiContainer,Button resignButton,Button offerDrawButton, VBox[][] bgPanes,
                     VBox[][] moveBoxes, StackPane[][] highlightPanes, GridPane chessBgBoard, GridPane chessHighlightBoard,
                     GridPane chessMoveBoard, HBox movesPlayedBox,ScrollPane movesPlayedScrollpane, Label lineLabel,
                     Button playPauseButton,Slider timeSlider, VBox p1Indicator, VBox p2Indicator, Label p1moveClk,
                     Label p2moveClk, ComboBox<Integer> player1SimSelector, ComboBox<Integer> player2SimSelector,
                     TextArea currentGamePgn,Slider puzzleEloSlider,Label puzzleElo,Button nextPuzzleButton,VBox puzzleTagsBox) {
        this.mainScreenController = mainScreenController;
        this.chessBoardGUIHandler = new ChessBoardGUIHandler(this, chessPieceBoard, eatenWhites, eatenBlacks, piecesAtLocations, ArrowBoard, bgPanes, moveBoxes, highlightPanes, chessHighlightBoard, chessBgBoard, chessMoveBoard, localInfo);
        this.gameHandler = new ChessGameHandler(this);
        this.chessActionHandler = new ChessActionHandler(this, bestmovesBox, localInfo, sandboxPieces, gameInfo, chatInput, sendMessageButton,emojiContainer,resignButton,offerDrawButton, movesPlayedBox,movesPlayedScrollpane, lineLabel, playPauseButton,timeSlider, p1Indicator, p2Indicator, p1moveClk, p2moveClk, player1SimSelector, player2SimSelector, currentGamePgn);
        this.asyncController = new ThreadController(this);
        this.puzzleGuiManager = new PuzzleGuiManager(this,App.puzzleManager,puzzleEloSlider,puzzleElo,nextPuzzleButton,puzzleTagsBox);
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
        int maxMoveIndex = gameHandler.gameWrapper.getGame().getMaxIndex();
        int currentMoveIndex = gameHandler.gameWrapper.getGame().getCurMoveIndex();
        int bottomRange = Math.max(gameHandler.gameWrapper.getGame().getMinIndex(), currentMoveIndex - 1);
        int topRange = Math.min(currentMoveIndex + 1, maxMoveIndex);
        for (int i = bottomRange; i <= topRange; i++) {
            addSearchRequest(i);
        }
    }

    private void addSearchRequest(int i) {
        if (isInValidActiveGame() && !cachedResults.containsKey(i) && !currentlySearching.contains(i)) {
            currentlySearching.add(i);
            asyncController.generalTask.addTask(() -> {
                try {
                    MultiResult result = searcher.search(gameHandler.gameWrapper.getGame().getPos(i).clonePosition().toBackend(gameHandler.gameWrapper.getGame().getGameStateAtPos(i), gameHandler.gameWrapper.getGame().isWhiteTurn(i)), ChessConstants.DefaultWaitTime / 2, ChessConstants.NMOVES);
                    Platform.runLater(() -> {
                        cachedResults.put(i, result);
                        currentlySearching.remove(i);
                    });
                } catch (Exception e) {
                    logger.error("Search request exception!: ", e);
                }
            });

        }
    }

    public void getCentralEvaluation() {
        if (isInValidActiveGame()) {
            int currentIndex = gameHandler.gameWrapper.getGame().getCurMoveIndex();
            if (cachedResults.containsKey(currentIndex) && (currentIndex < 0 || cachedResults.containsKey(currentIndex - 1))) {
                setStateBasedOnResults(cachedResults.get(currentIndex), currentIndex < 0 ? null : cachedResults.get(currentIndex - 1));
            } else {
                addSearchRequest(currentIndex);
//                addSearchRequest(Math.max(currentIndex - 1, -1));
                asyncController.generalTask.addTask(() -> {
                    Platform.runLater(this::getCentralEvaluation);
                });
            }
        }


    }

    private void setStateBasedOnResults(MultiResult currentResults, MultiResult previousResults) {
        SearchResult primeResult = currentResults.results()[0];

        boolean isWhiteTurn = gameHandler.gameWrapper.getGame().isWhiteTurn(); // for relative evaluation
        if (MainScreenState.isEvalAllowed(mainScreenController.currentState)) {
            mainScreenController.setEvalBar((primeResult.evaluation() / (double) 100) * (isWhiteTurn ? 1 : -1), primeResult.depth(), false);
            // eval over time todo
        }

        if (mainScreenController.currentState == MainScreenState.VIEWER) {
            // todo
            chessActionHandler.addBestMovesToViewer(currentResults);
            if (gameHandler.gameWrapper.getGame().getCurMoveIndex() >= 0) {
                CachedPv pv = previousResults.moveValues().get(gameHandler.gameWrapper.getGame().getCurrentPosition().getMoveThatCreatedThis());
                MoveRanking ranking = MoveRanking.getMoveRanking(previousResults.results()[0].evaluation(), pv.evaluation(), previousResults.results()[0].pV(), pv.pV());
                chessBoardGUIHandler.addMoveRanking(gameHandler.gameWrapper.getGame().getCurrentPosition().getMoveThatCreatedThis(), ranking, gameHandler.gameWrapper.getGame().isWhiteOriented());
            }
        }

//        for(SearchPair linePv : primeResult.pV()){
//            if(linePv == null){
//                break;
//            }
//            chessBoardGUIHandler.addArrow(new MoveArrow(linePv.pvMove(),"Black"));
//        }
    }


    public boolean isInViewerActive() {
        return isInValidActiveGame() && mainScreenController.currentState == MainScreenState.VIEWER;
    }

    public boolean isInValidActiveGame(){
        return !App.isStartScreen && mainScreenController.currentState != MainScreenState.SIMULATION && gameHandler.currentlyGameActive() && !gameHandler.gameWrapper.getGame().getGameState().isGameOver();
    }


    public void tryPreloadCentralEvaluations() {
        if(gameHandler.currentlyGameActive()){
            for(int i = 0;i<gameHandler.gameWrapper.getGame().getMaxIndex();i++){
                addSearchRequest(i);
            }

        }
    }
}
