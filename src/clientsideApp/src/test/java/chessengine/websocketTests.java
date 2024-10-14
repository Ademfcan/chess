package chessengine;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.Managers.WebSocketClient;
import chessserver.*;
import org.junit.jupiter.api.Test;

import jakarta.websocket.DeploymentException;
import java.io.IOException;
import java.util.ArrayList;

public class websocketTests {
    @Test void testGetClientCount() throws InterruptedException {
        try {
            WebSocketClient c1 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Bob","test", ProfilePicture.DEFAULT,new CampaignProgress(123),new ArrayList<>())));
            c1.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c2 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Jenkins","test",ProfilePicture.DEFAULT,new CampaignProgress(123),new ArrayList<>())));
            c2.sendRequest(INTENT.CREATEGAME,"reg10");

            WebSocketClient c3 = new WebSocketClient(new FrontendClient(new UserInfo(100,"Alice","test",ProfilePicture.DEFAULT,new CampaignProgress(123),new ArrayList<>())));
            c3.sendRequest(INTENT.CREATEGAME,"reg10");

            c1.sendRequest(INTENT.PULLTOTALPLAYERCOUNT,"");
            Thread.sleep(2000);

        }
        catch (DeploymentException | IOException e){
            System.out.println(e.getMessage());
        }
    }

//    @Test void makePgnMoveTest(){
//        ChessGame testGame = ChessGame.createTestGame()
//    }

}
