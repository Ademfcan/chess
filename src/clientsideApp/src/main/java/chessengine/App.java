package chessengine;

import chessengine.Audio.SoundPlayer;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.Enums.Window;
import chessengine.Puzzle.PuzzleManager;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Functions.MagicBitboardGenerator;
import chessengine.Computation.Stockfish;
import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.KeyManager;
import chessengine.Enums.MainScreenState;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Graphics.AppStateChangeNotification;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.GlobalMessager;
import chessengine.Graphics.MainScreenController;
import chessengine.Managers.CampaignMessageManager;
import chessengine.Managers.ClientManager;
import chessengine.Managers.UserPreferenceManager;
import chessengine.Managers.WebSocketClient;
import chessengine.Start.StartScreenController;
import chessserver.Enums.CampaignTier;
import chessserver.Enums.GlobalTheme;
import chessserver.Enums.INTENT;
import chessserver.Friends.FriendDataResponse;
import chessserver.Communication.DatabaseEntry;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import jakarta.websocket.DeploymentException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
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
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class App extends Application {

    public final static double referenceDpi = 100;
    private final static String startUrl = "/FxmlFiles/StartScreen.fxml";
    private final static String mainUrl = "/FxmlFiles/MainScreen.fxml";
    private final static String startCss = "/CSSFiles/StartScreenCss.css";
    private final static String mainCss = "/CSSFiles/MainScreenCss.css";
    private static final long MaxTimeDeltaBetweenSynchrosMS = TimeUnit.SECONDS.toMillis(10); // 10s
    public static Logger appLogger = LogManager.getLogger("App_Logger");
    public static MainScreenController mainScreenController;
    public static StartScreenController startScreenController;
    public static GlobalMessager messager;
    public static SoundPlayer soundPlayer;
    public static boolean isStartScreen;
    public static Window currentWindow;
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
    private static long lastFriendSynchroMS = -1;

    public static PuzzleManager puzzleManager;

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
        return webclient != null && webclient.isOpen();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readFromObjectMapper(String object, Class<T> objectClass){
        try {
            return objectMapper.readValue(object,objectClass);
        }
        catch (Exception e){
            appLogger.error("Objectmapper error when reading value!",e);
        }
        return null;
    }


    public static void changeUser(UserInfo info) {
        userManager.changeAppUser(info);
        try {
            webclient = userManager.getClientFromUser();
        } catch (DeploymentException e) {
            messager.sendMessage("No connection to server!", Window.Start);
        } catch (IOException e) {
            appLogger.error("Error when changing user", e);
        }
    }

    public static boolean attemptReconnection() {
        try {
            Platform.runLater(() -> {
                messager.sendMessage("Disconnected from server, reconnecting...", Window.Main);
                messager.sendMessage("Disconnected from server, reconnecting...", Window.Start);
                messager.addLoadingCircle(Window.Main);
                messager.addLoadingCircle(Window.Start);
            });
            webclient = userManager.getClientFromUser();
            return true;
        } catch (DeploymentException e) {
            appLogger.debug("Connection Failed");
            webclient = null;
        } catch (IOException e) {
            appLogger.error("Error when attempting reconnection", e);
        } finally {
            Platform.runLater(() -> {
                messager.removeLoadingCircles(Window.Main);
                messager.removeLoadingCircles(Window.Start);
            });
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
        currentWindow = Window.Start;
        mainScene.setRoot(startRoot);
        updateTheme(globalTheme);
        if (!soundPlayer.isUserPrefBgPaused()) {
            soundPlayer.playSong(false);
        }


    }

    /**
     * play as white flag specifies wether player wants to play as white
     **/
    public static void changeToMainScreenWithoutAny(String gameName, boolean isVsComputer, boolean isPlayer1White, MainScreenState state, boolean playAsWhite) {
        isStartScreen = false;
        currentWindow = Window.Main;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupWithoutGame(isVsComputer, isPlayer1White, gameName, userManager.getUserName(), userManager.getUserElo(), userManager.getUserPfpUrl(), state, playAsWhite);
        soundPlayer.pauseSong(false);

    }

    public static void changeToMainScreenWithGame(ChessGame loadedGame, MainScreenState state, boolean isFirstLoad) {
        isStartScreen = false;
        currentWindow = Window.Main;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupWithGame(loadedGame, state, isFirstLoad);


    }

    public static void changeToMainScreenPuzzle() {
        isStartScreen = false;
        currentWindow = Window.Main;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupPuzzle();

    }

    public static void changeToMainScreenOnline(ChessGame onlinePreinit,String gameType) {
        isStartScreen = false;
        currentWindow = Window.Main;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.preinitOnlineGame(gameType,onlinePreinit);

    }

    public static void changeToMainScreenCampaign(CampaignTier campaignTier, int campaignLevelOfTier, int difficulty) {
        isStartScreen = false;
        currentWindow = Window.Main;
        mainScene.setRoot(mainRoot);
        updateTheme(globalTheme);
        mainScreenController.setupCampaign(userManager.getUserName(), userManager.getUserElo(), userManager.getUserPfpUrl(), campaignTier, campaignLevelOfTier, difficulty);
        soundPlayer.pauseSong(false);

    }

    // web client stuff
    public static void sendRequest(INTENT intent, String extraInfo, Consumer<String> requestResponseAction, boolean isUserSent) {
        if (!isWebClientConnected()) {
            appLogger.debug("Client null trying to create new one");
            if (attemptReconnection()) {
                sendRequest(intent, extraInfo, requestResponseAction, isUserSent);
            } else {
                if (isUserSent) {
                    messager.sendMessage("No server connection!", Window.Main);
                    messager.sendMessage("No server connection!", Window.Start);
                }
                appLogger.error("Server Not Acessable!!");

            }
        } else {
            webclient.sendRequest(intent, extraInfo, requestResponseAction);

        }

    }

    public static void getUserRequest(String username, String passwordHash, Consumer<String> requestAction) {
        if (!isWebClientConnected()) {
            appLogger.debug("Client null trying to create new one");
            if (attemptReconnection()) {
                getUserRequest(username, passwordHash, requestAction);
            } else {
                appLogger.error("Server Not Acessable!!");

            }
        } else {
            webclient.getUserRequest(username, passwordHash, requestAction);


        }
    }

    public static void createAndLoginClientRequest(String username, String password, int currentUUID) {
        try {
            String passwordHash = CryptoUtils.sha256AndBase64(password);

            if (userManager.isLoggedIn()) {
                logout();
//                System.out.println("----------------------------------------------------------");
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
        userPreferenceManager.resetToDefault(true);
        startScreenController.setupOldGamesBox(new ArrayList<>());
        startScreenController.campaignManager.reset();
    }

    public static void partialDatabaseUpdateRequest(UserInfo userInfo) {
        databaseRequest(INTENT.UPDATEUSER, userInfo, userPreferenceManager.getUserPref(), null, null);
    }

    public static void partialDatabaseUpdateRequest(UserPreferences userPreferences) {
        databaseRequest(INTENT.UPDATEUSER, userManager.getCurrentUser(), userPreferences, null, null);
    }

    public static void synchronizeWithServer() {
        if (!userManager.isLoggedIn()) {
            appLogger.debug("Not logged in, cannot synchronize");
            return;
        }
        if (!isWebClientConnected()) {
            appLogger.debug("No connection to server, trying to reconnect");
            if (attemptReconnection()) {
                synchronizeWithServer();
            }
            return;
        }
        String currentPasswordHash = KeyManager.tryLoadCurrentPasswordHash();
        getUserRequest(userManager.getUserName(), currentPasswordHash, (out) -> {
            if (out.isEmpty()) {
                // no record of account on server, so make one
                appLogger.warn("No user in the database, storing local copy");
                databaseRequest(INTENT.PUTUSER, userManager.getCurrentUser(), userPreferenceManager.getUserPref(), currentPasswordHash, null);
            } else {
                try {
                    DatabaseEntry newEntry = objectMapper.readValue(out, DatabaseEntry.class);
                    long serverLastTimeStamp = newEntry.getUserInfo().getLastUpdateTimeMS();
                    long localLastTimeStamp = userManager.getLastTimeStampMs();
                    System.out.println("Server timestamp: " + serverLastTimeStamp + " local timestamp: " + localLastTimeStamp);
                    if (serverLastTimeStamp >= localLastTimeStamp) {
                        // go with server, so update local
                        Platform.runLater(() -> {
                            appLogger.debug("Updating local with server copy");
                            App.refreshAppWithNewUser(newEntry);
                        });
                    } else {
                        // go with local, so update server
                        databaseRequest(INTENT.UPDATEUSER, userManager.getCurrentUser(), userPreferenceManager.getUserPref(), currentPasswordHash, null);
                    }


                } catch (JsonProcessingException jsonProcessingException) {
                    appLogger.error("Json processing exeption when parsing validate client request output!", jsonProcessingException);
                }

                // now handle any incoming friend requests
                updateServerTempValues(currentPasswordHash);

            }
        });
        resyncFriends(true);
    }

    public static void updateServerTempValues(String currentPasswordHash) {
        sendRequest(INTENT.READINCOMINGFRIENDREQUESTS, userManager.getUUID() + "," + currentPasswordHash, (friendRequests) -> {
            if (!friendRequests.isEmpty()) {
                Platform.runLater(() -> {
                    userManager.addMoreFriendRequests(friendRequests, true);
                });
            }
        }, false);

        // now handle any accepted friend requests

        sendRequest(INTENT.READACCEPTEDFRIENDREQUESTS, userManager.getUUID() + "," + currentPasswordHash, (friendRequests) -> {
            if (!friendRequests.isEmpty()) {
                Platform.runLater(() -> {
                    userManager.addAcceptedFriendRequests(friendRequests, true);
                });
            }
        }, false);
    }

    public static void databaseRequest(INTENT intent, UserInfo userInfo, UserPreferences userPreferences, String passwordHash, Consumer<String> responseAction) {
        if (!isWebClientConnected()) {
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
//            App.messager.sendMessageQuick("Updating database", true);
            webclient.databaseRequest(intent, userInfo.getUserName(), realPasswordHash, userInfo.getUuid(), new DatabaseEntry(userInfo, userPreferences), responseAction);

        }

    }

    public static void refreshAppWithNewUser(DatabaseEntry databaseEntry) {
        userPreferenceManager.reloadWithUser(databaseEntry.getPreferences(), false);
        userManager.reloadNewAppUser(databaseEntry.getUserInfo(), true, false);
        startScreenController.resetUserInfo();
        resyncFriends(true);
    }

    public static void sendFriendRequest(String userName, Runnable runIfSucess) {
        int existence = userManager.doesFriendExist(userName, true);
        if (existence > 0) {
            switch (existence) {
                case 1:
                    messager.sendMessage("Already your friend!", Window.Start);
                    break;
                case 2:
                    messager.sendMessage("You have already sent a friend request", Window.Start);
                    break;
                case 3:
                    messager.sendMessage("This person has already sent you a friend request", Window.Start);
                    break;
            }
            return;
        }


        sendRequest(INTENT.SENDFRIENDREQUEST, userName, (out) -> {
            Platform.runLater(() -> {
                if (!out.isEmpty()) {
                    App.userManager.addOutgoingRequest(userName, Integer.parseInt(out), true);
                    messager.sendMessage("Request Sent", Window.Start);
                } else {
                    messager.sendMessage("Failed to send request", Window.Start);
                }
                if (runIfSucess != null) {
                    runIfSucess.run();
                }
            });

        }, true);
    }

    public static void acceptIncomingRequest(String userName, int UUID, Runnable runIfSucess) {
        int existence = userManager.doesFriendExist(userName, false);
        if (existence > 0) {
            messager.sendMessage("Already your friend!", Window.Start);
            return;
        }

        App.sendRequest(INTENT.SENDACCEPTEDFRIENDREQUEST, Integer.toString(UUID), (out) -> {
            Platform.runLater(() -> {
                if (Boolean.parseBoolean(out)) {
                    App.userManager.addNewFriend(userName, UUID, true);
                    messager.sendMessage("Added friend", Window.Start);
                } else {
                    messager.sendMessage("Error! Friend no longer exists on server", Window.Start);
                }

                if (runIfSucess != null) {
                    runIfSucess.run();
                }

            });
        }, true);


    }

    public static void resyncFriends(boolean forceResync) {
        long currentTime = System.currentTimeMillis();
        if (!forceResync) {
            if (currentTime - lastFriendSynchroMS < MaxTimeDeltaBetweenSynchrosMS) {
                appLogger.debug("Not synchronizing, too soon!");
                return;
            }
        }
        lastFriendSynchroMS = currentTime;
        // batched update calls

        String incomingUUIDS = userManager.getIncomingRequestUUIDStr();
        if (!incomingUUIDS.isEmpty()) {
            if (!isWebClientConnected()) {
                userManager.setCurrentIncomingRequestsFromServer(UserHelperFunctions.createPlaceholderFriends(userManager.getIncomingFriendRequests()));
            }
            sendRequest(INTENT.GETFRIENDDATA, incomingUUIDS, (out) -> {
                FriendDataResponse response = readFromObjectMapper(out, FriendDataResponse.class);
                if (response != null) {
                    Platform.runLater(() -> {
                        userManager.setCurrentIncomingRequestsFromServer(response);
                    });
                }
            }, false);
        }
        String suggestedUUIDS = userManager.getSuggestedFriendUUIDStr();
        if (!suggestedUUIDS.isEmpty()) {
            if (!isWebClientConnected()) {
                userManager.setCurrentSuggestedFriendsFromServer(UserHelperFunctions.createPlaceholderFriends(userManager.getFriendSuggestions()));
            }
            sendRequest(INTENT.GETFRIENDDATA, suggestedUUIDS, (out) -> {
                FriendDataResponse response = readFromObjectMapper(out, FriendDataResponse.class);
                if (response != null) {
                    Platform.runLater(() -> {
                        userManager.setCurrentSuggestedFriendsFromServer(response);
                    });
                }
            }, false);
        }
        String friendUUIDS = userManager.getFriendUUIDStr();
        if (!friendUUIDS.isEmpty()) {
            if (!isWebClientConnected()) {
                userManager.setCurrentFriendsFromServer(UserHelperFunctions.createPlaceholderFriends(userManager.getFriends()));
            }
            sendRequest(INTENT.GETFRIENDDATA, friendUUIDS, (out) -> {
                FriendDataResponse response = readFromObjectMapper(out, FriendDataResponse.class);
                if (response != null) {
                    Platform.runLater(() -> {
                        userManager.setCurrentFriendsFromServer(response);
                    });
                }

            }, false);

        }

    }

    public static void updateUsernamesFromServer() {
        String incomingUUIDS = userManager.getIncomingRequestUUIDStr();
        if (!incomingUUIDS.isEmpty()) {
            sendRequest(INTENT.GETUSERNAMES, incomingUUIDS, (out) -> {
                Platform.runLater(() -> {
                    userManager.updateIncomingRequestUsernames(out);
                });
            }, false);
        }
        String outgoingUUIDS = userManager.getOutgoingRequestUUIDStr();
        if (!outgoingUUIDS.isEmpty()) {
            sendRequest(INTENT.GETUSERNAMES, outgoingUUIDS, (out) -> {
                Platform.runLater(() -> {
                    userManager.updateOutgoingRequestUsernames(out);
                });
            }, false);
        }
        String friendUUIDS = userManager.getFriendUUIDStr();
        if (!friendUUIDS.isEmpty()) {
            sendRequest(INTENT.GETUSERNAMES, friendUUIDS, (out) -> {
                Platform.runLater(() -> {
                    userManager.updateFriendUsernames(out);
                });
            }, false);
        }
    }

    public static void createOnlineGameRequest(String gameType,ChessGame onlinePreinit) {
        webclient.setLinkedGame(onlinePreinit);
        sendRequest(INTENT.CREATEGAME,gameType,(out) ->{
            if(Integer.parseInt(out) == -1){
                // match found
                Platform.runLater(()->{
                    App.messager.removeLoadingCircles(Window.Main);
                });
            }
            System.out.println("todo");
        },true);
    }

    @Override
    public void init() throws Exception {
        super.init();
        // load app

        notifyPreloader(new ProgressNotification(0.1));
        notifyPreloader(new AppStateChangeNotification("Loading stockfish..."));
        stockfishForEval = new Stockfish();
        getMoveStockfish = new Stockfish();

        notifyPreloader(new ProgressNotification(0.2));
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


        notifyPreloader(new ProgressNotification(0.3));
        notifyPreloader(new AppStateChangeNotification("Loading magic bitboards..."));

        magicBitboardGenerator = new MagicBitboardGenerator();


        dpi = Screen.getPrimary().getDpi();
        dpiScaleFactor = dpi / referenceDpi;

        notifyPreloader(new ProgressNotification(0.35));
        notifyPreloader(new AppStateChangeNotification("Loading user..."));

        userManager = new ClientManager();

        notifyPreloader(new ProgressNotification(0.4));
        notifyPreloader(new AppStateChangeNotification("Connecting to server..."));

        try {
            webclient = userManager.getClientFromUser();
        } catch (DeploymentException e) {
            appLogger.debug("No connection to server!");
            webclient = null;
        } catch (IOException e) {
            appLogger.error("Io error on webclient creation", e);
        }
        notifyPreloader(new ProgressNotification(0.5));


        notifyPreloader(new ProgressNotification(0.5));
        notifyPreloader(new AppStateChangeNotification("Loading managers..."));
        soundPlayer = new SoundPlayer();

        ChessCentralControl = new ChessCentralControl();
        messager = new GlobalMessager();
        userPreferenceManager = new UserPreferenceManager();
        campaignMessager = new CampaignMessageManager(ChessCentralControl);

        notifyPreloader(new AppStateChangeNotification("Loading puzzles..."));
        puzzleManager = new PuzzleManager();


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        notifyPreloader(new ProgressNotification(0.6));
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

        notifyPreloader(new ProgressNotification(0.7));
        notifyPreloader(new AppStateChangeNotification("Setting up graphics..."));

        messager.Init(startMessageBoard, mainMessageBoard, startScreenController.startRef, mainScreenController.mainRef);
        mainScene = new Scene(startRoot);
        isStartScreen = true;
        currentWindow = Window.Start;
        primaryStage.setOnCloseRequest(e -> {
            puzzleManager.close();
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
        userManager.init(startScreenController);
        mainScreenController.oneTimeSetup();
        notifyPreloader(new ProgressNotification(0.8));

        userPreferenceManager.setDefaultSelections();

        if (webclient != null) {
            synchronizeWithServer();
            notifyPreloader(new AppStateChangeNotification("Connected to server"));
        } else {
            notifyPreloader(new AppStateChangeNotification("Server connection failed"));
        }

        ((StackPane) startRoot).getChildren().add(startMessageBoard);
        ((StackPane) mainRoot).getChildren().add(mainMessageBoard);


        primaryStage.setScene(mainScene);
        primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight()/2);
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth()/2);
        notifyPreloader(new ProgressNotification(0.9));
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
        if (ChessCentralControl.gameHandler.currentlyGameActive() && ChessCentralControl.gameHandler.isCurrentGameFirstSetup() && mainScreenController.currentState != MainScreenState.VIEWER && mainScreenController.currentState != MainScreenState.SANDBOX && mainScreenController.currentState != MainScreenState.SIMULATION) {
            if (ChessCentralControl.gameHandler.gameWrapper.getGame().getMaxIndex() > ChessCentralControl.gameHandler.gameWrapper.getGame().getMinIndex()) {
                // if the game is not empty add it
                App.userManager.saveUserGame(ChessCentralControl.gameHandler.gameWrapper.getGame());
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
