package chessengine.Start;

import chessengine.App;
import chessengine.Enums.Window;
import chessengine.Graphics.AppWindow;
import chessengine.Graphics.CommonIcons;
import chessserver.ChessRepresentations.ChessGame;
import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.KeyManager;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.StartScreenState;
import chessengine.Enums.UserInfoState;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Managers.CampaignManager;
import chessengine.Managers.UserPreferenceManager;
import chessengine.Misc.ClientsideFriendDataResponse;
import chessserver.Enums.Gametype;
import chessserver.Enums.INTENT;
import chessserver.Enums.ProfilePicture;
import chessserver.Friends.FriendDataResponse;
import chessserver.Communication.DatabaseEntry;
import chessserver.User.UserPreferences;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;
import org.nd4j.shade.jackson.core.JsonProcessingException;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable, AppWindow {
    // root node
    @FXML
    StackPane fullScreen;

    public ReadOnlyDoubleProperty getRootWidth() {
        return fullScreen.widthProperty();
    }
    public ReadOnlyDoubleProperty getRootHeight() {
        return fullScreen.heightProperty();
    }
    public Region getRoot(){
        return fullScreen;
    }
    // main content wrapper
    @FXML
    GridPane content;

    // top left profile container
    @FXML
    HBox profileBox;
    @FXML
    ImageView profileButton;
    @FXML
    Label nameProfileLabel;
    @FXML
    Label eloProfileLabel;

    // top right spacer with old games label
    @FXML
    HBox topRightSpacer;
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
    Button backgroundAudioButton;
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
    VBox multiplayerInfo;
    @FXML
    Label poolCount;
    @FXML
    ComboBox<String> gameTypes;
    @FXML
    Button multiplayerStart;
    @FXML
    VBox disconnectedScreen;
    @FXML
    Button reconnectButton;

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
    Label createPasswordLabel;
    @FXML
    TextField createPassword;
    @FXML
    Button createAccountButton;
    @FXML
    Hyperlink loginInsteadButton;
    @FXML
    Hyperlink backToUserInfo1;

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
    @FXML
    Hyperlink backToUserInfo;

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
    Hyperlink SignUpPage;
    @FXML
    Hyperlink LoginPage;

    @FXML
    HBox backButtonBox;
    @FXML
    Hyperlink userInfoBackButton;

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

    public VBox getSettingsWrapper(){
        return generalSettingsVbox;
    }

    @FXML
    HBox s1;
    @FXML
    Label themeLabel;
    @FXML
    ChoiceBox<String> themeSelection;

    @FXML
    HBox s2;
    @FXML
    Label bgLabel;
    @FXML
    ComboBox<String> bgColorSelector;

    @FXML
    HBox s3;
    @FXML
    Label pieceLabel;
    @FXML
    ComboBox<String> pieceSelector;

    @FXML
    HBox s4;
    @FXML
    Label audioMuteBG;
    @FXML
    Button audioMuteBGButton;

    @FXML
    VBox s5;
    @FXML
    Label audioLabelBG;
    @FXML
    Slider audioSliderBG;

    @FXML
    HBox s6;
    @FXML
    Label audioMuteEff;
    @FXML
    Button audioMuteEffButton;

    @FXML
    VBox s7;
    @FXML
    Label audioLabelEff;
    @FXML
    Slider audioSliderEff;

    @FXML
    HBox s8;
    @FXML
    Label evalLabel;
    @FXML
    ComboBox<String> evalOptions;

    @FXML
    HBox s9;
    @FXML
    Label nMovesLabel;
    @FXML
    ComboBox<String> nMovesOptions;

    @FXML
    HBox s10;
    @FXML
    Label computerLabel;
    @FXML
    ComboBox<String> computerOptions;

    // start screen reference
    @FXML
    Pane startRef;

    public Pane getMessageBoard(){
        return startRef;
    }







    List<ChessGame> oldGames;
    private StartScreenState currentState;
    private StartScreenState lastStateBeforeUserSettings;
    private HashMap<String,ClientsideFriendDataResponse> lookupCache;
    public UserInfoManager userInfoManager;
    private final Logger logger = LogManager.getLogger("Start_Screen_Controller");
    public CampaignManager campaignManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startRef.setMouseTransparent(true);
        oldGamesPanelContent.setStyle("-fx-background-color: lightgray");
        lookupCache = new HashMap<>();

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
        setupPuzzleOptions();

        setUpMiscelaneus();
        setUpBindings();

        setUpCampaignScreen();
        campaignManager = new CampaignManager(levelContainer, levelContainerElements, levelContainerPath, campaignScroller, mainArea, campaignBackground, campaignBackground2);
        campaignManager.setLevelUnlocksBasedOnProgress(App.userManager.getCampaignProgress());
        oldGames = loadGamesFromSave();
        setupOldGamesBox(oldGames);
        setupUserOldGamesBox(oldGames);
    }

    private void setupPuzzleOptions() {
        enterPuzzleButton.setOnMouseClicked(e ->{
            App.changeToMainScreenPuzzle();
        });
    }

    public void setProfileInfo(ProfilePicture picture, String name, int elo, int uuid) {
        profileButton.setImage(new Image(picture.urlString));
        nameProfileLabel.setText(name);
        eloProfileLabel.setText(Integer.toString(elo));
    }

    public void setUserProfileInfo(ProfilePicture picture, String name, int elo,int uuid) {
        userInfoPfp.setImage(new Image(picture.urlString));
        userInfoUserName.setText("Name: " + name);
        userInfoUserElo.setText("ELO: " + elo);
        userInfoUUID.setText("ID: " + uuid);
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
        App.bindingController.bindSmallTextCustom(playAsWhite, Window.Start, "-fx-background-color: white ;-fx-text-fill: black");
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
                App.bindingController.bindSmallTextCustom(playAsWhite, Window.Start, "-fx-background-color: white ;-fx-text-fill: black");
            } else {
                playAsWhite.setText("Play as Black");
                App.bindingController.bindSmallTextCustom(playAsWhite, Window.Start, "-fx-background-color: black ;-fx-text-fill: white");

            }
//
        });
    }

    private void setUpSideNavButtons() {
        campaignButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.CAMPAIGN);
            campaignManager.scrollToPlayerTier(App.userManager.getCampaignProgress());
        });
        App.bindingController.bindSmallText(campaignButton, Window.Start);

        localButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.REGULAR);
        });
        App.bindingController.bindSmallText(localButton, Window.Start);

        pgnButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.PGN);
        });
        App.bindingController.bindSmallText(pgnButton, Window.Start);

        multiplayerButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.MULTIPLAYER);
        });
        App.bindingController.bindSmallText(multiplayerButton, Window.Start);

        extraModesButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.EXTRA);
        });
        App.bindingController.bindSmallText(extraModesButton, Window.Start);

        settingsButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.GENERALSETTINGS);
        });
        App.bindingController.bindSmallText(settingsButton, Window.Start);


        backgroundAudioButton.setOnMouseClicked(e -> {
            boolean isCurPaused = !App.userPreferenceManager.isBackgroundMusic();
            if (isCurPaused) {
                // unpause so change to playing icon
                backgroundAudioButton.setGraphic(CommonIcons.unmutedAudio);
            } else {
                // pause so make not playing icon
                backgroundAudioButton.setGraphic(CommonIcons.mutedAudio);

            }
            // muted = opposite of is bg music
            App.userPreferenceManager.setBackgroundmusic(isCurPaused);
        });
        App.bindingController.bindSmallText(backgroundAudioButton, Window.Start);

    }


    private boolean handleAnInvalid(TextField inputBox, String identifier){
        // clear any existing "errors"
        inputBox.setPromptText("");
        inputBox.setStyle("");

        String input = inputBox.getText();
        if (input.isEmpty()) {
            inputBox.setPromptText("please enter a " + identifier);
            inputBox.setStyle("-fx-border-color: red");
            return true;
        }
        else if(input.contains(",")){
            inputBox.clear();
            inputBox.setPromptText(identifier + " may not contain a comma");
            inputBox.setStyle("-fx-border-color: red");
            return true;
        }
        else if(input.length() >= 255){
            inputBox.clear();
            inputBox.setPromptText(identifier + " too long");
            inputBox.setStyle("-fx-border-color: red");
            return true;
        }
        return false;
    }

    private void setUpUserSettings() {
        userInfoManager = new UserInfoManager(this,App.userManager.isLoggedIn() ? UserInfoState.LOGGEDIN : UserInfoState.SIGNEDOUT);
        profileButton.setOnMouseClicked(e -> {
            if (currentState == StartScreenState.USERSETTINGS) {
                if(userInfoPage.isVisible()){
                    setSelection(lastStateBeforeUserSettings);
                }
                else{
                    setUserOptions(0);
                }
            } else {
                App.resyncFriends(false);
                resetUserInfo();
                userInfoManager.showFriends();
                setUserOptions(0);
                setSelection(StartScreenState.USERSETTINGS);
            }
        });
        loginButton.setOnMouseClicked(e -> {
            boolean isInvalid = handleAnInvalid(nameInput,"Username") || handleAnInvalid(passwordInput,"Password");
            if(isInvalid){
                return;
            }
            try{
                String passwordHash = CryptoUtils.sha256AndBase64(passwordInput.getText());
                App.getUserRequest(nameInput.getText(),passwordHash,(out) ->{
                    if(out.isEmpty()){
                        Platform.runLater(() -> {
                            App.messager.sendMessage("Invalid account!", Window.Start);
                        });
                    }
                    else{
                        try{
                            KeyManager.saveNewPassword(passwordHash);
                            DatabaseEntry newEntry = App.objectMapper.readValue(out, DatabaseEntry.class);
                            Platform.runLater(() ->{
                                System.out.println("loading new user: " + newEntry.getUserInfo().getUserName());
                                App.refreshAppWithNewUser(newEntry);
                                App.updateServerTempValues(passwordHash);

                            });

                        } catch (JsonProcessingException jsonProcessingException) {
                            logger.error("Json processing exeption when parsing validate client request output!\n",jsonProcessingException);
                        }
                    }}
                );
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException){
                logger.error("Never gonna hit this lol",noSuchAlgorithmException);
            }
            nameInput.clear();
            passwordInput.clear();



        });


        createAccountButton.setOnMouseClicked(e -> {
            String usernameText = createUsername.getText();
            String passwordText = createPassword.getText();
            boolean isInvalid = handleAnInvalid(createUsername,"Username") || handleAnInvalid(createPassword,"Password");
            if(isInvalid){
                return;
            }





            App.sendRequest(INTENT.CHECKUSERNAME, usernameText,(out ->{
                int uuid;
                boolean isUsernamePresent;
                if(!out.isEmpty()){
                    uuid = Integer.parseInt(out);
                    isUsernamePresent = false;
                } else {
                    uuid = -1;
                    isUsernamePresent = true;
                }
                Platform.runLater(() ->{
                    usernamePresentResponse(isUsernamePresent,uuid,usernameText,passwordText);
                });
            }),true);

        });

        signUpInsteadButton.setOnMouseClicked(e -> {
            setUserOptions(2);
        });

        loginInsteadButton.setOnMouseClicked(e -> {
            setUserOptions(1);
        });

        SignUpPage.setOnMouseClicked(e -> {
            setUserOptions(2);
        });

        LoginPage.setOnMouseClicked(e -> {
            setUserOptions(1);
        });

        backToUserInfo.setOnMouseClicked(e -> {
            resetUserInfo();
        });

        backToUserInfo1.setOnMouseClicked(e -> {
            resetUserInfo();
        });

        userInfoBackButton.setOnMouseClicked(e ->{
            resetUserInfo();
        });
        // default is user info screen
        setUserOptions(0);

        userInfoPfp.setOnMouseClicked(e ->{
            ProfilePicture nextPicture = getNextPfp(App.userManager.getUserPfp());
            userInfoPfp.setImage(new Image(nextPicture.urlString));
            App.userManager.updateUserPfp(nextPicture);
        });

        friendsLookupInput.textProperty().addListener((observable, oldValue, newValue) ->{

            friendsLookupContent.getChildren().clear();
            Label noResultsLabel = new Label("No Results");
            App.bindingController.bindSmallText(noResultsLabel,Window.Start,"Black");
            friendsLookupContent.getChildren().add(noResultsLabel);

            if(!newValue.isEmpty()){
                String inputText = newValue.trim();
                if(lookupCache.containsKey(inputText)){
                    userInfoManager.updateFriendsLookup(lookupCache.get(inputText));
                }
                else{
                    App.sendRequest(INTENT.MATCHALLUSERNAMES,inputText,(out) ->{
                        ClientsideFriendDataResponse response = UserHelperFunctions.readFriendDataResponse(App.readFromObjectMapper(out, FriendDataResponse.class));
                        lookupCache.put(inputText,response);
                        Platform.runLater(() ->{
                            userInfoManager.updateFriendsLookup(response);
                        });
                    },true);
                }
            }

        });

        // default panel is your friends panel
        setFriendsPanel(0);

        FriendsButton.setOnMouseClicked(e ->{
            setFriendsPanel(0);
            userInfoManager.showFriends();
        });

        RequestsButton.setOnMouseClicked(e ->{
            setFriendsPanel(0);
            userInfoManager.showIncomingRequests();
        });

        SuggestedFriendsButton.setOnMouseClicked(e ->{
            setFriendsPanel(0);
            userInfoManager.showSuggestedFriends();
        });

        friendsLookupButton.setOnMouseClicked(e ->{
            setFriendsPanel(1);
        });

        // bindings

        // keep user bottom user info box symmetric
        userOldGamesLabelContainer.prefHeightProperty().bind(friendsNavBar.heightProperty());

        // bind top left profile icon
        App.bindingController.bindCustom(fullScreen.widthProperty(),profileButton.fitWidthProperty(),150,.08);
        App.bindingController.bindCustom(fullScreen.widthProperty(),profileButton.fitWidthProperty(),150,.08);

        App.bindingController.bindCustom(topUserInfoBox.heightProperty(),userInfoPfp.fitHeightProperty(),150,1);
        userInfoPfp.fitWidthProperty().bind(userInfoPfp.fitHeightProperty());

        // text size/color bindings
        App.bindingController.bindSmallText(friendsLookupInput,Window.Start,"Black");
        App.bindingController.bindSmallText(FriendsButton,Window.Start,"Black");
        App.bindingController.bindSmallText(RequestsButton,Window.Start,"Black");
        App.bindingController.bindSmallText(SuggestedFriendsButton,Window.Start,"Black");
        App.bindingController.bindSmallText(friendsLookupButton,Window.Start,"Black");
        App.bindingController.bindMediumText(userInfoUserName,Window.Start,"Black");
        App.bindingController.bindSmallText(userInfoUUID,Window.Start,"Black");
        App.bindingController.bindSmallText(userInfoUserElo,Window.Start,"Black");
        App.bindingController.bindSmallText(userInfoRank,Window.Start,"Black");
        App.bindingController.bindSmallText(userOldGamesLabel,Window.Start,"Black");
        App.bindingController.bindSmallText(LoginPage,Window.Start,"Black");
        App.bindingController.bindSmallText(SignUpPage,Window.Start,"Black");
        App.bindingController.bindSmallText(userInfoBackButton,Window.Start,"Black");

        userOldGamesContent.setStyle("-fx-background-color: gray");
        friendsLookup.setStyle("-fx-background-color: gray");
        friendsContent.setStyle("-fx-background-color: gray");

        setUserInfoNav(0);
    }

    public void resetUserInfo(){
        setUserOptions(0);
        userInfoManager.clearAllUserPanels();
        userInfoManager.changeUserInfoState(App.userManager.isLoggedIn() ? UserInfoState.LOGGEDIN : UserInfoState.SIGNEDOUT);
        userInfoManager.reloadUserPanel(App.userManager.getCurrentUser(),false,false);
    }

    public void reset(){
        nameInput.clear();
        passwordInput.clear();
        createUsername.clear();
        createPassword.clear();
        friendsLookupInput.clear();
        friendsLookupContent.getChildren().clear();
        pgnTextArea.clear();
        poolCount.setText("");
    }





    private ProfilePicture getNextPfp(ProfilePicture current) {
        int next = (current.ordinal() + 1) % ProfilePicture.values().length;
        return ProfilePicture.values()[next];


    }

    /** 0 = user info 1 = login 2 = sign up **/
    private void setUserOptions(int i) {
        setUserInfoNav(i == 0 ? 0 : 1);
        accountCreationPage.setVisible(i == 2);
        accountCreationPage.setMouseTransparent(i != 2);
        loginPage.setVisible(i == 1);
        loginPage.setMouseTransparent(i != 1);
        userInfoPage.setVisible(i == 0);
        userInfoPage.setMouseTransparent(i != 0);
    }
    /** 0 = your friends,  1 = friends lookup**/
    private void setFriendsPanel(int i){
        friendsContent.setVisible(i == 0);
        friendsContent.setMouseTransparent(i != 0);


        friendsLookup.setVisible(i == 1);
        friendsLookup.setMouseTransparent(i != 1);

    }
    /** 0 = regular nav, 1 = back button only**/
    void setUserInfoNav(int i){
        hyperlinkBox.setVisible(i == 0);
        hyperlinkBox.setMouseTransparent(i != 0);

        backButtonBox.setVisible(i == 1);
        backButtonBox.setMouseTransparent(i != 1);
    }

    private void setUpGeneralSettings() {
        UserPreferenceManager.setupUserSettingsScreen(themeSelection, bgColorSelector, pieceSelector, audioMuteBGButton, audioSliderBG, audioMuteEffButton, audioSliderEff, evalOptions, nMovesOptions, computerOptions, Window.Main);
        App.bindingController.bindSmallText(themeLabel, Window.Start);
        App.bindingController.bindSmallText(bgLabel, Window.Start);
        App.bindingController.bindSmallText(pieceLabel, Window.Start);
        App.bindingController.bindSmallText(audioLabelBG, Window.Start);
        App.bindingController.bindSmallText(audioMuteBG, Window.Start);
        App.bindingController.bindSmallText(audioLabelEff, Window.Start);
        App.bindingController.bindSmallText(audioMuteEff, Window.Start);
        App.bindingController.bindSmallText(evalLabel, Window.Start);
        App.bindingController.bindSmallText(nMovesLabel, Window.Start);
        App.bindingController.bindSmallText(computerLabel, Window.Start);


        // container bindings
        generalSettingsScrollpane.prefWidthProperty().bind(mainArea.widthProperty());
        generalSettingsScrollpane.prefHeightProperty().bind(mainArea.heightProperty());

        generalSettingsVbox.prefWidthProperty().bind(generalSettingsScrollpane.widthProperty());

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
            App.sendRequest(INTENT.GETNUMBEROFPOOLERS, gameTypes.getValue(),(out) ->{
                Platform.runLater(() -> {
                    App.startScreenController.poolCount.setText("number of players in pool: " + out);
                });
            } ,true);
        });
        multiplayerStart.setOnMouseClicked(e -> {
            if (!gameTypes.getSelectionModel().isEmpty()) {
                String gameType = gameTypes.getValue();
                App.changeToMainScreenOnline(ChessGame.getOnlinePreInit(gameType,App.userManager.getUserName(),App.userManager.getUserElo(),App.userManager.getUserPfpUrl()),gameType); // isPlayer1White will change when match is found
            }

        });
        // handle no internet connection
        if (!App.isWebClientConnected()) {
            disableMultioptions();
        } else {
            enableMultioptions(false);
        }
        reconnectButton.setGraphic(new FontIcon("fas-sync"));
        reconnectButton.setOnMouseClicked(e -> {
            boolean isSucess = App.attemptReconnection();
            if (isSucess) {
                App.messager.sendMessage("Connected to server", Window.Start);
                enableMultioptions(true);
            } else {
                App.messager.sendMessage("Connection Failed", Window.Start);
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
                    App.changeToMainScreenWithGame(game, MainScreenState.LOCAL,true);

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
        content.prefWidthProperty().bind(fullScreen.widthProperty());
        content.prefHeightProperty().bind(fullScreen.heightProperty());

        mainArea.prefWidthProperty().bind(fullScreen.widthProperty().subtract(rightSidePanel.widthProperty()).subtract(sideButtons.widthProperty()));
        mainArea.prefHeightProperty().bind(fullScreen.heightProperty().subtract(profileBox.heightProperty()));

        // top left profile info
        App.bindingController.bindSmallText(nameProfileLabel, Window.Start);
        App.bindingController.bindSmallText(eloProfileLabel, Window.Start);
        sideButtons.prefHeightProperty().bind(content.heightProperty().subtract(profileBox.heightProperty()));

        // profile options
        App.bindingController.bindLargeText(loginTitle, Window.Start, "black");
        App.bindingController.bindMediumText(nameLabel, Window.Start, "black");
        App.bindingController.bindMediumText(passwordLabel, Window.Start, "black");

        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, passwordInput, 350, .45);
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, nameInput, 350, .45);

        oldGamesPanel.prefHeightProperty().bind(mainArea.heightProperty());
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(mainArea, oldGamesPanel, 350, .5);
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(mainArea, themeSelection, 50, .1);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(mainArea, themeSelection, 100, .1);
        oldGamesPanelContent.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        App.bindingController.bindSmallText(oldGamesLabel, Window.Start, "black");
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
        profileScreen.setVisible(false);
        profileScreen.setMouseTransparent(true);
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
                profileScreen.setVisible(true);
                profileScreen.setMouseTransparent(false);
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
    public void AddNewGameToSaveGui(ChessGame newGame){
        AddNewGameToSaveGui(newGame,oldGamesPanelContent);
        AddNewGameToSaveGui(newGame,userOldGamesContent);
    }

    private void AddNewGameToSaveGui(ChessGame newGame, Pane gamesContainer) {
        HBox gameContainer = new HBox();

        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.setSpacing(2);

        VBox gameInfo = new VBox();
        HBox innerGameInfo = new HBox();

        gameInfo.setAlignment(Pos.CENTER);
        gameInfo.setSpacing(1);

        innerGameInfo.setAlignment(Pos.CENTER);

        Label gameName = new Label(newGame.getGameName());
        App.bindingController.bindSmallText(gameName, Window.Start, "Black");

        Label playersName = new Label(newGame.getWhitePlayerName() + " vs " + newGame.getBlackPlayerName());
        App.bindingController.bindSmallText(playersName, Window.Start, "Black");


        innerGameInfo.getChildren().addAll(playersName);
        gameInfo.getChildren().addAll(gameName, innerGameInfo);

        Button deleteButton = new Button();
        deleteButton.setMinWidth(Button.USE_PREF_SIZE);
        deleteButton.setMinHeight(Button.USE_PREF_SIZE);
        deleteButton.setMaxWidth(Button.USE_PREF_SIZE);
        deleteButton.setMaxHeight(Button.USE_PREF_SIZE);
        deleteButton.setOnMouseClicked(e -> {
            removeFromOldGames(String.valueOf(newGame.getGameHash()));
        });
        App.bindingController.bindCustom(gameContainer.widthProperty(), deleteButton.prefWidthProperty(), 30, .2);
        deleteButton.prefHeightProperty().bind(deleteButton.prefWidthProperty().multiply(1.2));
        App.bindingController.bindSmallText(deleteButton,Window.Start);
        deleteButton.setGraphic(new FontIcon("fas-trash"));


        Button openGame = new Button();
        openGame.setMinWidth(Button.USE_PREF_SIZE);
        openGame.setMinHeight(Button.USE_PREF_SIZE);
        openGame.setMaxWidth(Button.USE_PREF_SIZE);
        openGame.setMaxHeight(Button.USE_PREF_SIZE);
        openGame.setOnMouseClicked(e -> {
            App.changeToMainScreenWithGame(newGame.cloneGame(), MainScreenState.VIEWER ,false);

        });
        App.bindingController.bindCustom(gameContainer.widthProperty(), openGame.prefWidthProperty(), 30, .2);
        openGame.prefHeightProperty().bind(openGame.prefWidthProperty().multiply(1.2));
        App.bindingController.bindSmallText(openGame,Window.Start);
        openGame.setGraphic(new FontIcon("fas-folder-open"));

        gameContainer.getChildren().add(gameInfo);
        gameContainer.getChildren().add(openGame);
        gameContainer.getChildren().add(deleteButton);

        gameContainer.prefHeightProperty().bind(deleteButton.prefHeightProperty().add(3));
        gameContainer.prefWidthProperty().bind(gamesContainer.widthProperty());
        gameContainer.setStyle("-fx-background-color: darkgrey");
        gameContainer.setUserData(String.valueOf(newGame.getGameHash()));

        gamesContainer.getChildren().add(0, gameContainer);
    }

    private void removeFromOldGames(String hashCode) {
//        PersistentSaveManager.removeGameFromData(hashCode);
        App.userManager.removeGameFromSave(hashCode);
        oldGamesPanelContent.getChildren().removeIf(e -> e.getUserData().equals(hashCode));
    }

    private List<ChessGame> loadGamesFromSave() {
        return App.userManager.readSavedGames();
    }

    public void setupOldGamesBox(List<ChessGame> gamesToLoad) {
        oldGamesPanelContent.getChildren().clear();
        for (ChessGame g : gamesToLoad) {
            AddNewGameToSaveGui(g,oldGamesPanelContent);
        }
    }
    public void setupUserOldGamesBox(List<ChessGame> gamesToLoad) {
        System.out.println("A\nA\nA\nA\nA\nA\nA\nA\nA\nA\n");
        userOldGamesContent.getChildren().clear();
        for (ChessGame g : gamesToLoad) {
            AddNewGameToSaveGui(g,userOldGamesContent);
        }
    }

    public void usernamePresentResponse(boolean isUsernamePresent,int currentUUID,String lastUsername,String lastPassword) {
        if (lastUsername != null && lastPassword != null) {
            if (isUsernamePresent) {
                createUsername.clear();
                createUsername.setPromptText("This username is already taken!");
                createUsername.setStyle("-fx-border-color: red");
            } else {
                createUsername.clear();
                createPassword.clear();
                App.messager.sendMessage("Creating account", Window.Start);
                App.createAndLoginClientRequest(lastUsername, lastPassword,currentUUID);
            }
        } else {
            logger.error("Getting username present response without correct last values!");
        }
    }

    public void setDefaultSelections(UserPreferences userPref) {
        themeSelection.getSelectionModel().select(userPref.getGlobalTheme().toString());
        computerOptions.getSelectionModel().select(userPref.getComputerMoveDiff().eloRange + (userPref.getComputerMoveDiff().isStockfishBased ? "(S*)" : ""));
        evalOptions.getSelectionModel().select(userPref.getEvalStockfishBased() ? "Stockfish" : "My Computer");
        nMovesOptions.getSelectionModel().select(userPref.getNMovesStockfishBased() ? "Stockfish" : "My Computer");
        audioSliderBG.setValue(userPref.getBackgroundVolume());
        audioSliderEff.setValue(userPref.getEffectVolume());
        bgColorSelector.setValue(userPref.getChessboardTheme().toString());
        pieceSelector.setValue(userPref.getPieceTheme().toString());
        audioMuteBGButton.setGraphic(userPref.isBackgroundmusic() ? CommonIcons.unmutedAudio : CommonIcons.mutedAudio);
        audioMuteEffButton.setGraphic(userPref.isEffectSounds() ? CommonIcons.unmutedAudio : CommonIcons.mutedAudio);
    }
}
