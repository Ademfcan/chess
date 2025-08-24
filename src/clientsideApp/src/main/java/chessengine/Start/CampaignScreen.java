package chessengine.Start;

import chessengine.App;
import chessengine.FXInitQueue;
import chessengine.Graphics.AppSubWindow;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.StartScreenSubWindow;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Communication.User;
import chessserver.Enums.CampaignAttempt;
import chessserver.User.CampaignProgress;
import chessserver.Enums.CampaignTier;
import chessserver.Enums.ProfilePicture;
import chessserver.User.UserWGames;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CampaignScreen extends StartScreenSubWindow {

    private final DoubleBinding[] cloudYBindings = new DoubleBinding[CampaignTier.values().length];
    private final double[] vvaluesForTiers = new double[CampaignTier.values().length];
    private final String unfilledUrl = "/ChessAssets/StartScreenPawn/pawnUnfilled.png";
    private final String filledUrl = "/ChessAssets/StartScreenPawn/pawnFilled.png";
    private final Image unfilledPawn = new Image(unfilledUrl);
    private final Image filledPawn = new Image(filledUrl);
    private final String cloudUrl = "BackgroundImages/cloud.png";
    private final List<VBox[]> infoContainers = new ArrayList<>(CampaignTier.values().length);
    private final List<DoubleBinding[]> yLayoutBindings = new ArrayList<>(CampaignTier.values().length);

    // magic number soup (pretty much all denoting ratios) -----------
    private final float widthToJump = .50f;
    private final float widthToXoffset = .05f;
    private final float widthToOffsetY = .01f;
    private final float widthToLevelRadius = .10f;
    private final float widthToLevelInfoWidth = .37f;
    private final float widthToLevelInfoHeight = .33f;
    private final float widthToInfoSpacing = .1f;
    private final float infoWidthToLvlDiffWidth = .5f;
    private final float infoHeightToLvlDiffHeight = .2f;
    private final float infoWidthToLvlBtnWidth = .5f;
    private final float infoHeightToLvlBtnHeight = .2f;
    private final float infoWidthToPawnWidth = .1f;
    private final float widthToTierSpacerHeight = .5f;
    private final float widthToTierSpacerSpaceBetweenLevelAndSpacer = .03f;

    private final Random random = new Random();
    private final Logger logger = LogManager.getLogger(this.toString());
    private CampaignTier currentScrollerTier = null;
    private DoubleBinding yJumpPerLevel;
    private DoubleBinding tierSpacerSpaceBetween;

    public CampaignScreen(StartScreenController startScreenController) {
        super(startScreenController);

        FXInitQueue.runAfterInit(() ->{
            drawLevels(controller.levelContainer.widthProperty());
            setLevelUnlocksBasedOnProgress(App.userManager.userInfoManager.getCampaignProgress());
            refreshVvaluesForTiers();

            setUpChangeBgListener();
            controller.mainArea.widthProperty().addListener(e -> refreshVvaluesForTiers());
            controller.mainArea.heightProperty().addListener(e -> refreshVvaluesForTiers());

        });
    }

    @Override
    public void initLayout() {
        controller.campaignScreen.prefWidthProperty().bind(controller.mainArea.widthProperty());
        controller.campaignScreen.prefHeightProperty().bind(controller.mainArea.heightProperty());
        controller.campaignScroller.prefWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.campaignScroller.prefHeightProperty().bind(controller.campaignScreen.prefHeightProperty());
        controller.campaignStack.prefWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.campaignStack.prefHeightProperty().bind(controller.campaignScreen.prefHeightProperty());
        controller.campaignBackground.fitWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.campaignBackground.fitHeightProperty().bind(controller.campaignScreen.prefHeightProperty());
        controller.campaignBackground2.fitWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.campaignBackground2.fitHeightProperty().bind(controller.campaignScreen.prefHeightProperty());
        controller.campaignBackground.setPreserveRatio(false);
        controller.campaignBackground2.setPreserveRatio(false);
        controller.levelContainer.prefWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.levelContainerElements.prefWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.levelContainerPath.prefWidthProperty().bind(controller.campaignScreen.prefWidthProperty());
        controller.levelContainerPath.setMouseTransparent(true);
        controller.levelContainerElements.toFront();


    }

    @Override
    public void initGraphics() {
        controller.campaignScroller.setStyle("-fx-background-color: rgba(170,170,170,.20)");

    }

    @Override
    public void updateWithUser(UserWGames userWGames){
        App.startScreenController.campaignScreenM.setLevelUnlocksBasedOnProgress(userWGames.user().userInfo().getUserCampaignProgress());
    }

    private void setUpChangeBgListener() {
        controller.campaignScroller.vvalueProperty().addListener(e -> {
            CampaignTier newTier = getCurrentTierScrollerIsOn(controller.campaignScroller.getVvalue());
            if (currentScrollerTier == null) {
                currentScrollerTier = newTier;
            } else if (newTier != currentScrollerTier) {
                // change in tier
                fadeBetweenBackgrounds(controller.campaignBackground, controller.campaignBackground2, newTier.bgUrl);
                currentScrollerTier = newTier;
            }
        });
    }

    public void refreshVvaluesForTiers() {
        for (int i = 0; i < CampaignTier.values().length; i++) {
            vvaluesForTiers[i] = getVvalueForTarget(CampaignTier.values()[i]);
        }
    }

    private CampaignTier getCurrentTierScrollerIsOn(double currentVvalue) {
        for (int i = 0; i < vvaluesForTiers.length; i++) {
            if (currentVvalue > vvaluesForTiers[i]) {
                return CampaignTier.values()[i];
            }
        }
        logger.error("Should not be here(get current tier scroller is on)");
        logger.debug("VValues: \n" + Arrays.toString(vvaluesForTiers));
        return CampaignTier.LastTier;
    }

    private double getVvalueForTarget(CampaignTier tier) {

        return getVvalueHelper(0, tier.ordinal(), true);

    }

    private double getVvalueForTarget(CampaignTier tier, int playerLevel) {
        return getVvalueHelper(playerLevel, tier.ordinal(), false);
    }

    private double getVvalueHelper(int curTier, int curCloudOrdinal, boolean onlyCloud) {
        logger.debug("Numtiers: " + curTier + " numclouds: " + curCloudOrdinal);
        ReadOnlyDoubleProperty currentHeight = controller.levelContainerPath.prefHeightProperty();
        DoubleBinding total;
        if (onlyCloud) {
            // only to the cloud
            total = cloudYBindings[curCloudOrdinal];
        } else {
            total = yLayoutBindings.get(curCloudOrdinal)[curTier];
            // slight offset
            total = total.add(controller.levelContainerPath.widthProperty().multiply(0.33));
            total = total.subtract(controller.levelContainerPath.widthProperty().multiply(widthToTierSpacerHeight * .9).multiply(curCloudOrdinal));
        }
        return total.divide(currentHeight).getValue();
    }

    public void scrollToPlayerTier(CampaignProgress playerProgress) {
        Animation scrollAnimation = new Timeline(new KeyFrame(Duration.seconds(1.4), new KeyValue(controller.campaignScroller.vvalueProperty(), getVvalueForTarget(playerProgress.getCurrentTier(), playerProgress.getCurrentLevelOfTier()))));
        VBox cur = infoContainers.get(playerProgress.getCurrentTier().ordinal())[playerProgress.getCurrentLevelOfTier()];
        cur.setVisible(true);
        cur.setMouseTransparent(false);
        scrollAnimation.play();
    }

    public void setLevelUnlocksBasedOnProgress(CampaignProgress playerProgress) {
        reset();
        for (int i = CampaignTier.LastTier.ordinal(); i >= 0; i--) {
            VBox[] tierVboxes = infoContainers.get(i);
            CampaignTier curTier = CampaignTier.values()[i];
            int lockedAmnt = -1;
            int unlockedAmnt = -1;
            if (playerProgress.getCurrentTier() == curTier && playerProgress.getCurrentLevelOfTier() < curTier.NLevels - 1) {
                // means that somewhere along this tier you will have unlocked levels and locked levels
                unlockedAmnt = playerProgress.getCurrentLevelOfTier() + 1;
                lockedAmnt = curTier.NLevels - unlockedAmnt;

            } else if (curTier.ordinal() > playerProgress.getCurrentTier().ordinal()) {
                //  player has not even reached this tier yet, so all levels locked
                unlockedAmnt = 0;
                lockedAmnt = curTier.NLevels;
            } else {
                // player is above this tier so all levels unlocked
                unlockedAmnt = curTier.NLevels;
                lockedAmnt = 0;
            }
            int startIndex = curTier.NLevels - 1;
            while (lockedAmnt > 0) {
                lockedAmnt--;
                Button playButton = (Button) tierVboxes[startIndex].getChildren().stream().filter(r -> r.getUserData() != null && r.getUserData().toString().equals("elb")).toList().get(0);
                playButton.setDisable(true);
                startIndex--;
            }
            while (unlockedAmnt > 0) {
                unlockedAmnt--;
                Button playButton = (Button) tierVboxes[startIndex].getChildren().stream().filter(r -> r.getUserData() != null && r.getUserData().toString().equals("elb")).toList().get(0);
                playButton.setDisable(false);

                // also set pawn level

                HBox pawnContainter = (HBox) tierVboxes[startIndex].getChildren().stream().filter(r -> r.getUserData() != null && r.getUserData().toString().equals("pc")).toList().get(0);
                int pawnLevel = playerProgress.getStarsForALevel(curTier, startIndex);
                for (int j = 0; j < pawnLevel; j++) {
                    ImageView pawn = (ImageView) pawnContainter.getChildren().get(j);
                    pawn.setImage(filledPawn);
                }
                startIndex--;
            }

        }
        // lastly highlight the current players level
        // todo

    }
    // resets eveything back to default state
    public void reset() {
        for (int i = CampaignTier.LastTier.ordinal(); i >= 0; i--) {
            VBox[] tierVboxes = infoContainers.get(i);
            CampaignTier curTier = CampaignTier.values()[i];

            for (int j = 0; j < curTier.NLevels; j++) {
                Button playButton = (Button) tierVboxes[j].getChildren().stream().filter(r -> r.getUserData() != null && r.getUserData().toString().equals("elb")).toList().get(0);
                playButton.setDisable(true);

                // also set pawn level

                HBox pawnContainter = (HBox) tierVboxes[j].getChildren().stream().filter(r -> r.getUserData() != null && r.getUserData().toString().equals("pc")).toList().get(0);
                for (int z = 0; z < 3; z++) {
                    ImageView pawn = (ImageView) pawnContainter.getChildren().get(z);
                    pawn.setImage(unfilledPawn);
                }
            }

        }
        // lastly highlight the start players level
        // also todo
    }

    public void drawLevels(ReadOnlyDoubleProperty widthProperty) {
        // each level has a circle you click on to play the level
        // additionaly there is a vbox that contains the level information/play button
        DoubleBinding circleRadius = widthProperty.multiply(widthToLevelRadius);
        DoubleBinding levelInfoHeight = widthProperty.multiply(widthToLevelInfoHeight);
        DoubleBinding levelInfoWidth = widthProperty.multiply(widthToLevelInfoWidth);
        DoubleBinding levelButtonWidth = levelInfoWidth.multiply(infoWidthToLvlBtnWidth);
        DoubleBinding levelButtonHeight = levelInfoHeight.multiply(infoHeightToLvlBtnHeight);
        DoubleBinding levelDiffWidth = levelInfoWidth.multiply(infoWidthToLvlDiffWidth);
        DoubleBinding levelDiffHeight = levelInfoHeight.multiply(infoHeightToLvlDiffHeight);

        yJumpPerLevel = widthProperty.multiply(widthToJump);
        DoubleBinding xOffset = widthProperty.multiply(widthToXoffset);
        DoubleBinding xShiftPerLevelB = widthProperty.subtract(xOffset.add(circleRadius).multiply(2));
        DoubleBinding xSpacing = widthProperty.multiply(widthToInfoSpacing);
        boolean isShiftToRight = true;
        // since we are going down -> up, and the y coordinate system grows downwards
        // we need to start at the very end then decrease our y value
        DoubleBinding offsetYBottom = widthProperty.multiply(widthToOffsetY);
        int numTiers = CampaignTier.values().length;
        int numLevels = 0;
        for (CampaignTier c : CampaignTier.values()) {
            numLevels += c.NLevels;
        }

        // spacer stuff
        DoubleBinding tierSpacerHeight = widthProperty.multiply(widthToTierSpacerHeight);
        tierSpacerSpaceBetween = widthProperty.multiply(widthToTierSpacerSpaceBetweenLevelAndSpacer);


        DoubleBinding largestY = yJumpPerLevel.multiply(numLevels - 1).add(offsetYBottom.multiply(2).add(Bindings.max(circleRadius.multiply(2), levelInfoHeight.multiply(2)))).add(tierSpacerHeight.multiply(numTiers));

        // set size of container
        controller.levelContainer.prefHeightProperty().bind(largestY);
        controller.levelContainerElements.prefHeightProperty().bind(largestY);
        controller.levelContainerPath.prefHeightProperty().bind(largestY);

        // for info panel content
        DoubleBinding pawnWidth = levelInfoWidth.multiply(infoWidthToPawnWidth);

        // for creating the path
        DoubleBinding startYB = largestY.subtract(offsetYBottom).subtract(Bindings.max(circleRadius.multiply(2), levelInfoHeight));
        DoubleBinding startXB = xOffset.add(circleRadius);
        DoubleBinding lastXB = null;
        DoubleBinding lastYB = null;
        for (int i = 0; i < CampaignTier.values().length; i++) {
            CampaignTier currentTier = CampaignTier.values()[i];
            VBox[] infoContainersForLevel = new VBox[currentTier.NLevels];
            DoubleBinding[] yLayoutForLevel = new DoubleBinding[currentTier.NLevels];
            for (int j = 0; j < currentTier.NLevels; j++) {
                // for each level create a circle(contains the pfp of the opponent + button to get level info)
                // also contains the level info panel it self of course, which has 3 pawns to show your completion of the level, a play button and the name of the opponent
                // play level circle
                String levelName = currentTier.levelNames[j];
                int levelElo = currentTier.eloIndexes[j];
                int challengeIndex = currentTier.pfpIndexes[j];
                Circle newLevelToggle = new Circle();
                Image buttonProfile = new Image(ProfilePicture.values()[challengeIndex].urlString);
                newLevelToggle.setUserData(Integer.toString(challengeIndex));
                newLevelToggle.setFill(new ImagePattern(buttonProfile));
                newLevelToggle.setUserData("nlt");
                // level information panel
                VBox newLevelPanel = new VBox();
                newLevelPanel.setStyle("-fx-background-color: white;-fx-background-radius: 25px");
                newLevelPanel.setVisible(false);
                newLevelPanel.setMouseTransparent(true);
                newLevelPanel.setAlignment(Pos.CENTER);
                newLevelPanel.setSpacing(10);


                newLevelPanel.prefWidthProperty().bind(levelInfoWidth);
                newLevelPanel.prefHeightProperty().bind(levelInfoHeight);

                newLevelToggle.radiusProperty().bind(circleRadius);

                newLevelToggle.setOnMouseClicked(e -> {
                    newLevelPanel.setVisible(!newLevelPanel.isVisible());
                    newLevelPanel.setMouseTransparent(!newLevelPanel.isMouseTransparent());
                });
                // positioning stuff
                DoubleBinding startXL;
                if (isShiftToRight) {
                    startXL = startXB.add(circleRadius).add(xSpacing);
                } else {
                    startXL = startXB.subtract(circleRadius).subtract(xSpacing).subtract(levelInfoWidth);
                }
                newLevelPanel.layoutXProperty().bind(startXL);
                DoubleBinding startYL = startYB.subtract(levelInfoHeight.divide(2));
                newLevelPanel.layoutYProperty().bind(startYL);

                newLevelToggle.layoutXProperty().bind(startXB);
                newLevelToggle.layoutYProperty().bind(startYB);
                // drawing path curve
                if (lastYB != null) {
                    QuadCurve path = new QuadCurve();
                    path.startXProperty().bind(lastXB);
                    path.startYProperty().bind(lastYB);
                    double randControl1 = random.nextDouble(1.9, 2.1);
                    double randControl2 = random.nextDouble(1.9, 2.1);
                    path.controlXProperty().bind(lastXB.add(startXB).divide(randControl1));
                    path.controlYProperty().bind(lastYB.add(startYB).divide(randControl2));
                    path.endXProperty().bind(startXB);
                    path.endYProperty().bind(startYB);
                    path.setFill(null);
                    path.setStroke(Paint.valueOf("Black"));
                    controller.levelContainerPath.getChildren().add(path);
                }
                // save y binding of node before shifting
                yLayoutForLevel[j] = startYB;
                // setting oldCoordinates
                lastXB = startXB;
                lastYB = startYB;

                // changing to new coordinates
                if (isShiftToRight) {
                    startXB = startXB.add(xShiftPerLevelB);
                } else {
                    startXB = startXB.subtract(xShiftPerLevelB);

                }
                startYB = startYB.subtract(yJumpPerLevel);
                isShiftToRight = !isShiftToRight;

                // children of infoPanel
                Label title = new Label(levelName + " " + levelElo);
                BindingController.bindLargeText(title, "black");
                // will contain the pawns showing your score
                // like 3 stars in a game
                HBox pawnContainer = new HBox(5);
                pawnContainer.setUserData("pc");
                pawnContainer.setAlignment(Pos.CENTER);
                // create the pawns
                for (int k = 0; k < 3; k++) {
                    ImageView pawn = new ImageView();
                    pawn.setImage(new Image(unfilledUrl));
                    pawn.fitWidthProperty().bind(pawnWidth);
                    pawn.setPreserveRatio(true);
                    pawn.setUserData(k);
                    pawnContainer.getChildren().add(pawn);
                }
                ComboBox<String> difficulyBox = new ComboBox<>();
                difficulyBox.getItems().addAll("Easy", "Medium", "Hard");
                difficulyBox.prefWidthProperty().bind(levelDiffWidth);
                difficulyBox.prefHeightProperty().bind(levelDiffHeight);
                difficulyBox.getSelectionModel().select(1);


                Button enterLevelButton = new Button("Play");
                enterLevelButton.setUserData("elb");
                enterLevelButton.prefWidthProperty().bind(levelButtonWidth);
                enterLevelButton.prefHeightProperty().bind(levelButtonHeight);
                enterLevelButton.setStyle("-fx-background-radius: 20px");
                int levelOfTier = j;
                enterLevelButton.setOnMouseClicked(e -> {
                    // todo difficulty
                    App.changeToMainScreenCampaign(currentTier, levelOfTier, difficulyBox.getSelectionModel().getSelectedIndex() + 1);
                });
                newLevelPanel.getChildren().add(title);
                newLevelPanel.getChildren().add(pawnContainer);
                newLevelPanel.getChildren().add(difficulyBox);
                newLevelPanel.getChildren().add(enterLevelButton);
                controller.levelContainerElements.getChildren().add(newLevelToggle);
                controller.levelContainerElements.getChildren().add(newLevelPanel);
                infoContainersForLevel[j] = newLevelPanel;
            }
            // lastly draw the spacer

            // start by shifting start y by the spacer offset
            startYB = startYB.subtract(tierSpacerSpaceBetween);
            // add the shifter y to the tier endpoints for the scroll listener
            // then shift it by the height of the tierspacer
            startYB = startYB.subtract(tierSpacerHeight);
            cloudYBindings[i] = startYB;

            ImageView tierSpacer = new ImageView(cloudUrl);
            // binding sizes
            tierSpacer.fitWidthProperty().bind(widthProperty);
            tierSpacer.fitHeightProperty().bind(tierSpacerHeight);
            // binding location
            tierSpacer.layoutYProperty().bind(startYB);
            // tierspacer x will always be zero as it will cover the whole width of the screen
            tierSpacer.setLayoutX(0);
            // add to the levelcontainer path because this is a graphical element
            controller.levelContainerElements.getChildren().add(tierSpacer);
            // at the very end, add the infocontainersforlevel array to the list
            infoContainers.add(infoContainersForLevel);
            yLayoutBindings.add(yLayoutForLevel);
            // lastly(actually :)) we shift y by the spacer again
            startYB = startYB.subtract(tierSpacerSpaceBetween);
        }

    }

    public void fadeBetweenBackgrounds(ImageView currentImageView, ImageView standByImageView, String newImageUrl) {
        FadeTransition hideCur = new FadeTransition(Duration.seconds(1));
        hideCur.setNode(currentImageView);
        hideCur.setFromValue(1);
        hideCur.setToValue(0);

        standByImageView.setImage(new Image(newImageUrl));
        FadeTransition showNew = new FadeTransition(Duration.seconds(1));
        showNew.setNode(standByImageView);
        showNew.setFromValue(0);
        showNew.setToValue(1);

        hideCur.play();
        showNew.play();
        // shift them now
    }


    public void handleCampaignUpdate(CampaignAttempt attempt, ChessGame completedGame) {
        int numStars = 0;

        if (completedGame.getGameState().isStaleMated()) {
            numStars = 1;
        } else if (completedGame.getGameState().isCheckMated()[1]) {
            // game over only happens if draw or checkmate
            // so if not draw it must be checkmate
            // so we only have to check if white(the user) won
            numStars = attempt.difficulty();
        }

        if (numStars > 0) {
            // now if the user earned any stars make sure you add them
            // btw this will not decrement your level if you have done better before
            App.userManager.userInfoManager.setLevelStars(attempt.levelAttempted().tier(),
                                                            attempt.levelAttempted().levelOfTier(), numStars);

            // now if this was a new level for them(and they were sucessful), unlock the next one
            if (attempt.levelAttempted().tier().equals(App.userManager.userInfoManager.getCurrentCampaignTier())
                    && attempt.levelAttempted().levelOfTier() == App.userManager.userInfoManager.getCurrentCampaignLevel()) {
                App.userManager.userInfoManager.moveToNextLevel();
            }

        }
    }
}
