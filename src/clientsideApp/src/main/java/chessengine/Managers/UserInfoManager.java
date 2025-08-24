package chessengine.Managers;

import chessengine.App;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Crypto.TokenStore;
import chessserver.ChessRepresentations.PlayerInfo;
import chessserver.Enums.CampaignTier;
import chessserver.Enums.ProfilePicture;
import chessserver.Friends.Friend;
import chessserver.Friends.FriendDataResponse;
import chessserver.Friends.ServerFriendData;
import chessserver.User.CampaignProgress;
import chessserver.User.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class UserInfoManager {
    private static final Logger logger = LogManager.getLogger("Client_Manager");

    // --- Friend Request Handling ---

    public List<Friend> getFriends() {
        return getAppUser().getFriends();
    }

    public List<Friend> friendsFromDataResponse(FriendDataResponse response) {
        return response.dataResponse().stream()
                .map(data -> new Friend(data.currentUsername(), data.UUID()))
                .collect(Collectors.toList());
    }

    public List<UUID> getSuggestedFriendUUIDs() {
        return getAppUser().getSuggestedFriends().stream()
                .map(Friend::getUUID)
                .collect(Collectors.toList());
    }

    public void updateFriendUsernames(String serverResponse) {
        getAppUser().updateFriendUsernames(serverResponse);
        App.triggerUpdateUser();
    }





    // --- User Info Access ---

    public boolean isLoggedIn() {
        return TokenStore.getRefreshToken() != null;
    }

    public UserInfo getAppUser() {
        return PersistentSaveManager.userInfoTracker.getTracked();
    }

    public long getLastTimeStampMs() {
        return getAppUser().getLastUpdateTimeMS();
    }

    public PlayerInfo getCurrentPlayerInfo() {
        return new PlayerInfo(getUUID(), getUserName(), getUserElo(), getUserPfpUrl());
    }

    public UUID getUUID() {
        return getAppUser().getUuid();
    }

    public String getUserName() {
        return getAppUser().getUserName();
    }

    public int getUserElo() {
        return getAppUser().getUserelo();
    }

    public String getUserPfpUrl() {
        return getAppUser().getProfilePicture().urlString;
    }

    public ProfilePicture getUserPfp() {
        return getAppUser().getProfilePicture();
    }

    // --- User Info Updates ---

    public void updateUserPfp(ProfilePicture newPicture) {
        if (!getUserPfp().equals(newPicture)) {
            getAppUser().setProfilePicture(newPicture);
            App.triggerUpdateUser();
        } else {
            logger.debug("Already have same pfp");
        }
    }

    public void updateUserElo(int newElo) {
        if (newElo >= 0) {
            getAppUser().setUserelo(newElo);
            App.triggerUpdateUser();
        } else {
            logger.debug("Elo change is 0 so no change needed");
        }
    }

    public void changeUserName(String newName) {
        if (!getUserName().equals(newName)) {
            getAppUser().setUserName(newName);
            App.triggerUpdateUser();
        } else {
            logger.debug("New name is same as current name");
        }
    }

    public void changeUUID(UUID newUUID) {
        if (!getUUID().equals(newUUID)) {
            getAppUser().setUuid(newUUID);
            App.triggerUpdateUser();
        } else {
            logger.debug("New uuid is same as current uuid");
        }
    }

    public void changeUserEmail(String newEmail) {
        if (!getAppUser().getUserEmail().equals(newEmail)) {
            getAppUser().setUserEmail(newEmail);
            App.triggerUpdateUser();
        } else {
            logger.debug("New user email is same as current email");
        }
    }

    // --- Campaign Progress ---

    public CampaignProgress getCampaignProgress() {
        return getAppUser().getUserCampaignProgress();
    }

    public int getCurrentCampaignLevel() {
        return getCampaignProgress().getCurrentLevelOfTier();
    }

    public CampaignTier getCurrentCampaignTier() {
        return getCampaignProgress().getCurrentTier();
    }

    public void moveToNewCampaignTier(CampaignTier newTier) {
        getCampaignProgress().setCurrentTier(newTier);
        App.triggerUpdateUser();
    }

    public void moveToNextLevel() {
        getCampaignProgress().moveToNextLevel();
        App.triggerUpdateUser();
    }

    public void setLevelStars(CampaignTier tier, int level, int stars) {
        getCampaignProgress().setStarsForALevel(tier, level, stars);
        App.triggerUpdateUser();
    }



}
