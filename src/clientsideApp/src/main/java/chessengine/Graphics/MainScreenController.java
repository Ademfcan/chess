package chessengine.Graphics;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.CentralControlComponents.Resettable;
import chessengine.Enums.Window;
import chessengine.Misc.Constants;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessMove;
import chessengine.Enums.MainScreenState;
import chessserver.Enums.*;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessengine.Managers.UserPreferenceManager;
import chessserver.Misc.ChessConstants;
import chessserver.User.UserPreferences;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable, Resettable, AppWindow {

    @FXML
    StackPane fullScreen;
    public ReadOnlyDoubleProperty getWindowWidth() {
        return fullScreen.widthProperty();
    }
    public ReadOnlyDoubleProperty getWindowHeight() {
        return fullScreen.heightProperty();
    }
    public Region getWindow(){
        return fullScreen;
    }

    @FXML
    Pane mainRef;

    public Pane getMessageBoard() {
        return mainRef;
    }

    @FXML
    HBox sidePanelPopup;
    @FXML
    VBox sidePanelPopupContent;
    @FXML
    Button hideSidePanel;
    @FXML
    Button openSidePanel;


    @FXML
    HBox content;
    @FXML
    VBox gameContainer;
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
    HBox topPlayerTurnContainer;
    @FXML
    VBox topPlayerTurnIndicator;
    @FXML
    Label topPlayerTurnTime;

    @FXML
    HBox topRightSpacer;



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
    HBox bottomPlayerTurnContainer;
    @FXML
    VBox bottomPlayerTurnIndicator;
    @FXML
    Label bottomPlayerTurnTime;

    @FXML
    HBox bottomRightSpacer;



    /* Inner chess board */
    @FXML
    StackPane chessBoardContainer;

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
    HBox movesPlayedBox;

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
    Button homeButton;
    @FXML
    Button settingsButton;


    // settings screen
    @FXML
    ScrollPane settingsScroller;
    @FXML
    VBox settingsScreen;

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
    ComboBox<String> evalOptions;

    @FXML
    Label nMovesLabel;
    @FXML
    ComboBox<String> nMovesOptions;

    @FXML
    Label computerLabel;
    @FXML
    ComboBox<String> computerOptions;

    @FXML
    TextArea currentGamePgn;
    @FXML
    Label currentGamePgnLabel;

    @FXML
    Label pgnSaveLabel;
    @FXML
    Button pgnSaveButton;

    @FXML
    Button hideSettings;

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
    private Logger logger;
    private ChessCentralControl ChessCentralControl;
    private VBox currentControls;


    public chessengine.CentralControlComponents.ChessCentralControl getChessCentralControl() {
        return ChessCentralControl;
    }

    public void endAsync() {
        if (this.ChessCentralControl != null) {
            logger.debug("Killing threads");
            ChessCentralControl.asyncController.killAll();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentGamePgn.setEditable(false);

        // some elements need mouse transparency because they are on top of control elements
        mainRef.setMouseTransparent(true);
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
        gameoverMenu.toFront();
        evalBar.setTranslateZ(-1);

        logger.debug("initializing Main Screen");
        setUpPiecesAndListeners();
        ChessCentralControl = App.ChessCentralControl;

        ChessCentralControl.init(this, chessPieceBoard, bottomEatenContainer, topEatenContainer, peicesAtLocations, inGameInfo,
                arrowBoard, bestMovesBox, campaignInfo, sandboxPieces, chatInput, sendMessageButton,emojiContainer,resignButton,offerDrawButton, Bgpanes, moveBoxes, highlightPanes,
                chessBgBoard, chessHighlightBoard, movesPlayedBox,movesPlayed, lineLabel,playPauseButton,timeSlider, bottomPlayerTurnIndicator,
                topPlayerTurnIndicator, bottomPlayerTurnTime, topPlayerTurnTime,player1SimSelector,player2SimSelector,currentGamePgn,puzzleEloSlider,puzzleElo,hintButton,puzzleTagsBox);
//         small change to make sure moves play box is always focused on the very end
        movesPlayedBox.getChildren().addListener((ListChangeListener<Node>) change -> {

            // makes sure its always at the end
            movesPlayed.setHvalue(1);
        });

        // main settings screen also has a pgn options button


        setPromoPeices(true);
        setUpButtons();
        setUpDragAction();

        if (initPreferences != null) {
//            ChessCentralControl.asyncController.setComputerDepth(initPreferences.getComputerMoveDepth()); // todo
            ChessCentralControl.chessBoardGUIHandler.changeChessBg(initPreferences.getChessboardTheme().toString());
        }


        // game over menu config
        gameoverMenu.setBackground(Constants.gameOverBackground);

        hideSidePanelPopup();
        sidePanelPopupContent.getStyleClass().add("root");

        handleSidePanelChange();


    }

    public void oneTimeSetup() {
        // called after app's classes are initialized
        setUpLayout();
        setupIcons();
        setEvalBar(0, -1, false);
        UserPreferenceManager.setupUserSettingsScreen(themeSelection, bgColorSelector, pieceSelector, null, null, audioMuteEffButton, audioSliderEff, evalOptions,nMovesOptions,computerOptions, Window.Start);
        ChessCentralControl.chessActionHandler.init();
        ChessCentralControl.puzzleGuiManager.init();
        ChessCentralControl.fullReset(true);

    }

    private void setUpDragAction() {
        chessBgBoard.setOnMouseDragged(e -> {
            ChessCentralControl.chessActionHandler.handleBoardDrag(e);
        });
        chessBgBoard.setOnMouseReleased(e -> {
            ChessCentralControl.chessActionHandler.handleBoardRelease(e, currentState);

        });
        chessBgBoard.setOnMousePressed(e -> {
            ChessCentralControl.chessActionHandler.handleBoardPress(e, currentState);
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
            String fileName = ChessCentralControl.gameHandler.gameWrapper.getGame().getGameName() + ".txt";
            String pgn = ChessCentralControl.gameHandler.gameWrapper.getGame().gameToPgn();
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
            int minIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex();
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > minIndex) {
                changeToAbsoluteMoveIndex(minIndex);

            }
        });

        MoveBackButton.setOnMouseClicked(e -> {
            logger.debug("Left button clicked");
            int minIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex();
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > minIndex) {
                changeMove(-1, false,false,false);

            }


        });

        MoveForwardButton.setOnMouseClicked(e -> {
            logger.debug("Right button clicked");
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() < ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                changeMove(1, false,false,false);


            }
        });

        EndOfGameButton.setOnMouseClicked(e -> {
            logger.debug("right Reset clicked");
            if (currentState != MainScreenState.PUZZLE && ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() < ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                changeToAbsoluteMoveIndex(ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());

            }
        });

        openSidePanel.setOnMouseClicked(e -> {
            showSidePanelPopup();
        });

        hideSidePanel.setOnMouseClicked(e -> {
            hideSidePanelPopup();
        });

    }

    /** Clears current game, performs cleanup and then goes back to start screen**/
    public void HomeReset(){
        // clearing all board related stuff
        // stopping async threads (eval bar etc)
        ChessCentralControl.asyncController.stopAll();

        // if the game we are viewing is here for its first time and is not an empty game (maxindex > -1) then we save it)
        if (ChessCentralControl.gameHandler.currentlyGameActive() && ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && MainScreenState.isSaveableState(currentState) && ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex() > ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex()) {
//                PersistentSaveManager.appendGameToAppData(ChessCentralControl.gameHandler.currentGame);
            App.startScreenController.AddNewGameToSaveGui(ChessCentralControl.gameHandler.gameWrapper.getGame(),App.startScreenController.oldGamesPanelContent);
            App.startScreenController.AddNewGameToSaveGui(ChessCentralControl.gameHandler.gameWrapper.getGame(),App.startScreenController.userOldGamesContent);
            App.userManager.saveUserGame(ChessCentralControl.gameHandler.gameWrapper.getGame());
        }
        // need to leave online game if applicable
        if (ChessCentralControl.gameHandler.gameWrapper.isActiveWebGame()) {
            System.out.println("Leaving game!");
            ChessCentralControl.gameHandler.gameWrapper.leaveWebGame();
        }
        else if(ChessCentralControl.gameHandler.gameWrapper.isWebGame() && !ChessCentralControl.gameHandler.gameWrapper.isWebGameInitialized()){
            App.sendRequest(INTENT.LEAVEWAITINGPOOL,"",null,true);
            App.messager.removeLoadingCircles(Window.Main);
        }


        // boolean flag for scrolling to the next level
        boolean isNewLvl = false;

        // if we are in campaign mode and the game finished, depending on the outcome we will want to move the player to the next level
        if (currentState == MainScreenState.CAMPAIGN) {
            // draw will be one star regardless of difficulty
            // other than that only a win will also have you progress
            CampaignTier completedTier = ChessCentralControl.gameHandler.getCampaignTier();
            int completedLevelOfTier = ChessCentralControl.gameHandler.getLevelOfCampaignTier();
            int numStars = 0;
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getGameState().isStaleMated()) {
                numStars = 1;
            } else if (ChessCentralControl.gameHandler.gameWrapper.getGame().getGameState().isCheckMated()[1]) {
                // game over only happens if draw or checkmate
                // so if not draw it must be checkmate
                // so we only have to check if white(the user) won
                numStars = ChessCentralControl.gameHandler.getGameDifficulty();
            }

            if (numStars != 0) {
                // now if the user earned any stars make sure you add them
                // btw this will not decrement your level if you have done better before
                App.userManager.setLevelStars(completedTier, completedLevelOfTier, numStars);

                // now if this was a new level for them(and they were sucessful), unlock the next one
                if (completedTier.equals(App.userManager.getCurrentCampaignTier()) && completedLevelOfTier == App.userManager.getCurrentCampaignLevel()) {
                    isNewLvl = true;
                    App.userManager.moveToNextLevel();
                }

            }


        }
        ChessCentralControl.gameHandler.clearGame();
        App.changeToStart();
        // in campaign mode as you move to the next level, scroll up to that level
        if (isNewLvl) {
            App.startScreenController.campaignManager.scrollToPlayerTier(App.userManager.getCampaignProgress());
        }
    }

    public void processChatInput() {
        if(!chatInput.getText().isBlank()){
            App.soundPlayer.playEffect(Effect.MESSAGE);
            boolean isWhiteOriented = ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
            ChessCentralControl.chessActionHandler.appendNewMessageToChat("(" + (isWhiteOriented ? ChessCentralControl.gameHandler.gameWrapper.getGame().getWhitePlayerName() : ChessCentralControl.gameHandler.gameWrapper.getGame().getBlackPlayerName()) + ") " + chatInput.getText());
            if (ChessCentralControl.gameHandler.gameWrapper.isActiveWebGame() && ChessCentralControl.gameHandler.gameWrapper.isCurrentWebGameInitialized()) {
                App.sendRequest(INTENT.SENDCHAT, chatInput.getText(),null,true);
            }
            chatInput.clear();
        }
        else{
            logger.debug("Not sending chat, empty input");
        }
    }

    // for campaign only

    public void setUpLayout() {
        // game container
        gameContainer.prefHeightProperty().bind(getWindowHeight());
        gameContainer.prefWidthProperty().bind(chessBoardAndEvalContainer.widthProperty());

        chessBoardAndEvalContainer.prefWidthProperty().bind(chessBoardContainer.widthProperty().add(evalBar.widthProperty()));
        chessBoardAndEvalContainer.prefHeightProperty().bind(chessBoardContainer.heightProperty());

        // chessboard Top
        topPlayerIcon.fitHeightProperty().bind(chessBoardTop.heightProperty().multiply(.8));
        topPlayerIcon.fitWidthProperty().bind(topPlayerIcon.fitHeightProperty());
        topPlayerTurnIndicator.prefHeightProperty().bind(chessBoardTop.heightProperty());
        App.bindingController.bindSmallText(topPlayerName, Window.Main);
        topPlayerName.setTextOverrun(OverrunStyle.CLIP);
        TextUtils.addTooltipOnElipsis(topPlayerName);
        // chessboard Bottom
        bottomPlayerIcon.fitHeightProperty().bind(chessBoardBottom.heightProperty().multiply(.8));
        bottomPlayerIcon.fitWidthProperty().bind(bottomPlayerIcon.fitHeightProperty());
        bottomPlayerTurnIndicator.prefHeightProperty().bind(chessBoardBottom.heightProperty());
        App.bindingController.bindSmallText(bottomPlayerName, Window.Main);
        bottomPlayerName.setTextOverrun(OverrunStyle.CLIP);
        TextUtils.addTooltipOnElipsis(bottomPlayerName);



        // fixed square chess board
        chessBoardContainer.prefWidthProperty().bind(Bindings.min(
                getWindowWidth()
                    .subtract(evalBar.widthProperty()),
                getWindowHeight()
                    .subtract(chessBoardTop.heightProperty())
                    .subtract(chessBoardBottom.heightProperty())));
        chessBoardContainer.prefHeightProperty().bind(chessBoardContainer.widthProperty());

        chessBoardTop.prefWidthProperty().bind(gameContainer.widthProperty());
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(gameContainer, chessBoardTop, 75, .10);
        chessBoardBottom.prefWidthProperty().bind(gameContainer.widthProperty());
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(gameContainer, chessBoardBottom, 75, .10);


        topEatenContainer.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        App.bindingController.bindSmallText(topAdvantage, Window.Main);
        bottomEatenContainer.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        App.bindingController.bindSmallText(bottomAdvantage, Window.Main);


        // eval bar related
        evalBar.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(chessBoardContainer, evalBar, 75, .1);
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
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(gameContainer, sidePanel, 300, 0.6);
        sidePanel.prefHeightProperty().bind(getWindowHeight());

        sidePanelInline.prefHeightProperty().bind(getWindowHeight());
        sidePanelInline.prefWidthProperty().bind(getWindowWidth().subtract(gameContainer.widthProperty()));

        // configure inline side panel visibility
        sidePanelInline.visibleProperty().bind(getWindowWidth().greaterThan(gameContainer.widthProperty().add(sidePanel.prefWidthProperty())));
        sidePanelInline.managedProperty().bind(sidePanelInline.visibleProperty());

        openSidePanel.visibleProperty().bind(sidePanelInline.visibleProperty().not().and(sidePanelPopup.visibleProperty().not()));
        openSidePanel.managedProperty().bind(openSidePanel.visibleProperty());

        getWindowWidth().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::handleSidePanelChange);
        });
        getWindowHeight().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::handleSidePanelChange);
        });

        sidePanelPopup.prefWidthProperty().bind(sidePanel.widthProperty().add(hideSidePanel.widthProperty()));


        // simulation controls
        App.bindingController.bindSmallText(simulationScore, Window.Main, "Black");
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, playPauseButton, 200, .3);
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(sidePanel, playPauseButton, 180, .2);


        // sidepanel stuff
        App.bindingController.bindSmallText(player1SimLabel,Window.Main,"Black");
        App.bindingController.bindSmallText(player2SimLabel,Window.Main,"Black");

        sandboxPieces.prefHeightProperty().bind(switchingOptions.heightProperty());

        App.bindingController.bindSmallText(lineLabel,Window.Main,"Black");

//        evalOverTimeBox.prefHeightProperty().bind(localInfo.heightProperty());
//        evalOverTimeBox.prefWidthProperty().bind(localInfo.widthProperty());

        App.bindingController.bindSmallText(currentGamePgnLabel,Window.Main,"Black");
        App.bindingController.bindSmallText(currentGamePgn,Window.Main,"Black");
        currentGamePgn.prefWidthProperty().bind(settingsScreen.widthProperty());

        // moves played
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(sidePanel, movesPlayedBox, 50, .3);

        // pawn  promo
        promoContainer.prefWidthProperty().bind(chessPieceBoard.widthProperty().divide(8));
        promoContainer.prefHeightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        promoContainer.spacingProperty().bind(promoContainer.heightProperty().divide(4).subtract(chessPieceBoard.heightProperty().divide(ChessCentralControl.chessBoardGUIHandler.pieceSize)).divide(4));



        // moves played box
        movesPlayed.setFitToHeight(true);
        movesPlayed.prefWidthProperty().bind(getSidePanelWidth());

        // all the different side panels
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, campaignInfo, 100, .8);
        App.bindingController.bindSmallText(campaignInfo, Window.Main, "Black");

        campaignInfo.prefHeightProperty().bind(switchingOptions.heightProperty());


        App.bindingController.bindSmallText(inGameInfo, Window.Main, "black");
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

        App.bindingController.bindSmallText(stateLabel, Window.Main);
        App.bindingController.bindLargeText(victoryLabel, Window.Main, "White");



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
        App.bindingController.bindXLargeText(gameoverTitle, Window.Main, "White");
        App.bindingController.bindXLargeText(victoryLabel, Window.Main, "White");

        // miscelaneus buttons
        App.bindingController.bindMediumText(settingsButton, Window.Main,"Black");
        App.bindingController.bindMediumText(homeButton, Window.Main,"Black");
        App.bindingController.bindMediumText(MoveBackButton, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, MoveBackButton, 110, .32);
        App.bindingController.bindMediumText(MoveForwardButton, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, MoveForwardButton, 110, .32);
        App.bindingController.bindMediumText(StartOfGameButton, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, StartOfGameButton, 140, .36);
        App.bindingController.bindMediumText(EndOfGameButton, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, EndOfGameButton, 140, .36);
        App.bindingController.bindSmallText(saveIndicator, Window.Main);
        App.bindingController.bindMediumText(reset, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, reset, 240, .45);
//
        App.bindingController.bindSmallText(evalLabel,Window.Main);
        App.bindingController.bindSmallText(nMovesLabel,Window.Main);
        App.bindingController.bindSmallText(computerLabel,Window.Main);


        App.bindingController.bindSmallText(puzzleTagsLabel,Window.Main);
    }

    public void handleSidePanelChange(){

        boolean newSidePanelInline = getWindowWidth().doubleValue() > gameContainer.getPrefWidth() + sidePanel.getPrefWidth();
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
            ChessCentralControl.asyncController.setComputerDifficulty(App.userPreferenceManager.getPrefDifficulty());
        }
        // set up board



        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, true);


        setMoveLabels(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        ChessCentralControl.chessActionHandler.highlightMovesPlayedLine(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex());
        if(currentState == MainScreenState.ONLINE){
            setTimeLabels(ChessCentralControl.gameHandler.gameWrapper.getWebGameType());
        }
    }



    public void setupCampaign(String player1Name, int player1Elo, String player1PfpUrl, CampaignTier levelTier, int levelOfTier, int campaignDifficuly) {
        ChessCentralControl.fullReset(true);
        String campaignOpponentName = levelTier.levelNames[levelOfTier];
        int campaignOpponentElo = levelTier.eloIndexes[levelOfTier];
        String pfpUrl2 = ProfilePicture.values()[levelTier.pfpIndexes[levelOfTier]].urlString;

        this.currentState = MainScreenState.CAMPAIGN;
        // set computer difficulty to closest based on elo
        ChessCentralControl.asyncController.setComputerDifficulty(ComputerDifficulty.getDifficultyOffOfElo(campaignOpponentElo, false));

        ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGameWithName("Campaign T:" + (levelTier.ordinal() + 1) + "L: " + levelOfTier, player1Name, campaignOpponentName, player1Elo, campaignOpponentElo, player1PfpUrl, pfpUrl2, true, true));
        ChessCentralControl.gameHandler.setGameDifficulty(campaignDifficuly);
        ChessCentralControl.gameHandler.setCampaignTier(levelTier);
        ChessCentralControl.gameHandler.setLevelOfCampaignTier(levelOfTier);

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

    public void setupWithoutGame(boolean isVsComputer, boolean isWhiteOriented, String gameName, String player1Name, int player1Elo, String player1PfpUrl, MainScreenState currentState,boolean playAsWhite) {
        ChessCentralControl.fullReset(isWhiteOriented);
        this.currentState = currentState;

        // ternary shit show
        String whitePlayerName = playAsWhite ? player1Name : isVsComputer ? "Computer" : "Player 2";
        String blackPlayerName = !playAsWhite ? player1Name : isVsComputer ? "Computer" : "Player 2";

        int whiteElo = playAsWhite ? player1Elo : isVsComputer ? App.userPreferenceManager.getPrefDifficulty().eloRange : player1Elo;
        int blackElo = !playAsWhite ? player1Elo : isVsComputer ? App.userPreferenceManager.getPrefDifficulty().eloRange : player1Elo;

        String whitePfpUrl = playAsWhite ? player1PfpUrl : isVsComputer ? ProfilePicture.ROBOT.urlString : player1PfpUrl;
        String blackPfpUrl = !playAsWhite ? player1PfpUrl : isVsComputer ? ProfilePicture.ROBOT.urlString : player1PfpUrl;


        if (gameName.isEmpty()) {
            ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGame(whitePlayerName,blackPlayerName, whiteElo,blackElo , whitePfpUrl, blackPfpUrl, isVsComputer, isWhiteOriented));
        } else {
            ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGameWithName(gameName, whitePlayerName, blackPlayerName, whiteElo, blackElo, whitePfpUrl, blackPfpUrl, isVsComputer, isWhiteOriented));
        }
        String extraInfo = "";
        if (currentState == MainScreenState.LOCAL) {
            extraInfo = isVsComputer ? "vs Computer" : "PvP";
        }
        setUp(extraInfo);
    }

    public void setupWithGame(ChessGame gameToSetup, MainScreenState currentState, boolean isFirstLoad) {
        ChessCentralControl.fullReset(gameToSetup.isWhiteOriented());
        this.currentState = currentState;
        ChessCentralControl.gameHandler.switchToGame(gameToSetup, isFirstLoad);
        String extraStuff = "";
        switch (currentState) {
            case VIEWER -> extraStuff = gameToSetup.getGameName();
            case LOCAL -> extraStuff = gameToSetup.isVsComputer() ? "vs Computer (Pgn)" : "PvP (Pgn)";
            case ONLINE -> extraStuff = gameToSetup.getGameName();
        }
        setUp(extraStuff);
    }

    public void setupPuzzle() {
        ChessCentralControl.puzzleGuiManager.loadInNewPuzzle();
    }

    public void preinitOnlineGame(String gameType,ChessGame onlinePreinit) {
        ChessCentralControl.fullReset(onlinePreinit.isWhiteOriented());
        this.currentState = MainScreenState.ONLINE;
        ChessCentralControl.gameHandler.switchToOnlineGame(onlinePreinit, Gametype.getType(gameType), true);
        App.messager.addLoadingCircle(Window.Main);
        App.createOnlineGameRequest(gameType,onlinePreinit);

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
                ChessCentralControl.puzzleGuiManager.loadInNewPuzzle();
            });
        }
        else{
            // else normal behaviour
            reset.setText("Reset");
            reset.setOnMouseClicked(e -> {
                if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() != ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex()) {
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
                ChessCentralControl.puzzleGuiManager.loadInNewPuzzle();
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
        DoubleBinding x = ChessCentralControl.chessBoardGUIHandler.calcLayoutXBinding(promoX, promoContainer.widthProperty());
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
                ImageView piece = new ImageView(ChessCentralControl.chessBoardGUIHandler.createPiecePath(i, isWhite));
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
            ChessCentralControl.chessActionHandler.promoPawn(peiceType, currentState);
            hidePromo();
        });

    }

    public void changeToAbsoluteMoveIndex(int absIndex) {
        logger.debug("Resetting to abs index:" + absIndex);
        ChessCentralControl.partialReset(ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
        ChessCentralControl.gameHandler.gameWrapper.moveToMoveIndexAbsolute(absIndex, true);

        setMoveLabels(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        updateSimpleAdvantageLabels();

        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);
    }

    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset,boolean forceChange,boolean noAnimate) {
        // puzzle check (cannot always change move)
        if(!forceChange && currentState == MainScreenState.PUZZLE){
            int curIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
            int maxSoFar = ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxSoFar();
            if(direction > 0 && curIndex >= maxSoFar){
                // making move will take you over the maxsofar
                return;
            }
        }

        logger.debug("Changing move by " + direction);

        if (isReset) {
            // resets the backend chessgame
            ChessCentralControl.gameHandler.gameWrapper.reset();
        } else {
            ChessCentralControl.gameHandler.gameWrapper.changeToDifferentMove(direction, App.userPreferenceManager.isNoAnimate() || noAnimate);
        }

        setMoveLabels(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);

    }

    public void moveToNextPuzzleMove(boolean isDragMove){
        // this also increases visisted index because its a forced change
        ChessCentralControl.gameHandler.gameWrapper.getGame().incrementMaxSoFar();
        changeMove(1,false,true,isDragMove);// if you have drag move then you dont animate
    }

    private void clearSimpleAdvantageLabels() {
        bottomAdvantage.setText("");
        topAdvantage.setText("");
    }

    public void updateSimpleAdvantageLabels() {
        boolean isPlayer1White = ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
        Label whiteLabel = isPlayer1White ? bottomAdvantage : topAdvantage;
        Label blackLabel = !isPlayer1White ? bottomAdvantage : topAdvantage;
        clearSimpleAdvantageLabels();
        int simpleAdvantage = AdvancedChessFunctions.getSimpleAdvantage(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
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
        if (!App.isStartScreen && (currentState == MainScreenState.LOCAL || currentState == MainScreenState.CAMPAIGN) && ChessCentralControl.gameHandler.gameWrapper.getGame().isVsComputer() && move != null) {
            logger.info("Looking at best move for " + (ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteTurn() ? "WhitePeices" : "BlackPeices"));
            logger.info("Computer thinks move: \n" + move);
            // computers move
            ChessCentralControl.chessActionHandler.handleMakingMove(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.isEating(), move.isWhite(), move.isCastleMove(), move.isEnPassant(), true, false, move.getPromoIndx(), currentState, false);

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
            logger.debug(String.format("Is white turn?: %s", ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteTurn()));
            logger.debug(String.format("Is checkmated?: %b", ChessCentralControl.gameHandler.gameWrapper.getGame().getGameState().isCheckMated()[0]));
            if (event.getButton() == MouseButton.PRIMARY) {
                // if the click was a primary click then we want to check if the player can make a move
                // boardinfo:  boardInfo[0] = is there a piece on that square?  boardInfo[1] = is that piece white?
                int backendY = ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? y : 7 - y;
                int backendX = ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? x : 7 - x;
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, ChessCentralControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board, "squareclick");
                logger.debug("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                ChessCentralControl.chessActionHandler.handleSquareClick(x, y, boardInfo[0], boardInfo[1], currentState);


            } else if (event.getButton() == MouseButton.SECONDARY) {
                // toggle square highlight with two different types of colors
                // todo: make square highlights different depending on background color
                ChessCentralControl.chessBoardGUIHandler.toggleSquareHighlight(x, y, true);
            }

        });
    }

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

    public void setDefaultSelections(UserPreferences userPref) {
        themeSelection.getSelectionModel().select(userPref.getGlobalTheme().toString());
        computerOptions.getSelectionModel().select(userPref.getComputerMoveDiff().eloRange + (userPref.getComputerMoveDiff().isStockfishBased ? "(S*)" : ""));
        evalOptions.getSelectionModel().select(userPref.getEvalStockfishBased() ? "Stockfish" : "My Computer");
        nMovesOptions.getSelectionModel().select(userPref.getNMovesStockfishBased() ? "Stockfish" : "My Computer");
        audioSliderEff.setValue(userPref.getEffectVolume());
        bgColorSelector.setValue(userPref.getChessboardTheme().toString());
        pieceSelector.setValue(userPref.getPieceTheme().toString());
        audioMuteEffButton.setText(userPref.isEffectSounds() ? "🔉" : "✖");
    }
}
