package chessengine;

import chessserver.ChessboardTheme;
import chessserver.INTENT;
import chessserver.UserPreferences;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {

    public UserPreferences initPreferences = null;

    StackPane[][] Bgpanes = new StackPane[8][8];
    private ImageView[][] peicesAtLocations = new ImageView[8][8];

    @FXML
    StackPane fullScreen;
    @FXML
    GridPane content;
    @FXML
    HBox sideAreaFull;
    @FXML
    VBox leftSideSpacer;

    @FXML
    HBox topRightInfo;


    @FXML
    Pane mainMessageBoard;

    @FXML
    Button LeftButton;

    @FXML
    Button RightButton;

    @FXML
    Label stateLabel;

    @FXML
    Button settingsButton;

    @FXML
    StackPane chessBoardContainer;

    @FXML
    GridPane chessHighlightBoard;

    @FXML
    GridPane chessBgBoard;

    @FXML
    Pane arrowBoard;

    @FXML
    GridPane promotionScreen;

    @FXML
    public Pane chessPieceBoard;



    @FXML
    public VBox gameoverMenu;

    @FXML
    public Label gameoverTitle;

    @FXML
    public Button gameoverHomebutton;




    @FXML
    public Label saveIndicator;

    @FXML
    public Button reset;

    @FXML
    VBox evalBar;

    @FXML
    StackPane evalContainer;

    @FXML
    VBox evalLabelBox;

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
    GridPane settingsScreen;

    @FXML
    VBox onlineControls;

    @FXML
    VBox viewerControls;

    @FXML
    VBox localControls;

    @FXML
    VBox sandboxControls;

    @FXML
    VBox bestMovesBox;
    @FXML
    GridPane sandboxPieces;

    @FXML
    TextArea localInfo;




    @FXML
    ImageView player1Select;

    @FXML
    ImageView player2Select;

    @FXML
    VBox gameControls;

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
    Label evalLabel;

    @FXML
    Label pgnSaveLabel;

    @FXML
    Button pgnSaveButton;

    @FXML
    Label audioLabel;

    @FXML
    Slider audioSlider;

    @FXML
    Label player1Label;

    @FXML
    Label player2Label;

    @FXML
    Label BlackNumericalAdv;

    @FXML
    Label WhiteNumericalAdv;

    @FXML
    TextArea inGameInfo;

    @FXML
    TextField chatInput;

    @FXML
    Button sendMessageButton;







    private Logger logger;


    public chessengine.ChessCentralControl getChessCentralControl() {
        return ChessCentralControl;
    }

    private ChessCentralControl ChessCentralControl;

    public void endAsync(){
        if(this.ChessCentralControl != null){
            ChessConstants.mainLogger.debug("Killing threads");
            ChessCentralControl.asyncController.killAll();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        mainMessageBoard.setMouseTransparent(true);
        logger = LogManager.getLogger(this.toString());

        logger.debug("initializing Main Screen");
        setUpPiecesAndListeners();
        ChessCentralControl = App.getCentralControl();

        ChessCentralControl.init(chessPieceBoard,eatenWhites,eatenBlacks,peicesAtLocations,inGameInfo,arrowBoard,bestMovesBox,localInfo,sandboxPieces,chatInput,sendMessageButton,Bgpanes,chessHighlightBoard);

        // some elements need mouse transparency because they are on top of control elements
        chessPieceBoard.setMouseTransparent(true);
        arrowBoard.setMouseTransparent(true);
        victoryLabel.setMouseTransparent(true);
        setPromoPeices(true);
        players = new ImageView[]{player1Select,player2Select};
        setUpBindings();
        setUpButtons();
        setUpSettingScreenMain();
        setUpDragAction();

        if(initPreferences != null){
            ChessCentralControl.asyncController.setComputerDepth(initPreferences.getComputerMoveDepth());
            ChessCentralControl.asyncController.setComputerDepth(initPreferences.getComputerMoveDepth());
            ChessCentralControl.chessBoardGUIHandler.changeChessBg(initPreferences.getChessboardTheme().toString());
        }
        setEvalBar(0,0,false,false);
        bs = new Region[]{chessBgBoard,chessPieceBoard,arrowBoard,chessHighlightBoard};

    }
    int bindx = 0;
    Region[] bs;
    String[] names = new String[]{"bg","pb","arrb","hb"};
    private void clearB(){
        for(Region r : bs){
            r.setStyle("");
        }
    }
    private void switchB(){
        clearB();
        bs[bindx].setStyle("-fx-background-color: rgba(100,200,100,0.2)");
        App.messager.sendMessage(names[bindx],false, Duration.seconds(2));
        bindx++;
        if(bindx >= bs.length){
            bindx = 0;
        }
    }


    private void setUpDragAction(){
        chessBgBoard.setOnMouseDragged(e->{
            ChessCentralControl.chessActionHandler.handleBoardDrag(e);
        });
        chessBgBoard.setOnMouseReleased(e->{
            ChessCentralControl.chessActionHandler.handleBoardRelease(e,currentState);

        });
        chessBgBoard.setOnMousePressed(e->{
            ChessCentralControl.chessActionHandler.handleBoardPress(e,currentState);
        });
    }

    private void setUpPiecesAndListeners(){for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            StackPane stackpane = new StackPane();
            StackPane Bgstackpane = new StackPane();

            Bgstackpane.setUserData(i + "," + j);
            chessHighlightBoard.add(stackpane, i, j);
            chessBgBoard.add(Bgstackpane, i, j);
            setUpSquareClickEvent(Bgstackpane);
            Bgpanes[i][j] = Bgstackpane;

        }

    }

    }

    private void setUpSettingScreenMain(){
        bgColorSelector.getItems().addAll(Arrays.stream(ChessboardTheme.values()).map(ChessboardTheme::toString).toList());
        bgColorSelector.setOnAction(e ->{
            ChessCentralControl.chessBoardGUIHandler.changeChessBg(bgColorSelector.getValue());
        });
        bgColorSelector.getSelectionModel().selectFirst();

        pieceSelector.getItems().addAll(
                Arrays.stream(ChessboardTheme.values()).map(ChessboardTheme::toString).toList()
        );
        pieceSelector.setOnAction(e ->{
            // todo
            ChessCentralControl.chessBoardGUIHandler.changeChessBg(pieceSelector.getValue());

        });

        pieceSelector.getSelectionModel().selectFirst();


        evalSelector.getItems().addAll(
                1,2,3,4,5,6,7,8
        );
        evalSelector.setOnAction(e ->{
            ChessCentralControl.asyncController.setComputerDepth(evalSelector.getValue());
        });

        evalSelector.getSelectionModel().select(4);

        audioSlider.valueProperty().addListener(e ->{
            double volume = audioSlider.getValue()/100;
            App.soundPlayer.changeVolumeEffects(volume);
        });
        audioSlider.setValue(80);
    }

    private void setUpButtons(){

        setupresetToHome(gameoverHomebutton);
        setupresetToHome(homeButton);




        pgnSaveButton.setOnMouseClicked(e ->{
            String fileName = ChessCentralControl.gameHandler.currentGame.getGameName() + ".txt";
            String pgn = ChessCentralControl.gameHandler.currentGame.gameToPgn();
            logger.debug("Saving game");
            GeneralChessFunctions.saveToFile(fileName,pgn);
        });

        settingsButton.setOnMouseClicked(e -> {
            toggleSettingsAndGameControls();
        });

        LeftButton.setOnMouseClicked(e -> {
            logger.debug("Right button clicked");
            if(ChessCentralControl.gameHandler.currentGame.curMoveIndex >= 0){
                changeMove(-1,false);

            }


        });

        RightButton.setOnMouseClicked(e -> {
            logger.debug("Right button clicked");
            if(ChessCentralControl.gameHandler.currentGame.curMoveIndex < ChessCentralControl.gameHandler.currentGame.maxIndex){
                changeMove(1,false);



            }
        });

        reset.setOnMouseClicked(e ->{
            if(ChessCentralControl.gameHandler.currentGame.curMoveIndex != -1){
                changeMove(0,true);
            }
        });
    }

    private void setupresetToHome(Button gameoverHomebutton) {
        gameoverHomebutton.setOnMouseClicked(e ->{
            if(ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && !currentState.equals(MainScreenState.VIEWER) && !currentState.equals(MainScreenState.SANDBOX)){
                PersistentSaveManager.appendGameToAppData(ChessCentralControl.gameHandler.currentGame);
            }
            if(ChessCentralControl.gameHandler.currentGame.isWebGame()){
                ChessCentralControl.gameHandler.currentGame.leaveWebGame();
            }
            ChessCentralControl.chessActionHandler.reset();
            ChessCentralControl.chessBoardGUIHandler.removeAllPieces();
            ChessCentralControl.asyncController.stopAll();
            setEvalBar(0,-1,false,false);

            App.changeToStart();
        });
    }

    public void processChatInput() {
        App.soundPlayer.playEffect(Effect.MESSAGE);
        ChessCentralControl.chessActionHandler.appendNewMessageToChat("("+ ChessCentralControl.gameHandler.currentGame.getPlayer1name() + ") " + chatInput.getText());
        if(ChessCentralControl.gameHandler.currentGame.isWebGame() && ChessCentralControl.gameHandler.currentGame.isWebGameInitialized()){
            App.webclient.sendRequest(INTENT.SENDCHAT,chatInput.getText());
        }
        chatInput.clear();
    }

    public void setUpBindings(){
        // chess board
        content.prefWidthProperty().bind(fullScreen.widthProperty());
        content.prefHeightProperty().bind(fullScreen.heightProperty());

        chessBoardContainer.prefHeightProperty().bind(fullScreen.heightProperty().subtract(eatenBlacks.heightProperty().multiply(2)));
//        chessBoardContainer.prefWidthProperty().bind(chessBoardContainer.heightProperty().multiply(1.1));
        chessBoardContainer.prefWidthProperty().bind(content.widthProperty().multiply(.6));
        chessPieceBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        chessPieceBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        arrowBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        arrowBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        // side panel

        // eval bar related

        evalBar.prefHeightProperty().bind(chessHighlightBoard.heightProperty());
        evalContainer.prefHeightProperty().bind(chessHighlightBoard.heightProperty());
        evalLabelBox.spacingProperty().bind(evalLabelBox.heightProperty().divide(3));

        // sidepanel stuff
        sidePanel.prefHeightProperty().bind(chessPieceBoard.heightProperty());
        BindingController.bindSmallText(stateLabel,true);

        BindingController.bindChildTextToParentWidth(chessPieceBoard,victoryLabel,.2);
        BindingController.bindChildTextToParentWidth(chessPieceBoard,player1Label,.4);
        BindingController.bindChildTextToParentWidth(chessPieceBoard,player2Label,.4);
        BindingController.bindChildTextToParentWidth(chessPieceBoard,WhiteNumericalAdv,.2);
        BindingController.bindChildTextToParentWidth(chessPieceBoard,BlackNumericalAdv,.2);
        arrowBoard.prefWidthProperty().bind(chessPieceBoard.widthProperty());
        arrowBoard.prefHeightProperty().bind(chessPieceBoard.heightProperty());
        whiteadvantage.heightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        blackadvantage.heightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        eatenBlacks.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty()));
        eatenWhites.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty()));
//        reset.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty().multiply(2)));
//        LeftButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
//        LeftButton.prefHeightProperty().bind(LeftButton.widthProperty());
//        RightButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
//        gameControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        settingsScreen.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        topControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        bottomControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        RightButton.prefHeightProperty().bind(RightButton.widthProperty());
        player1Select.fitHeightProperty().bind(eatenBlacks.heightProperty());
        player2Select.fitHeightProperty().bind(eatenBlacks.heightProperty());
        player1Select.fitWidthProperty().bind(player1Select.fitHeightProperty());
        player2Select.fitWidthProperty().bind(player2Select.fitHeightProperty());
//        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        player1Select.setImage(images[0]);
//        player2Select.setImage(images[0]);
        initLabelBindings(bgLabel);
        initLabelBindings(pieceLabel);
        initLabelBindings(evalLabel);
//
        player1Label.prefWidthProperty().bind(sidePanel.widthProperty().divide(2));
        player2Label.prefWidthProperty().bind(sidePanel.widthProperty().divide(2));
    }
    // methods called every new game
    public MainScreenState currentState;

    public void setUp(boolean isVsComputer,String gameName,ChessGame gameToSetup,String player1Name,int player1Elo,MainScreenState currentState){
        this.currentState = currentState;
        setMainControls(currentState);
        checkHideEvalBar(currentState);
        ChessCentralControl.chessBoardGUIHandler.clearHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        clearSimpleAdvantageLabels();
        hidePromo();
        hideSettings();
        showGameControlls();
        hideGameOver();
        ChessCentralControl.chessBoardGUIHandler.resetEverything();
        
        
        boolean isVsComputerReal = gameToSetup != null ? gameToSetup.isVsComputer() : isVsComputer;
        setUpIcons(isVsComputerReal);
        if(Objects.nonNull(gameToSetup)){
            if(currentState.equals(MainScreenState.VIEWER)){
                // in viewer mode, you will have a old game that you dont want to modify, however you still want to play around with a temporary copy
                ChessCentralControl.gameHandler.switchToGame(gameToSetup.cloneGame());
            }
            ChessCentralControl.gameHandler.switchToGame(gameToSetup);

        }
        else{
            ChessCentralControl.gameHandler.switchToNewGame(new ChessGame(gameName,isVsComputer));
        }
        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex,ChessCentralControl.gameHandler.currentGame.maxIndex);
        setPlayerLabels(player1Name,player1Elo,isVsComputerReal ? "Computer" : "",isVsComputerReal ? 100000 : 0);
        

    }

    private void checkHideEvalBar(MainScreenState currentState) {
        if(currentState.equals(MainScreenState.ONLINE) || currentState.equals(MainScreenState.LOCAL)){
            // hidden as these are real games
            evalBar.setVisible(false);
        }
        else{
            evalBar.setVisible(true);
        }
    }

    private void setUpViewerPanel() {

    }

    private void setUpOnlinePanel() {
    }



    ImageView[] players;
    Image[] images = new Image[]{new Image("/PlayerIcons/defaultpfp.png"), new Image("/PlayerIcons/robot.png")};


    private void setUpIcons(boolean isVsComputer){
        player1Select.setImage(images[0]);
        player2Select.setImage(isVsComputer ? images[1] : images[0]);
    }

    public void setPlayerLabels(String player1Name,int player1Elo, String player2Name,int player2Elo){
        player1Label.setText(player1Name + " " + Integer.toString(player1Elo));
        player2Label.setText(player2Name + " " + Integer.toString(player2Elo));
    }

    private void initLabelBindings(Label l){
        l.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(2));
    }

    // toggle the pawn promotion screen
    public void togglePromo(){
        promotionScreen.setMouseTransparent(!promotionScreen.isMouseTransparent());
        promotionScreen.setVisible(!promotionScreen.isVisible());
    }

    private void hidePromo(){
        promotionScreen.setMouseTransparent(true);
        promotionScreen.setVisible(false);
    }

    private void toggleGameOver(){
        gameoverMenu.setMouseTransparent(!gameoverMenu.isMouseTransparent());
        gameoverMenu.setVisible(!gameoverMenu.isVisible());

    }

    private void hideGameOver(){
        gameoverMenu.setMouseTransparent(true);
        gameoverMenu.setVisible(false);
    }


    private void showGameOver(){
        gameoverMenu.setMouseTransparent(false);
        gameoverMenu.setVisible(true);
    }
    private void toggleSettingsAndGameControls(){
        settingsScreen.setMouseTransparent(!settingsScreen.isMouseTransparent());
        settingsScreen.setVisible(!settingsScreen.isVisible());
        gameControls.setVisible(!gameControls.isVisible());
        gameControls.setMouseTransparent(!gameControls.isMouseTransparent());

    }

    private void hideSettings(){
        settingsScreen.setMouseTransparent(true);
        settingsScreen.setVisible(false);
    }



    private void showGameControlls(){
        gameControls.setMouseTransparent(false);
        gameControls.setVisible(true);
    }

    private VBox currentControls;
    private void setMainControls(MainScreenState currentState){
        hideAllControls();
        switch (currentState){
            case VIEWER -> {
                currentControls = viewerControls;
                stateLabel.setText("Viewer");
            }
            case LOCAL -> {
                currentControls = localControls;
                stateLabel.setText("Local Game");
            }
            case ONLINE -> {
                currentControls = onlineControls;
                stateLabel.setText("Online Game");
            }
            case SANDBOX -> {
                currentControls = sandboxControls;
                stateLabel.setText("Sandbox Mode");
            }
        }
        currentControls.setMouseTransparent(false);
        currentControls.setVisible(true);
    }

    private void hideAllControls(){
        viewerControls.setMouseTransparent(true);
        viewerControls.setVisible(false);
        localControls.setMouseTransparent(true);
        localControls.setVisible(false);
        onlineControls.setMouseTransparent(true);
        onlineControls.setVisible(false);
        sandboxControls.setMouseTransparent(true);
        sandboxControls.setVisible(false);
    }






    // change promotion peice colors if needed
    boolean lastPromoWhite = false;
    private void setPromoPeices(boolean isWhite){
        if(lastPromoWhite != isWhite){
            // want different color promo peices
            promoContainer.getChildren().clear();
            for(int i = 1;i<5;i++){
                ImageView piece = new ImageView(ChessCentralControl.chessBoardGUIHandler.createPiecePath(i,isWhite));
                piece.fitHeightProperty().bind(promoContainer.heightProperty().divide(2));
                piece.fitWidthProperty().bind(promoContainer.widthProperty().divide(4));
                piece.setPreserveRatio(true);
                setUpPromoListener(piece,i);
                promoContainer.getChildren().add(piece);
            }
            lastPromoWhite = isWhite;
        }


    }

    // set up the onlick listeners for the pieces a pawn can promote too
    private void setUpPromoListener(ImageView promo,int peiceType){
        promo.setOnMouseClicked(event ->{
            ChessCentralControl.chessActionHandler.promoPawn(peiceType,currentState);
            togglePromo();
        });

    }
    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset){
        ChessCentralControl.chessActionHandler.updateSidePanel(currentState,false,"");
        ChessCentralControl.chessBoardGUIHandler.clearHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Changing move by " + direction);
        if(isReset){
            ChessCentralControl.gameHandler.currentGame.reset();
            victoryLabel.setText("");
        }
        else{
            ChessCentralControl.gameHandler.currentGame.changeToDifferentMove(direction);
        }
        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex,ChessCentralControl.gameHandler.currentGame.maxIndex);
        if(!ChessCentralControl.gameHandler.currentGame.gameStates.isCheckMated()){
            victoryLabel.setText("");
            hideGameOver();
        }
        else{
            showGameOver();
        }
        updateSimpleAdvantageLabels();
    }

    private void clearSimpleAdvantageLabels(){
        WhiteNumericalAdv.setText("");
        BlackNumericalAdv.setText("");
    }

    public void updateSimpleAdvantageLabels(){
        clearSimpleAdvantageLabels();
        int simpleAdvantage = AdvancedChessFunctions.getSimpleAdvantage(ChessCentralControl.gameHandler.currentGame.currentPosition.board);
        if(simpleAdvantage >= 0){
            WhiteNumericalAdv.setText(Integer.toString(simpleAdvantage));
        }
        else{
            BlackNumericalAdv.setText(Integer.toString(simpleAdvantage));

        }
    }
    // draw the eval bar for the screen
    public void setEvalBar(double advantage, int depth,boolean isEvalCallable,boolean gameOver){
        setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,advantage,evalDepth);
        if(!isEvalCallable){
            if(advantage > 100000){
                victoryLabel.setText("Winner : White!");
            }
            else if(advantage < -100000){
                victoryLabel.setText("Winner : Black!");
            }
        }
        if(gameOver){
            showGameOver();
        }
        if(depth > 0){
            evalDepth.setText(Integer.toString(depth));
        }
    }

    public void setMoveLabels(int curIndex,int maxIndex){
        saveIndicator.setText((curIndex + 1 )+ "/" + (maxIndex+1));
    }
    private void setEvalBar(Label whiteEval, Label blackEval, Rectangle whiteBar, Rectangle blackBar, double advantage,Label evalDepth){
        double barModPercent = passThroughAsymptote(Math.abs(advantage))/5;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if(advantage >= 0){
            // white advantage or equal position
            evalDepth.setStyle("-fx-text-fill: black");
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
            if(advantage < -.2){
                // change eval depth color to match the black covering it now that the black has an advantage
                evalDepth.setStyle("-fx-text-fill: white");
            }
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










    // what actually happens when you click a square on the board
    public void makeComputerMove(ChessMove move){
        if(currentState.equals(MainScreenState.LOCAL) && ChessCentralControl.gameHandler.currentGame.isVsComputer()){
            logger.info("Looking at best move for " + (ChessCentralControl.gameHandler.currentGame.isPlayer1Turn() ? "WhitePeices" : "BlackPeices"));
            logger.info("Computer thinks move: \n" + move.toString());
            // computers move
            // since when eating a piece you have to change visuals, need to hanndle it differently
            ChessCentralControl.chessActionHandler.handleMakingMove(move.getOldX(),move.getOldY(),move.getNewX(),move.getNewY(),move.isEating(),false,move.isCastleMove(),true,false,move.getPromoIndx(),currentState,false);

        }

    }


    private void setUpSquareClickEvent(StackPane square) {
        square.setOnMouseClicked(event -> {
//            switchB();
            // finding which image view was clicked and getting coordinates
            StackPane pane = (StackPane) event.getSource();
            String[] xy = pane.getUserData().toString().split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            logger.debug(String.format("Square clicked at coordinates X:%d, Y:%d",x,y));
            logger.debug(String.format("Is white turn?: %s", ChessCentralControl.gameHandler.currentGame.isPlayer1Turn()));
            logger.debug(String.format("Is checkmated?: %b", ChessCentralControl.gameHandler.currentGame.gameStates.isCheckMated()));
            if (event.getButton() == MouseButton.PRIMARY){
                // if the click was a primary click then we want to check if the player can make a move
                // boardinfo:  boardInfo[0] = is there a piece on that square?  boardInfo[1] = is that piece white?
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(x, y, ChessCentralControl.gameHandler.currentGame.currentPosition.board);
                logger.debug("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                ChessCentralControl.chessActionHandler.handleSquareClick(x,y,boardInfo[0],boardInfo[1],currentState);






            } else if (event.getButton() == MouseButton.SECONDARY) {
                ChessCentralControl.chessBoardGUIHandler.toggleSquareHighlight(x, y, true);
            }

        });
    }
    // toggle square highlight with two different types of colors
    // todo: make square highlights different depending on background color

    // checks if square player wants to move is in the moves allowed for that peice



}
