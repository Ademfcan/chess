package chessserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import javax.websocket.Session;
import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClientHandler {
    private static final Logger logger = LogManager.getLogger("Client_Handler");
    private static final Map<Session, BackendClient> clientHashMap = Collections.synchronizedMap(new HashMap<>());
    private static final WaitingPool pool = new WaitingPool();

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void handleMessage(String message, Session session, DataSource dataSource) throws IOException {
        try {
            InputMessage input = objectMapper.readValue(message, InputMessage.class);
            if (input.getIntent().equals(INTENT.CLOSESESS)) {
                System.out.println("Intentionaly closing!");
                session.close();
            }
            else if(input.getIntent().isDbRelated){
                try (Connection conn = dataSource.getConnection()) {
                    switch (input.getIntent()){
                        case GETUSER -> {
                            String[] info = input.getExtraInformation().split(",");
                            String query = "SELECT * FROM users WHERE Username = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, info[0]); // Set username
                            stmt.setString(2, info[1]); // Set password hash
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                sendMessage(session,ServerResponseType.DATARESPONSE,rs.getString("Dataentry"));
                            }
                            sendMessage(session,ServerResponseType.DATARESPONSE,"");
                        }

                        case PUTUSER -> {
                            String[] info = input.getExtraInformation().split(",");
                            String insertQuery = "INSERT INTO users (username, passwordhash, Dataentry) VALUES (?, ?, ?)";
                            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                            pstmt.setString(1, info[0]); // Set the username
                            pstmt.setString(2, info[1]); // Set the password hash
                            pstmt.setString(3, info[2]); // Set the email

                            int rowsInserted = pstmt.executeUpdate(); // Execute the insert
                            if (rowsInserted > 0) {
                                logger.debug("A new user was inserted successfully!");
                            }

                        }
                    }
//
                }
                catch (SQLException sqlException) {
                    sendMessage(session, ServerResponseType.SQLERROR, sqlException.getMessage());
                }
//
            }
            else {
                BackendClient c = getClient(input.getClient(), session);
                switch (input.getIntent()) {
                    case MAKEMOVE -> {
                        String[] info = input.getExtraInformation().split(",");
                        if (c.isInGame()) {
                            c.getCurrentGame().makeMove(info[0], Integer.parseInt(info[1]), c);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "not currently in game, cannot make a move");
                        }
                    }
                    case SENDCHAT -> {
                        // todo
                        if (c.isInGame()) {
                            c.getCurrentGame().sendChat(input.getExtraInformation(), c);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "not currently in game, cannot send a message");
                        }
                    }
                    case CREATEGAME -> {
                        Gametype wantedType = Gametype.getType(input.getExtraInformation());
                        boolean isMatch = pool.matchClient(c, wantedType);
                        if (!isMatch) {
                            // match not found
                            sendMessage(c.getClientSession(), ServerResponseType.INWAITINGPOOL, Integer.toString(pool.getWaitingClientsOfTypeCount(c, wantedType, 1000)));

                        }

                    }
                    case LEAVEGAME -> {
                        if (c.isInGame()) {
                            c.endGame(false, false, true);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "cannot leave game as not currently in a game");
                        }
                    }
                    case GETNUMBEROFPOOLERS -> {
                        Gametype wantedType = Gametype.getType(input.getExtraInformation());
                        sendMessage(c.getClientSession(), ServerResponseType.NUMBEROFPOOLERS, Integer.toString(pool.getWaitingClientsOfTypeCount(c, wantedType, 1000)));
                    }
                    case PULLTOTALPLAYERCOUNT ->
                            sendMessage(c.getClientSession(), ServerResponseType.TOTALPLAYERCOUNT, Integer.toString(pool.getPoolCount()));
                    case GAMEFINISHED -> {
                        if (c.isInGame()) {
                            String[] split = input.getExtraInformation().split(",");
                            boolean isClientWinner = Boolean.parseBoolean(split[0]);
                            boolean isDraw = Boolean.parseBoolean(split[1]);
                            c.endGame(isClientWinner, isDraw, false);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "cannot send checkmate as not currently in a game");
                        }
                    }
                }


            }
        } catch (Exception e) {
            sendMessage(session, ServerResponseType.INVALIDOPERATION, e.getMessage());

        }
    }

    private static BackendClient getClient(FrontendClient c, Session session) {
        BackendClient bc = clientHashMap.getOrDefault(session, null);
        if (Objects.isNull(bc)) {
            // first init
            bc = c.createBackend(session);
            clientHashMap.put(session, bc);

        }
        return bc;
    }

    public static void sendMessage(Session session, ServerResponseType serverResponseType, String extraInfo) {
        try {
            // Send the message to the client associated with this session
            String message = objectMapper.writeValueAsString(new OutputMessage(serverResponseType, extraInfo));
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that may occur during message sending
        }
    }


    public static void handleClosure(Session session) {
        BackendClient c = clientHashMap.getOrDefault(session, null);
        if (Objects.nonNull(c)) {
            if (c.isInGame()) {
                c.getCurrentGame().closeGame(c, false, false, true);
            }
        }
        clientHashMap.remove(session);


    }
}
