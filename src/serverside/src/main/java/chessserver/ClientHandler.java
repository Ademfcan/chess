package chessserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import javax.websocket.Session;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ClientHandler {
    private static final Logger logger = LogManager.getLogger("Client_Handler");
    private static final Map<Session, BackendClient> clientHashMap = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Integer, Session> UUIDSessionMap = Collections.synchronizedMap(new HashMap<>());
    private static final WaitingPool pool = new WaitingPool();

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void handleMessage(String message, Session session, DataSource dataSource) throws IOException {
        try {
            InputMessage input = objectMapper.readValue(message, InputMessage.class);
            BackendClient c = getClient(input.getClient(), session);
            if (input.getIntent().equals(INTENT.CLOSESESS)) {
                System.out.println("Intentionaly closing!");
                session.close();
            } else if (input.getIntent().isDbRelated) {
                try (Connection conn = dataSource.getConnection()) {
                    switch (input.getIntent()) {
                        case GETUSER -> {
                            String[] info = input.getExtraInformation().split(",");
                            String query = "SELECT * FROM users WHERE Username = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, info[0]); // Set username
                            stmt.setString(2, info[1]); // Set password hash
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, rs.getString("Dataentry"), input.getUniqueId());
                            } else {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, "", input.getUniqueId());
                            }
                            rs.close();
                            stmt.close();
                        }

                        case PUTUSER -> {
                            DatabaseRequest request = objectMapper.readValue(input.getExtraInformation(), DatabaseRequest.class);
                            String insertQuery = "INSERT INTO users (Username, Passwordhash, UUID, Dataentry,lastTimeStamp) VALUES (?, ?, ?, ?, ?)";
                            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                            pstmt.setString(1, request.getUserName()); // Set the username
                            pstmt.setString(2, request.getPasswordHash()); // Set the password hash
                            pstmt.setString(3, Integer.toString(request.getUUID())); // username
                            pstmt.setString(4, request.getEntry()); // Set the data
                            pstmt.setString(5, String.valueOf(request.getRequestTimeStampMS())); // Set the data

                            int rowsInserted = pstmt.executeUpdate(); // Execute the insert
                            if (rowsInserted > 0) {
                                logger.debug("A new user was inserted successfully!");
                            }
                            pstmt.close();

                            // also increment uuid

                            String query = "UPDATE UUID SET currentUUID = currentUUID + 1";
                            PreparedStatement stmt = conn.prepareStatement(query);

                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();

                        }

                        case UPDATEUSER -> {
                            DatabaseRequest request = objectMapper.readValue(input.getExtraInformation(), DatabaseRequest.class);
                            String query = "Update users Set Dataentry = ?, lastTimeStamp = ? WHERE UUID = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, request.getEntry()); // set data
                            stmt.setString(2, String.valueOf(request.getRequestTimeStampMS())); // set data
                            stmt.setString(3, Integer.toString(request.getUUID())); // username
                            stmt.setString(4, request.getPasswordHash()); // passwordhash
                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();

                        }

                        case DELETEUSER -> {

                            String[] info = input.getExtraInformation().split(",");
                            String query = "DELETE from users WHERE UUID = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, info[0]); // username
                            stmt.setString(2, info[1]); // passwordhash

                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();

                        }

                        case CHECKUSERNAME -> {
                            String userName = input.getExtraInformation();
                            String query = "SELECT * from users Where Username = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, userName); // username
                            ResultSet rs = stmt.executeQuery();
                            if (!rs.next()) {
                                String uuidQuery = "SELECT * from UUID";
                                PreparedStatement uuidStmt = conn.prepareStatement(uuidQuery);
                                ResultSet uuidRs = uuidStmt.executeQuery();
                                if(uuidRs.next()){
                                    sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, uuidRs.getString("currentUUID"), input.getUniqueId());
                                }
                                else{
                                    sendMessage(session,ServerResponseType.INVALIDOPERATION,"Major error! UUID should never be null", Integer.MAX_VALUE);
                                }
                                uuidRs.close();
                                uuidStmt.close();
                            } else {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, "", input.getUniqueId());
                            }
                            rs.close();
                            stmt.close();
                        }

                        case GetCurrentUUID -> {
                            String query = "SELECT * from UUID";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            ResultSet rs = stmt.executeQuery();
                            if(rs.next()){
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, rs.getString("currentUUID"), input.getUniqueId());
                            }
                            else{
                                sendMessage(session,ServerResponseType.INVALIDOPERATION,"Major error! UUID should never be null", Integer.MAX_VALUE);
                            }
                            stmt.close();
                            rs.close();
                        }

                        case IncrementUUID -> {
                            String query = "UPDATE UUID SET currentUUID = currentUUID + 1";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();
                        }

                        case SENDFRIENDREQUEST -> {
                            String incomingUserName = input.getExtraInformation();
                            String query = "SELECT UUID from users Where Username = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, incomingUserName); // username
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                int incomingUUID = Integer.parseInt(rs.getString("UUID"));
                                String requesterUUID = Integer.toString(input.getClient().getInfo().getUuid());
                                if (UUIDSessionMap.containsKey(incomingUUID)) {
                                    // requested friend is online
                                    sendMessage(UUIDSessionMap.get(incomingUUID), ServerResponseType.INCOMINGFRIENDREQUEST, requesterUUID, input.getUniqueId());
                                } else {
                                    // not online so add to sql requests
                                    String addQuery = "Update users Set Incomingrequests = Incomingrequests + ? WHERE UUID = ?";
                                    PreparedStatement addStmt = conn.prepareStatement(addQuery);
                                    addStmt.setString(1, requesterUUID + ",");
                                    addStmt.setString(2, Integer.toString(incomingUUID));
                                    addStmt.executeUpdate();
                                    addStmt.close();
                                }
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"true", input.getUniqueId());
                            } else {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, "false", input.getUniqueId());
                            }

                            rs.close();
                            stmt.close();

                        }

                        case READINCOMINGFRIENDREQUESTS -> {
                            String[] split = input.getExtraInformation().split(",");
                            String query = "Select Incomingrequests from users Where UUID = ? And Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1,split[0]);
                            stmt.setString(2,split[1]);
                            ResultSet rs = stmt.executeQuery();
                            if(rs.next()){
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,rs.getString("Incomingrequests"),input.getUniqueId());

                                // now also clear the requests

                                String clearQuery = "Update users Set Incomingrequests = '' Where UUID = ? And Passwordhash = ?";
                                PreparedStatement clearStmt = conn.prepareStatement(clearQuery);

                                if(clearStmt.executeUpdate() > 0){
                                    sendMessage(session,ServerResponseType.SQLSUCESS,"cleared requests",Integer.MAX_VALUE);
                                }
                                else{
                                    sendMessage(session,ServerResponseType.SQLERROR,"failed to clear requests",Integer.MAX_VALUE);
                                }


                            }
                            else{
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"",input.getUniqueId());
                            }

                            rs.close();
                            stmt.close();


                        }

                        case GETUUIDS -> {
                            StringBuilder uuidResponses = new StringBuilder();
                            String[] usernames = input.getExtraInformation().split(",");
                            for (String username : usernames) {
                                String query = "SELECT UUID from users Where Username = ?";
                                PreparedStatement stmt = conn.prepareStatement(query);
                                stmt.setString(1, username); // username
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next()) {
                                    uuidResponses.append(rs.getString("UUID"));
                                    uuidResponses.append(",");

                                }
                                stmt.close();
                                rs.close();
                            }
                            sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, uuidResponses.toString(), input.getUniqueId());


                        }

                        case GETUSERNAMES -> {
                            StringBuilder usernameResponses = new StringBuilder();
                            String[] uuids = input.getExtraInformation().split(",");
                            for (String uuid : uuids) {
                                String query = "SELECT Username from users Where UUID = ?";
                                PreparedStatement stmt = conn.prepareStatement(query);
                                stmt.setString(1, uuid); // username
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next()) {
                                    usernameResponses.append(rs.getString("UUID"));
                                    usernameResponses.append(",");

                                }
                                rs.close();
                                stmt.close();
                            }
                            sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, usernameResponses.toString(), input.getUniqueId());
                        }

                        // todo but need database elo param
//                        case GETRANK ->{
//                            String UUID = input.getExtraInformation();
//
//
//                        }

                        case MATCHALLUSERNAMES -> {
                            String usernameSnippet = input.getExtraInformation();
                            String query = "SELECT Username from users where Username like ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1,"%" + usernameSnippet + "%");
                            ResultSet rs = stmt.executeQuery();
                            if(rs.next()){
                                StringBuilder output = new StringBuilder(rs.getString("Username"));
                                while (rs.next()){
                                    output.append(",");
                                    output.append(rs.getString("Username"));
                                }
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, output.toString(), input.getUniqueId());
                            }
                            else{
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"", input.getUniqueId());
                            }
                            rs.close();
                            stmt.close();
                        }
                    }
//
                } catch (SQLException sqlException) {
                    sendMessage(session, ServerResponseType.SQLERROR, sqlException.getMessage(), Integer.MAX_VALUE);
                }
//
            } else {
                switch (input.getIntent()) {
                    case MAKEMOVE -> {
                        String[] info = input.getExtraInformation().split(",");
                        if (c.isInGame()) {
                            c.getCurrentGame().makeMove(info[0], Integer.parseInt(info[1]), c);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "not currently in game, cannot make a move", input.getUniqueId());
                        }
                    }
                    case SENDCHAT -> {
                        // todo
                        if (c.isInGame()) {
                            c.getCurrentGame().sendChat(input.getExtraInformation(), c);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "not currently in game, cannot send a message", input.getUniqueId());
                        }
                    }
                    case CREATEGAME -> {
                        Gametype wantedType = Gametype.getType(input.getExtraInformation());
                        boolean isMatch = pool.matchClient(c, wantedType);
                        if (!isMatch) {
                            // match not found
                            sendMessage(c.getClientSession(), ServerResponseType.SERVERRESPONSEACTIONREQUEST, Integer.toString(pool.getWaitingClientsOfTypeCount(c, wantedType, 1000)), input.getUniqueId());

                        }

                    }
                    case LEAVEGAME -> {
                        if (c.isInGame()) {
                            c.endGame(false, false, true);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "cannot leave game as not currently in a game", input.getUniqueId());
                        }
                    }
                    case GETNUMBEROFPOOLERS -> {
                        Gametype wantedType = Gametype.getType(input.getExtraInformation());
                        sendMessage(c.getClientSession(), ServerResponseType.SERVERRESPONSEACTIONREQUEST, Integer.toString(pool.getWaitingClientsOfTypeCount(c, wantedType, 1000)), input.getUniqueId());
                    }
                    case PULLTOTALPLAYERCOUNT ->
                            sendMessage(c.getClientSession(), ServerResponseType.SERVERRESPONSEACTIONREQUEST, Integer.toString(pool.getPoolCount()), input.getUniqueId());
                    case GAMEFINISHED -> {
                        if (c.isInGame()) {
                            String[] split = input.getExtraInformation().split(",");
                            boolean isClientWinner = Boolean.parseBoolean(split[0]);
                            boolean isDraw = Boolean.parseBoolean(split[1]);
                            c.endGame(isClientWinner, isDraw, false);
                        } else {
                            sendMessage(c.getClientSession(), ServerResponseType.INVALIDOPERATION, "cannot send checkmate as not currently in a game", input.getUniqueId());
                        }
                    }
                }


            }
        } catch (Exception e) {
            sendMessage(session, ServerResponseType.INVALIDOPERATION, e.getMessage(), Integer.MAX_VALUE);

        }
    }

    private static BackendClient getClient(FrontendClient c, Session session) {
        BackendClient bc = clientHashMap.getOrDefault(session, null);
        if (Objects.isNull(bc)) {
            // first init
            bc = c.createBackend(session);
            clientHashMap.put(session, bc);

        }
        UUIDSessionMap.put(bc.getInfo().getUuid(), session);

        return bc;
    }

    public static void sendMessage(Session session, ServerResponseType serverResponseType, String extraInfo, int uniqueId) {
        try {
            // Send the message to the client associated with this session
            String message = objectMapper.writeValueAsString(new OutputMessage(serverResponseType, extraInfo, uniqueId));
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

        UUIDSessionMap.remove(c.getInfo().getUuid());


    }
}
