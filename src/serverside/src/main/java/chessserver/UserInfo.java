package chessserver;

import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class UserInfo {
    int userelo;
    String userName;
    String userEmail;
    int uuid;
    CampaignProgress userCampaignProgress;
    ProfilePicture profilePicture;
    String profilePictureUrl;
    List<FriendInfo> friendUserNames;
    List<Integer> incomingRequests;
    List<String> outgoingRequests;
    long lastUpdateTimeMS;
    List<String> compressedGames;

    public UserInfo(int userelo, String userName, String userEmail, int uuid, CampaignProgress userCampaignProgress, ProfilePicture profilePicture, List<FriendInfo> friendUserNames, List<Integer> incomingRequests, List<String> outgoingRequests, List<String> compressedGames) {
        this.userelo = userelo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.uuid = uuid;
        this.userCampaignProgress = userCampaignProgress;
        this.profilePicture = profilePicture;
        this.profilePictureUrl = profilePicture.urlString;
        this.friendUserNames = friendUserNames;
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.compressedGames = compressedGames;
        updateLastTimeStamp();
    }

    public UserInfo() {
        // empty for objectmapper serialization
    }

    public long getLastUpdateTimeMS() {
        return lastUpdateTimeMS;
    }

    public void setLastUpdateTimeMS(long lastUpdateTimeMS) {
        this.lastUpdateTimeMS = lastUpdateTimeMS;
        updateLastTimeStamp();

    }

    private void updateLastTimeStamp() {
        lastUpdateTimeMS = System.currentTimeMillis();
    }

    public List<String> getCompressedGames() {
        return compressedGames;
    }

    public void setCompressedGames(List<String> compressedGames) {
        this.compressedGames = compressedGames;
        updateLastTimeStamp();
    }

    public void addGameUncompressed(String gameHash,String saveStringUncompresssed) {
        compressedGames.add(gameHash + "," + Base64.getEncoder().encodeToString(saveStringUncompresssed.getBytes()));
        updateLastTimeStamp();
    }

    public void removeGameUncompressed(String gameHash){
        compressedGames.removeIf(game -> game.split(",")[0].equals(gameHash));
        updateLastTimeStamp();
    }

    public String[] getUncompressedSaveStrings() {
        String[] uncompressedSaveStrings = new String[compressedGames.size()];
        for (int i = 0; i < compressedGames.size(); i++) {
            uncompressedSaveStrings[i] = new String(Base64.getDecoder().decode(compressedGames.get(i)));
        }
        return uncompressedSaveStrings;
    }

    public List<Integer> getIncomingRequests() {
        return incomingRequests;
    }

    public void setIncomingRequests(List<Integer> incomingRequests) {
        this.incomingRequests = incomingRequests;
        updateLastTimeStamp();
    }

    public List<String> getOutgoingRequests() {
        return outgoingRequests;
    }

    public void setOutgoingRequests(List<String> outgoingRequests) {
        this.outgoingRequests = outgoingRequests;
        updateLastTimeStamp();
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
        updateLastTimeStamp();
    }


    public List<FriendInfo> getFriendUserNames() {
        return friendUserNames;
    }

    public void setFriendUserNames(List<FriendInfo> friendUserNames) {
        this.friendUserNames = friendUserNames;
        updateLastTimeStamp();
    }

    public CampaignProgress getUserCampaignProgress() {
        return userCampaignProgress;
    }

    public void setUserCampaignProgress(CampaignProgress userCampaignProgress) {
        this.userCampaignProgress = userCampaignProgress;
        updateLastTimeStamp();
    }

    public int getUserelo() {
        return userelo;
    }

    public void setUserelo(int userelo) {
        this.userelo = userelo;
        updateLastTimeStamp();
    }

    public void adjustElo(int change) {
        this.userelo += change;
        updateLastTimeStamp();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        updateLastTimeStamp();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        updateLastTimeStamp();
    }


    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
        updateLastTimeStamp();
    }

    public String getProfilePictureUrl() {
        return profilePicture.urlString;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        updateLastTimeStamp();
    }


}
