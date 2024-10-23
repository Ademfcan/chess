package chessengine.Graphics;

import chessengine.App;
import chessengine.Misc.ChessConstants;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GlobalMessager {
    private final List<NodeWrapper> movingArrowsStart = new ArrayList<>();
    private final List<NodeWrapper> movingArrowsMain = new ArrayList<>();
    private final List<Node> focusPointsStart = new ArrayList<>();
    private final List<Node> focusPointsMain = new ArrayList<>();
    private final Logger logger = LogManager.getLogger(this.toString());
    private final double shiftPercentage = 1.05d;
    private final double moveTime = .7; // seconds
    private final Duration moveDuration = Duration.seconds(moveTime);
    private Group startMessager;
    private Group mainMessager;
    private Pane startRef;
    private Pane mainRef;
    private boolean isInit;
    private final String[] clrs = new String[]{"Red", "Blue", "Green", "Cyan", "midnightblue"};
    private final Random r = new Random();
    public GlobalMessager() {
        isInit = false;
    }
    public void addLoadingCircle(boolean isStart){
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setUserData("l_c");
        Pane ref = isStart ? startRef : mainRef;
        ref.getChildren().add(indicator);
    }

    public void Init(Group startMessager, Group mainMessager, Pane startRef, Pane mainRef) {
        this.startMessager = startMessager;
        this.mainMessager = mainMessager;
        this.startRef = startRef;
        this.mainRef = mainRef;
        startRef.layoutBoundsProperty().addListener(e -> {
            Platform.runLater(() -> {
                redrawArrows(true);
                redrawFocusNodes(true);
            });
        });
        mainRef.layoutBoundsProperty().addListener(e -> {
            Platform.runLater(() -> {
                redrawArrows(false);
                redrawFocusNodes(false);

            });
        });

        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void sendMessageQuick(String message, boolean isforStart) {
        sendMessage(message, isforStart, Duration.seconds(2));


    }

    public void sendMessage(String message, boolean isforStart, Duration duration) {
        if (isInit) {
            Label messageL = new Label(message);
            Pane ref = isforStart ? startRef : mainRef;
            PathTransition transition = new PathTransition();
            FadeTransition ftransition = new FadeTransition();

            Line path = new Line(ref.getWidth() / 2, ref.getHeight() / 2, ref.getWidth() / 2, 0);
            transition.setPath(path);
            transition.setDuration(duration);
            transition.setInterpolator(Interpolator.EASE_OUT);
            transition.setOnFinished(e -> {
                ref.getChildren().remove(messageL);
            });
            ref.getChildren().add(messageL);

            ftransition.setFromValue(1.0);
            ftransition.setToValue(0);
            ftransition.setDuration(duration);

            ParallelTransition pTrans = new ParallelTransition(messageL, transition, ftransition);

            pTrans.play();

        } else {
            ChessConstants.mainLogger.error("Trying to send a global message before it is init");

        }


    }

    private void addRef(double rx, double ry) {
        Circle c = new Circle(10);
        c.setFill(Paint.valueOf(clrs[r.nextInt(clrs.length)]));
        c.setLayoutX(rx);
        c.setLayoutY(ry);
        startMessager.getChildren().add(c);
    }

    public void addMovingArrow(Node focus, double percentX, double percentY, double animationTimeSeconds, boolean isForStart) {
        Bounds boundsInScene = focus.localToScene(focus.getBoundsInLocal());
        // Get the x and y coordinates relative to the scene
        double leftX = boundsInScene.getMinX();
        double rightX = boundsInScene.getMaxX();
        double sceneY = boundsInScene.getCenterY();
        if (isForStart) {
            movingArrowsStart.add(new NodeWrapper(focus, percentX, percentY, animationTimeSeconds));
        } else {
            movingArrowsMain.add(new NodeWrapper(focus, percentX, percentY, animationTimeSeconds));
        }
        addArrow(leftX, rightX, sceneY, percentX, percentY, isForStart, true, animationTimeSeconds);

    }

    private void addArrow(double leftX, double rightX, double y, double percentX, double percentY, boolean isForStart, boolean animate, double animationTimeSeconds) {
        Pane ref = isForStart ? startRef : mainRef;
        double spaceX = ref.getWidth();
        double spaceY = ref.getHeight();
        double spaceNeededX = percentX * spaceX;
        double spaceNeededY = percentY * spaceY;
        double moveShiftX = spaceNeededX * shiftPercentage;
        double moveShiftY = spaceNeededY * shiftPercentage;
        spaceNeededX += moveShiftX;
        spaceNeededY += moveShiftY;
        double rightSpace = spaceX - rightX;
        double arrowWidth = (spaceNeededX + spaceNeededY) / 15;


        int dirX;
        double endX;
        double startX;
        if (rightSpace > spaceNeededX) {
            // space on right
            dirX = 1;
            endX = rightX + spaceNeededX;
            startX = rightX;
        } else if (leftX > spaceNeededX) {
            // space on left
            dirX = -1;
            endX = leftX - spaceNeededX;
            startX = leftX;
        } else {
            logger.error("No enough X Space left for arrow rX:" + rightX + " percX:" + percentX);
            dirX = 1;
            endX = rightX + spaceNeededX; // default to right
            startX = rightX;
        }

        double topSpace = y;

        int dirY;
        double endY;
        if (topSpace > spaceNeededY) {
            // space on top
            dirY = -1;
            endY = y - spaceNeededY;
        } else if (spaceY - y > spaceNeededY) {
            // space on bottom
            dirY = 1;
            endY = y + spaceNeededY;
        } else {
            logger.error("No enough Y Space left for arrow Y:" + y + " percY:" + percentY);
            dirY = -1;
            endY = y - spaceNeededY; // default to
        }


        double xDiff = endX - startX;
        double yDiff = endY - y;
        double angArrow = Math.atan(yDiff / xDiff);
//        System.out.println("Angle: " + Math.toDegrees(angArrow));
        if (spaceNeededX < 0) {
            angArrow = Math.PI + angArrow;
        }
        double sinArrow = Math.sin(-(Math.PI - angArrow));
        double cosArrow = Math.cos(-(Math.PI - angArrow));

        double shift = arrowWidth * (ShowArrow.heightScaleFactor + .55d);
        double shiftX = -shift * cosArrow;
        double shiftY = -shiftX * sinArrow;

        moveShiftX *= dirX;
        moveShiftY *= dirY;

        // adjust for arrow width


        ShowArrow arrow = new ShowArrow(endX, endY, startX, y, arrowWidth, shiftX, shiftY, "blue");// test;
        SVGPath arrowSvg = arrow.generateSvg((spaceY), (spaceX));
        arrowSvg.setUserData("arrow");


        ref.getChildren().add(arrowSvg);

        if (animate) {

            Timeline moveAnimationX = new Timeline(new KeyFrame(moveDuration, new KeyValue(arrowSvg.translateXProperty(), moveShiftX)), new KeyFrame(moveDuration, new KeyValue(arrowSvg.translateYProperty(), moveShiftY)));
            moveAnimationX.setAutoReverse(true);
            moveAnimationX.setCycleCount((int) ((animationTimeSeconds / moveTime) / 2) + 1); // keep odd so it ends up at start state
            moveAnimationX.setOnFinished(e -> {
                arrowSvg.setTranslateX(0);
                arrowSvg.setTranslateY(0);
            });


            moveAnimationX.play();
        }

    }

    private void redrawArrows(boolean isForStart) {
        clearArrows(isForStart);
        List<NodeWrapper> arrowsToRedraw = isForStart ? movingArrowsStart : movingArrowsMain;
        for (NodeWrapper s : arrowsToRedraw) {
            Bounds boundsInScene = s.n.localToScene(s.n.getBoundsInLocal());
            // Get the x and y coordinates relative to the scene
            double leftX = boundsInScene.getMinX();
            double rightX = boundsInScene.getMaxX();
            double sceneY = boundsInScene.getCenterY();
//            addRef(boundsInScene.getCenterX(),sceneY);
            addArrow(leftX, rightX, sceneY, s.percentX, s.percentY, isForStart, true, s.animationTimeSeconds);
        }
    }

    private void redrawFocusNodes(boolean isForStart) {
        clearFocusNodes(isForStart);
        List<Node> arrowsToRedraw = isForStart ? focusPointsStart : focusPointsMain;
        for (Node s : arrowsToRedraw) {
            addFocusNodeH(s, isForStart);
        }
    }

    private void clearArrows(boolean isForStart) {
        Pane parent = isForStart ? startRef : mainRef;
        parent.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("arrow"));
    }

    private void clearFocusNodes(boolean isForStart) {
        Group parent = isForStart ? startMessager : mainMessager;
        parent.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("focus"));
    }

    private void clearInfoBoxes(boolean isForStart) {
        Pane parent = isForStart ? startRef : mainRef;
        parent.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("bg"));
    }

    public void addInformationBox(double percentCenterX, double percentCenterY, double percX, double percY, String title, String information, boolean isStart) {
        Pane ref = isStart ? startRef : mainRef;
        VBox background = new VBox(6);
        background.setUserData("bg");
        background.setAlignment(Pos.CENTER);
        background.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: black; -fx-border-width: 1.4;-fx-border-radius: 15;" + background.getStyle()); // todo save in constants not here also make dynamic radiuses
        background.prefWidthProperty().bind(ref.widthProperty().multiply(percX));
        background.prefHeightProperty().bind(ref.heightProperty().multiply(percY));

        background.layoutXProperty().bind(ref.widthProperty().multiply(percentCenterX).subtract(background.widthProperty().divide(2)));
        background.layoutYProperty().bind(ref.heightProperty().multiply(percentCenterY).subtract(background.heightProperty().divide(2)));

        Label titleLabel = new Label(title);
        App.bindingController.bindMediumText(titleLabel, !isStart, "Black");

        Text content = new Text(information);
        App.bindingController.bindSmallTextCustom(content, !isStart, "-fx-text-fill: Black;-fx-text-alignment: center;");
        background.getChildren().addAll(titleLabel, content);

        ref.getChildren().add(background);

    }

    public void addFocusNode(Node n, boolean isStart) {
        if (isStart) {
            focusPointsStart.add(n);
        } else {
            focusPointsMain.add(n);
        }
        addFocusNodeH(n, isStart);
    }

    private final int[][] horizontalknowns = new int[][]{new int[]{0, 0, 1, -1}, new int[]{0, -1, 1, 1},new int[]{0, -1, -1, 1}, new int[]{-1, 0, 1, 1}, };

    private void addFocusNodeH(Node n, boolean isStart) {
        Pane ref = isStart ? startRef : mainRef;
        Group container = isStart ? startMessager : mainMessager;
        Bounds boundsInScene = n.localToScene(n.getBoundsInLocal());
        // Get the x and y coordinates relative to the scene
        double x = boundsInScene.getMinX();
        double y = boundsInScene.getMinY();
        double w = boundsInScene.getWidth();
        double h = boundsInScene.getHeight();
        double x2 = x + w;
        double y2 = y + h;
        double totalWidth = ref.getWidth();
        double totalHeight = ref.getHeight();
        // top rect
        Rectangle focusMaskTop = new Rectangle(0, 0, totalWidth,y);
        focusMaskTop.setFill(Paint.valueOf("rgba(234,240,240,0.2)"));
        // Prevent this node from consuming the event
        focusMaskTop.setUserData("focus");
        focusMaskTop.setMouseTransparent(false);
        Rectangle focusMaskBottom = new Rectangle(0, y2, totalWidth,totalHeight);
        focusMaskBottom.setFill(Paint.valueOf("rgba(234,240,240,0.2)"));
        // Prevent this node from consuming the event
        focusMaskBottom.setUserData("focus");
        focusMaskBottom.setMouseTransparent(false);
        Rectangle focusMaskLeft = new Rectangle(0, y, x,h);
        focusMaskLeft.setFill(Paint.valueOf("rgba(234,240,240,0.2)"));
        // Prevent this node from consuming the event
        focusMaskLeft.setUserData("focus");
        focusMaskLeft.setMouseTransparent(false);
        Rectangle focusMaskRight = new Rectangle(x2, y, totalWidth-x2,h);
        focusMaskRight.setFill(Paint.valueOf("rgba(234,240,240,0.2)"));
        // Prevent this node from consuming the event
        focusMaskRight.setUserData("focus");
        focusMaskRight.setMouseTransparent(false);


        ref.getChildren().addAll(focusMaskTop,focusMaskBottom,focusMaskLeft,focusMaskRight);


    }

    public void removeLoadingCircles(boolean isStart) {
        Pane ref = isStart ? startRef : mainRef;
        ref.getChildren().removeIf(c -> c.getUserData() != null && c.getUserData().equals("l_c"));

    }

    private record NodeWrapper(Node n, double percentX, double percentY, double animationTimeSeconds) {


    }

}
