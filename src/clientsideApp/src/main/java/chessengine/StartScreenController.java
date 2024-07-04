package chessengine;

import chessserver.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable {

    @FXML
    StackPane fullscreen;

    @FXML
    GridPane content;

    @FXML
    Pane startMessageBoard;

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
    HBox sandboxScreen;

    @FXML
    Button enterSandboxButton;


    // campaign screen
    @FXML
    StackPane levelContainer;
    @FXML
    Pane levelContainerPath;
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



    // pgn screen

    @FXML
    TextArea pgnTextArea;

    @FXML
    Button pgnLoadGame;

    @FXML
    RadioButton pvpRadioButton;

    @FXML
    RadioButton computerRadioButton;

    @FXML
    Button addNewGame;

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
    Button backgroundAudioButton;

    @FXML
    Button sandboxButton;




    // multiplayer options
    @FXML
    ComboBox<String> gameTypes;

    @FXML
    Button multiplayerStart;

    @FXML
    Label poolCount;

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
    Label audioMuteBG;

    @FXML
    Button audioMuteBGButton;

    @FXML
    Label audioLabelBG;

    @FXML
    Slider audioSliderBG;

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



    // user settings


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


    List<ChessGame> oldGames;
    private StartScreenState currentState;

    private StartScreenState lastStateBeforeUserSettings;

    public CampaignManager campaignManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startMessageBoard.setMouseTransparent(true);





    }

    public void setup(){
        setSelection(StartScreenState.REGULAR);
        setUpLocalOptions();
        setUpPgnOptions();
        setUpMultiOptions();
        setUpUserSettings();
        setUpGeneralSettings();
        setUpSideNavButtons();
        setupSandboxOptions();

        setUpMiscelaneus();
        setUpBindings();

        setUpCampaignScreen();
        campaignManager = new CampaignManager(levelContainer,levelContainerElements,levelContainerPath,campaignScroller,mainArea,campaignBackground,campaignBackground2);
        campaignManager.setLevelUnlocksBasedOnProgress(App.userManager.getCampaignProgress());

        oldGames = loadGamesFromSave();
        setupOldGamesBox(oldGames);
    }

    public void setProfileInfo(ProfilePicture picture, String name, int elo){
        profileButton.setImage(new Image(picture.urlString));
        System.out.println("Changing image: " + picture.urlString);
        nameProfileLabel.setText(name);
        eloProfileLabel.setText(Integer.toString(elo));
    }



    private void setUpCampaignScreen(){
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

    private void setUpLocalOptions(){
        vsPlayer.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("game" ,false,MainScreenState.LOCAL);

        });
        vsComputer.setOnMouseClicked(e -> {
            App.changeToMainScreenWithoutAny("game" ,true,MainScreenState.LOCAL);
        });
    }

    private void setUpSideNavButtons(){
        campaignButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.CAMPAIGN);
            campaignManager.scrollToPlayerTier(App.userManager.getCampaignProgress());
        });
        App.bindingController.bindSmallText(campaignButton,false);

        localButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.REGULAR);
        });
        App.bindingController.bindSmallText(localButton,false);

        pgnButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.PGN);
        });
        App.bindingController.bindSmallText(pgnButton,false);

        multiplayerButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.MULTIPLAYER);
        });
        App.bindingController.bindSmallText(multiplayerButton,false);

        sandboxButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.SANDBOX);
        });
        App.bindingController.bindSmallText(sandboxButton,false);

        settingsButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.GENERALSETTINGS);
        });
        App.bindingController.bindSmallText(settingsButton,false);

        backgroundAudioButton.setOnMouseClicked(e->{
            boolean isCurPaused = App.soundPlayer.getPaused();
            if(isCurPaused){
                // unpause so change to playing icon
                backgroundAudioButton.setText("🔉");
            }
            else{
                // pause so make not playing icon
                backgroundAudioButton.setText("✖");

            }
            // muted = opposite of is bg music
            App.userPreferenceManager.setBackgroundmusic(isCurPaused);
        });
        App.bindingController.bindSmallText(backgroundAudioButton,false);

    }
    boolean isRed = false;


    private void setUpUserSettings(){
        profileButton.setOnMouseClicked(e->{
           if(currentState.equals(StartScreenState.USERSETTINGS)){
               setSelection(lastStateBeforeUserSettings);
           }
           else{
               setSelection(StartScreenState.USERSETTINGS);
           }
        });
        saveUserOptions.setOnMouseClicked(e->{

            if(nameInput.getText().isEmpty()){
                nameInput.setPromptText("please enter a name");
            }
            if(passwordInput.getText().isEmpty()){
                passwordInput.setPromptText("please enter a password");
            }
            else{
                App.webclient.validateClientRequest(nameInput.getText(),passwordInput.getText());


            }

        });

    }

    private void setUpGeneralSettings(){
        UserPreferenceManager.setupUserSettingsScreen(themeSelection,bgColorSelector,pieceSelector,audioMuteBGButton,audioSliderBG,audioMuteEffButton,audioSliderEff,evalOptions,computerOptions,false);
        App.bindingController.bindSmallText(themeLabel,false);
        App.bindingController.bindSmallText(bgLabel,false);
        App.bindingController.bindSmallText(pieceLabel,false);
        App.bindingController.bindSmallText(audioLabelBG,false);
        App.bindingController.bindSmallText(audioMuteBG,false);
        App.bindingController.bindSmallText(audioLabelEff,false);
        App.bindingController.bindSmallText(audioMuteEff,false);
        App.bindingController.bindSmallText(evalLabel,false);
        App.bindingController.bindSmallText(computerOptions,false);








        // container bindings
        generalSettingsScrollpane.prefWidthProperty().bind(mainArea.widthProperty());
        generalSettingsScrollpane.prefHeightProperty().bind(mainArea.heightProperty());

        generalSettingsVbox.prefWidthProperty().bind(generalSettingsScrollpane.widthProperty());



        // binding selectors and buttons

        bgColorSelector.prefWidthProperty().bind(themeSelection.widthProperty());
        bgColorSelector.prefHeightProperty().bind(themeSelection.heightProperty());


        // binding labels
//        BindingController.bindChildTextToParentWidth(themeSelection,themeLabel,.1);


    }

    private void setUpMultiOptions(){

        gameTypes.getItems().addAll(Arrays.stream(Gametype.values()).map(Gametype::getStrVersion).toList());
        gameTypes.setOnAction(e->{
            App.webclient.sendRequest(INTENT.GETNUMBEROFPOOLERS,gameTypes.getValue());
        });
        multiplayerStart.setOnMouseClicked(e->{
            if(!gameTypes.getSelectionModel().isEmpty()){
                App.changeToMainScreenWithGame(new ChessGame(gameTypes.getValue()),false,MainScreenState.ONLINE);
            }

        });
        // handle no internet connection
        if(App.webclient == null){
            disableMultioptions();
        }
        else{
            enableMultioptions(false);
        }
        reconnectButton.setOnMouseClicked(e->{
            boolean isSucess = App.attemptReconnection();
            if(isSucess){
                App.messager.sendMessageQuick("Connected to server",true);
                enableMultioptions(true);
            }
            else{
                App.messager.sendMessageQuick("Connection Failed",true);
                disableMultioptions();
            }

        });



    }

    public void disableMultioptions(){
        gameTypes.setDisable(true);
        multiplayerStart.setDisable(true);
        poolCount.setText("No Server Connection!");
        reconnectButton.setVisible(true);
        reconnectButton.setMouseTransparent(false);
    }

    public void enableMultioptions(boolean showMessage){
        gameTypes.setDisable(false);
        multiplayerStart.setDisable(false);
        if(showMessage){
            poolCount.setText("Connected To Internet");
        }
        else{
            poolCount.setText("");
        }
        reconnectButton.setVisible(false);
        reconnectButton.setMouseTransparent(true);
    }

    private void setUpPgnOptions(){
        // default options
        computerRadioButton.setSelected(false);
        pvpRadioButton.setSelected(true);
        // styling
        pgnTextArea.setStyle("-fx-text-fill: Black");


        pvpRadioButton.setOnMouseClicked(e ->{
            computerRadioButton.setSelected(!pvpRadioButton.isSelected());
        });
        computerRadioButton.setOnMouseClicked(e ->{
            pvpRadioButton.setSelected(!computerRadioButton.isSelected());
        });
        pgnLoadGame.setOnMouseClicked(e-> {
            if(pgnTextArea.getText().isEmpty()){
                // todo make blinking red effect
                // todo validate text
                //pgnTextArea.setEffect...
                pgnTextArea.setPromptText("Please enter a pgn!");

            }
            else{
                try {
                    ChessGame game = new ChessGame(pgnTextArea.getText(),"game",computerRadioButton.isSelected());
                    App.changeToMainScreenWithGame(game,computerRadioButton.isSelected(),MainScreenState.LOCAL);
                }
                catch (Exception ex){
                    pgnTextArea.clear();
                    pgnTextArea.setPromptText("Invalid pgn entered");
                }

            }
        });

    }


    private void setupSandboxOptions(){
        enterSandboxButton.setOnMouseClicked(e->{
            App.changeToMainScreenWithoutAny("Sandbox",false,MainScreenState.SANDBOX);
        });
    }
    private final int maxNewGameButtonSize = 100;
    private void setUpBindings(){
        // top left profile info
        App.bindingController.bindSmallText(nameProfileLabel,false);
        App.bindingController.bindSmallText(eloProfileLabel,false);
        mainAreaTopSpacer.prefHeightProperty().bind(content.heightProperty().multiply(0.01));
        content.prefWidthProperty().bind(fullscreen.widthProperty());
        content.prefHeightProperty().bind(fullscreen.heightProperty());
        sideButtons.prefHeightProperty().bind(content.heightProperty().subtract(profileBox.heightProperty()).subtract(bottomSpacer.heightProperty()));
        mainAreaReference.prefWidthProperty().bind(content.widthProperty().subtract(sideButtons.widthProperty()).subtract(rightSidePanel.widthProperty()));
        mainAreaReference.prefHeightProperty().bind(content.heightProperty().subtract(mainAreaTopSpacer.heightProperty()).subtract(bottomSpacer.heightProperty()));
        mainArea.prefWidthProperty().bind(mainAreaReference.widthProperty());
        mainArea.prefHeightProperty().bind(mainAreaReference.heightProperty());
        mainArea.setStyle("-fx-border-color: black;-fx-border-width: 2px");

        // profile options
        App.bindingController.bindLargeText(loginTitle,false,"black");
        App.bindingController.bindMediumText(nameLabel,false,"black");
        App.bindingController.bindMediumText(eloLabel,false,"black");

        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, passwordInput,350,.45);
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,nameInput,350,.45);

        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,addNewGame,maxNewGameButtonSize,.1);
        addNewGame.prefHeightProperty().bind(addNewGame.widthProperty());
        oldGamesPanel.prefHeightProperty().bind(mainArea.heightProperty());
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(mainArea,oldGamesPanel,350,.5);
        App.bindingController.bindChildHeightToParentHeightWithMaxSize(mainArea,themeSelection,50,.1);
        App.bindingController.bindChildWidthToParentWidthWithMaxSize(mainArea,themeSelection,100,.1);
        oldGamesPanelContent.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        App.bindingController.bindSmallText(oldGamesLabel,false,"black");
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,reconnectButton,120,.30);
        reconnectButton.prefHeightProperty().bind(reconnectButton.prefWidthProperty().multiply(0.75));
    }

    private void setUpMiscelaneus(){
        oldGamesPanelContent.setAlignment(Pos.TOP_CENTER);
        oldGamesPanelContent.setSpacing(3);


    }
    private void hideAllScreensnButtons(){
        campaignScreen.setVisible(false);
        campaignScreen.setMouseTransparent(true);
        sandboxScreen.setVisible(false);
        sandboxScreen.setMouseTransparent(true);
        pgnSelectionScreen.setVisible(false);
        pgnSelectionScreen.setMouseTransparent(true);
        mainSelectionScreen.setVisible(false);
        mainSelectionScreen.setMouseTransparent(true);
        multiplayerSelectionScreen.setVisible(false);
        multiplayerSelectionScreen.setMouseTransparent(true);
        userSettingScreen.setVisible(false);
        userSettingScreen.setMouseTransparent(true);
        generalSettingsScreen.setVisible(false);
        generalSettingsScreen.setMouseTransparent(true);

    }
    private void setSelection(StartScreenState state){
        hideAllScreensnButtons();
        switch (state){
            case PGN -> {
                pgnSelectionScreen.setVisible(true);
                pgnSelectionScreen.setMouseTransparent(false);
            }
            case REGULAR -> {
                mainSelectionScreen.setVisible(true);
                mainSelectionScreen.setMouseTransparent(false);
            }
            case MULTIPLAYER -> {
                multiplayerSelectionScreen.setVisible(true);
                multiplayerSelectionScreen.setMouseTransparent(false);
            }
            case USERSETTINGS -> {
                lastStateBeforeUserSettings = currentState;
                userSettingScreen.setVisible(true);
                userSettingScreen.setMouseTransparent(false);
            }
            case GENERALSETTINGS -> {
                generalSettingsScreen.setVisible(true);
                generalSettingsScreen.setMouseTransparent(false);
            }
            case SANDBOX -> {
                sandboxScreen.setVisible(true);
                sandboxScreen.setMouseTransparent(false);
            }
            case CAMPAIGN -> {
                campaignScreen.setVisible(true);
                campaignScreen.setMouseTransparent(false);
            }
        }
        this.currentState = state;


    }

    private final Image trashIcon = new Image("/StartScreenIcons/trash.png");
    private final Image computerIcon = new Image("/StartScreenIcons/robot.png");
    private final Image playerIcon = new Image("/StartScreenIcons/player.png");

    public void AddNewGameToSaveGui(ChessGame newGame){
        HBox gameContainer = new HBox();

        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.setSpacing(2);

        VBox gameInfo = new VBox();
        HBox innerGameInfo = new HBox();

        gameInfo.setAlignment(Pos.CENTER);
        gameInfo.setSpacing(1);

        innerGameInfo.setAlignment(Pos.CENTER);

        Label gameName = new Label("Name: " + newGame.getGameName());
        App.bindingController.bindMediumText(gameName, false,"Black");

        Label playersName = new Label(newGame.getPlayer1name() + " vs " + newGame.getPlayer2name());
        App.bindingController.bindMediumText(playersName, false,"Black");



        innerGameInfo.getChildren().addAll(playersName);
        gameInfo.getChildren().addAll(gameName,innerGameInfo);

        Button deleteButton = new Button();
        deleteButton.setOnMouseClicked(e->{
            removeFromOldGames(String.valueOf(newGame.getGameHash()));
        });
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(gameContainer,deleteButton,30,.3);
        deleteButton.prefHeightProperty().bind(deleteButton.widthProperty());
//        deleteButton.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView trashIconView = new ImageView(trashIcon);
        trashIconView.fitHeightProperty().bind(gameContainer.widthProperty().divide(10));
        trashIconView.fitWidthProperty().bind(gameContainer.widthProperty().divide(10));
//        deleteButton.setGraphic(trashIconView);



        Button openGame = new Button();
        openGame.setOnMouseClicked(e->{
            App.changeToMainScreenWithGame(newGame,false,MainScreenState.VIEWER);

        });
        App.bindingController.bindChildWidthToParentHeightWithMaxSize(gameContainer,openGame,30,.3);
        openGame.prefHeightProperty().bind(openGame.widthProperty());
//        openGame.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView playerIconView = new ImageView(playerIcon);
        playerIconView.fitHeightProperty().bind(gameContainer.widthProperty().divide(10));
        playerIconView.fitWidthProperty().bind(gameContainer.widthProperty().divide(10));
//        openGame.setGraphic(playerIconView);

        gameContainer.getChildren().add(gameInfo);
        gameContainer.getChildren().add(openGame);
        gameContainer.getChildren().add(deleteButton);

        App.bindingController.bindChildHeightToParentHeightWithMaxSize(oldGamesPanel,gameContainer,75,.15);
        gameContainer.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        gameContainer.setStyle("-fx-background-color: darkgrey");
        gameContainer.setUserData(String.valueOf(newGame.getGameHash()));

        oldGamesPanelContent.getChildren().add(0,gameContainer);
    }

    private void removeFromOldGames(String hashCode){
        PersistentSaveManager.removeGameFromData(hashCode);
        oldGamesPanelContent.getChildren().removeIf(e-> e.getUserData().equals(hashCode));
    }

    private List<ChessGame> loadGamesFromSave(){

        return PersistentSaveManager.readFromAppData();
    }

    private void setupOldGamesBox(List<ChessGame> gamesToLoad){
        oldGamesPanelContent.getChildren().clear();
        for(ChessGame g : gamesToLoad){
            AddNewGameToSaveGui(g);
        }
    }

}
