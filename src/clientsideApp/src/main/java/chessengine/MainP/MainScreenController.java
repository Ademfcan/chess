package chessengine.MainP;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.CentralControlComponents.ResettableGame;
import chessengine.Enums.Window;
import chessengine.Graphics.AppWindow;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.TextUtils;
import chessengine.Misc.Constants;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessMove;
import chessengine.Enums.MainScreenState;
import chessserver.ChessRepresentations.PlayerInfo;
import chessserver.Enums.*;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageTypes.ChessGameMessageTypes;
import chessserver.Net.Payload;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.DecimalFormat;
import java.util.UUID;

public class MainScreenController extends AppWindow implements ResettableGame {

    private final Logger logger = LogManager.getLogger(this.toString());;
    private final ImageView[][] peicesAtLocations = new ImageView[8][8];

    public UserPreferences initPreferences = null;
    public MainScreenState currentState;
    VBox[][] Bgpanes = new VBox[8][8];
    StackPane[][] highlightPanes = new StackPane[8][8];
    VBox[][] moveBoxes = new VBox[8][8];

    // change promotion peice colors if needed
    boolean lastPromoWhite = false;
    boolean isSidePanelInline = true;
    boolean isSidePanelToggled = false;
    private ChessCentralControl chessCentralControl;
    private VBox currentControls;

    public MainScreenController(){
        super();
    }


    @Override
    public void initLayout(){
        currentGamePgn.setEditable(false);

        // some elements need mouse transparency because they are on top of control elements
        mainRef.setMouseTransparent(true);
        chessMoveBoard.setMouseTransparent(true);
        chessHighlightBoard.setMouseTransparent(true);
        chessPieceBoard.setMouseTransparent(true);
        arrowBoard.setMouseTransparent(true);
        victoryLabel.setMouseTransparent(true);

        movesPlayedBox.getChildren().addListener((ListChangeListener<Node>) change -> {
            // makes sure scrollpane its always at the end
            movesPlayed.setVvalue(1.0);
        });

        hideSidePanelPopup();
        handleSidePanelChange();

        // ordering views
        chessHighlightBoard.toFront();
        chessPieceBoard.toFront();
        chessMoveBoard.toFront();
        arrowBoard.toFront();
        promotionScreen.toFront();
        gameoverMenu.toFront();
        evalBar.setTranslateZ(-1);


    }

    @Override
    public void initGraphics() {
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

        // game over menu config
        gameoverMenu.setBackground(Constants.gameOverBackground);


        sidePanelPopupContent.getStyleClass().add("root");

    }

    @Override
    public void afterInitialize(){
        resetState();

        // called after app's classes are initialized
        chessCentralControl = App.chessCentralControl;
        chessCentralControl.init(this, chessPieceBoard, bottomEatenContainer, topEatenContainer, peicesAtLocations, inGameInfo,
                arrowBoard, bestMovesBox, campaignInfo, sandboxPieces, chatInput, sendMessageButton,emojiContainer,resignButton,offerDrawButton, Bgpanes, moveBoxes, highlightPanes,
                chessBgBoard, chessHighlightBoard, movesPlayedBox,movesPlayed, lineLabel,playPauseButton,timeSlider, bottomPlayerTurnTime, topPlayerTurnTime,player1SimSelector,
                player2SimSelector,currentGamePgn,puzzleEloSlider,puzzleElo,hintButton,puzzleTagsBox);

        chessCentralControl.chessActionHandler.init();
        chessCentralControl.puzzleGuiManager.init();

        setUpLayout();
        setUpPiecesAndListeners();
        setPromoPeices(true);
        setUpButtons();
        setUpDragAction();
        setupIcons();
        setEvalBar(0, -1, false);


        chessCentralControl.fullReset(true);

        if (initPreferences != null) {
//            ChessCentralControl.asyncController.setComputerDepth(initPreferences.getComputerMoveDepth()); // todo
            chessCentralControl.chessBoardGUIHandler.changeChessBg(initPreferences.getChessboardTheme().toString());
        }
    }

    private void setUpDragAction() {
        chessBgBoard.setOnMouseDragged(e -> {
            chessCentralControl.chessActionHandler.handleBoardDrag(e);
        });
        chessBgBoard.setOnMouseReleased(e -> {
            chessCentralControl.chessActionHandler.handleBoardRelease(e, currentState);

        });
        chessBgBoard.setOnMousePressed(e -> {
            chessCentralControl.chessActionHandler.handleBoardPress(e, currentState);
        });
    }

    private void setUpPiecesAndListeners() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane stackpane = new StackPane();
                VBox Bgstackbox = new VBox();
                Bgstackbox.setAlignment(Pos.CENTER);
                StackPane innerBgBorder = new StackPane();
                BindingController.bindRegionTo2Styles(innerBgBorder, fullScreen.widthProperty(), "-fx-border-width:", "-fx-border-radius:", ChessConstants.borderWidthFactor, ChessConstants.borderRadFactor, "-fx-border-color: white;-fx-background-color: transparent");
                innerBgBorder.prefWidthProperty().bind(Bgstackbox.widthProperty().multiply(.99));
                innerBgBorder.prefHeightProperty().bind(Bgstackbox.heightProperty().multiply(.99));
                Bgstackbox.getChildren().add(innerBgBorder);
                VBox moveShowContainer = new VBox();
                moveShowContainer.setAlignment(Pos.CENTER);
                Circle moveShow = new Circle();
                moveShow.setFill(Color.valueOf(ChessConstants.InnerMoveCircleColor));
                BindingController.bindRegionTo2Styles(moveShowContainer, fullScreen.widthProperty(), "-fx-border-width:", "-fx-border-radius:", ChessConstants.borderWidthFactor, ChessConstants.borderRadFactor, "-fx-border-color: black");
                moveShow.radiusProperty().bind(chessMoveBoard.widthProperty().add(chessMoveBoard.heightProperty()).divide(75));
                moveShowContainer.getChildren().add(moveShow);

                Bgstackbox.setUserData(i + "," + j);
                chessHighlightBoard.add(stackpane, i, j);
                chessBgBoard.add(Bgstackbox, i, j);
                chessMoveBoard.add(moveShowContainer, i, j);
                setUpSquareClickEvent(Bgstackbox);
                Bgpanes[i][j] = Bgstackbox;
                moveBoxes[i][j] = moveShowContainer;
                highlightPanes[i][j] = stackpane;

            }

        }

    }

    private void setUpButtons() {
        homeButton.setOnMouseClicked(e->{
            HomeReset();
        });


        pgnSaveButton.setOnMouseClicked(e -> {
            String fileName = chessCentralControl.gameHandler.gameWrapper.getGame().getGameName() + ".txt";
            String pgn = chessCentralControl.gameHandler.gameWrapper.getGame().gameToPgn();
            logger.debug("Saving game");
            GeneralChessFunctions.saveToFile(fileName, pgn);
        });

        settingsButton.setOnMouseClicked(e -> {
            toggleSettingsAndGameControls();
        });
        hideSettings.setOnMouseClicked(e ->{
            toggleSettingsAndGameControls();
        });

        StartOfGameButton.setOnMouseClicked(e -> {
            logger.debug("Left Reset clicked");
            int minIndex = chessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex();
            if (chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > minIndex) {
                changeToAbsoluteMoveIndex(minIndex);

            }
        });

        MoveBackButton.setOnMouseClicked(e -> {
            logger.debug("Left button clicked");
            int minIndex = chessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex();
            if (chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > minIndex) {
                changeMove(-1, false,false,false);

            }

            System.out.println(GeneralChessFunctions.getBoardDetailedString(chessCentralControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board));


        });

        MoveForwardButton.setOnMouseClicked(e -> {
            logger.debug("Right button clicked");
            if (chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() < chessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                changeMove(1, false,false,false);


            }
        });

        EndOfGameButton.setOnMouseClicked(e -> {
            logger.debug("right Reset clicked");
            if (currentState != MainScreenState.PUZZLE && chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() < chessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                changeToAbsoluteMoveIndex(chessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());

            }
        });

        openSidePanel.setOnMouseClicked(e -> {
            showSidePanelPopup();
        });

        hideSidePanel.setOnMouseClicked(e -> {
            hideSidePanelPopup();
        });

        inlineHomeButton.setOnMouseClicked(e -> {
            HomeReset();
        });

    }

    /** Clears current game, performs cleanup and then goes back to start screen**/
    public void HomeReset(){
        // clearing all board related stuff
        // stopping async threads (eval bar etc)
        chessCentralControl.asyncController.stopAll();

        // if the game we are viewing is here for its first time and is not an empty game (maxindex > -1) then we save it)
        if (MainScreenState.isSaveableState(currentState) && chessCentralControl.gameHandler.shouldSaveGame()) {
            App.saveGame(chessCentralControl.gameHandler.gameWrapper.getGame(), chessCentralControl.gameHandler.gameWrapper.isWebGame());
        }
        // need to leave online game if applicable
        if (chessCentralControl.gameHandler.gameWrapper.isActiveWebGame()) {
            chessCentralControl.gameHandler.gameWrapper.leaveWebGame();
        }
        else if(chessCentralControl.gameHandler.gameWrapper.isWebGame() && !chessCentralControl.gameHandler.gameWrapper.isWebGameInitialized()){
            App.clientChessGameMessageHandler.leaveWaitingPool();
        }


        // boolean flag for scrolling to the next level
        boolean isNewLvl = false;

        // if we are in campaign mode and the game finished, depending on the outcome we will want to move the player to the next level
        if (currentState == MainScreenState.CAMPAIGN) {
            // draw will be one star regardless of difficulty
            // other than that only a win will also have you progress
            CampaignAttempt attempt = chessCentralControl.gameHandler.getCampaignAttempt();
            ChessGame completedGame = chessCentralControl.gameHandler.gameWrapper.getGame();


            App.startScreenController.campaignScreenM.handleCampaignUpdate(attempt, completedGame);



        }
        chessCentralControl.gameHandler.clearGame();
        App.changeToStart();

    }

    public void processChatInput() {
        if(!chatInput.getText().isBlank()){
            App.soundPlayer.playEffect(Effect.MESSAGE);
            boolean isWhiteOriented = chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
            chessCentralControl.chessActionHandler.appendNewMessageToChat("(" + (isWhiteOriented ? chessCentralControl.gameHandler.gameWrapper.getGame().getWhitePlayerName() : chessCentralControl.gameHandler.gameWrapper.getGame().getBlackPlayerName()) + ") " + chatInput.getText());
            if (chessCentralControl.gameHandler.gameWrapper.isActiveWebGame() && chessCentralControl.gameHandler.gameWrapper.isCurrentWebGameInitialized()) {
                App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(
                        new MessageConfig(
                                new Message(ChessGameMessageTypes.ClientRequest.SENDCHAT,
                                        new Payload.StringPayload(chatInput.getText()))));

            }
            chatInput.clear();
        }
        else{
            logger.debug("Not sending chat, empty input");
        }
    }

    // for campaign only

    public void setUpLayout() {
        //
        movesPlayed.prefHeightProperty().bind(gameMenu.heightProperty()
                .subtract(lineLabel.heightProperty())
                .subtract(bottomControls.heightProperty())
                .subtract(navigation.heightProperty())
                .divide(2));

        switchingOptions.prefHeightProperty().bind(movesPlayed.heightProperty());


        // game container
        gameContainer.prefHeightProperty().bind(getRootHeight());
        gameContainer.prefWidthProperty().bind(chessBoardAndEvalContainer.widthProperty());

        chessBoardAndEvalContainer.prefWidthProperty().bind(chessBoardContainer.widthProperty().add(evalBar.widthProperty()));
        chessBoardAndEvalContainer.prefHeightProperty().bind(chessBoardContainer.heightProperty());

        // chessboard Top
        topPlayerIcon.fitHeightProperty().bind(chessBoardTop.heightProperty().multiply(.8));
        topPlayerIcon.fitWidthProperty().bind(topPlayerIcon.fitHeightProperty());

        BindingController.bindSmallText(topPlayerName);
//        topPlayerName.setTextOverrun(OverrunStyle.CLIP);
        TextUtils.addTooltipOnElipsis(topPlayerName);
        // chessboard Bottom
        bottomPlayerIcon.fitHeightProperty().bind(chessBoardBottom.heightProperty().multiply(.8));
        bottomPlayerIcon.fitWidthProperty().bind(bottomPlayerIcon.fitHeightProperty());

        BindingController.bindSmallText(bottomPlayerName);
//        bottomPlayerName.setTextOverrun(OverrunStyle.CLIP);
        TextUtils.addTooltipOnElipsis(bottomPlayerName);



        // fixed square chess board
        chessBoardContainer.prefWidthProperty().bind(Bindings.min(
                getRootWidth()
                    .subtract(evalBar.widthProperty()),
                getRootHeight()
                    .subtract(chessBoardTop.heightProperty())
                    .subtract(chessBoardBottom.heightProperty())));
        chessBoardContainer.prefHeightProperty().bind(chessBoardContainer.widthProperty());

        chessBoardTop.prefWidthProperty().bind(gameContainer.widthProperty());
        BindingController.bindCustom(gameContainer.heightProperty(), chessBoardTop.prefHeightProperty(), 75, .10);
        chessBoardBottom.prefWidthProperty().bind(gameContainer.widthProperty());
        BindingController.bindCustom(gameContainer.heightProperty(), chessBoardBottom.prefHeightProperty(), 75, .10);
//        topPlayerLabels.spacingProperty().bind(topPlayerLabels.widthProperty().subtract(topPlayerTurnTime.widthProperty()).subtract(topAdvantage.widthProperty()));
//        bottomPlayerLabels.spacingProperty().bind(bottomPlayerLabels.widthProperty().subtract(bottomPlayerTurnTime.widthProperty()).subtract(bottomAdvantage.widthProperty()));

        BindingController.bindSmallText(topPlayerTurnTime);
        BindingController.bindSmallText(topAdvantage);
        BindingController.bindSmallText(bottomPlayerTurnTime);
        BindingController.bindSmallText(bottomAdvantage);

//        topEatenContainer.prefWidthProperty().bind(chessBoardContainer.widthProperty().divide(2));
        topRightSpacer.prefWidthProperty().bind(evalBar.widthProperty());
//        bottomEatenContainer.prefWidthProperty().bind(chessBoardContainer.widthProperty().divide(2));
        bottomRightSpacer.prefWidthProperty().bind(evalBar.widthProperty());




        // eval bar related
        evalBar.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        BindingController.bindCustom(chessBoardContainer.widthProperty(), evalBar.prefWidthProperty(), 75, .10);
        evalContainer.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        evalLabelBox.spacingProperty().bind((evalLabelBox.heightProperty()
                .subtract(whiteEval.heightProperty())
                .subtract(blackEval.heightProperty())
                .subtract(evalDepth.heightProperty()))
                .divide(3));
        // eval bar rectangle default heights
        whiteadvantage.heightProperty().bind(chessBoardContainer.heightProperty().divide(2));
        blackadvantage.heightProperty().bind(chessBoardContainer.heightProperty().divide(2));

        // side panel
        BindingController.bindCustom(gameContainer.widthProperty(), sidePanel.prefWidthProperty(), 300, .6);
        sidePanel.prefHeightProperty().bind(getRootHeight());

        sidePanelInline.prefWidthProperty().bind(sidePanel.prefWidthProperty());

        // configure inline side panel visibility
        sidePanelInline.visibleProperty().bind(getRootWidth().greaterThan(gameContainer.widthProperty().add(sidePanel.prefWidthProperty())));
        sidePanelInline.managedProperty().bind(sidePanelInline.visibleProperty());

        openSidePanel.visibleProperty().bind(sidePanelInline.visibleProperty().not().and(sidePanelPopup.visibleProperty().not()));
        openSidePanel.managedProperty().bind(openSidePanel.visibleProperty());

        inlineHomeButton.visibleProperty().bind(openSidePanel.visibleProperty());
        inlineHomeButton.managedProperty().bind(openSidePanel.visibleProperty());

        getRootWidth().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::handleSidePanelChange);
        });
        getRootHeight().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::handleSidePanelChange);
        });

        sidePanelPopup.prefWidthProperty().bind(sidePanel.widthProperty().add(hideSidePanel.widthProperty()));


        // simulation controls
        BindingController.bindSmallText(simulationScore, "Black");
        BindingController.bindCustom(sidePanel.widthProperty(), playPauseButton.prefWidthProperty(), 200, .3);
        BindingController.bindCustom(sidePanel.heightProperty(), playPauseButton.prefHeightProperty(), 180, .2);

        // sidepanel stuff
        BindingController.bindSmallText(player1SimLabel, "Black");
        BindingController.bindSmallText(player2SimLabel, "Black");

        sandboxPieces.prefHeightProperty().bind(switchingOptions.heightProperty());

        BindingController.bindSmallText(lineLabel, "Black");

//        evalOverTimeBox.prefHeightProperty().bind(localInfo.heightProperty());
//        evalOverTimeBox.prefWidthProperty().bind(localInfo.widthProperty());

        BindingController.bindSmallText(currentGamePgnLabel, "Black");
        BindingController.bindSmallText(currentGamePgn, "Black");
        currentGamePgn.prefWidthProperty().bind(settingsScreen.widthProperty());

        // pawn  promo
        promoContainer.prefWidthProperty().bind(chessPieceBoard.widthProperty().divide(8));
        promoContainer.prefHeightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        promoContainer.spacingProperty().bind(promoContainer.heightProperty().divide(4).subtract(chessPieceBoard.heightProperty().divide(chessCentralControl.chessBoardGUIHandler.pieceSize)).divide(4));



        // moves played box
        movesPlayed.setFitToHeight(true);
        movesPlayed.prefWidthProperty().bind(getSidePanelWidth());

        // all the different side panels
        BindingController.bindCustom(sidePanel.widthProperty(), campaignInfo.prefWidthProperty(), 100, .8);
        BindingController.bindSmallText(campaignInfo, "Black");

        campaignInfo.prefHeightProperty().bind(switchingOptions.heightProperty());


        BindingController.bindSmallText(inGameInfo, "black");
        inGameInfo.prefWidthProperty().bind(onlineControls.widthProperty());
        inGameInfo.prefHeightProperty().bind(switchingOptions.heightProperty().multiply(0.4));
        sendMessageButton.prefHeightProperty().bind(switchingOptions.heightProperty().multiply(0.17));
        sendMessageButton.prefWidthProperty().bind(inGameInfo.widthProperty().divide(8));
        chatInput.prefWidthProperty().bind(inGameInfo.widthProperty().subtract(sendMessageButton.widthProperty()).subtract(5));
        chatInput.prefHeightProperty().bind(sendMessageButton.heightProperty());
            resignButton.prefHeightProperty().bind(switchingOptions.heightProperty().multiply(.17));
        offerDrawButton.prefHeightProperty().bind(switchingOptions.heightProperty().multiply(.17));
        emojiContainer.prefWidthProperty().bind(onlineControls.widthProperty());
        emojiContainer.prefHeightProperty().bind(switchingOptions.heightProperty().multiply(0.2));

        // side panel labels

        BindingController.bindSmallText(stateLabel);
        BindingController.bindLargeText(victoryLabel, "White");



//        reset.prefWidthProperty().bind(chessPieceBoard.widthProperty().subtract(whiteadvantage.widthProperty().multiply(2)));
//        LeftButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
//        LeftButton.prefHeightProperty().bind(LeftButton.widthProperty());
//        RightButton.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()).divide(3));
//        gameControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        settingsScreen.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        topControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        bottomControls.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        RightButton.prefHeightProperty().bind(RightButton.widthProperty());




//        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        initLabelBindings(bgLabel);
//        initLabelBindings(pieceLabel);
//        initLabelBindings(evalLabel);

        // setting screen
        settingsScroller.prefWidthProperty().bind(getSidePanelWidth());
        settingsScroller.prefHeightProperty().bind(getSidePanelWidth());

        settingsScreen.prefWidthProperty().bind(getSidePanelWidth());

        // game over menu
        BindingController.bindXLargeText(gameoverTitle, "White");
        BindingController.bindXLargeText(victoryLabel, "White");

        // miscelaneus buttons
        BindingController.bindMediumText(settingsButton, "Black");
        BindingController.bindMediumText(homeButton, "Black");
        BindingController.bindMediumText(MoveBackButton);
        BindingController.bindCustom(sidePanel.widthProperty(), MoveBackButton.prefWidthProperty(), 110, .32);
        BindingController.bindMediumText(MoveForwardButton);
        BindingController.bindCustom(sidePanel.widthProperty(), MoveForwardButton.prefWidthProperty(), 110, .32);
        BindingController.bindMediumText(StartOfGameButton);
        BindingController.bindCustom(sidePanel.widthProperty(), StartOfGameButton.prefWidthProperty(), 140, .36);
        BindingController.bindMediumText(EndOfGameButton);
        BindingController.bindCustom(sidePanel.widthProperty(), EndOfGameButton.prefWidthProperty(), 140, .36);
        BindingController.bindSmallText(saveIndicator);
        BindingController.bindMediumText(reset);
        BindingController.bindCustom(sidePanel.widthProperty(), reset.prefWidthProperty(), 240, .45);
//


        BindingController.bindSmallText(puzzleTagsLabel);
    }

    public void handleSidePanelChange(){

        boolean newSidePanelInline = getRootWidth().doubleValue() > gameContainer.getPrefWidth() + sidePanel.getPrefWidth();
        if(newSidePanelInline == isSidePanelInline){
            return;
        }

        // new state
        isSidePanelInline = newSidePanelInline;

        if (isSidePanelInline) {

            // move to inline version
            if(isSidePanelToggled){
                hideSidePanelPopup();
            }
            // by default the child might already be non-empty
            if(sidePanelInline.getChildren().isEmpty()){
                sidePanelInline.getChildren().add(sidePanel);
            }
            sidePanelPopupContent.getChildren().remove(sidePanel);

        }
        else{
            // move to popup version
            sidePanelInline.getChildren().remove(sidePanel);
            sidePanelPopupContent.getChildren().add(sidePanel);
            showSidePanelPopup();

        }
    }

    public void setupIcons(){
        homeButton.setGraphic(new FontIcon("fas-house-user"));
        inlineHomeButton.setGraphic(new FontIcon("fas-house-user"));

        settingsButton.setGraphic(new FontIcon("fas-sliders-h"));
        hideSettings.setGraphic(new FontIcon("fas-arrow-left"));

        StartOfGameButton.setGraphic(new FontIcon("fas-fast-backward"));
        MoveBackButton.setGraphic(new FontIcon("fas-step-backward"));
        MoveForwardButton.setGraphic(new FontIcon("fas-step-forward"));
        EndOfGameButton.setGraphic(new FontIcon("fas-fast-forward"));

        openSidePanel.setGraphic(openIcon);
        hideSidePanel.setGraphic(closeIcon);
    }


    FontIcon closeIcon = new FontIcon("fas-arrow-right");
    FontIcon openIcon = new FontIcon("fas-arrow-left");

    public void hideSidePanelPopup(){
        isSidePanelToggled = false;
        sidePanelPopup.setVisible(false);
        sidePanelPopup.setMouseTransparent(true);
    }

    public void showSidePanelPopup(){

        isSidePanelToggled = true;
        sidePanelPopup.setVisible(true);
        sidePanelPopup.setMouseTransparent(false);
    }


    /**
     * Setup Steps that are called every game, regardless of campaign or not
     **/
    private void setUp(String extraStuff) {
        // side panel controls are slightly different for every mode
        setMainControls(currentState, extraStuff);
        // some modes do not want an eval bar
        checkHideEvalBar(currentState);
        checkHideMovesPlayed(currentState);
        checkHideResetButton(currentState);
        // depending on the current state, the reset button does different things
        setResetButtonFunctionality(currentState);
        setGameOverButtonFunctionality(currentState);
        if (currentState == MainScreenState.LOCAL) {
            // since in campaign mode the diffiiculty might have been changed, when back to local set it to whatever selected
            chessCentralControl.asyncController.setComputerDifficulty(App.userManager.userPreferenceManager.getPrefDifficulty());
        }
        // set up board



        chessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, true);


        setMoveLabels(chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), chessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        chessCentralControl.chessActionHandler.highlightMovesPlayedLine(chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex());
        if(currentState == MainScreenState.ONLINE){
            setTimeLabels(chessCentralControl.gameHandler.gameWrapper.getWebGameType());
        }
    }



    public void setupCampaign(PlayerInfo playerInfo, CampaignTier levelTier, int levelOfTier, int campaignDifficuly) {
        chessCentralControl.fullReset(true);
        String campaignOpponentName = levelTier.levelNames[levelOfTier];
        int campaignOpponentElo = levelTier.eloIndexes[levelOfTier];
        String pfpUrl2 = ProfilePicture.values()[levelTier.pfpIndexes[levelOfTier]].urlString;

        this.currentState = MainScreenState.CAMPAIGN;
        // set computer difficulty to closest based on elo
        chessCentralControl.asyncController.setComputerDifficulty(ComputerDifficulty.getDifficultyOffOfElo(campaignOpponentElo, false));

        chessCentralControl.gameHandler.switchToNewGame(
                ChessGame.createSimpleGameWithName("Campaign T:" + (levelTier.ordinal() + 1) + "L: " + levelOfTier,
                        playerInfo, new PlayerInfo(UUID.nameUUIDFromBytes(campaignOpponentName.getBytes()), campaignOpponentName, campaignOpponentElo, pfpUrl2), false), false);
        chessCentralControl.gameHandler.setCampaignAttempt(new CampaignAttempt(new CampaignLevel(levelTier, levelOfTier), campaignDifficuly));

        String campainDiffAsStr = "";
        switch (campaignDifficuly) {
            case 1:
                campainDiffAsStr = "Easy";
                break;
            case 2:
                campainDiffAsStr = "Medium";
                break;
            case 3:
                campainDiffAsStr = "Hard";
                break;
        }


        // in campaign the user always plays white
        setUp((levelTier.ordinal() + 1) + "," + levelOfTier + "," + campainDiffAsStr);
    }

    public void setupWithoutGame(boolean isVsComputer, boolean isWhiteOriented, String gameName, PlayerInfo playerInfo, MainScreenState currentState,boolean playAsWhite) {
        chessCentralControl.fullReset(isWhiteOriented);
        this.currentState = currentState;

        PlayerInfo whitePlayer;
        PlayerInfo blackPlayer;

        if(playAsWhite){
            whitePlayer = playerInfo;
            blackPlayer = ChessConstants.getComputerPlayerInfoWElo(App.userManager.userPreferenceManager.getPrefDifficulty().eloRange);
        }
        else{
            blackPlayer = playerInfo;
            whitePlayer = ChessConstants.getComputerPlayerInfoWElo(App.userManager.userPreferenceManager.getPrefDifficulty().eloRange);
        }

        chessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGameWithName(gameName, whitePlayer, blackPlayer, isWhiteOriented), isVsComputer);

        String extraInfo = "";
        if (currentState == MainScreenState.LOCAL) {
            extraInfo = isVsComputer ? "vs Computer" : "PvP";
        }
        setUp(extraInfo);
    }

    public void setupWithGame(ChessGame gameToSetup, boolean isVsComputer, MainScreenState currentState, boolean isFirstLoad) {
        chessCentralControl.fullReset(gameToSetup.isWhiteOriented());
        this.currentState = currentState;
        chessCentralControl.gameHandler.switchToGame(gameToSetup, isVsComputer, isFirstLoad);

        setUp(gameToSetup.getGameName());
    }

    public void setupPuzzle() {
        chessCentralControl.puzzleGuiManager.loadInNewPuzzle();
    }

    public void preinitOnlineGame(String gameType, ChessGame onlinePreinit) {
        chessCentralControl.fullReset(onlinePreinit.isWhiteOriented());
        this.currentState = MainScreenState.ONLINE;
        chessCentralControl.gameHandler.switchToOnlineGame(onlinePreinit, Gametype.getType(gameType), true);
        App.messager.addLoadingCircle(Window.Main);
        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(ChessGameMessageTypes.ClientRequest.CREATEGAME, new Payload.StringPayload(gameType))));
        setUp(onlinePreinit.getGameName());
    }

    private void checkHideResetButton(MainScreenState currentState) {
        if(MainScreenState.isResetShown(currentState)){
            reset.setVisible(true);
            reset.setMouseTransparent(false);
        }
        else{
            reset.setVisible(false);
            reset.setMouseTransparent(true);
        }
    }

    private void checkHideEvalBar(MainScreenState currentState) {
        // hidden as these are real games
        evalBar.setVisible(MainScreenState.isEvalAllowed(currentState));
    }

    private void checkHideMovesPlayed(MainScreenState currentState) {
        // hidden as these are real games
        movesPlayed.setVisible(MainScreenState.isMovesPlayedShown(currentState));
        movesPlayed.setMouseTransparent(!MainScreenState.isMovesPlayedShown(currentState));
        System.out.println("Is moves played visible: " + MainScreenState.isMovesPlayedShown(currentState));
    }

    private void setResetButtonFunctionality(MainScreenState currentState) {
        if(currentState == MainScreenState.PUZZLE){
            // the reset button will instead act like a next puzzle button
            reset.setText("Next Puzzle");
            reset.setOnMouseClicked(e->{
                chessCentralControl.puzzleGuiManager.loadInNewPuzzle();
            });
        }
        else{
            // else normal behaviour
            reset.setText("Reset");
            reset.setOnMouseClicked(e -> {
                if (chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() != chessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex()) {
                    changeMove(0, true,false,false);
                }
            });
        }
    }

    private void setGameOverButtonFunctionality(MainScreenState currentState){
        if(currentState == MainScreenState.PUZZLE){
            // the reset button will instead act like a next puzzle button
            gameoverHomebutton.setText("Next Puzzle");
            gameoverHomebutton.setOnMouseClicked(e->{
                chessCentralControl.puzzleGuiManager.loadInNewPuzzle();
            });
        }
        else{
            // else normal behaviour
            gameoverHomebutton.setText("Go Home");
            gameoverHomebutton.setOnMouseClicked(e -> {
                HomeReset();
            });
        }
    }



    public void setPlayerIcons(String player1Url, String player2Url, boolean isPlayer1White) {
        ImageView player1 = isPlayer1White ? bottomPlayerIcon : topPlayerIcon;
        ImageView player2 = isPlayer1White ? topPlayerIcon : bottomPlayerIcon;
        player1.setImage(new Image(player1Url));
        player2.setImage(new Image(player2Url));
    }

    public void setPlayerLabels(String whitePlayerName, int whiteElo, String blackPlayerName, int blackElo,boolean isPlayer1White) {
        Label p1Label = isPlayer1White ? bottomPlayerName : topPlayerName;
        Label p2Label = isPlayer1White ? topPlayerName : bottomPlayerName;
        System.out.println(whitePlayerName + " " + whiteElo + " " + blackPlayerName + " " + blackElo);
        if(whiteElo >= 0){
            p1Label.setText(whitePlayerName + " " + whiteElo);
        }
        else{
            p1Label.setText(whitePlayerName);
        }
        if(blackElo >= 0){
            p2Label.setText(blackPlayerName + " " + blackElo);
        }
        else{
            p2Label.setText(blackPlayerName);
        }

    }


    // toggle the pawn promotion screen
    public void showPromo(int promoX, boolean isWhite, boolean isPlayer1White) {
        // reusing piece calculation as you can use it for the promo screen too.
        logger.debug("Showing promo");
        setPromoPeices(isWhite);
        if(!isPlayer1White){
            promoX = 7-promoX; // invert
        }
        DoubleBinding x = chessCentralControl.chessBoardGUIHandler.calcLayoutXBinding(promoX, promoContainer.widthProperty());
        promoContainer.layoutXProperty().bind(x);
        if (!isWhite == isPlayer1White) {
            promoContainer.layoutYProperty().bind(chessBoardContainer.widthProperty().divide(2));
        } else {
            promoContainer.layoutYProperty().bind(new SimpleDoubleProperty(0));
        }

        promotionScreen.setMouseTransparent(!promotionScreen.isMouseTransparent());
        promotionScreen.setVisible(!promotionScreen.isVisible());
    }

    private void hidePromo() {
        promotionScreen.setMouseTransparent(true);
        promotionScreen.setVisible(false);
    }

    private void toggleGameOver() {
        gameoverMenu.setMouseTransparent(!gameoverMenu.isMouseTransparent());
        gameoverMenu.setVisible(!gameoverMenu.isVisible());

    }

    public void hideGameOver() {
        gameoverMenu.setMouseTransparent(true);
        gameoverMenu.setVisible(false);
        victoryLabel.setText("");
    }

    public void showGameOver(String title) {
        gameoverMenu.setMouseTransparent(false);
        gameoverMenu.setVisible(true);
        victoryLabel.setText(title);
    }

    private void toggleSettingsAndGameControls() {
        settingsScroller.setMouseTransparent(!settingsScroller.isMouseTransparent());
        settingsScroller.setVisible(!settingsScroller.isVisible());
        gameMenu.setVisible(!gameMenu.isVisible());
        gameMenu.setMouseTransparent(!gameMenu.isMouseTransparent());

    }

    private void hideSettings() {
        settingsScroller.setMouseTransparent(true);
        settingsScroller.setVisible(false);
    }

    private void showGameControls() {
        gameMenu.setMouseTransparent(false);
        gameMenu.setVisible(true);
    }

    private void setMainControls(MainScreenState currentState, String gameName) {
        hideAllControls();
        if(currentState != MainScreenState.SIMULATION){
            stateLabel.setText(gameName); // game name
        }
        switch (currentState) {
            case VIEWER -> {
                currentControls = viewerControls;
            }
            case LOCAL -> {
                currentControls = localControls;
            }
            case ONLINE -> {
                currentControls = onlineControls;
            }
            case SANDBOX -> {
                currentControls = sandboxControls;
            }
            case CAMPAIGN -> {
                currentControls = campaignControls;
//                String[] split = gameName.split(",");
//                int T = Integer.parseInt(split[0]); // tier
//                int L = Integer.parseInt(split[1]); // level of tier
//                String Diff = split[2]; // difficuly
//                stateLabel.setText(String.format("Campaign T:%d L:%d (%s)", T, L, Diff));
            }
            case SIMULATION -> {
                currentControls = simulationControls;
                stateLabel.setText("Simulation Mode");
            }
            case PUZZLE -> {
                currentControls = puzzleControls;
                stateLabel.setText("Puzzle Mode");
            }
        }
        currentControls.setMouseTransparent(false);
        currentControls.setVisible(true);
    }

    private void hideAllControls() {
        viewerControls.setMouseTransparent(true);
        viewerControls.setVisible(false);
        localControls.setMouseTransparent(true);
        localControls.setVisible(false);
        campaignControls.setMouseTransparent(true);
        campaignControls.setVisible(false);
        simulationControls.setMouseTransparent(true);
        simulationControls.setVisible(false);
        onlineControls.setMouseTransparent(true);
        onlineControls.setVisible(false);
        sandboxControls.setMouseTransparent(true);
        sandboxControls.setVisible(false);
        puzzleControls.setMouseTransparent(true);
        puzzleControls.setVisible(false);
    }

    private void setPromoPeices(boolean isWhite) {
        if (lastPromoWhite != isWhite) {
            // want different color promo peices
            promoContainer.getChildren().clear();
            for (int i = 1; i < 5; i++) {
                ImageView piece = new ImageView(chessCentralControl.chessBoardGUIHandler.createPiecePath(i, isWhite));
                piece.fitWidthProperty().bind(promoContainer.widthProperty().multiply(.9));
                piece.fitHeightProperty().bind(piece.fitWidthProperty());
                piece.setPreserveRatio(true);
                setUpPromoListener(piece, i);
                promoContainer.getChildren().add(piece);
            }
            lastPromoWhite = isWhite;
        }


    }

    // set up the onlick listeners for the pieces a pawn can promote too
    private void setUpPromoListener(ImageView promo, int peiceType) {
        promo.setOnMouseClicked(event -> {
            chessCentralControl.chessActionHandler.promoPawn(peiceType, currentState);
            hidePromo();
        });

    }

    public void changeToAbsoluteMoveIndex(int absIndex) {
        logger.debug("Resetting to abs index:" + absIndex);
        chessCentralControl.partialReset(chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
        chessCentralControl.gameHandler.gameWrapper.moveToMoveIndexAbsolute(absIndex, true);

        setMoveLabels(chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), chessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        updateSimpleAdvantageLabels();

        chessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);
    }

    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset,boolean forceChange,boolean noAnimate) {
        // puzzle check (cannot always change move)
        if(!forceChange && currentState == MainScreenState.PUZZLE){
            int curIndex = chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
            int maxSoFar = chessCentralControl.gameHandler.gameWrapper.getGame().getMaxSoFar();
            if(direction > 0 && curIndex >= maxSoFar){
                // making move will take you over the maxsofar
                return;
            }
        }

        logger.debug("Changing move by " + direction);

        if (isReset) {
            // resets the backend chessgame
            chessCentralControl.fullReset(true);
            chessCentralControl.gameHandler.gameWrapper.reset();
        } else {
            chessCentralControl.gameHandler.gameWrapper.changeToDifferentMove(direction, App.userManager.userPreferenceManager.isAnimationsOff() || noAnimate);
        }

        setMoveLabels(chessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), chessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        updateSimpleAdvantageLabels();
        chessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);

    }

    public void moveToNextPuzzleMove(boolean isDragMove){
        // this also increases visisted index because its a forced change
        chessCentralControl.gameHandler.gameWrapper.getGame().incrementMaxSoFar();
        changeMove(1,false,true,isDragMove);// if you have drag move then you dont animate
    }

    private void clearSimpleAdvantageLabels() {
        bottomAdvantage.setText("");
        topAdvantage.setText("");
    }

    public void updateSimpleAdvantageLabels() {
        boolean isPlayer1White = chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
        Label whiteLabel = isPlayer1White ? bottomAdvantage : topAdvantage;
        Label blackLabel = !isPlayer1White ? bottomAdvantage : topAdvantage;
        clearSimpleAdvantageLabels();
        int simpleAdvantage = AdvancedChessFunctions.getSimpleAdvantage(chessCentralControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
        if (simpleAdvantage > 0) {
            whiteLabel.setText("+" + simpleAdvantage);
        } else if (simpleAdvantage < 0) {
            // flip the sign from a negative to a positive
            blackLabel.setText("+" + simpleAdvantage * -1);
        } else {
            // clear labels as zero advantage
            clearSimpleAdvantageLabels();
        }
    }

    // draw the eval bar for the screen
    public void setEvalBar(double advantage, int depth, boolean gameOver) {
        setEvalBar(whiteEval, blackEval, whiteadvantage, blackadvantage, advantage, evalDepth,chessPieceBoard);
        if (gameOver) {
            String title = "Draw";
            if (advantage > 100000) {
                title = "Winner : White!";
            } else if (advantage < -100000) {
                title = "Winner : Black!";
            }

            showGameOver(title);
        }
        if (depth > 0) {
            evalDepth.setText(Integer.toString(depth));
        }
    }



    public void setMoveLabels(int curIndex, int maxIndex) {
        saveIndicator.setText((curIndex + 1) + "/" + (maxIndex + 1));
    }
    public void setTimeLabels(Gametype gametype) {
        if(gametype == null){
            bottomPlayerTurnTime.setText("");
            topPlayerTurnTime.setText("");
            return;
        }
        bottomPlayerTurnTime.setText(ChessConstants.formatSeconds((int) gametype.getTimeUnit().toSeconds(gametype.getLength())));
        topPlayerTurnTime.setText(ChessConstants.formatSeconds((int) gametype.getTimeUnit().toSeconds(gametype.getLength())));
    }



    public void setSimScore(int numComputerWins, int numStockFishWins, int numDraws,int estimatedElo) {
        if(estimatedElo > 0){
            simulationScore.setText(String.format("My C' Wins: %d S'Fish Wins: %d Draws: %d Estimated elo: %d", numComputerWins, numStockFishWins, numDraws,estimatedElo));
        }
        else{
            simulationScore.setText(String.format("My C' Wins: %d S'Fish Wins: %d Draws: %d Estimated elo: --", numComputerWins, numStockFishWins, numDraws));
        }
    }


    private void setEvalBar(Label whiteEval, Label blackEval, Rectangle whiteBar, Rectangle blackBar, double advantage, Label evalDepth,Region heightReference) {
        double barModPercent = passThroughAsymptote(Math.abs(advantage)) / 5;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if (advantage >= 0) {
            // white advantage or equal position
            if(evalDepth != null){
                evalDepth.setStyle("-fx-text-fill: black");
            }
            if(whiteEval != null && blackEval != null){
                if (advantage < 1000000) {
                    whiteEval.setText(decimalFormat.format(advantage));
                } else {
                    whiteEval.setText("M");

                }
                blackEval.setText("");
            }
            whiteBar.heightProperty().bind(heightReference.heightProperty().divide(2).add(heightReference.heightProperty().divide(2).multiply(barModPercent)));
            blackBar.heightProperty().bind(heightReference.heightProperty().divide(2).multiply(1 - barModPercent));

        } else {
            if (evalDepth != null && advantage < -.2) {
                // change eval depth color to match the black covering it now that the black has an advantage
                evalDepth.setStyle("-fx-text-fill: white");
            }
            if(blackEval != null && whiteEval != null){
                if (advantage > -1000000) {
                    blackEval.setText(decimalFormat.format(advantage));
                } else {
                    blackEval.setText("M");

                }
                whiteEval.setText("");
            }
            blackBar.heightProperty().bind(heightReference.heightProperty().divide(2).add(heightReference.heightProperty().divide(2).multiply(barModPercent)));
            whiteBar.heightProperty().bind(heightReference.heightProperty().divide(2).multiply(1 - barModPercent));


        }


    }

    public void addToEvaluationOverTimeBox(double advantage){
        DoubleBinding widthBinding = Bindings.createDoubleBinding(() -> evalOverTimeBox.getChildren().isEmpty() ? evalOverTimeBox.getWidth() : evalOverTimeBox.getWidth()/evalOverTimeBox.getChildren().size(),evalOverTimeBox.getChildren());
        Rectangle whiteRect = new Rectangle();
        whiteRect.setFill(Paint.valueOf("White"));
        whiteRect.widthProperty().bind(widthBinding);
//        HBox.setHgrow(whiteRect,Priority.ALWAYS);
//        whiteRect.maxWidth(Double.MAX_VALUE);
        Rectangle blackRect = new Rectangle();
        blackRect.setFill(Paint.valueOf("Black"));
        blackRect.widthProperty().bind(widthBinding);
//        HBox.setHgrow(blackRect,Priority.ALWAYS);
//        blackRect.maxWidth(Double.MAX_VALUE);


        setEvalBar(null,null,whiteRect,blackRect,advantage,null,evalOverTimeBox);
        evalOverTimeBox.getChildren().add(new VBox(whiteRect,blackRect));

    }

    // make the growth of the eval bar nonlinear
    private double passThroughAsymptote(double advantage) {
        return (5 * Math.pow(advantage, 2)) / (Math.pow(advantage, 2) + 0.5 * advantage + 10);
    }
    // remove all square highlights


    // what actually happens when you click a square on the board
    public void makeComputerMove(ChessMove move) {
        if (!App.isStartScreen && (currentState == MainScreenState.LOCAL || currentState == MainScreenState.CAMPAIGN) && chessCentralControl.gameHandler.gameWrapper.isVsComputer() && move != null) {
            logger.info("Looking at best move for " + (chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteTurn() ? "WhitePeices" : "BlackPeices"));
            logger.info("Computer thinks move: \n" + move);
            // computers move
            chessCentralControl.chessActionHandler.handleMakingMove(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.isEating(), move.isWhite(), move.isCastleMove(), move.isEnPassant(), true, false, move.getPromoIndx(), currentState, false);

        }

    }


    private void setUpSquareClickEvent(VBox square) {
        square.setOnMouseClicked(event -> {
            // finding which image view was clicked and getting coordinates
            VBox pane = (VBox) event.getSource();
            String[] xy = pane.getUserData().toString().split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            logger.debug(String.format("Square clicked at coordinates X:%d, Y:%d", x, y));
            logger.debug(String.format("Is white turn?: %s", chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteTurn()));
            logger.debug(String.format("Is checkmated?: %b", chessCentralControl.gameHandler.gameWrapper.getGame().getGameState().isCheckMated()[0]));
            if (event.getButton() == MouseButton.PRIMARY) {
                // if the click was a primary click then we want to check if the player can make a move
                // boardinfo:  boardInfo[0] = is there a piece on that square?  boardInfo[1] = is that piece white?
                int backendY = chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? y : 7 - y;
                int backendX = chessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? x : 7 - x;
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, chessCentralControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board, "squareclick");
                logger.debug("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                chessCentralControl.chessActionHandler.handleSquareClick(x, y, boardInfo[0], boardInfo[1], currentState);


            } else if (event.getButton() == MouseButton.SECONDARY) {
                // toggle square highlight with two different types of colors
                // todo: make square highlights different depending on background color
                chessCentralControl.chessBoardGUIHandler.toggleSquareHighlight(x, y, true);
            }

        });
    }

    @Override
    public void fullReset(boolean isWhiteOriented) {
        setEvalBar(0, -1, false);
        clearSimpleAdvantageLabels();
        hidePromo();
        hideSettings();
        showGameControls();
        hideGameOver();
        setMoveLabels(0,0);
        setTimeLabels(null);
        resetLabels();
        partialReset(isWhiteOriented);
    }

    @Override
    public void partialReset(boolean isWhiteOriented){

    }

    private void resetLabels(){
        gameoverTitle.setText("");
        stateLabel.setText("");
        blackEval.setText("");
        whiteEval.setText("");
        victoryLabel.setText("");
        lineLabel.setText("");
        bottomPlayerName.setText("");
        topPlayerName.setText("");
        simulationScore.setText("");

    }

    @Override
    public void resetState(){
        hideAllControls();
        hideGameOver();
        hidePromo();
        hideSettings();
        showGameControls();

    }

    @Override
    public void onOnline(){

    }

    @Override
    public void onOffline(){

    }

    @Override
    public void onLogin(){

    }

    @Override
    public void onLogout(){

    }

    @Override
    public void updateWithUser(UserWGames userWGames) {

    }






    /* Fxml fields and methods*/




    @FXML
    StackPane fullScreen;
    @Override
    public ReadOnlyDoubleProperty getRootWidth() {
        return fullScreen.widthProperty();
    }
    public ReadOnlyDoubleProperty getRootHeight() {
        return fullScreen.heightProperty();
    }
    @Override
    public Region getRoot(){
        return fullScreen;
    }

    @FXML
    Pane mainRef;

    @Override
    public Pane getMessageBoard() {
        return mainRef;
    }

    @FXML
    Group mainGroup;

    @Override
    public Group getMessageGroup() {
        return mainGroup;
    }

    @FXML
    HBox sidePanelPopup;
    @FXML
    VBox sidePanelPopupContent;
    @FXML
    Button hideSidePanel;


    @FXML
    HBox content;
    @FXML
    VBox gameContainer;
    public ReadOnlyDoubleProperty getGameContainerWidth() {
        return gameContainer.widthProperty();
    }
    public ReadOnlyDoubleProperty getGameContainerHeight() {
        return gameContainer.heightProperty();
    }
    public ReadOnlyDoubleProperty getGameContainerLayoutX(){
        return gameContainer.layoutXProperty();
    }



    @FXML
    VBox sidePanelInline;

    /* Chess board and eval bar*/
    @FXML
    HBox chessBoardAndEvalContainer;

    /* Chessboard Top */
    @FXML
    HBox chessBoardTop;
    // eaten pieces
    @FXML
    Label topAdvantage;
    @FXML
    HBox topEatenContainer;
    // player box
    @FXML
    HBox topPlayerBox;
    @FXML
    ImageView topPlayerIcon;
    @FXML
    Label topPlayerName;
    @FXML
    HBox topPlayerLabels;
    @FXML
    Label topPlayerTurnTime;

    @FXML
    HBox topRightSpacer;
    @FXML
    Button openSidePanel;



    /* Chessboard Bottom */
    @FXML
    HBox chessBoardBottom;
    // eaten pieces
    @FXML
    Label bottomAdvantage;
    @FXML
    HBox bottomEatenContainer;
    // player box
    @FXML
    HBox bottomPlayerBox;
    @FXML
    ImageView bottomPlayerIcon;
    @FXML
    Label bottomPlayerName;
    @FXML
    HBox bottomPlayerLabels;
    @FXML
    Label bottomPlayerTurnTime;

    @FXML
    HBox bottomRightSpacer;
    @FXML
    Button inlineHomeButton;



    /* Inner chess board */
    @FXML
    StackPane chessBoardContainer;

    public Region getChessBoard() {
        return chessBoardContainer;
    }
    // grid based overlays that display over squares
    // move ranking circles
    @FXML
    GridPane chessMoveBoard;
    // highlight squares
    @FXML
    GridPane chessHighlightBoard;
    // interaction squares, and the background itself
    @FXML
    GridPane chessBgBoard;

    // overlays that display items
    // chess pieces
    @FXML
    Pane chessPieceBoard;
    // move arrows
    @FXML
    Pane arrowBoard;
    // promotion interactions
    @FXML
    Pane promotionScreen;
    @FXML
    VBox promoContainer;
    // game over interactions
    @FXML
    VBox gameoverMenu;
    @FXML
    Label gameoverTitle;
    @FXML
    Button gameoverHomebutton;
    @FXML
    Label victoryLabel;

    /* Inner Eval bar*/
    @FXML
    VBox evalBar;
    @FXML
    StackPane evalContainer;
    // rectangles that compose the bar
    @FXML
    Rectangle blackadvantage;
    @FXML
    Rectangle whiteadvantage;
    // eval labels
    @FXML
    VBox evalLabelBox;
    @FXML
    Label blackEval;
    @FXML
    Label whiteEval;
    @FXML
    Label evalDepth;

    /* Side panel */
    @FXML
    StackPane sidePanel;

    public ReadOnlyDoubleProperty getSidePanelWidth() {
        return sidePanel.widthProperty();
    }
    public ReadOnlyDoubleProperty getSidePanelHeight() {
        return sidePanel.heightProperty();
    }

    /* Game Menu */
    @FXML
    VBox gameMenu;

    /* Game Controls*/
    @FXML
    VBox gameControls;

    // current game line
    @FXML
    Label lineLabel;

    // moves played overgame
    @FXML
    ScrollPane movesPlayed;
    @FXML
    VBox movesPlayedBox;

    // switching options based on gamemode
    @FXML
    StackPane switchingOptions;



    // online games
    @FXML
    VBox onlineControls;
    @FXML
    TextArea inGameInfo;
    @FXML
    TextField chatInput;
    @FXML
    Button sendMessageButton;
    @FXML
    HBox emojiContainer;
    @FXML
    Button resignButton;
    @FXML
    Button offerDrawButton;

    // viewer games (just "watching")
    @FXML
    VBox viewerControls;
    @FXML
    VBox bestMovesBox;


    @FXML
    VBox localControls;
    @FXML
    HBox evalOverTimeBox;

    @FXML
    VBox campaignControls;
    @FXML
    TextArea campaignInfo;

    @FXML
    VBox simulationControls;
    @FXML
    Button playPauseButton;
    @FXML
    Slider timeSlider;
    @FXML
    Label player1SimLabel;
    @FXML
    Label player2SimLabel;
    @FXML
    ComboBox<Integer> player1SimSelector;
    @FXML
    ComboBox<Integer> player2SimSelector;
    @FXML
    Label simulationScore;

    @FXML
    VBox puzzleControls;
    @FXML
    Slider puzzleEloSlider;
    @FXML
    Label puzzleElo;
    @FXML
    Button hintButton;
    @FXML
    Label puzzleTagsLabel;
    @FXML
    VBox puzzleTagsBox;

    @FXML
    VBox sandboxControls;
    @FXML
    GridPane sandboxPieces;



    /* Bottom Controls */
    @FXML
    VBox bottomControls;
    @FXML
    Button reset;
    @FXML
    Label saveIndicator;

    @FXML
    Button StartOfGameButton;
    @FXML
    Button MoveBackButton;
    @FXML
    Button MoveForwardButton;
    @FXML
    Button EndOfGameButton;

    @FXML
    Label stateLabel;


    /* Navigation */
    @FXML
    HBox navigation;
    @FXML
    Button homeButton;
    @FXML
    Button settingsButton;


    // settings screen
    @FXML
    ScrollPane settingsScroller;
    @FXML
    VBox settingsScreen;

    @Override
    public VBox getSettingsWrapper(){
        return settingsScreen;
    }

    @FXML
    Label currentGamePgnLabel;
    @FXML
    TextArea currentGamePgn;

    @FXML
    Label pgnSaveLabel;
    @FXML
    Button pgnSaveButton;

    @FXML
    Button hideSettings;

}
