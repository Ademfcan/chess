package chessengine;

import chessserver.Gametype;
import chessserver.INTENT;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StartScreenController implements Initializable {

    @FXML
    GridPane fullscreen;

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


    @FXML
    ChoiceBox<String> themeSelection;



    @FXML
    ComboBox<String> gameTypes;

    @FXML
    Button multiplayerStart;

    @FXML
    Label poolCount;

    @FXML
    Label nameLabel;

    @FXML
    TextField nameInput;

    @FXML
    Label eloLabel;

    @FXML
    TextField eloInput;

    @FXML
    ImageView userSettingToggle;

    @FXML
    Button saveUserOptions;

    List<ChessGame> oldGames;
    private StartScreenState currentState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setSelection(StartScreenState.REGULAR);


        themeSelection.getItems().addAll("Light","Dark");
        themeSelection.getSelectionModel().selectFirst();
        themeSelection.setOnAction(e->{
            boolean isLight = themeSelection.getValue().equals("Light");
            App.updateGlobalTheme(isLight ? ThemesGlobal.Light : ThemesGlobal.Dark);
        });

        userSettingToggle.setOnMouseClicked(e->{
            // toggle between regular and usersettings
            if(currentState.equals(StartScreenState.REGULAR)){
                setSelection(StartScreenState.USERSETTINGS);
            }
            else{
                setSelection(StartScreenState.REGULAR);
            }
        });

        setUpLocalOptions();
        setUpPgnOptions();
        setUpMultiOptions();
        setUpUserOptions();
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
        backgroundAudioButton.setOnMouseClicked(e->{
            boolean isMuted = App.soundPlayer.toggleAudio();
            if(isMuted){
                backgroundAudioButton.setText("✖");
            }
            else{
                backgroundAudioButton.setText("🔉");
            }
        });
    }


    private void setUpUserOptions(){
        nameInput.setText(App.appUser.getName());
        eloInput.setText(Integer.toString(App.appUser.getElo()));
        saveUserOptions.setOnMouseClicked(e->{
            if(nameInput.getText().isEmpty()){
                nameInput.setPromptText("please enter a name");
            }
            if(eloInput.getText().isEmpty()){
                eloInput.setPromptText("please enter a number");
            }
            else{
                try {
                    int elo = Integer.parseInt(eloInput.getText());
                    App.changeClient(nameInput.getText(),elo);
                }
                catch (NumberFormatException exception){
                    eloInput.clear();
                    eloInput.setPromptText("please enter a valid number!");
                }

            }

        });
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
        mainArea.prefWidthProperty().bind(leftMain.widthProperty().subtract(sideButtons.widthProperty()));
        mainArea.prefHeightProperty().bind(leftMain.heightProperty());
        DoubleProperty smallLabelsFontSize = new SimpleDoubleProperty();
        smallLabelsFontSize.bind(oldGamesPanelContent.widthProperty().divide(18));
        // profile options
        BindingController.bindChildTextToParentWidth(mainArea,nameLabel,.7);
        BindingController.bindChildTextToParentWidth(mainArea,eloLabel,7);

        BindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,eloInput,350,.7);
        BindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,nameInput,350,.7);

        BindingController.bindChildWidthToParentHeightWithMaxSize(mainArea,addNewGame,maxNewGameButtonSize,.1);
        addNewGame.prefHeightProperty().bind(addNewGame.widthProperty());
        oldGamesPanel.prefHeightProperty().bind(mainArea.heightProperty());
        BindingController.bindChildWidthToParentWidthWithMaxSize(mainArea,oldGamesPanel,350,.5);
        BindingController.bindChildHeightToParentHeightWithMaxSize(mainArea,themeSelection,50,.1);
        BindingController.bindChildWidthToParentWidthWithMaxSize(mainArea,themeSelection,100,.1);
        oldGamesPanelContent.prefWidthProperty().bind(oldGamesPanel.widthProperty());
        oldGamesLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ",smallLabelsFontSize.toString()));
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

    }
    private void setSelection(StartScreenState state){
        this.currentState = state;
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
                userSettingScreen.setVisible(true);
                userSettingScreen.setMouseTransparent(false);
            }
            case SANDBOX -> {
                sandboxButton.setStyle("-fx-border-style: 2px black");
                sandboxScreen.setVisible(true);
                sandboxScreen.setMouseTransparent(false);
            }
        }

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
        App.removeGameFromData(hashCode);
        oldGamesPanelContent.getChildren().removeIf(e-> e.getUserData().equals(hashCode));
    }

    private List<ChessGame> loadGamesFromSave(){

        return App.readFromAppData();
    }

    private void setupOldGamesBox(List<ChessGame> gamesToLoad){
        oldGamesPanelContent.getChildren().clear();
        for(ChessGame g : gamesToLoad){
            AddNewGameToSaveGui(g);
        }
    }

}
