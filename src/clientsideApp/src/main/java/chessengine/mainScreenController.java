package chessengine;

import chessserver.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {

    public UserPreferences initPreferences = null;

    VBox[][] Bgpanes = new VBox[8][8];
    StackPane[][] highlightPanes = new StackPane[8][8];
    VBox[][] moveBoxes = new VBox[8][8];
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
    VBox leftTopAdvBox;

    @FXML
    VBox rightTopAdvBox;

    @FXML
    HBox topRightPlayer2;

    @FXML
    HBox bottomRightPlayer1;


    @FXML
    Pane mainMessageBoard;

    @FXML
    Button LeftReset;

    @FXML
    Button LeftButton;

    @FXML
    Button RightButton;

    @FXML
    Button RightReset;

    @FXML
    Label stateLabel;

    @FXML
    Button settingsButton;

    @FXML
    StackPane chessBoardContainer;

    @FXML
    HBox chessBoardAndEvalContainer;


    @FXML
    GridPane chessHighlightBoard;

    @FXML
    GridPane chessBgBoard;

    @FXML
    GridPane chessMoveBoard;

    @FXML
    Pane arrowBoard;

    @FXML
    Pane promotionScreen;

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
    VBox promoContainer;


    // moves played area
    @FXML
    HBox movesPlayedBox;

    @FXML
    ScrollPane movesPlayed;




    // setting screen
    @FXML
    ScrollPane settingsScroller;

    @FXML
    VBox settingsScreen;

    @FXML
    VBox onlineControls;

    @FXML
    VBox viewerControls;

    @FXML
    VBox localControls;

    @FXML
    VBox campaignControls;

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
    GridPane mainSidePanel;

    @FXML
    VBox gameControls;





    @FXML
    StackPane sidePanel;


    @FXML
    VBox topControls;

    @FXML
    VBox bottomControls;



    // settings screen

    @FXML
    Label themeLabel;

    @FXML
    ChoiceBox<String> themeSelection;

    @FXML
    Label bgLabel;

    @FXML
    ComboBox<String> bgColorSelector;

    @FXML
    Label pieceLabel;

    @FXML
    ComboBox<String> pieceSelector;

    @FXML
    Label audioMuteEff;

    @FXML
    Button audioMuteEffButton;

    @FXML
    Label audioLabelEff;

    @FXML
    Slider audioSliderEff;

    @FXML
    Label evalLabel;

    @FXML
    ComboBox<Integer> evalOptions;

    @FXML
    Label computerLabel;

    @FXML
    ComboBox<Integer> computerOptions;

    @FXML
    Label pgnSaveLabel;

    @FXML
    Button pgnSaveButton;





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

        // some elements need mouse transparency because they are on top of control elements
        mainMessageBoard.setMouseTransparent(true);
        chessMoveBoard.setMouseTransparent(true);
        chessHighlightBoard.setMouseTransparent(true);
        chessPieceBoard.setMouseTransparent(true);
        arrowBoard.setMouseTransparent(true);
        victoryLabel.setMouseTransparent(true);
        logger = LogManager.getLogger(this.toString());

        // load wood grain texture
        Image woodBg = new Image("BackgroundImages/woodBg-Photoroom.png");

        // Create a BackgroundImage
        BackgroundImage backgroundImageWood = new BackgroundImage(woodBg,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);


        // Set the Background to the StackPane
        chessBoardContainer.setBackground(new Background(backgroundImageWood));

        // now to see the wood we need to slightly make the other containers transparent
        chessBgBoard.setOpacity(.95);
        chessHighlightBoard.setOpacity(.95);

        // ordering views
        chessHighlightBoard.toFront();
        chessPieceBoard.toFront();
        chessMoveBoard.toFront();
        arrowBoard.toFront();
        promotionScreen.toFront();

        logger.debug("initializing Main Screen");
        setUpPiecesAndListeners();
        ChessCentralControl = App.ChessCentralControl;

        ChessCentralControl.init(this,chessPieceBoard,eatenWhites,eatenBlacks,peicesAtLocations,inGameInfo,arrowBoard,bestMovesBox,localInfo,sandboxPieces,chatInput,sendMessageButton,Bgpanes,moveBoxes, highlightPanes,chessBgBoard,chessHighlightBoard,chessMoveBoard,movesPlayedBox);
//         small change to make sure moves play box is always focused on the very end
        movesPlayedBox.getChildren().addListener((ListChangeListener<Node>) change ->{
            // makes sure its always at the end
            movesPlayed.setHvalue(1);
        });

        // main settings screen also has a pgn options button


        setPromoPeices(true);
        setUpButtons();
        setUpDragAction();

        if(initPreferences != null){
            ChessCentralControl.asyncController.setComputerDepth(initPreferences.getComputerMoveDepth());
            ChessCentralControl.asyncController.setComputerDepth(initPreferences.getComputerMoveDepth());
            ChessCentralControl.chessBoardGUIHandler.changeChessBg(initPreferences.getChessboardTheme().toString());
        }


    }

    public void oneTimeSetup(){
        // called after app's classes are initialized
        setUpBindings();
        setEvalBar(0,-1,false,false);
        UserPreferenceManager.setupUserSettingsScreen(themeSelection,bgColorSelector,pieceSelector,null,null,audioMuteEffButton,audioSliderEff,evalOptions,computerOptions,true);

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
            VBox Bgstackbox = new VBox();
            Bgstackbox.setAlignment(Pos.CENTER);
            StackPane innerBgBorder = new StackPane();
            BindingController.bindRegionTo2Styles(innerBgBorder,fullScreen.widthProperty(),"-fx-border-width:","-fx-border-radius:",ChessConstants.borderWidthFactor,ChessConstants.borderRadFactor,"-fx-border-color: white;-fx-background-color: transparent");
            innerBgBorder.prefWidthProperty().bind(Bgstackbox.widthProperty().multiply(.99));
            innerBgBorder.prefHeightProperty().bind(Bgstackbox.heightProperty().multiply(.99));
            Bgstackbox.getChildren().add(innerBgBorder);
            VBox moveShowContainer = new VBox();
            moveShowContainer.setAlignment(Pos.CENTER);
            Circle moveShow = new Circle();
            moveShow.setFill(Color.valueOf(ChessConstants.InnerMoveCircleColor));
            BindingController.bindRegionTo2Styles(moveShowContainer,fullScreen.widthProperty(),"-fx-border-width:","-fx-border-radius:",ChessConstants.borderWidthFactor,ChessConstants.borderRadFactor,"-fx-border-color: black");
            moveShow.radiusProperty().bind(chessMoveBoard.widthProperty().add(chessMoveBoard.heightProperty()).divide(75));
            moveShowContainer.getChildren().add(moveShow);

            Bgstackbox.setUserData(i + "," + j);
            chessHighlightBoard.add(stackpane, i, j);
            chessBgBoard.add(Bgstackbox, i, j);
            chessMoveBoard.add(moveShowContainer,i,j);
            setUpSquareClickEvent(Bgstackbox);
            Bgpanes[i][j] = Bgstackbox;
            moveBoxes[i][j] = moveShowContainer;
            highlightPanes[i][j] = stackpane;

        }

    }

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

        LeftReset.setOnMouseClicked(e ->{
            logger.debug("Left Reset clicked");
            if(ChessCentralControl.gameHandler.currentGame.curMoveIndex > -1){
                changeToAbsoluteMoveIndex(-1);

            }
        });

        LeftButton.setOnMouseClicked(e -> {
            logger.debug("Left button clicked");
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

        RightReset.setOnMouseClicked(e ->{
            logger.debug("right Reset clicked");
            if(ChessCentralControl.gameHandler.currentGame.curMoveIndex < ChessCentralControl.gameHandler.currentGame.maxIndex){
                changeToAbsoluteMoveIndex(ChessCentralControl.gameHandler.currentGame.maxIndex);

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
            // clearing all board related stuff
            ChessCentralControl.chessBoardGUIHandler.removeAllPieces();


            // stopping async threads (eval bar etc)
            ChessCentralControl.asyncController.stopAll();

            // if the game we are viewing is here for its first time and is not an empty game (maxindex > -1) then we save it)
            if(ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && !currentState.equals(MainScreenState.VIEWER) && !currentState.equals(MainScreenState.SANDBOX) && ChessCentralControl.gameHandler.currentGame.maxIndex != -1){
                PersistentSaveManager.appendGameToAppData(ChessCentralControl.gameHandler.currentGame);
            }
            // need to leave online game if applicable
            if(ChessCentralControl.gameHandler.currentGame.isWebGame()){
                ChessCentralControl.gameHandler.currentGame.leaveWebGame();
            }


            // boolean flag for scrolling to the next level
            boolean isNewLvl = false;

           // if we are in campaign mode and the game finished, depending on the outcome we will want to move the player to the next level
           if(currentState.equals(MainScreenState.CAMPAIGN)){
               // draw will be one star regardless of difficulty
               // other than that only a win will also have you progress
               CampaignTier completedTier = ChessCentralControl.gameHandler.getCampaignTier();
               int completedLevelOfTier = ChessCentralControl.gameHandler.getLevelOfCampaignTier();
               int numStars = 0;
               if(ChessCentralControl.gameHandler.currentGame.gameStates.isStaleMated()){
                   numStars = 1;
               }
               else if(ChessCentralControl.gameHandler.currentGame.gameStates.isCheckMated()[1]){
                   // game over only happens if draw or checkmate
                   // so if not draw it must be checkmate
                   // so we only have to check if white(the user) won
                   numStars = ChessCentralControl.gameHandler.getGameDifficulty();
               }

               if(numStars != 0){
                   // now if the user earned any stars make sure you add them
                   // btw this will not decrement your level if you have done better before
                   App.userManager.setLevelStars(completedTier,completedLevelOfTier,numStars);

                   // now if this was a new level for them(and they were sucessful), unlock the next one
                   if(completedTier.equals(App.userManager.getCurrentCampaignTier()) && completedLevelOfTier == App.userManager.getCurrentCampaignLevel()){
                       isNewLvl =true;
                       App.userManager.moveToNextLevel();
                   }

               }


           }
            ChessCentralControl.gameHandler.clearGame();
            App.changeToStart();
            // in campaign mode as you move to the next level, scroll up to that level
            if(isNewLvl){
                App.startScreenController.campaignManager.scrollToPlayerTier(App.userManager.getCampaignProgress());
            }

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
        // pawn  promo
        promoContainer.prefWidthProperty().bind(chessPieceBoard.widthProperty().divide(8));
        promoContainer.prefHeightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        promoContainer.spacingProperty().bind(promoContainer.heightProperty().divide(4).subtract(chessPieceBoard.heightProperty().divide(ChessCentralControl.chessBoardGUIHandler.pieceSize)).divide(4));

        // chess board
        content.prefWidthProperty().bind(fullScreen.widthProperty());
        content.prefHeightProperty().bind(fullScreen.heightProperty());

        chessBoardContainer.prefWidthProperty().bind(Bindings.min(chessBoardAndEvalContainer.widthProperty().subtract(evalBar.widthProperty()),fullScreen.heightProperty().subtract(eatenBlacks.heightProperty().multiply(2))));
        chessBoardContainer.prefHeightProperty().bind(chessBoardContainer.widthProperty());
        chessPieceBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        chessPieceBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        arrowBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        arrowBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        // side panel


        // eval bar related
        evalBar.prefHeightProperty().bind(chessHighlightBoard.heightProperty());
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(chessBoardContainer,evalBar,75,.1);
        evalContainer.prefHeightProperty().bind(chessHighlightBoard.heightProperty());
        evalLabelBox.spacingProperty().bind(evalLabelBox.heightProperty().divide(3));
        // default heights
        whiteadvantage.heightProperty().bind(chessBoardContainer.heightProperty().divide(2));
        blackadvantage.heightProperty().bind(chessBoardContainer.heightProperty().divide(2));

        eatenBlacks.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        eatenWhites.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        // sidepanel stuff
        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessBoardAndEvalContainer.widthProperty()).subtract(leftSideSpacer.widthProperty()));
        sidePanel.prefHeightProperty().bind(content.heightProperty());

        // moves played box
        movesPlayed.setFitToHeight(true);
        movesPlayed.prefWidthProperty().bind(sidePanel.widthProperty());

        // all the different side panels
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel,localInfo,100,.8);
        App.bindingController.bindSmallText(localInfo,true,"black");

        localInfo.prefHeightProperty().bind(gameControls.heightProperty().subtract(movesPlayed.heightProperty()).subtract(bottomControls.heightProperty()));
        inGameInfo.prefWidthProperty().bind(localInfo.widthProperty());
        inGameInfo.prefHeightProperty().bind(localInfo.heightProperty().subtract(sendMessageButton.heightProperty()));
        sendMessageButton.prefWidthProperty().bind(inGameInfo.widthProperty().subtract(inGameInfo.widthProperty().divide(8)));
        chatInput.prefWidthProperty().bind(inGameInfo.widthProperty().subtract(sendMessageButton.widthProperty()).subtract(5));
        chatInput.prefHeightProperty().bind(sendMessageButton.heightProperty());

// these todo
//        this.bestmovesBox = bestmovesBox;
//        this.localInfo = localInfo;
//        this.sandboxPieces = sandboxPieces;
//        this.chatArea = chatArea;
//        this.chatInput = chatInput;
//        this.sendMessageButton = sendMessageButton;

        // side panel labels

        App.bindingController.bindSmallText(stateLabel,true);
        App.bindingController.bindLargeText(victoryLabel,true,"White");
        App.bindingController.bindSmallText(player1Label,true);
        App.bindingController.bindSmallText(player2Label,true);
        App.bindingController.bindSmallText(WhiteNumericalAdv,true);
        App.bindingController.bindSmallText(BlackNumericalAdv,true);
//        reset.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty().multiply(2)));
//        LeftButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
//        LeftButton.prefHeightProperty().bind(LeftButton.widthProperty());
//        RightButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
//        gameControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        settingsScreen.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        topControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        bottomControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        RightButton.prefHeightProperty().bind(RightButton.widthProperty());
        player1Select.fitHeightProperty().bind(eatenBlacks.heightProperty().multiply(.8));
        player1Select.fitWidthProperty().bind(player1Select.fitHeightProperty().multiply(.8));
        player2Select.fitHeightProperty().bind(eatenBlacks.heightProperty().multiply(.8));
        player2Select.fitWidthProperty().bind(player2Select.fitHeightProperty().multiply(.8));
//        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        initLabelBindings(bgLabel);
//        initLabelBindings(pieceLabel);
//        initLabelBindings(evalLabel);

        // setting screen
        settingsScroller.prefWidthProperty().bind(sidePanel.widthProperty());
        settingsScroller.prefHeightProperty().bind(sidePanel.heightProperty());

        settingsScreen.prefWidthProperty().bind(sidePanel.widthProperty());
//
    }
    // methods called every new game
    public MainScreenState currentState;

    // for campaign only

    /** Setup Steps that are called every game, regardless of campaign or not**/
    private void setUp(boolean isWhiteOriented){
        // side panel controls are slightly different for every mode
        setMainControls(currentState);
        // some modes do not want an eval bar
        checkHideEvalBar(currentState);

        // set up board
        setEvalBar(0,-1,false,false);
        clearSimpleAdvantageLabels();
        hidePromo();
        hideSettings();
        showGameControlls();
        hideGameOver();
        ChessCentralControl.chessActionHandler.reset();
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();


        ChessCentralControl.chessActionHandler.updateSidePanel(currentState,false,true);

        

        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex,ChessCentralControl.gameHandler.currentGame.maxIndex);


    }

    public void setupCampaign(String player1Name, int player1Elo,String player1PfpUrl,CampaignTier levelTier,int levelOfTier,int campaignDifficuly){
        String campaignOpponentName = levelTier.levelNames[levelOfTier];
        int campaignOpponentElo = levelTier.eloIndexes[levelOfTier];
        String pfpUrl2 = ProfilePicture.values()[levelTier.pfpIndexes[levelOfTier]].urlString;

        this.currentState = MainScreenState.CAMPAIGN;
        ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGame(player1Name,campaignOpponentName,player1Elo,campaignOpponentElo,player1PfpUrl,pfpUrl2,true,true));
        ChessCentralControl.gameHandler.setGameDifficulty(campaignDifficuly);
        ChessCentralControl.gameHandler.setCampaignTier(levelTier);
        ChessCentralControl.gameHandler.setLevelOfCampaignTier(levelOfTier);

        // in campaign the user always plays white
        setUp(false);
    }

    public void setupRegular(boolean isVsComputer,boolean isWhiteOriented,String gameName,ChessGame gameToSetup,String player1Name,int player1Elo,String player1PfpUrl,MainScreenState currentState){
        this.currentState = currentState;
        boolean isVsComputerReal = gameToSetup != null ? gameToSetup.isVsComputer() : isVsComputer;
        if(Objects.nonNull(gameToSetup)){
            if(currentState.equals(MainScreenState.VIEWER)){
                // in viewer mode, you will have a old game that you dont want to modify, however you still want to play around with a temporary copy
                ChessCentralControl.gameHandler.switchToGame(gameToSetup.cloneGame());
            }
            ChessCentralControl.gameHandler.switchToGame(gameToSetup);

        }
        else{
            if(gameName.isEmpty()){
                ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGame(player1Name,isVsComputerReal ? "Computer" : "Player 1",player1Elo,isVsComputerReal ? 3000 : player1Elo,player1PfpUrl,isVsComputerReal ? ProfilePicture.ROBOT.urlString: player1PfpUrl,isVsComputerReal,isWhiteOriented));
            }
            else{
                ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGameWithName(gameName,player1Name,isVsComputerReal ? "Computer" : "Player 1",player1Elo,isVsComputerReal ? 3000 : player1Elo,player1PfpUrl,isVsComputerReal ? ProfilePicture.ROBOT.urlString: player1PfpUrl,isVsComputerReal,isWhiteOriented));
            }
        }
        setUp(true);
    }

    private void checkHideEvalBar(MainScreenState currentState) {
        if(currentState.equals(MainScreenState.ONLINE) || currentState.equals(MainScreenState.LOCAL) || currentState.equals(MainScreenState.CAMPAIGN)){
            // hidden as these are real games
            evalBar.setVisible(false);
        }
        else{
            evalBar.setVisible(true);
        }
    }







    public void setUpIcons(String player1Url,String player2Url){
        player1Select.setImage(new Image(player1Url));
        player2Select.setImage(new Image(player2Url));
    }



    public void setPlayerLabels(String player1Name,int player1Elo, String player2Name,int player2Elo){
        player1Label.setText(player1Name + " " + Integer.toString(player1Elo));
        player2Label.setText(player2Name + " " + Integer.toString(player2Elo));
    }



    // toggle the pawn promotion screen
    public void showPromo(int promoX, boolean isWhite,boolean isWhiteOriented){
        // reusing piece calculation as you can use it for the promo screen too.
        System.out.println("Promo shown");
        setPromoPeices(isWhite);
        DoubleBinding x = ChessCentralControl.chessBoardGUIHandler.calcLayoutXBinding(promoX,promoContainer.widthProperty());
        promoContainer.layoutXProperty().bind(x);
        if(!isWhite == isWhiteOriented){
            promoContainer.layoutYProperty().bind(chessBoardContainer.widthProperty().divide(2));
        }
        else{
            promoContainer.layoutYProperty().bind(new SimpleDoubleProperty(0));
        }

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

    public void hideGameOver(){
        gameoverMenu.setMouseTransparent(true);
        gameoverMenu.setVisible(false);
    }


    public void showGameOver(){
        gameoverMenu.setMouseTransparent(false);
        gameoverMenu.setVisible(true);
    }
    private void toggleSettingsAndGameControls(){
        settingsScroller.setMouseTransparent(!settingsScroller.isMouseTransparent());
        settingsScroller.setVisible(!settingsScroller.isVisible());
        mainSidePanel.setVisible(!mainSidePanel.isVisible());
        mainSidePanel.setMouseTransparent(!mainSidePanel.isMouseTransparent());

    }

    private void hideSettings(){
        settingsScroller.setMouseTransparent(true);
        settingsScroller.setVisible(false);
    }



    private void showGameControlls(){
        mainSidePanel.setMouseTransparent(false);
        mainSidePanel.setVisible(true);
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
            case CAMPAIGN -> {
                currentControls = campaignControls;
                stateLabel.setText("Campaign Mode");
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
        campaignControls.setMouseTransparent(true);
        campaignControls.setVisible(false);
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
                piece.fitWidthProperty().bind(promoContainer.widthProperty().multiply(.9));
                piece.fitHeightProperty().bind(piece.fitWidthProperty());
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
            hidePromo();
        });

    }

    public void changeToAbsoluteMoveIndex(int absIndex){
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Resetting to abs index:" + absIndex);


        ChessCentralControl.gameHandler.currentGame.moveToMoveIndexAbsolute(absIndex,true);

        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex,ChessCentralControl.gameHandler.currentGame.maxIndex);
        if(!ChessCentralControl.gameHandler.currentGame.gameStates.isCheckMated()[0]){
            victoryLabel.setText("");
            hideGameOver();
        }
        else{
            showGameOver();
        }
        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.updateSidePanel(currentState,false,false);
    }

    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset){
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Changing move by " + direction);
        if(isReset){
            // resets the backend chessgame
            ChessCentralControl.gameHandler.currentGame.reset();


        }
        else{
            ChessCentralControl.gameHandler.currentGame.changeToDifferentMove(direction,false);


        }
        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex,ChessCentralControl.gameHandler.currentGame.maxIndex);
        if(!ChessCentralControl.gameHandler.currentGame.gameStates.isCheckMated()[0]){
            victoryLabel.setText("");
            hideGameOver();
        }
        else{
            showGameOver();
        }
        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.updateSidePanel(currentState,false,false);

    }

    private void clearSimpleAdvantageLabels(){
        WhiteNumericalAdv.setText("");
        BlackNumericalAdv.setText("");
    }

    public void updateSimpleAdvantageLabels(){
        clearSimpleAdvantageLabels();
        int simpleAdvantage = AdvancedChessFunctions.getSimpleAdvantage(ChessCentralControl.gameHandler.currentGame.currentPosition.board);
        if(simpleAdvantage > 0){
            WhiteNumericalAdv.setText("+" + Integer.toString(simpleAdvantage));
        }
        else if(simpleAdvantage < 0){
            // flip the sign from a negative to a positive
            BlackNumericalAdv.setText("+" + Integer.toString(simpleAdvantage*-1));
        }
        else{
            // clear labels as zero advantage
            clearSimpleAdvantageLabels();
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
            if(advantage == 0){
                victoryLabel.setText("Draw!");
            }
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
        if((currentState.equals(MainScreenState.LOCAL) || currentState.equals(MainScreenState.CAMPAIGN)) && ChessCentralControl.gameHandler.currentGame.isVsComputer()){
            logger.info("Looking at best move for " + (ChessCentralControl.gameHandler.currentGame.isPlayer1Turn() ? "WhitePeices" : "BlackPeices"));
            logger.info("Computer thinks move: \n" + move.toString());
            // computers move
            // since when eating a piece you have to change visuals, need to hanndle it differently
            ChessCentralControl.chessActionHandler.handleMakingMove(move.getOldX(),move.getOldY(),move.getNewX(),move.getNewY(),move.isEating(),move.isWhite(),move.isCastleMove(),move.isEnPassant(),true,false,move.getPromoIndx(),currentState,false);

        }

    }


    private void setUpSquareClickEvent(VBox square) {
        square.setOnMouseClicked(event -> {
//            switchB();
            // finding which image view was clicked and getting coordinates
            VBox pane = (VBox) event.getSource();
            String[] xy = pane.getUserData().toString().split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            logger.debug(String.format("Square clicked at coordinates X:%d, Y:%d",x,y));
            logger.debug(String.format("Is white turn?: %s", ChessCentralControl.gameHandler.currentGame.isPlayer1Turn()));
            logger.debug(String.format("Is checkmated?: %b", ChessCentralControl.gameHandler.currentGame.gameStates.isCheckMated()[0]));
            if (event.getButton() == MouseButton.PRIMARY){
                // if the click was a primary click then we want to check if the player can make a move
                // boardinfo:  boardInfo[0] = is there a piece on that square?  boardInfo[1] = is that piece white?
                int backendY = ChessCentralControl.gameHandler.currentGame.isWhiteOriented() ? y : 7-y;
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(x, backendY, ChessCentralControl.gameHandler.currentGame.currentPosition.board);
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
