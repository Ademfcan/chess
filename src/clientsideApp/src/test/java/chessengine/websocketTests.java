package chessengine;

import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.KeyManager;
import chessengine.Managers.WebSocketClient;
import chessserver.Misc.ChessConstants;
import chessserver.Enums.INTENT;
import chessserver.User.FrontendClient;
import org.junit.jupiter.api.Test;

import jakarta.websocket.DeploymentException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class websocketTests {
    @Test void testGetClientCount() throws InterruptedException {
        try {
            WebSocketClient c1 = new WebSocketClient(new FrontendClient(ChessConstants.defaultClient.getInfo()));
            c1.sendRequest(INTENT.CREATEGAME,"reg10",null);

            WebSocketClient c2 = new WebSocketClient(new FrontendClient(ChessConstants.defaultClient.getInfo()));
            c2.sendRequest(INTENT.CREATEGAME,"reg10",null);

            WebSocketClient c3 = new WebSocketClient(new FrontendClient(ChessConstants.defaultClient.getInfo()));
            c3.sendRequest(INTENT.CREATEGAME,"reg10",null);

            c1.sendRequest(INTENT.PULLTOTALPLAYERCOUNT,"", System.out::println);
            Thread.sleep(2000);

        }
        catch (DeploymentException | IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Test void consumerTest(){
        try {
            WebSocketClient c1 = new WebSocketClient(new FrontendClient(ChessConstants.defaultClient.getInfo()));
            c1.sendRequest(INTENT.GETNUMBEROFPOOLERS,"reg10", (out) ->{
                System.out.println("Recieved message: \n" + out);
            });
            Thread.sleep(2000);

        }
        catch (DeploymentException | IOException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test void sha256Test() throws NoSuchAlgorithmException {
        System.out.println("F"  + CryptoUtils.sha256AndBase64("F"));
        System.out.println("Key" + KeyManager.tryLoadCurrentPasswordHash());
    }



}
