package chessengine;

import chessengine.Audio.SoundPlayer;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.Computation.Stockfish;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Crypto.TokenStore;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.Window;
import chessengine.Graphics.*;
import chessengine.MainP.MainScreenController;
import chessengine.Managers.CampaignMessageManager;
import chessengine.Managers.UserManager;
import chessengine.Net.AuthenticatedMessageSender;
import chessengine.Net.ClientMessageHandlers.*;
import chessengine.Net.JWTManager;
import chessengine.Net.WebSocketClient;
import chessengine.Puzzle.PuzzleManager;
import chessengine.Settings.SettingsManager;
import chessengine.Start.StartScreenController;
import chessengine.Triggers.*;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.Communication.User;
import chessserver.Enums.CampaignTier;
import chessserver.Enums.GlobalTheme;
import chessserver.Functions.MagicBitboardGenerator;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageTypes.DatabaseMessageTypes;
import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
import chessserver.User.UserWGames;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class App extends Application {
    public static final Logger appLogger = LogManager.getLogger("App_Logger");
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    // DPI Scaling
    public static final double referenceDpi = 100;
    public static double screenDpi;
    public static double dpiScaleFactor;

    // FXML and CSS paths
    private static final String startUrl = "/FxmlFiles/StartScreen.fxml";
    private static final String mainUrl = "/FxmlFiles/MainScreen.fxml";
    private static final String startCss = "/CSSFiles/StartScreenCss.css";
    private static final String mainCss = "/CSSFiles/MainScreenCss.css";

    // Static system components
    public static final ChessCentralControl chessCentralControl;
    public static final Stockfish stockfishForEval;
    public static final Stockfish getMoveStockfish;
    public static final MagicBitboardGenerator magicBitboardGenerator;
    public static final SoundPlayer soundPlayer;
    public static final PuzzleManager puzzleManager;
    public static final UserManager userManager;
    public static final CampaignMessageManager campaignMessager;
    public static final GlobalMessager messager;

    // Network and messaging
    public static final ClientChessGameMessageHandler clientChessGameMessageHandler;
    public static final ClientDatabaseMessageHandler clientDatabaseMessageHandler;
    public static final ClientUserMessageHandler clientUserMessageHandler;
    public static final ClientMetricMessageHandler clientMetricMessageHandler;
    public static final JWTManager JWTManager;
    public static final AuthenticatedMessageSender authenticatedMessageSender;

    // Graphics state
    public static boolean isStartScreen = true;
    public static Window currentWindow = Window.Start;
    public static GlobalTheme globalTheme = GlobalTheme.Dark;
    public static MainScreenController mainScreenController;
    public static StartScreenController startScreenController;
    public static Scene mainScene;
    private static Parent startRoot;
    private static Parent mainRoot;

    static {
        userManager = new UserManager();

        TriggerManager.registerAccumulatingTrigger(UserConfigurable.class);
        TriggerManager.registerAccumulatingTrigger(Resettable.class);
        TriggerManager.registerAccumulatingTrigger(Onlineable.class);
        TriggerManager.registerAccumulatingTrigger(Loginable.class);
        TriggerManager.registerAccumulatingTrigger(Closable.class);
        TriggerManager.registerAccumulatingTrigger(OnFriendUpdate.class);

        soundPlayer = new SoundPlayer();
        chessCentralControl = new ChessCentralControl();
        magicBitboardGenerator = new MagicBitboardGenerator();
        stockfishForEval = new Stockfish();
        getMoveStockfish = new Stockfish();

        logStockfishStart(stockfishForEval, "eval");
        logStockfishStart(getMoveStockfish, "nmoves");

        puzzleManager = new PuzzleManager();
        campaignMessager = new CampaignMessageManager(chessCentralControl);
        messager = new GlobalMessager();
    }

    static {
        clientChessGameMessageHandler = new ClientChessGameMessageHandler(WebSocketClient.getWebSocketConnection());
        clientDatabaseMessageHandler = new ClientDatabaseMessageHandler(WebSocketClient.getWebSocketConnection());
        clientUserMessageHandler = new ClientUserMessageHandler(WebSocketClient.getWebSocketConnection());
        clientMetricMessageHandler = new ClientMetricMessageHandler(WebSocketClient.getWebSocketConnection());

        JWTManager = new JWTManager(clientUserMessageHandler, App::logout);
        authenticatedMessageSender = new AuthenticatedMessageSender(clientUserMessageHandler, JWTManager);
    }

    private static void logStockfishStart(Stockfish engine, String name) {
        if (engine.startEngine()) {
            appLogger.debug("Started stockfish for {} successfully", name);
        } else {
            appLogger.error("Stockfish for {} start failed", name);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void notifyInitProgress(double progress, String message) {
        notifyPreloader(new ProgressNotification(progress));
        notifyPreloader(new AppStateChangeNotification(message + "..."));
    }

    @Override
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(App::close));

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        screenDpi = Screen.getPrimary().getDpi();
        dpiScaleFactor = screenDpi / referenceDpi;

        notifyInitProgress(0.1, "Loading Application layout");

        try {
            FXMLLoader startLoader = new FXMLLoader(App.class.getResource(startUrl));
            startLoader.setControllerFactory(c -> startScreenController = new StartScreenController());
            startRoot = startLoader.load();

            FXMLLoader mainLoader = new FXMLLoader(App.class.getResource(mainUrl));
            mainLoader.setControllerFactory(c -> mainScreenController = new MainScreenController());
            mainRoot = mainLoader.load();
        } catch (IOException e) {
            appLogger.error("Error on loading FXML", e);
            throw new RuntimeException(e);
        }

        mainScene = new Scene(startRoot);

        notifyInitProgress(0.8, "Creating core graphics");
        messager.Init(startScreenController.getMessageGroup(), mainScreenController.getMessageGroup(),
                startScreenController.getMessageBoard(), mainScreenController.getMessageBoard());

        notifyInitProgress(0.9, "Setting final touches");
        setupPrimaryStage(primaryStage);

        notifyInitProgress(1, "Finished loading application");
        FXInitQueue.flush();

        if (userManager.isLoggedIn()) setLoggedIn();
        else setLoggedOut();

        WebSocketClient.tryConnectServer();

        triggerUpdateUser();
    }

    private void setupPrimaryStage(Stage primaryStage) {
        primaryStage.setScene(mainScene);
        primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight() / 2);
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
        primaryStage.show();
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image("/appIcon/icon.png"));
        primaryStage.requestFocus();
    }

    // Screen transitions

    public static void changeToStart() {
        isStartScreen = true;
        currentWindow = Window.Start;
        mainScene.setRoot(startRoot);
        changeAppTheme(globalTheme);
        if (!soundPlayer.isUserPrefBgPaused()) soundPlayer.playSong(false);
    }
    private static void changeToMainScreen() {
        isStartScreen = false;
        currentWindow = Window.Main;
        mainScene.setRoot(mainRoot);
        changeAppTheme(globalTheme);
        soundPlayer.pauseSong(false);
    }

    public static void changeToMainScreenWithoutAny(String gameName, boolean isVsComputer, boolean isPlayer1White, MainScreenState state, boolean playAsWhite) {
        changeToMainScreen();
        mainScreenController.setupWithoutGame(isVsComputer, isPlayer1White, gameName, userManager.userInfoManager.getCurrentPlayerInfo(), state, playAsWhite);
    }

    public static void changeToMainScreenWithGame(ChessGame loadedGame, boolean isVsComputer, MainScreenState state, boolean isFirstLoad) {
        changeToMainScreen();
        mainScreenController.setupWithGame(loadedGame, isVsComputer, state, isFirstLoad);
    }

    public static void changeToMainScreenPuzzle() {
        changeToMainScreen();
        mainScreenController.setupPuzzle();
    }

    public static void changeToMainScreenOnline(ChessGame onlinePreinit, String gameType) {
        changeToMainScreen();
        mainScreenController.preinitOnlineGame(gameType, onlinePreinit);
    }

    public static void changeToMainScreenCampaign(CampaignTier tier, int level, int difficulty) {
        changeToMainScreen();
        mainScreenController.setupCampaign(userManager.userInfoManager.getCurrentPlayerInfo(), tier, level, difficulty);
    }

    // User & App Updates

    public static void changeAppTheme(GlobalTheme newTheme) {
        globalTheme = newTheme;
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(globalTheme.cssLocation);
        mainScene.getStylesheets().add(isStartScreen ? startCss : mainCss);
    }


    public static void saveGame(ChessGame game, boolean isOnline) {
        if(isOnline){
            saveOnlineGame(game);
        }
        else{
            saveLocalGame(game);
        }
    }

    private static void saveOnlineGame(ChessGame game) {
        PersistentSaveManager.gameTracker.getTracked().add(GameInfo.fromChessGame(game));
    }


    private static void saveLocalGame(ChessGame game) {
        appLogger.debug("Saving local game");
        GameInfo gameInfo = GameInfo.fromChessGame(game);

        if(userManager.isLoggedIn()) {
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(
                    new MessageConfig(
                            new Message(DatabaseMessageTypes.ClientRequest.SaveChessGame,
                                    new DatabaseMessagePayloadTypes.SaveGamePayload(gameInfo, true))
                            ).onStatusResponse((status) -> {
                                if(status.response().isErrorStatus()){
                                    PersistentSaveManager.unsavedGameTracker.getTracked().add(gameInfo);
                                }
                                else{
                                    PersistentSaveManager.gameTracker.getTracked().add(gameInfo);
                                }
                                Platform.runLater(App::triggerUpdateUser);

                            }
                    ));
        }
        else{
            PersistentSaveManager.unsavedGameTracker.getTracked().add(gameInfo);
            triggerUpdateUser();
        }

    }

    public static void reconnectClient() {
        if(WebSocketClient.tryConnectServer()){
            messager.sendMessage("Connected to server!");
        }
        else{
            messager.sendMessage("Server reconnection failed!");
        }
    }

    public static boolean isOnline() {
        return WebSocketClient.isConnected();
    }

    public static void resetApp() {
        appLogger.info("Resetting app");
        for (Resettable r : TriggerManager.getTriggerables(Resettable.class)) {
            r.resetState();
        }
    }

    public static void logout(){
        setLoggedOut();
        clearPersistentData(true);
    }

    public static void setLoggedOut() {
        appLogger.info("Logging out to app");
        for (Loginable l : TriggerManager.getTriggerables(Loginable.class)) l.onLogout();
    }

    public static void clearPersistentData(boolean trySaveBefore){
        System.out.println("Forcing updates....");

        if(trySaveBefore){
            PersistentSaveManager.userInfoTracker.forceUpdate();
            PersistentSaveManager.userPreferenceTracker.forceUpdate();
            PersistentSaveManager.gameTracker.forceUpdate();
            PersistentSaveManager.unsavedGameTracker.forceUpdate();
        }

        PersistentSaveManager.userInfoTracker.resetTracked();
        PersistentSaveManager.userPreferenceTracker.resetTracked();
        PersistentSaveManager.gameTracker.resetTracked();
        PersistentSaveManager.unsavedGameTracker.resetTracked();

        TokenStore.clearRefreshToken();
    }

    public static void setLoggedIn() {
        appLogger.info("Logging into app");
        for (Loginable l : TriggerManager.getTriggerables(Loginable.class)) l.onLogin();
    }

    public static void setOnline() {
        appLogger.info("Setting app online");
        for (Onlineable o : TriggerManager.getTriggerables(Onlineable.class)) o.onOnline();
    }

    public static void setOffline() {
        appLogger.info("Setting app offline");
        for (Onlineable o : TriggerManager.getTriggerables(Onlineable.class)) o.onOffline();
        for (Onlineable o : TriggerManager.getTriggerables(Onlineable.class)) o.onOffline();
    }

    public static UserWGames getCurrentUserWGames() {
        return new UserWGames(
                new User(PersistentSaveManager.userInfoTracker.getTracked(), PersistentSaveManager.userPreferenceTracker.getTracked()),
                PersistentSaveManager.getAllGames());
    }

    public static void triggerUpdateUser() {
        updateUser(getCurrentUserWGames());
    }

    public static void updateUser(UserWGames userWGames) {
        appLogger.info("Updating app with user: " + userWGames.user().preferences());

        for (UserConfigurable u : TriggerManager.getTriggerables(UserConfigurable.class)) u.updateWithUser(userWGames);

        changeAppTheme(userWGames.user().preferences().getGlobalTheme());
        SettingsManager.updateSettingsWrapper(userWGames.user().preferences());

        PersistentSaveManager.updateAll(userWGames);
    }

    public static void close(){
        appLogger.info("Calling on close triggers...: " + TriggerManager.getTriggerables(Closable.class));
        for (Closable c : TriggerManager.getTriggerables(Closable.class)){
            c.onClose();
        }

        scheduledExecutorService.shutdownNow();
    }

    @Override
    public void stop() {
        close();
    }

}
