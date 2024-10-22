package chessengine.Managers;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.Misc.ChessConstants;
import chessserver.*;
import jakarta.websocket.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.nd4j.shade.jackson.databind.ObjectMapper;

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
            OutputMessage out = ChessConstants.objectMapper.readValue(message, OutputMessage.class);
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
                    UserInfo info = ChessConstants.objectMapper.readValue(out.getExtraInformation(), UserInfo.class);
                    App.changeUser(info);
                }

                case GAMECLOSED -> {
                    logger.debug("Game closed");
                    linkedGame.sendMessageToInfo(out.getExtraInformation());
                }
                case ENTEREDGAME -> {
                    String[] info = out.getExtraInformation().split(",");
                    String opponentName = info[0];
                    int opponentElo = Integer.parseInt(info[1]);
                    String pfpUrl = info[2];
                    boolean isWhiteOriented = Boolean.parseBoolean(info[3]);
                    linkedGame.setWebGameInitialized(true);
                    linkedGame.initWebGame(opponentName, opponentElo, pfpUrl, isWhiteOriented);
                    linkedGame.sendMessageToInfo("Game Started!\nName: " + opponentName + " elo: " + opponentElo);
                }
                case INVALIDOPERATION -> {
                    logger.error("Invalid operation: \n" + out.getExtraInformation());
                }
                case SQLERROR -> {
                    logger.error("Sql error: \n" + out.getExtraInformation());
                }
                case CHATFROMOPPONENT -> {
                    linkedGame.sendMessageToInfo("(" + linkedGame.getBlackPlayerName() + ") " + out.getExtraInformation());
                    App.soundPlayer.playEffect(Effect.MESSAGE);
                }
                case GAMEMOVEFROMOPPONENT -> {
                    linkedGame.makePgnMove(out.getExtraInformation(), true);
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
                    App.messager.sendMessageQuick(out.getExtraInformation(),true);
                    App.messager.sendMessageQuick(out.getExtraInformation(),false);
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
        App.attemptReconnection();
    }

    public void sendRequest(INTENT intent, String extraInfo,Consumer<String> runOnResponse) {
//        logger.debug("Sending request with Intent: " + intent.toString() + " exInfo: " + extraInfo);
        try {
            InputMessage inputMessage = new InputMessage(this.client, intent, extraInfo);
            String message = ChessConstants.objectMapper.writeValueAsString(new InputMessage(this.client, intent, extraInfo));
            if(runOnResponse != null){
                responseMap.put(inputMessage.getUniqueId(),runOnResponse);
            }
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            logger.error("Error on sending request", e);
        }
    }

    public void getUserRequest(String userName, String userPassword,Consumer<String> requestAction) {
        // todo: encrypt!!
        sendRequest(INTENT.GETUSER, userName + "," + userPassword,requestAction);

    }

    public void databaseRequest(INTENT intent,String userName, String passwordHash,int UUID,DatabaseEntry entry,Consumer<String> responseAction){
        if(entry == null){
            sendRequest(intent, userName + "," + passwordHash,responseAction);
        }
        else{
            try {
                DatabaseRequest request  = new DatabaseRequest(ChessConstants.objectMapper.writeValueAsString(entry),userName,passwordHash,UUID,entry.getUserInfo().getLastUpdateTimeMS());
                sendRequest(intent,ChessConstants.objectMapper.writeValueAsString(request),responseAction);
            }
            catch (org.nd4j.shade.jackson.core.JsonProcessingException e){
                logger.error("objctmapper json eception for database request",e);
            }
        }
    }



    public void close() throws IOException {
        session.close();
    }
}
