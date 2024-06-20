package chessengine;

import chessserver.FrontendClient;
import chessserver.INTENT;
import chessserver.UserInfo;
import org.junit.jupiter.api.Test;

import jakarta.websocket.DeploymentException;
import java.io.IOException;

public class websocketTests {
    @Test void testGetClientCount() throws InterruptedException {
        try {
            WebSocketClient c1 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Bob","test","test")));
            c1.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c2 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Jenkins","test","test")));
            c2.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c3 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Alice","test","test")));
            c3.sendRequest(INTENT.CREATEGAME,"reg10");

            c1.sendRequest(INTENT.PULLTOTALPLAYERCOUNT,"");
            Thread.sleep(2000);

        }
        catch (DeploymentException | IOException e){
            System.out.println(e.getMessage());
        }
    }
}
