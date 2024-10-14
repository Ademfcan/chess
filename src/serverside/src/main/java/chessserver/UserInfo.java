package chessserver;

import java.util.List;

public class UserInfo {
    int userelo;
    CampaignProgress userCampaignProgress;
    String userName;
    String userEmail;
    ProfilePicture profilePicture;
    String profilePictureUrl;
    List<FriendInfo> friendUserNames;

    public UserInfo(int userelo, String userName, String userEmail, ProfilePicture profilePicture, CampaignProgress userCampaignProgress, List<FriendInfo> friendUserNames) {
        this.userelo = userelo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.profilePicture = profilePicture;
        this.profilePictureUrl = profilePicture.urlString;
        this.userCampaignProgress = userCampaignProgress;
        this.friendUserNames = friendUserNames;
    }

    public UserInfo() {
        // empty for objectmapper serialization
    }

    public List<FriendInfo> getFriendUserNames() {
        return friendUserNames;
    }

    public void setFriendUserNames(List<FriendInfo> friendUserNames) {
        this.friendUserNames = friendUserNames;
    }

    public CampaignProgress getUserCampaignProgress() {
        return userCampaignProgress;
    }

    public void setUserCampaignProgress(CampaignProgress userCampaignProgress) {
        this.userCampaignProgress = userCampaignProgress;
    }

    public int getUserelo() {
        return userelo;
    }

    public void setUserelo(int userelo) {
        this.userelo = userelo;
    }

    public void adjustElo(int change) {
        this.userelo = change;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }



    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureUrl() {
        return profilePicture.urlString;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }


}
