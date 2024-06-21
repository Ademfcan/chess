package chessengine;

import chessserver.ChessboardTheme;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ChessBoardGUIHandler {
    private final AnchorPane arrowBoard;
    private final StackPane[][] bgPanes;
    private final GridPane chessHighlightBoard;
    public Pane chessPieceBoard;
    private HBox eatenWhites;
    private HBox eatenBlacks;

    public ImageView[][] piecesAtLocations;

    private Logger logger;

    private List<Arrow> arrows;
    public ChessBoardGUIHandler(Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, AnchorPane ArrowBoard, StackPane[][] bgPanes,GridPane chessHighlightBoard){
        this.chessPieceBoard = chessPieceBoard;
        this.eatenWhites = eatenWhites;
        this.eatenBlacks = eatenBlacks;
        this.piecesAtLocations = piecesAtLocations;
        this.logger = LogManager.getLogger(this.toString());
        this.arrowBoard = ArrowBoard;
        this.bgPanes = bgPanes;
        this.chessHighlightBoard = chessHighlightBoard;
        arrows = new ArrayList<>();
        chessPieceBoard.widthProperty().addListener(e->{
            redrawArrows();

        });
        chessPieceBoard.heightProperty().addListener(e->{

            redrawArrows();

        });

    }

    private void redrawArrows(){
        arrowBoard.getChildren().clear();
        for(Arrow a : arrows){
            // redraw with new size
            drawArrow(a);
        }
    }


    public void clearArrows(){
        arrows.clear();
        arrowBoard.getChildren().clear();
    }



    public void addArrow(Arrow newArrow){
        arrows.add(newArrow);
        drawArrow(newArrow);
    }

    private void drawArrow(Arrow arrow){
        SVGPath path = arrow.generateSvg((int)arrowBoard.getPrefHeight(),(int)arrowBoard.getPrefWidth());
        arrowBoard.getChildren().add(path);

        System.out.println("Adding arrow: " + path.getContent());
    }

    public int[] turnLayoutXyintoBoardXy(double lx,double ly){
        double boxX = chessPieceBoard.getWidth()/8;
        double boxY = chessPieceBoard.getHeight()/8;
        int x = (int) (lx/boxX);
        int y = (int) (ly/boxY);
        return new int[] {x,y};
    }

    public void removeLayoutBindings(ImageView piece){
        if(piece.layoutXProperty().isBound()){
            piece.layoutXProperty().unbind();
            piece.layoutYProperty().unbind();

        }

    }

    public void putBackLayoutBindings(ImageView piece,int x, int y){
        piece.layoutXProperty().bind(chessPieceBoard.widthProperty().divide(8).multiply(x).add(chessPieceBoard.widthProperty().divide(16).subtract(piece.fitWidthProperty().divide(2))));
        piece.layoutYProperty().bind(chessPieceBoard.heightProperty().divide(8).multiply(y).add(chessPieceBoard.heightProperty().divide(16).subtract(piece.fitHeightProperty().divide(2))));
    }


    public void resetEverything(){
        eatenBlacks.getChildren().clear();
        eatenWhites.getChildren().clear();
        reloadNewBoard(ChessConstants.startBoardState);
    }
    public final int pieceSize = 9;
    public ImageView createNewPiece(int brdIndex, boolean isWhite, boolean isEaten){
        System.out.println("Trying to create a: " + GeneralChessFunctions.getPieceType(brdIndex));
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

        piece.fitHeightProperty().bind(chessPieceBoard.heightProperty().divide(isEaten ? 16 : pieceSize));
        piece.fitWidthProperty().bind(chessPieceBoard.widthProperty().divide(isEaten ? 16 : pieceSize));
        piece.setPreserveRatio(true);


        GridPane.setHalignment(piece, HPos.CENTER);
        GridPane.setValignment(piece, VPos.CENTER);
        return piece;
    }




    public String createPiecePath(int brdIndex, boolean isWhite){
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

    public void removeFromChessBoard(int x, int y,boolean isWhite){
        boolean isRemoved = chessPieceBoard.getChildren().removeIf(n -> n.getUserData().toString().equals(x + "," + y + "," + isWhite));
        piecesAtLocations[x][y] = null;
        if(!isRemoved){
            logger.error(String.format("No pieces were removed at X:%d ,Y:%d",x,y));
        }

    }

    public void removeFromChessBoard(ImageView piece,int x, int y){
        piecesAtLocations[x][y] = null;
        chessPieceBoard.getChildren().remove(piece);
    }

    public void addToChessBoard(int x, int y,int brdIndex,boolean isWhite){
        ImageView peice = createNewPiece(brdIndex,isWhite,false);
        peice.setUserData(x + "," + y + "," + isWhite);
        peice.layoutXProperty().bind(chessPieceBoard.widthProperty().divide(8).multiply(x).add(chessPieceBoard.widthProperty().divide(16).subtract(peice.fitWidthProperty().divide(2))));
        peice.layoutYProperty().bind(chessPieceBoard.heightProperty().divide(8).multiply(y).add(chessPieceBoard.heightProperty().divide(16).subtract(peice.fitHeightProperty().divide(2))));
        chessPieceBoard.getChildren().add(peice);
        piecesAtLocations[x][y] = peice;

    }

    public void movePieceOnBoard(int oldX, int oldY,int newX, int newY,boolean isWhite){
        ImageView piece = piecesAtLocations[oldX][oldY];
        piecesAtLocations[oldX][oldY] = null;
        removeLayoutBindings(piece);
        movePieceOnBoard(oldX,oldY,newX,newY,isWhite,piece);




    }

    public void moveNewPieceOnBoard(int oldX, int oldY,int newX, int newY,int boardIndex,boolean isWhite){
        int[] xyOld = calcXY(oldX,oldY);
        ImageView piece = createNewPiece(boardIndex,isWhite,false);
        piece.setLayoutX(xyOld[0]);
        piece.setLayoutY(xyOld[1]);
        chessPieceBoard.getChildren().add(piece);
        movePieceOnBoard(oldX,oldY,newX,newY,isWhite,piece);


    }

    public boolean inTransition;
    private void movePieceOnBoard(int oldX, int oldY,int newX, int newY,boolean isWhite,ImageView piece){
        PathTransition transition = new PathTransition();
        inTransition = true;
        transition.setDuration(Duration.seconds(.22));
        int[] xyOld = calcXY(oldX,oldY);
        int[] xyNew = calcXY(newX,newY);
        double offsetX = chessPieceBoard.getWidth()/(2*pieceSize);
        double offsetY = chessPieceBoard.getHeight()/(2*pieceSize);
        transition.setPath(new Line(offsetX,offsetY,xyNew[0]-xyOld[0]+offsetX,xyNew[1]-xyOld[1]+offsetY));
        transition.setNode(piece);
        transition.setOnFinished(e->{
            piece.setTranslateX(0);
            piece.setTranslateY(0);
            putBackLayoutBindings(piece,newX,newY);
            piecesAtLocations[newX][newY] = piece;
            piece.setUserData(newX + "," + newY + "," + isWhite);
            inTransition = false;
        });
        transition.play();
    }





    public int[] calcXY(int x, int y){
        double gridX = chessPieceBoard.getWidth()/8;
        double gridY = chessPieceBoard.getHeight()/8;
        double offsetX = chessPieceBoard.getWidth()/(2*pieceSize);
        double offsetY = chessPieceBoard.getHeight()/(2*pieceSize);
        return new int[]{(int)(gridX*x+gridX/2-offsetX),(int)(gridY*y+gridY/2-offsetY)};

    }

    public int[] calcXYWithoutOffset(int x, int y){
        double gridX = chessPieceBoard.getWidth()/8;
        double gridY = chessPieceBoard.getHeight()/8;

        return new int[]{(int)(gridX*x+gridX/2),(int)(gridY*y+gridY/2)};

    }



    public void updateEatenPieces( int pieceIndex,boolean isWhite){
        ImageView smallPeice = createNewPiece(pieceIndex,isWhite,true);
        smallPeice.setUserData(Integer.toString(pieceIndex));
        if(isWhite){
            eatenWhites.getChildren().add(smallPeice);
        }
        else{
            eatenBlacks.getChildren().add(smallPeice);
        }

    }

    public void removeFromEatenPeices(String BoardId,boolean isWhite){
        HBox eatenPieces = isWhite ?  eatenWhites : eatenBlacks;
        Iterator<Node> it = eatenPieces.getChildren().iterator();
        while(it.hasNext()){
            ImageView v = (ImageView) it.next();
            if(v.getUserData().equals(BoardId)){
                it.remove();
                break;
            }
        }
    }

    public void removeAllPieces(){
        chessPieceBoard.getChildren().clear();
    }

    public void reloadNewBoard(ChessPosition position){
        removeAllPieces();
        boolean isWhite = true;
        for(int j = 0;j<2;j++){
            // one pass for white pieces then black
            long[] pieces = isWhite ? position.board.getWhitePieces() : position.board.getBlackPieces();
            for(int i = 0;i<6;i++){
                if(i == 5){
                    // king
                    XYcoord kingLocation = isWhite ? position.board.getWhiteKingLocation() : position.board.getBlackKingLocation();
                    addToChessBoard(kingLocation.x, kingLocation.y, ChessConstants.KINGINDEX,isWhite);
                }
                else{
                    List<XYcoord> coords = GeneralChessFunctions.getPieceCoords(pieces[i]);
                    for(XYcoord c : coords){
                        addToChessBoard(c.x,c.y,i,isWhite);
                    }
                }

            }
            isWhite = !isWhite;
        }

    }
    // not usefull but maybe later
    private void setUpChessPieces(GridPane board) {
        int pieceX = 0;
        int pieceY = 6;
        String pathStart = "w_";
        String restOfPath = "";
        boolean isWhite = true;
        boolean isPawn = true;
        for (int i = 0; i < 2; i++) {
            // colors
            for (int j = 0; j < 2; j++) {
                // pawns vs normal pieces
                for (int z = 0; z < 8; z++) {
                    if (!isWhite) {
                        pathStart = "b_";
                    }
                    if (isPawn) {
                        restOfPath = "pawn";
                    } else {
                        switch (z) {
                            case 0:
                            case 7:
                                restOfPath = "rook";
                                break;
                            case 1:
                            case 6:
                                restOfPath = "knight";
                                break;

                            case 2:
                            case 5:
                                restOfPath = "bishop";
                                break;

                            case 3:
                                restOfPath = "queen";
                                break;

                            case 4:
                                restOfPath = "king";
                                break;


                        }
                    }
                    ImageView piece = new ImageView("/ChessAssets/ChessPieces/" + pathStart + restOfPath + "_1x_ns.png");
                    piece.fitHeightProperty().bind(chessPieceBoard.heightProperty().divide(9));
                    piece.fitWidthProperty().bind(chessPieceBoard.widthProperty().divide(8));
                    piece.setPreserveRatio(true);



                    GridPane.setHalignment(piece, HPos.CENTER);
                    GridPane.setValignment(piece, VPos.CENTER);


                    board.add(piece, pieceX, pieceY);
                    piecesAtLocations[pieceX][pieceY] = piece;

                    pieceX++;

                }
                pieceX = 0;
                if (isWhite) {
                    pieceY++;
                } else {
                    pieceY--;
                }
                isPawn = false;
            }
            pieceY = 1;
            isPawn = true;
            isWhite = false;

        }
    }
    public void highlightMove(ChessMove move){
        highlightSquare(move.getOldX(),move.getOldY(),true);
        highlightSquare(move.getNewX(),move.getNewY(),true);
    }

    String highlightColor = "";

    public void clearHighlights()
    {
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                removeHiglight(i,j);
            }
        }

    }
    // pretty self explanatory
    public void changeChessBg(String colorType) {

        boolean isLight = true;
        ChessboardTheme theme = ChessboardTheme.getCorrespondingTheme(colorType);
        int count = 0;

        for (Node n : chessHighlightBoard.getChildren()) {
            String curr = "";
            if (isLight) {
                curr = theme.lightColor;
                isLight = false;
            } else {
                curr = theme.darkColor;
                isLight = true;
            }
            if (count < 63) {
                count++;
            }
            int x = count / 8;
            int y = count % 8;



            // currBgColors[count] = curr;
            n.setStyle("-fx-background-color: " + curr);
            if (count % 8 == 0) {
                // offset every row for checkerboard
                isLight = !isLight;
            }


        }
    }

    // get color values for each background type
    // first value is the "lighter color"  and second is the darker color
    private static String[] getColorStr(String colortype) {
        return switch (colortype) {
            case "Ice" -> new String[]{"#7FDEFF", "#4F518C"};
            case "Traditional" -> new String[]{"#9e7a3a", "#2e120b"};
            case "Halloween" -> new String[]{"#ff6619", "#241711"};
            case "Summer" -> new String[]{"#f7cc0a", "#22668D"};
            case "Cherry" -> new String[]{"#f7b2ad", "#8c2155"};
            default -> null;
        };
    }
    public void highlightSquare(int x, int y, boolean isPieceSelection) {
        if (isPieceSelection) {
            highlightColor = "rgba(44, 212, 255, 0.25)";
        } else {
            highlightColor = "rgba(223, 90, 37, 0.4)";
        }
        bgPanes[x][y].setStyle("-fx-background-color: " + highlightColor);

    }

    public void toggleSquareHighlight(int x, int y, boolean isPieceSelection){
        if (isPieceSelection) {
            highlightColor = "rgba(44, 212, 255, 0.25)";
        } else {
            highlightColor = "rgba(223, 90, 37, 0.4)";
        }
        if (!bgPanes[x][y].getStyle().contains("rgba")) {
            bgPanes[x][y].setStyle("-fx-background-color: " + highlightColor);
        } else {
            bgPanes[x][y].setStyle("-fx-background-color: transparent");

        }
    }
    // fixed method to clear any highlights
    private void removeHiglight(int x, int y){
        bgPanes[x][y].setStyle("-fx-background-color: transparent");

    }

}
