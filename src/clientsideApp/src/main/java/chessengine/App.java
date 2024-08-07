
package chessengine;

import chessserver.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import jakarta.websocket.DeploymentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class App extends Application {

    public static Logger appLogger = LogManager.getLogger("App_Logger");
    public static mainScreenController mainScreenController;
    public static StartScreenController startScreenController;

    public static GlobalMessager messager;

    public static SoundPlayer soundPlayer;
    private final static String startUrl = "/FxmlFiles/StartScreen.fxml";
    private final static String mainUrl = "/FxmlFiles/MainScreen.fxml";

    private final static String startCss = "/CSSFiles/StartScreenCss.css";
    private final static String mainCss = "/CSSFiles/MainScreenCss.css";


    private static Stage mainStage;

    private static Scene mainScene;
    private static Parent startRoot;
    private static Parent mainRoot;

    public static boolean isStartScreen;

    public static GlobalTheme globalTheme;

    public static UserManager userManager;
    public static BindingController bindingController;
    public static UserPreferenceManager userPreferenceManager;
    public static CampaignMessager campaignMessager;

    public static WebSocketClient getWebclient() {
        return webclient;
    }


    private static WebSocketClient webclient;

    public static void updateTheme(GlobalTheme newTheme){
        globalTheme = newTheme;
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(globalTheme.cssLocation);
        mainScene.getStylesheets().add(isStartScreen ? startCss : mainCss);

    }




    public static ChessCentralControl ChessCentralControl;

    public static boolean isWebClientNull() {
        return webclient == null;
    }




    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        userManager = new UserManager();

        try {
            webclient = userManager.getClientFromUser();
        }
        catch (DeploymentException e){
            System.out.println("No connection to server!");
            webclient = null;
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        soundPlayer = new SoundPlayer();

        ChessCentralControl = new ChessCentralControl();
        mainStage = primaryStage;
        messager = new GlobalMessager();
        userPreferenceManager = new UserPreferenceManager();
        campaignMessager = new CampaignMessager(ChessCentralControl);

        try {

            FXMLLoader fxmlLoader2 = new FXMLLoader(App.class.getResource(startUrl));
            fxmlLoader2.setControllerFactory(c -> setStartScreenController()); // Set controller factory
            startRoot = fxmlLoader2.load();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(mainUrl));
            fxmlLoader.setControllerFactory(c -> setMainScreenController()); // Set controller factory
            mainRoot = fxmlLoader.load();


        } catch (IOException e) {
            e.printStackTrace();
        }
        messager.Init(startScreenController.startMessageBoard,mainScreenController.mainMessageBoard);
        mainScene = new Scene(startRoot);
        userManager.init(startScreenController);
        isStartScreen = true;
        primaryStage.setOnCloseRequest(e->{
            mainScreenController.endAsync();
        });

        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if(isStartScreen){
                startRoot.requestLayout();
            }
            else{
                mainRoot.requestLayout();
            }
        });

        bindingController = new BindingController(mainScreenController.content,startScreenController.content);
        userPreferenceManager.init();
        startScreenController.setup();
        mainScreenController.oneTimeSetup();

        userPreferenceManager.setDefaultSelections();


        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image("/appIcon/icon.png"));



    }



    public static void changeUser(UserInfo info){
        userManager.changeAppUser(info);
        try {
            webclient = userManager.getClientFromUser();
        }
        catch (DeploymentException e){
            messager.sendMessageQuick("No connection to server!",true);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static boolean attemptReconnection(){
        try {
            webclient = userManager.getClientFromUser();
            return true;
        }
        catch (DeploymentException e){
            ChessConstants.mainLogger.debug("Connection Failed");
            webclient = null;
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        return false;

    }

    public static void adjustGameToUserPreferences(UserPreferences preferences){
        if(!preferences.isBackgroundmusic()){
            App.startScreenController.backgroundAudioButton.setText("✖");
            App.startScreenController.audioMuteBGButton.setText("✖");
            soundPlayer.pauseSong(true);
        }
        else{
            App.startScreenController.backgroundAudioButton.setText("🔉");
            App.startScreenController.audioMuteBGButton.setText("🔉");
            soundPlayer.playSong(true);
        }
        App.startScreenController.audioMuteEffButton.setText(preferences.isEffectSounds() ? "🔉" : "✖");
        soundPlayer.changeVolumeBackground(preferences.getBackgroundVolume());
        soundPlayer.changeVolumeEffects(preferences.getEffectVolume());
        soundPlayer.setEffectsMuted(!preferences.isEffectSounds());

        updateTheme(preferences.getGlobalTheme());
        if(ChessCentralControl.isInit()){
            ChessCentralControl centralControl = mainScreenController.getChessCentralControl();
            // nmoves and eval use same depth for now
            centralControl.asyncController.setEvalDepth(preferences.getEvalDepth());
            centralControl.asyncController.setNmovesDepth(preferences.getEvalDepth());
            centralControl.asyncController.setComputerDepth(preferences.getComputerMoveDepth());
            boolean isWhiteOriented = centralControl.gameHandler.currentGame == null || centralControl.gameHandler.currentGame.isWhiteOriented();
            centralControl.chessBoardGUIHandler.changeChessBg(preferences.getChessboardTheme().toString(),isWhiteOriented);
            // todo pieces theme

        }
        else{
            // handle first init with preferences
            // should never come to this branch
            mainScreenController.initPreferences = preferences;
        }
    }





    @Override
    public void stop(){
        mainScreenController.endAsync();
        if(ChessCentralControl.gameHandler.currentGame != null && ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && !mainScreenController.currentState.equals(MainScreenState.VIEWER) && !mainScreenController.currentState.equals(MainScreenState.SANDBOX)){
            if(ChessCentralControl.gameHandler.currentGame.maxIndex > -1){
                // if the game is not empty add it
                PersistentSaveManager.appendGameToAppData(ChessCentralControl.gameHandler.currentGame);
            }
        }
    }


    private mainScreenController setMainScreenController(){
        mainScreenController = new mainScreenController();
        return mainScreenController;
    }

    private StartScreenController setStartScreenController(){
        startScreenController = new StartScreenController();
        return startScreenController;
    }

    public static void changeToStart(){
        isStartScreen = true;
        mainScene.setRoot(startRoot);
        updateTheme(globalTheme);
        if(!soundPlayer.isUserPrefBgPaused()){
            soundPlayer.playSong(false);
        }


    }

    public static void changeToMainScreenWithoutAny(String gameName, boolean isVsComputer,boolean isWhiteOriented,MainScreenState state){
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupWithoutGame(isVsComputer,isWhiteOriented,gameName,userManager.getUserName(), userManager.getUserElo(),userManager.getUserPfpUrl(), state);
        soundPlayer.pauseSong(false);

    }



    public static void changeToMainScreenWithGame(ChessGame loadedGame,MainScreenState state,boolean isFirstLoad){
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupWithGame(loadedGame,state,isFirstLoad);


    }

    public static void changeToMainScreenOnline(ChessGame onlinePreinit){
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.preinitOnlineGame(onlinePreinit);

    }

    public static void changeToMainScreenCampaign(CampaignTier campaignTier,int campaignLevelOfTier,int difficulty){
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupCampaign(userManager.getUserName(), userManager.getUserElo(),userManager.getUserPfpUrl(),campaignTier,campaignLevelOfTier,difficulty);
        soundPlayer.pauseSong(false);

    }





    // web client stuff
    public static void sendRequest(INTENT intent, String extraInfo) {
        if(webclient == null){
            appLogger.debug("Client null trying to create new one");
            if(attemptReconnection()){
                webclient.sendRequest(intent,extraInfo);
            }
            else{
                appLogger.error("Server Not Acessable!!");

            }
        }
        else{
            webclient.sendRequest(intent,extraInfo);

        }

    }

    public static void validateClientRequest(String text, String text1) {
        if(webclient == null){
            appLogger.debug("Client null trying to create new one");
            if(attemptReconnection()){
                webclient.validateClientRequest(text,text1);
            }
            else{
                appLogger.error("Server Not Acessable!!");

            }
        }
        else{
            webclient.validateClientRequest(text,text1);

        }
    }






}
