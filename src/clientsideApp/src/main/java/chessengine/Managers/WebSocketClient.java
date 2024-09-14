package chessengine.Managers;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.ChessRepresentations.ChessGame;
import chessserver.*;
import jakarta.websocket.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {
    private final Logger logger = LogManager.getLogger(this.getClass());
    //    private final URI serverUri = URI.create("wss://ademchessserver.azurewebsites.net/app/home");
    private final URI serverUri = URI.create("ws://localhost:8080/app/home");
    private final ObjectMapper objectMapper;
    private final FrontendClient client;

    private ChessGame linkedGame;

    private Session session;

    public WebSocketClient(FrontendClient client) throws DeploymentException, IOException {
        this.linkedGame = null;
        this.objectMapper = new ObjectMapper();
        this.client = client;
        ClientManager clientManager = ClientManager.createClient();
        clientManager.connectToServer(this, serverUri);
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
            OutputMessage out = objectMapper.readValue(message, OutputMessage.class);
            switch (out.getServerResponseType()) {
                case CLIENTVAIDATIONSUCESS -> {
                    // sucessfuly accesed acount on
                    UserInfo info = objectMapper.readValue(out.getExtraInformation(), UserInfo.class);
                    App.changeUser(info);
                }
                case CLIENTVALIDATIONFAIL -> {
                    // username password did not match
                    App.messager.sendMessageQuick("Validation Failed", true);
                }
                case INWAITINGPOOL -> {
                    linkedGame.sendMessageToInfo("Waiting in queue");
                    logger.debug("Added to wait pool");
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
                case LEFTGAMESUCESS -> {
                    logger.debug("Left game");
                }
                case INVALIDOPERATION -> {
                    logger.error("Invalid operation");
                }
                case NUMBEROFPOOLERS -> {
                    //logger.debug("Number in specific pool is: " + out.getExtraInformation());
                    Platform.runLater(() -> {
                        App.startScreenController.poolCount.setText("number of players in pool: " + out.getExtraInformation());
                    });
                }
                case TOTALPLAYERCOUNT -> {
                    // todo integrate this
                    logger.debug("Total in pool is: " + out.getExtraInformation());
                }
                case CHATFROMOPPONENT -> {
                    linkedGame.sendMessageToInfo("(" + linkedGame.getBlackPlayerName() + ")" + out.getExtraInformation());
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
            }
        } catch (Exception e) {
            logger.error("Error on websocket recieve message", e);
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

    public void sendRequest(INTENT intent, String extraInfo) {
//        logger.debug("Sending request with Intent: " + intent.toString() + " exInfo: " + extraInfo);
        try {
            String message = objectMapper.writeValueAsString(new InputMessage(this.client, intent, extraInfo));
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            logger.error("Error on sending request", e);
        }
    }

    public void validateClientRequest(String userName, String userPassword) {
        // todo: encrypt!!
        sendRequest(INTENT.GETUSER, userName + "," + userPassword);

    }

    public void close() throws IOException {
        session.close();
    }
}
