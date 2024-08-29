package chessengine;

import chessserver.*;
import jakarta.websocket.DeploymentException;

import java.io.IOException;
import java.util.Objects;

public class ClientManager {
    private FrontendClient appUser;

    public ClientManager() {
        appUser = PersistentSaveManager.readUserInfoFromAppData();

        if (Objects.isNull(appUser)) {
            appUser = ChessConstants.defaultUser;
        }
    }

    public void changeAppUser(UserInfo newInfo) {
        appUser = new FrontendClient(newInfo);
        loadChanges();
        // no need to push to database as userinfo is already coming from the database
    }

    public WebSocketClient getClientFromUser() throws DeploymentException, IOException {
        return new WebSocketClient(appUser);
    }

    public void init(StartScreenController controller) {
        // graphical stuff
        controller.setProfileInfo(appUser.getInfo().getProfilePicture(), appUser.getInfo().getUserName(), appUser.getInfo().getUserelo());
    }

    public String getUserName() {
        return appUser.getInfo().getUserName();
    }

    public int getUserElo() {
        return appUser.getInfo().getUserelo();
    }

    public String getUserPfpUrl() {
        return appUser.getInfo().getProfilePicture().urlString;
    }

    public void updateUserElo(int change) {
        if (change != 0) {
            appUser.getInfo().adjustElo(change);
            loadChanges();
            pushChangesToDatabase();
        } else {
            ChessConstants.mainLogger.debug("Elo change is 0 so no change needed");
        }

    }

    public void changeUserName(String newName) {
        if (!appUser.getInfo().getUserName().equals(newName)) {
            appUser.getInfo().setUserName(newName);
            loadChanges();
            pushChangesToDatabase();
        } else {
            ChessConstants.mainLogger.debug("New name is same as current name");
        }
    }

    public void changeUserEmail(String newEmail) {
        if (!appUser.getInfo().getUserEmail().equals(newEmail)) {
            appUser.getInfo().setUserEmail(newEmail);
            loadChanges();
            pushChangesToDatabase();
        } else {
            ChessConstants.mainLogger.debug("New user email is same as current email");
        }

    }

    public void changeProfilePicture(ProfilePicture newPicture) {
        if (!appUser.getInfo().getProfilePicture().equals(newPicture)) {
            appUser.getInfo().setProfilePicture(newPicture);
            loadChanges();
            pushChangesToDatabase();
        } else {
            ChessConstants.mainLogger.debug("New pfp is same as current pfp");
        }
    }

    public int getCurrentCampaignLevel() {
        return appUser.getInfo().getUserCampaignProgress().getCurrentLevelOfTier();
    }

    public CampaignTier getCurrentCampaignTier() {
        return appUser.getInfo().getUserCampaignProgress().getCurrentTier();
    }

    public CampaignProgress getCampaignProgress() {
        return appUser.getInfo().getUserCampaignProgress();
    }

    public void moveToNewCampaignTier(CampaignTier newTier) {
        appUser.getInfo().getUserCampaignProgress().setCurrentTier(newTier);
        loadChanges();
        pushChangesToDatabase();
    }

    public void moveToNextLevel() {
        appUser.getInfo().getUserCampaignProgress().moveToNextLevel();
        loadChanges();
        pushChangesToDatabase();

    }

    public void setLevelStars(CampaignTier completedTier, int levelOfTier, int numStars) {
        appUser.getInfo().getUserCampaignProgress().setStarsForALevel(completedTier, levelOfTier, numStars);
        loadChanges();
        pushChangesToDatabase();

    }

    private void loadChanges() {
        App.startScreenController.setProfileInfo(appUser.getInfo().getProfilePicture(), appUser.getInfo().getUserName(), appUser.getInfo().getUserelo());
        App.startScreenController.campaignManager.setLevelUnlocksBasedOnProgress(appUser.getInfo().getUserCampaignProgress());
        PersistentSaveManager.writeUserToAppData(appUser.getInfo());
    }

    private void pushChangesToDatabase() {

    }
}
