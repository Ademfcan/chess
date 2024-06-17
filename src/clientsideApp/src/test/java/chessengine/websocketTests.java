package chessengine;

import chessserver.FrontendClient;
import chessserver.INTENT;
import org.junit.jupiter.api.Test;

import jakarta.websocket.DeploymentException;
import java.io.IOException;

public class websocketTests {
    @Test void testGetClientCount() throws InterruptedException {
        try {
            WebSocketClient c1 = new WebSocketClient(new FrontendClient("Adem",100));
            c1.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c2 = new WebSocketClient(new FrontendClient("Bob",100));
            c2.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c3 = new WebSocketClient(new FrontendClient("Alice",100));
            c3.sendRequest(INTENT.CREATEGAME,"reg10");

            c1.sendRequest(INTENT.PULLTOTALPLAYERCOUNT,"");
            Thread.sleep(2000);

        }
        catch (DeploymentException | IOException e){
            System.out.println(e.getMessage());
        }
    }
}
