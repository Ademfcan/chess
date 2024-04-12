package chessengine;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class chessBoardGUIHandler {

    public static ImageView createNewPeice(int brdIndex, boolean isWhite, GridPane chessPeiceBoard, boolean isEaten){
        String restOfPath ="";
        String pathStart = isWhite ? "w_" : "b_";
        switch (brdIndex) {
            case 0 -> restOfPath = "pawn";
            case 1 -> restOfPath = "knight";
            case 2 -> restOfPath = "bishop";
            case 3 -> restOfPath = "rook";
            case 4 -> restOfPath = "queen";
            case 5 -> restOfPath = "king";
        }
        ImageView piece = new ImageView("/ChessAssets/ChessPieces/" + pathStart + restOfPath + "_1x_ns.png");

        piece.fitHeightProperty().bind(chessPeiceBoard.heightProperty().divide(isEaten ? 16 : 8.5));
        piece.fitWidthProperty().bind(chessPeiceBoard.widthProperty().divide(isEaten ? 16 : 8.5));
        piece.setPreserveRatio(true);


        GridPane.setHalignment(piece, HPos.CENTER);
        GridPane.setValignment(piece, VPos.CENTER);
        return piece;
    }



    public static String createPeicePath(int brdIndex, boolean isWhite){
        String restOfPath ="";
        String pathStart = isWhite ? "w_" : "b_";
        switch (brdIndex) {
            case 0 -> restOfPath = "pawn";
            case 1 -> restOfPath = "knight";
            case 2 -> restOfPath = "bishop";
            case 3 -> restOfPath = "rook";
            case 4 -> restOfPath = "queen";
            case 5 -> restOfPath = "king";
        }
        return "/ChessAssets/ChessPieces/" + pathStart + restOfPath + "_1x_ns.png";
    }

    public static void removeFromGridPane(int x, int y, GridPane pane){
        pane.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y);
    }
}
