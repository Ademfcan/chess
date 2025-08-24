package chessengine.Start;

import chessengine.App;
import chessengine.Enums.StartScreenState;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.CommonIcons;
import chessengine.Graphics.GraphicsFunctions;
import chessengine.Graphics.StartScreenSubWindow;
import chessserver.Misc.ChessConstants;
import chessserver.User.UserInfo;
import chessserver.User.UserWGames;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

public class NavigationScreen extends StartScreenSubWindow {
    private StartScreenState currentState;
    private StartScreenState lastStateBeforeUserSettings;

    public StartScreenState getCurrentState() {
        return currentState;
    }

    public NavigationScreen(StartScreenController controller) {
        super(controller);
    }

    private void hideAllScreensButtons() {
        GraphicsFunctions.toggleHideAndDisable(controller.campaignScreen, true);
        GraphicsFunctions.toggleHideAndDisable(controller.pgnSelectionScreen, true);
        GraphicsFunctions.toggleHideAndDisable(controller.mainSelectionScreen, true);
        GraphicsFunctions.toggleHideAndDisable(controller.multiplayerSelectionScreen, true);
        GraphicsFunctions.toggleHideAndDisable(controller.profileScreen, true);
        GraphicsFunctions.toggleHideAndDisable(controller.generalSettingsScreen, true);
        GraphicsFunctions.toggleHideAndDisable(controller.extraModesScreen, true);
    }

    public void setSelection(StartScreenState state) {
        hideAllScreensButtons();
        switch (state) {
            case PGN -> {
                GraphicsFunctions.toggleHideAndDisable(controller.pgnSelectionScreen, false);
            }
            case REGULAR -> {
                GraphicsFunctions.toggleHideAndDisable(controller.mainSelectionScreen, false);
            }
            case MULTIPLAYER -> {
                GraphicsFunctions.toggleHideAndDisable(controller.multiplayerSelectionScreen, false);
            }
            case USERSETTINGS -> {
                GraphicsFunctions.toggleHideAndDisable(controller.profileScreen, false);
            }
            case GENERALSETTINGS -> {
                GraphicsFunctions.toggleHideAndDisable(controller.generalSettingsScreen, false);
            }
            case EXTRA -> {
                GraphicsFunctions.toggleHideAndDisable(controller.extraModesScreen, false);
            }
            case CAMPAIGN -> {
                GraphicsFunctions.toggleHideAndDisable(controller.campaignScreen, false);
            }
        }
        this.currentState = state;
    }

    @Override
    public void initLayout(){
        BindingController.bindCustom(
                controller.sideButtons.widthProperty(),
                controller.profileButton.fitWidthProperty(),
                150, .5);
        controller.profileButton.fitHeightProperty().bind(controller.profileButton.fitWidthProperty());


        controller.statusBox.prefWidthProperty().bind(controller.profileButton.fitWidthProperty());
        controller.statusBox.prefHeightProperty().bind(controller.profileButton.fitHeightProperty());

        controller.statusLabel.radiusProperty().bind(controller.profileButton.fitWidthProperty().divide(15));
    }


    @Override
    public void afterInit(){
        // toggle logic
        controller.profileButton.setOnMouseClicked(e -> {
            if (getCurrentState() == StartScreenState.USERSETTINGS) {
                setSelection(lastStateBeforeUserSettings);
            } else {
                lastStateBeforeUserSettings = currentState;
                setSelection(StartScreenState.USERSETTINGS);
            }
        });

        controller.campaignButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.CAMPAIGN);
            controller.campaignScreenM.scrollToPlayerTier(App.userManager.userInfoManager.getCampaignProgress());
        });
        BindingController.bindMediumText(controller.campaignButton);
        controller.campaignButton.setGraphic(new HBox(5, CommonIcons.campaign1, CommonIcons.campaign2));

        controller.localButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.REGULAR);
        });
        BindingController.bindMediumText(controller.localButton);
        controller.localButton.setGraphic(new HBox(5, CommonIcons.local1, CommonIcons.local2));

        controller.pgnButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.PGN);
        });
        BindingController.bindMediumText(controller.pgnButton);
        controller.pgnButton.setGraphic(CommonIcons.pgn);

        controller.multiplayerButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.MULTIPLAYER);
        });
        BindingController.bindMediumText(controller.multiplayerButton);
        controller.multiplayerButton.setGraphic(new HBox(5, CommonIcons.online1, CommonIcons.online2));

        controller.extraModesButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.EXTRA);
        });
        BindingController.bindMediumText(controller.extraModesButton);

        controller.extraModesButton.setGraphic(CommonIcons.extramodes);

        controller.settingsButton.setOnMouseClicked(e -> {
            setSelection(StartScreenState.GENERALSETTINGS);
        });
        BindingController.bindMediumText(controller.settingsButton);
        controller.settingsButton.setGraphic(CommonIcons.settings);
    }

    private void setStatusLabel(boolean isOnline) {
        if (isOnline) {
            controller.statusLabel.setFill(Paint.valueOf("Green"));
        } else {
            controller.statusLabel.setFill(Paint.valueOf("Gray"));
        }

    }

    @Override
    public void updateWithUser(UserWGames userWGames) {
        controller.profileButton.setImage(new Image(userWGames.user().userInfo().getProfilePicture().urlString));
    }


    @Override
    public void onOnline(){
        setStatusLabel(true);
    }

    @Override
    public void onOffline(){
        setStatusLabel(false);
    }
}
