package chessserver;

public class FriendInfo {
    String userName;
    int elo;
    int numWins;
    int numLosses;
    int numDraws;
    String profilePictureUrl;
    ProfilePicture profilePicture;

    public FriendInfo(String userName, int elo, int numWins, int numLosses, int numDraws, String profilePictureUrl, ProfilePicture profilePicture) {
        this.userName = userName;
        this.elo = elo;
        this.numWins = numWins;
        this.numLosses = numLosses;
        this.numDraws = numDraws;
        this.profilePictureUrl = profilePictureUrl;
        this.profilePicture = profilePicture;
    }

    public FriendInfo() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

}
