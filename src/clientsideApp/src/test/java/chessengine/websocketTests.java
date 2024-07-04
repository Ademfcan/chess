package chessengine;

import chessserver.*;
import org.junit.jupiter.api.Test;

import jakarta.websocket.DeploymentException;
import java.io.IOException;

public class websocketTests {
    @Test void testGetClientCount() throws InterruptedException {
        try {
            WebSocketClient c1 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Bob","test","test", ProfilePicture.DEFAULT,new CampaignProgress())));
            c1.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c2 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Jenkins","test","test",ProfilePicture.DEFAULT,new CampaignProgress())));
            c2.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c3 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Alice","test","test",ProfilePicture.DEFAULT,new CampaignProgress())));
            c3.sendRequest(INTENT.CREATEGAME,"reg10");

            c1.sendRequest(INTENT.PULLTOTALPLAYERCOUNT,"");
            Thread.sleep(2000);

        }
        catch (DeploymentException | IOException e){
            System.out.println(e.getMessage());
        }
    }
}
