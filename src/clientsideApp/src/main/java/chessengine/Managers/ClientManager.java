package chessengine.Managers;

import chessengine.App;
import chessserver.ChessRepresentations.ChessGame;
import chessengine.Crypto.CryptoUtils;
import chessengine.Crypto.KeyManager;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Misc.ClientsideDataEntry;
import chessengine.Start.StartScreenController;
import chessserver.Misc.ChessConstants;
import chessengine.Misc.ClientsideFriendDataResponse;
import chessserver.Enums.CampaignTier;
import chessserver.Enums.ProfilePicture;
import chessserver.Friends.Friend;
import chessserver.Friends.FriendDataPair;
import chessserver.Friends.FriendDataResponse;
import chessserver.Friends.FriendInfo;
import chessserver.Communication.DatabaseEntry;
import chessserver.User.CampaignProgress;
import chessserver.User.FrontendClient;
import chessserver.User.UserInfo;
import jakarta.websocket.DeploymentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientManager {
    private static final Logger logger = LogManager.getLogger("Client_Manager");
    private ClientsideFriendDataResponse currentFriendsFromServer = new ClientsideFriendDataResponse();
    private ClientsideFriendDataResponse currentIncomingRequestsFromServer = new ClientsideFriendDataResponse();
    private ClientsideFriendDataResponse currentSuggestedFriendsFromServer = new ClientsideFriendDataResponse();
    private FrontendClient appUser;

    public ClientManager() {
        appUser = PersistentSaveManager.readUserInfoFromAppData();

        if (Objects.isNull(appUser)) {
            appUser = ChessConstants.defaultClient;
        }
    }

    public ClientsideFriendDataResponse getCurrentFriendsFromServer() {
        return currentFriendsFromServer;
    }

    public void setCurrentFriendsFromServer(FriendDataResponse friendsFromServer) {
        this.currentFriendsFromServer = new ClientsideFriendDataResponse();
        for(FriendDataPair dataPair: friendsFromServer.getDataResponse()){

            if(dataPair.getFriendDatabaseEntryAsString().isEmpty()){
                // no longer exists on server
                appUser.getInfo().getFriends().removeIf(f -> f.getUUID() == dataPair.getUUID());
            }
            else{
                currentFriendsFromServer.addDatabaseEntry(new ClientsideDataEntry(dataPair.isOnline(),App.readFromObjectMapper(dataPair.getFriendDatabaseEntryAsString(), DatabaseEntry.class)));
            }
        }
    }

    public void setCurrentFriendsFromServer(ClientsideFriendDataResponse friendsFromServerProcessed){
        this.currentFriendsFromServer = friendsFromServerProcessed;
    }

    public ClientsideFriendDataResponse getCurrentIncomingRequestsFromServer() {
        return currentIncomingRequestsFromServer;
    }

    public void setCurrentIncomingRequestsFromServer(FriendDataResponse incomingRequestsFromServer) {
        this.currentIncomingRequestsFromServer = new ClientsideFriendDataResponse();
        for(FriendDataPair dataPair: incomingRequestsFromServer.getDataResponse()){
            if(dataPair.getFriendDatabaseEntryAsString().isEmpty()){
                // no longer exists on server
                appUser.getInfo().getIncomingRequests().removeIf(f -> f.getUUID() == dataPair.getUUID());
            }
            else{
                currentIncomingRequestsFromServer.addDatabaseEntry(new ClientsideDataEntry(dataPair.isOnline(),App.readFromObjectMapper(dataPair.getFriendDatabaseEntryAsString(), DatabaseEntry.class)));
            }
        }
    }

    public void setCurrentIncomingRequestsFromServer(ClientsideFriendDataResponse incomingRequestsFromServerProcessed){
        this.currentIncomingRequestsFromServer = incomingRequestsFromServerProcessed;
    }

    public ClientsideFriendDataResponse getCurrentSuggestedFriendsFromServer() {
        return currentSuggestedFriendsFromServer;
    }

    public void setCurrentSuggestedFriendsFromServer(FriendDataResponse suggestedFriendsFromServer) {
        this.currentSuggestedFriendsFromServer = new ClientsideFriendDataResponse();
        for(FriendDataPair dataPair: suggestedFriendsFromServer.getDataResponse()){

            if(dataPair.getFriendDatabaseEntryAsString().isEmpty()){
                // no longer exists on server
                // todo
            }
            else{
                currentSuggestedFriendsFromServer.addDatabaseEntry(new ClientsideDataEntry(dataPair.isOnline(),App.readFromObjectMapper(dataPair.getFriendDatabaseEntryAsString(), DatabaseEntry.class)));
            }
        }
    }

    public void setCurrentSuggestedFriendsFromServer(ClientsideFriendDataResponse suggestedFriendsFromServerProcessed){
        this.currentSuggestedFriendsFromServer = suggestedFriendsFromServerProcessed;
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
        controller.userInfoManager.reloadUserPanel(appUser.getInfo(),true,false);
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

    public void updateUserElo(int newElo) {
        if (newElo >= 0) {
            appUser.getInfo().setNewElo(newElo);
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
        App.startScreenController.userInfoManager.reloadUserPanel(appUser.getInfo(),true,false);
        App.startScreenController.campaignManager.setLevelUnlocksBasedOnProgress(appUser.getInfo().getUserCampaignProgress());
        App.startScreenController.userInfoManager.showCurrentFriendsPanel();
        PersistentSaveManager.writeUserToAppData(appUser.getInfo());
    }

    private void pushChangesToDatabase() {
        if (isLoggedIn()) {
            App.partialDatabaseUpdateRequest(appUser.getInfo());
        } else {
            logger.debug("Not pushing to database, not signed in");
        }
    }

    public void clearTempValues(){
        currentIncomingRequestsFromServer = new ClientsideFriendDataResponse();
        currentSuggestedFriendsFromServer = new ClientsideFriendDataResponse();
        currentFriendsFromServer = new ClientsideFriendDataResponse();
        cachedGames = null;
    }

    public void reloadAppUser() {
        clearTempValues();
        loadChanges();
    }

    public void reloadNewAppUser(UserInfo newAppUser,boolean serverReconnect, boolean updateDatabase) {
        clearTempValues();
        appUser = new FrontendClient(newAppUser);
        if(serverReconnect){
            if (App.isWebClientConnected()) {
                try {
                    App.getWebclient().close();
                }
                catch (IOException e){
                    logger.error("Websocket close error",e);
                }
            }
            App.attemptReconnection();
        }
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public void logout() {

        reloadNewAppUser(ChessConstants.defaultClient.getInfo(),true, false);
    }

    public void addMoreFriendRequests(String out, boolean updateDatabase) {
        for (String entry : out.split(";")) {
            String[] split = entry.split(",");
            Friend friend = new Friend(split[0], Integer.parseInt(split[1]));
            appUser.getInfo().getIncomingRequests().add(friend);
        }
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public void addAcceptedFriendRequests(String out, boolean updateDatabase) {
        for (String entry : out.split(";")) {
            String[] split = entry.split(",");
            String userName = split[0];
            int UUID = Integer.parseInt(split[1]);
            FriendInfo friend = new FriendInfo(userName, UUID);
            appUser.getInfo().getFriends().add(friend);
            appUser.getInfo().getOutgoingRequests().removeIf(f -> f.getUUID() == UUID);
        }
        loadChanges();
        if (updateDatabase) {
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

    private List<ChessGame> cachedGames;
    public List<ChessGame> readSavedGames() {
       if(cachedGames == null){
           cachedGames = UserHelperFunctions.readSavedGames(appUser.getInfo().getSavedGames());
       }
       return cachedGames;
    }

    public String getOutgoingRequestUUIDStr() {
        return appUser.getInfo().aquireOutgoingRequestUUIDSAsStr();
    }

    public String getIncomingRequestUUIDStr() {
        return appUser.getInfo().aquireIncomingRequestUUIDSAsStr();
    }

    public String getSuggestedFriendUUIDStr() {
        // todo;
        return "";
    }

    public String getFriendUUIDStr() {
        return appUser.getInfo().aquireFriendUUIDSAsStr();
    }

    public void updateOutgoingRequestUsernames(String serverResponse) {
        appUser.getInfo().updateOutgoingRequestUsernames(serverResponse);
        loadChanges();
        pushChangesToDatabase();
    }

    public void updateIncomingRequestUsernames(String serverResponse) {
        appUser.getInfo().updateIncomingRequestUsernames(serverResponse);
        loadChanges();
        pushChangesToDatabase();
    }

    public void updateFriendUsernames(String serverResponse) {
        appUser.getInfo().updateFriendUsernames(serverResponse);
        loadChanges();
        pushChangesToDatabase();
    }


    /**
     * O = No existence, 1 = Friend, 2 = Sent outgoing request, 3 = have incoming request <p> All of these are mutually exclusive (should be*) </p>
     **/
    public int doesFriendExist(String userName, boolean includeFriendRequests) {
        boolean isFriend = appUser.getInfo().getFriends().stream().anyMatch(f -> f.getCurrentUsername().equals(userName));
        if (isFriend) {
            return 1;
        }

        if (includeFriendRequests) {
            boolean isInOutgoing = appUser.getInfo().getOutgoingRequests().stream().anyMatch(f -> f.getCurrentUsername().equals(userName));
            if (isInOutgoing) {
                return 2;
            }
            boolean isInIncoming = appUser.getInfo().getOutgoingRequests().stream().anyMatch(f -> f.getCurrentUsername().equals(userName));
            if (isInIncoming) {
                return 3;
            }
        }

        return 0;
    }

    public Friend matchIncomingRequests(String userName) {
        List<Friend> match = appUser.getInfo().getIncomingRequests().stream().filter(f -> f.getCurrentUsername().equals(userName)).toList();
        return !match.isEmpty() ? match.get(0) : null;
    }

    public Friend matchOutgoingRequests(String userName) {
        List<Friend> match = appUser.getInfo().getOutgoingRequests().stream().filter(f -> f.getCurrentUsername().equals(userName)).toList();
        return !match.isEmpty() ? match.get(0) : null;
    }

    public FriendInfo matchExistingFriends(String userName) {
        List<FriendInfo> match = appUser.getInfo().getFriends().stream().filter(f -> f.getCurrentUsername().equals(userName)).toList();
        return !match.isEmpty() ? match.get(0) : null;
    }


    public List<FriendInfo> getFriends() {
        return appUser.getInfo().getFriends();
    }

    public List<Friend> getIncomingFriendRequests() {
        return appUser.getInfo().getIncomingRequests();
    }

    public List<Friend> getOutgoingFriendRequests() {
        return appUser.getInfo().getOutgoingRequests();
    }

    public void addNewFriend(String userName, int UUID, boolean updateDatabase) {
        FriendInfo newFriend = new FriendInfo(userName, UUID);
        appUser.getInfo().getFriends().add(newFriend);
        // remove from requests, if exists
        appUser.getInfo().getIncomingRequests().removeIf(f -> f.getUUID() == UUID);

        appUser.getInfo().getOutgoingRequests().removeIf(f -> f.getUUID() == UUID);


        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public void addNewFriendRequest(Friend request, boolean updateDatabase) {
        appUser.getInfo().getIncomingRequests().add(request);
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public void addOutgoingRequest(String userName, int UUID, boolean updateDatabase) {
        appUser.getInfo().getOutgoingRequests().add(new Friend(userName, UUID));
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public void removeOldIncomingRequest(int UUID, boolean updateDatabase) {
        appUser.getInfo().getIncomingRequests().removeIf(f -> f.getUUID() == UUID);
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    public List<Friend> getFriendSuggestions() {
        // todo
        return new ArrayList<>();
    }

}
