package chessengine.Graphics;

import chessengine.App;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.StartScreenState;
import chessengine.Managers.CampaignManager;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Managers.UserPreferenceManager;
import chessserver.Gametype;
import chessserver.INTENT;
import chessserver.ProfilePicture;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable {

    private final int maxNewGameButtonSize = 100;
    private final Image trashIcon = new Image("/StartScreenIcons/trash.png");
    private final Image computerIcon = new Image("/StartScreenIcons/robot.png");
    private final Image playerIcon = new Image("/StartScreenIcons/player.png");
    public CampaignManager campaignManager;
    @FXML
    public GridPane content;
    @FXML
    public Pane startRef;
    @FXML
    public Button backgroundAudioButton;
    @FXML
    public Label poolCount;
    @FXML
    public ChoiceBox<String> themeSelection;
    @FXML
    public ComboBox<String> bgColorSelector;
    @FXML
    public ComboBox<String> pieceSelector;
    @FXML
    public Button audioMuteBGButton;
    @FXML
    public Slider audioSliderBG;
    @FXML
    public Button audioMuteEffButton;
    // user settings
    @FXML
    public Slider audioSliderEff;
    @FXML
    public ComboBox<String> evalOptions;
    @FXML
    public ComboBox<String> nMovesOptions;
    @FXML
    public ComboBox<String> computerOptions;
    @FXML
    StackPane fullscreen;
    @FXML
    HBox profileBox;
    @FXML
    VBox rightSidePanel;
    @FXML
    HBox bottomSpacer;
    @FXML
    Button vsPlayer;
    @FXML
    Button vsComputer;
    @FXML
    ToggleButton playAsWhite;
    // main area screens
    @FXML
    VBox campaignScreen;
    @FXML
    HBox pgnSelectionScreen;
    @FXML
    HBox mainSelectionScreen;
    @FXML
    HBox multiplayerSelectionScreen;
    @FXML
    HBox userSettingScreen;
    @FXML
    HBox generalSettingsScreen;
    @FXML
    HBox extraModesScreen;
    @FXML
    Button enterSandboxButton;
    @FXML
    Button enterSimulationButton;
    @FXML
    Button enterExplorerButton;
    // campaign screen
    @FXML
    StackPane levelContainer;
    @FXML
    Pane levelContainerPath;
    // pgn screen
    @FXML
    Pane levelContainerElements;
    @FXML
    ScrollPane campaignScroller;
    @FXML
    StackPane campaignStack;
    @FXML
    ImageView campaignBackground;
    @FXML
    ImageView campaignBackground2;
    @FXML
    TextArea pgnTextArea;
    @FXML
    Button pgnLoadGame;
    @FXML
    RadioButton pvpRadioButton;
    @FXML
    RadioButton computerRadioButton;
    @FXML
    Label oldGamesLabel;
    @FXML
    ScrollPane oldGamesPanel;
    @FXML
    VBox oldGamesPanelContent;
    @FXML
    VBox mainAreaTopSpacer;
    @FXML
    VBox mainAreaReference;
    @FXML
    StackPane mainArea;
    // side panel stuff
    @FXML
    VBox sideButtons;
    @FXML
    Button campaignButton;
    @FXML
    Button localButton;
    @FXML
    Button pgnButton;
    @FXML
    Button multiplayerButton;
    @FXML
    Button settingsButton;
    @FXML
    Button extraModesButton;
    // multiplayer options
    @FXML
    ComboBox<String> gameTypes;
    @FXML
    Button multiplayerStart;
    @FXML
    Button reconnectButton;
    // general settings
    @FXML
    ScrollPane generalSettingsScrollpane;
    @FXML
    VBox generalSettingsVbox;
    @FXML
    Label themeLabel;
    @FXML
    Label bgLabel;
    @FXML
    Label pieceLabel;
    @FXML
    Label audioMuteBG;
    @FXML
    Label audioLabelBG;
    @FXML
    Label audioMuteEff;
    @FXML
    Label audioLabelEff;
    @FXML
    Label evalLabel;
    @FXML
    Label nMovesLabel;
    @FXML
    Label computerLabel;
    // login page
    @FXML
    Label loginTitle;
    @FXML
    Label nameLabel;
    @FXML
    TextField nameInput;
    @FXML
    Label eloLabel;
    @FXML
    TextField passwordInput;
    // profile
    @FXML
    ImageView profileButton;
    @FXML
    Label nameProfileLabel;
    @FXML
    Label eloProfileLabel;
    @FXML
    Button saveUserOptions;

    @FXML
    HBox settingSpacer;

    @FXML
    HBox s1;

    @FXML
    HBox s2;

    @FXML
    HBox s3;

    @FXML
    HBox s4;

    @FXML
    VBox s5;

    @FXML
    HBox s6;

    @FXML
    VBox s7;

    @FXML
    HBox s8;

    @FXML
    HBox s9;

    @FXML
    HBox s10;
    List<ChessGame> oldGames;
    boolean isRed = false;
    private StartScreenState currentState;
    private StartScreenState lastStateBeforeUserSettings;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startRef.setMouseTransparent(true);
        oldGamesPanelContent.setStyle("-fx-background-color: lightgray");


    }

    public void setup() {
        setSelection(StartScreenState.REGULAR);
        setUpLocalOptions();
        setUpPgnOptions();
        setUpMultiOptions();
        setUpUserSettings();
        setUpGeneralSettings();
        setUpSideNavButtons();
        setupSandboxOptions();
        setupExplorerOptions();
        setupSimulationOptions();

        setUpMiscelaneus();
        setUpBindings();

        setUpCampaignScreen();
        campaignManager = new CampaignManager(levelContainer, levelContainerElements, levelContainerPath, campaignScroller, mainArea, campaignBackground, campaignBackground2);
        campaignManager.setLevelUnlocksBasedOnProgress(App.userManager.getCampaignProgress());
        oldGames = loadGamesFromSave();
        setupOldGamesBox(oldGames);
    }

    public void setProfileInfo(ProfilePicture picture, String name, int elo) {
        profileButton.setImage(new Image(picture.urlString));
        nameProfileLabel.setText(name);
        eloProfileLabel.setText(Integer.toString(elo));
    }

    private void setUpCampaignScreen() {
        campaignScreen.prefWidthProperty().bind(mainArea.widthProperty());
        campaignScreen.prefHeightProperty().bind(mainArea.heightProperty());
        campaignScroller.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        campaignScroller.prefHeightProperty().bind(campaignScreen.prefHeightProperty());
        campaignScroller.setStyle("-fx-background-color: rgba(170,170,170,.20)");
        campaignStack.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        campaignStack.prefHeightProperty().bind(campaignScreen.prefHeightProperty());
        campaignBackground.fitWidthProperty().bind(campaignScreen.prefWidthProperty());
        campaignBackground.fitHeightProperty().bind(campaignScreen.prefHeightProperty());
        campaignBackground2.fitWidthProperty().bind(campaignScreen.prefWidthProperty());
        campaignBackground2.fitHeightProperty().bind(campaignScreen.prefHeightProperty());
        campaignBackground.setPreserveRatio(false);
        campaignBackground2.setPreserveRatio(false);
        levelContainer.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        levelContainerElements.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        levelContainerPath.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        levelContainerPath.setMouseTransparent(true);
        levelContainerElements.toFront();
    }

    private void setUpLocalOptions() {
        playAsWhite.setSelected(true);
        playAsWhite.setText("Play as White");
        App.bindingController.bindSmallTextCustom(playAsWhite, false, "-fx-background-color: white ;-fx-text-fill: black");
        vsPlayer.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Local Game", false, playAsWhite.isSelected(), MainScreenState.LOCAL, playAsWhite.isSelected());

        });
        vsComputer.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Local Game", true, playAsWhite.isSelected(), MainScreenState.LOCAL, playAsWhite.isSelected());
        });
        playAsWhite.setOnAction(e -> {
            // Get the node's local bounds

//            App.messager.addFocusNode(playAsWhite,true);
//            App.messager.addMovingArrow(playAsWhite,.03,.03,5,true);
//            App.messager.addInformationBox(.5,.5,.3,.3,"Welcome","Hello, i will guide you through a small tutorial",true);
            playAsWhite.setSelected(playAsWhite.isSelected());
            if (playAsWhite.isSelected()) {
                playAsWhite.setText("Play as White");
                App.bindingController.bindSmallTextCustom(playAsWhite, false, "-fx-background-color: white ;-fx-text-fill: black");
            } else {
                playAsWhite.setText("Play as Black");
                App.bindingController.bindSmallTextCustom(playAsWhite, false, "-fx-background-color: black ;-fx-text-fill: white");

            }
        });
    }

    private void setUpSideNavButtons() {
        campaignButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.CAMPAIGN);
            campaignManager.scrollToPlayerTier(App.userManager.getCampaignProgress());
        });
        App.bindingController.bindSmallText(campaignButton, false);

        localButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.REGULAR);
        });
        App.bindingController.bindSmallText(localButton, false);

        pgnButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.PGN);
        });
        App.bindingController.bindSmallText(pgnButton, false);

        multiplayerButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.MULTIPLAYER);
        });
        App.bindingController.bindSmallText(multiplayerButton, false);

        extraModesButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.EXTRA);
        });
        App.bindingController.bindSmallText(extraModesButton, false);

        settingsButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.GENERALSETTINGS);
        });
        App.bindingController.bindSmallText(settingsButton, false);


        backgroundAudioButton.setOnMouseClicked(e -> {
            boolean isCurPaused = !App.userPreferenceManager.isBackgroundMusic();
            if (isCurPaused) {
                // unpause so change to playing icon
                backgroundAudioButton.setText("🔉");
            } else {
                // pause so make not playing icon
                backgroundAudioButton.setText("✖");

            }
            // muted = opposite of is bg music
            App.userPreferenceManager.setBackgroundmusic(isCurPaused);
        });
        App.bindingController.bindSmallText(backgroundAudioButton, false);

    }

    private void setUpUserSettings() {
        profileButton.setOnMouseClicked(e -> {
            if (currentState.equals(StartScreenState.USERSETTINGS)) {
                setSelection(lastStateBeforeUserSettings);
            } else {
                setSelection(StartScreenState.USERSETTINGS);
            }
        });
        saveUserOptions.setOnMouseClicked(e -> {

            if (nameInput.getText().isEmpty()) {
                nameInput.setPromptText("please enter a name");
            }
            if (passwordInput.getText().isEmpty()) {
                passwordInput.setPromptText("please enter a password");
            } else {
                App.validateClientRequest(nameInput.getText(), passwordInput.getText());


            }

        });

    }

    private void setUpGeneralSettings() {
        UserPreferenceManager.setupUserSettingsScreen(themeSelection, bgColorSelector, pieceSelector, audioMuteBGButton, audioSliderBG, audioMuteEffButton, audioSliderEff, evalOptions, nMovesOptions, computerOptions, false);
        App.bindingController.bindSmallText(themeLabel, false);
        App.bindingController.bindSmallText(bgLabel, false);
        App.bindingController.bindSmallText(pieceLabel, false);
        App.bindingController.bindSmallText(audioLabelBG, false);
        App.bindingController.bindSmallText(audioMuteBG, false);
        App.bindingController.bindSmallText(audioLabelEff, false);
        App.bindingController.bindSmallText(audioMuteEff, false);
        App.bindingController.bindSmallText(evalLabel, false);
        App.bindingController.bindSmallText(nMovesLabel, false);
        App.bindingController.bindSmallText(computerLabel, false);


        // container bindings
        generalSettingsScrollpane.prefWidthProperty().bind(mainArea.widthProperty());
        generalSettingsScrollpane.prefHeightProperty().bind(mainArea.heightProperty());

        generalSettingsVbox.prefWidthProperty().bind(generalSettingsScrollpane.widthProperty());

        settingSpacer.prefHeightProperty().bind(fullscreen.heightProperty().multiply(.02));

        s1.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s2.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s3.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s4.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s5.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s6.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s7.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s8.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s9.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));
        s10.prefWidthProperty().bind(generalSettingsVbox.widthProperty().divide(2));

        // binding selectors and buttons

        bgColorSelector.prefWidthProperty().bind(themeSelection.widthProperty());
        bgColorSelector.prefHeightProperty().bind(themeSelection.heightProperty());


        // binding labels
//        BindingController.bindChildTextToParentWidth(themeSelection,themeLabel,.1);


    }

    private void setUpMultiOptions() {

        gameTypes.getItems().addAll(Arrays.stream(Gametype.values()).map(Gametype::getStrVersion).toList());
        gameTypes.setOnAction(e -> {
            App.sendRequest(INTENT.GETNUMBEROFPOOLERS, gameTypes.getValue());
        });
        multiplayerStart.setOnMouseClicked(e -> {
            if (!gameTypes.getSelectionModel().isEmpty()) {
                // todo this is not correct!
                App.changeToMainScreenOnline(ChessGame.getOnlinePreInit(gameTypes.getValue(), true)); // isWhiteOriented will change when match is found
            }

        });
        // handle no internet connection
        if (App.isWebClientNull()) {
            disableMultioptions();
        } else {
            enableMultioptions(false);
        }
        reconnectButton.setOnMouseClicked(e -> {
            boolean isSucess = App.attemptReconnection();
            if (isSucess) {
                App.messager.sendMessageQuick("Connected to server", true);
                enableMultioptions(true);
            } else {
                App.messager.sendMessageQuick("Connection Failed", true);
                disableMultioptions();
            }

        });


    }

    public void disableMultioptions() {
        gameTypes.setDisable(true);
        multiplayerStart.setDisable(true);
        poolCount.setText("No Server Connection!");
        reconnectButton.setVisible(true);
        reconnectButton.setMouseTransparent(false);
    }

    public void enableMultioptions(boolean showMessage) {
        gameTypes.setDisable(false);
        multiplayerStart.setDisable(false);
        if (showMessage) {
            poolCount.setText("Connected To Internet");
        } else {
            poolCount.setText("");
        }
        reconnectButton.setVisible(false);
        reconnectButton.setMouseTransparent(true);
    }

    private void setUpPgnOptions() {
        // default options
        computerRadioButton.setSelected(false);
        pvpRadioButton.setSelected(true);
        // styling
        pgnTextArea.setStyle("-fx-text-fill: Black");


        pvpRadioButton.setOnMouseClicked(e -> {
            computerRadioButton.setSelected(!pvpRadioButton.isSelected());
        });
        computerRadioButton.setOnMouseClicked(e -> {
            pvpRadioButton.setSelected(!computerRadioButton.isSelected());
        });
        pgnLoadGame.setOnMouseClicked(e -> {
            if (pgnTextArea.getText().isEmpty()) {
                // todo make blinking red effect
                // todo validate text
                //pgnTextArea.setEffect...
                pgnTextArea.setPromptText("Please enter a pgn!");

            } else {
                try {
                    ChessGame game = ChessGame.gameFromPgnLimitedInfo(pgnTextArea.getText(), "Pgn Game", App.userManager.getUserName(), App.userManager.getUserElo(), App.userManager.getUserPfpUrl(), computerRadioButton.isSelected(), playAsWhite.isSelected());
                    App.changeToMainScreenWithGame(game, MainScreenState.LOCAL, true);

                } catch (Exception ex) {
                    pgnTextArea.clear();
                    pgnTextArea.setPromptText("Invalid pgn entered");
                }

            }
        });

    }

    private void setupSandboxOptions() {
        enterSandboxButton.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Sandbox Game", false, true, MainScreenState.SANDBOX, true);
        });
    }

    private void setupExplorerOptions() {
        enterExplorerButton.setOnMouseClicked(e -> {
            App.changeToMainScreenWithGame(ChessGame.createEmptyExplorer(), MainScreenState.VIEWER, false);
        });
    }

    private void setupSimulationOptions() {
        enterSimulationButton.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Simulation Game", true, true, MainScreenState.SIMULATION, true);
        });
    }

    private void setUpBindings() {



        // top left profile info
        App.bindingController.bindSmallText(nameProfileLabel, false);
        App.bindingController.bindSmallText(eloProfileLabel, false);
        mainAreaTopSpacer.prefHeightProperty().bind(content.heightProperty().multiply(0.01));
        content.prefWidthProperty().bind(fullscreen.widthProperty());
        content.prefHeightProperty().bind(fullscreen.heightProperty());
        sideButtons.prefHeightProperty().bind(content.heightProperty().subtract(profileBox.heightProperty()).subtract(bottomSpacer.heightProperty()));
        mainAreaReference.prefWidthProperty().bind(content.widthProperty().subtract(sideButtons.widthProperty()).subtract(rightSidePanel.widthProperty()));
        mainAreaReference.prefHeightProperty().bind(content.heightProperty().subtract(mainAreaTopSpacer.heightProperty()).subtract(bottomSpacer.heightProperty()));
        mainArea.prefWidthProperty().bind(mainAreaReference.widthProperty());
        mainArea.prefHeightProperty().bind(mainAreaReference.heightProperty());

        // profile options
        App.bindingController.bindLargeText(loginTitle, false, "black");
        App.bindingController.bindMediumText(nameLabel, false, "black");
        App.bindingController.bindMediumText(eloLabel, false, "black");

        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, passwordInput, 350, .45);
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, nameInput, 350, .45);

        oldGamesPanel.prefHeightProperty().bind(mainArea.heightProperty());
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(mainArea, oldGamesPanel, 350, .5);
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(mainArea, themeSelection, 50, .1);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(mainArea, themeSelection, 100, .1);
        oldGamesPanelContent.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        App.bindingController.bindSmallText(oldGamesLabel, false, "black");
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, reconnectButton, 120, .30);
        reconnectButton.prefHeightProperty().bind(reconnectButton.prefWidthProperty().multiply(0.75));
    }

    private void setUpMiscelaneus() {
        oldGamesPanelContent.setAlignment(Pos.TOP_CENTER);
        oldGamesPanelContent.setSpacing(3);


    }

    //    private final Background buttonSelectedBg = new Background(new BackgroundFill(Color.LIGHTGRAY,new CornerRadii(3),null));
//    private final Background buttonUnSelectedBg = new Background(new BackgroundFill(Color.DARKGRAY,new CornerRadii(3),null));
    private void hideAllScreensnButtons() {
        campaignScreen.setVisible(false);
        campaignScreen.setMouseTransparent(true);
//        campaignButton.setBackground(buttonUnSelectedBg);
//        sandboxButton.setBackground(buttonUnSelectedBg);
        pgnSelectionScreen.setVisible(false);
        pgnSelectionScreen.setMouseTransparent(true);
//        pgnButton.setBackground(buttonUnSelectedBg);
        mainSelectionScreen.setVisible(false);
        mainSelectionScreen.setMouseTransparent(true);
//        localButton.setBackground(buttonUnSelectedBg);
        multiplayerSelectionScreen.setVisible(false);
        multiplayerSelectionScreen.setMouseTransparent(true);
//        multiplayerButton.setBackground(buttonUnSelectedBg);
        userSettingScreen.setVisible(false);
        userSettingScreen.setMouseTransparent(true);
//        settingsButton.setBackground(buttonUnSelectedBg);
        generalSettingsScreen.setVisible(false);
        generalSettingsScreen.setMouseTransparent(true);

        extraModesScreen.setVisible(false);
        extraModesScreen.setMouseTransparent(true);


//        profileButton.setStyle("");


    }

    private void setSelection(StartScreenState state) {
        hideAllScreensnButtons();
        switch (state) {
            case PGN -> {
                pgnSelectionScreen.setVisible(true);
                pgnSelectionScreen.setMouseTransparent(false);
//                pgnButton.setBackground(buttonSelectedBg);
            }
            case REGULAR -> {
                mainSelectionScreen.setVisible(true);
                mainSelectionScreen.setMouseTransparent(false);
//                localButton.setBackground(buttonSelectedBg);
            }
            case MULTIPLAYER -> {
                multiplayerSelectionScreen.setVisible(true);
                multiplayerSelectionScreen.setMouseTransparent(false);
//                multiplayerButton.setBackground(buttonSelectedBg);
            }
            case USERSETTINGS -> {
                lastStateBeforeUserSettings = currentState;
                userSettingScreen.setVisible(true);
                userSettingScreen.setMouseTransparent(false);
//                profileButton.setStyle("-fx-border-style: 1px black");
            }
            case GENERALSETTINGS -> {
                generalSettingsScreen.setVisible(true);
                generalSettingsScreen.setMouseTransparent(false);
//                settingsButton.setBackground(buttonSelectedBg);
            }
            case EXTRA -> {
                extraModesScreen.setVisible(true);
                extraModesScreen.setMouseTransparent(false);
//                sandboxButton.setBackground(buttonSelectedBg);
            }
            case CAMPAIGN -> {
                campaignScreen.setVisible(true);
                campaignScreen.setMouseTransparent(false);
//                campaignButton.setBackground(buttonSelectedBg);
            }
        }
        this.currentState = state;


    }

    public void AddNewGameToSaveGui(ChessGame newGame) {
        HBox gameContainer = new HBox();

        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.setSpacing(2);

        VBox gameInfo = new VBox();
        HBox innerGameInfo = new HBox();

        gameInfo.setAlignment(Pos.CENTER);
        gameInfo.setSpacing(1);

        innerGameInfo.setAlignment(Pos.CENTER);

        Label gameName = new Label(newGame.getGameName());
        App.bindingController.bindSmallText(gameName, false, "Black");

        Label playersName = new Label(newGame.getWhitePlayerName() + " vs " + newGame.getBlackPlayerName());
        App.bindingController.bindSmallText(playersName, false, "Black");


        innerGameInfo.getChildren().addAll(playersName);
        gameInfo.getChildren().addAll(gameName, innerGameInfo);

        Button deleteButton = new Button();
        deleteButton.setOnMouseClicked(e -> {
            removeFromOldGames(String.valueOf(newGame.getGameHash()));
        });
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(gameContainer, deleteButton, 30, .3);
        deleteButton.prefHeightProperty().bind(deleteButton.widthProperty());
//        deleteButton.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView trashIconView = new ImageView(trashIcon);
        trashIconView.fitHeightProperty().bind(gameContainer.widthProperty().divide(10));
        trashIconView.fitWidthProperty().bind(gameContainer.widthProperty().divide(10));
        deleteButton.setGraphic(trashIconView);


        Button openGame = new Button();
        openGame.setOnMouseClicked(e -> {
            App.changeToMainScreenWithGame(newGame.cloneGame(), MainScreenState.VIEWER, false);

        });
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(gameContainer, openGame, 30, .3);
        openGame.prefHeightProperty().bind(openGame.widthProperty());
//        openGame.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView playerIconView = new ImageView(playerIcon);
        playerIconView.fitHeightProperty().bind(gameContainer.widthProperty().divide(10));
        playerIconView.fitWidthProperty().bind(gameContainer.widthProperty().divide(10));
        openGame.setGraphic(playerIconView);

        gameContainer.getChildren().add(gameInfo);
        gameContainer.getChildren().add(openGame);
        gameContainer.getChildren().add(deleteButton);

        App.bindingController.bindChildHeightToParentHeightWithMaxSize(oldGamesPanel, gameContainer, 75, .15);
        gameContainer.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        gameContainer.setStyle("-fx-background-color: darkgrey");
        gameContainer.setUserData(String.valueOf(newGame.getGameHash()));

        oldGamesPanelContent.getChildren().add(0, gameContainer);
    }

    private void removeFromOldGames(String hashCode) {
        PersistentSaveManager.removeGameFromData(hashCode);
        oldGamesPanelContent.getChildren().removeIf(e -> e.getUserData().equals(hashCode));
    }

    private List<ChessGame> loadGamesFromSave() {

        return PersistentSaveManager.readGamesFromAppData();
    }

    private void setupOldGamesBox(List<ChessGame> gamesToLoad) {
        oldGamesPanelContent.getChildren().clear();
        for (ChessGame g : gamesToLoad) {
            AddNewGameToSaveGui(g);
        }
    }

}
