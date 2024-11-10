package chessserver.ClientHandling;

import chessserver.Enums.Gametype;
import chessserver.ServerChessGame;
import chessserver.User.BackendClient;

import java.util.*;
import java.util.stream.Collectors;

public class WaitingPool {
    private final int maxEloDiff = 800;
    private final Map<Gametype, Set<BackendClient>> waitingClients;

    public WaitingPool() {
        waitingClients = Collections.synchronizedMap(new HashMap<>());
        waitingClients.put(Gametype.REGULARUNLIMITED, Collections.synchronizedSet(new TreeSet<>()));
        waitingClients.put(Gametype.REGULAR30, Collections.synchronizedSet(new TreeSet<>()));
        waitingClients.put(Gametype.REGULAR10, Collections.synchronizedSet(new TreeSet<>()));
    }

    public List<BackendClient> getWaitingClientsOfType(BackendClient currentClient, Gametype wantedGametype, int maxEloDiff) {
        return waitingClients.get(wantedGametype).stream().filter(c -> Math.abs((c.getInfo().getUserelo()) - currentClient.getInfo().getUserelo()) <= maxEloDiff).collect(Collectors.toList());
    }

    public int getWaitingClientsOfTypeCount(BackendClient currentClient, Gametype wantedGametype, int maxEloDiff) {
        return (int) waitingClients.get(wantedGametype).stream().filter(c -> Math.abs((c.getInfo().getUserelo()) - currentClient.getInfo().getUserelo()) <= maxEloDiff).count();
    }

    public int getPoolCount() {
        int count = 0;
        for (Gametype g : Gametype.values()) {
            count += waitingClients.get(g).size();
        }
        return count;
    }

    public boolean matchClient(BackendClient newClient, Gametype wantedGametype) {
        BackendClient bestMatch = findBestMatch(newClient, wantedGametype);
        if (Objects.isNull(bestMatch)) {
            waitingClients.get(wantedGametype).add(newClient);
            return false;
        } else {
            new ServerChessGame(newClient, bestMatch, true, wantedGametype);
            waitingClients.get(wantedGametype).remove(bestMatch);
            return true;
        }

    }

    private BackendClient findBestMatch(BackendClient newClient, Gametype wantedGametype) {
        Set<BackendClient> clients = waitingClients.get(wantedGametype);
        if (clients.isEmpty()) {
            return null;
        } else {
            for (BackendClient c : clients) {
                if ((Math.abs(c.getInfo().getUserelo()) - newClient.getInfo().getUserelo()) <= maxEloDiff) {
                    return c;
                }
            }
        }
        return null;
    }

    public void tryRemoveClient(BackendClient c) {
        for(Set<BackendClient> clients : waitingClients.values()){
            clients.remove(c);
        }
    }
}
