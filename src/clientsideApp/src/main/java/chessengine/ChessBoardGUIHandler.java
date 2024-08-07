package chessengine;

import chessserver.ChessboardTheme;
import javafx.animation.PathTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChessBoardGUIHandler {
    private final Pane arrowBoard;
    private final GridPane chessHighlightBoard;
    private final GridPane chessMoveBoard;
    private final GridPane chessBgBoard;
    public final Pane chessPieceBoard;
    private final HBox eatenWhites;
    private final HBox eatenBlacks;

    public final ImageView[][] piecesAtLocations;
    private final VBox[][] bgPanes;

    private final VBox[][] moveBoxes;
    private final StackPane[][] highlightPanes;

    private Logger logger;

    private final List<Arrow> arrows;




    public ChessBoardGUIHandler(Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, Pane ArrowBoard, VBox[][] bgPanes,VBox[][] moveBoxes, StackPane[][] highlightPanes,GridPane chessHighlightBoard,GridPane chessBgBoard,GridPane chessMoveBoard){
        this.chessPieceBoard = chessPieceBoard;
        this.eatenWhites = eatenWhites;
        this.eatenBlacks = eatenBlacks;
        this.piecesAtLocations = piecesAtLocations;
        this.logger = LogManager.getLogger(this.toString());
        this.arrowBoard = ArrowBoard;
        this.bgPanes = bgPanes;
        this.highlightPanes = highlightPanes;
        this.chessHighlightBoard = chessHighlightBoard;
        this.chessMoveBoard = chessMoveBoard;
        this.chessBgBoard = chessBgBoard;
        this.moveBoxes = moveBoxes;
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
        // remove arrow when drawn into same location where there already is one. Like a toggle switch
        boolean isAlreadyArrow = arrowBoard.getChildren().removeIf(a -> a.getUserData().toString().equals(newArrow.startX + "," + newArrow.startY + "," + newArrow.endX + "," + newArrow.endY));
        if(!isAlreadyArrow){
            arrows.add(newArrow);
            drawArrow(newArrow);
        }
        else{
            arrows.removeIf(a->a.equals(newArrow));
        }
    }

    private void drawArrow(Arrow arrow){
        SVGPath path = arrow.generateSvg((int)arrowBoard.getPrefHeight(),(int)arrowBoard.getPrefWidth());
        arrowBoard.getChildren().add(path);
        path.setUserData(arrow.startX + "," + arrow.startY + "," + arrow.endX + "," + arrow.endY);

//        System.out.println("Adding arrow: " + path.getContent());
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


    public void resetEverything(boolean isWhiteOriented){
        eatenBlacks.getChildren().clear();
        eatenWhites.getChildren().clear();
        reloadNewBoard(ChessConstants.startBoardState,isWhiteOriented);
    }
    public final int pieceSize = 9;
    public ImageView createNewPiece(int brdIndex, boolean isWhite, boolean isEaten){
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

    public void removeFromChessBoard(int x, int y,boolean isWhite,boolean isWhiteOriented){
        if(!isWhiteOriented){
            y = 7-y;
        }
        int fy = y;
//        System.out.println("Trying to remove a piece at x:" + x + "y: " + y + " iswhite?:" + isWhite);
        boolean isRemoved = chessPieceBoard.getChildren().removeIf(n -> n.getUserData().toString().equals(x + "," + fy + "," + isWhite));
        piecesAtLocations[x][y] = null;
        if(!isRemoved){
            logger.error(String.format("No pieces were removed at X:%d ,Y:%d",x,y));
        }

    }

    public void removeFromChessBoard(ImageView piece,int x, int y,boolean isWhiteOriented){
        if(!isWhiteOriented){
            y = 7-y;
        }
        piecesAtLocations[x][y] = null;
        chessPieceBoard.getChildren().remove(piece);
    }

    public void addToChessBoard(int x, int y,int brdIndex,boolean isWhite,boolean isWhiteOriented){
        if(!isWhiteOriented){
            y = 7-y;
        }
        ImageView peice = createNewPiece(brdIndex,isWhite,false);
        peice.setUserData(x + "," + y + "," + isWhite);
        peice.layoutXProperty().bind(calcLayoutXBinding(x,peice.fitWidthProperty()));
        peice.layoutYProperty().bind(calcLayoutYBinding(y,peice.fitHeightProperty()));
        chessPieceBoard.getChildren().add(peice);
        piecesAtLocations[x][y] = peice;

    }

    public DoubleBinding calcLayoutXBinding(int x, ReadOnlyDoubleProperty widthProperty){
        return chessPieceBoard.widthProperty().divide(8).multiply(x).add(chessPieceBoard.widthProperty().divide(16).subtract(widthProperty.divide(2)));
    }
    public DoubleBinding calcLayoutYBinding(int y,ReadOnlyDoubleProperty heightProperty){
        return chessPieceBoard.heightProperty().divide(8).multiply(y).add(chessPieceBoard.heightProperty().divide(16).subtract(heightProperty.divide(2)));
    }

    public void movePieceOnBoard(int oldX, int oldY,int newX, int newY,boolean isWhite,boolean isWhiteOriented){
        if(!isWhiteOriented){
            oldY = 7-oldY;
            newY = 7-newY;
        }
        ImageView piece = piecesAtLocations[oldX][oldY];
        piecesAtLocations[oldX][oldY] = null;
        removeLayoutBindings(piece);
        movePieceOnBoard(oldX,oldY,newX,newY,isWhite,piece);




    }

    public void moveNewPieceOnBoard(int oldX, int oldY,int newX, int newY,int boardIndex,boolean isWhite,boolean isWhiteOriented){
        if(!isWhiteOriented){
            oldY = 7-oldY;
            newY = 7-newY;
        }
        int[] xyOld = calcXY(oldX,oldY);

        ImageView piece = createNewPiece(boardIndex,isWhite,false);
        piece.setLayoutX(xyOld[0]);
        piece.setLayoutY(xyOld[1]);
        piece.setUserData(newX + "," + newY + "," + isWhite);
        chessPieceBoard.getChildren().add(piece);
        movePieceOnBoard(oldX,oldY,newX,newY,isWhite,piece);


    }

    private float transitionTimePerSquare = .04f;
    public boolean inTransition;
    private void movePieceOnBoard(int oldX, int oldY,int newX, int newY,boolean isWhite,ImageView piece){
        double dist  = Math.sqrt(Math.pow(newY-oldY,2) + Math.pow(newX-oldX,2));
        PathTransition transition = new PathTransition();
        inTransition = true;
        transition.setDuration(Duration.seconds(dist*transitionTimePerSquare));
        int[] xyOld = calcXY(oldX,oldY);
        int[] xyNew = calcXY(newX,newY);
        double offsetX = chessPieceBoard.getWidth()/(2*pieceSize);
        double offsetY = chessPieceBoard.getHeight()/(2*pieceSize);
        transition.setPath(new Line(offsetX,offsetY,xyNew[0]-xyOld[0]+offsetX,xyNew[1]-xyOld[1]+offsetY));
        transition.setNode(piece);
        piece.setUserData(newX + "," + newY + "," + isWhite);
        piecesAtLocations[newX][newY] = piece;
        transition.setOnFinished(e->{
            piece.setTranslateX(0);
            piece.setTranslateY(0);
            putBackLayoutBindings(piece,newX,newY);
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



    public void updateEatenPieces(int pieceIndex,boolean isTopSide){
        ImageView smallPeice = createNewPiece(pieceIndex,isTopSide,true);
        smallPeice.setUserData(Integer.toString(pieceIndex));
        if(isTopSide){
            System.out.println("Adding top");
            eatenWhites.getChildren().add(smallPeice);
        }
        else{
            System.out.println("Adding bottom");
            eatenBlacks.getChildren().add(smallPeice);
        }

    }

    public void removeFromEatenPeices(String BoardId,boolean isTopSide){
        HBox eatenPieces = isTopSide ?  eatenWhites : eatenBlacks;
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

    public void reloadNewBoard(ChessPosition position,boolean isWhiteOriented){
        // adjust chess bg squares depending on orientation
        changeChessBg(currentColorType,isWhiteOriented);

        removeAllPieces();
        boolean isWhite = true;
        for(int j = 0;j<2;j++){
            // one pass for white pieces then black
            long[] pieces = isWhite ? position.board.getWhitePieces() : position.board.getBlackPieces();
            for(int i = 0;i<6;i++){
                if(i == 5){
                    // king
                    XYcoord kingLocation = isWhite ? position.board.getWhiteKingLocation() : position.board.getBlackKingLocation();
                    addToChessBoard(kingLocation.x, kingLocation.y, ChessConstants.KINGINDEX,isWhite,isWhiteOriented);
                }
                else{
                    List<XYcoord> coords = GeneralChessFunctions.getPieceCoords(pieces[i]);
                    for(XYcoord c : coords){
                        addToChessBoard(c.x,c.y,i,isWhite,isWhiteOriented);
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
    public void highlightMove(ChessMove move,boolean isWhiteOriented){
        // clear old highlight
        if(lastMoveHighlighted != null){
            removeHiglight(lastMoveHighlighted.getOldX(),lastMoveHighlighted.getOldY());
            removeHiglight(lastMoveHighlighted.getNewX(),lastMoveHighlighted.getNewY());
        }
        if(!isWhiteOriented){
            lastMoveHighlighted = move.invertMove();
        }
        else{
            lastMoveHighlighted = move;

        }
        highlightSquare(lastMoveHighlighted.getOldX(),lastMoveHighlighted.getOldY(),false);
        highlightSquare(lastMoveHighlighted.getNewX(),lastMoveHighlighted.getNewY(),false);

    }

    ChessMove lastMoveHighlighted;

    String highlightColor = "";

    public void clearAllHighlights()
    {
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                removeHiglight(i,j);
                removeHiglightBorder(i,j);
                removeMoveSquare(i,j);
            }
        }

    }

    public void clearUserCreatedHighlights()
    {
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                if(!isNonUserHighlight(i,j)){
                    removeHiglight(i,j);
                }
                removeHiglightBorder(i,j);
                removeMoveSquare(i,j);
            }
        }

    }


    private boolean isNonUserHighlight(int i,int j){
        if(lastMoveHighlighted != null){
            return i == lastMoveHighlighted.getNewX() && j == lastMoveHighlighted.getNewY() || i == lastMoveHighlighted.getOldX() || j == lastMoveHighlighted.getOldY();
        }
        return false;
    }

    // pretty self explanatory
    String currentColorType = ChessboardTheme.TRADITIONAL.toString(); // default type
    public void changeChessBg(String colorType,boolean isWhiteOriented) {
        currentColorType = colorType;
        boolean isLight = isWhiteOriented;
        ChessboardTheme theme = ChessboardTheme.getCorrespondingTheme(colorType);
        int count = 0;

        for (Node n : chessBgBoard.getChildren()) {
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

            VBox bg = (VBox) n;
//            App.bindingController.bindRegionWithCustomStyles(bg,App.mainScreenController.fullScreen.widthProperty(),new String[]{"-fx-background-radius:"},new double[]{.0025},"-fx-background-color:" + curr);
            bg.styleProperty().unbind();
            BindingController.bindRegionToStyle(bg,App.mainScreenController.fullScreen.widthProperty(),"-fx-background-radius:",.0025,"-fx-background-color:" + curr);

            // currBgColors[count] = curr;
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

    public void removeHiglightBorder(int x, int y){
        BindingController.bindRegionTo2Styles(moveBoxes[x][y],App.mainScreenController.fullScreen.widthProperty(),"-fx-border-radius:","-fx-border-width:",ChessConstants.borderRadFactor,ChessConstants.borderWidthFactor,"-fx-border-color:black");

    }

    public void higlightBorder(int x, int y){
        BindingController.bindRegionTo2Styles(moveBoxes[x][y],App.mainScreenController.fullScreen.widthProperty(),"-fx-border-radius:","-fx-border-width:",ChessConstants.borderRadFactor,ChessConstants.borderWidthFactorExp,"-fx-border-color:white");

    }

    public void removeMoveSquare(int x,int y){
        moveBoxes[x][y].getChildren().get(0).setVisible(false);
    }

    public void showMoveSquare(int x,int y){
        // only child is the circle
        moveBoxes[x][y].getChildren().get(0).setVisible(true);
    }

    public void highlightSquare(int x, int y, boolean isPieceSelection) {
        if (isPieceSelection) {
            highlightColor = "rgba(223, 90, 37, 0.6)";
        } else {
            highlightColor = "rgba(44, 212, 255, 0.6)";
        }
        highlightPanes[x][y].setStyle("-fx-background-color: " + highlightColor);

    }

    public void toggleSquareHighlight(int x, int y, boolean isPieceSelection){
        if (isPieceSelection) {
            highlightColor = "rgba(223, 90, 37, 0.6)";
        } else {
            highlightColor = "rgba(44, 212, 255, 0.6)";
        }
        if (!highlightPanes[x][y].getStyle().contains("rgba")) {
            highlightPanes[x][y].setStyle("-fx-background-color: " + highlightColor);
        } else {
            highlightPanes[x][y].setStyle("-fx-background-color: transparent");

        }
    }
    // fixed method to clear any highlights
    public void removeHiglight(int x, int y){
        highlightPanes[x][y].setStyle("-fx-background-color: transparent");

    }

}
