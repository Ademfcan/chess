package chessserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

public class ClientHandler {
    private static Map<Session,BackendClient> clientHashMap = Collections.synchronizedMap(new HashMap<>());
    private static WaitingPool pool = new WaitingPool();

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static void handleMessage(String message,Session session) throws IOException {
        try{
            InputMessage input = objectMapper.readValue(message,InputMessage.class);
            if(input.getIntent().equals(INTENT.CLOSESESS)){
                System.out.println("Intentionaly closing!");
                session.close();
            }
            else{
                BackendClient c = getClient(input.getClient(),session);
                switch (input.getIntent()){
                    case MAKEMOVE -> {
                        String[] info = input.getExtraInformation().split(",");
                        if(c.isInGame()){
                            c.getCurrentGame().makeMove(info[0],Integer.parseInt(info[1]),c);
                        }
                        else{
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION,"not currently in game, cannot make a move");
                        }
                    }
                    case SENDCHAT -> {
                        // todo
                        if(c.isInGame()){
                            c.getCurrentGame().sendChat(input.getExtraInformation(),c);
                        }
                        else{
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION,"not currently in game, cannot send a message");
                        }
                    }
                    case CREATEGAME -> {
                        Gametype wantedType = Gametype.getType(input.getExtraInformation());
                        boolean isMatch = pool.matchClient(c,wantedType);
                        if(!isMatch){
                            // match not found
                            sendMessage(c.getClientSession(), ServerResponseType.INWAITINGPOOL,Integer.toString(pool.getWaitingClientsOfTypeCount(c,wantedType,1000)));

                        }

                    }
                    case LEAVEGAME -> {
                        if(c.isInGame()){
                             c.endGame(false,false,true);
                        }
                        else{
                           sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION,"cannot leave game as not currently in a game");
                        }
                    }
                    case GETNUMBEROFPOOLERS -> {
                        Gametype wantedType = Gametype.getType(input.getExtraInformation());
                        sendMessage(c.getClientSession(),ServerResponseType.NUMBEROFPOOLERS,Integer.toString(pool.getWaitingClientsOfTypeCount(c,wantedType,1000)));
                    }
                    case PULLTOTALPLAYERCOUNT -> sendMessage(c.getClientSession(),ServerResponseType.TOTALPLAYERCOUNT,Integer.toString(pool.getPoolCount()));
                    case GAMEFINISHED -> {
                        if(c.isInGame()){
                            String[] split = input.getExtraInformation().split(",");
                            boolean isClientWinner = Boolean.parseBoolean(split[0]);
                            boolean isDraw = Boolean.parseBoolean(split[1]);
                            c.endGame(isClientWinner,isDraw,false);
                        }
                        else{
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION,"cannot send checkmate as not currently in a game");
                        }
                    }
                }


            }
        }
        catch(Exception e){
            sendMessage(session, ServerResponseType.INVALIDOPERATION,e.getMessage());

        }
    }

    private static BackendClient getClient(FrontendClient c,Session session){
        BackendClient bc = clientHashMap.getOrDefault(session, null);
        if(Objects.isNull(bc)){
            // first init
            bc = c.createBackend(session);
            clientHashMap.put(session,bc);

        }
        return bc;
    }

    public static void sendMessage(Session session, ServerResponseType serverResponseType,String extraInfo) {
        try {
            // Send the message to the client associated with this session
            String message = objectMapper.writeValueAsString(new OutputMessage(serverResponseType,extraInfo));
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that may occur during message sending
        }
    }


    public static void handleClosure(Session session) {
        BackendClient c = clientHashMap.getOrDefault(session, null);
        if(Objects.nonNull(c)){
            if(c.isInGame()) {
                c.getCurrentGame().closeGame(c, false, false, true);
            }
        }
        clientHashMap.remove(session);


    }
}
