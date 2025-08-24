package chessserver.ClientHandling;

import chessserver.Enums.Gametype;
import chessserver.ServerChessGame;
import chessserver.User.BackendClient;

import java.util.*;

public class WaitingPool {
    private final int maxEloDiff = 800;
    private final Map<Gametype, Map<Integer, BackendClient>> waitingClients;

    public WaitingPool() {
        waitingClients = Collections.synchronizedMap(new HashMap<>());
        waitingClients.put(Gametype.REGULARUNLIMITED, Collections.synchronizedMap(new TreeMap<>()));
        waitingClients.put(Gametype.REGULAR30, Collections.synchronizedMap(new TreeMap<>()));
        waitingClients.put(Gametype.REGULAR10, Collections.synchronizedMap(new TreeMap<>()));
    }

    private List<BackendClient> getWaitingClientsOfType(BackendClient currentClient, Gametype wantedGametype, int maxEloDiff) {
        TreeMap<Integer, BackendClient> clients = (TreeMap<Integer, BackendClient>) waitingClients.get(wantedGametype);

        if (clients.isEmpty()) {
            return Collections.emptyList();
        }

        int lower = currentClient.getClientInfo().getUserelo() - maxEloDiff;
        int upper = currentClient.getClientInfo().getUserelo() + maxEloDiff;
        return new ArrayList<>(clients.subMap(lower, true, upper, true).values());
    }

    public int getValidWaitingClientsOfTypeCount(BackendClient currentClient, Gametype wantedGametype) {
        return getWaitingClientsOfType(currentClient, wantedGametype, maxEloDiff).size();
    }

    public int getTotalPoolCount() {
        int count = 0;
        for (Gametype g : Gametype.values()) {
            count += waitingClients.get(g).size();
        }
        return count;
    }

    public void matchClient(BackendClient newClient, Gametype wantedGametype) {
        BackendClient bestMatch = findNearestMatch(newClient, wantedGametype);

        if (Objects.isNull(bestMatch)) {
            waitingClients.get(wantedGametype).put(newClient.getClientInfo().getUserelo(), newClient);
        } else {
            new ServerChessGame(newClient, bestMatch, true, wantedGametype);
        }
    }

    private BackendClient findNearestMatch(BackendClient newClient, Gametype wantedGametype) {
        // closest match within maxEloDiff
        TreeMap<Integer, BackendClient> clients = (TreeMap<Integer, BackendClient>) waitingClients.get(wantedGametype);

        if (clients.isEmpty()) {
            return null;
        }

        int elo = newClient.getClientInfo().getUserelo();
        Integer floor = clients.floorKey(elo);
        Integer ceil = clients.ceilingKey(elo);

        if (floor != null && ceil == null) return clients.remove(floor);
        if (ceil != null && floor == null) return clients.remove(ceil);

        // Choose whichever is closer to elo
        return (elo - floor <= ceil - elo) ? clients.remove(floor) : clients.remove(ceil);
    }

    public boolean tryRemoveClient(BackendClient c) {
        boolean wasRemoved = false;
        for(Map<Integer, BackendClient> clients : waitingClients.values()){
            wasRemoved |= clients.remove(c.getClientInfo().getUserelo(), c);
        }
        return wasRemoved;
    }
}
