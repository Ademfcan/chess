package chessengine.Start;

import chessengine.App;
import chessserver.ChessRepresentations.ChessGame;
import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.KeyManager;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.StartScreenState;
import chessengine.Enums.UserInfoState;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Managers.CampaignManager;
import chessengine.Managers.UserPreferenceManager;
import chessserver.Misc.ChessConstants;
import chessengine.Misc.ClientsideFriendDataResponse;
import chessserver.Enums.Gametype;
import chessserver.Enums.INTENT;
import chessserver.Enums.ProfilePicture;
import chessserver.Friends.FriendDataResponse;
import chessserver.Communication.DatabaseEntry;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import org.nd4j.shade.jackson.core.JsonProcessingException;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable {

    private final Logger logger = LogManager.getLogger("Start_Screen_Controller");
    private final int maxNewGameButtonSize = 100;
    private final Image trashIcon = new Image("/StartScreenIcons/trash.png");
    private final Image computerIconUrl = new Image("/StartScreenIcons/robot.png");
    private final Image openIcon = new Image("/StartScreenIcons/openGame.png");
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
    public VBox oldGamesPanelContent;
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



    @FXML
    StackPane userSettingsStack;
    @FXML
    HBox topUserInfoBox;
    @FXML
    VBox topUserInfoLeftBox;
    @FXML
    VBox topUserInfoRightBox;

    @FXML
    HBox bottomUserInfoBox;
    @FXML
    StackPane bottomInfoNav;
    // login page
    @FXML
    HBox backButtonBox;
    @FXML
    Hyperlink userInfoBackButton;

    @FXML
    VBox loginPage;
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
    @FXML
    HBox hyperlinkBox;
    @FXML
    Hyperlink signUpInsteadButton;
    @FXML
    Hyperlink backToUserInfo;

    // sign up page

    @FXML
    VBox accountCreationPage;
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


    // profile
    @FXML
    ImageView profileButton;
    @FXML
    Label nameProfileLabel;
    @FXML
    Label eloProfileLabel;
    @FXML
    Button loginButton;

    @FXML
    VBox userInfoPage;
    @FXML
    ImageView userInfoPfp;
    @FXML
    Label userInfoUserName;
    @FXML
    VBox userInfoUsernameSpacer;
    @FXML
    Label userInfoUUID;
    @FXML
    Label userInfoUserElo;
    @FXML
    Label userInfoRank;
    @FXML
    Hyperlink SignUpPage;
    @FXML
    Hyperlink LoginPage;


    @FXML
    VBox userOldGamesContainer;
    @FXML
    ScrollPane userOldGamesScrollpane;
    @FXML
    Label userOldGamesLabel;
    @FXML
    public VBox userOldGamesContent;

    @FXML
    TextField friendsLookupInput;
    @FXML
    VBox friendsLookup;
    @FXML
    public VBox friendsLookupContent;
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
    private HashMap<String,ClientsideFriendDataResponse> lookupCache;
    public UserInfoManager userInfoManager;
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

        setUpMiscelaneus();
        setUpBindings();

        setUpCampaignScreen();
        campaignManager = new CampaignManager(levelContainer, levelContainerElements, levelContainerPath, campaignScroller, mainArea, campaignBackground, campaignBackground2);
        campaignManager.setLevelUnlocksBasedOnProgress(App.userManager.getCampaignProgress());
        oldGames = loadGamesFromSave();
        setupOldGamesBox(oldGames);
        setupUserOldGamesBox(oldGames);
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
            if (currentState.equals(StartScreenState.USERSETTINGS)) {
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
                            App.messager.sendMessageQuick("Invalid account!",true);
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
            App.bindingController.bindSmallText(noResultsLabel,false,"Black");
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
        App.bindingController.bindCustom(fullscreen.widthProperty(),profileButton.fitWidthProperty(),150,.08);
        App.bindingController.bindCustom(fullscreen.widthProperty(),profileButton.fitWidthProperty(),150,.08);

        userSettingScreen.prefWidthProperty().bind(mainArea.widthProperty());
        userSettingScreen.prefHeightProperty().bind(mainArea.heightProperty());

        userSettingsStack.prefWidthProperty().bind(userSettingScreen.widthProperty());
        userSettingsStack.prefHeightProperty().bind(userSettingScreen.heightProperty());

        userInfoPage.prefWidthProperty().bind(userSettingsStack.widthProperty());
        userInfoPage.prefHeightProperty().bind(userSettingsStack.heightProperty());

        accountCreationPage.prefWidthProperty().bind(userSettingsStack.widthProperty());
        accountCreationPage.prefHeightProperty().bind(userSettingsStack.heightProperty());

        loginPage.prefWidthProperty().bind(userSettingsStack.widthProperty());
        loginPage.prefHeightProperty().bind(userSettingsStack.heightProperty());

        App.bindingController.bindCustom(userSettingsStack.widthProperty(),userInfoPfp.fitWidthProperty(),150,.3);
        App.bindingController.bindCustom(userSettingsStack.heightProperty(),userInfoPfp.fitHeightProperty(),150,.3);

        App.bindingController.bindSmallText(friendsLookupInput,false,"Black");
        App.bindingController.bindSmallText(FriendsButton,false,"Black");
        App.bindingController.bindSmallText(RequestsButton,false,"Black");
        App.bindingController.bindSmallText(SuggestedFriendsButton,false,"Black");
        App.bindingController.bindSmallText(friendsLookupButton,false,"Black");



        // user info page

        // top box
        App.bindingController.bindMediumText(userInfoUserName,false,"Black");
        App.bindingController.bindSmallText(userInfoUUID,false,"Black");
        App.bindingController.bindSmallText(userInfoUserElo,false,"Black");
        App.bindingController.bindSmallText(userInfoRank,false,"Black");

        App.bindingController.bindCustom(userSettingScreen.prefHeightProperty(),topUserInfoBox.prefHeightProperty(),400,.3);
        topUserInfoBox.prefWidthProperty().bind(userSettingScreen.prefWidthProperty());
        userInfoPfp.fitHeightProperty().bind(topUserInfoBox.heightProperty().subtract(userInfoUserName.heightProperty()));
        userInfoPfp.fitWidthProperty().bind(userInfoPfp.fitHeightProperty());
        userInfoUsernameSpacer.prefWidthProperty().bind(userInfoPfp.fitWidthProperty().divide(2).subtract(userInfoUserName.widthProperty().divide(2)));
        topUserInfoRightBox.prefWidthProperty().bind(topUserInfoBox.prefWidthProperty().subtract(userInfoPfp.fitWidthProperty()));
        topUserInfoRightBox.prefHeightProperty().bind(userInfoPfp.fitHeightProperty());

        // bottom box
        App.bindingController.bindSmallText(userOldGamesLabel,false,"Black");
        bottomUserInfoBox.prefHeightProperty().bind(userSettingScreen.prefHeightProperty().subtract(topUserInfoBox.heightProperty()).subtract(bottomInfoNav.heightProperty()));
        bottomUserInfoBox.prefWidthProperty().bind(userSettingScreen.prefWidthProperty());

        userOldGamesContainer.prefHeightProperty().bind(bottomUserInfoBox.heightProperty());
        userOldGamesContainer.prefWidthProperty().bind(userSettingScreen.widthProperty().subtract(FriendsPanel.widthProperty()));

        App.bindingController.bindCustom(bottomUserInfoBox.heightProperty().subtract(FriendsButton.heightProperty()),FriendsPanel.prefHeightProperty(),600,1);
        FriendsPanel.prefWidthProperty().bind(userSettingScreen.prefWidthProperty().multiply(.6));

        App.bindingController.bindCustom(userOldGamesContainer.heightProperty().subtract(userOldGamesLabel.heightProperty()),userOldGamesScrollpane.prefHeightProperty(),600,1);
        userOldGamesScrollpane.prefWidthProperty().bind(userOldGamesContainer.widthProperty());
        userOldGamesContent.prefWidthProperty().bind(userOldGamesScrollpane.widthProperty());
        userOldGamesContent.minHeightProperty().bind(userOldGamesScrollpane.heightProperty());
        userOldGamesContent.setStyle("-fx-background-color: gray");

        FriendsStackpane.prefWidthProperty().bind(FriendsPanel.widthProperty());
        FriendsStackpane.prefHeightProperty().bind(Bindings.max(friendsLookup.heightProperty(), friendsContent.heightProperty()));

        friendsLookup.prefWidthProperty().bind(FriendsStackpane.widthProperty());
        friendsLookup.setStyle("-fx-background-color: gray");
        friendsLookup.minHeightProperty().bind(FriendsPanel.heightProperty());
        friendsLookupContent.prefWidthProperty().bind(friendsLookup.prefWidthProperty());

        friendsContent.prefWidthProperty().bind(FriendsStackpane.widthProperty());
        friendsContent.setStyle("-fx-background-color: gray");
        friendsContent.minHeightProperty().bind(FriendsPanel.heightProperty());
        // hyperlink box
//        bottomInfoNav.prefHeightProperty().bind(LoginPage.heightProperty());
//        bottomInfoNav.prefWidthProperty().bind(userSettingScreen.widthProperty());
//        hyperlinkBox.prefHeightProperty().bind(bottomInfoNav.heightProperty());
//        hyperlinkBox.prefWidthProperty().bind(userSettingScreen.widthProperty());
//        backButtonBox.prefHeightProperty().bind(bottomInfoNav.heightProperty());
//        backButtonBox.prefWidthProperty().bind(userSettingScreen.widthProperty());
        App.bindingController.bindSmallText(LoginPage,false,"Black");
        App.bindingController.bindSmallText(SignUpPage,false,"Black");
        App.bindingController.bindSmallText(userInfoBackButton,false,"Black");

        setUserInfoNav(0);
    }

    public void resetUserInfo(){
        setUserOptions(0);
        userInfoManager.clearAllUserPanels();
        userInfoManager.changeUserInfoState(App.userManager.isLoggedIn() ? UserInfoState.LOGGEDIN : UserInfoState.SIGNEDOUT);
        userInfoManager.reloadUserPanel(App.userManager.getCurrentUser(),false,false);
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

    public void AddNewGameToSaveGui(ChessGame newGame, VBox gamesContainer) {
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
        App.bindingController.bindCustom(gameContainer.widthProperty(), deleteButton.prefWidthProperty(), 30, .3);
        deleteButton.prefHeightProperty().bind(deleteButton.widthProperty());
//        deleteButton.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView trashIconView = new ImageView(trashIcon);
        trashIconView.fitHeightProperty().bind(deleteButton.widthProperty());
        trashIconView.fitWidthProperty().bind(deleteButton.widthProperty());
//        deleteButton.setGraphic(trashIconView);


        Button openGame = new Button();
        openGame.setOnMouseClicked(e -> {
            App.changeToMainScreenWithGame(newGame.cloneGame(), MainScreenState.VIEWER ,false);

        });
        App.bindingController.bindCustom(gameContainer.widthProperty(), openGame.prefWidthProperty(), 30, .3);
        openGame.prefHeightProperty().bind(openGame.widthProperty());
//        openGame.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView playerIconView = new ImageView(openIcon);
        playerIconView.fitHeightProperty().bind(openGame.widthProperty());
        playerIconView.fitWidthProperty().bind(openGame.heightProperty());
//        openGame.setGraphic(playerIconView);

        gameContainer.getChildren().add(gameInfo);
        gameContainer.getChildren().add(openGame);
        gameContainer.getChildren().add(deleteButton);

        App.bindingController.bindCustom(gamesContainer.widthProperty(), gameContainer.prefHeightProperty(), 75, .30);
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

//        return PersistentSaveManager.readGamesFromAppData();
        return App.userManager.readSavedGames();
    }

    public void setupOldGamesBox(List<ChessGame> gamesToLoad) {
        oldGamesPanelContent.getChildren().clear();
        for (ChessGame g : gamesToLoad) {
            AddNewGameToSaveGui(g,oldGamesPanelContent);
        }
    }
    public void setupUserOldGamesBox(List<ChessGame> gamesToLoad) {
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
                App.messager.sendMessageQuick("Creating account", true);
                App.createAndLoginClientRequest(lastUsername, lastPassword,currentUUID);
            }
        } else {
            logger.error("Getting username present response without correct last values!");
        }
    }
}
