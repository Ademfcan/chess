package chessserver.User;

import chessserver.Enums.ProfilePicture;
import chessserver.Friends.Friend;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class UserInfo {
    private static final Logger logger = LogManager.getLogger("User_Info_Logger");
    private static final UserInfo defaultInfo = new UserInfo("", 0, "", new UUID(0L, 0L),
                                                            new CampaignProgress(123), ProfilePicture.DEFAULT,
                                                            new ArrayList<>(),new ArrayList<>());


    long lastUpdateTimeMS;
    int userelo;
    UUID uuid;

    String userName;
    String userEmail;
    ProfilePicture profilePicture;
    CampaignProgress userCampaignProgress;

    List<Friend> friends;
    List<Friend> suggestedFriends;

    @JsonCreator
    public UserInfo(
            @JsonProperty("userName") String userName,
            @JsonProperty("userelo") int userelo,
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("uuid") UUID uuid,
            @JsonProperty("userCampaignProgress") CampaignProgress userCampaignProgress,
            @JsonProperty("profilePicture") ProfilePicture profilePicture,
            @JsonProperty("friends") List<Friend> friends,
            @JsonProperty("suggestedFriends") List<Friend> suggestedFriends
    ) {
        this.userelo = userelo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.uuid = uuid;
        this.userCampaignProgress = userCampaignProgress;
        this.profilePicture = profilePicture;
        this.friends = friends;
        this.suggestedFriends = suggestedFriends;
    }

    public static UserInfo getPartiallyDefaultUserInfo(String userName, String userEmail, UUID UUID){
        UserInfo defaultUser = defaultInfo;
        defaultUser.setUserName(userName);
        defaultUser.setUuid(UUID);
        defaultUser.setUserEmail(userEmail);
        defaultUser.updateLastTimeStamp();

        return defaultUser;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        updateLastTimeStamp();
    }

    public void updateFriendUsernames(String serverResponse){
        String[] responseSplit = serverResponse.split(",");
        if(responseSplit.length != friends.size()){
            logger.error("Size mismatch between username update response and list size!\nResponse: " + serverResponse);
            return;
        }
        Iterator<Friend> listIterator = friends.listIterator();
        for(String username : responseSplit){
            if (username.isEmpty()) {
                // account no longer exists, no match was found
                listIterator.remove();
            } else {
                listIterator.next().setCurrentUsername(username);
            }
        }

    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
        updateLastTimeStamp();
    }

    public void setUserelo(int userelo) {
        this.userelo = userelo;
        this.userelo = Math.max(this.userelo,0);
        updateLastTimeStamp();
    }

    public void setUserName(String userName) {
        this.userName = userName;
        updateLastTimeStamp();
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        updateLastTimeStamp();
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
        updateLastTimeStamp();
    }

    private void updateLastTimeStamp() {
        lastUpdateTimeMS = System.currentTimeMillis();
    }

    public long getLastUpdateTimeMS() {
        return lastUpdateTimeMS;
    }
    public List<Friend> getFriends() {
        return friends;
    }

    public void setSuggestedFriends(List<Friend> suggestedFriends) {
        this.suggestedFriends = suggestedFriends;
    }

    public List<Friend> getSuggestedFriends() {
        return suggestedFriends;
    }
    public UUID getUuid() {
        return uuid;
    }
    public int getUserelo() {
        return userelo;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserEmail() {
        return userEmail;
    }

    @JsonIgnore
    public String getProfilePictureUrl() {
        return profilePicture.urlString;

    }
    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }
    public CampaignProgress getUserCampaignProgress() {
        return userCampaignProgress;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userelo=" + userelo +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", uuid=" + uuid +
                ", userCampaignProgress=" + userCampaignProgress +
                ", profilePicture=" + profilePicture +
                ", friends=" + friends +
                ", lastUpdateTimeMS=" + lastUpdateTimeMS +
                '}';
    }


}
