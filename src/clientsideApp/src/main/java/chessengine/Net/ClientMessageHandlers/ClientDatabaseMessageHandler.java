package chessengine.Net.ClientMessageHandlers;

import chessengine.App;
import chessengine.AutoRegister;
import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.DeviceId;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Enums.Window;
import chessengine.Graphics.OnFriendUpdate;
import chessengine.Graphics.UserConfigurable;
import chessengine.TriggerManager;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.Communication.User;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.DatabaseMessageTypes;
import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClientDatabaseMessageHandler extends MessageHandler<DatabaseMessageTypes.ServerRequest> {
    private final ObjectMapper databaseMapper = new ObjectMapper();

    public ClientDatabaseMessageHandler(WebSocketConnection connection) {
        super(false, connection, DatabaseMessageTypes.ServerRequest.class, MessagePath.Endpoint.CLIENT);
    }

    public void synchronizeWithServer() {
        System.out.println("Synchronizing");
        if(!App.userManager.isLoggedIn()){
            return;
        }

        System.out.println("Pushing games");
        pushUnsavedGames();

        UserWGames currentUserWGames = App.getCurrentUserWGames();
        tryGetUserWGames((out) -> {
            System.out.println("User w games: " + out.get());
            UserWGames userWGames = out.get();

            User serverUser = userWGames.user();
            User currentUser = currentUserWGames.user();
            long serverLastTimeStamp = serverUser.userInfo().getLastUpdateTimeMS();
            long localLastTimeStamp = currentUser.userInfo().getLastUpdateTimeMS();
            logger.debug("Server timestamp: " + serverLastTimeStamp + " local timestamp: " + localLastTimeStamp);


            if (serverLastTimeStamp >= localLastTimeStamp) {
                // go with server, so update local
                Platform.runLater(() -> App.updateUser(userWGames));
            } else {
                // go with local, so update server
                App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.UPDATEUSER,
                        new DatabaseMessagePayloadTypes.UserPayload(currentUser))));
            }
        });

        System.out.println("Syncing friends");
        // now read any friend requests that have been waiting
        resyncFriends(true);
    }

    private long lastFriendSynchroMS = -1;
    private static final long MinFriendResyncTime = TimeUnit.SECONDS.toMillis(10); // 10s

    public void resyncFriends(boolean forceResync) {
        // given friends can change their name, but UUIDS are fixed, occasionally probe to see if anything has changed
        // this applies to other changing things aswell, like online status
        if(!App.userManager.isLoggedIn()){
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (!forceResync) {
            if (currentTime - lastFriendSynchroMS < MinFriendResyncTime) {
                logger.warn("Not synchronizing, too soon!");
                return;
            }
        }
        lastFriendSynchroMS = currentTime;

        // update friend requests
        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.READINCOMINGFRIENDREQUESTS, new Payload.Empty()))
                .onDataResponse((DatabaseMessagePayloadTypes.UUIDSPayload response) -> {
                            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.GETFRIENDDATA, new DatabaseMessagePayloadTypes.UUIDSPayload(response.uuids())))
                                    .onDataResponse((UserMessagePayloadTypes.FriendDataResponsePayload dataResponse) -> {
                                        Platform.runLater(() -> {
                                            TriggerManager.getTriggerables(OnFriendUpdate.class).forEach(t -> t.onCurrentIncomingRequests(dataResponse.response()));
                                        });
                                    }));
                        }));

        // update friend requests
        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.READOUTGOINGFRIENDREQUESTS, new Payload.Empty()))
                .onDataResponse((DatabaseMessagePayloadTypes.UUIDSPayload response) -> {
                    App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.GETFRIENDDATA, new DatabaseMessagePayloadTypes.UUIDSPayload(response.uuids())))
                            .onDataResponse((UserMessagePayloadTypes.FriendDataResponsePayload dataResponse) -> {
                                Platform.runLater(() -> {
                                    TriggerManager.getTriggerables(OnFriendUpdate.class).forEach(t -> t.onCurrentOutgoingRequests(dataResponse.response()));
                                });
                            }));
                }));

        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.READFRIENDS, new Payload.Empty()))
                .onDataResponse((DatabaseMessagePayloadTypes.UUIDSPayload response) -> {
                            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.GETFRIENDDATA, new DatabaseMessagePayloadTypes.UUIDSPayload(response.uuids())))
                                    .onDataResponse((UserMessagePayloadTypes.FriendDataResponsePayload dataResponse) -> {
                                        Platform.runLater(() -> {
                                            TriggerManager.getTriggerables(OnFriendUpdate.class).forEach(t -> t.onCurrentFriends(dataResponse.response()));
                                        });
                                    }));
                        }));

        // suggested friends are local only
        List<UUID> suggestedUUIDS = App.userManager.userInfoManager.getSuggestedFriendUUIDs();
        if(!suggestedUUIDS.isEmpty()) {
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.GETFRIENDDATA, new DatabaseMessagePayloadTypes.UUIDSPayload(suggestedUUIDS)))
                    .onDataResponse((UserMessagePayloadTypes.FriendDataResponsePayload dataResponse) -> {
                        Platform.runLater(() -> {
                            TriggerManager.getTriggerables(OnFriendUpdate.class).forEach(t -> t.onSuggestedFriends(dataResponse.response()));
                        });
                    }));
        }


    }

    public void pushUnsavedGames(){
        if(!App.userManager.isLoggedIn()){
            return;
        }

        int i = 0;
        int chunkSize = 10;
        List<GameInfo> tracked = PersistentSaveManager.unsavedGameTracker.getTracked();

        while (i < tracked.size()) {
            List<GameInfo> chunk = tracked.subList(i, Math.min(tracked.size(), i + chunkSize));
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.SaveChessGames,
                    new DatabaseMessagePayloadTypes.SaveGamesPayload(chunk.stream().map(p -> new DatabaseMessagePayloadTypes.SaveGamePayload(p, true)).toList())))
                    .onStatusResponse((status) -> {
                        if(!status.response().isErrorStatus()) chunk.clear();
                        PersistentSaveManager.gameTracker.getTracked().addAll(chunk);
                    }));

            i += chunkSize;
        }
    }


    public void tryGetUserWGames(Consumer<Optional<UserWGames>> onGetUser) {
        // trys to retrieve a certain user based on their username and password hash
        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.LOADFULLUSER,
                new Payload.IntegerPayload(50))).onDataResponse((DatabaseMessagePayloadTypes.UserWGamesPayload response) -> {
            Optional<UserWGames> entry = Optional.empty();
            if(response.userWGames() != null) {
                entry = Optional.of(response.userWGames());
            }
            onGetUser.accept(entry);
        }));
    }

    @Override
    protected void handleMessage(DatabaseMessageTypes.ServerRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) {
        Payload messagePayload = message.getMessagePayload();
        switch (messageOption) {
            case SQLSUCESS -> {
                logger.debug("Sql update sucess");
            }
            case SQLMESSAGE -> {
                String sqlMessage = ((Payload.StringPayload) messagePayload).payload();
                try {
                    Platform.runLater(() -> {
                        App.messager.sendMessage(sqlMessage, Window.Main);
                        App.messager.sendMessage(sqlMessage, Window.Start);
                    });
                }
                catch (Exception e) {
                    logger.debug(sqlMessage);
                }
            }
            case UPDATEFRIENDS -> resyncFriends(true);
        }
    }

}
