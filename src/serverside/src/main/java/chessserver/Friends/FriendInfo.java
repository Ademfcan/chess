package chessserver.Friends;

public class FriendInfo extends Friend {
    int numWins;
    int numLosses;
    int numDraws;

    public FriendInfo(int UUID, String currentUsername, int numWins, int numLosses, int numDraws) {
        super(currentUsername, UUID);
        this.numWins = numWins;
        this.numLosses = numLosses;
        this.numDraws = numDraws;
    }
    public FriendInfo(String currentUsername,int UUID) {
        super(currentUsername, UUID);
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
