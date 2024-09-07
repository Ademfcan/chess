package chessengine.CentralControlComponents;

import chessengine.Graphics.MainScreenController;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;


public class ChessCentralControl {

    public MainScreenController mainScreenController;
    public ChessBoardGUIHandler chessBoardGUIHandler;
//    public ChessboardMoveMaker chessboardMoveMaker;

    public ThreadController asyncController;
    public ChessGameHandler gameHandler;
    public ChessActionHandler chessActionHandler;
    private boolean isInit = false;
    public ChessCentralControl() {


    }

    public boolean isInit() {
        return this.isInit;
    }


    public void init(MainScreenController mainScreenController, Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, TextArea gameInfo, Pane ArrowBoard, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextField chatInput, Button sendMessageButton, VBox[][] bgPanes, VBox[][] moveBoxes, StackPane[][] highlightPanes, GridPane chessBgBoard, GridPane chessHighlightBoard, GridPane chessMoveBoard, HBox movesPlayedBox, Label lineLabel, Button playPauseButton, VBox p1Indicator, VBox p2Indicator, Label p1moveClk, Label p2moveClk, ComboBox<String> player1SimSelector,ComboBox<String> player2SimSelector) {
        this.mainScreenController = mainScreenController;
        this.chessBoardGUIHandler = new ChessBoardGUIHandler(chessPieceBoard, eatenWhites, eatenBlacks, piecesAtLocations, ArrowBoard, bgPanes, moveBoxes, highlightPanes, chessHighlightBoard, chessBgBoard, chessMoveBoard,localInfo);
        this.gameHandler = new ChessGameHandler(this);
        this.chessActionHandler = new ChessActionHandler(this, bestmovesBox, localInfo, sandboxPieces, gameInfo, chatInput, sendMessageButton, movesPlayedBox,lineLabel, playPauseButton, p1Indicator, p2Indicator, p1moveClk, p2moveClk,player1SimSelector,player2SimSelector);
        this.asyncController = new ThreadController(5, 5, this);
        isInit = true;

    }
}
