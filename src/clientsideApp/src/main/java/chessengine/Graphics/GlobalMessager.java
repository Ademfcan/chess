package chessengine.Graphics;

import chessengine.Misc.ChessConstants;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class GlobalMessager {
    private Pane startMessager;
    private Pane mainMessager;

    private final List<NodeWrapper> movingArrowsStart = new ArrayList<>();
    private final List<NodeWrapper> movingArrowsMain = new ArrayList<>();

    private final Logger logger = LogManager.getLogger(this.toString());


    private boolean isInit;

    public GlobalMessager() {
        isInit = false;
    }

    public void Init(Pane startMessager, Pane mainMessager) {
        this.startMessager = startMessager;
        this.mainMessager = mainMessager;
        startMessager.widthProperty().addListener(e -> {
            Platform.runLater(()->{
                redrawArrows(true);
            });
        });
        startMessager.heightProperty().addListener(e -> {
            Platform.runLater(()->{
                redrawArrows(true);
            });
        });
        mainMessager.widthProperty().addListener(e -> {
            Platform.runLater(()->{
                redrawArrows(false);
            });
        });
        mainMessager.heightProperty().addListener(e -> {
            Platform.runLater(()->{
                redrawArrows(false);
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
            Pane theOne = isforStart ? startMessager : mainMessager;
            PathTransition transition = new PathTransition();
            FadeTransition ftransition = new FadeTransition();

            Line path = new Line(theOne.getWidth() / 2, theOne.getHeight() / 2, theOne.getWidth() / 2, 0);
            transition.setPath(path);
            transition.setDuration(duration);
            transition.setInterpolator(Interpolator.EASE_OUT);
            transition.setOnFinished(e -> {
                theOne.getChildren().remove(messageL);
            });
            theOne.getChildren().add(messageL);

            ftransition.setFromValue(1.0);
            ftransition.setToValue(0);
            ftransition.setDuration(duration);

            ParallelTransition pTrans = new ParallelTransition(messageL, transition, ftransition);

            pTrans.play();

        } else {
            ChessConstants.mainLogger.error("Trying to send a global message before it is init");

        }


    }
//    private String[] clrs = new String[]{"Red","Blue","Green","Cyan","midnightblue"};
//    private Random r = new Random();
//    private void addRef(double rx,double ry){
//        Circle c = new Circle(10);
//        c.setFill(Paint.valueOf(clrs[r.nextInt(clrs.length)]));
//        c.setLayoutX(rx);
//        c.setLayoutY(ry);
//        startMessager.getChildren().add(c);
//    }


    public void addMovingArrow(Node focus, double percentX, double percentY, double animationTimeSeconds, boolean isForStart) {
        Bounds boundsInScene = focus.localToScene(focus.getBoundsInLocal());
        // Get the x and y coordinates relative to the scene
        double leftX = boundsInScene.getMinX();
        double rightX = boundsInScene.getMaxX();
        double sceneY = boundsInScene.getCenterY();
        if (isForStart) {
            movingArrowsStart.add(new NodeWrapper(focus, percentX, percentY,animationTimeSeconds, isForStart));
        } else {
            movingArrowsMain.add(new NodeWrapper(focus, percentX, percentY,animationTimeSeconds, isForStart));
        }
        addArrow(leftX, rightX, sceneY, percentX, percentY, isForStart, true, animationTimeSeconds);

    }

    private final double shiftPercentage = 1.05d;
    private final double moveTime = .7; // seconds
    private final Duration moveDuration = Duration.seconds(moveTime);

    private void addArrow(double leftX, double rightX, double y, double percentX, double percentY, boolean isForStart, boolean animate, double animationTimeSeconds) {
        Pane ref = isForStart ? startMessager : mainMessager;
        double spaceX = ref.getWidth();
        double spaceY = ref.getHeight();
        double spaceNeededX = percentX * spaceX;
        double spaceNeededY = percentY * spaceY;
        System.out.println("SpaceX" + spaceNeededX);
        System.out.println("SpaceY" + spaceNeededY);
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

        double shift = arrowWidth * (ShowArrow.heightScaleFactor+.55d);
        double shiftX = -shift * cosArrow;
        double shiftY = -shiftX * sinArrow;

        moveShiftX *= dirX;
        moveShiftY *= dirY;

        // adjust for arrow width


        ShowArrow arrow = new ShowArrow(endX, endY, startX, y, arrowWidth, shiftX,shiftY, "blue");// test;
        SVGPath arrowSvg = arrow.generateSvg((spaceY), (spaceX));
        arrowSvg.setUserData("arrow");

        if (isForStart) {
            startMessager.getChildren().add(arrowSvg);
        } else {
            mainMessager.getChildren().add(arrowSvg);
        }

        if (animate) {

            Timeline moveAnimationX = new Timeline(new KeyFrame(moveDuration, new KeyValue(arrowSvg.translateXProperty(), moveShiftX)), new KeyFrame(moveDuration, new KeyValue(arrowSvg.translateYProperty(), moveShiftY)));
            moveAnimationX.setAutoReverse(true);
            moveAnimationX.setCycleCount((int) ((animationTimeSeconds / moveTime)/2)+1); // keep odd so it ends up at start state
            moveAnimationX.setOnFinished(e -> {
                arrowSvg.setTranslateX(0);
                arrowSvg.setTranslateY(0);
            });


            moveAnimationX.play();
        }

    }

    private void redrawArrows(boolean isForStart) {
        Pane choice = isForStart ? startMessager : mainMessager;
        choice.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("arrow"));
        List<NodeWrapper> arrowsToRedraw = isForStart ? movingArrowsStart : movingArrowsMain;
        for (NodeWrapper s : arrowsToRedraw) {
            Bounds boundsInScene = s.n.localToScene(s.n.getBoundsInLocal());
            // Get the x and y coordinates relative to the scene
            double leftX = boundsInScene.getMinX();
            double rightX = boundsInScene.getMaxX();
            double sceneY = boundsInScene.getCenterY();
            addArrow(leftX, rightX, sceneY, s.percentX, s.percentY, isForStart, true, s.animationTimeSeconds);
        }
    }

    private record NodeWrapper(Node n, double percentX, double percentY,double animationTimeSeconds, boolean isForStart) {
    }


}
