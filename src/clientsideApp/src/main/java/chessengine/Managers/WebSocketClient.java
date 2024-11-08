package chessengine.Managers;

import chessengine.App;
import chessengine.Audio.Effect;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Communication.InputMessage;
import chessserver.Communication.OutputMessage;
import chessserver.Enums.INTENT;
import chessserver.Friends.Friend;
import chessserver.Communication.DatabaseEntry;
import chessserver.Communication.DatabaseRequest;
import chessserver.User.FrontendClient;
import chessserver.User.UserInfo;
import jakarta.websocket.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ClientEndpoint
public class WebSocketClient {
    private final Logger logger = LogManager.getLogger(this.getClass());
    //    private final URI serverUri = URI.create("wss://ademchessserver.azurewebsites.net/app/home");
    private final URI serverUri = URI.create("ws://20.157.72.110:8081/app/home");
    private FrontendClient client;

    private ChessGame linkedGame;

    private Session session;

    private Map<Integer, Consumer<String>> responseMap;

    public WebSocketClient(FrontendClient client) throws DeploymentException, IOException {
        this.linkedGame = null;
        this.client = client;
        this.responseMap = Collections.synchronizedMap(new HashMap<>());
        ClientManager clientManager = ClientManager.createClient();
        clientManager.connectToServer(this, serverUri);
    }

    public void updateClient(FrontendClient newClient){
        this.client = newClient;
    }

    public void setLinkedGame(ChessGame chessGame) {
        this.linkedGame = chessGame;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.debug("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            OutputMessage out = App.objectMapper.readValue(message, OutputMessage.class);
            if(responseMap.containsKey(out.getUniqueId())){
                responseMap.get(out.getUniqueId()).accept(out.getExtraInformation());
                responseMap.remove(out.getUniqueId());
                return;
            }
            switch (out.getServerResponseType()) {
                case SERVERRESPONSEACTIONREQUEST -> {
                    logger.error("------------Critical-ERROR--------------\nThe server has reponded with an action request, and no action is provided!");

                }
                case CLIENTVAIDATIONSUCESS -> {
                    // sucessfuly accesed acount on
                    UserInfo info = App.objectMapper.readValue(out.getExtraInformation(), UserInfo.class);
                    App.changeUser(info);
                }

                case GAMECLOSED -> {
                    logger.debug("Game closed");
                    Platform.runLater(() ->{
                        App.ChessCentralControl.chessActionHandler.appendNewMessageToChat(out.getExtraInformation());
                    });
                }
                case ENTEREDGAME -> {
                    String[] info = out.getExtraInformation().split(",");
                    String opponentName = info[0];
                    int opponentElo = Integer.parseInt(info[1]);
                    String pfpUrl = info[2];
                    boolean isPlayer1White = Boolean.parseBoolean(info[3]);
                    Platform.runLater(() ->{
                        App.ChessCentralControl.gameHandler.gameWrapper.initWebGame(opponentName, opponentElo, pfpUrl, isPlayer1White);
                        App.ChessCentralControl.chessActionHandler.appendNewMessageToChat("Game Started!\nName: " + opponentName + " elo: " + opponentElo);
                    });
                }
                case INVALIDOPERATION -> {
                    logger.error("Invalid operation: \n" + out.getExtraInformation());
                }
                case SQLERROR -> {
                    logger.error("Sql error: \n" + out.getExtraInformation());
                }
                case CHATFROMOPPONENT -> {
                    Platform.runLater(() ->{
                        App.ChessCentralControl.chessActionHandler.appendNewMessageToChat("(" + linkedGame.getBlackPlayerName() + ") " + out.getExtraInformation());
                        App.soundPlayer.playEffect(Effect.MESSAGE);
                    });
                }
                case GAMEMOVEFROMOPPONENT -> {
                    Platform.runLater(() ->{
                        App.ChessCentralControl.gameHandler.gameWrapper.makePgnMove(out.getExtraInformation(), true,App.userPreferenceManager.isNoAnimate());
                    });
                }
                case ELOUPDATE -> {
                    int change = Integer.parseInt(out.getExtraInformation());
                    Platform.runLater(() -> {
                        App.userManager.updateUserElo(change);
                    });
                }
                case SQLSUCESS -> {
                    logger.debug("Sql update sucess");
                }
                case SQLMESSAGE ->{
                    Platform.runLater(() ->{
                        App.messager.sendMessageQuick(out.getExtraInformation(),true);
                        App.messager.sendMessageQuick(out.getExtraInformation(),false);
                    });
                }
                case INCOMINGFRIENDREQUEST -> {
                    String[] in = out.getExtraInformation().split(",");
                    Friend request = new Friend(in[0],Integer.parseInt(in[1]));
                    Platform.runLater(() -> {
                        App.userManager.addNewFriendRequest(request, true);
                    });
                }
                case ACCEPTEDFRIENDREQUEST -> {
                    String[] in = out.getExtraInformation().split(",");
                    Platform.runLater(() ->{
                        App.userManager.addNewFriend(in[0],Integer.parseInt(in[1]),true);
                    });

                }
                case CLOSEDSUCESS -> {
                    logger.debug("Session closed sucessfully");
                }
            }
        } catch (Exception e) {
            logger.error("Error on websocket receive message", e);
        }
    }

    @OnError
    public void onError(Throwable t) {
        logger.error("Webclient error occurred", t);
    }

    @OnClose
    public void onClose(Session session) {
//        App.attemptReconnection();
    }

    public void sendRequest(INTENT intent, String extraInfo, Consumer<String> runOnResponse) {
//        logger.debug("Sending request with Intent: " + intent.toString() + " exInfo: " + extraInfo);
        try {
            InputMessage inputMessage = new InputMessage(this.client, intent, extraInfo);
            String message = App.objectMapper.writeValueAsString(inputMessage);
            if(runOnResponse != null){
                responseMap.put(inputMessage.getUniqueId(),runOnResponse);
            }
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            logger.error("Error on sending request", e);
        }
    }

    public void getUserRequest(String userName, String userPasswordHash,Consumer<String> requestAction) {
        // todo: encrypt!!
        sendRequest(INTENT.GETUSER, userName + "," + userPasswordHash,requestAction);

    }

    public void databaseRequest(INTENT intent, String userName, String passwordHash, int UUID, DatabaseEntry entry, Consumer<String> responseAction){
        if(entry == null){
            sendRequest(intent, userName + "," + passwordHash,responseAction);
        }
        else{
            try {
                DatabaseRequest request  = new DatabaseRequest(App.objectMapper.writeValueAsString(entry),userName,passwordHash,UUID,entry.getUserInfo().getLastUpdateTimeMS(),entry.getUserInfo().getUserelo());
                sendRequest(intent,App.objectMapper.writeValueAsString(request),responseAction);
            }
            catch (org.nd4j.shade.jackson.core.JsonProcessingException e){
                logger.error("objctmapper json eception for database request",e);
            }
        }
    }



    public void close() throws IOException {
        logger.debug("Closing session");
        session.close();
    }

    public boolean isOpen() {
        return session.isOpen();
    }
}
