package chessengine.Start;

import chessengine.App;
import chessengine.Enums.FriendEntry;
import chessengine.Enums.Window;
import chessengine.FXInitQueue;
import chessengine.Functions.UserHelperFunctions;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.GraphicsFunctions;
import chessengine.Graphics.OnFriendUpdate;
import chessengine.Graphics.StartScreenSubWindow;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.Enums.ProfilePicture;
import chessserver.Friends.FriendDataResponse;
import chessserver.Friends.ServerFriendData;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageTypes.DatabaseMessageTypes;
import chessserver.Net.MessageTypes.UserMessageTypes;
import chessserver.Net.Payload;
import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import chessserver.User.UserInfo;
import chessserver.User.UserWGames;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProfileScreen extends StartScreenSubWindow implements OnFriendUpdate {
    private static final Logger logger = LogManager.getLogger("User_Info_Manager");
    private final HashMap<String, FriendDataResponse> lookupCache = new HashMap<>();
    private UserInfoState currentUserInfoState;
    private UserInfo currentUserShown;
    private FriendState lastFriendState = FriendState.FRIENDS;


    public ProfileScreen(StartScreenController startScreenController, UserInfoState currentUserInfoState) {
        super(startScreenController);
        this.currentUserInfoState = currentUserInfoState;

        FXInitQueue.runAfterInit(() -> {
            App.scheduledExecutorService.scheduleAtFixedRate(App.clientDatabaseMessageHandler::synchronizeWithServer, 0, 2, TimeUnit.MINUTES);
        });
    }

    @Override
    public void initLayout() {
        controller.userInfoPfp.fitWidthProperty().bind(controller.userInfoPfp.fitHeightProperty());
        // keep user bottom user info box symmetric
        controller.userOldGamesLabelContainer.prefHeightProperty().bind(controller.friendsNavBar.heightProperty());

        // text size/color bindings
        BindingController.bindCustom(
                controller.topUserInfoBox.heightProperty(),
                controller.userInfoPfp.fitHeightProperty(),
                150, 1);
        BindingController.bindSmallText(controller.friendsLookupInput, "Black");
        BindingController.bindSmallText(controller.FriendsButton, "Black");
        BindingController.bindSmallText(controller.RequestsButton, "Black");
        BindingController.bindSmallText(controller.SuggestedFriendsButton, "Black");
        BindingController.bindSmallText(controller.friendsLookupButton, "Black");
        BindingController.bindMediumText(controller.userInfoUserName, "Black");
        BindingController.bindSmallText(controller.userInfoUUID, "Black");
        BindingController.bindSmallText(controller.userInfoUserElo, "Black");
        BindingController.bindSmallText(controller.userInfoRank, "Black");
        BindingController.bindSmallText(controller.userOldGamesLabel, "Black");
        BindingController.bindXLargeText(controller.userOfflineErrorTitle, "Black");
        BindingController.bindMediumText(controller.userOfflineErrorLabel, "Black");
        BindingController.bindSmallText(controller.userOfflineErrorButton, "Black");

    }

    @Override
    public void initGraphics() {
        controller.userOldGamesContent.setStyle("-fx-background-color: gray");
        controller.friendsLookup.setStyle("-fx-background-color: gray");
        controller.friendsContent.setStyle("-fx-background-color: gray");


    }

    @Override
    public void afterInit() {


        setupButtonActions();
    }



    public void setupButtonActions() {
        controller.loginButton.setOnMouseClicked(e -> {
            loginRoutine();
        });

        controller.createAccountButton.setOnMouseClicked(e -> {
            signupRoutine();
        });

        // navigation
        controller.signUpInsteadButton.setOnMouseClicked(e -> {
            setProfileState(ProfileState.CREATEACC);
        });

        controller.loginInsteadButton.setOnMouseClicked(e -> {
            setProfileState(ProfileState.LOGIN);
        });

        controller.LogoutButton.setOnMouseClicked(e -> {
            App.logout();
            App.triggerUpdateUser();
        });



        controller.userInfoPfp.setOnMouseClicked(e -> {
//            ProfilePicture nextPicture = getNextPfp(App.userManager.userInfoManager.getUserPfp());
//            controller.userInfoPfp.setImage(new Image(nextPicture.urlString));
//            App.userManager.userInfoManager.updateUserPfp(nextPicture);
            App.clientDatabaseMessageHandler.synchronizeWithServer();
        });

        controller.friendsLookupInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                searchUserRoutine(newValue);
            }
        });

        controller.FriendsButton.setOnMouseClicked(e -> {
            setFriendsPanelState(FriendState.FRIENDS);
        });

        controller.RequestsButton.setOnMouseClicked(e -> {
            setFriendsPanelState(FriendState.REQUESTS);
        });

        controller.SuggestedFriendsButton.setOnMouseClicked(e -> {
            setFriendsPanelState(FriendState.SUGGESTED);
        });

        controller.friendsLookupButton.setOnMouseClicked(e -> {
            setFriendsPanelState(FriendState.LOOKUP);
        });

        controller.userOfflineErrorButton.setOnMouseClicked(e -> {
            App.reconnectClient();
        });

        controller.exitPreview.setOnMouseClicked(e -> {
            changeUserInfoState(UserInfoState.LOGGEDIN);
            setProfileState(ProfileState.USERINFO);
            updateWithUser(App.getCurrentUserWGames());
            setUserInfoHyperlinks(false);
        });
    }

    @Override
    public void resetState() {
        resetFriendPanels();
        resetProfileUserInfo();
        resetUserCreationInputs();
    }


    public void resetUserCreationInputs() {
        controller.nameInput.clear();
        controller.passwordInput.clear();
        controller.createUsername.clear();
        controller.createPassword.clear();
    }

    void resetFriendPanels() {
        controller.friendsLookup.getChildren().clear();
        controller.friendsLookupInput.clear();
        controller.friendsLookupContent.getChildren().clear();
        controller.friendsContent.getChildren().clear();

        controller.userInfoPfp.setImage(null);
    }

    void resetProfileUserInfo() {
        controller.userInfoPfp.setImage(new Image(ProfilePicture.DEFAULT.urlString));
        controller.userInfoUserName.setText("[Name]");
        controller.userInfoUserElo.setText("[ELO]");
        controller.userInfoUUID.setText("[ID]");
        controller.userInfoRank.setText("[Rank]");
    }



    private ProfilePicture getNextPfp(ProfilePicture current) {
        int next = (current.ordinal() + 1) % ProfilePicture.values().length;
        return ProfilePicture.values()[next];
    }

    private boolean checkMarkInvalid(TextField inputBox, String identifier) {
        // clear any existing "errors"
        inputBox.setPromptText("");
        inputBox.setStyle("");

        String input = inputBox.getText();
        if (input.isEmpty()) {
            inputBox.setPromptText("please enter a " + identifier);
            inputBox.setStyle("-fx-border-color: red");
            return true;
        } else if (input.contains(",")) {
            inputBox.clear();
            inputBox.setPromptText(identifier + " may not contain a comma");
            inputBox.setStyle("-fx-border-color: red");
            return true;
        } else if (input.length() >= 255) {
            inputBox.clear();
            inputBox.setPromptText(identifier + " too long");
            inputBox.setStyle("-fx-border-color: red");
            return true;
        }
        return false;
    }


    private void loginRoutine() {
        if (checkMarkInvalid(controller.nameInput, "Username") ||
                checkMarkInvalid(controller.passwordInput, "Password")) return;

        App.userManager.login(controller.nameInput.getText(), controller.passwordInput.getText());
        controller.nameInput.clear();
        controller.passwordInput.clear();
    }

    public void signupRoutine() {
        if (checkMarkInvalid(controller.createUsername, "Username") ||
            checkMarkInvalid(controller.createPassword, "Password") ||
            checkMarkInvalid(controller.createEmail, "Email")) {
            return;
        }

        String usernameText = controller.createUsername.getText();
        String emailText = controller.createEmail.getText();
        String passwordText = controller.createPassword.getText();


        App.clientUserMessageHandler.sendMessage(new MessageConfig(new Message(UserMessageTypes.ClientRequest.CHECKUSERNAMEOPEN, new Payload.StringPayload(usernameText))).onDataResponse((Payload.BooleanPayload isOpen) -> {
            Platform.runLater(() -> usernamePresentResponse(isOpen.payload(), usernameText, emailText, passwordText));
        }));
    }

    public void usernamePresentResponse(boolean isUsernameOpen, String lastUsername, String lastEmail, String lastPassword) {
        if (lastUsername != null && lastPassword != null) {
            if (!isUsernameOpen) {
                controller.createUsername.clear();
                controller.createUsername.setPromptText("This username is already taken!");
                controller.createUsername.setStyle("-fx-border-color: red");
            } else {
                controller.createUsername.clear();
                controller.createPassword.clear();
                controller.createEmail.clear();
                App.messager.sendMessage("Creating account", Window.Start);
                App.userManager.signup(lastUsername, lastEmail, lastPassword, true);
            }
        } else {
            logger.error("Getting username present response without correct last values!");
        }
    }

    public void searchUserRoutine(String inputText) {
        controller.friendsLookupContent.getChildren().clear();
        Label noResultsLabel = new Label("No Results");
        BindingController.bindSmallText(noResultsLabel, "Black");
        controller.friendsLookupContent.getChildren().add(noResultsLabel);

        inputText = inputText.trim();

        if (lookupCache.containsKey(inputText)) {
            updateFriendsLookup(lookupCache.get(inputText));
        } else {
            String finalInputText = inputText;
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.MATCHALLUSERNAMES, new Payload.StringPayload(inputText))).onDataResponse(
                    (UserMessagePayloadTypes.FriendDataResponsePayload dataResponse) -> {
                        lookupCache.put(finalInputText, dataResponse.response());
                        Platform.runLater(() -> updateFriendsLookup(dataResponse.response()));
                    }
            ));

        }
    }


    private void showAFriendsBox(FriendDataResponse friendDataResponse, String... emptyMessages) {
        controller.friendsContent.getChildren().clear();
        if (friendDataResponse.dataResponse().isEmpty()) {
            for (String emptyMessage : emptyMessages) {
                addEmptyEntry(controller.friendsContent, emptyMessage);
            }
        } else {
            for (ServerFriendData friend : friendDataResponse.dataResponse()) {
                addFriendEntry(controller.friendsContent, controller.FriendsPanel, friend, FriendEntry.UNCONNECTED);
            }

        }
    }

    private void setFriendsPanel(boolean isLookup) {
        GraphicsFunctions.toggleHideAndDisable(controller.friendsContent, isLookup);
        GraphicsFunctions.toggleHideAndDisable(controller.friendsLookup, !isLookup);
    }

    private void setUserInfoHyperlinks(boolean isPreview) {
        GraphicsFunctions.toggleHideAndDisable(controller.hyperlinkBox, isPreview);
        GraphicsFunctions.toggleHideAndDisable(controller.previewHyperlinkBox, !isPreview);
    }

    private void disableFriendsPanel(String message) {
        GraphicsFunctions.toggleHideAndDisable(controller.loggedInFriendsBox, true);

        controller.disabledFriendsMessage.setText(message);
        GraphicsFunctions.toggleHideAndDisable(controller.disabledFriendsBox, false);
    }

    private void enableFriendsPanel() {
        controller.disabledFriendsMessage.setText("");
        GraphicsFunctions.toggleHideAndDisable(controller.disabledFriendsBox, true);

        GraphicsFunctions.toggleHideAndDisable(controller.loggedInFriendsBox, false);
    }


    public void changeUserInfoState(UserInfoState newUserInfoState) {
        if (currentUserInfoState != newUserInfoState) {
            currentUserInfoState = newUserInfoState;
            switch (currentUserInfoState) {
                case PREVIEW, SIGNEDOUT -> {
                    disableFriendsPanel(newUserInfoState.toString());
                }
                case LOGGEDIN -> {
                    enableFriendsPanel();
                }
            }
        }
    }

    private void setupUserOldGamesBox(List<GameInfo> gamesToLoad) {
        controller.userOldGamesContent.getChildren().clear();
        for (GameInfo g : gamesToLoad) {
            controller.AddNewGameToSaveGui(g, controller.userOldGamesContent);
        }
    }


    public void setProfileUserInfo(UserInfo userInfo) {
        controller.userInfoPfp.setImage(new Image(userInfo.getProfilePicture().urlString));
        controller.userInfoUserName.setText("Name: " + userInfo.getUserName());
        controller.userInfoUserElo.setText("ELO: " + userInfo.getUserelo());
        controller.userInfoUUID.setText("ID: " + userInfo.getUuid());
        controller.userInfoRank.setText("Rank: -"); // TODO
    }



    void updateFriendsLookup(FriendDataResponse friendInfo) {
        if (friendInfo == null) {
            logger.error("Null friend lookup!");
            return;
        }
        controller.friendsLookupContent.getChildren().clear();


        for (ServerFriendData friendData : friendInfo.dataResponse()) {
            if (friendData.UUID().equals(App.userManager.userInfoManager.getUUID())) {
                // dont display self in lookup
                continue;
            }

            Relation relation = getFriendRelation(friendData.UUID());

            switch (relation) {
                case FRIEND -> {
                    addFriendEntry(controller.friendsLookupContent, controller.FriendsPanel, friendData, FriendEntry.CONNECTED);
                }
                case INCOMING_REQUESTED -> {
                    addFriendEntry(controller.friendsLookupContent, controller.FriendsPanel, friendData, FriendEntry.INCOMINGREQUESTED);
                }
                case OUTGOING_REQUESTED -> {
                    addFriendEntry(controller.friendsLookupContent, controller.FriendsPanel, friendData, FriendEntry.OUTGOINGREQUESTED);
                }
                case NO_RELATION -> {
                    addFriendEntry(controller.friendsLookupContent, controller.FriendsPanel, friendData, FriendEntry.UNCONNECTED);
                }
            }
        }
    }

    private void addFriendEntry(VBox container, Region Reference, ServerFriendData entry, FriendEntry entryType) {
        UserWGames userWGames = entry.dataEntry();
        HBox lookupEntry = new HBox();
        lookupEntry.setStyle("-fx-background-color: gray; -fx-border-color: black");
        BindingController.bindCustom(Reference.widthProperty(), lookupEntry.spacingProperty(), 20, .4);

        lookupEntry.setAlignment(Pos.CENTER);
        lookupEntry.prefWidthProperty().bind(Reference.widthProperty());
        BindingController.bindCustom(Reference.heightProperty(), lookupEntry.prefHeightProperty(), 60, .1);

        // profile picture / preview button
        Pane profileGroup = new Pane();
        profileGroup.prefHeightProperty().bind(lookupEntry.heightProperty());
        profileGroup.prefWidthProperty().bind(profileGroup.heightProperty());
        profileGroup.setOnMouseClicked(e -> {
            changeUserInfoState(UserInfoState.PREVIEW);
            setProfileState(ProfileState.USERINFO);
            updateWithUser(userWGames);
            setUserInfoHyperlinks(true);
        });
        profileGroup.setStyle("-fx-border-radius: 25;-fx-border-color: black;-fx-border-width: 2");

        ImageView profilePicture = new ImageView(userWGames.user().userInfo().getProfilePictureUrl());
        profilePicture.fitHeightProperty().bind(lookupEntry.heightProperty().subtract(5));
        profilePicture.fitWidthProperty().bind(profilePicture.fitHeightProperty());
        profilePicture.layoutXProperty().bind(profileGroup.widthProperty().subtract(profilePicture.fitWidthProperty()).divide(2));
        profilePicture.layoutYProperty().bind(profileGroup.heightProperty().subtract(profilePicture.fitHeightProperty()).divide(2));

        Circle activityIndicator = new Circle();
        if (entry.isOnline()) {
            activityIndicator.setFill(Paint.valueOf("Green"));
        } else {
            activityIndicator.setFill(Paint.valueOf("Gray"));
        }
        activityIndicator.radiusProperty().bind(profilePicture.fitHeightProperty().divide(16));
        activityIndicator.layoutXProperty().bind(profilePicture.layoutXProperty().add(profilePicture.fitWidthProperty()));
        activityIndicator.layoutYProperty().bind(profilePicture.layoutYProperty().add(profilePicture.fitHeightProperty()));
        profileGroup.getChildren().addAll(activityIndicator, profilePicture);

        lookupEntry.getChildren().add(profileGroup);


        // name
        String friendName = entry.currentUsername();
        UUID friendUUID = entry.UUID();

        Label name = new Label(friendName);
        BindingController.bindSmallText(name, "black");
        lookupEntry.getChildren().add(name);

        // action button / graphic
        // two types require action buttons. Either to send a request, or accept a incoming request
        if (entryType == FriendEntry.UNCONNECTED || entryType == FriendEntry.INCOMINGREQUESTED) {
            Button actionButton = new Button();


            actionButton.prefHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
            actionButton.prefWidthProperty().bind(actionButton.prefHeightProperty());
            ImageView sendRequestGraphic = new ImageView(entryType.urlString);
            sendRequestGraphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
            sendRequestGraphic.fitWidthProperty().bind(sendRequestGraphic.fitHeightProperty());
            actionButton.setGraphic(sendRequestGraphic);
            lookupEntry.getChildren().add(actionButton);

            if (entryType == FriendEntry.UNCONNECTED) {
                actionButton.setOnMouseClicked(e -> {
                    App.clientDatabaseMessageHandler.sendMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.SENDFRIENDREQUEST, new Payload.StringPayload(friendName))));

                    lookupEntry.getChildren().remove(actionButton);
                    System.out.println("Send friend request to: " + friendName);
                    ImageView updatedRequestGraphic = new ImageView(FriendEntry.OUTGOINGREQUESTED.urlString);
                    updatedRequestGraphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
                    updatedRequestGraphic.fitWidthProperty().bind(updatedRequestGraphic.fitHeightProperty());
                    lookupEntry.getChildren().add(updatedRequestGraphic);

                });
            } else {
                actionButton.setOnMouseClicked(e -> {
                    App.clientDatabaseMessageHandler.sendMessage(new MessageConfig(new Message(DatabaseMessageTypes.ClientRequest.SENDACCEPTEDFRIENDREQUEST, new DatabaseMessagePayloadTypes.UUIDPayload(friendUUID))));

                    System.out.println("Accepted friend request from: " + friendName + " with UUID: " + friendUUID);
                    lookupEntry.getChildren().remove(actionButton);
                    ImageView updatedRequestGraphic = new ImageView(FriendEntry.CONNECTED.urlString);
                    updatedRequestGraphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
                    updatedRequestGraphic.fitWidthProperty().bind(updatedRequestGraphic.fitHeightProperty());
                    lookupEntry.getChildren().add(updatedRequestGraphic);
                });
            }
        } else {
            ImageView graphic = new ImageView(entryType.urlString);
            graphic.fitWidthProperty().bind(graphic.fitHeightProperty());
            graphic.fitHeightProperty().bind(lookupEntry.heightProperty().divide(1.1));
            lookupEntry.getChildren().add(graphic);
        }

        container.getChildren().add(lookupEntry);
    }

    private void addEmptyEntry(VBox container, String message) {
        Label label = new Label(message);
        BindingController.bindSmallText(label, "Black");
        container.getChildren().add(label);
    }


    public void showCurrentFriendsPanel() {
        setFriendsPanelState(lastFriendState);
    }


    public void setProfileState(ProfileState profileState) {
        GraphicsFunctions.toggleHideAndDisable(controller.accountCreationPage, profileState != ProfileState.CREATEACC);
        GraphicsFunctions.toggleHideAndDisable(controller.loginPage, profileState != ProfileState.LOGIN);
        GraphicsFunctions.toggleHideAndDisable(controller.userInfoPage, profileState != ProfileState.USERINFO);
        GraphicsFunctions.toggleHideAndDisable(controller.userOfflineScreen, profileState != ProfileState.OfflineAndLoggedout);
    }

    public void setFriendsPanelState(FriendState friendState) {
        setFriendsPanel(friendState == FriendState.LOOKUP);

        switch (friendState) {
            case FRIENDS:
                showFriends();
                break;
            case REQUESTS:
                showIncomingRequests();
                break;
            case SUGGESTED:
                showSuggestedFriends();
                break;
        }
    }

    public void showFriends() {
        lastFriendState = FriendState.FRIENDS;
        showAFriendsBox(currentFriends,
                "No friends", "Try sending a friend request");
    }

    public void showIncomingRequests() {
        lastFriendState = FriendState.REQUESTS;
        showAFriendsBox(currentIncomingRequests,
                "No current friend requests");
    }

    public void showSuggestedFriends() {
        lastFriendState = FriendState.SUGGESTED;
        showAFriendsBox(currentSuggestedFriends,
                "No current suggestions", "Try playing some online games");
    }

    @Override
    public void updateWithUser(UserWGames userWGames) {
        setProfileUserInfo(userWGames.user().userInfo());
        setupUserOldGamesBox(userWGames.games());

        clearCurrentResponses();
        currentFriends = UserHelperFunctions.createPlaceholderFriends(userWGames.user().userInfo().getFriends());

    }

    private boolean online = false;
    private boolean loggedin = false;

    @Override
    public void onOnline(){
        online = true;

        if(!loggedin){
            setProfileState(ProfileState.LOGIN);
        }
        else{
            setProfileState(ProfileState.USERINFO);
        }
    }

    @Override
    public void onOffline() {
        online = false;

        setProfileState(ProfileState.OfflineAndLoggedout);
    }

    @Override
    public void onLogin() {
        loggedin = true;
        if(online){
            setProfileState(ProfileState.USERINFO);
        }
    }

    @Override
    public void onLogout() {
        loggedin = false;
        if(online){
            setProfileState(ProfileState.LOGIN);
        }
    }

    // defaults

    private void clearCurrentResponses(){
        currentFriends = FriendDataResponse.empty();
        currentOutgoingRequests = FriendDataResponse.empty();
        currentIncomingRequests = FriendDataResponse.empty();
        currentSuggestedFriends = FriendDataResponse.empty();
    }

    private FriendDataResponse currentFriends = FriendDataResponse.empty();
    private FriendDataResponse currentOutgoingRequests = FriendDataResponse.empty();
    private FriendDataResponse currentIncomingRequests = FriendDataResponse.empty();
    private FriendDataResponse currentSuggestedFriends = FriendDataResponse.empty();

;
    @Override
    public void onCurrentFriends(FriendDataResponse response) {
        currentFriends = response;
    }

    @Override
    public void onCurrentOutgoingRequests(FriendDataResponse response) {
        currentOutgoingRequests = response;
    }

    @Override
    public void onCurrentIncomingRequests(FriendDataResponse response) {
        currentIncomingRequests = response;
    }

    @Override
    public void onSuggestedFriends(FriendDataResponse response) {
        currentSuggestedFriends = response;
    }

    public Relation getFriendRelation(UUID friend) {
        if (App.userManager.userInfoManager.getAppUser().getFriends().stream().anyMatch(f -> f.getUUID().equals(friend))) {
            return Relation.FRIEND;
        }
        if (currentOutgoingRequests.dataResponse().stream().anyMatch(f -> f.UUID().equals(friend))) {
            return Relation.OUTGOING_REQUESTED;
        }
        if (currentIncomingRequests.dataResponse().stream().anyMatch(f -> f.UUID().equals(friend))) {
            return Relation.INCOMING_REQUESTED;
        }
        return Relation.NO_RELATION;
    }

    public enum Relation {
        FRIEND, OUTGOING_REQUESTED, INCOMING_REQUESTED, NO_RELATION
    }


    public enum ProfileState {
        CREATEACC,
        LOGIN,
        USERINFO,
        OfflineAndLoggedout
    }

    public enum FriendState {
        FRIENDS,
        REQUESTS,
        SUGGESTED,
        LOOKUP
    }

    public enum UserInfoState {
        LOGGEDIN,
        PREVIEW,
        SIGNEDOUT
    }

}
