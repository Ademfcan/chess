package chessengine.Start;

import chessengine.App;
import chessengine.Enums.FriendEntry;
import chessengine.Enums.UserInfoState;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Misc.ClientsideDataEntry;
import chessengine.Misc.ClientsideFriendDataResponse;
import chessserver.Communication.DatabaseEntry;
import chessserver.Friends.Friend;
import chessserver.Friends.FriendInfo;
import chessserver.User.UserInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
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

    public void reloadUserPanel(UserInfo user,boolean forceReload,boolean isPreviewLoad){
        if(forceReload || currentUserShown != user){
            if(!isPreviewLoad){
                controller.setProfileInfo(user.getProfilePicture(),user.getUserName(),user.getUserelo(),user.getUuid());
                controller.setupOldGamesBox(UserHelperFunctions.readSavedGames(user.getSavedGames()));
            }
            controller.setUserProfileInfo(user.getProfilePicture(),user.getUserName(),user.getUserelo(),user.getUuid());
            controller.setupUserOldGamesBox(UserHelperFunctions.readSavedGames(user.getSavedGames()));

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
        for(ClientsideDataEntry entry : clientsideFriendDataResponse.readDatabaseEntries()){
            String lookupName = entry.getDatabaseEntry().getUserInfo().getUserName();
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

    private void addFriendEntry(VBox container, Region Reference, ClientsideDataEntry entry, FriendEntry entryType){
        DatabaseEntry databaseEntry = entry.getDatabaseEntry();
        HBox lookupEntry = new HBox();
        lookupEntry.setStyle("-fx-background-color: gray; -fx-border-color: black");
        App.bindingController.bindCustom(Reference.widthProperty(),lookupEntry.spacingProperty(),20,.4);

        lookupEntry.setAlignment(Pos.CENTER);
        lookupEntry.prefWidthProperty().bind(Reference.widthProperty());
        App.bindingController.bindCustom(Reference.heightProperty(),lookupEntry.prefHeightProperty(),60,.1);

        // profile picture / preview button
        Pane profileGroup = new Pane();
        profileGroup.prefHeightProperty().bind(lookupEntry.heightProperty());
        profileGroup.prefWidthProperty().bind(profileGroup.heightProperty());
        profileGroup.setOnMouseClicked(e ->{
            changeUserInfoState(UserInfoState.PREVIEW);
            reloadUserPanel(databaseEntry.getUserInfo(),false,true);
            controller.setUserInfoNav(1);

        });
        profileGroup.setStyle("-fx-border-radius: 25;-fx-border-color: black;-fx-border-width: 2");

        ImageView profilePicture = new ImageView(databaseEntry.getUserInfo().getProfilePictureUrl());
        profilePicture.fitHeightProperty().bind(lookupEntry.heightProperty().subtract(5));
        profilePicture.fitWidthProperty().bind(profilePicture.fitHeightProperty());
        profilePicture.layoutXProperty().bind(profileGroup.widthProperty().subtract(profilePicture.fitWidthProperty()).divide(2));
        profilePicture.layoutYProperty().bind(profileGroup.heightProperty().subtract(profilePicture.fitHeightProperty()).divide(2));

        Circle activityIndicator = new Circle();
        activityIndicator.setStroke(Paint.valueOf("White"));
        if(entry.isCurrentlyOnline()){
            activityIndicator.setFill(Paint.valueOf("Green"));
        }
        else{
            activityIndicator.setFill(Paint.valueOf("Gray"));
        }
        activityIndicator.radiusProperty().bind(profilePicture.fitHeightProperty().divide(16));
        activityIndicator.layoutXProperty().bind(profilePicture.layoutXProperty().add(profilePicture.fitWidthProperty()));
        activityIndicator.layoutYProperty().bind(profilePicture.layoutYProperty().add(profilePicture.fitHeightProperty()));
        profileGroup.getChildren().addAll(activityIndicator,profilePicture);


        lookupEntry.getChildren().add(profileGroup);




        // name
        String friendName = databaseEntry.getUserInfo().getUserName();
        int friendUUID = databaseEntry.getUserInfo().getUuid();
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

    /** 0 = friends panel, 1 = incoming requests, 2 = suggested friends**/
    private int currentFriendState = 0;

    public void showFriends(){
        currentFriendState = 0;
        controller.friendsContent.getChildren().clear();
        if(App.userManager.getCurrentFriendsFromServer().readDatabaseEntries().isEmpty()){
            addEmptyEntry(controller.friendsContent,"No friends");
        }
        else{
            for(ClientsideDataEntry friend : App.userManager.getCurrentFriendsFromServer().readDatabaseEntries()){
                addFriendEntry(controller.friendsContent,controller.FriendsPanel,friend,FriendEntry.CONNECTED);
            }
        }
    }

    public void showIncomingRequests(){
        currentFriendState = 1;
        controller.friendsContent.getChildren().clear();
        if(App.userManager.getCurrentIncomingRequestsFromServer().readDatabaseEntries().isEmpty()){
            addEmptyEntry(controller.friendsContent,"No current requests");
        }
        else{
            for(ClientsideDataEntry friend : App.userManager.getCurrentIncomingRequestsFromServer().readDatabaseEntries()){
                addFriendEntry(controller.friendsContent,controller.FriendsPanel,friend,FriendEntry.INCOMINGREQUESTED);
            }
        }
    }
    public void showSuggestedFriends(){
        currentFriendState = 2;
        controller.friendsContent.getChildren().clear();
        if(App.userManager.getCurrentSuggestedFriendsFromServer().readDatabaseEntries().isEmpty()){
            addEmptyEntry(controller.friendsContent,"No current suggestions");
            addEmptyEntry(controller.friendsContent,"Try playing some online games");
        }
        else{
            for(ClientsideDataEntry friend : App.userManager.getCurrentSuggestedFriendsFromServer().readDatabaseEntries()){
                addFriendEntry(controller.friendsContent,controller.FriendsPanel,friend,FriendEntry.UNCONNECTED);
            }

        }
    }

    void clearAllUserPanels(){
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

    public void showCurrentFriendsPanel() {
        switch (currentFriendState){
            case 0:
                showFriends();
                break;
            case 1:
                showIncomingRequests();
                break;
            case 2:
                showSuggestedFriends();
                break;
        }
    }

}
