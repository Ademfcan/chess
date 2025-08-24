package chessengine.Managers;

import chessengine.App;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Crypto.TokenStore;
import chessengine.Enums.Window;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.Communication.User;
import chessserver.Misc.Tuple;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageTypes.DatabaseMessageTypes;
import chessserver.Net.Payload;
import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class UserManager {
    private final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
    private final int maxGamesToLoad = 50;

    public final UserInfoManager userInfoManager;
    public final UserPreferenceManager userPreferenceManager;

    public UserManager(){
        userInfoManager = new UserInfoManager();
        userPreferenceManager = new UserPreferenceManager();

        // as the file gets periodically saved, we can use this to also push changes to the server
        PersistentSaveManager.userInfoTracker.setOnPeriodicUpdate(this::pushUserInfo);
        PersistentSaveManager.userPreferenceTracker.setOnPeriodicUpdate(this::pushUserPreferences);

        // trigger first login state change
    }


    // database updates
    public void pushUserInfo(UserInfo userInfo) {
        if(isLoggedIn()){
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message( DatabaseMessageTypes.ClientRequest.UPDATEUSERINFO,
                    new DatabaseMessagePayloadTypes.UserInfoPayload(userInfoManager.getAppUser()))));
        }
        else{
            logger.debug("User not logged in, not pushing user info");
        }
    }

    public void pushUserPreferences(UserPreferences userPreferences) {
        if(isLoggedIn()){
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.UPDATEUSERPREF,
                    new DatabaseMessagePayloadTypes.UserPrefPayload(userPreferenceManager.getUserPref()))));
        }
        else{
            logger.debug("User not logged in, not pushing user preferences");
        }
    }



    public User getCurrentUser() {
        return new User(userInfoManager.getAppUser(), userPreferenceManager.getUserPref());
    }


    // sign up


    public void signup(String userName, String userEmail, String userPassword, boolean loginIfSuccess) {
        // start by setting the user's
        App.clientUserMessageHandler.signup(userName, userEmail, userPassword, (success) -> {
            if (success) {
                Platform.runLater(() -> App.messager.sendMessage("Account created successfully!", Window.Start));
                if(loginIfSuccess){
                    Tuple<List<GameInfo>, UserPreferences> newUserStuff = null;
                    if(!isLoggedIn()){
                        // new user might have played a couple of games or changed some preferences
                        // keep them
                        newUserStuff = new Tuple<>(
                                PersistentSaveManager.unsavedGameTracker.getTracked(),
                                PersistentSaveManager.userPreferenceTracker.getTracked()
                        );
                    }

                    Platform.runLater(() -> App.messager.sendMessage("Logging in...", Window.Start));
                    login(userName, userPassword, newUserStuff);
                }
            } else {
                Platform.runLater(() ->App.messager.sendMessage("Failed to create account. Please try again.", Window.Start));
            }
        });


    }

    // login/logout

    public boolean isLoggedIn(){return TokenStore.getRefreshToken() != null;}


    public void login(String userName, String userPassword) {
        login(userName, userPassword, null);
    }

    private void login(String userName, String userPassword, @Nullable Tuple<List<GameInfo>, UserPreferences> fromNotLoggedIn) {
        App.clientUserMessageHandler.login(userName, userPassword, (refreshToken)  -> {
            if (refreshToken.isPresent()) {
                String token = refreshToken.get();
                TokenStore.setRefreshToken(token);

                // retrieve user
                App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.LOADFULLUSER, new Payload.IntegerPayload(maxGamesToLoad)))
                        .onDataResponse((DatabaseMessagePayloadTypes.UserWGamesPayload userPayload) -> {
                            PersistentSaveManager.updateAll(userPayload.userWGames());
                            Platform.runLater(() -> {
                                App.updateUser(userPayload.userWGames());
                                App.setLoggedIn();

                                if(fromNotLoggedIn != null){
                                    PersistentSaveManager.unsavedGameTracker.updateTracked(fromNotLoggedIn.first());
                                    PersistentSaveManager.userPreferenceTracker.updateTracked(fromNotLoggedIn.second());

                                    App.triggerUpdateUser();
                                    App.clientDatabaseMessageHandler.synchronizeWithServer();
                                }
                            });
                        }));


            } else {
                Platform.runLater(() -> {
                    App.messager.sendMessage("Failed to log in!");
                });
            }
        });
    }

}
