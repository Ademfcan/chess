package chessserver;

import chessserver.User.BackendClient;
import chessserver.User.UserInfo;
import jakarta.websocket.Session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveClientManager {
    private static final Set<Session> activeSessions = Collections.synchronizedSet(new HashSet<>());

    private static final Map<UUID, Session> loggedInUsersToSession = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Session, UUID> loggedInSessionToUsers = Collections.synchronizedMap(new HashMap<>());

    private static final Map<UUID, BackendClient> clientHashMap = Collections.synchronizedMap(new HashMap<>());

    public static void markActiveSession(Session clientSession) {
        activeSessions.add(clientSession);
    }

    public static void markInactiveSession(Session clientSession) {
        activeSessions.remove(clientSession);
    }

    public static boolean isSessionActive(Session clientSession) {
        return activeSessions.contains(clientSession);
    }


    public static void addLoggedInUser(UUID userUUID, Session clientSession) {
        loggedInUsersToSession.put(userUUID, clientSession);
        loggedInSessionToUsers.put(clientSession, userUUID);
    }

    public static void removeLoggedInUser(UUID userUUID) {
        Session clientSession = loggedInUsersToSession.remove(userUUID);
        if (clientSession != null) {
            loggedInSessionToUsers.remove(clientSession);
        }
    }

    public static void removeLoggedInSession(Session clientSession) {
        UUID userUUID = loggedInSessionToUsers.remove(clientSession);
        if (userUUID != null) {
            loggedInUsersToSession.remove(userUUID);
        }
    }

    public static Session getLoggedInSession(UUID userUUID) {
        return loggedInUsersToSession.get(userUUID);
    }

    public static UUID getLoggedInUser(Session clientSession) {
        return loggedInSessionToUsers.get(clientSession);
    }

    public static boolean isClientActive(UUID userUUID){
        return loggedInUsersToSession.containsKey(userUUID);
    }

    public static int getNumActiveClients() {
        return activeSessions.size();
    }

    private static final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    public static void startHeartbeat(Session session) {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if(session.isOpen()) {
                try {
                    session.getBasicRemote().sendPing(ByteBuffer.wrap(new byte[0]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                markInactiveSession(session);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public static BackendClient getClient(UUID userUUID, Session clientSession) {

        BackendClient backendClient = clientHashMap.getOrDefault(userUUID, null);

        if (Objects.isNull(backendClient)) {
            try{
                UserInfo userInfo = ChessEndpoint.serverUserMessageHandler.getInfoFromUUID(userUUID);
                backendClient = new BackendClient(userUUID, clientSession, userInfo);
                clientHashMap.put(userUUID, backendClient);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return backendClient;
    }
}
