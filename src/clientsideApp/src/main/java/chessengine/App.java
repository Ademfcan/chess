package chessengine;

import chessengine.Audio.SoundPlayer;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.Computation.MagicBitboardGenerator;
import chessengine.Computation.Stockfish;
import chessengine.Crypto.KeyManager;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Crypto.CryptoUtils;
import chessengine.Enums.MainScreenState;
import chessengine.Graphics.*;
import chessengine.Managers.CampaignMessageManager;
import chessengine.Managers.ClientManager;
import chessengine.Managers.UserPreferenceManager;
import chessengine.Managers.WebSocketClient;
import chessengine.Misc.ChessConstants;
import chessserver.*;
import jakarta.websocket.DeploymentException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.shade.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class App extends Application {

    public final static double referenceDpi = 100;
    private final static String startUrl = "/FxmlFiles/StartScreen.fxml";
    private final static String mainUrl = "/FxmlFiles/MainScreen.fxml";
    private final static String startCss = "/CSSFiles/StartScreenCss.css";
    private final static String mainCss = "/CSSFiles/MainScreenCss.css";
    public static Logger appLogger = LogManager.getLogger("App_Logger");
    public static MainScreenController mainScreenController;
    public static StartScreenController startScreenController;
    public static GlobalMessager messager;
    public static SoundPlayer soundPlayer;
    public static boolean isStartScreen;
    public static GlobalTheme globalTheme;
    public static ClientManager userManager;
    public static BindingController bindingController;
    public static UserPreferenceManager userPreferenceManager;
    public static CampaignMessageManager campaignMessager;
    public static chessengine.CentralControlComponents.ChessCentralControl ChessCentralControl;
    public static double dpi;
    public static double dpiScaleFactor;
    public static Stockfish stockfishForEval;
    public static Stockfish getMoveStockfish;
    public static MagicBitboardGenerator magicBitboardGenerator;
    private static Stage mainStage;
    private static Scene mainScene;
    private static Parent startRoot;
    private static Parent mainRoot;
    private static WebSocketClient webclient;

    public static WebSocketClient getWebclient() {
        return webclient;
    }


    public static void updateTheme(GlobalTheme newTheme) {
        globalTheme = newTheme;
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(globalTheme.cssLocation);
        mainScene.getStylesheets().add(isStartScreen ? startCss : mainCss);

    }

    public static boolean isWebClientConnected() {
        return webclient == null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static void changeUser(UserInfo info) {
        userManager.changeAppUser(info);
        try {
            webclient = userManager.getClientFromUser();
        } catch (DeploymentException e) {
            messager.sendMessageQuick("No connection to server!", true);
        } catch (IOException e) {
            appLogger.error("Error when changing user", e);
        }
    }

    public static boolean attemptReconnection() {
        try {
            messager.sendMessageQuick("Disconnected from server, reconnecting...",true);
            messager.sendMessageQuick("Disconnected from server, reconnecting...",false);
            messager.addLoadingCircle(true);
            messager.addLoadingCircle(false);
            webclient = userManager.getClientFromUser();
            messager.removeLoadingCircles(true);
            messager.removeLoadingCircles(false);
            return true;
        } catch (DeploymentException e) {
            ChessConstants.mainLogger.debug("Connection Failed");
            webclient = null;
        } catch (IOException e) {
            appLogger.error("Error when attemting reconnection", e);
        }
        return false;

    }

    public static void adjustGameToUserPreferences(UserPreferences preferences) {
        if (!preferences.isBackgroundmusic()) {
            App.startScreenController.backgroundAudioButton.setText("✖");
            App.startScreenController.audioMuteBGButton.setText("✖");
            soundPlayer.pauseSong(true);
        } else {
            App.startScreenController.backgroundAudioButton.setText("🔉");
            App.startScreenController.audioMuteBGButton.setText("🔉");
            soundPlayer.playSong(true);
        }
        App.startScreenController.audioMuteEffButton.setText(preferences.isEffectSounds() ? "🔉" : "✖");
        soundPlayer.changeVolumeBackground(preferences.getBackgroundVolume());
        soundPlayer.changeVolumeEffects(preferences.getEffectVolume());
        soundPlayer.setEffectsMuted(!preferences.isEffectSounds());

        updateTheme(preferences.getGlobalTheme());
        if (ChessCentralControl.isInit()) {
            ChessCentralControl centralControl = mainScreenController.getChessCentralControl();
            // nmoves and eval use same depth for now
//            centralControl.asyncController.setEvalDepth(preferences.getEvalDepth());
//            centralControl.asyncController.setNmovesDepth(preferences.getEvalDepth());
            centralControl.asyncController.setComputerDifficulty(preferences.getComputerMoveDiff());
            boolean isWhiteOriented = centralControl.gameHandler.currentGame == null || centralControl.gameHandler.currentGame.isWhiteOriented();
            centralControl.chessBoardGUIHandler.changeChessBg(preferences.getChessboardTheme().toString());
            // todo pieces theme

        } else {
            // handle first init with preferences
            // should never come to this branch
            mainScreenController.initPreferences = preferences;
        }
    }

    public static void changeToStart() {
        isStartScreen = true;
        mainScene.setRoot(startRoot);
        updateTheme(globalTheme);
        if (!soundPlayer.isUserPrefBgPaused()) {
            soundPlayer.playSong(false);
        }


    }

    /**
     * play as white flag specifies wether player wants to play as white
     **/
    public static void changeToMainScreenWithoutAny(String gameName, boolean isVsComputer, boolean isWhiteOriented, MainScreenState state, boolean playAsWhite) {
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupWithoutGame(isVsComputer, isWhiteOriented, gameName, userManager.getUserName(), userManager.getUserElo(), userManager.getUserPfpUrl(), state, playAsWhite);
        soundPlayer.pauseSong(false);

    }

    public static void changeToMainScreenWithGame(chessengine.ChessRepresentations.ChessGame loadedGame, MainScreenState state, boolean isFirstLoad) {
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupWithGame(loadedGame, state, isFirstLoad);


    }

    public static void changeToMainScreenOnline(ChessGame onlinePreinit) {
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.preinitOnlineGame(onlinePreinit);

    }

    public static void changeToMainScreenCampaign(CampaignTier campaignTier, int campaignLevelOfTier, int difficulty) {
        isStartScreen = false;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupCampaign(userManager.getUserName(), userManager.getUserElo(), userManager.getUserPfpUrl(), campaignTier, campaignLevelOfTier, difficulty);
        soundPlayer.pauseSong(false);

    }

    // web client stuff
    public static void sendRequest(INTENT intent, String extraInfo, Consumer<String> requestResponseAction) {
        if (webclient == null) {
            appLogger.debug("Client null trying to create new one");
            if (attemptReconnection()) {
                sendRequest(intent, extraInfo, requestResponseAction);
            } else {
                appLogger.error("Server Not Acessable!!");

            }
        } else {
            webclient.sendRequest(intent, extraInfo, requestResponseAction);

        }

    }

    public static void getUserRequest(String username, String passwordHash,Consumer<String> requestAction) {
        if (webclient == null) {
            appLogger.debug("Client null trying to create new one");
            if (attemptReconnection()) {
                getUserRequest(username, passwordHash,requestAction);
            } else {
                appLogger.error("Server Not Acessable!!");

            }
        } else {
            webclient.getUserRequest(username, passwordHash,requestAction);


        }
    }

    public static void createAndLoginClientRequest(String username, String password, int currentUUID) {
        try {
            String passwordHash = CryptoUtils.sha256AndBase64(password);

            if (userManager.isLoggedIn()) {
                logout();
                System.out.println("----------------------------------------------------------");
            }


            userManager.changeUserName(username, false);
            userManager.changeUUID(currentUUID, false);
            KeyManager.saveNewPassword(passwordHash);

            databaseRequest(INTENT.PUTUSER, userManager.getCurrentUser(), userPreferenceManager.getUserPref(), passwordHash, null);

        } catch (NoSuchAlgorithmException e) {
            appLogger.error("Never should hit this, LOL", e);
        }
    }

    public static void logout() {
        userManager.logout();
        userPreferenceManager.resetToDefault();
        startScreenController.setupOldGamesBox(new ArrayList<>());
        startScreenController.campaignManager.reset();
    }

    public static void partialDatabaseUpdateRequest(UserInfo userInfo) {
        databaseRequest(INTENT.UPDATEUSER, userInfo, userPreferenceManager.getUserPref(), null, null);
    }

    public static void partialDatabaseUpdateRequest(UserPreferences userPreferences) {
        databaseRequest(INTENT.UPDATEUSER, userManager.getCurrentUser(), userPreferences, null, null);
    }

    private static void synchronizeWithServer(){
        if(webclient == null){
            appLogger.debug("No connection to server, cannot synchronize");
            return;
        }
        if(!userManager.isLoggedIn()){
            appLogger.debug("Not logged in, cannot synchronize");
            return;
        }
        String currentPasswordHash = KeyManager.tryLoadCurrentPasswordHash();
        getUserRequest(userManager.getUserName(),currentPasswordHash,(out) ->{
            if(out.isEmpty()){
                // no record of account on server, so make one
                appLogger.warn("No user in the database, storing local copy");
                databaseRequest(INTENT.PUTUSER,userManager.getCurrentUser(),userPreferenceManager.getUserPref(),currentPasswordHash,null);
            }
            else{
                try{
                    DatabaseEntry newEntry = ChessConstants.objectMapper.readValue(out, DatabaseEntry.class);
                    long serverLastTimeStamp = newEntry.getUserInfo().getLastUpdateTimeMS();
                    long localLastTimeStamp = userManager.getLastTimeStampMs();
                    System.out.println("Server timestamp: " + serverLastTimeStamp + " local timestamp: " + localLastTimeStamp);
                    if(serverLastTimeStamp >= localLastTimeStamp){
                        // go with server, so update local
                        Platform.runLater(() ->{
                            appLogger.debug("Updating local with server copy");
                            App.refreshAppWithNewUser(newEntry);
                        });
                    }
                    else{
                        // go with local, so update server
                        databaseRequest(INTENT.UPDATEUSER,userManager.getCurrentUser(),userPreferenceManager.getUserPref(),currentPasswordHash,null);
                    }


                } catch (JsonProcessingException jsonProcessingException) {
                    appLogger.error("Json processing exeption when parsing validate client request output!",jsonProcessingException);
                }

                // now handle any incoming friend requests

                sendRequest(INTENT.READINCOMINGFRIENDREQUESTS,userManager.getCurrentUser() + "," + currentPasswordHash,(friendRequests) ->{
                    if(!friendRequests.isEmpty()){
                        userManager.addMoreFriendRequests(friendRequests,true);
                    }
                });
            }
        });
    }


    public static void databaseRequest(INTENT intent, UserInfo userInfo, UserPreferences userPreferences, String passwordHash, Consumer<String> responseAction) {
        if (webclient == null) {
            appLogger.debug("Client null trying to create new one");
            if (attemptReconnection()) {
                databaseRequest(intent, userInfo, userPreferences, passwordHash, responseAction);
            } else {
                appLogger.error("Server Not Acessable!!");
            }
        } else {
            String realPasswordHash = passwordHash == null ? KeyManager.tryLoadCurrentPasswordHash() : passwordHash;
            if (realPasswordHash == null) {
                appLogger.error("Cannot make a database request, no password(hash)");
            }
            App.messager.sendMessageQuick("Updating database", true);
            webclient.databaseRequest(intent, userInfo.getUserName(), realPasswordHash, userInfo.getUuid(), new DatabaseEntry(userInfo, userPreferences), responseAction);

        }

    }

    public static void refreshAppWithNewUser(DatabaseEntry databaseEntry) {
        App.userPreferenceManager.reloadWithUser(databaseEntry.getPreferences(), false);
        App.userManager.reloadNewAppUser(databaseEntry.getUserInfo(), false);
    }

    @Override
    public void init() throws Exception {
        super.init();
        // load app

        notifyPreloader(new Preloader.ProgressNotification(0.1));
        notifyPreloader(new AppStateChangeNotification("Loading stockfish..."));
        stockfishForEval = new Stockfish();
        getMoveStockfish = new Stockfish();

        notifyPreloader(new Preloader.ProgressNotification(0.2));
        notifyPreloader(new AppStateChangeNotification("Starting stockfish..."));
        if (stockfishForEval.startEngine()) {
            appLogger.debug("Started stockfish for eval succesfully");
        } else {
            appLogger.error("Stockfish for eval start failed");
        }

        if (getMoveStockfish.startEngine()) {
            appLogger.debug("Started stockfish for nmoves succesfully");
        } else {
            appLogger.error("Stockfish for nmoves start failed");
        }


        notifyPreloader(new Preloader.ProgressNotification(0.3));
        notifyPreloader(new AppStateChangeNotification("Loading magic bitboards..."));

        magicBitboardGenerator = new MagicBitboardGenerator();


        dpi = Screen.getPrimary().getDpi();
        dpiScaleFactor = dpi / referenceDpi;

        notifyPreloader(new Preloader.ProgressNotification(0.35));
        notifyPreloader(new AppStateChangeNotification("Loading user..."));

        userManager = new ClientManager();

        notifyPreloader(new Preloader.ProgressNotification(0.4));
        notifyPreloader(new AppStateChangeNotification("Connecting to server..."));

        try {
            webclient = userManager.getClientFromUser();
        } catch (DeploymentException e) {
            appLogger.debug("No connection to server!");
            webclient = null;
        } catch (IOException e) {
            appLogger.error("Io error on webclient creation", e);
        }
        notifyPreloader(new Preloader.ProgressNotification(0.5));





        notifyPreloader(new Preloader.ProgressNotification(0.5));
        notifyPreloader(new AppStateChangeNotification("Loading managers..."));
        soundPlayer = new SoundPlayer();

        ChessCentralControl = new ChessCentralControl();
        messager = new GlobalMessager();
        userPreferenceManager = new UserPreferenceManager();
        campaignMessager = new CampaignMessageManager(ChessCentralControl);

        if (webclient != null) {
            synchronizeWithServer();
            notifyPreloader(new AppStateChangeNotification("Connected to server"));
        } else {
            notifyPreloader(new AppStateChangeNotification("Server connection failed"));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        notifyPreloader(new Preloader.ProgressNotification(0.6));
        notifyPreloader(new AppStateChangeNotification("Loading graphics..."));
        mainStage = primaryStage;

        try {

            FXMLLoader fxmlLoader2 = new FXMLLoader(App.class.getResource(startUrl));
            fxmlLoader2.setControllerFactory(c -> setStartScreenController()); // Set controller factory
            startRoot = fxmlLoader2.load();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(mainUrl));
            fxmlLoader.setControllerFactory(c -> setMainScreenController()); // Set controller factory
            mainRoot = fxmlLoader.load();


        } catch (IOException e) {
            appLogger.error("Error on loading fxml", e);
        }
        Group startMessageBoard = new Group();
        Group mainMessageBoard = new Group();

        notifyPreloader(new Preloader.ProgressNotification(0.7));
        notifyPreloader(new AppStateChangeNotification("Setting up graphics..."));

        messager.Init(startMessageBoard, mainMessageBoard, startScreenController.startRef, mainScreenController.mainRef);
        mainScene = new Scene(startRoot);
        userManager.init(startScreenController);
        isStartScreen = true;
        primaryStage.setOnCloseRequest(e -> {
            mainScreenController.endAsync();
        });

        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isStartScreen) {
                startRoot.requestLayout();
            } else {
                mainRoot.requestLayout();
            }
        });

        bindingController = new BindingController(mainScreenController.content, startScreenController.content);
        userPreferenceManager.init();
        startScreenController.setup();
        mainScreenController.oneTimeSetup();
        notifyPreloader(new Preloader.ProgressNotification(0.8));

        userPreferenceManager.setDefaultSelections();

        ((StackPane) startRoot).getChildren().add(startMessageBoard);
        ((StackPane) mainRoot).getChildren().add(mainMessageBoard);


        primaryStage.setScene(mainScene);
        primaryStage.setHeight(540);
        primaryStage.setWidth(960);
        notifyPreloader(new Preloader.ProgressNotification(0.9));
        notifyPreloader(new AppStateChangeNotification("Ready"));
        primaryStage.show();
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image("/appIcon/icon.png"));
        primaryStage.requestFocus();


    }

    @Override
    public void stop() {
        stockfishForEval.stopEngine();
        mainScreenController.endAsync();
        if (ChessCentralControl.gameHandler.currentGame != null && ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && !mainScreenController.currentState.equals(MainScreenState.VIEWER) && !mainScreenController.currentState.equals(MainScreenState.SANDBOX) && !mainScreenController.currentState.equals(MainScreenState.SIMULATION)) {
            if (ChessCentralControl.gameHandler.currentGame.maxIndex > -1) {
                // if the game is not empty add it
//                PersistentSaveManager.appendGameToAppData(ChessCentralControl.gameHandler.currentGame);
                App.userManager.saveUserGame(ChessCentralControl.gameHandler.currentGame);
            }
        }
    }

    private MainScreenController setMainScreenController() {
        mainScreenController = new MainScreenController();
        return mainScreenController;
    }

    private StartScreenController setStartScreenController() {
        startScreenController = new StartScreenController();
        return startScreenController;
    }


}
