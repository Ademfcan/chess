package chessengine.Start;

import chessengine.App;
import chessengine.Enums.FriendEntry;
import chessengine.Enums.UserInfoState;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Misc.ClientsideFriendDataResponse;
import chessserver.DatabaseEntry;
import chessserver.Friend;
import chessserver.FriendInfo;
import chessserver.UserInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserInfoManager {
    private static final Logger logger = LogManager.getLogger("User_Info_Manager");
    private StartScreenController controller;
    private UserInfoState currentUserInfoState;
    private UserInfo currentUserShown;
    public UserInfoManager(StartScreenController startScreenController,UserInfoState currentUserInfoState){
        this.controller = startScreenController;
        this.currentUserInfoState = currentUserInfoState;
        changeUserInfoState(this.currentUserInfoState);
    }

    public void changeUserInfoState(UserInfoState newUserInfoState){
        if(currentUserInfoState != newUserInfoState){
            currentUserInfoState = newUserInfoState;
            switch (currentUserInfoState){
                case PREVIEW -> {
                    disableFriendsPanel();
                }
                case LOGGEDIN -> {
                    enableFriendsPanel();
                }
                case SIGNEDOUT -> {
                    disableFriendsPanel();
                }
            }
        }
    }

    public void reloadUserPanel(UserInfo user,boolean forceReload){
        if(forceReload || currentUserShown != user){
            controller.setProfileInfo(user.getProfilePicture(),user.getUserName(),user.getUserelo(),user.getUuid());
            controller.setupOldGamesBox(UserHelperFunctions.readSavedGames(user.getSavedGames()));
            if(currentUserInfoState == UserInfoState.LOGGEDIN){
                App.resyncFriends(true);
                showFriends();
            }
        }
        else{
            logger.debug("Already showing same user!");
        }
    }


    void updateFriendsLookup(ClientsideFriendDataResponse clientsideFriendDataResponse){
        if(clientsideFriendDataResponse == null){
            logger.error("Null friend lookup!");
            return;
        }
        controller.friendsLookupContent.getChildren().clear();
        if(clientsideFriendDataResponse.readDatabaseEntries().isEmpty()){
            return;
        }
        for(DatabaseEntry entry : clientsideFriendDataResponse.readDatabaseEntries()){
            String lookupName = entry.getUserInfo().getUserName();
            if(lookupName.equals(App.userManager.getUserName())){
                continue;
            }
            Friend outgoing = App.userManager.matchOutgoingRequests(lookupName);
            if(outgoing != null){
                addFriendEntry(controller.friendsLookupContent,controller.FriendsPanel,entry, FriendEntry.OUTGOINGREQUESTED);
                continue;
            }
            Friend incoming = App.userManager.matchIncomingRequests(lookupName);
            if(incoming != null){
                addFriendEntry(controller.friendsLookupContent,controller.FriendsPanel,entry,FriendEntry.INCOMINGREQUESTED);
                continue;
            }

            FriendInfo currentFriend = App.userManager.matchExistingFriends(lookupName);
            if(currentFriend != null){
                addFriendEntry(controller.friendsLookupContent,controller.FriendsPanel,entry,FriendEntry.CONNECTED);
                continue;
            }
            addFriendEntry(controller.friendsLookupContent,controller.FriendsPanel,entry,FriendEntry.UNCONNECTED);


        }
    }

    private void addFriendEntry(VBox container, Region Reference, DatabaseEntry entry, FriendEntry entryType){
        HBox lookupEntry = new HBox();
        lookupEntry.setStyle("-fx-background-color: gray; -fx-border-color: black");
        App.bindingController.bindCustom(Reference.widthProperty(),lookupEntry.spacingProperty(),20,.4);

        lookupEntry.setAlignment(Pos.CENTER);
        lookupEntry.prefWidthProperty().bind(Reference.widthProperty());
        App.bindingController.bindCustom(Reference.heightProperty(),lookupEntry.prefHeightProperty(),60,.1);

        // profile picture / preview button
        ImageView profilePicture = new ImageView(entry.getUserInfo().getProfilePictureUrl());
        profilePicture.fitHeightProperty().bind(lookupEntry.heightProperty().subtract(5));
        profilePicture.fitWidthProperty().bind(profilePicture.fitHeightProperty());
        profilePicture.setOnMouseClicked(e ->{
            changeUserInfoState(UserInfoState.PREVIEW);
            reloadUserPanel(entry.getUserInfo(),false);
            controller.setUserInfoNav(1);

        });

        lookupEntry.getChildren().add(profilePicture);




        // name
        String friendName = entry.getUserInfo().getUserName();
        int friendUUID = entry.getUserInfo().getUuid();
        Label name = new Label(friendName);
        App.bindingController.bindSmallText(name,false,"black");
        lookupEntry.getChildren().add(name);

        // action button / graphic
        // two types require action buttons. Either to send a request, or accept a incoming request
        if(entryType ==FriendEntry.UNCONNECTED || entryType == FriendEntry.INCOMINGREQUESTED){
            Button actionButton = new Button();


            actionButton.prefHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
            actionButton.prefWidthProperty().bind(actionButton.prefHeightProperty());
            ImageView sendRequestGraphic = new ImageView(entryType.urlString);
            sendRequestGraphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
            sendRequestGraphic.fitWidthProperty().bind(sendRequestGraphic.fitHeightProperty());
            actionButton.setGraphic(sendRequestGraphic);
            lookupEntry.getChildren().add(actionButton);

            if(entryType.equals(FriendEntry.UNCONNECTED)){
                actionButton.setOnMouseClicked(e-> {
                    App.sendFriendRequest(friendName,() ->{
                        lookupEntry.getChildren().remove(actionButton);
                        System.out.println("Send friend request to: " + friendName);
                        ImageView updatedRequestGraphic = new ImageView(FriendEntry.OUTGOINGREQUESTED.urlString);
                        updatedRequestGraphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
                        updatedRequestGraphic.fitWidthProperty().bind(updatedRequestGraphic.fitHeightProperty());
                        lookupEntry.getChildren().add(updatedRequestGraphic);
                    });

                });
            }
            else{
                actionButton.setOnMouseClicked(e->{
                    App.acceptIncomingRequest(friendName,friendUUID,() ->{
                        System.out.println("Accepted friend request from: " + friendName + " with UUID: " + friendUUID );
                        lookupEntry.getChildren().remove(actionButton);
                        ImageView updatedRequestGraphic = new ImageView(FriendEntry.CONNECTED.urlString);
                        updatedRequestGraphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
                        updatedRequestGraphic.fitWidthProperty().bind(updatedRequestGraphic.fitHeightProperty());
                        lookupEntry.getChildren().add(updatedRequestGraphic);
                    });
                });
            }
        }
        else{
            ImageView graphic = new ImageView(entryType.urlString);
            graphic.fitWidthProperty().bind(graphic.fitHeightProperty());
            graphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
            lookupEntry.getChildren().add(graphic);
        }

        container.getChildren().add(lookupEntry);
    }

    private void addEmptyEntry(VBox container,String message){
        Label label = new Label(message);
        App.bindingController.bindSmallText(label,false,"Black");
        container.getChildren().add(label);
    }


    public void showFriends(){
        controller.friendsContent.getChildren().clear();
        if(App.userManager.getCurrentFriendsFromServer().readDatabaseEntries().isEmpty()){
            addEmptyEntry(controller.friendsContent,"No friends");
        }
        else{
            for(DatabaseEntry friend : App.userManager.getCurrentFriendsFromServer().readDatabaseEntries()){
                addFriendEntry(controller.friendsContent,controller.FriendsPanel,friend,FriendEntry.CONNECTED);
            }
        }
    }

    public void showIncomingRequests(){
        controller.friendsContent.getChildren().clear();
        if(App.userManager.getCurrentIncomingRequestsFromServer().readDatabaseEntries().isEmpty()){
            addEmptyEntry(controller.friendsContent,"No current requests");
        }
        else{
            for(DatabaseEntry friend : App.userManager.getCurrentIncomingRequestsFromServer().readDatabaseEntries()){
                addFriendEntry(controller.friendsContent,controller.FriendsPanel,friend,FriendEntry.INCOMINGREQUESTED);
            }
        }
    }
    public void showSuggestedFriends(){
        controller.friendsContent.getChildren().clear();
        if(App.userManager.getCurrentSuggestedFriendsFromServer().readDatabaseEntries().isEmpty()){
            addEmptyEntry(controller.friendsContent,"No current suggestions");
            addEmptyEntry(controller.friendsContent,"Try playing some online games");
        }
        else{
            for(DatabaseEntry friend : App.userManager.getCurrentSuggestedFriendsFromServer().readDatabaseEntries()){
                addFriendEntry(controller.friendsContent,controller.FriendsPanel,friend,FriendEntry.UNCONNECTED);
            }

        }
    }

    private void clearAllUserPanels(){
        controller.friendsLookup.getChildren().clear();
        controller.friendsLookupInput.clear();
        controller.friendsContent.getChildren().clear();
        controller.userOldGamesContent.getChildren().clear();
        controller.userInfoPfp.setImage(null);
    }

    private void disableFriendsPanel(){
        clearAllUserPanels();
        controller.FriendsButton.setDisable(true);
        controller.RequestsButton.setDisable(true);
        controller.SuggestedFriendsButton.setDisable(true);
        controller.friendsLookupInput.setDisable(true);
        controller.friendsLookupButton.setDisable(true);
    }

    void enableFriendsPanel(){
        controller.FriendsButton.setDisable(false);
        controller.RequestsButton.setDisable(false);
        controller.SuggestedFriendsButton.setDisable(false);
        controller.friendsLookupInput.setDisable(false);
        controller.friendsLookupButton.setDisable(false);
    }
}
