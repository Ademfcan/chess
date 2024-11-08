package chessserver.ClientHandling;

import chessserver.Enums.Gametype;
import chessserver.Enums.INTENT;
import chessserver.Enums.ServerResponseType;
import chessserver.Friends.FriendDataPair;
import chessserver.Friends.FriendDataResponse;
import chessserver.User.FrontendClient;
import chessserver.Communication.InputMessage;
import chessserver.Communication.OutputMessage;
import chessserver.Communication.DatabaseRequest;
import chessserver.User.BackendClient;
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
            if(message.startsWith("test:")){
                int testUUID = Integer.parseInt(message.split("test:")[1].trim());
                Session testSession = UUIDSessionMap.get(testUUID);
                if(testSession != null){
                    sendMessage(session, ServerResponseType.INVALIDOPERATION,testSession.getId() + "\n" + clientHashMap.get(testSession).toString(),Integer.MAX_VALUE);
                }
                else{
                    sendMessage(session,ServerResponseType.INVALIDOPERATION,"None",Integer.MAX_VALUE);
                }
                return;
            }

            InputMessage input = objectMapper.readValue(message, InputMessage.class);
            BackendClient c = getClient(input.getClient(), session);
            if (input.getIntent().equals(INTENT.CLOSESESS)) {
                System.out.println("Intentionaly closing!");
                session.close();
            } else if (input.getIntent().isDbRelated) {
                ArrayList<PreparedStatement> openedStatements = new ArrayList<>();
                ArrayList<ResultSet> openedResultSets = new ArrayList<>();
                try (Connection conn = dataSource.getConnection()) {
                    switch (input.getIntent()) {
                        case GETUSER -> {
                            String[] info = input.getExtraInformation().split(",");
                            String query = "SELECT * FROM users WHERE Username = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1, info[0]); // Set username
                            stmt.setString(2, info[1]); // Set password hash
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            if (rs.next()) {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, rs.getString("Dataentry"), input.getUniqueId());
                            } else {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, "", input.getUniqueId());
                            }
                            stmt.close();
                            rs.close();
                        }

                        case PUTUSER -> {
                            DatabaseRequest request = objectMapper.readValue(input.getExtraInformation(), DatabaseRequest.class);
                            String insertQuery = "INSERT INTO users (Username, Passwordhash, UUID, Dataentry,lastTimeStamp,elo) VALUES (?, ?, ?, ?, ?,?)";
                            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                            openedStatements.add(pstmt);
                            pstmt.setString(1, request.getUserName()); // Set the username
                            pstmt.setString(2, request.getPasswordHash()); // Set the password hash
                            pstmt.setString(3, Integer.toString(request.getUUID())); // username
                            pstmt.setString(4, request.getEntry()); // Set the data
                            pstmt.setString(5, String.valueOf(request.getRequestTimeStampMS())); // Set the data
                            pstmt.setString(6, Integer.toString(request.getCurrentElo())); // username

                            int rowsInserted = pstmt.executeUpdate(); // Execute the insert
                            if (rowsInserted > 0) {
                                logger.debug("A new user was inserted successfully!");
                            }
                            pstmt.close();

                            // also increment uuid

                            String query = "UPDATE UUID SET currentUUID = currentUUID + 1";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();

                        }

                        case UPDATEUSER -> {
                            DatabaseRequest request = objectMapper.readValue(input.getExtraInformation(), DatabaseRequest.class);
                            String query = "Update users Set Dataentry = ?, lastTimeStamp = ?, elo = ? WHERE UUID = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1, request.getEntry()); // set data
                            stmt.setString(2, String.valueOf(request.getRequestTimeStampMS())); // set data
                            stmt.setString(3, Integer.toString(request.getCurrentElo())); // username
                            stmt.setString(4, Integer.toString(request.getUUID())); // username
                            stmt.setString(5, request.getPasswordHash()); // passwordhash
                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();

                        }

                        case DELETEUSER -> {

                            String[] info = input.getExtraInformation().split(",");
                            String query = "DELETE from users WHERE UUID = ? AND Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
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
                            openedStatements.add(stmt);
                            stmt.setString(1, userName); // username
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            if (!rs.next()) {
                                String uuidQuery = "SELECT * from UUID";
                                PreparedStatement uuidStmt = conn.prepareStatement(uuidQuery);
                                openedStatements.add(uuidStmt);
                                ResultSet uuidRs = uuidStmt.executeQuery();
                                openedResultSets.add(uuidRs);
                                if(uuidRs.next()){
                                    sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, uuidRs.getString("currentUUID"), input.getUniqueId());
                                }
                                else{
                                    sendMessage(session,ServerResponseType.INVALIDOPERATION,"Major error! UUID should never be null", Integer.MAX_VALUE);
                                }
                                uuidStmt.close();
                                uuidRs.close();
                            } else {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, "", input.getUniqueId());
                            }
                            stmt.close();
                            rs.close();
                        }

                        case GetCurrentUUID -> {
                            String query = "SELECT * from UUID";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
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
                            openedStatements.add(stmt);
                            if (stmt.executeUpdate() > 0) {
                                sendMessage(session, ServerResponseType.SQLSUCESS, "", input.getUniqueId());
                            }
                            stmt.close();
                        }

                        case SENDFRIENDREQUEST -> {
                            String incomingUserName = input.getExtraInformation();
                            String query = "SELECT UUID from users Where Username = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1, incomingUserName); // username
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            if (rs.next()) {
                                int incomingUUID = Integer.parseInt(rs.getString("UUID"));
                                String requesterUUID = Integer.toString(input.getClient().getInfo().getUuid());
                                String requesterUsername = input.getClient().getInfo().getUserName();
                                if (UUIDSessionMap.containsKey(incomingUUID)) {
                                    // requested friend is online
                                    sendMessage(UUIDSessionMap.get(incomingUUID), ServerResponseType.INCOMINGFRIENDREQUEST, requesterUsername + "," + requesterUUID, Integer.MAX_VALUE);
                                } else {
                                    // not online so add to sql requests
                                    String addQuery = "Update users Set Incomingrequests = CONCAT(IFNULL(Incomingrequests, ''), ?) WHERE UUID = ?";
                                    PreparedStatement addStmt = conn.prepareStatement(addQuery);
                                    openedStatements.add(addStmt);
                                    addStmt.setString(1, requesterUsername + "," + requesterUUID + ";");
                                    addStmt.setString(2, Integer.toString(incomingUUID));
                                    addStmt.executeUpdate();
                                    addStmt.close();
                                }
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,Integer.toString(incomingUUID), input.getUniqueId());
                            } else {
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, "", input.getUniqueId());
                            }
                            stmt.close();
                            rs.close();


                        }

                        case READINCOMINGFRIENDREQUESTS -> {
                            String[] split = input.getExtraInformation().split(",");
                            String query = "Select Incomingrequests from users Where UUID = ? And Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1,split[0]);
                            stmt.setString(2,split[1]);
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            if(rs.next()){
                                String requests = rs.getString("Incomingrequests");
                                if(requests != null){
                                    sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,requests,input.getUniqueId());

                                    // now also clear the requests

                                    String clearQuery = "Update users Set Incomingrequests = '' Where UUID = ? And Passwordhash = ?";
                                    PreparedStatement clearStmt = conn.prepareStatement(clearQuery);
                                    clearStmt.setString(1,split[0]);
                                    clearStmt.setString(2,split[1]);
                                    openedStatements.add(clearStmt);
                                    if(clearStmt.executeUpdate() > 0){
                                        sendMessage(session,ServerResponseType.SQLSUCESS,"cleared requests",Integer.MAX_VALUE);
                                    }
                                    else{
                                        sendMessage(session,ServerResponseType.SQLERROR,"failed to clear requests",Integer.MAX_VALUE);
                                    }

                                    clearStmt.close();
                                }
                                else{
                                    sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"",input.getUniqueId());
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
                                openedStatements.add(stmt);
                                stmt.setString(1, username); // username
                                ResultSet rs = stmt.executeQuery();
                                openedResultSets.add(rs);
                                if (rs.next()) {
                                    uuidResponses.append(rs.getString("UUID"));

                                }
                                uuidResponses.append(",");
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
                                openedStatements.add(stmt);
                                stmt.setString(1, uuid); // username
                                ResultSet rs = stmt.executeQuery();
                                openedResultSets.add(rs);
                                if (rs.next()) {
                                    usernameResponses.append(rs.getString("Username"));
                                }
                                usernameResponses.append(",");
                                rs.close();
                                stmt.close();
                            }
                            sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, usernameResponses.toString(), input.getUniqueId());
                        }

                        case GETFRIENDDATA ->{
                            FriendDataResponse dataResponse = new FriendDataResponse(new ArrayList<>());
                            String[] uuids = input.getExtraInformation().split(",");
                            for (String uuid : uuids) {
                                int UUID = Integer.parseInt(uuid);
                                String query = "SELECT Dataentry from users Where UUID = ?";
                                PreparedStatement stmt = conn.prepareStatement(query);
                                openedStatements.add(stmt);
                                stmt.setString(1, uuid); // username
                                ResultSet rs = stmt.executeQuery();
                                openedResultSets.add(rs);
                                if (rs.next()) {
                                    boolean isOnline = UUIDSessionMap.containsKey(UUID);
                                    dataResponse.getDataResponse().add(new FriendDataPair(UUID,rs.getString("Dataentry"),isOnline));
                                }
                                else{
                                    dataResponse.getDataResponse().add(new FriendDataPair(UUID,"",false));
                                }
                                rs.close();
                                stmt.close();
                            }
                            sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, objectMapper.writeValueAsString(dataResponse), input.getUniqueId());
                        }

                        case GETRANK ->{
                            String UUID = input.getExtraInformation();
                            String query = "SELECT rank FROM (SELECT UUID, elo, RANK() OVER (ORDER BY elo DESC) AS rank FROM users) ranked WHERE UUID = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1,UUID);
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            String rank = "";
                            if(rs.next()){
                                rank = Integer.toString(rs.getInt("rank"));
                            }
                            rs.close();
                            stmt.close();
                            sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST,rank, input.getUniqueId());
                        }

                        case MATCHALLUSERNAMES -> {
                            String usernameSnippet = input.getExtraInformation();
                            String query = "SELECT Dataentry,UUID from users where Username like ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1,"%" + usernameSnippet + "%");
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            FriendDataResponse dataResponse = new FriendDataResponse(new ArrayList<>());
                            if(rs.next()){
                                while (rs.next()){
                                    int UUID = Integer.parseInt(rs.getString("UUID"));
                                    String dataEntryAsString = rs.getString("Dataentry");
                                    boolean isOnline = UUIDSessionMap.containsKey(UUID);
                                    dataResponse.getDataResponse().add(new FriendDataPair(UUID,dataEntryAsString,isOnline));
                                }
                                sendMessage(session, ServerResponseType.SERVERRESPONSEACTIONREQUEST, objectMapper.writeValueAsString(dataResponse), input.getUniqueId());
                            }
                            else{
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,objectMapper.writeValueAsString(dataResponse), input.getUniqueId());
                            }
                            rs.close();
                            stmt.close();
                        }

                        case SENDACCEPTEDFRIENDREQUEST -> {
                            int acceptedUUID = Integer.parseInt(input.getExtraInformation());
                            String requesterUUID = Integer.toString(input.getClient().getInfo().getUuid());
                            String requesterUsername = input.getClient().getInfo().getUserName();
                            String findQuery = "Select * from users Where UUID = ?";
                            PreparedStatement findStmt = conn.prepareStatement(findQuery);
                            openedStatements.add(findStmt);
                            findStmt.setString(1,Integer.toString(acceptedUUID));
                            ResultSet rs = findStmt.executeQuery();
                            openedResultSets.add(rs);
                            if(rs.next()){
                                if (UUIDSessionMap.containsKey(acceptedUUID)) {
                                    // requested friend is online
                                    sendMessage(UUIDSessionMap.get(acceptedUUID), ServerResponseType.ACCEPTEDFRIENDREQUEST, requesterUsername + "," + requesterUUID, Integer.MAX_VALUE);
                                } else {
                                    // not online so add to sql requests
                                    String addQuery = "Update users Set Acceptedrequests = CONCAT(IFNULL(Acceptedrequests, ''), ?) WHERE UUID = ?";
                                    PreparedStatement addStmt = conn.prepareStatement(addQuery);
                                    openedStatements.add(addStmt);
                                    addStmt.setString(1, requesterUsername + "," + requesterUUID + ";");
                                    addStmt.setString(2, Integer.toString(acceptedUUID));
                                    addStmt.executeUpdate();
                                    addStmt.close();
                                }
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"true",input.getUniqueId());
                            }
                            else{
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"false",input.getUniqueId());
                            }
                            findStmt.close();
                            rs.close();

                        }

                        case READACCEPTEDFRIENDREQUESTS ->{
                            String[] split = input.getExtraInformation().split(",");
                            String query = "Select Acceptedrequests from users Where UUID = ? And Passwordhash = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            openedStatements.add(stmt);
                            stmt.setString(1,split[0]);
                            stmt.setString(2,split[1]);
                            ResultSet rs = stmt.executeQuery();
                            openedResultSets.add(rs);
                            if(rs.next()){
                                String requests = rs.getString("Acceptedrequests");
                                if(requests != null){
                                    sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,requests,input.getUniqueId());

                                    // now also clear the requests

                                    String clearQuery = "Update users Set Acceptedrequests = '' Where UUID = ? And Passwordhash = ?";
                                    PreparedStatement clearStmt = conn.prepareStatement(clearQuery);
                                    clearStmt.setString(1,split[0]);
                                    clearStmt.setString(2,split[1]);
                                    openedStatements.add(clearStmt);
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


                            }
                            else{
                                sendMessage(session,ServerResponseType.SERVERRESPONSEACTIONREQUEST,"",input.getUniqueId());
                            }

                            rs.close();
                            stmt.close();
                        }
                    }
//
                } catch (SQLException sqlException) {
                    sendMessage(session, ServerResponseType.SQLERROR, "Error thrown from intent: " + input.getIntent() + "\n" + sqlException.getMessage(), Integer.MAX_VALUE);
                    logger.error("Sql Error",sqlException);
                }
                finally {
                    try {
                        // if an error was thrown before these guys could close, then cleanup
                        for(PreparedStatement stmt: openedStatements){
                            if(!stmt.isClosed()){
                                stmt.close();
                            }
                        }

                        for(ResultSet rs: openedResultSets){
                            if(!rs.isClosed()){
                                rs.close();
                            }
                        }
                    }
                    catch (SQLException sqlException){
                        logger.error("Error closing statements!",sqlException);
                    }
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
                        sendMessage(c.getClientSession(), ServerResponseType.SERVERRESPONSEACTIONREQUEST, Integer.toString(-1), input.getUniqueId());

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
            logger.error("General error handling message",e);

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
            logger.error("Error sending message",e);
            // Handle any errors that may occur during message sending
        }
    }


    public static void handleClosure(Session session) {
        BackendClient c = clientHashMap.getOrDefault(session, null);
        if (Objects.nonNull(c)) {
            if (c.isInGame()) {
                c.getCurrentGame().closeGame(c, false, false, true);
            }
            UUIDSessionMap.remove(c.getInfo().getUuid());
        }
        clientHashMap.remove(session);

        sendMessage(session,ServerResponseType.CLOSEDSUCESS,"",Integer.MAX_VALUE);


    }
}
