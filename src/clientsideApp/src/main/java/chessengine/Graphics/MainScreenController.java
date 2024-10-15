package chessengine.Graphics;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Enums.MainScreenState;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Managers.UserPreferenceManager;
import chessengine.Misc.ChessConstants;
import chessserver.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    private final ImageView[][] peicesAtLocations = new ImageView[8][8];
    public UserPreferences initPreferences = null;
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
    // methods called every new game
    public MainScreenState currentState;
    VBox[][] Bgpanes = new VBox[8][8];
    StackPane[][] highlightPanes = new StackPane[8][8];
    VBox[][] moveBoxes = new VBox[8][8];
    @FXML
    public StackPane fullScreen;
    @FXML
    public GridPane content;
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
    public Pane mainRef;
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
    HBox evalOverTimeBox;
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

    @FXML
    public Label lineLabel; //  current game line

    // moves played area

    @FXML
    StackPane switchingOptions;

    @FXML
    Label player1SimLabel;
    @FXML
    Label player2SimLabel;

    @FXML
    ComboBox<Integer> player1SimSelector;

    @FXML
    ComboBox<Integer> player2SimSelector;

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
    VBox simulationControls;
    @FXML
    VBox sandboxControls;
    @FXML
    VBox bestMovesBox;
    @FXML
    GridPane sandboxPieces;
    @FXML
    TextArea campaignInfo;
    @FXML
    ImageView player1Select;
    @FXML
    ImageView player2Select;
    @FXML
    VBox player1TurnIndicator;
    @FXML
    VBox player2TurnIndicator;
    @FXML
    Label player1MoveClock;
    @FXML
    Label player2MoveClock;
    @FXML
    GridPane mainSidePanel;
    @FXML
    VBox gameControls;
    @FXML
    public StackPane sidePanel;
    @FXML
    VBox topControls;


    // settings screen
    @FXML
    VBox bottomControls;
    @FXML
    Label themeLabel;
    @FXML
    public ChoiceBox<String> themeSelection;
    @FXML
    Label bgLabel;
    @FXML
    public ComboBox<String> bgColorSelector;
    @FXML
    Label pieceLabel;
    @FXML
    public ComboBox<String> pieceSelector;
    @FXML
    Label audioMuteEff;
    @FXML
    public Button audioMuteEffButton;
    @FXML
    Label audioLabelEff;
    @FXML
    public Slider audioSliderEff;
    @FXML
    Label evalLabel;
    @FXML
    public ComboBox<String> evalOptions;

    @FXML
    Label nMovesLabel;
    @FXML
    public ComboBox<String> nMovesOptions;

    @FXML
    Label computerLabel;
    @FXML
    public ComboBox<String> computerOptions;
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
    // simulation controls
    @FXML
    Button playPauseButton;
    @FXML
    Slider timeSlider;
    @FXML
    Label simulationScore;

    @FXML
    TextArea currentGamePgn;
    @FXML
    Label currentGamePgnLabel;


    String[] labelStorage = new String[]{"_", "_"};
    // change promotion peice colors if needed
    boolean lastPromoWhite = false;
    private Logger logger;
    private ChessCentralControl ChessCentralControl;
    private VBox currentControls;


    public chessengine.CentralControlComponents.ChessCentralControl getChessCentralControl() {
        return ChessCentralControl;
    }

    public void endAsync() {
        if (this.ChessCentralControl != null) {
            ChessConstants.mainLogger.debug("Killing threads");
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

        logger.debug("initializing Main Screen");
        setUpPiecesAndListeners();
        ChessCentralControl = App.ChessCentralControl;

        ChessCentralControl.init(this, chessPieceBoard, eatenWhites, eatenBlacks, peicesAtLocations, inGameInfo,
                arrowBoard, bestMovesBox, campaignInfo, sandboxPieces, chatInput, sendMessageButton, Bgpanes, moveBoxes, highlightPanes,
                chessBgBoard, chessHighlightBoard, chessMoveBoard, movesPlayedBox, lineLabel,playPauseButton,timeSlider, player1TurnIndicator,
                player2TurnIndicator, player1MoveClock, player2MoveClock,player1SimSelector,player2SimSelector,currentGamePgn);
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
        gameoverMenu.setBackground(ChessConstants.gameOverBackground);


    }

    public void oneTimeSetup() {
        // called after app's classes are initialized
        setUpBindings();
        setEvalBar(0, -1, false);
        UserPreferenceManager.setupUserSettingsScreen(themeSelection, bgColorSelector, pieceSelector, null, null, audioMuteEffButton, audioSliderEff, evalOptions,nMovesOptions,computerOptions, true);
        ChessCentralControl.chessActionHandler.reset();

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

        setupresetToHome(gameoverHomebutton);
        setupresetToHome(homeButton);


        pgnSaveButton.setOnMouseClicked(e -> {
            String fileName = ChessCentralControl.gameHandler.currentGame.getGameName() + ".txt";
            String pgn = ChessCentralControl.gameHandler.currentGame.gameToPgn();
            logger.debug("Saving game");
            GeneralChessFunctions.saveToFile(fileName, pgn);
        });

        settingsButton.setOnMouseClicked(e -> {
            toggleSettingsAndGameControls();
        });

        LeftReset.setOnMouseClicked(e -> {
            logger.debug("Left Reset clicked");
            if (ChessCentralControl.gameHandler.currentGame.curMoveIndex > -1) {
                changeToAbsoluteMoveIndex(-1);

            }
        });

        LeftButton.setOnMouseClicked(e -> {
            logger.debug("Left button clicked");
            if (ChessCentralControl.gameHandler.currentGame.curMoveIndex >= 0) {
                changeMove(-1, false);

            }


        });

        RightButton.setOnMouseClicked(e -> {
            logger.debug("Right button clicked");
            if (ChessCentralControl.gameHandler.currentGame.curMoveIndex < ChessCentralControl.gameHandler.currentGame.maxIndex) {
                changeMove(1, false);


            }
        });

        RightReset.setOnMouseClicked(e -> {
            logger.debug("right Reset clicked");
            if (ChessCentralControl.gameHandler.currentGame.curMoveIndex < ChessCentralControl.gameHandler.currentGame.maxIndex) {
                changeToAbsoluteMoveIndex(ChessCentralControl.gameHandler.currentGame.maxIndex);

            }
        });

        reset.setOnMouseClicked(e -> {
            if (ChessCentralControl.gameHandler.currentGame.curMoveIndex != -1) {
                changeMove(0, true);
            }
        });
    }

    private void setupresetToHome(Button gameoverHomebutton) {
        gameoverHomebutton.setOnMouseClicked(e -> {
            // clearing all board related stuff

            ChessCentralControl.chessBoardGUIHandler.removeAllPieces();
            ChessCentralControl.chessBoardGUIHandler.resetEverything(true);
            ChessCentralControl.chessActionHandler.reset();


            // stopping async threads (eval bar etc)
            ChessCentralControl.asyncController.stopAll();

            // if the game we are viewing is here for its first time and is not an empty game (maxindex > -1) then we save it)
            if (ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && !currentState.equals(MainScreenState.VIEWER) && !currentState.equals(MainScreenState.SANDBOX) && !currentState.equals(MainScreenState.SIMULATION) && ChessCentralControl.gameHandler.currentGame.maxIndex > -1) {
                PersistentSaveManager.appendGameToAppData(ChessCentralControl.gameHandler.currentGame);
                App.startScreenController.AddNewGameToSaveGui(ChessCentralControl.gameHandler.currentGame);
            }
            // need to leave online game if applicable
            if (ChessCentralControl.gameHandler.currentGame.isWebGame()) {
                ChessCentralControl.gameHandler.currentGame.leaveWebGame();
            }


            // boolean flag for scrolling to the next level
            boolean isNewLvl = false;

            // if we are in campaign mode and the game finished, depending on the outcome we will want to move the player to the next level
            if (currentState.equals(MainScreenState.CAMPAIGN)) {
                // draw will be one star regardless of difficulty
                // other than that only a win will also have you progress
                CampaignTier completedTier = ChessCentralControl.gameHandler.getCampaignTier();
                int completedLevelOfTier = ChessCentralControl.gameHandler.getLevelOfCampaignTier();
                int numStars = 0;
                if (ChessCentralControl.gameHandler.currentGame.gameState.isStaleMated()) {
                    numStars = 1;
                } else if (ChessCentralControl.gameHandler.currentGame.gameState.isCheckMated()[1]) {
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

        });
    }

    public void processChatInput() {
        App.soundPlayer.playEffect(Effect.MESSAGE);
        ChessCentralControl.chessActionHandler.appendNewMessageToChat("(" + ChessCentralControl.gameHandler.currentGame.getWhitePlayerName() + ") " + chatInput.getText());
        if (ChessCentralControl.gameHandler.currentGame.isWebGame() && ChessCentralControl.gameHandler.currentGame.isWebGameInitialized()) {
            App.sendRequest(INTENT.SENDCHAT, chatInput.getText());
        }
        chatInput.clear();
    }

    // for campaign only

    public void setUpBindings() {
        App.bindingController.bindSmallText(currentGamePgnLabel,true,"Black");
        App.bindingController.bindSmallText(currentGamePgn,true,"Black");
        currentGamePgn.prefWidthProperty().bind(settingsScreen.widthProperty());

        // moves played
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(sideAreaFull, movesPlayedBox, 50, .3);

        // pawn  promo
        promoContainer.prefWidthProperty().bind(chessPieceBoard.widthProperty().divide(8));
        promoContainer.prefHeightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        promoContainer.spacingProperty().bind(promoContainer.heightProperty().divide(4).subtract(chessPieceBoard.heightProperty().divide(ChessCentralControl.chessBoardGUIHandler.pieceSize)).divide(4));

        // chess board
        content.prefWidthProperty().bind(fullScreen.widthProperty());
        content.prefHeightProperty().bind(fullScreen.heightProperty());

        chessBoardContainer.prefWidthProperty().bind(Bindings.min(fullScreen.widthProperty().subtract(evalBar.widthProperty()).subtract(sideAreaFull.widthProperty()).subtract(leftSideSpacer.widthProperty()), fullScreen.heightProperty().subtract(eatenBlacks.heightProperty().multiply(2))));
        chessBoardContainer.prefHeightProperty().bind(chessBoardContainer.widthProperty());
        chessPieceBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        chessPieceBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        arrowBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        arrowBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        chessHighlightBoard.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        chessHighlightBoard.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        promotionScreen.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        promotionScreen.prefHeightProperty().bind(chessBoardContainer.heightProperty());


        // side panel
        // simulation controls
        App.bindingController.bindSmallText(simulationScore, true, "Black");
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, playPauseButton, 200, .3);
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(sideAreaFull, playPauseButton, 180, .2);
        // eval bar related
        evalBar.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(chessBoardContainer, evalBar, 75, .1);
        evalContainer.prefHeightProperty().bind(chessBoardContainer.heightProperty());
        evalLabelBox.spacingProperty().bind(evalLabelBox.heightProperty().divide(3));
        // default heights
        whiteadvantage.heightProperty().bind(chessBoardContainer.heightProperty().divide(2));
        blackadvantage.heightProperty().bind(chessBoardContainer.heightProperty().divide(2));

        eatenBlacks.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        eatenWhites.prefWidthProperty().bind(chessBoardContainer.widthProperty());
        // sidepanel stuff
        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessBoardAndEvalContainer.widthProperty()).subtract(leftSideSpacer.widthProperty()));
        sidePanel.prefHeightProperty().bind(content.heightProperty());

        switchingOptions.prefHeightProperty().bind(gameControls.heightProperty().subtract(bottomControls.heightProperty().subtract(lineLabel.heightProperty()).subtract(movesPlayed.heightProperty()).subtract(gameControls.heightProperty().divide(4))));

        App.bindingController.bindSmallText(player1SimLabel,true,"Black");
        App.bindingController.bindSmallText(player2SimLabel,true,"Black");

        sandboxPieces.prefHeightProperty().bind(switchingOptions.heightProperty());

        App.bindingController.bindSmallText(lineLabel,true,"Black");

//        evalOverTimeBox.prefHeightProperty().bind(localInfo.heightProperty());
//        evalOverTimeBox.prefWidthProperty().bind(localInfo.widthProperty());


        // moves played box
        movesPlayed.setFitToHeight(true);
        movesPlayed.prefWidthProperty().bind(sidePanel.widthProperty());

        // all the different side panels
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, campaignInfo, 100, .8);
        App.bindingController.bindSmallText(campaignInfo, true, "Black");

        campaignInfo.prefHeightProperty().bind(switchingOptions.heightProperty());
        App.bindingController.bindSmallText(inGameInfo, true, "black");
        inGameInfo.prefWidthProperty().bind(campaignInfo.widthProperty());
        inGameInfo.prefHeightProperty().bind(campaignInfo.heightProperty().subtract(sendMessageButton.heightProperty()));
        sendMessageButton.prefWidthProperty().bind(inGameInfo.widthProperty().subtract(inGameInfo.widthProperty().divide(8)));
        chatInput.prefWidthProperty().bind(inGameInfo.widthProperty().subtract(sendMessageButton.widthProperty()).subtract(5));
        chatInput.prefHeightProperty().bind(sendMessageButton.heightProperty());


        // side panel labels

        App.bindingController.bindSmallText(stateLabel, true);
        App.bindingController.bindLargeText(victoryLabel, true, "White");
        App.bindingController.bindSmallText(player1Label, true);
        App.bindingController.bindSmallText(player2Label, true);
        App.bindingController.bindSmallText(WhiteNumericalAdv, true);
        App.bindingController.bindSmallText(BlackNumericalAdv, true);
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

        player1TurnIndicator.prefHeightProperty().bind(player1Select.fitHeightProperty().multiply(.8));
        player2TurnIndicator.prefHeightProperty().bind(player1Select.fitHeightProperty().multiply(.8));
        player1TurnIndicator.prefWidthProperty().bind(player1Select.fitWidthProperty().multiply(1.2));
        player2TurnIndicator.prefWidthProperty().bind(player1Select.fitWidthProperty().multiply(1.2));
        player1MoveClock.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateTextVisibility(player1MoveClock, true);
            }
        });

        player2MoveClock.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateTextVisibility(player2MoveClock, false);
            }
        });

//        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        initLabelBindings(bgLabel);
//        initLabelBindings(pieceLabel);
//        initLabelBindings(evalLabel);

        // setting screen
        settingsScroller.prefWidthProperty().bind(sidePanel.widthProperty());
        settingsScroller.prefHeightProperty().bind(sidePanel.heightProperty());

        settingsScreen.prefWidthProperty().bind(sidePanel.widthProperty());

        // game over menu
        App.bindingController.bindXLargeText(gameoverTitle, true, "White");
        App.bindingController.bindXLargeText(victoryLabel, true, "White");

        // miscelaneus buttons
        App.bindingController.bindMediumText(settingsButton, true,"Black");
        App.bindingController.bindMediumText(homeButton, true,"Black");
        App.bindingController.bindMediumText(LeftButton, true);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, LeftButton, 110, .32);
        App.bindingController.bindMediumText(RightButton, true);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, RightButton, 110, .32);
        App.bindingController.bindMediumText(LeftReset, true);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, LeftReset, 140, .36);
        App.bindingController.bindMediumText(RightReset, true);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, RightReset, 140, .36);
        App.bindingController.bindSmallText(saveIndicator, true);
        App.bindingController.bindMediumText(reset, true);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, reset, 240, .45);
//
        App.bindingController.bindSmallText(evalLabel,true);
        App.bindingController.bindSmallText(nMovesLabel,true);
        App.bindingController.bindSmallText(computerLabel,true);
    }

    // label hide ellipsis hack
    private void updateTextVisibility(Label label, boolean isP1Label) {
        Text textNode = new Text(label.getText());
        textNode.setFont(label.getFont());

        // Measure the width of the text
        double textWidth = textNode.getLayoutBounds().getWidth();

        // store cleared text if necessary
        int storageindex = isP1Label ? 0 : 1;

        // Check if the text width exceeds the label's width
        if (textWidth > label.getWidth()) {
            labelStorage[storageindex] = label.getText();
            label.setText(""); // Set text to empty string
        } else {
            if (!labelStorage[storageindex].equals("_")) {
                label.setText(labelStorage[storageindex]); // Set the original stored if was cleared
            }

        }
    }

    /**
     * Setup Steps that are called every game, regardless of campaign or not
     **/
    private void setUp(String extraStuff) {
        ChessCentralControl.clearForNewGame();

        // side panel controls are slightly different for every mode
        setMainControls(currentState, extraStuff);
        // some modes do not want an eval bar
        checkHideEvalBar(currentState);
        checkHideMoveControls(currentState);
        if (currentState.equals(MainScreenState.LOCAL)) {
            // since in campaign mode the diffiiculty might have been changed, when back to local set it to whatever selected
            ChessCentralControl.asyncController.setComputerDifficulty(App.userPreferenceManager.getPrefDifficulty());
        }
        // set up board
        setEvalBar(0, -1, false);
        clearSimpleAdvantageLabels();
        hidePromo();
        hideSettings();
        showGameControlls();
        hideGameOver();
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();


        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, true);


        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex, ChessCentralControl.gameHandler.currentGame.maxIndex);


    }

    public void setupCampaign(String player1Name, int player1Elo, String player1PfpUrl, CampaignTier levelTier, int levelOfTier, int campaignDifficuly) {
        String campaignOpponentName = levelTier.levelNames[levelOfTier];
        int campaignOpponentElo = levelTier.eloIndexes[levelOfTier];
        String pfpUrl2 = ProfilePicture.values()[levelTier.pfpIndexes[levelOfTier]].urlString;

        this.currentState = MainScreenState.CAMPAIGN;
        // set computer difficulty to closest based on elo
        ChessCentralControl.asyncController.setComputerDifficulty(ComputerDifficulty.getDifficultyOffOfElo(campaignOpponentElo, false));

        ChessCentralControl.gameHandler.switchToNewGame(chessengine.ChessRepresentations.ChessGame.createSimpleGameWithName("Campaign T:" + (levelTier.ordinal() + 1) + "L: " + levelOfTier, player1Name, campaignOpponentName, player1Elo, campaignOpponentElo, player1PfpUrl, pfpUrl2, true, true));
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
        this.currentState = currentState;

        // ternary shit show
        String whitePlayerName = playAsWhite ? player1Name : isVsComputer ? "Computer" : "Player 2";
        String blackPlayerName = !playAsWhite ? player1Name : isVsComputer ? "Computer" : "Player 2";

        int whiteElo = playAsWhite ? player1Elo : isVsComputer ? ChessConstants.ComputerEloEstimate : player1Elo;
        int blackElo = !playAsWhite ? player1Elo : isVsComputer ? ChessConstants.ComputerEloEstimate : player1Elo;

        String whitePfpUrl = playAsWhite ? player1PfpUrl : isVsComputer ? ProfilePicture.ROBOT.urlString : player1PfpUrl;
        String blackPfpUrl = !playAsWhite ? player1PfpUrl : isVsComputer ? ProfilePicture.ROBOT.urlString : player1PfpUrl;


        if (gameName.isEmpty()) {
            ChessCentralControl.gameHandler.switchToNewGame(chessengine.ChessRepresentations.ChessGame.createSimpleGame(whitePlayerName,blackPlayerName, whiteElo,blackElo , whitePfpUrl, blackPfpUrl, isVsComputer, isWhiteOriented));
        } else {
            ChessCentralControl.gameHandler.switchToNewGame(chessengine.ChessRepresentations.ChessGame.createSimpleGameWithName(gameName, whitePlayerName, blackPlayerName, whiteElo, blackElo, whitePfpUrl, blackPfpUrl, isVsComputer, isWhiteOriented));
        }
        String extraInfo = "";
        if (currentState.equals(MainScreenState.LOCAL)) {
            extraInfo = isVsComputer ? "vs Computer" : "PvP";
        }
        setUp(extraInfo);
    }

    public void setupWithGame(chessengine.ChessRepresentations.ChessGame gameToSetup, MainScreenState currentState, boolean isFirstLoad) {
        this.currentState = currentState;
        ChessCentralControl.gameHandler.switchToGame(gameToSetup, isFirstLoad);
        String extraStuff = "";
        switch (currentState) {
            case VIEWER -> extraStuff = gameToSetup.getGameName();
            case LOCAL -> extraStuff = gameToSetup.isVsComputer() ? "vs Computer (Pgn)" : "PvP (Pgn)";
            case ONLINE -> extraStuff = gameToSetup.getGameType();
        }
        setUp(extraStuff);
    }

    public void preinitOnlineGame(ChessGame onlinePreinit) {
        this.currentState = MainScreenState.ONLINE;
        // put loading icon
        ChessCentralControl.gameHandler.switchToGame(onlinePreinit, true);
        setUp(onlinePreinit.getGameType());
    }

    private void checkHideMoveControls(MainScreenState currentState) {
//        if (currentState.equals(MainScreenState.SIMULATION)) {
//            // hidden as these are real games
//            bottomControls.setVisible(false);
//            bottomControls.setMouseTransparent(true);
//        } else {
            bottomControls.setVisible(true);
            bottomControls.setMouseTransparent(false);
//        }
    }

    private void checkHideEvalBar(MainScreenState currentState) {
        // hidden as these are real games
        evalBar.setVisible(isEvalAllowed(currentState));
    }

    public boolean isEvalAllowed(MainScreenState currentState){
        return !currentState.equals(MainScreenState.ONLINE) && !currentState.equals(MainScreenState.LOCAL) && !currentState.equals(MainScreenState.CAMPAIGN);
    }

    public void setPlayerIcons(String player1Url, String player2Url, boolean isWhiteOriented) {
        ImageView player1 = isWhiteOriented ? player1Select : player2Select;
        ImageView player2 = isWhiteOriented ? player2Select : player1Select;
        player1.setImage(new Image(player1Url));
        player2.setImage(new Image(player2Url));
    }

    public void setPlayerLabels(String whitePlayerName, int whiteElo, String blackPlayerName, int blackElo,boolean isWhiteOriented) {
        Label p1Label = isWhiteOriented ? player1Label : player2Label;
        Label p2Label = isWhiteOriented ? player2Label : player1Label;
        p1Label.setText(whitePlayerName + " " + whiteElo);
        p2Label.setText(blackPlayerName + " " + blackElo);

    }


    // toggle the pawn promotion screen
    public void showPromo(int promoX, boolean isWhite, boolean isWhiteOriented) {
        // reusing piece calculation as you can use it for the promo screen too.
        logger.debug("Showing promo");
        setPromoPeices(isWhite);
        if(!isWhiteOriented){
            promoX = 7-promoX; // invert
        }
        DoubleBinding x = ChessCentralControl.chessBoardGUIHandler.calcLayoutXBinding(promoX, promoContainer.widthProperty());
        promoContainer.layoutXProperty().bind(x);
        if (!isWhite == isWhiteOriented) {
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
    }

    public void showGameOver(String title) {
        gameoverMenu.setMouseTransparent(false);
        gameoverMenu.setVisible(true);
        victoryLabel.setText(title);
    }

    private void toggleSettingsAndGameControls() {
        settingsScroller.setMouseTransparent(!settingsScroller.isMouseTransparent());
        settingsScroller.setVisible(!settingsScroller.isVisible());
        mainSidePanel.setVisible(!mainSidePanel.isVisible());
        mainSidePanel.setMouseTransparent(!mainSidePanel.isMouseTransparent());

    }

    private void hideSettings() {
        settingsScroller.setMouseTransparent(true);
        settingsScroller.setVisible(false);
    }

    private void showGameControlls() {
        mainSidePanel.setMouseTransparent(false);
        mainSidePanel.setVisible(true);
    }

    private void setMainControls(MainScreenState currentState, String extraStuff) {
        hideAllControls();
        switch (currentState) {
            case VIEWER -> {
                currentControls = viewerControls;
                stateLabel.setText("Viewer: " + extraStuff); // game name
            }
            case LOCAL -> {
                currentControls = localControls;
                stateLabel.setText("Local Game: " + extraStuff); // pvp or pvc (computer)
            }
            case ONLINE -> {
                currentControls = onlineControls;
                stateLabel.setText("Online " + extraStuff + " Game"); // time controls
            }
            case SANDBOX -> {
                currentControls = sandboxControls;
                stateLabel.setText("Sandbox Mode");
            }
            case CAMPAIGN -> {
                String[] split = extraStuff.split(",");
                int T = Integer.parseInt(split[0]); // tier
                int L = Integer.parseInt(split[1]); // level of tier
                String Diff = split[2]; // difficuly
                currentControls = campaignControls;
                stateLabel.setText(String.format("Campaign T:%d L:%d (%s)", T, L, Diff));
            }
            case SIMULATION -> {
                currentControls = simulationControls;
                stateLabel.setText("Simulation Mode");
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
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Resetting to abs index:" + absIndex);

        int curMoveIndex = ChessCentralControl.gameHandler.currentGame.curMoveIndex;
        ChessCentralControl.gameHandler.currentGame.moveToMoveIndexAbsolute(absIndex, false, true);

        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex, ChessCentralControl.gameHandler.currentGame.maxIndex);

        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);
    }

    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset) {
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Changing move by " + direction);
        if (isReset) {
            // resets the backend chessgame
            ChessCentralControl.gameHandler.currentGame.reset();
            ChessCentralControl.clearForNewGame();


        } else {
            ChessCentralControl.gameHandler.currentGame.changeToDifferentMove(direction, false, false);


        }
        setMoveLabels(ChessCentralControl.gameHandler.currentGame.curMoveIndex, ChessCentralControl.gameHandler.currentGame.maxIndex);

        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);

    }

    private void clearSimpleAdvantageLabels() {
        WhiteNumericalAdv.setText("");
        BlackNumericalAdv.setText("");
    }

    public void updateSimpleAdvantageLabels() {
        boolean isWhiteOriented = ChessCentralControl.gameHandler.currentGame.isWhiteOriented();
        Label whiteLabel = isWhiteOriented ? WhiteNumericalAdv : BlackNumericalAdv;
        Label blackLabel = !isWhiteOriented ? WhiteNumericalAdv : BlackNumericalAdv;
        clearSimpleAdvantageLabels();
        int simpleAdvantage = AdvancedChessFunctions.getSimpleAdvantage(ChessCentralControl.gameHandler.currentGame.currentPosition.board);
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

            showGameOver(title );
        }
        if (depth > 0) {
            evalDepth.setText(Integer.toString(depth));
        }
    }



    public void setMoveLabels(int curIndex, int maxIndex) {
        saveIndicator.setText((curIndex + 1) + "/" + (maxIndex + 1));
    }

    public void setSimScore(int numComputerWins, int numStockFishWins, int numDraws,int estimatedElo) {
        simulationScore.setText(String.format("My C' Wins: %d S'Fish Wins: %d Draws: %d Estimated elo: %d", numComputerWins, numStockFishWins, numDraws,estimatedElo));
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
        if (!App.isStartScreen && (currentState.equals(MainScreenState.LOCAL) || currentState.equals(MainScreenState.CAMPAIGN)) && ChessCentralControl.gameHandler.currentGame.isVsComputer() && move != null) {
            logger.info("Looking at best move for " + (ChessCentralControl.gameHandler.currentGame.isWhiteTurn() ? "WhitePeices" : "BlackPeices"));
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
            logger.debug(String.format("Is white turn?: %s", ChessCentralControl.gameHandler.currentGame.isWhiteTurn()));
            logger.debug(String.format("Is checkmated?: %b", ChessCentralControl.gameHandler.currentGame.gameState.isCheckMated()[0]));
            if (event.getButton() == MouseButton.PRIMARY) {
                // if the click was a primary click then we want to check if the player can make a move
                // boardinfo:  boardInfo[0] = is there a piece on that square?  boardInfo[1] = is that piece white?
                int backendY = ChessCentralControl.gameHandler.currentGame.isWhiteOriented() ? y : 7 - y;
                int backendX = ChessCentralControl.gameHandler.currentGame.isWhiteOriented() ? x : 7 - x;
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, ChessCentralControl.gameHandler.currentGame.currentPosition.board, "squareclick");
                logger.debug("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                ChessCentralControl.chessActionHandler.handleSquareClick(x, y, boardInfo[0], boardInfo[1], currentState);


            } else if (event.getButton() == MouseButton.SECONDARY) {
                // toggle square highlight with two different types of colors
                // todo: make square highlights different depending on background color
                ChessCentralControl.chessBoardGUIHandler.toggleSquareHighlight(x, y, true);
            }

        });
    }

    // checks if square player wants to move is in the moves allowed for that peice


}
