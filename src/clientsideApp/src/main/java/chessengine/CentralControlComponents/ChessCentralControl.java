package chessengine.CentralControlComponents;

import chessengine.CentralControlComponents.ChessActionHandler;
import chessengine.CentralControlComponents.ChessBoardGUIHandler;
import chessengine.CentralControlComponents.ChessGameHandler;
import chessengine.CentralControlComponents.ThreadController;
import chessengine.mainScreenController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;


public class ChessCentralControl {

    public chessengine.mainScreenController mainScreenController;
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

    public void init(mainScreenController mainScreenController, Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, TextArea gameInfo, Pane ArrowBoard, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextField chatInput, Button sendMessageButton, VBox[][] bgPanes, VBox[][] moveBoxes, StackPane[][] highlightPanes, GridPane chessBgBoard, GridPane chessHighlightBoard, GridPane chessMoveBoard, HBox movesPlayedBox, Button playPauseButton, VBox p1Indicator, VBox p2Indicator, Label p1moveClk, Label p2moveClk) {
        this.mainScreenController = mainScreenController;
        this.chessBoardGUIHandler = new ChessBoardGUIHandler(chessPieceBoard, eatenWhites, eatenBlacks, piecesAtLocations, ArrowBoard, bgPanes, moveBoxes, highlightPanes, chessHighlightBoard, chessBgBoard, chessMoveBoard);
        this.gameHandler = new ChessGameHandler(this);
        this.chessActionHandler = new ChessActionHandler(this, bestmovesBox, localInfo, sandboxPieces, gameInfo, chatInput, sendMessageButton, movesPlayedBox, playPauseButton, p1Indicator, p2Indicator, p1moveClk, p2moveClk);
        this.asyncController = new ThreadController(5, 5, this);
//        this.chessboardMoveMaker = new ChessboardMoveMaker(chessBoardGUIHandler);
        isInit = true;

    }
}
