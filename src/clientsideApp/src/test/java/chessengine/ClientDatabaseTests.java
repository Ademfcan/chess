//package chessengine;
//
//import chessengine.Crypto.DeviceId;
//import chessengine.Crypto.TokenStore;
//import chessengine.Net.ClientMessageHandlers.ClientDatabaseMessageHandler;
//import chessengine.Net.ClientMessageHandlers.ClientUserMessageHandler;
//import chessengine.Net.WebSocketClient;
//import chessserver.JWTUtil;
//import chessserver.Net.AuthenticationPayload;
//import chessserver.Net.MessageHandler;
//import chessserver.Net.MessageTypes.DatabaseMessageTypes;
//import chessserver.Net.MessageTypes.UserMessageTypes;
//import chessserver.Net.Payload;
//import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
//import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
//import org.junit.jupiter.api.Test;
//
//public class ClientDatabaseTests {
//    private void checkInitClient(){
//        if (!WebSocketClient.isConnected()) {
//            WebSocketClient.tryConnectServer();
//        }
//    }
//
//    private void waitForPossibleResponse(int waitTimeS){
//        try {
//            Thread.sleep(waitTimeS * 1000);
//        }
//        catch (InterruptedException ignored){
//
//        }
//    }
//
//
//    @Test
//    void testWebsocketConnection(){
//        checkInitClient();
//        assert WebSocketClient.isConnected();
//    }
//
//    @Test
//    void testPutUser(){
//        checkInitClient();
//        ClientDatabaseMessageHandler messageHandler = new ClientDatabaseMessageHandler(WebSocketClient.getWebSocketConnection());
//        messageHandler.sendMessage(UserMessageTypes.ClientRequest.SIGNUP,
//                new UserMessagePayloadTypes.SignUpPayload("test", "test@gmail.com", "password", DeviceId.DEVICE_ID),
//                new AuthenticationPayload.EmptyAuthentication(),
//        (Payload.BooleanPayload sucess) -> System.out.println(sucess.payload()), null);
//
//
//        waitForPossibleResponse(10);
//
//    }
//
//    @Test
//    void testLoginUser(){
//        checkInitClient();
//        ClientDatabaseMessageHandler messageHandler = new ClientDatabaseMessageHandler(WebSocketClient.getWebSocketConnection());
//        messageHandler.sendMessage(UserMessageTypes.ClientRequest.LOGIN,
//                new UserMessagePayloadTypes.LoginPayload("test", "password", DeviceId.DEVICE_ID),
//                new AuthenticationPayload.EmptyAuthentication(),
//        (UserMessagePayloadTypes.RefreshAcessTokenPayload refreshToken) -> {
//            if (refreshToken.refreshToken() != null) {
//                System.out.println("Login successful, refresh token: " + refreshToken.refreshToken());
//                TokenStore.setRefreshToken(refreshToken.refreshToken());
//            } else {
//                System.out.println("Login failed.");
//            }
//        }, null);
//
//        waitForPossibleResponse(10);
//    }
//
//    @Test
//    void testRefreshTokenValid(){
//        checkInitClient();
//
//        ClientDatabaseMessageHandler messageHandler = new ClientDatabaseMessageHandler(WebSocketClient.getWebSocketConnection());
//
//        messageHandler.sendMessage(UserMessageTypes.ClientRequest.REFRESHVALID,
//                new UserMessagePayloadTypes.RefreshAcessTokenPayload(TokenStore.getRefreshToken(), DeviceId.DEVICE_ID),
//                new AuthenticationPayload.EmptyAuthentication(), (Payload.BooleanPayload isValid) -> {;
//                    if (isValid.payload()) {
//                        System.out.println("Refresh token is valid.");
//                    } else {
//                        System.out.println("Refresh token is invalid.");
//                    }
//                }, null);
//
//        waitForPossibleResponse(10);
//
//    }
//
//    @Test
//    void getAcessTokenFromRefreshToken() {
//        checkInitClient();
//
//        ClientUserMessageHandler messageHandler = new ClientUserMessageHandler(WebSocketClient.getWebSocketConnection());
//        messageHandler.sendMessage(UserMessageTypes.ClientRequest.GETJWTTOKEN, new UserMessagePayloadTypes.RefreshAcessTokenPayload(TokenStore.getRefreshToken(), "deviceID"),
//                new AuthenticationPayload.EmptyAuthentication(),
//                (UserMessagePayloadTypes.JWTAcessTokenPayload jwtToken) -> {
//                    if (jwtToken.JwtToken() != null) {
//                        System.out.println("JWT Access Token: " + jwtToken.JwtToken());
//                    } else {
//                        System.out.println("Failed to retrieve JWT Access Token.");
//                    }
//                }, null);
//
//        waitForPossibleResponse(10);
//
//    }
//
//    @Test
//    void checkAccessTokenValidity() {
//        String acessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJDaGVzcy1hcHAiLCJzdWIiOiI2ZDk4Y2FiYy02OTc0LTQwY2UtYWI1Yi1hYTljYTJlN2NhZjYiLCJpYXQiOjE3NTE1OTM5MzEsImV4cCI6MTc1MTU5NTczMX0.zCciyDzoWM8GyiLoW5maXPUoV3I51b3RZBWfLS3FZUk";
//        System.out.println(JWTUtil.isExpired(acessToken));
//        System.out.println(JWTUtil.getTokenUUID(acessToken));
//    }
//
//
//
//    @Test
//    void testGetUser(){
//        checkInitClient();
//        ClientUserMessageHandler messageHandlerUser = new ClientUserMessageHandler(WebSocketClient.getWebSocketConnection());
//        ClientDatabaseMessageHandler messageHandlerDatabase = new ClientDatabaseMessageHandler(WebSocketClient.getWebSocketConnection());
//
//        messageHandlerUser.getJWTToken(TokenStore.getRefreshToken(), (token) -> {
//            if (token.isPresent()) {
//                messageHandlerDatabase.sendMessage(DatabaseMessageTypes.ClientRequest.GETUSER,
//                        new Payload.Empty(),
//                        new AuthenticationPayload.JWTAuthentication(token.get(), "deviceID"),
//                        (DatabaseMessagePayloadTypes.UserAsStrPayload response) -> {
//                            if (response != null) {
//                                System.out.println("User Info: " + response.userInfoStr());
//                                System.out.println("User Preferences: " + response.userPrefStr());
//                            } else {
//                                System.out.println("Failed to retrieve user information.");
//                            }
//                        }, null);
//            } else {
//                System.out.println("Failed to retrieve JWT Token.");
//            }
//        });
//
//        waitForPossibleResponse(10);
//
//    }
//
//
//    @Test void testPrint(){
//        checkInitClient();
//        ClientUserMessageHandler messageHandlerUser = new ClientUserMessageHandler(WebSocketClient.getWebSocketConnection());
//        messageHandlerUser.sendMessage(MessageHandler.GenericServerResponses.PRINT_MESSAGE,
//                new Payload.StringPayload("Hello, this is a test message!"),
//                null, null);
//
//        waitForPossibleResponse(10);
//    }
//
//    @Test void testAuthInvalid(){
//        checkInitClient();
//        ClientUserMessageHandler messageHandlerUser = new ClientUserMessageHandler(WebSocketClient.getWebSocketConnection());
//        messageHandlerUser.sendMessage(DatabaseMessageTypes.ClientRequest.MATCHALLUSERNAMES,
//                new Payload.StringPayload("test"),
//                (UserMessagePayloadTypes.FriendDataResponsePayload rsps) -> System.out.println(rsps), null);
//
//        waitForPossibleResponse(10);
//    }
//}
