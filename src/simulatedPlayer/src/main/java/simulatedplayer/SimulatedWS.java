package simulatedplayer;

import chessserver.ChessRepresentations.ChessGame;
import chessserver.Communication.InputMessage;
import chessserver.Communication.OutputMessage;
import chessserver.Enums.INTENT;
import chessserver.User.FrontendClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ClientManager;

@ClientEndpoint
public class SimulatedWS {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final URI serverUri = URI.create("ws://20.157.72.110:8081/app/home");
    private SimulatedGame linkedGame;
    private FrontendClient frontendClient;
    private Session session;
    private Map<Integer, Consumer<String>> responseMap;
    private final ObjectMapper objectMapper;

    public SimulatedWS(SimulatedGame linkedGame, FrontendClient simulatedClient) {
        this.frontendClient = simulatedClient;
        this.linkedGame = linkedGame;
        this.responseMap = Collections.synchronizedMap(new HashMap<>());
        this.objectMapper = new ObjectMapper();

        try {
            connect();
        }
        catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect() throws DeploymentException, IOException {
        ClientManager clientManager = ClientManager.createClient();
        clientManager.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, TimeUnit.SECONDS.toMillis(2));
        clientManager.connectToServer(this, serverUri);
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
            if(responseMap.containsKey(out.getUniqueId())){
                responseMap.get(out.getUniqueId()).accept(out.getExtraInformation());
                responseMap.remove(out.getUniqueId());
                return;
            }
            switch (out.getServerResponseType()) {
                case SERVERRESPONSEACTIONREQUEST -> {
                    System.out.println(out.toString());
                    logger.error("------------Critical-ERROR--------------\nThe server has reponded with an action request, and no action is provided!");

                }
                case ASKINGFORDRAW -> {
                    linkedGame.considerDraw();
                }
                case GAMECLOSED -> {
                    linkedGame.endGame();
                }
                case GAMEFINISHED -> {
                    linkedGame.endGame();
                }
                case ENTEREDGAME -> {
                    String[] info = out.getExtraInformation().split(",");
                    boolean isPlayer1White = Boolean.parseBoolean(info[3]);
                    linkedGame.enteringGame(isPlayer1White);
                }
                case CHATFROMOPPONENT -> {
                    linkedGame.considerRespondingChat(out.getExtraInformation());
                }
                case GAMEMOVEFROMOPPONENT -> {
                    linkedGame.inputMove(out.getExtraInformation());
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
    }

    public void enterGameRequest(String gameType) {
        sendRequest(INTENT.CREATEGAME,gameType,(out) ->{
            if(Integer.parseInt(out) == -1){
                System.out.println("Joining game");
            }
            else{
                System.out.println("In waiting pool");
            }
        });
    }

    public void sendRequest(INTENT intent, String extraInfo, Consumer<String> runOnResponse) {
//        logger.debug("Sending request with Intent: " + intent.toString() + " exInfo: " + extraInfo);
        try {
            InputMessage inputMessage = new InputMessage(this.frontendClient, intent, extraInfo);
            String message = objectMapper.writeValueAsString(inputMessage);
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

    public void close() throws IOException {
        logger.debug("Closing session");
        session.close();
    }

    public boolean isOpen() {
        return session.isOpen();
    }
}
