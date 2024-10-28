package chessengine.Managers;

import chessengine.App;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.KeyManager;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Graphics.StartScreenController;
import chessengine.Misc.ChessConstants;
import chessserver.*;
import jakarta.websocket.DeploymentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientManager {
    private static final Logger logger = LogManager.getLogger("Client_Manager");
    private FrontendClient appUser;

    public ClientManager() {
        appUser = PersistentSaveManager.readUserInfoFromAppData();

        if (Objects.isNull(appUser)) {
            appUser = ChessConstants.defaultClient;
        }
    }

    public boolean isLoggedIn() {
        return KeyManager.tryLoadCurrentPasswordHash() != null;
    }

    public UserInfo getCurrentUser() {
        return appUser.getInfo();
    }

    public long getLastTimeStampMs() {
        return appUser.getInfo().getLastUpdateTimeMS();
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
        controller.setProfileInfo(appUser.getInfo().getProfilePicture(), appUser.getInfo().getUserName(), appUser.getInfo().getUserelo(), appUser.getInfo().getUuid());
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

    public ProfilePicture getUserPfp() {
        return appUser.getInfo().getProfilePicture();
    }

    public void updateUserPfp(ProfilePicture newPicture) {
        if (!appUser.getInfo().getProfilePicture().equals(newPicture)) {
            appUser.getInfo().setProfilePicture(newPicture);
            loadChanges();
            pushChangesToDatabase();
        } else {
            logger.debug("Already have same pfp");
        }

    }

    public void updateUserElo(int change) {
        if (change != 0) {
            appUser.getInfo().adjustElo(change);
            loadChanges();
            pushChangesToDatabase();
        } else {
            logger.debug("Elo change is 0 so no change needed");
        }

    }

    public void changeUserName(String newName, boolean updateDatabase) {
        if (!appUser.getInfo().getUserName().equals(newName)) {
            appUser.getInfo().setUserName(newName);
            loadChanges();
            if (updateDatabase) {
                pushChangesToDatabase();
            }
        } else {
            logger.debug("New name is same as current name");
        }
    }


    public void changeUUID(int newUUID, boolean updateDatabase) {
        if (appUser.getInfo().getUuid() != newUUID) {
            appUser.getInfo().setUuid(newUUID);
            loadChanges();
            if (updateDatabase) {
                pushChangesToDatabase();
            }
        } else {
            logger.debug("New uuid is same as current uuid");
        }
    }

    public int getUUID() {
        return appUser.getInfo().getUuid();
    }

    public void changeUserEmail(String newEmail) {
        if (!appUser.getInfo().getUserEmail().equals(newEmail)) {
            appUser.getInfo().setUserEmail(newEmail);
            loadChanges();
            pushChangesToDatabase();
        } else {
            logger.debug("New user email is same as current email");
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
        App.startScreenController.setProfileInfo(appUser.getInfo().getProfilePicture(), appUser.getInfo().getUserName(), appUser.getInfo().getUserelo(), appUser.getInfo().getUuid());
        App.startScreenController.campaignManager.setLevelUnlocksBasedOnProgress(appUser.getInfo().getUserCampaignProgress());
        App.startScreenController.setupOldGamesBox(readSavedGames());
        PersistentSaveManager.writeUserToAppData(appUser.getInfo());
    }

    private void pushChangesToDatabase() {
        if (isLoggedIn()) {
            App.partialDatabaseUpdateRequest(appUser.getInfo());
        } else {
            logger.debug("Not pushing to database, not signed in");
        }
    }

    public void reloadNewAppUser(UserInfo newAppUser, boolean updateDatabase) {
        appUser = new FrontendClient(newAppUser);
        if (App.isWebClientConnected()) {
            App.getWebclient().updateClient(appUser);
        }
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public void logout() {
        reloadNewAppUser(ChessConstants.defaultClient.getInfo(), false);
    }

    public void addMoreFriendRequests(String out,boolean updateDatabase) {
        for(String s : out.split(",")){
            appUser.getInfo().getIncomingRequests().add(Integer.parseInt(s));
        }
        loadChanges();
        if(updateDatabase){
            pushChangesToDatabase();
        }
    }

    public void saveUserGame(ChessGame game) {
        appUser.getInfo().addSaveGame(CryptoUtils.chessGameToSaveString(game));
        loadChanges();
        pushChangesToDatabase();
    }

    public void removeGameFromSave(String hashCode) {
        appUser.getInfo().removeSaveGame(hashCode);
        loadChanges();
        pushChangesToDatabase();
    }

    public List<ChessGame> readSavedGames() {
        List<ChessGame> out = new ArrayList<>();
        String[] saves = appUser.getInfo().aquireSaveStrings();
        for(String save : saves){
            out.add(CryptoUtils.gameFromSaveString(save));
        }

        return out;
    }
}
