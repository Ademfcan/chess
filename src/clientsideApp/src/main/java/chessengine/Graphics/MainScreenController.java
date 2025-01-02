package chessengine.Graphics;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.CentralControlComponents.ChessCentralControl;
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
    VBox puzzleControls;
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
    @FXML
    Button resignButton;
    @FXML
    Button offerDrawButton;
    // simulation controls
    @FXML
    Button playPauseButton;
    @FXML
    Slider timeSlider;
    @FXML
    Label simulationScore;

    @FXML
    Slider puzzleEloSlider;
    @FXML
    Label puzzleElo;
    @FXML
    Button nextPuzzleButton;
    @FXML
    Label puzzleTagsLabel;
    @FXML
    VBox puzzleTagsBox;

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

        logger.debug("initializing Main Screen");
        setUpPiecesAndListeners();
        ChessCentralControl = App.ChessCentralControl;

        ChessCentralControl.init(this, chessPieceBoard, eatenWhites, eatenBlacks, peicesAtLocations, inGameInfo,
                arrowBoard, bestMovesBox, campaignInfo, sandboxPieces, chatInput, sendMessageButton,resignButton,offerDrawButton, Bgpanes, moveBoxes, highlightPanes,
                chessBgBoard, chessHighlightBoard, chessMoveBoard, movesPlayedBox,movesPlayed, lineLabel,playPauseButton,timeSlider, player1TurnIndicator,
                player2TurnIndicator, player1MoveClock, player2MoveClock,player1SimSelector,player2SimSelector,currentGamePgn,puzzleEloSlider,puzzleElo,nextPuzzleButton,puzzleTagsBox);
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


    }

    public void oneTimeSetup() {
        // called after app's classes are initialized
        setUpBindings();
        setEvalBar(0, -1, false);
        UserPreferenceManager.setupUserSettingsScreen(themeSelection, bgColorSelector, pieceSelector, null, null, audioMuteEffButton, audioSliderEff, evalOptions,nMovesOptions,computerOptions, Window.Start);
        ChessCentralControl.chessActionHandler.reset();
        ChessCentralControl.puzzleGuiManager.init();

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

        setUpResetToHome(gameoverHomebutton);
        setUpResetToHome(homeButton);


        pgnSaveButton.setOnMouseClicked(e -> {
            String fileName = ChessCentralControl.gameHandler.gameWrapper.getGame().getGameName() + ".txt";
            String pgn = ChessCentralControl.gameHandler.gameWrapper.getGame().gameToPgn();
            logger.debug("Saving game");
            GeneralChessFunctions.saveToFile(fileName, pgn);
        });

        settingsButton.setOnMouseClicked(e -> {
            toggleSettingsAndGameControls();
        });

        LeftReset.setOnMouseClicked(e -> {
            logger.debug("Left Reset clicked");
            int minIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex();
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > minIndex) {
                changeToAbsoluteMoveIndex(minIndex);

            }
        });

        LeftButton.setOnMouseClicked(e -> {
            logger.debug("Left button clicked");
            int minIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex();
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > minIndex) {
                changeMove(-1, false,false);

            }


        });

        RightButton.setOnMouseClicked(e -> {
            logger.debug("Right button clicked");
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() < ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                changeMove(1, false,false);


            }
        });

        RightReset.setOnMouseClicked(e -> {
            logger.debug("right Reset clicked");
            if (currentState != MainScreenState.PUZZLE && ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() < ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                changeToAbsoluteMoveIndex(ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());

            }
        });

        reset.setOnMouseClicked(e -> {
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() != ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex()) {
                changeMove(0, true,false);
            }
        });
    }

    private void setUpResetToHome(Button gameoverHomebutton) {
        gameoverHomebutton.setOnMouseClicked(e -> {
            HomeReset();
        });
    }
    /** Clears current game, performs cleanup and then goes back to start screen**/
    public void HomeReset(){
        // clearing all board related stuff

        ChessCentralControl.chessBoardGUIHandler.removeAllPieces();
        ChessCentralControl.chessBoardGUIHandler.resetEverything(true);
        ChessCentralControl.chessActionHandler.reset();


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

    public void setUpBindings() {
        App.bindingController.bindSmallText(currentGamePgnLabel,Window.Main,"Black");
        App.bindingController.bindSmallText(currentGamePgn,Window.Main,"Black");
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
        App.bindingController.bindSmallText(simulationScore, Window.Main, "Black");
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

        App.bindingController.bindSmallText(player1SimLabel,Window.Main,"Black");
        App.bindingController.bindSmallText(player2SimLabel,Window.Main,"Black");

        sandboxPieces.prefHeightProperty().bind(switchingOptions.heightProperty());

        App.bindingController.bindSmallText(lineLabel,Window.Main,"Black");

//        evalOverTimeBox.prefHeightProperty().bind(localInfo.heightProperty());
//        evalOverTimeBox.prefWidthProperty().bind(localInfo.widthProperty());


        // moves played box
        movesPlayed.setFitToHeight(true);
        movesPlayed.prefWidthProperty().bind(sidePanel.widthProperty());

        // all the different side panels
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sidePanel, campaignInfo, 100, .8);
        App.bindingController.bindSmallText(campaignInfo, Window.Main, "Black");

        campaignInfo.prefHeightProperty().bind(switchingOptions.heightProperty());
        App.bindingController.bindSmallText(inGameInfo, Window.Main, "black");
        inGameInfo.prefWidthProperty().bind(campaignInfo.widthProperty());
        inGameInfo.prefHeightProperty().bind(campaignInfo.heightProperty().subtract(sendMessageButton.heightProperty()));
        sendMessageButton.prefWidthProperty().bind(inGameInfo.widthProperty().divide(8));
        chatInput.prefWidthProperty().bind(inGameInfo.widthProperty().subtract(sendMessageButton.widthProperty()).subtract(5));
        chatInput.prefHeightProperty().bind(sendMessageButton.heightProperty());


        // side panel labels

        App.bindingController.bindSmallText(stateLabel, Window.Main);
        App.bindingController.bindLargeText(victoryLabel, Window.Main, "White");
        App.bindingController.bindSmallText(player1Label, Window.Main);
        App.bindingController.bindSmallText(player2Label, Window.Main);
        App.bindingController.bindSmallText(WhiteNumericalAdv, Window.Main);
        App.bindingController.bindSmallText(BlackNumericalAdv, Window.Main);
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
//        player1MoveClock.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                updateTextVisibility(player1MoveClock, true);
//            }
//        });
//
//        player2MoveClock.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                updateTextVisibility(player2MoveClock, false);
//            }
//        });

//        sidePanel.prefWidthProperty().bind(fullScreen.widthProperty().subtract(chessPieceBoard.widthProperty()).subtract(whiteadvantage.widthProperty()));
//        initLabelBindings(bgLabel);
//        initLabelBindings(pieceLabel);
//        initLabelBindings(evalLabel);

        // setting screen
        settingsScroller.prefWidthProperty().bind(sidePanel.widthProperty());
        settingsScroller.prefHeightProperty().bind(sidePanel.heightProperty());

        settingsScreen.prefWidthProperty().bind(sidePanel.widthProperty());

        // game over menu
        App.bindingController.bindXLargeText(gameoverTitle, Window.Main, "White");
        App.bindingController.bindXLargeText(victoryLabel, Window.Main, "White");

        // miscelaneus buttons
        App.bindingController.bindMediumText(settingsButton, Window.Main,"Black");
        App.bindingController.bindMediumText(homeButton, Window.Main,"Black");
        App.bindingController.bindMediumText(LeftButton, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, LeftButton, 110, .32);
        App.bindingController.bindMediumText(RightButton, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, RightButton, 110, .32);
        App.bindingController.bindMediumText(LeftReset, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, LeftReset, 140, .36);
        App.bindingController.bindMediumText(RightReset, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, RightReset, 140, .36);
        App.bindingController.bindSmallText(saveIndicator, Window.Main);
        App.bindingController.bindMediumText(reset, Window.Main);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(sideAreaFull, reset, 240, .45);
//
        App.bindingController.bindSmallText(evalLabel,Window.Main);
        App.bindingController.bindSmallText(nMovesLabel,Window.Main);
        App.bindingController.bindSmallText(computerLabel,Window.Main);


        App.bindingController.bindSmallText(puzzleTagsLabel,Window.Main);
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
        ChessCentralControl.chessActionHandler.reset();

        // side panel controls are slightly different for every mode
        setMainControls(currentState, extraStuff);
        // some modes do not want an eval bar
        checkHideEvalBar(currentState);
        checkHideMovesPlayed(currentState);
        checkHideResetButton(currentState);
        if (currentState == MainScreenState.LOCAL) {
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


        setMoveLabels(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());
        ChessCentralControl.chessActionHandler.highlightMovesPlayedLine(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex());
        if(currentState == MainScreenState.ONLINE){
            setTimeLabels(ChessCentralControl.gameHandler.gameWrapper.getWebGameType());
        }
    }

    public void setupCampaign(String player1Name, int player1Elo, String player1PfpUrl, CampaignTier levelTier, int levelOfTier, int campaignDifficuly) {
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

    public void setupWithoutGame(boolean isVsComputer, boolean isPlayer1White, String gameName, String player1Name, int player1Elo, String player1PfpUrl, MainScreenState currentState,boolean playAsWhite) {
        this.currentState = currentState;

        // ternary shit show
        String whitePlayerName = playAsWhite ? player1Name : isVsComputer ? "Computer" : "Player 2";
        String blackPlayerName = !playAsWhite ? player1Name : isVsComputer ? "Computer" : "Player 2";

        int whiteElo = playAsWhite ? player1Elo : isVsComputer ? App.userPreferenceManager.getPrefDifficulty().eloRange : player1Elo;
        int blackElo = !playAsWhite ? player1Elo : isVsComputer ? App.userPreferenceManager.getPrefDifficulty().eloRange : player1Elo;

        String whitePfpUrl = playAsWhite ? player1PfpUrl : isVsComputer ? ProfilePicture.ROBOT.urlString : player1PfpUrl;
        String blackPfpUrl = !playAsWhite ? player1PfpUrl : isVsComputer ? ProfilePicture.ROBOT.urlString : player1PfpUrl;


        if (gameName.isEmpty()) {
            ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGame(whitePlayerName,blackPlayerName, whiteElo,blackElo , whitePfpUrl, blackPfpUrl, isVsComputer, isPlayer1White));
        } else {
            ChessCentralControl.gameHandler.switchToNewGame(ChessGame.createSimpleGameWithName(gameName, whitePlayerName, blackPlayerName, whiteElo, blackElo, whitePfpUrl, blackPfpUrl, isVsComputer, isPlayer1White));
        }
        String extraInfo = "";
        if (currentState == MainScreenState.LOCAL) {
            extraInfo = isVsComputer ? "vs Computer" : "PvP";
        }
        setUp(extraInfo);
    }

    public void setupWithGame(ChessGame gameToSetup, MainScreenState currentState, boolean isFirstLoad) {
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
        this.currentState = MainScreenState.ONLINE;
        // put loading icon
        ChessCentralControl.gameHandler.switchToOnlineGame(onlinePreinit, Gametype.getType(gameType), true);
        setUp(onlinePreinit.getGameName());

        App.messager.addLoadingCircle(Window.Main);
        App.createOnlineGameRequest(gameType,onlinePreinit);
    }

    private void checkHideResetButton(MainScreenState currentState) {
        if(!MainScreenState.cannotReset(currentState)){
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
    }



    public void setPlayerIcons(String player1Url, String player2Url, boolean isPlayer1White) {
        ImageView player1 = isPlayer1White ? player1Select : player2Select;
        ImageView player2 = isPlayer1White ? player2Select : player1Select;
        player1.setImage(new Image(player1Url));
        player2.setImage(new Image(player2Url));
    }

    public void setPlayerLabels(String whitePlayerName, int whiteElo, String blackPlayerName, int blackElo,boolean isPlayer1White) {
        Label p1Label = isPlayer1White ? player1Label : player2Label;
        Label p2Label = isPlayer1White ? player2Label : player1Label;
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
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Resetting to abs index:" + absIndex);

        int curMoveIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
        ChessCentralControl.gameHandler.gameWrapper.moveToMoveIndexAbsolute(absIndex, true);

        setMoveLabels(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());

        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);
    }

    // change the board to a previosly saved position
    private void changeMove(int direction, boolean isReset,boolean forceChange) {
        // puzzle check
        if(!forceChange && currentState == MainScreenState.PUZZLE){
            int curIndex = ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
            int maxSoFar = ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxSoFar();
            System.out.println(maxSoFar);
            if(direction > 0 && curIndex >= maxSoFar){
                // making move will take you over the maxsofar
                return;
            }
        }
        ChessCentralControl.chessBoardGUIHandler.clearAllHighlights();
        ChessCentralControl.chessBoardGUIHandler.clearArrows();
        logger.debug("Changing move by " + direction);
        if (isReset) {
            // resets the backend chessgame
            ChessCentralControl.gameHandler.gameWrapper.reset();
            ChessCentralControl.clearForNewGame();


        } else {
            ChessCentralControl.gameHandler.gameWrapper.changeToDifferentMove(direction, App.userPreferenceManager.isNoAnimate());


        }
        setMoveLabels(ChessCentralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex(), ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex());

        updateSimpleAdvantageLabels();
        ChessCentralControl.chessActionHandler.makeBackendUpdate(currentState, false, false);

    }

    public void moveToNextPuzzleMove(){
        // this also increases visisted index because its a forced change
        ChessCentralControl.gameHandler.gameWrapper.getGame().incrementMaxSoFar();

        changeMove(1,false,true);
    }

    private void clearSimpleAdvantageLabels() {
        WhiteNumericalAdv.setText("");
        BlackNumericalAdv.setText("");
    }

    public void updateSimpleAdvantageLabels() {
        boolean isPlayer1White = ChessCentralControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
        Label whiteLabel = isPlayer1White ? WhiteNumericalAdv : BlackNumericalAdv;
        Label blackLabel = !isPlayer1White ? WhiteNumericalAdv : BlackNumericalAdv;
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

            showGameOver(title );
        }
        if (depth > 0) {
            evalDepth.setText(Integer.toString(depth));
        }
    }



    public void setMoveLabels(int curIndex, int maxIndex) {
        saveIndicator.setText((curIndex + 1) + "/" + (maxIndex + 1));
    }
    public void setTimeLabels(Gametype gametype) {
        player1MoveClock.setText(ChessConstants.formatSeconds((int) gametype.getTimeUnit().toSeconds(gametype.getLength())));
        player2MoveClock.setText(ChessConstants.formatSeconds((int) gametype.getTimeUnit().toSeconds(gametype.getLength())));
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



    // checks if square player wants to move is in the moves allowed for that peice


}
