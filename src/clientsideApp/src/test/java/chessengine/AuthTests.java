package chessengine;

import chessengine.Crypto.TokenStore;
import chessengine.Net.AuthenticatedMessageSender;
import chessengine.Net.ClientMessageHandlers.ClientChessGameMessageHandler;
import chessengine.Net.JWTManager;
import chessengine.Net.WebSocketClient;
import chessengine.Triggers.Loginable;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.DatabaseMessageTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Test;

import java.util.List;


public class AuthTests {
    private void checkInitClient(){
        if (!WebSocketClient.isConnected()) {
            WebSocketClient.tryConnectServer();
        }
    }

        private void waitForPossibleResponse(int waitTimeS){
        try {
            Thread.sleep(waitTimeS * 1000);
        }
        catch (InterruptedException ignored){

        }
    }


    @Test void testAuthRetry(){
        checkInitClient();
        MessageHandler<?> handler = new ClientChessGameMessageHandler(WebSocketClient.getWebSocketConnection());
        JWTManager m = new JWTManager(handler, () -> {
            System.out.println("Failed to retrieve access token, \"logging out\"");
        });
        AuthenticatedMessageSender sender = new AuthenticatedMessageSender(handler, m);


        sender.sendAuthenticatedRetryableMessage(
                new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.MATCHALLUSERNAMES, new Payload.StringPayload("aaa")))
                        .onDataResponse((UserMessagePayloadTypes.FriendDataResponsePayload fdr) -> System.out.println(fdr) ));


//        waitForPossibleResponse(20);
//        sender.sendAuthenticatedRetryableMessage(
//                new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.MATCHALLUSERNAMES, new Payload.StringPayload("aaa")))
//                        .onDataResponse((UserMessagePayloadTypes.FriendDataResponsePayload fdr) -> System.out.println(fdr) ));

        waitForPossibleResponse(100);
    }

}
