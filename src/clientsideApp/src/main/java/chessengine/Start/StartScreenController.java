package chessengine.Start;

import chessengine.App;
import chessserver.ChessRepresentations.GameInfo;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Graphics.AppWindow;
import chessengine.Graphics.BindingController;
import chessserver.ChessRepresentations.ChessGame;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.StartScreenState;
import chessserver.User.UserWGames;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.UUID;

public class StartScreenController extends AppWindow {
    private final Logger logger = LogManager.getLogger("Start_Screen_Controller");


    public final ProfileScreen profileScreenM;
    public final CampaignScreen campaignScreenM;
    public final OnlineScreen onlineScreen;
    public final NavigationScreen navigationScreen;
    public final OldGamesScreen oldGamesScreen;


    public StartScreenController(){
        super();

        profileScreenM = new ProfileScreen(this, App.userManager.isLoggedIn() ? ProfileScreen.UserInfoState.LOGGEDIN : ProfileScreen.UserInfoState.SIGNEDOUT);
        campaignScreenM = new CampaignScreen(this);
        onlineScreen = new OnlineScreen(this);
        navigationScreen = new NavigationScreen(this);
        oldGamesScreen = new OldGamesScreen(this);
    }


    @Override
    public void initLayout(){
        setUpBindings();
        startRef.setMouseTransparent(true);

    }

    @Override
    public void initGraphics() {
    }

    @Override
    public void afterInitialize() {
        resetState();


        postBindings();
        setUpLocalOptions();
        setUpPgnOptions();
        setUpUserSettings();
        setUpGeneralSettings();
        setupSandboxOptions();
        setupExplorerOptions();
        setupSimulationOptions();
        setupPuzzleOptions();
    }


    private void postBindings(){
        BindingController.bindLargeText(loginTitle, "black");
        BindingController.bindMediumText(nameLabel, "black");
        BindingController.bindMediumText(passwordLabel, "black");
        BindingController.bindCustom(mainArea.heightProperty(), passwordInput.prefWidthProperty(), 350, .45);

        BindingController.bindSmallText(oldGamesLabel, "black");
    }

    private void setupPuzzleOptions() {
        enterPuzzleButton.setOnMouseClicked(e ->{
            App.changeToMainScreenPuzzle();
        });
    }


    private void setUpLocalOptions() {
        playAsWhite.setSelected(true);
        playAsWhite.setText("Play as White");
        BindingController.bindSmallTextCustom(playAsWhite, "-fx-background-color: white ;-fx-text-fill: black");
        vsPlayer.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Local Game", false, playAsWhite.isSelected(), MainScreenState.LOCAL, playAsWhite.isSelected());

        });
        vsComputer.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Local Game", true, playAsWhite.isSelected(), MainScreenState.LOCAL, playAsWhite.isSelected());
        });
        playAsWhite.setOnAction(e -> {
            // Get the node's local bounds
            playAsWhite.setSelected(playAsWhite.isSelected());
            if (playAsWhite.isSelected()) {
                playAsWhite.setText("Play as White");
                BindingController.bindSmallTextCustom(playAsWhite, "-fx-background-color: white ;-fx-text-fill: black");
            } else {
                playAsWhite.setText("Play as Black");
                BindingController.bindSmallTextCustom(playAsWhite, "-fx-background-color: black ;-fx-text-fill: white");

            }
//
        });
    }


    private void setUpUserSettings() {

    }









    private void setUpGeneralSettings() {
        // container bindings
        generalSettingsScrollpane.prefWidthProperty().bind(mainArea.widthProperty());
        generalSettingsScrollpane.prefHeightProperty().bind(mainArea.heightProperty());

        generalSettingsVbox.prefWidthProperty().bind(generalSettingsScrollpane.widthProperty());

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
                    ChessGame game = ChessGame.createSimpleGameWithNameAndPgn(pgnTextArea.getText(), "Pgn Game",
                            App.userManager.userInfoManager.getCurrentPlayerInfo(), App.userManager.userPreferenceManager.getCurrentComputerPlayer(), playAsWhite.isSelected()) ;
                    App.changeToMainScreenWithGame(game, computerRadioButton.isSelected(),  MainScreenState.LOCAL,true);

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
            App.changeToMainScreenWithGame(ChessGame.createEmptyExplorer(), false, MainScreenState.VIEWER, false);
        });
    }

    private void setupSimulationOptions() {
        enterSimulationButton.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("Simulation Game", true, true, MainScreenState.SIMULATION, true);
        });
    }

    private void setUpBindings() {
        content.prefWidthProperty().bind(fullScreen.widthProperty());
        content.prefHeightProperty().bind(fullScreen.heightProperty());

        sideButtons.prefHeightProperty().bind(content.heightProperty().subtract(profileBox.heightProperty()));
        // top left profile info

        // profile options
        mainArea.prefWidthProperty().bind(fullScreen.widthProperty().subtract(rightSidePanel.widthProperty()).subtract(sideButtons.widthProperty()));
        mainArea.prefHeightProperty().bind(fullScreen.heightProperty());

    }

    //    private final Background buttonSelectedBg = new Background(new BackgroundFill(Color.LIGHTGRAY,new CornerRadii(3),null));
//    private final Background buttonUnSelectedBg = new Background(new BackgroundFill(Color.DARKGRAY,new CornerRadii(3),null));

    public void AddNewGameToSaveGui(GameInfo newGame){
        AddNewGameToSaveGui(newGame,userOldGamesContent);
    }

    protected void AddNewGameToSaveGui(GameInfo newGame, Pane gamesContainer) {
        HBox gameContainer = new HBox();

        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.setSpacing(2);

        VBox gameInfo = new VBox();
        HBox innerGameInfo = new HBox();

        gameInfo.setAlignment(Pos.CENTER);
        gameInfo.setSpacing(1);

        innerGameInfo.setAlignment(Pos.CENTER);

        Label gameName = new Label(newGame.gameName());
        BindingController.bindSmallText(gameName, "Black");

        Label playersName = new Label(newGame.whitePlayer().playerName() + " vs " + newGame.blackPlayer().playerName());
        BindingController.bindSmallText(playersName, "Black");


        innerGameInfo.getChildren().addAll(playersName);
        gameInfo.getChildren().addAll(gameName, innerGameInfo);

        Button deleteButton = new Button();
        deleteButton.setGraphic(new FontIcon("fas-trash"));

        deleteButton.setOnMouseClicked(e -> {
            removeFromOldGames(newGame.gameUUID());
            App.triggerUpdateUser();
        });
        BindingController.bindCustom(gameContainer.widthProperty(), deleteButton.prefWidthProperty(), 30, .2);
        deleteButton.prefHeightProperty().bind(deleteButton.prefWidthProperty().multiply(1.2));
        BindingController.bindSmallText(deleteButton);


        Button openGame = new Button();
        openGame.setGraphic(new FontIcon("fas-folder-open"));

        openGame.setOnMouseClicked(e -> {
            if(App.userManager.isLoggedIn()){
                App.changeToMainScreenWithGame(newGame.toChessGame(App.userManager.getCurrentUser().userInfo(), true),
                        false, MainScreenState.VIEWER ,false);
            }
            else{
                App.changeToMainScreenWithGame(newGame.toChessGame(), false, MainScreenState.VIEWER ,false);
            }

        });
        BindingController.bindCustom(gameContainer.widthProperty(), openGame.prefWidthProperty(), 30, .2);
        openGame.prefHeightProperty().bind(openGame.prefWidthProperty().multiply(1.2));
        BindingController.bindSmallText(openGame);

        gameContainer.getChildren().add(gameInfo);
        gameContainer.getChildren().add(openGame);
        gameContainer.getChildren().add(deleteButton);

        gameContainer.prefHeightProperty().bind(deleteButton.prefHeightProperty().add(3));
        gameContainer.prefWidthProperty().bind(gamesContainer.widthProperty());
        gameContainer.setStyle("-fx-background-color: darkgrey");
        gameContainer.setUserData(newGame.gameUUID());

        gamesContainer.getChildren().add(0, gameContainer);
    }

    private void removeFromOldGames(UUID gameID) {
        PersistentSaveManager.removeGameFromData(gameID);
    }







    // pure graphics no logic


    // set main area selection



    public void resetOldGamePanels(){
        userOldGamesContent.getChildren().clear();
    }




    // Profile screen


    // online Screen

    // pgn screen
    void resetPgnScreen(){
        pgnTextArea.clear();
    }



    // default state
    @Override
    public void resetState(){
        navigationScreen.setSelection(StartScreenState.REGULAR);
        profileScreenM.resetState();
        campaignScreenM.resetState();

        resetPgnScreen();

        resetOldGamePanels();
    }



    // loginable
    @Override
    public void onLogin(){
        // friends area

    }

    @Override
    public void onLogout(){
        // friends area

    }

    public void onOnline(){
    }

    @Override
    public void onOffline(){
    }


    // user configurable
    @Override
    public void updateWithUser(UserWGames userWGames) {
    }





    /*Injectable fields and getters*/



    // root node
    @FXML
    StackPane fullScreen;

    @Override
    public ReadOnlyDoubleProperty getRootWidth() {
        return fullScreen.widthProperty();
    }
    @Override
    public ReadOnlyDoubleProperty getRootHeight() {
        return fullScreen.heightProperty();
    }
    @Override
    public Region getRoot(){
        return fullScreen;
    }
    // main content wrapper
    @FXML
    GridPane content;

    // top left profile container
    @FXML
    StackPane profileBox;
    @FXML
    HBox statusBox;
    @FXML
    Circle statusLabel;
    @FXML
    ImageView profileButton;
    // top right spacer with old games label
    @FXML
    Label oldGamesLabel;

    // mid right old games panel
    @FXML
    VBox rightSidePanel;
    @FXML
    ScrollPane oldGamesPanel;
    @FXML
    VBox oldGamesPanelContent;

    // mid left side navigation buttons
    @FXML
    VBox sideButtons;
    @FXML
    Button campaignButton;
    @FXML
    Button localButton;
    @FXML
    Button multiplayerButton;
    @FXML
    Button pgnButton;
    @FXML
    Button settingsButton;
    @FXML
    Button extraModesButton;

    // center main content, with a bunch of stackable screens
    @FXML
    StackPane mainArea;

    // "main" screen
    @FXML
    HBox mainSelectionScreen;
    @FXML
    Label mainTitle;
    @FXML
    Button vsPlayer;
    @FXML
    Button vsComputer;
    @FXML
    ToggleButton playAsWhite;


    // campaign screen
    @FXML
    VBox campaignScreen;
    @FXML
    StackPane campaignStack;
    @FXML
    ImageView campaignBackground;
    @FXML
    ImageView campaignBackground2;
    @FXML
    ScrollPane campaignScroller;
    @FXML
    StackPane levelContainer;
    @FXML
    Pane levelContainerElements;
    @FXML
    Pane levelContainerPath;

    // pgn screen
    @FXML
    VBox pgnSelectionScreen;
    @FXML
    Label pgnTitle;
    @FXML
    TextArea pgnTextArea;
    @FXML
    RadioButton pvpRadioButton;
    @FXML
    RadioButton computerRadioButton;
    @FXML
    Button pgnLoadGame;

    // multiplayer
    @FXML
    StackPane multiplayerSelectionScreen;
    @FXML
    VBox connectedScreen;
    @FXML
    Label multiplayerTitle;
    @FXML
    HBox multiplayerInfo;
    @FXML
    Label numOnline;
    @FXML
    Label numInPool;
    @FXML
    ComboBox<String> gameTypes;
    @FXML
    Button multiplayerStart;
    @FXML
    VBox onlineErrorScreen;
    @FXML
    Label onlineErrorTitle;
    @FXML
    Label onlineErrorLabel;
    @FXML
    Button onlineErrorButton;

    // profile screen w 3 pages
    @FXML
    HBox profileScreen;
    @FXML
    StackPane userSettingsStack;

    // account creation
    @FXML
    VBox accountCreationPage;
    @FXML
    Label createAccountTitle;
    @FXML
    Label createUsernameLabel;
    @FXML
    TextField createUsername;
    @FXML
    Label createEmailLabel;
    @FXML
    TextField createEmail;
    @FXML
    Label createPasswordLabel;
    @FXML
    TextField createPassword;
    @FXML
    Button createAccountButton;
    @FXML
    Hyperlink loginInsteadButton;
    // login page
    @FXML
    VBox loginPage;
    @FXML
    Label loginTitle;
    @FXML
    Label nameLabel;
    @FXML
    TextField nameInput;
    @FXML
    Label passwordLabel;
    @FXML
    TextField passwordInput;
    @FXML
    Button loginButton;

    @FXML
    Hyperlink signUpInsteadButton;
    // main profile page
    @FXML
    VBox userInfoPage;
    @FXML
    HBox topUserInfoBox;
    @FXML
    ImageView userInfoPfp;
    @FXML
    VBox topUserInfoRightBox;

    @FXML
    HBox userOldGamesLabelContainer;
    @FXML
    Label userInfoUserName;
    @FXML
    Label userInfoUUID;
    @FXML
    Label userInfoUserElo;
    @FXML
    Label userInfoRank;

    @FXML
    VBox userOldGamesContainer;
    @FXML
    Label userOldGamesLabel;
    @FXML
    ScrollPane userOldGamesScrollpane;
    @FXML
    VBox userOldGamesContent;

    @FXML
    StackPane optionalUserInfoBox;
    @FXML
    VBox loggedInFriendsBox;
    @FXML
    HBox disabledFriendsBox;
    @FXML
    Label disabledFriendsMessage;


    @FXML
    HBox friendsNavBar;
    @FXML
    Button FriendsButton;
    @FXML
    Button RequestsButton;
    @FXML
    Button SuggestedFriendsButton;
    @FXML
    Button friendsLookupButton;

    @FXML
    ScrollPane FriendsPanel;
    @FXML
    StackPane FriendsStackpane;
    @FXML
    VBox friendsContent;
    @FXML
    VBox friendsLookup;
    @FXML
    TextField friendsLookupInput;
    @FXML
    VBox friendsLookupContent;

    @FXML
    StackPane bottomInfoNav;

    @FXML
    HBox hyperlinkBox;
    @FXML
    Hyperlink LogoutButton;
    @FXML
    HBox previewHyperlinkBox;
    @FXML
    Hyperlink exitPreview;

    @FXML
    VBox userOfflineScreen;
    @FXML
    Label userOfflineErrorTitle;
    @FXML
    Label userOfflineErrorLabel;
    @FXML
    Button userOfflineErrorButton;



    // extra options
    @FXML
    HBox extraModesScreen;
    @FXML
    Button enterSandboxButton;
    @FXML
    Button enterSimulationButton;
    @FXML
    Button enterExplorerButton;
    @FXML
    Button enterPuzzleButton;

    // settings screen
    @FXML
    HBox generalSettingsScreen;
    @FXML
    ScrollPane generalSettingsScrollpane;
    @FXML
    VBox generalSettingsVbox;

    @Override
    public VBox getSettingsWrapper(){
        return generalSettingsVbox;
    }


    // start screen reference
    @FXML
    Pane startRef;

    @Override
    public Pane getMessageBoard(){
        return startRef;
    }

    @FXML
    Group startGroup;

    @Override
    public Group getMessageGroup(){
        return startGroup;
    }

}
