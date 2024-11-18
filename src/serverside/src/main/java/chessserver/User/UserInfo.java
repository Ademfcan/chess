package chessserver.User;

import chessserver.Enums.ProfilePicture;
import chessserver.Friends.Friend;
import chessserver.Friends.FriendInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserInfo {
    private static final Logger logger = LogManager.getLogger("User_Info_Logger");
    int userelo;
    String userName;
    String userEmail;
    int uuid;
    CampaignProgress userCampaignProgress;
    ProfilePicture profilePicture;
    String profilePictureUrl;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userelo=" + userelo +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", uuid=" + uuid +
                ", userCampaignProgress=" + userCampaignProgress +
                ", profilePicture=" + profilePicture +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", friends=" + friends +
                ", incomingRequests=" + incomingRequests +
                ", outgoingRequests=" + outgoingRequests +
                ", lastUpdateTimeMS=" + lastUpdateTimeMS +
                ", savedGames=" + savedGames +
                '}';
    }

    List<FriendInfo> friends;
    List<Friend> incomingRequests;
    List<Friend> outgoingRequests;
    long lastUpdateTimeMS;
    List<String> savedGames;

    public UserInfo(String userName, int userelo, String userEmail, int uuid, CampaignProgress userCampaignProgress, ProfilePicture profilePicture, List<FriendInfo> friends, List<Friend> incomingRequests, List<Friend> outgoingRequests, List<String> compressedGames) {
        this.userelo = userelo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.uuid = uuid;
        this.userCampaignProgress = userCampaignProgress;
        this.profilePicture = profilePicture;
        this.profilePictureUrl = profilePicture.urlString;
        this.friends = friends;
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.savedGames = compressedGames;
        updateLastTimeStamp();
    }

    public static UserInfo getPartiallyDefaultUserInfo(String userName,int UUID){
        return new UserInfo(userName,0,"",UUID,new CampaignProgress(),ProfilePicture.DEFAULT,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
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

    public List<String> getSavedGames() {
        return savedGames;
    }



    public void setSavedGames(List<String> savedGames) {
        this.savedGames = savedGames;
        updateLastTimeStamp();
    }

    public void addSaveGame(String saveStringUncompresssed) {
        savedGames.add(saveStringUncompresssed);
        updateLastTimeStamp();
    }

    public void removeSaveGame(String gameHash){
        savedGames.removeIf(game -> game.split(",")[0].equals(gameHash));
        updateLastTimeStamp();
    }
    public List<Friend> getIncomingRequests() {
        return incomingRequests;
    }

    public String aquireIncomingRequestUUIDSAsStr(){
        return aquireUUIDSAsStr(incomingRequests);
    }

    public void updateIncomingRequestUsernames(String serverResponse){
        updateUsernames(incomingRequests,serverResponse);
    }

    public void setIncomingRequests(List<Friend> incomingRequests) {
        this.incomingRequests = incomingRequests;
        updateLastTimeStamp();
    }

    public List<Friend> getOutgoingRequests() {
        return outgoingRequests;
    }
    public String aquireOutgoingRequestUUIDSAsStr(){
        return aquireUUIDSAsStr(outgoingRequests);
    }

    public void updateOutgoingRequestUsernames(String serverResponse){
        updateUsernames(outgoingRequests,serverResponse);
    }
    public void setOutgoingRequests(List<Friend> outgoingRequests) {
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


    public List<FriendInfo> getFriends() {
        return friends;
    }
    public String aquireFriendUUIDSAsStr(){
        return aquireUUIDSAsStr(friends);
    }

    public void updateFriendUsernames(String serverResponse){
        String[] responseSplit = serverResponse.split(",");
        if(responseSplit.length != friends.size()){
            logger.error("Size mismatch between username update response and list size!\nResponse: " + serverResponse);
            return;
        }
        Iterator<FriendInfo> listIterator = friends.listIterator();
        for(String username : responseSplit){
            if (username.isEmpty()) {
                // account no longer exists, no match was found
                listIterator.remove();
            } else {
                listIterator.next().setCurrentUsername(username);
            }
        }

    }

    public void setFriends(List<FriendInfo> friends) {
        this.friends = friends;
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
        this.userelo = Math.max(this.userelo,0);
        updateLastTimeStamp();
    }

    public void adjustElo(int change) {
        this.userelo += change;
        this.userelo = Math.max(this.userelo,0);
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

    private String aquireUUIDSAsStr(List<? extends Friend> list){
        StringBuilder sb = new StringBuilder();
        for(Friend f : list){
            sb.append(f.getUUID()).append(",");
        }
        return sb.toString();
    }

    private void updateUsernames(List<Friend> list, String usernameUpdateResponse){
        String[] responseSplit = usernameUpdateResponse.split(",");
        if(responseSplit.length != list.size()){
            logger.error("Size mismatch between username update response and list size!\nResponse: " + usernameUpdateResponse);
            return;
        }
        Iterator<Friend> listIterator = list.listIterator();
        for(String username : responseSplit){
            if (username.isEmpty()) {
                // account no longer exists, no match was found
                listIterator.remove();
            } else {
                listIterator.next().setCurrentUsername(username);
            }
        }
    }


}
