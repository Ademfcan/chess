package chessengine.CentralControlComponents;

import chessengine.App;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.XYcoord;
import chessengine.Enums.MoveRanking;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Graphics.Arrow;
import chessengine.Graphics.BindingController;
import chessengine.Misc.ChessConstants;
import chessserver.ChessboardTheme;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardGUIHandler {
    public final Pane chessPieceBoard;
    public final ImageView[][] piecesAtLocations;
    public final int pieceSize = 9;
    private final Pane arrowBoard;
    private final GridPane chessHighlightBoard;
    private final GridPane chessMoveBoard;
    private final GridPane chessBgBoard;
    private final HBox eatenWhitesContainer;
    private final HBox eatenBlacksContainer;
    private final VBox[][] bgPanes;
    private final VBox[][] moveBoxes;
    private final StackPane[][] highlightPanes;
    private final Logger logger;
    private final List<Arrow> arrows;
    private final Pane[] eatenBlacksArr = new Pane[ChessConstants.KINGINDEX + 1];
    private final int[] eatenBlacksCountArr = new int[ChessConstants.KINGINDEX + 1];
    private final Pane[] eatenWhitesArr = new Pane[ChessConstants.KINGINDEX + 1];
    private final int[] eatenWhitesCountArr = new int[ChessConstants.KINGINDEX + 1];
    private final double overlapFactor = .2d;
    private final float transitionTimePerSquare = .04f;
    public boolean inTransition;
    ChessMove lastMoveHighlighted;
    String highlightColor = "";
    // pretty self explanatory
    String currentColorType = ChessboardTheme.TRADITIONAL.toString(); // default type

    TextArea localInfo;
    ChessCentralControl myControl;
    private Circle lastMoveRank = null;

    public ChessBoardGUIHandler(ChessCentralControl myControl, Pane chessPieceBoard, HBox eatenWhites, HBox eatenBlacks, ImageView[][] piecesAtLocations, Pane ArrowBoard, VBox[][] bgPanes, VBox[][] moveBoxes, StackPane[][] highlightPanes, GridPane chessHighlightBoard, GridPane chessBgBoard, GridPane chessMoveBoard, TextArea localInfo) {
        this.myControl = myControl;
        this.chessPieceBoard = chessPieceBoard;
        this.eatenWhitesContainer = eatenWhites;
        this.eatenBlacksContainer = eatenBlacks;
        this.piecesAtLocations = piecesAtLocations;
        this.logger = LogManager.getLogger(this.toString());
        this.arrowBoard = ArrowBoard;
        this.bgPanes = bgPanes;
        this.highlightPanes = highlightPanes;
        this.chessHighlightBoard = chessHighlightBoard;
        this.chessMoveBoard = chessMoveBoard;
        this.chessBgBoard = chessBgBoard;
        this.moveBoxes = moveBoxes;
        this.localInfo = localInfo;
        arrows = new ArrayList<>();
        chessPieceBoard.layoutBoundsProperty().addListener(e -> {
            redrawArrows();

        });
        setUpEatenArrays(true);
        setUpEatenArrays(false);

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

    private void setUpEatenArrays(boolean isWhite) {
        Pane[] ref = isWhite ? eatenWhitesArr : eatenBlacksArr;
        HBox parent = isWhite ? eatenWhitesContainer : eatenBlacksContainer;
        // every piece type can be eaten, except for kings, however in sandbox you can so we add all
        for (int i = 0; i <= ChessConstants.KINGINDEX; i++) {
            Pane childContainer = new Pane();
            parent.getChildren().add(childContainer);
            childContainer.prefHeightProperty().bind(parent.heightProperty());
            parent.widthProperty().addListener(e -> {
                Platform.runLater(() -> {
                    double newWidth = childContainer.getChildren().stream().mapToDouble(n -> (n.getBoundsInParent().getWidth() * overlapFactor)).sum() + (childContainer.getChildren().isEmpty() ? 0 : childContainer.getChildren().get(0).getBoundsInParent().getWidth());
                    childContainer.setPrefWidth(newWidth);
                });
            });
            childContainer.getChildren().addListener((ListChangeListener<? super Node>) e -> {
                Platform.runLater(() -> {
                    double newWidth = childContainer.getChildren().stream().mapToDouble(n -> (n.getBoundsInParent().getWidth() * overlapFactor)).sum() + (childContainer.getChildren().isEmpty() ? 0 : childContainer.getChildren().get(0).getBoundsInParent().getWidth());
                    childContainer.setPrefWidth(newWidth);
                });
            });

            ref[i] = childContainer;
        }

    }

    private void redrawArrows() {
        arrowBoard.getChildren().removeIf(c -> !c.getUserData().toString().equals("ranking"));
        for (Arrow a : arrows) {
            // redraw with new size
            drawArrow(a);
        }
    }

    public void clearArrows() {
        arrows.clear();
        arrowBoard.getChildren().clear();
    }

    public void addArrow(Arrow newArrow) {
        // remove arrow when drawn into same location where there already is one. Like a toggle switch
        boolean isAlreadyArrow = arrowBoard.getChildren().removeIf(a -> a.getUserData().toString().equals(newArrow.toString()));
        if (!isAlreadyArrow) {
            arrows.add(newArrow);
            drawArrow(newArrow);
        } else {
            arrows.removeIf(a -> a.equals(newArrow));
        }
    }

    private void drawArrow(Arrow arrow) {
        SVGPath path = arrow.generateSvg(arrowBoard.getPrefHeight(), arrowBoard.getPrefWidth());
        arrowBoard.getChildren().add(path);
        path.setUserData(arrow.toString());

//        System.out.println("Adding arrow: " + path.getContent());
    }

    public int[] turnLayoutXyintoBoardXy(double lx, double ly) {
        double boxX = chessPieceBoard.getWidth() / 8;
        double boxY = chessPieceBoard.getHeight() / 8;
        int x = (int) (lx / boxX);
        int y = (int) (ly / boxY);
        return new int[]{x, y};
    }

    public void removeLayoutBindings(ImageView piece) {
        if (piece.layoutXProperty().isBound()) {
            piece.layoutXProperty().unbind();
            piece.layoutYProperty().unbind();

        }

    }

    public void putBackLayoutBindings(ImageView piece, int x, int y) {
        piece.layoutXProperty().bind(chessPieceBoard.widthProperty().divide(8).multiply(x).add(chessPieceBoard.widthProperty().divide(16).subtract(piece.fitWidthProperty().divide(2))));
        piece.layoutYProperty().bind(chessPieceBoard.heightProperty().divide(8).multiply(y).add(chessPieceBoard.heightProperty().divide(16).subtract(piece.fitHeightProperty().divide(2))));
    }

    public void resetEverything(boolean isWhiteOriented) {
        // eaten pieces
        for (int i = 0; i <= ChessConstants.KINGINDEX; i++) {
            eatenWhitesArr[i].getChildren().clear();
            eatenBlacksArr[i].getChildren().clear();
            eatenBlacksCountArr[i] = 0;
            eatenWhitesCountArr[i] = 0;

        }
        reloadNewBoard(ChessConstants.startBoardState, isWhiteOriented);
    }

    public ImageView createNewPiece(int brdIndex, boolean isWhite, boolean isEaten) {
        String restOfPath = "";
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

    public String createPiecePath(int brdIndex, boolean isWhite) {
        String restOfPath = "";
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

    public void removeFromChessBoard(int x, int y, boolean isWhite, boolean isWhiteOriented) {
        if (!isWhiteOriented) {
            y = 7 - y;
            x = 7 - x;
        }
        int fy = y;
        int fx = x;
//        System.out.println("Trying to remove a piece at x:" + x + "y: " + y + " iswhite?:" + isWhite);
        boolean isRemoved = chessPieceBoard.getChildren().removeIf(n -> n.getUserData().toString().equals(fx + "," + fy + "," + isWhite));
        piecesAtLocations[x][y] = null;
        if (!isRemoved) {
            logger.error(String.format("No pieces were removed at X:%d ,Y:%d", x, y));
        }

    }

    public void removeFromChessBoard(ImageView piece, int x, int y, boolean isWhiteOriented) {
        if (!isWhiteOriented) {
            y = 7 - y;
            x = 7 - x;
        }
        piecesAtLocations[x][y] = null;
        chessPieceBoard.getChildren().remove(piece);
    }

    public void addToChessBoard(int x, int y, int brdIndex, boolean isWhite, boolean isWhiteOriented) {
        if (!isWhiteOriented) {
            y = 7 - y;
            x = 7 - x;
        }
        ImageView peice = createNewPiece(brdIndex, isWhite, false);
        peice.setUserData(x + "," + y + "," + isWhite);
        peice.layoutXProperty().bind(calcLayoutXBinding(x, peice.fitWidthProperty()));
        peice.layoutYProperty().bind(calcLayoutYBinding(y, peice.fitHeightProperty()));
        chessPieceBoard.getChildren().add(peice);
        piecesAtLocations[x][y] = peice;

    }

    public DoubleBinding calcLayoutXBinding(int x, ReadOnlyDoubleProperty widthProperty) {
        return chessPieceBoard.widthProperty().divide(8).multiply(x).add(chessPieceBoard.widthProperty().divide(16).subtract(widthProperty.divide(2)));
    }

    public DoubleBinding calcLayoutYBinding(int y, ReadOnlyDoubleProperty heightProperty) {
        return chessPieceBoard.heightProperty().divide(8).multiply(y).add(chessPieceBoard.heightProperty().divide(16).subtract(heightProperty.divide(2)));
    }

    public void movePieceOnBoard(int oldX, int oldY, int newX, int newY, boolean isWhite, boolean isWhiteOriented) {
        if (!isWhiteOriented) {
            oldY = 7 - oldY;
            newY = 7 - newY;
            oldX = 7 - oldX;
            newX = 7 - newX;
        }

        ImageView piece = piecesAtLocations[oldX][oldY];
        piecesAtLocations[oldX][oldY] = null;
        removeLayoutBindings(piece);
        movePieceOnBoard(oldX, oldY, newX, newY, isWhite, piece);


    }

    public void moveNewPieceOnBoard(int oldX, int oldY, int newX, int newY, int boardIndex, boolean isWhite, boolean isWhiteOriented) {
        if (!isWhiteOriented) {
            oldY = 7 - oldY;
            newY = 7 - newY;
            oldX = 7 - oldX;
            newX = 7 - newX;
            // todo find one missing x flip
        }
        int[] xyOld = calcXY(oldX, oldY);

        ImageView piece = createNewPiece(boardIndex, isWhite, false);
        piece.setLayoutX(xyOld[0]);
        piece.setLayoutY(xyOld[1]);
        piece.setUserData(newX + "," + newY + "," + isWhite);
        chessPieceBoard.getChildren().add(piece);
        movePieceOnBoard(oldX, oldY, newX, newY, isWhite, piece);


    }

    private void movePieceOnBoard(int oldX, int oldY, int newX, int newY, boolean isWhite, ImageView piece) {
        double dist = Math.sqrt(Math.pow(newY - oldY, 2) + Math.pow(newX - oldX, 2));
        PathTransition transition = new PathTransition();
        inTransition = true;
        transition.setDuration(Duration.seconds(dist * transitionTimePerSquare));
        int[] xyOld = calcXY(oldX, oldY);
        int[] xyNew = calcXY(newX, newY);
        double offsetX = chessPieceBoard.getWidth() / (2 * pieceSize);
        double offsetY = chessPieceBoard.getHeight() / (2 * pieceSize);
        transition.setPath(new Line(offsetX, offsetY, xyNew[0] - xyOld[0] + offsetX, xyNew[1] - xyOld[1] + offsetY));
        transition.setNode(piece);
        piece.setUserData(newX + "," + newY + "," + isWhite);
        piecesAtLocations[newX][newY] = piece;
        transition.setOnFinished(e -> {
            piece.setTranslateX(0);
            piece.setTranslateY(0);
            putBackLayoutBindings(piece, newX, newY);
            inTransition = false;
        });
        transition.play();
    }

    public int[] calcXY(int x, int y) {
        double gridX = chessPieceBoard.getWidth() / 8;
        double gridY = chessPieceBoard.getHeight() / 8;
        double offsetX = chessPieceBoard.getWidth() / (2 * pieceSize);
        double offsetY = chessPieceBoard.getHeight() / (2 * pieceSize);
        return new int[]{(int) (gridX * x + gridX / 2 - offsetX), (int) (gridY * y + gridY / 2 - offsetY)};

    }

    public int[] calcXYWithoutOffset(int x, int y) {
        double gridX = chessPieceBoard.getWidth() / 8;
        double gridY = chessPieceBoard.getHeight() / 8;

        return new int[]{(int) (gridX * x + gridX / 2), (int) (gridY * y + gridY / 2)};

    }

    public void addToEatenPieces(int pieceIndex, boolean isWhite, boolean isWhiteOritented) {
        boolean isTopSide = isWhite == isWhiteOritented;
        if(pieceIndex == ChessConstants.EMPTYINDEX){
            logger.error("Invalid piece index provided!");
        }
        ImageView smallPeice = createNewPiece(pieceIndex, isWhite, true);
        smallPeice.layoutYProperty().bind(eatenBlacksContainer.heightProperty().divide(2).subtract(smallPeice.fitWidthProperty().divide(2))); // all will have same height
        if (isTopSide) {
//            System.out.println("Adding top");
            int currentPieceCount = eatenWhitesCountArr[pieceIndex]++;
            smallPeice.layoutXProperty().bind(smallPeice.fitWidthProperty().multiply(overlapFactor).multiply(currentPieceCount));
            eatenWhitesArr[pieceIndex].getChildren().add(smallPeice);
        } else {
//            System.out.println("Adding bottom");
            int currentPieceCount = eatenBlacksCountArr[pieceIndex]++;
            smallPeice.layoutXProperty().bind(smallPeice.fitWidthProperty().multiply(overlapFactor).multiply(currentPieceCount));
            eatenBlacksArr[pieceIndex].getChildren().add(smallPeice);
        }

    }

    public void removeFromEatenPeices(int pieceIndex, boolean isTopSide) {
        if (isTopSide) {
//            System.out.println("Removing top");
            int size = eatenWhitesArr[pieceIndex].getChildren().size();
            if (size > 0) {
                eatenWhitesArr[pieceIndex].getChildren().remove(size - 1);
                eatenWhitesCountArr[pieceIndex]--;
            } else {
                logger.error("Trying to remove a piece that is not in eaten pieces");
            }
        } else {
//            System.out.println("Removing bottom");
            int size = eatenBlacksArr[pieceIndex].getChildren().size();
            if (size > 0) {
                eatenBlacksArr[pieceIndex].getChildren().remove(size - 1);
                eatenBlacksCountArr[pieceIndex]--;

            } else {
                logger.error("Trying to remove a piece that is not in eaten pieces");
            }
        }
    }

    public void removeAllPieces() {
        chessPieceBoard.getChildren().clear();
    }

    public void reloadNewBoard(ChessPosition position, boolean isWhiteOriented) {
        // adjust chess bg squares depending on orientation
        changeChessBg(currentColorType);

        removeAllPieces();
        boolean isWhite = true;
        for (int j = 0; j < 2; j++) {
            // one pass for white pieces then black
            long[] pieces = isWhite ? position.board.getWhitePiecesBB() : position.board.getBlackPiecesBB();
            for (int i = 0; i < 6; i++) {
                if (i == 5) {
                    // king
                    XYcoord kingLocation = isWhite ? position.board.getWhiteKingLocation() : position.board.getBlackKingLocation();
                    addToChessBoard(kingLocation.x, kingLocation.y, ChessConstants.KINGINDEX, isWhite, isWhiteOriented);
                } else {
                    List<XYcoord> coords = GeneralChessFunctions.getPieceCoords(pieces[i]);
                    for (XYcoord c : coords) {
                        addToChessBoard(c.x, c.y, i, isWhite, isWhiteOriented);
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

    public void highlightMove(ChessMove move, boolean isWhiteOriented) {
        if (lastMoveHighlighted != null) {
            // clear old highlight
            removeHiglight(lastMoveHighlighted.getOldX(), lastMoveHighlighted.getOldY());
            removeHiglight(lastMoveHighlighted.getNewX(), lastMoveHighlighted.getNewY());
        }
        if (!isWhiteOriented) {
            lastMoveHighlighted = move.invertMove();
        } else {
            lastMoveHighlighted = move;

        }
        highlightSquare(lastMoveHighlighted.getOldX(), lastMoveHighlighted.getOldY(), false);
        highlightSquare(lastMoveHighlighted.getNewX(), lastMoveHighlighted.getNewY(), false);


    }

    public void clearAllHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                removeHiglight(i, j);
                removeHiglightBorder(i, j);
                removeMoveSquare(i, j);
            }
        }

    }

    public void clearUserCreatedHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isNonUserHighlight(i, j)) {
                    removeHiglight(i, j);
                }
                removeHiglightBorder(i, j);
                removeMoveSquare(i, j);
            }
        }

    }

    private boolean isNonUserHighlight(int i, int j) {
        if (lastMoveHighlighted != null) {
            return i == lastMoveHighlighted.getNewX() && j == lastMoveHighlighted.getNewY() || i == lastMoveHighlighted.getOldX() || j == lastMoveHighlighted.getOldY();
        }
        return false;
    }

    public void changeChessBg(String colorType) {
        currentColorType = colorType;
        boolean isLight = true;
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
            BindingController.bindRegionToStyle(bg, App.mainScreenController.fullScreen.widthProperty(), "-fx-background-radius:", .0025, "-fx-background-color:" + curr);

            // currBgColors[count] = curr;
            if (count % 8 == 0) {
                // offset every row for checkerboard
                isLight = !isLight;
            }


        }
    }

    public void removeHiglightBorder(int x, int y) {
        BindingController.bindRegionTo2Styles(moveBoxes[x][y], App.mainScreenController.fullScreen.widthProperty(), "-fx-border-radius:", "-fx-border-width:", ChessConstants.borderRadFactor, ChessConstants.borderWidthFactor, "-fx-border-color:black");

    }

    public void higlightBorder(int x, int y) {
        BindingController.bindRegionTo2Styles(moveBoxes[x][y], App.mainScreenController.fullScreen.widthProperty(), "-fx-border-radius:", "-fx-border-width:", ChessConstants.borderRadFactor, ChessConstants.borderWidthFactorExp, "-fx-border-color:white");

    }

    public void removeMoveSquare(int x, int y) {
        moveBoxes[x][y].getChildren().get(0).setVisible(false);
    }

    public void showMoveSquare(int x, int y) {
        // only child is the circle
        moveBoxes[x][y].getChildren().get(0).setVisible(true);
    }

    public void highlightSquare(int x, int y, String customColor) {
        highlightPanes[x][y].setStyle("-fx-background-color: " + customColor);

    }

    public void highlightSquare(int x, int y, boolean isPieceSelection) {
        if (isPieceSelection) {
            highlightColor = "rgba(223, 90, 37, 0.6)";
        } else {
            highlightColor = "rgba(44, 212, 255, 0.6)";
        }
        highlightPanes[x][y].setStyle("-fx-background-color: " + highlightColor);

    }

    public void toggleSquareHighlight(int x, int y, boolean isPieceSelection) {
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
    public void removeHiglight(int x, int y) {
        highlightPanes[x][y].setStyle("-fx-background-color: transparent");

    }

    public void addMoveRanking(ChessMove moveThatCreatedThis, MoveRanking ranking, boolean isWhiteOriented) {

        int rankingEndX = isWhiteOriented ? moveThatCreatedThis.getNewX() : 7 - moveThatCreatedThis.getNewX();
        int rankingEndY = isWhiteOriented ? moveThatCreatedThis.getNewY() : 7 - moveThatCreatedThis.getNewY();
        addRankingCircle(rankingEndX, rankingEndY, ranking.getColor(), ranking.getImage());
    }

    public void addRankingCircle(int rankingEndX, int rankingEndY, Paint color, Image image) {
        if (lastMoveRank != null) {
            arrowBoard.getChildren().remove(lastMoveRank);
        }
        Circle moveRank = new Circle();
        moveRank.setFill(new ImagePattern(image));
        moveRank.setStroke(color);
        moveRank.setStrokeWidth(2);
        moveRank.radiusProperty().bind(chessPieceBoard.widthProperty().divide(24));
        moveRank.layoutXProperty().bind(calcMoveRankingXBinding(rankingEndX));
        moveRank.layoutYProperty().bind(calcMoveRankingBinding(rankingEndY));
        moveRank.setUserData("ranking");
        lastMoveRank = moveRank;
        arrowBoard.getChildren().add(moveRank);
    }

    // these will put in top right corner
    public DoubleBinding calcMoveRankingXBinding(int x) {
        return chessPieceBoard.widthProperty().divide(8).multiply(x + 1);
    }

    public DoubleBinding calcMoveRankingBinding(int y) {
        return chessPieceBoard.heightProperty().divide(8).multiply(y);
    }


    /** Updating the chessboard **/
    /**
     * Updating the only a chessmove on the board
     **/

    public void makeChessMove(ChessMove move, boolean isReverse, ChessPosition currentPosition, ChessPosition newPos, boolean isWhiteOriented) {

        if (move.isEating() && !isReverse) {
            // needs to be before move
            int eatenAddIndex = move.getEatingIndex();
            addToEatenPieces(eatenAddIndex, !move.isWhite(), isWhiteOriented);
            removeFromChessBoard(move.getNewX(), move.getNewY(), !move.isWhite(), isWhiteOriented);
        }
        if (move.isEnPassant()) {
            if (!isReverse) {
                int backDir = move.isWhite() ? 1 : -1;
                int eatY = move.getNewY() + backDir;
                int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), eatY, !move.isWhite(), currentPosition.board);
                addToEatenPieces(eatenAddIndex, !move.isWhite(), isWhiteOriented);
                removeFromChessBoard(move.getNewX(), eatY, !move.isWhite(), isWhiteOriented);
            } else {
                int backDir = move.isWhite() ? 1 : -1;
                int eatY = move.getOldY() + backDir;
                int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getOldX(), eatY, !move.isWhite(), newPos.board);
                removeFromEatenPeices(eatenAddIndex, !move.isWhite() == isWhiteOriented);
                addToChessBoard(move.getOldX(), eatY, eatenAddIndex, !move.isWhite(), isWhiteOriented);
            }

        }
        if (move.isCastleMove()) {
            // shortcastle is +x dir longcastle = -2x dir
            if (isReverse) {
                int dirFrom = move.getOldX() == 6 ? 1 : -2;
                int dirTo = move.getOldX() == 6 ? -1 : 1;
                // uncastle
                movePieceOnBoard(move.getOldX() + dirTo, move.getOldY(), move.getOldX() + dirFrom, move.getNewY(), move.isWhite(), isWhiteOriented);
            } else {
                int dirFrom = move.getNewX() == 6 ? 1 : -2;
                int dirTo = move.getNewX() == 6 ? -1 : 1;
                movePieceOnBoard(move.getNewX() + dirFrom, move.getOldY(), move.getNewX() + dirTo, move.getNewY(), move.isWhite(), isWhiteOriented);
            }

        }
        // this is where the piece actually moves
        if (!move.isPawnPromo()) {
            // in pawn promo we need to handle differently as the piece changes
            movePieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.isWhite(), isWhiteOriented);

        }
        // move
        else {
            if (isReverse) {
                removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite(), isWhiteOriented);
                moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), ChessConstants.PAWNINDEX, move.isWhite(), isWhiteOriented);

            } else {
                removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite(), isWhiteOriented);
                moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.getPromoIndx(), move.isWhite(), isWhiteOriented);


            }
        }
        if (move.isEating() && isReverse) {
            // need to create a piece there to undo eating
            // must be after moving
            int pieceIndex = move.getEatingIndex();
            addToChessBoard(move.getOldX(), move.getOldY(), pieceIndex, !move.isWhite(), isWhiteOriented);
            removeFromEatenPeices(pieceIndex, !move.isWhite() == isWhiteOriented);

        }
        if (!isReverse) {
            highlightMove(move, isWhiteOriented);
        }


    }

}
