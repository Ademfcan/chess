package chessserver;

public class FriendInfo {
    int UUID;
    int elo;
    int numWins;
    int numLosses;
    int numDraws;

    public FriendInfo(String userName, int UUID, int elo, int numWins, int numLosses, int numDraws, String profilePictureUrl, ProfilePicture profilePicture) {
        this.UUID = UUID;
        this.elo = elo;
        this.numWins = numWins;
        this.numLosses = numLosses;
        this.numDraws = numDraws;
    }

    public FriendInfo() {

    }

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }


    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
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
