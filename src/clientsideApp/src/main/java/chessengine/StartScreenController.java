package chessengine;

import chessserver.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

    @FXML
    StackPane fullscreen;

    @FXML
    GridPane content;

    @FXML
    Pane startMessageBoard;

    @FXML
    HBox leftMain;

    @FXML
    Button vsPlayer;

    @FXML
    Button vsComputer;



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
    StackPane mainArea;

    // side panel stuff
    @FXML
    VBox sideButtons;

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
    @FXML
    Label nameLabel;

    @FXML
    TextField nameInput;

    @FXML
    Label eloLabel;

    @FXML
    TextField passwordInput;

    @FXML
    ImageView profileButton;





    @FXML
    Button saveUserOptions;

    List<ChessGame> oldGames;
    private StartScreenState currentState;

    private StartScreenState lastStateBeforeUserSettings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startMessageBoard.setMouseTransparent(true);

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

        oldGames = loadGamesFromSave();
        setupOldGamesBox(oldGames);



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
        localButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.REGULAR);
        });
        pgnButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.PGN);
        });
        multiplayerButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.MULTIPLAYER);
        });
        sandboxButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.SANDBOX);
        });
        settingsButton.setOnMouseClicked(e->{
            setSelection(StartScreenState.GENERALSETTINGS);
        });
        backgroundAudioButton.setOnMouseClicked(e->{
            boolean isMuted = App.soundPlayer.toggleAudio();
            if(isMuted){
                backgroundAudioButton.setText("✖");
            }
            else{
                backgroundAudioButton.setText("🔉");
            }
            // muted = opposite of is bg music
            App.userPreferenceManager.setBackgroundmusic(!isMuted);
        });
    }


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

        themeSelection.getItems().addAll("Light","Dark");
        themeSelection.setOnAction(e->{
            boolean isLight = themeSelection.getValue().equals("Light");
            App.userPreferenceManager.setGlobalTheme(isLight ? GlobalTheme.Light : GlobalTheme.Dark);
        });

        bgColorSelector.getItems().addAll(Arrays.stream(ChessboardTheme.values()).map(Enum::toString).toList());
        bgColorSelector.setOnAction(e ->{
            App.userPreferenceManager.setChessboardTheme(ChessboardTheme.getCorrespondingTheme(bgColorSelector.getValue()));
        });

        pieceSelector.getItems().addAll(
                Arrays.stream(ChessboardTheme.values()).map(ChessboardTheme::toString).toList()
        );
        pieceSelector.setOnAction(e ->{
            // todo
            App.userPreferenceManager.setPieceTheme(ChessPieceTheme.getCorrespondingTheme(pieceSelector.getValue()));
        });

        audioMuteBGButton.setOnMouseClicked(e->{

        });

        audioSliderBG.valueProperty().addListener(e->{
            App.userPreferenceManager.setBackgroundVolume(audioSliderBG.getValue());
        });

        audioMuteEffButton.setOnMouseClicked(e->{

        });

        audioSliderEff.valueProperty().addListener(e->{
            App.userPreferenceManager.setEffectVolume(audioSliderBG.getValue());
        });

        evalOptions.getItems().addAll(
                1,2,3,4,5,6,7,8
        );
        evalOptions.setOnAction(e ->{
            App.userPreferenceManager.setEvalDepth(evalOptions.getValue());

        });


        computerOptions.getItems().addAll(
                1,2,3,4,5,6,7,8
        );
        computerOptions.setOnAction(e ->{
            App.userPreferenceManager.setComputerMoveDepth(computerOptions.getValue());

        });







        // container bindings
        generalSettingsScrollpane.prefWidthProperty().bind(leftMain.widthProperty().subtract(sideButtons.widthProperty()).subtract(oldGamesPanel.widthProperty()));
        generalSettingsScrollpane.prefHeightProperty().bind(leftMain.heightProperty());

        generalSettingsVbox.prefWidthProperty().bind(generalSettingsScrollpane.widthProperty());
        generalSettingsVbox.prefHeightProperty().bind(generalSettingsScrollpane.heightProperty());



        // binding selectors and buttons
        BindingController.bindChildWidthToParentWidthWithMaxSize(fullscreen,themeSelection,200,.25);
        BindingController.bindChildHeightToParentHeightWithMaxSize(fullscreen,themeSelection,75,.20);
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
            App.messager.sendMessageQuick("Trying to connect",true);
            boolean isSucess = App.attemptReconnection();
            if(isSucess){
                enableMultioptions(true);
            }
            else{
                disableMultioptions();
            }

        });



    }

    public void disableMultioptions(){
        gameTypes.setDisable(true);
        multiplayerStart.setDisable(true);
        poolCount.setText("No Internet Connection!");
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
        computerRadioButton.setSelected(false);
        pvpRadioButton.setSelected(true);


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
        content.prefWidthProperty().bind(fullscreen.widthProperty());
        content.prefHeightProperty().bind(fullscreen.heightProperty());

        mainArea.prefWidthProperty().bind(leftMain.widthProperty().subtract(sideButtons.widthProperty()));
        mainArea.prefHeightProperty().bind(leftMain.heightProperty());
        DoubleProperty smallLabelsFontSize = new SimpleDoubleProperty();
        smallLabelsFontSize.bind(oldGamesPanelContent.widthProperty().divide(18));
        // profile options
        BindingController.bindChildTextToParentWidth(mainArea,nameLabel,.7);
        BindingController.bindChildTextToParentWidth(mainArea,eloLabel,7);

        BindingController.bindChildWidthToParentHeightWithMaxSize(mainArea, passwordInput,350,.45);
        BindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,nameInput,350,.45);

        BindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,addNewGame,maxNewGameButtonSize,.1);
        addNewGame.prefHeightProperty().bind(addNewGame.widthProperty());
        oldGamesPanel.prefHeightProperty().bind(mainArea.heightProperty());
        BindingController.bindChildWidthToParentWidthWithMaxSize(mainArea,oldGamesPanel,350,.5);
        BindingController.bindChildHeightToParentHeightWithMaxSize(mainArea,themeSelection,50,.1);
        BindingController.bindChildWidthToParentWidthWithMaxSize(mainArea,themeSelection,100,.1);
        oldGamesPanelContent.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        oldGamesLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ",smallLabelsFontSize.toString()));
        reconnectButton.prefWidthProperty().bind(mainArea.widthProperty().divide(8));
        reconnectButton.prefHeightProperty().bind(reconnectButton.prefWidthProperty().multiply(0.75));
    }

    private void setUpMiscelaneus(){
        oldGamesPanelContent.setAlignment(Pos.TOP_CENTER);
        oldGamesPanelContent.setSpacing(3);


    }
    private void hideAllScreens(){
        sandboxScreen.setVisible(false);
        sandboxScreen.setMouseTransparent(true);
        sandboxButton.setStyle("");
        pgnSelectionScreen.setVisible(false);
        pgnSelectionScreen.setMouseTransparent(true);
        pgnButton.setStyle("");
        mainSelectionScreen.setVisible(false);
        mainSelectionScreen.setMouseTransparent(true);
        localButton.setStyle("");
        multiplayerSelectionScreen.setVisible(false);
        multiplayerSelectionScreen.setMouseTransparent(true);
        multiplayerButton.setStyle("");
        userSettingScreen.setVisible(false);
        userSettingScreen.setMouseTransparent(true);
        generalSettingsScreen.setVisible(false);
        generalSettingsScreen.setMouseTransparent(true);

    }
    private void setSelection(StartScreenState state){
        hideAllScreens();
        switch (state){
            case PGN -> {
                pgnButton.setStyle("-fx-border-style: 2px black");
                pgnSelectionScreen.setVisible(true);
                pgnSelectionScreen.setMouseTransparent(false);
            }
            case REGULAR -> {
                localButton.setStyle("-fx-border-style: 2px black");
                mainSelectionScreen.setVisible(true);
                mainSelectionScreen.setMouseTransparent(false);
            }
            case MULTIPLAYER -> {
                multiplayerButton.setStyle("-fx-border-style: 2px black");
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
                sandboxButton.setStyle("-fx-border-style: 2px black");
                sandboxScreen.setVisible(true);
                sandboxScreen.setMouseTransparent(false);
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
        BindingController.bindChildTextToParentWidth(gameContainer,gameName, (double) 1 /4,"Black");

        Label playersName = new Label(newGame.getPlayer1name() + " vs " + newGame.getPlayer2name());
        BindingController.bindChildTextToParentWidth(gameContainer,playersName,(double) 1/12,"Black");



        innerGameInfo.getChildren().addAll(playersName);
        gameInfo.getChildren().addAll(gameName,innerGameInfo);

        Button deleteButton = new Button();
        deleteButton.setOnMouseClicked(e->{
            removeFromOldGames(String.valueOf(newGame.getGameHash()));
        });
        BindingController.bindChildWidthToParentHeightWithMaxSize(gameContainer,deleteButton,30,.3);
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
        BindingController.bindChildWidthToParentHeightWithMaxSize(gameContainer,openGame,30,.3);
        openGame.prefHeightProperty().bind(openGame.widthProperty());
//        openGame.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeButtons.asString()));
        ImageView playerIconView = new ImageView(playerIcon);
        playerIconView.fitHeightProperty().bind(gameContainer.widthProperty().divide(10));
        playerIconView.fitWidthProperty().bind(gameContainer.widthProperty().divide(10));
//        openGame.setGraphic(playerIconView);

        gameContainer.getChildren().add(gameInfo);
        gameContainer.getChildren().add(openGame);
        gameContainer.getChildren().add(deleteButton);

        BindingController.bindChildHeightToParentHeightWithMaxSize(oldGamesPanel,gameContainer,75,.15);
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
