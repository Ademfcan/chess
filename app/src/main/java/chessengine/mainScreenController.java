package chessengine;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {

    public boolean isWhiteTurn = true;
    Boolean peiceSelected = false;
    // [0] = x ,[1] =  y coords [2] =( 1 = white, -1 = black)
    int[] selectedPeiceInfo = {0, 0, 0};

    List<XYcoord> oldHighights = null;
    ImageView selectedPeice;

    String highlightColor = "";
    StackPane[][] Bgpanes = new StackPane[8][8];
    private ImageView[][] peicesAtLocations = new ImageView[8][8];

    @FXML
    Button LeftButton;

    @FXML
    Button RightButton;



    @FXML
    Button settingsButton;

    @FXML
    Button settingsButtonToggled;

    @FXML
    GridPane chessBoard;

    @FXML
    GridPane chessBgBoard;

    @FXML
    GridPane promotionScreen;

    @FXML
    public GridPane chessPieceBoard;

    @FXML
    public AnchorPane test;




    @FXML
    public Label saveIndicator;

    @FXML
    public Button reset;

    @FXML
    Rectangle blackadvantage;

    @FXML
    Label blackEval;

    @FXML
    Rectangle whiteadvantage;

    @FXML
    Label whiteEval;
    @FXML
    Label evalDepth;



    @FXML
    Label victoryLabel;

    @FXML
    HBox eatenWhites;

    @FXML
    HBox eatenBlacks;

    @FXML
    Button homeButton;

    @FXML
    HBox promoContainer;

    @FXML
    GridPane fullScreen;

    @FXML
    GridPane settingsScreen;

    @FXML
    ImageView player1Select;

    @FXML
    ImageView player2Select;

    @FXML
    AnchorPane gameControls;

    @FXML
    ComboBox<String> bgColorSelector;

    @FXML
    ComboBox<String> pieceSelector;

    @FXML
    ComboBox<Integer> evalSelector;

    @FXML
    StackPane sidePanel;

    @FXML
    HBox topControls;

    @FXML
    VBox bottomControls;

    @FXML
    Label bgLabel;

    @FXML
    Label pieceLabel;

    @FXML
    ComboBox<String> player1Selector;

    @FXML
    ComboBox<String> player2Selector;




    pieceLocationHandler pieceHandler;

    Computer chessAiForBestMove;
    Computer chessAiForEvalBar;

    evaluationbartask evalTask;
    chessComputerTask computerTask;

    private Logger logger;

    private boolean GameOver = false;

    private int gameEndIndx = 1000000;

    public boolean isVsComputer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(this.toString());
        chessPieceBoard.setMouseTransparent(true);
        pieceHandler = new pieceLocationHandler(GameOver, eatenWhites, eatenBlacks,chessPieceBoard);
        chessAiForBestMove = new Computer(pieceHandler,5);
        chessAiForEvalBar = new Computer(pieceHandler,5);
        logger.debug("initializing Main Screen");
        evalTask = new evaluationbartask(chessAiForEvalBar,this,4);
        new Thread(evalTask).start();
        computerTask = new chessComputerTask(chessAiForBestMove,this);
        new Thread(computerTask).start();
        chessPieceBoard.prefWidthProperty().bind(chessPieceBoard.heightProperty());

        whiteadvantage.heightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        blackadvantage.heightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        eatenBlacks.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty()));
        eatenWhites.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty()));
        reset.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty().multiply(2)));
        LeftButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
        LeftButton.prefHeightProperty().bind(LeftButton.widthProperty());
        RightButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
        gameControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
        settingsScreen.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
        topControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
        bottomControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
        RightButton.prefHeightProperty().bind(RightButton.widthProperty());
        player1Select.fitHeightProperty().bind(eatenBlacks.heightProperty());
        player2Select.fitHeightProperty().bind(eatenBlacks.heightProperty());
        player1Select.fitWidthProperty().bind(player1Select.fitHeightProperty());
        player2Select.fitWidthProperty().bind(player2Select.fitHeightProperty());
        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
        player1Select.setImage(images[0]);
        player2Select.setImage(images[0]);
        initLabelBindings(bgLabel);
        initLabelBindings(pieceLabel);



        initPlayerSelector(true,player1Selector);
        initPlayerSelector(false,player2Selector);

        test.setMouseTransparent(true);
        victoryLabel.setMouseTransparent(true);



        setPromoPeices(true,false);
        bgColorSelector.getItems().addAll(
                "Traditional",
                "Ice", "Halloween", "Summer","Cherry"
        );
        bgColorSelector.setOnAction(e ->{
            changeChessBg(bgColorSelector.getValue());
        });
        bgColorSelector.getSelectionModel().selectFirst();

        pieceSelector.getItems().addAll(
                "Traditional",
                "Ice", "Halloween", "Summer","Cherry"
        );
        pieceSelector.setOnAction(e ->{
            changeChessBg(bgColorSelector.getValue());
        });

        pieceSelector.getSelectionModel().selectFirst();


        evalSelector.getItems().addAll(
                1,2,3,4,5,6,7,8
        );
        evalSelector.setOnAction(e ->{
            chessAiForBestMove.setEvalDepth(evalSelector.getValue());
        });

        evalSelector.getSelectionModel().select(4);


        settingsButton.setOnMouseClicked(e -> {
            toggleSettings();
            toggleGameControls();

        });
        settingsButtonToggled.setOnMouseClicked(e -> {
            toggleSettings();
            toggleGameControls();

        });
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane stackpane = new StackPane();
                StackPane Bgstackpane = new StackPane();

                Bgstackpane.setUserData(i + "," + j);
                chessBoard.add(stackpane, i, j);
                chessBgBoard.add(Bgstackpane, i, j);
                setUpSquareClickEvent(Bgstackpane);
                Bgpanes[i][j] = Bgstackpane;

            }
        }
        changeChessBg("Traditional");
        setUpChessPieces(chessPieceBoard);
        homeButton.setOnMouseClicked(e -> {
            changeMove(0,true);
            App.changeScene(true);
        });
        LeftButton.setOnMouseClicked(e -> {
            if(pieceHandler.moveIndx >= 0){
                changeMove(-1,false);
                updateEvalThread(evalTask, pieceHandler);
                evalTask.evalRequest();

            }


        });

        RightButton.setOnMouseClicked(e -> {
            if(pieceHandler.moveIndx < pieceHandler.maxIndex){
                changeMove(1,false);
                updateEvalThread(evalTask, pieceHandler);
                evalTask.evalRequest();


            }
        });

        reset.setOnMouseClicked(e ->{
            if(pieceHandler.moveIndx != -1){
                changeMove(0,true);
                updateEvalThread(evalTask, pieceHandler);
                evalTask.evalRequest();
            }
        });
        hidePromo();
        hideSettings();
        players = new ImageView[]{player1Select,player2Select};



    }
    boolean[] currentStates = {true,true};
    ImageView[] players;
    Image[] images = new Image[]{new Image("/PlayerIcons/defaultpfp.png"), new Image("/PlayerIcons/robot.png")};
    private void initPlayerSelector(boolean isPlayer1, ComboBox<String> box){
        box.getItems().addAll(
            isPlayer1 ? "Player1" : "Player2","Computer"
        );
        box.getSelectionModel().selectFirst();
        box.prefWidthProperty().bind(player1Select.fitWidthProperty().multiply(2));
        box.prefHeightProperty().bind(player1Select.fitHeightProperty());
        box.setOnAction(e ->{
            if(box.getValue().equals("Computer")){
                changePlayer(isPlayer1,false);
//                logger.debug("Changing to computer as " + (isPlayer1 ? "Player 1" : "Player 2"));
            }
            else{
                changePlayer(isPlayer1,true);
//                logger.debug("Changing to player as " + (isPlayer1 ? "Player 1" : "Player 2"));

            }
        });
    }

    private void changePlayer(boolean isFirstPlayer, boolean isPlayer){
        int indx = isFirstPlayer ? 0 : 1;
        int isPindx = isPlayer ? 0 : 1;
        // very bad naming scheme
        // cant cast boolean to int :(
        if(isPlayer != currentStates[indx]){
            // only change if not already current state
            players[indx].setImage(images[isPindx]);
            currentStates[indx] = isPlayer;
        }
    }

    private void initLabelBindings(Label l){
        l.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(2));
//        l.fontProperty().bind();
    }

    // toggle the pawn promotion screen
    private void togglePromo(){
        promotionScreen.setMouseTransparent(!promotionScreen.isMouseTransparent());
        promotionScreen.setVisible(!promotionScreen.isVisible());
    }

    private void toggleSettings(){
        settingsScreen.setMouseTransparent(!settingsScreen.isMouseTransparent());
        settingsScreen.setVisible(!settingsScreen.isVisible());

    }

    private void toggleGameControls(){
        gameControls.setMouseTransparent(!gameControls.isMouseTransparent());
        gameControls.setVisible(!gameControls.isVisible());

    }

    private void hidePromo(){
        promotionScreen.setMouseTransparent(true);
        promotionScreen.setVisible(false);
    }

    private void hideSettings(){
        settingsScreen.setMouseTransparent(true);
        settingsScreen.setVisible(false);
    }
    // change promotion peice colors if needed
    boolean lastPromoWhite = false;
    private void setPromoPeices(boolean isWhite, boolean isExplore){
        if(isExplore){
            // later todo!!
        }
        else{
            if(lastPromoWhite != isWhite){
                // want different color promo peices
                promoContainer.getChildren().clear();
                for(int i = 1;i<5;i++){
                    ImageView piece = new ImageView(chessBoardGUIHandler.createPeicePath(i,isWhite));
                    piece.fitHeightProperty().bind(promoContainer.heightProperty().divide(2));
                    piece.fitWidthProperty().bind(promoContainer.widthProperty().divide(4));
                    piece.setPreserveRatio(true);
                    setUpPromoListener(piece,i);
                    promoContainer.getChildren().add(piece);
                }
                lastPromoWhite = isWhite;
            }

        }
    }

    // set up the onlick listeners for the pieces a pawn can promote too
    private void setUpPromoListener(ImageView promo,int peiceType){
        promo.setOnMouseClicked(event ->{
            promoPawn(peiceType);
            togglePromo();
        });

    }
    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset){
        if(isReset){
            pieceHandler.moveIndx = -1;
            pieceHandler.maxIndex = -1;
            pieceHandler.clearIndx();
            updateEvalThread(evalTask, pieceHandler);
            evalTask.evalRequest();
        }
        else{
            pieceHandler.updateMoveIndex(direction);
        }
        saveIndicator.setText((pieceHandler.moveIndx + 1 )+ "/" + (pieceHandler.maxIndex+1));
        GameOver = pieceHandler.moveIndx >= gameEndIndx;
        pieceHandler.ChangeBoard(peicesAtLocations,isWhiteTurn, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
        unselectEveryThing();
    }

    // draw the eval bar for the screen
    public void setEvalBar(double advantage, int depth){
        setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,advantage);
        evalDepth.setText(Integer.toString(depth));
    }
    private void setEvalBar(Label whiteEval, Label blackEval, Rectangle whiteBar, Rectangle blackBar, double advantage){
        double barModPercent = passThroughAsymptote(Math.abs(advantage))/5;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if(advantage >= 0){
            // white advantage or equal position
            if(advantage < 1000000){
                whiteEval.setText(decimalFormat.format(advantage));
            }
            else{
                whiteEval.setText("M");

            }
            blackEval.setText("");
            whiteBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).add(chessPieceBoard.heightProperty().divide(2).multiply(barModPercent)));
            blackBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).multiply(1-barModPercent));
            
        }
        else{
            if(advantage > -1000000){
                blackEval.setText(decimalFormat.format(advantage));
            }
            else{
                blackEval.setText("M");

            }
            whiteEval.setText("");
            blackBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).add(chessPieceBoard.heightProperty().divide(2).multiply(barModPercent)));
            whiteBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).multiply(1-barModPercent));


        }


    }
    // make the growth of the eval bar nonlinear
    private double passThroughAsymptote(double advantage){
        return (5 * Math.pow(advantage,2))/(Math.pow(advantage,2)+0.5*advantage + 10);
    }
    // remove all square highlights
    public void unselectEveryThing()
    {
        if(peiceSelected){
            removeHiglight(selectedPeiceInfo[0],selectedPeiceInfo[1]);
        }
        peiceSelected = false;
        if(oldHighights != null){
            for(XYcoord s : oldHighights){
                removeHiglight(s.x,s.y);
            }
        }

    }
    // pretty self explanatory
    private void changeChessBg(String colorType) {

        boolean isLight = true;
        highlightColor = colorType;
        String[] clrStr = getColorStr(colorType);
        String curr = "";
        int count = 0;

        for (Node n : chessBoard.getChildren()) {
            if (isLight) {
                curr = clrStr[0];
                isLight = false;
            } else {
                curr = clrStr[1];
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
    private String[] getColorStr(String colortype) {
        return switch (colortype) {
            case "Ice" -> new String[]{"#7FDEFF", "#4F518C"};
            case "Traditional" -> new String[]{"#9e7a3a", "#2e120b"};
            case "Halloween" -> new String[]{"#ff6619", "#241711"};
            case "Summer" -> new String[]{"#f7cc0a", "#22668D"};
            case "Cherry" -> new String[]{"#f7b2ad", "#8c2155"};
            default -> null;
        };
    }
    // put the default chess peices on the board with their proper colors etc
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
                    peicesAtLocations[pieceX][pieceY] = piece;

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
    // method to actually handle moving a peice from one place to another
    // looks a little weird because it handles computer moves and also player moves
    private void MoveAPeice(int startX, int startY, int endX, int endY, boolean isEating, boolean prevPieceColor, boolean isCastle, boolean isComputerMove,int promoIndx) {
        boolean[] moveInfo = new boolean[]{false, false};

        if (!isComputerMove) {
            moveInfo = checkIfMovePossible(oldHighights, endX, endY);
        }

        if (isComputerMove || moveInfo[0]) {
            if (!isComputerMove) {
                for (XYcoord s : oldHighights) {
                    removeHiglight(s.x, s.y);
                }
            }

            // super proud of progress so far :))))

            //logger.debug"Is castle?" + moveInfo[1]);

            if (isCastle || moveInfo[1]) {
                int jump = endX == 6 ? 1 : -1;
                pieceHandler.removeRookMoveRight(endX + jump, endY);
                pieceHandler.movePiece(prevPieceColor, endX + jump, endY, endX - jump, endY, true, false, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
                peicesAtLocations[endX - jump][endY] = peicesAtLocations[endX + jump][endY];
                GridPane.setRowIndex(peicesAtLocations[endX - jump][endY], endY);
                GridPane.setColumnIndex(peicesAtLocations[endX - jump][endY], endX - jump);
                peicesAtLocations[endX + jump][endY] = null;
                pieceHandler.removeCastlingRight(prevPieceColor);
                logger.debug("Castle right black: " + pieceHandler.blackCastleRight);
                logger.debug("Short rook black: " + pieceHandler.blackShortRookMove);
            }

            int pieceIndex = chessFunctions.getBoardWithPiece(startX, startY, prevPieceColor, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);

            if (pieceIndex == 5) {
                pieceHandler.removeCastlingRight(prevPieceColor);
            }
            else if(pieceIndex == 4){
                if (startX == 7 && startY == 7 || startX == 0 && startY == 7 || startX == 0 && startY == 0 || startX == 7 && startY == 0) {
                    pieceHandler.removeRookMoveRight(startX, startY);
                }
            }

            boolean isPromo = pieceIndex == 0 && endY == (isWhiteTurn ? 0 : 7);
            removeHiglight(startX, startY);
            peicesAtLocations[startX][startY] = null;

            if(!isPromo || isComputerMove){
                pieceHandler.movePiece(prevPieceColor, startX, startY, endX, endY, isEating, false, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
                if (isEating) {
                    pieceHandler.removePeice(!prevPieceColor, endX, endY, false, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC,isPromo);
                    chessPieceBoard.getChildren().remove(peicesAtLocations[endX][endY]);
                }
                if(isPromo){
                    // remove pawn and create new peice
                    chessBoardGUIHandler.removeFromGridPane(startX,startY,chessPieceBoard);
                    ImageView promoPeice = chessBoardGUIHandler.createNewPeice(promoIndx,isWhiteP,chessPieceBoard,false);
                    peicesAtLocations[endX][endY] = promoPeice;
                    chessPieceBoard.add(promoPeice,endX,endY);
                    pieceHandler.promoPawn(isWhiteTurn,startX,startY,endX,endY,promoIndx, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);

                }

                GridPane.setRowIndex(selectedPeice, endY);
                GridPane.setColumnIndex(selectedPeice, endX);
                peicesAtLocations[endX][endY] = selectedPeice;
                peiceSelected = false;
            }
            else
            {
                chessBoardGUIHandler.removeFromGridPane(startX,startY,chessPieceBoard);
                pawnX = endX;
                pawnY = endY;
                OpawnX = startX;
                OpawnY = startY;
                isWhiteP = isWhiteTurn;
                setPromoPeices(prevPieceColor, false);
                togglePromo();

            }



        }
    }


    int pawnX;
    int pawnY;

    int OpawnX;
    int OpawnY;

    boolean isWhiteP;




    private void promoPawn(int peiceIndx){
        logger.info("Promoting pawn at " + pawnX + "," + pawnY + " to a " + chessFunctions.getPieceType(peiceIndx));
        ImageView promoPeice = chessBoardGUIHandler.createNewPeice(peiceIndx,isWhiteP,chessPieceBoard,false);
        ImageView oldPeice = peicesAtLocations[pawnX][pawnY];
        if(oldPeice != null){
            chessPieceBoard.getChildren().remove(oldPeice);
        }
        peicesAtLocations[pawnX][pawnY] = promoPeice;

        chessPieceBoard.add(promoPeice,pawnX,pawnY);

        pieceHandler.promoPawn(isWhiteP,OpawnX,OpawnY,pawnX,pawnY,peiceIndx, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
    }
    // what actually happens when you click a square on the board
    private boolean waitingForComputer = false;
    public void makeComputerMove(chessMove move){
        logger.info("Looking at best move for " + (isWhiteTurn ? "WhitePeices" : "BlackPeices"));
        logger.info("Computer thinks move: \n" + move.toString());
        // computers move
        selectedPeice = peicesAtLocations[move.getOldX()][move.getOldY()];
        // since when eating a piece you have to change visuals, need to hanndle it differently
        if(move.isEating()){
            Platform.runLater(() -> {
                MoveAPeice(move.getOldX(),move.getOldY(),move.getNewX(),move.getNewY(),move.isEating(),false,move.isCastleMove(),true,move.getPromoIndx());
            });
        }
        else{
            MoveAPeice(move.getOldX(),move.getOldY(),move.getNewX(),move.getNewY(),move.isEating(),false,move.isCastleMove(),true,move.getPromoIndx());
        }
        isWhiteTurn = !isWhiteTurn;
        waitingForComputer = false;

        updateEvalThread(evalTask, pieceHandler);
        evalTask.evalRequest();
    }

    private void setUpSquareClickEvent(StackPane square) {
        square.setOnMouseClicked(event -> {
            logger.info("Square clicked");
            StackPane pane = (StackPane) event.getSource();
            String[] xy = pane.getUserData().toString().split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            //logger.info("X: " + x);
            //logger.info("Y: " + y);
            if (event.getButton() == MouseButton.PRIMARY && !GameOver && !waitingForComputer) {
                boolean[] boardInfo = chessFunctions.checkIfContains(x, y, pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
                //logger.debug("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                // possible move
                if (peiceSelected) {
                    boolean prevPeiceClr = (selectedPeiceInfo[2] > 0);
                    int oldX = selectedPeiceInfo[0];
                    int oldY = selectedPeiceInfo[1];

                    boolean canMove = checkIfMovePossible(oldHighights,x,y)[0];
                    if ((!boardInfo[0] || prevPeiceClr != boardInfo[1]) && canMove) {
                        // enemy colors or empty square

                        logger.debug("Moving " + prevPeiceClr + " peice from " + oldX + "," + oldY + " to " + x + "," + y);
                        MoveAPeice(oldX,oldY,x,y,boardInfo[0],prevPeiceClr,false,false,-10);


                        isWhiteTurn = !isWhiteTurn;

                        saveIndicator.setText((pieceHandler.moveIndx + 1) + "/" + (pieceHandler.maxIndex+1));

                        oldHighights = null;
                        if(pieceHandler.isCheckmated(pieceHandler.whitePiecesC, pieceHandler.blackPiecesC)){
                            double eval = chessAiForBestMove.getFullEval(pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
                            victoryLabel.setText("Winner : " + (eval > 0 ? "White" : "Black"));
                            setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,eval);
                            GameOver = true;
                            gameEndIndx = pieceHandler.moveIndx;

                        }

                        if(isVsComputer){
                            waitingForComputer = true;
                            updateCompThread(computerTask,pieceHandler);
                            computerTask.evalRequest();

                        }

                        updateEvalThread(evalTask, pieceHandler);
                        evalTask.evalRequest();




                    }
                    else if(boardInfo[0] && isWhiteTurn == boardInfo[1]){
                        // your own peice color

                        int clr = (boardInfo[1]) ? 1 : -1;
                        selectedPeiceInfo[0] = x;
                        selectedPeiceInfo[1] = y;
                        selectedPeiceInfo[2] = clr;
                        selectedPeice = peicesAtLocations[x][y];

                        removeHiglight(oldX, oldY);
                        if(oldHighights != null){
                            for(XYcoord s : oldHighights){
                                removeHiglight(s.x,s.y);
                            }
                        }
                        if(oldX != x || oldY != y){
                            highlightSquare(x, y, true);
                            List<XYcoord> highlightLocations = pieceHandler.getPossibleMoves(x,y,boardInfo[1], pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
                            oldHighights = highlightLocations;
                            for(XYcoord s : highlightLocations){
                                highlightSquare(s.x,s.y,false);
                            }
                        }
                        else {
                            peiceSelected = false;
                            oldHighights = null;
                        }

                        }
                    else if(!boardInfo[0]){
                        removeHiglight(oldX, oldY);

                        if(oldHighights != null){
                            for(XYcoord s : oldHighights){
                                removeHiglight(s.x,s.y);
                            }
                        }
                        peiceSelected = false;
                        oldHighights = null;
                    }
                    }

                else if(boardInfo[0] && boardInfo[1] == isWhiteTurn){
                    // no prev selection



                        List<XYcoord> moves = pieceHandler.getPossibleMoves(x,y,boardInfo[1], pieceHandler.whitePiecesC, pieceHandler.blackPiecesC);
                        oldHighights = moves;
                        for(XYcoord s : moves){
                            highlightSquare(s.x,s.y,false);
                        }



                        peiceSelected = true;

                        int clr = (boardInfo[1]) ? 1 : -1;
                        selectedPeiceInfo[0] = x;
                        selectedPeiceInfo[1] = y;
                        selectedPeiceInfo[2] = clr;
                        selectedPeice = peicesAtLocations[x][y];




                        highlightSquare(x, y, true);


                }
//                if(peiceSelected){
//                    //logger.debug("PrevPeice Selected x: " + selectedPeiceInfo[0]);
//                    //logger.debug("PrevPeice Selected y: " + selectedPeiceInfo[1]);
//                    //logger.debug("PrevPeice Selected isWhite: " + selectedPeiceInfo[2]);
//                }





            } else if (event.getButton() == MouseButton.SECONDARY) {
                highlightSquare(x, y, false);
            }

        });
    }
    // toggle square highlight with two different types of colors
    // todo: make square highlights different depending on background color
    private void highlightSquare(int x, int y, boolean isPieceSelection) {
        if (isPieceSelection) {
            highlightColor = "rgba(44, 212, 255, 0.25)";
        } else {
            highlightColor = "rgba(223, 90, 37, 0.4)";
        }
        if (!Bgpanes[x][y].getStyle().contains("rgba")) {
            Bgpanes[x][y].setStyle("-fx-background-color: " + highlightColor);
        } else {
            Bgpanes[x][y].setStyle("-fx-background-color: transparent");

        }
    }
    // fixed method to clear any highlights
    private void removeHiglight(int x, int y){
        Bgpanes[x][y].setStyle("-fx-background-color: transparent");

    }
    // checks if square player wants to move is in the moves allowed for that peice
    private boolean[] checkIfMovePossible(List<XYcoord> moves, int x, int y){
        // todo: change this to bitboard logic
        if(moves != null) {
            for (XYcoord s : moves) {
                if ((s.x == x && s.y == y) && s.isCastleMove()) {
                    return new boolean[]{true, true};
                } else if (s.x == x && s.y == y) {


                    return new boolean[]{true, false};
                }
            }
        }
        return new boolean[]{false,false};


    }

    private void updateEvalThread(evaluationbartask c, pieceLocationHandler p){
        c.currentWhiteBoard = p.whitePiecesC;
        c.currentBlackBoard = p.blackPiecesC;
        c.currentIsWhite = isWhiteTurn;
    }
    private void updateCompThread(chessComputerTask c, pieceLocationHandler p){
        c.currentWhiteBoard = p.whitePiecesC;
        c.currentBlackBoard = p.blackPiecesC;
        c.currentIsWhite = isWhiteTurn;
    }
}
