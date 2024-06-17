package chessengine;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;


public class ChessCentralControl {
    public ChessBoardGUIHandler chessBoardGUIHandler;

    public ThreadController asyncController;


    private boolean isGuiHandlerInitialized = false;

    public ChessGameHandler gameHandler;
    public chessActionHandler chessActionHandler;


    public ChessCentralControl(){


    }

    public void init(Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, TextArea gameInfo, AnchorPane ArrowBoard, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextField chatInput, Button sendMessageButton, StackPane[][] bgPanes,GridPane chessHighlightBoard){
        this.chessBoardGUIHandler = new ChessBoardGUIHandler(chessPieceBoard,eatenWhites,eatenBlacks,piecesAtLocations,ArrowBoard,bgPanes,chessHighlightBoard);
        this.asyncController = new ThreadController(5,5);
        this.gameHandler = new ChessGameHandler(this);
        this.chessActionHandler = new chessActionHandler(this,bestmovesBox,localInfo,sandboxPieces,gameInfo,chatInput,sendMessageButton);


    }
}
