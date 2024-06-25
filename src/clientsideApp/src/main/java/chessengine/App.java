
package chessengine;

import chessserver.FrontendClient;
import chessserver.GlobalTheme;
import chessserver.UserInfo;
import chessserver.UserPreferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import jakarta.websocket.DeploymentException;
import java.io.*;

public class App extends Application {

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
    public static UserPreferenceManager userPreferenceManager;

    public static WebSocketClient getWebclient() {
        return webclient;
    }


    public static WebSocketClient webclient;

    public static void updateGlobalTheme(GlobalTheme newTheme){
        globalTheme = newTheme;
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(globalTheme.cssLocation);
        mainScene.getStylesheets().add(isStartScreen ? startCss : mainCss);

    }




    private static ChessCentralControl ChessCentralControl;


    public static ChessCentralControl getCentralControl() {
        return ChessCentralControl;
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
        userPreferenceManager.init();
        userManager.init(startScreenController);
        isStartScreen = true;
        primaryStage.setOnCloseRequest(e->{
            mainScreenController.endAsync();

        });

        userPreferenceManager.setDefaultSelections();


        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/appIcon/icon.png"));



    }



    public static void changeUser(UserInfo info){
        userManager.changeAppUser(info);
        try {
            webclient = userManager.getClientFromUser();
        }
        catch (DeploymentException e){
            System.out.println("No connection to server!");
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
            System.out.println("No connection to server!");
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

        updateGlobalTheme(preferences.getGlobalTheme());
        if(ChessCentralControl.isInit()){
            ChessCentralControl centralControl = mainScreenController.getChessCentralControl();
            // nmoves and eval use same depth for now
            centralControl.asyncController.setEvalDepth(preferences.getEvalDepth());
            centralControl.asyncController.setNmovesDepth(preferences.getEvalDepth());
            centralControl.asyncController.setComputerDepth(preferences.getComputerMoveDepth());
            centralControl.chessBoardGUIHandler.changeChessBg(preferences.getChessboardTheme().toString());
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
        updateGlobalTheme(globalTheme);
        if(!soundPlayer.isUserPrefBgPaused()){
            soundPlayer.playSong(false);
        }


    }

    public static void changeToMainScreenWithoutAny(String gameName, boolean isVsComputer,MainScreenState state){
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateGlobalTheme(globalTheme);
        mainScreenController.setUp(isVsComputer,gameName,null,userManager.getUserName(), userManager.getUserElo(), state);
        soundPlayer.pauseSong(false);

    }



    public static void changeToMainScreenWithGame(ChessGame loadedGame,boolean isVsComputer,MainScreenState state){
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateGlobalTheme(globalTheme);
        mainScreenController.setUp(isVsComputer,"",loadedGame,userManager.getUserName(), userManager.getUserElo(),state);
        soundPlayer.pauseSong(false);


    }

    public static void changeToMainScreenMultiplayer(String player1Name, int player1Elo, ChessGame webGame){
        soundPlayer.pauseSong(false);

    }



    // user save related






}
