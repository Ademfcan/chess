package chessserver.Friends;

import java.util.UUID;

public class FriendInfo extends Friend {
    int numWins;
    int numLosses;
    int numDraws;

    public FriendInfo(UUID uuid, String currentUsername, int numWins, int numLosses, int numDraws) {
        super(currentUsername, uuid);
        this.numWins = numWins;
        this.numLosses = numLosses;
        this.numDraws = numDraws;
    }
    public FriendInfo(String currentUsername,UUID uuid) {
        super(currentUsername, uuid);
        this.numWins = 0;
        this.numLosses = 0;
        this.numDraws = 0;
    }

    public FriendInfo() {

    }

    public int getNumWins() {
        return numWins;
    }

    public void setNumWins(int numWins) {
        this.numWins = numWins;
    }

    public int getNumLosses() {
        return numLosses;
    }

    public void setNumLosses(int numLosses) {
        this.numLosses = numLosses;
    }

    public int getNumDraws() {
        return numDraws;
    }

    public void setNumDraws(int numDraws) {
        this.numDraws = numDraws;
    }

}
