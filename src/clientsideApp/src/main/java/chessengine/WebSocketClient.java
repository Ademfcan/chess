package chessengine;

import chessserver.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import org.glassfish.tyrus.client.ClientManager;

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

    public void setLinkedGame(ChessGame chessGame){
        this.linkedGame = chessGame;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
        try {
            OutputMessage out = objectMapper.readValue(message, OutputMessage.class);
            switch (out.getServerResponseType()){
                case CLIENTVAIDATIONSUCESS -> {
                    // sucessfuly accesed acount on
                    UserInfo info = objectMapper.readValue(out.getExtraInformation(), UserInfo.class);
                    App.changeClient(info);
                }
                case CLIENTVALIDATIONFAIL -> {
                    // username password did not match
                    System.out.println("incorrect");
                }
                case INWAITINGPOOL -> {
                    linkedGame.sendMessageToInfo("Waiting in queue");
                    System.out.println("Added to wait pool");
                }
                case GAMECLOSED -> {
                    System.out.println("Game closed");
                    linkedGame.sendMessageToInfo(out.getExtraInformation());
                }
                case ENTEREDGAME -> {
                    String[] info = out.getExtraInformation().split(",");
                    String opponentName = info[0];
                    int opponentElo = Integer.parseInt(info[1]);
                    linkedGame.setWebGameInitialized(true);
                    linkedGame.initWebGame(opponentName, opponentElo);
                    linkedGame.sendMessageToInfo("Game Started!\nName: " + opponentName + " elo: " + opponentElo);
                }
                case LEFTGAMESUCESS -> {
                    System.out.println("Left game");
                }
                case INVALIDOPERATION -> {
                    System.err.println("Invalid operation");
                }
                case NUMBEROFPOOLERS -> {
                    System.out.println("Number in specific pool is: " + out.getExtraInformation());
                    Platform.runLater(() -> {
                        App.startScreenController.poolCount.setText("number of players in pool: " + out.getExtraInformation());
                    });
                }
                case TOTALPLAYERCOUNT -> {
                    System.out.println("Total in pool is: " + out.getExtraInformation());
                }
                case TURNINDICATOR -> {
                    System.out.println("Your turn is now!");
                    linkedGame.setPlayer1Turn(true);
                }
                case CHATFROMOPPONENT -> {
                    System.out.println("(" + linkedGame.getPlayer2name() + ")" + out.getExtraInformation());
                    linkedGame.sendMessageToInfo("(" + linkedGame.getPlayer2name() + ")" + out.getExtraInformation());
                    App.soundPlayer.playEffect(Effect.MESSAGE);
                }
                case GAMEMOVEFROMOPPONENT -> {
                    linkedGame.makeNewMove(out.getExtraInformation());
                    System.out.println("Your opponent played: " + out.getExtraInformation());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("Error occurred: " + t.toString());
    }

    public void sendRequest(INTENT intent, String extraInfo) {
        try {
            String message = objectMapper.writeValueAsString(new InputMessage(this.client, intent, extraInfo));
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validateClientRequest(String userName,String userPassword){
        // todo: encrypt!!
        sendRequest(INTENT.GETUSER,userName + "," + userPassword);

    }

    public void close() throws IOException {
        session.close();
    }
}
