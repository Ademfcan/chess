package chessengine;

import chessserver.*;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.util.Duration;

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
    Button vsPlayer;

    @FXML
    Button vsComputer;

    // main area screens
    @FXML
    StackPane campaignScreen;
    @FXML
    ScrollPane campaignScroller;
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

        setUpCampaignScreen();


        oldGames = loadGamesFromSave();
        setupOldGamesBox(oldGames);




    }

    public void setProfileInfo(ProfilePicture picture, String name, int elo){
        profileButton.setImage(new Image(picture.urlString));
        System.out.println("Changing image: " + picture.urlString);
        nameProfileLabel.setText(name);
        eloProfileLabel.setText(Integer.toString(elo));
    }

    private final int NLevels = 15;
    private final ImageView[][] pawnStars = new ImageView[NLevels][3];
    private void setUpCampaignScreen(){
        campaignScreen.prefWidthProperty().bind(mainArea.widthProperty());
        campaignScreen.prefHeightProperty().bind(mainArea.heightProperty());
        campaignScroller.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        campaignScroller.prefHeightProperty().bind(campaignScreen.heightProperty());
        levelContainer.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        levelContainerElements.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        levelContainerPath.prefWidthProperty().bind(campaignScreen.prefWidthProperty());
        levelContainerPath.setMouseTransparent(true);
        levelContainerElements.toFront();
        drawLevels(levelContainer.widthProperty());
    }

    private void scrollToBottomAnimation(){
        Animation scrollAnimation = new Timeline(new KeyFrame(Duration.seconds(2),new KeyValue(campaignScroller.vvalueProperty(),1)));
        scrollAnimation.play();
    }

    private final float widthToJump = .63f;
    private final float widthToXoffset = .05f;
    private final float widthToOffsetY = .02f;
    private final float widthToLevelRadius = .12f;
    private final float widthToLevelWidth = .45f;
    private final float widthToLevelHeight = .40f;
    private final float widthToInfoSpacing = .1f;
    private final float infoWidthToLvlBtnWidth = .8f;
    private final float infoHeightToLvlBtnHeight = .4f;
    private final float infoWidthToPawnWidth = .2f;
    private final int[] challengepfpIndexes = new int[]{3,2,5,7,8,6,1,10,9,11,12,14,13,15,18};
    private void drawLevels(ReadOnlyDoubleProperty widthProperty){
        // each level has a circle you click on to play the level
        // additionaly there is a vbox that contains the level information/play button
        DoubleBinding circleRadius = widthProperty.multiply(widthToLevelRadius);
        DoubleBinding levelInfoHeight = widthProperty.multiply(widthToLevelHeight);
        DoubleBinding levelInfoWidth = widthProperty.multiply(widthToLevelWidth);
        DoubleBinding levelButtonWidth = levelInfoWidth.multiply(infoWidthToLvlBtnWidth);
        DoubleBinding levelButtonHeight = levelInfoHeight.multiply(infoHeightToLvlBtnHeight);

        DoubleBinding yJumpPerLevel = widthProperty.multiply(widthToJump);
        DoubleBinding xOffset = widthProperty.multiply(widthToXoffset);
        DoubleBinding xShiftPerLevelB = widthProperty.subtract(xOffset.add(circleRadius).multiply(2));
        DoubleBinding xSpacing = widthProperty.multiply(widthToInfoSpacing);
        boolean isShiftToRight = true;
        // since we are going down -> up, and the y coordinate system grows downwards
        // we need to start at the very end then decrease our y value
        DoubleBinding offsetYBottom = widthProperty.multiply(widthToOffsetY);
        DoubleBinding largestY = yJumpPerLevel.multiply(NLevels-1).add(offsetYBottom.multiply(2).add(Bindings.max(circleRadius.multiply(2),levelInfoHeight.multiply(2))));

        DoubleBinding startYB = largestY.subtract(offsetYBottom).subtract(Bindings.max(circleRadius.multiply(2),levelInfoHeight));
        DoubleBinding startXB = xOffset.add(circleRadius);
        // set size of container
        levelContainer.prefHeightProperty().bind(largestY);
        levelContainerElements.prefHeightProperty().bind(largestY);
        levelContainerPath.prefHeightProperty().bind(largestY);

        // for info panel content
        DoubleBinding pawnWidth = levelInfoWidth.multiply(infoWidthToPawnWidth);

        // for creating the path
        DoubleBinding lastXB = null;
        DoubleBinding lastYB = null;
        for(int i = 0;i<NLevels;i++){
            // play level circle
            Circle newLevelToggle = new Circle();
            int challengeIndex = challengepfpIndexes[i];
            Image buttonProfile = new Image(ProfilePicture.values()[challengeIndex].urlString);
            newLevelToggle.setUserData(Integer.toString(challengeIndex));
            newLevelToggle.setFill(new ImagePattern(buttonProfile));
            // level information panel
            VBox newLevelPanel = new VBox();
            newLevelPanel.setStyle("-fx-background-color: white");
            newLevelPanel.setVisible(false);
            newLevelPanel.setMouseTransparent(true);
            newLevelPanel.setAlignment(Pos.CENTER);
            newLevelPanel.setSpacing(10);



            newLevelPanel.prefWidthProperty().bind(levelInfoWidth);
            newLevelPanel.prefHeightProperty().bind(levelInfoHeight);

            newLevelToggle.radiusProperty().bind(circleRadius);

            newLevelToggle.setOnMouseClicked(e->{
                //toggleCircleColor(newLevelToggle);
//                cycleProfilePicture(newLevelToggle);
                System.out.println("Clicking");
                newLevelPanel.setVisible(!newLevelPanel.isVisible());
                newLevelPanel.setMouseTransparent(!newLevelPanel.isMouseTransparent());
            });
            // positioning stuff
            DoubleBinding startXL;
            if(isShiftToRight){
                startXL = startXB.add(circleRadius).add(xSpacing);
            }
            else{
                startXL = startXB.subtract(circleRadius).subtract(xSpacing).subtract(levelInfoWidth);
            }
            newLevelPanel.layoutXProperty().bind(startXL);
            DoubleBinding startYL = startYB.subtract(levelInfoHeight.divide(2));
            newLevelPanel.layoutYProperty().bind(startYL);

            newLevelToggle.layoutXProperty().bind(startXB);
            newLevelToggle.layoutYProperty().bind(startYB);
            // drawing path curve
            if(lastYB != null){
                QuadCurve path = new QuadCurve();
//                path.toBack();
                path.startXProperty().bind(lastXB);
                path.startYProperty().bind(lastYB);
                path.controlXProperty().bind(lastXB.add(startXB).divide(2.1));
                path.controlYProperty().bind(lastYB.add(startYB).divide(1.9));
                path.endXProperty().bind(startXB);
                path.endYProperty().bind(startYB);
                path.setFill(null);
                path.setStroke(Paint.valueOf("Black"));
                levelContainerPath.getChildren().add(path);
            }

            // setting oldCoordinates
            lastXB = startXB;
            lastYB = startYB;

            // changing to new coordinates
            if(isShiftToRight){
                startXB = startXB.add(xShiftPerLevelB);
            }
            else{
                startXB = startXB.subtract(xShiftPerLevelB);

            }
            startYB = startYB.subtract(yJumpPerLevel);
            isShiftToRight = !isShiftToRight;
            int level = i;

            // children of infoPanel
            Label title = new Label("Opponent");
            BindingController.bindChildTextToParentWidth(mainArea,title,.4);
            // will contain the pawns showing your score
            // like 3 stars in a game
            HBox pawnContainer = new HBox(5);
            pawnContainer.setAlignment(Pos.CENTER);
            for(int j = 0;j<3;j++){
                ImageView pawn = new ImageView();
                pawnStars[i][j] = pawn;
                pawn.setImage(new Image("/ChessAssets/StartScreenPawn/pawnUnfilled.png"));
                pawn.fitWidthProperty().bind(pawnWidth);
                pawn.setPreserveRatio(true);
                int j1 = j;
                pawn.setOnMouseClicked(e->{
                    System.out.println("pawn#" + j1);
                });
                pawnContainer.getChildren().add(pawn);
            }
            Button enterLevelButton = new Button("Play");
            enterLevelButton.prefWidthProperty().bind(levelButtonWidth);
            enterLevelButton.prefHeightProperty().bind(levelButtonHeight);
            enterLevelButton.setOnMouseClicked(e->{
                System.out.println("entering level " + level);
            });
            newLevelPanel.getChildren().add(title);
            newLevelPanel.getChildren().add(pawnContainer);
            newLevelPanel.getChildren().add(enterLevelButton);
            levelContainerElements.getChildren().add(newLevelToggle);
            levelContainerElements.getChildren().add(newLevelPanel);
        }

    }
    private final float infoWidthToStarSideLen = .25f;


    private void cycleProfilePicture(Circle clicked){
        if(clicked.getUserData() == null){
            clicked.setUserData("1");
        }
        int index  = Integer.parseInt(clicked.getUserData().toString());
        System.out.println(ProfilePicture.values()[index].urlString);
        Image newImage = new Image(ProfilePicture.values()[index].urlString);
        index++;
        if(index >= ProfilePicture.values().length){
            index = 0;
        }
        clicked.setFill(new ImagePattern(newImage));
        clicked.setUserData(Integer.toString(index));


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
            scrollToBottomAnimation();
        });
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
        UserPreferenceManager.setupUserSettingsScreen(themeSelection,bgColorSelector,pieceSelector,audioMuteBGButton,audioSliderBG,audioMuteEffButton,audioSliderEff,evalOptions,computerOptions);









        // container bindings
        generalSettingsScrollpane.prefWidthProperty().bind(mainArea.widthProperty());
        generalSettingsScrollpane.prefHeightProperty().bind(mainArea.heightProperty());

        generalSettingsVbox.prefWidthProperty().bind(generalSettingsScrollpane.widthProperty());



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
        content.prefWidthProperty().bind(fullscreen.widthProperty());
        content.prefHeightProperty().bind(fullscreen.heightProperty());

        mainArea.prefWidthProperty().bind(content.widthProperty().multiply(.8));
        mainArea.prefHeightProperty().bind(content.heightProperty().multiply(.9));
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
    private void hideAllScreensnButtons(){
        campaignButton.setStyle("");
        campaignScreen.setVisible(false);
        campaignScreen.setMouseTransparent(true);
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
        hideAllScreensnButtons();
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
            case CAMPAIGN -> {
                campaignButton.setStyle("-fx-border-style: 2px black");
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
