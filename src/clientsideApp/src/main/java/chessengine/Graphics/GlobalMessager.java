package chessengine.Graphics;

import chessengine.App;
import chessengine.Enums.Window;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.*;
import java.util.List;


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
    private Stack<Popup> popupsStart = new Stack<>();
    private Set<Integer> activeStartPopupIds = new HashSet<>();
    private Stack<Popup> popupsMain = new Stack<>();
    private Set<Integer> activeMainPopupIds = new HashSet<>();
    private boolean isInit;
    private final String[] clrs = new String[]{"Red", "Blue", "Green", "Cyan", "midnightblue"};
    private final Random r = new Random();
    public GlobalMessager() {
        isInit = false;
    }
    public void addLoadingCircle(Window window){
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setUserData("l_c");
        Pane ref = window == Window.Start ? startRef : mainRef;
        indicator.layoutXProperty().bind(ref.widthProperty().divide(2).subtract(indicator.widthProperty().divide(2)));
        indicator.layoutYProperty().bind(ref.heightProperty().divide(2).subtract(indicator.heightProperty().divide(2)));
        ref.getChildren().add(indicator);
    }

    public void Init(Group startMessager, Group mainMessager, Pane startRef, Pane mainRef) {
        this.startMessager = startMessager;
        this.mainMessager = mainMessager;
        this.startRef = startRef;
        startRef.setOnMouseClicked(e ->{
            // if the raw pane is being clicked, it means at least one popup is left and thus the pane is not empty
            Popup p = this.popupsStart.peek();
            if(p.isSkippable){
                if(p.runOnClose != null){
                    p.runOnClose.run();
                }
                closePopup(Window.Start,p.popupBox,p.popupIdx);
            }
            else{
                flashPopup(p.popupBox);
            }
        });
        mainRef.setOnMouseClicked(e ->{
            // if the raw pane is being clicked, it means at least one popup is left and thus the pane is not empty
            Popup p = this.popupsMain.peek();
            if(p.isSkippable){
                if(p.runOnClose != null){
                    p.runOnClose.run();
                }
                closePopup(Window.Main,p.popupBox,p.popupIdx);
            }
            else{
                flashPopup(p.popupBox);
            }
        });
        this.mainRef = mainRef;
        startRef.layoutBoundsProperty().addListener(e -> {
            Platform.runLater(() -> {
                redrawArrows(Window.Start);
                redrawFocusNodes(Window.Start);
            });
        });
        mainRef.layoutBoundsProperty().addListener(e -> {
            Platform.runLater(() -> {
                redrawArrows(Window.Main);
                redrawFocusNodes(Window.Main);

            });
        });

        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }

    private int numFloatingMessages = 0;
    public void sendMessage(String message, Window window) {
        sendMessage(message, window, Duration.seconds(2),Duration.seconds(2));


    }

    private void sendMessage(String message, Window window, Duration pauseDuration,Duration fadeAwayDuration) {
        if (isInit) {
            Label messageL = new Label(message);
            messageL.setFont(Font.font("Bold"));
            App.bindingController.bindSmallText(messageL,window);
            Pane ref = window == Window.Start ? startRef : mainRef;
            messageL.layoutXProperty().bind(ref.widthProperty().divide(2).subtract(messageL.widthProperty().divide(2)));
            if(numFloatingMessages == 0){
                messageL.layoutYProperty().bind(ref.heightProperty().divide(2).subtract(messageL.heightProperty().divide(2)));
            }
            else{
                messageL.layoutYProperty().bind(ref.heightProperty().divide(2).subtract(messageL.heightProperty().divide(2)).subtract(messageL.heightProperty().multiply(numFloatingMessages)));
            }
            ref.getChildren().add(messageL);

            PauseTransition pause = new PauseTransition(pauseDuration.add(Duration.seconds(0.2).multiply(numFloatingMessages)));
            FadeTransition fadetransition = new FadeTransition(fadeAwayDuration,messageL);
            fadetransition.setFromValue(1.0);
            fadetransition.setToValue(.1);
            fadetransition.setOnFinished(e ->{
                ref.getChildren().remove(messageL);
            });
            numFloatingMessages++;


            pause.setOnFinished(event -> {
                numFloatingMessages--;
                fadetransition.play();
            });

            pause.play();

        } else {
            logger.error("Trying to send a global message before it is init");

        }


    }

    private void addRef(double rx, double ry) {
        Circle c = new Circle(10);
        c.setFill(Paint.valueOf(clrs[r.nextInt(clrs.length)]));
        c.setLayoutX(rx);
        c.setLayoutY(ry);
        startMessager.getChildren().add(c);
    }

    public void addMovingArrow(Node focus, double percentX, double percentY, double animationTimeSeconds, Window window) {
        Bounds boundsInScene = focus.localToScene(focus.getBoundsInLocal());
        // Get the x and y coordinates relative to the scene
        double leftX = boundsInScene.getMinX();
        double rightX = boundsInScene.getMaxX();
        double sceneY = boundsInScene.getCenterY();
        if (window == Window.Start) {
            movingArrowsStart.add(new NodeWrapper(focus, percentX, percentY, animationTimeSeconds));
        } else {
            movingArrowsMain.add(new NodeWrapper(focus, percentX, percentY, animationTimeSeconds));
        }
        addArrow(leftX, rightX, sceneY, percentX, percentY, window, true, animationTimeSeconds);

    }

    private void addArrow(double leftX, double rightX, double y, double percentX, double percentY, Window window, boolean animate, double animationTimeSeconds) {
        Pane ref = window == Window.Start ? startRef : mainRef;
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

    private void redrawArrows(Window window) {
        clearArrows(window);
        List<NodeWrapper> arrowsToRedraw = window == Window.Start ? movingArrowsStart : movingArrowsMain;
        for (NodeWrapper s : arrowsToRedraw) {
            Bounds boundsInScene = s.n.localToScene(s.n.getBoundsInLocal());
            // Get the x and y coordinates relative to the scene
            double leftX = boundsInScene.getMinX();
            double rightX = boundsInScene.getMaxX();
            double sceneY = boundsInScene.getCenterY();
//            addRef(boundsInScene.getCenterX(),sceneY);
            addArrow(leftX, rightX, sceneY, s.percentX, s.percentY, window, true, s.animationTimeSeconds);
        }
    }

    private void redrawFocusNodes(Window window) {
        clearFocusNodes(window);
        List<Node> arrowsToRedraw = window == Window.Start ? focusPointsStart : focusPointsMain;
        for (Node s : arrowsToRedraw) {
            addFocusNodeH(s, window);
        }
    }

    private void clearArrows(Window window) {
        Pane parent = window == Window.Start ? startRef : mainRef;
        parent.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("arrow"));
    }

    private void clearFocusNodes(Window window) {
        Group parent = window == Window.Start ? startMessager : mainMessager;
        parent.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("focus"));
    }

    private void clearInfoBoxes(Window window) {
        Pane parent = window == Window.Start ? startRef : mainRef;
        parent.getChildren().removeIf(n -> n.getUserData() != null && n.getUserData().toString().equals("bg"));
    }

    public void addInformationBox(double percentCenterX, double percentCenterY, double percX, double percY, String title, String information, Window window) {
        Pane ref = window == Window.Start ? startRef : mainRef;
        VBox background = new VBox(6);
        background.setUserData("bg");
        background.setAlignment(Pos.CENTER);
        background.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: black; -fx-border-width: 1.4;-fx-border-radius: 15;" + background.getStyle()); // todo save in constants not here also make dynamic radiuses
        background.prefWidthProperty().bind(ref.widthProperty().multiply(percX));
        background.prefHeightProperty().bind(ref.heightProperty().multiply(percY));

        background.layoutXProperty().bind(ref.widthProperty().multiply(percentCenterX).subtract(background.widthProperty().divide(2)));
        background.layoutYProperty().bind(ref.heightProperty().multiply(percentCenterY).subtract(background.heightProperty().divide(2)));

        Label titleLabel = new Label(title);
        App.bindingController.bindMediumText(titleLabel, window, "Black");

        Text content = new Text(information);
        App.bindingController.bindSmallTextCustom(content, window, "-fx-text-fill: Black;-fx-text-alignment: center;");
        background.getChildren().addAll(titleLabel, content);

        ref.getChildren().add(background);

    }

    public void addFocusNode(Node n, Window window) {
        if (window == Window.Start) {
            focusPointsStart.add(n);
        } else {
            focusPointsMain.add(n);
        }
        addFocusNodeH(n, window);
    }

    private final int[][] horizontalknowns = new int[][]{new int[]{0, 0, 1, -1}, new int[]{0, -1, 1, 1},new int[]{0, -1, -1, 1}, new int[]{-1, 0, 1, 1}, };

    private void addFocusNodeH(Node n, Window window) {
        Pane ref = window == Window.Start ? startRef : mainRef;
        Group container = window == Window.Start ? startMessager : mainMessager;
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


        container.getChildren().addAll(focusMaskTop,focusMaskBottom,focusMaskLeft,focusMaskRight);


    }

    public void removeLoadingCircles(Window window) {
        Pane ref = window == Window.Start ? startRef : mainRef;
        ref.getChildren().removeIf(c -> c.getUserData() != null && c.getUserData().equals("l_c"));

    }



    public void createBooleanPopup(String prompt,String yesPrompt,String noPrompt, Window window, boolean isSkippable, int popupIdx, Runnable ifSucess,Runnable ifFail){
        Set<Integer> popupIds = window == Window.Start ? this.activeStartPopupIds : this.activeMainPopupIds;
        if(popupIds.contains(popupIdx)) return;
        HBox customAction = new HBox();
        customAction.setAlignment(Pos.CENTER);
        customAction.setSpacing(5);
        Button yes = new Button(yesPrompt);
        yes.prefWidthProperty().bind(customAction.widthProperty().divide(2.5));
        yes.prefHeightProperty().bind(customAction.heightProperty().multiply(0.7));
        App.bindingController.bindSmallText(yes, window);
        Button no = new Button(noPrompt);
        no.prefWidthProperty().bind(customAction.widthProperty().divide(2.5));
        no.prefHeightProperty().bind(customAction.heightProperty().multiply(0.7));
        App.bindingController.bindSmallText(no, window);
        customAction.getChildren().addAll(yes,no);
        VBox popup = createBasePopupWindow(prompt,customAction, window,isSkippable,popupIdx,ifFail);
        yes.setOnMouseClicked(e->{
            closePopup(window,popup,popupIdx);
            if(ifSucess != null){
                ifSucess.run();
            }
        });
        no.setOnMouseClicked(e->{
            closePopup(window,popup,popupIdx);
            if(ifFail != null){
                ifFail.run();
            }
        });

    }

    private VBox createBasePopupWindow(String prompt, HBox customAction, Window window, boolean isSkippable, int popupIdx,Runnable ifFail){


        VBox popupBox = new VBox();
        popupBox.setStyle("-fx-background-color: #E5ECE9;-fx-background-radius: 13");
        popupBox.setAlignment(Pos.TOP_CENTER);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        top.prefWidthProperty().bind(popupBox.widthProperty());
        top.prefHeightProperty().bind(popupBox.heightProperty().multiply(.3));

        Label promptLabel = new Label(prompt);
        App.bindingController.bindSmallText(promptLabel, window,"Black");
        top.getChildren().add(promptLabel);

        HBox bottom = new HBox();
        bottom.prefWidthProperty().bind(popupBox.widthProperty());
        bottom.prefHeightProperty().bind(popupBox.heightProperty().multiply(0.3));
        bottom.setAlignment(Pos.BOTTOM_RIGHT);
        if(isSkippable) {
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnMouseClicked(e ->{
                if(ifFail != null){
                    ifFail.run();
                }
                closePopup(window,popupBox,popupIdx);
            });
            cancelButton.prefWidthProperty().bind(bottom.prefWidthProperty().divide(3));
            cancelButton.prefHeightProperty().bind(bottom.prefHeightProperty());

            App.bindingController.bindXSmallText(cancelButton, window); // uses ismain
            bottom.getChildren().add(cancelButton);
        }

        customAction.prefWidthProperty().bind(popupBox.widthProperty());
        customAction.prefHeightProperty().bind(popupBox.heightProperty().subtract(top.heightProperty()).subtract(bottom.heightProperty()));
        popupBox.getChildren().addAll(top,customAction,bottom);

        Pane ref = window == Window.Start ? this.startRef : this.mainRef;
        App.bindingController.bindCustom(ref.widthProperty(),popupBox.prefWidthProperty(),350,0.4);
        App.bindingController.bindCustom(ref.heightProperty(),popupBox.prefHeightProperty(),220,0.4);
        popupBox.layoutXProperty().bind(ref.widthProperty().divide(2).subtract(popupBox.widthProperty().divide(2)));
        popupBox.layoutYProperty().bind(ref.heightProperty().divide(2).subtract(popupBox.heightProperty().divide(2)));
        addPopup(window,isSkippable,popupBox,popupIdx,ifFail);
        return popupBox;

    }

    private void addPopup(Window window, boolean isSkippable, VBox popupBox, int popupIdx,Runnable ifFail){
        Set<Integer> popupIds = window == Window.Start ? this.activeStartPopupIds : this.activeMainPopupIds;
        if(!popupIds.contains(popupIdx)){
            Pane ref = window == Window.Start ? this.startRef : this.mainRef;
            ref.getChildren().add(popupBox);
            popupBox.toFront();
            if(window == Window.Start){
                this.popupsStart.push(new Popup(popupBox,isSkippable,popupIdx,ifFail));
            }
            else{
                this.popupsMain.push(new Popup(popupBox,isSkippable,popupIdx,ifFail));
            }
            popupIds.add(popupIdx);
            updatePaneWithPopup(window);
        }
        else{
            logger.debug("Skipping adding already active popup");
        }

    }

    private void closePopup(Window window, VBox popup, int popupIdx) {
        Set<Integer> popupIds = window == Window.Start ? this.activeStartPopupIds : this.activeMainPopupIds;
        if(popupIds.contains(popupIdx)){
            Pane ref = window == Window.Start ? this.startRef : this.mainRef;
            ref.getChildren().remove(popup);
            removePanePopup(window);
            if(window == Window.Start){
                this.popupsStart.pop();
            }
            else{
                this.popupsMain.pop(); // should never be empty
            }
            popupIds.remove(popupIdx);
        }
        else {
            logger.warn("Trying to close a popup window with an invalid idx. (Not contained in current ids)");
        }

    }

    private void flashPopup(VBox popup){
        final Border visibleBorder = new Border(new BorderStroke(
                Paint.valueOf("red"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                new BorderWidths(4)
        ));
        final Border transparentBorder = new Border(new BorderStroke(
                Paint.valueOf("transparent"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                new BorderWidths(4)
        ));

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0),e -> popup.setBorder(transparentBorder)),
            new KeyFrame(Duration.seconds(0.2),e -> popup.setBorder(visibleBorder)),
            new KeyFrame(Duration.seconds(0.4),e -> popup.setBorder(transparentBorder))
        );
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.play();
    }
    private int numMainPopups = 0;
    private int numStartPopups = 0;
    private void updatePaneWithPopup(Window window){
        if(window == Window.Start){
            this.numStartPopups++;
        }
        else{
            this.numMainPopups++;
        }
        Pane ref = window == Window.Start ? this.startRef : this.mainRef;
        ref.setMouseTransparent(false); // adding a popup means that there is a popup guaranteed so no need to check for mouse transparency
    }

    private void removePanePopup(Window window){
        boolean greaterThanZero;
        if(window == Window.Start){
            this.numStartPopups--;
            greaterThanZero = this.numStartPopups > 0;
        }
        else{
            this.numMainPopups--;
            greaterThanZero = this.numMainPopups > 0;
        }
        Pane ref = window == Window.Start ? this.startRef : this.mainRef;
        ref.setMouseTransparent(!greaterThanZero); // if there are any popups (ie greaterthanzero) the pane should NOT be mouse transparent
    }

    private Rectangle lastHighlight = null;

    public void highlightSide(boolean isWhiteTurn, boolean isWhiteOriented, int moveTimeS){
        boolean isBottom = isWhiteOriented == isWhiteTurn;

        Rectangle higlight = new Rectangle();
        App.bindingController.bindCustom(mainRef.heightProperty(), higlight.heightProperty(), 3, 0.03);
        higlight.widthProperty()
                .bind(App.mainScreenController.getGameContainerWidth().divide(2));

        if(isWhiteTurn){
            higlight.setFill(Paint.valueOf("white"));
        }
        else{
            higlight.setFill(Paint.valueOf("black"));
        }


        // when the highlight may get scaled down, make sure it is still clipped to the right side of the screen
        higlight.layoutXProperty().bind(App.mainScreenController.getGameContainerLayoutX()
                .subtract(higlight.scaleXProperty().multiply(-1).add(1).divide(2).multiply(higlight.widthProperty())));

        if(isBottom){
            higlight.layoutYProperty().bind(mainRef.heightProperty().subtract(higlight.heightProperty()));
        }
        else{
            higlight.layoutYProperty().bind(new ReadOnlyDoubleWrapper(0));
        }

        //  if movetime > 0 add shrinking timeline transition (time left 'shrinking')
        if(moveTimeS > 0){
            ScaleTransition st = new ScaleTransition(Duration.seconds(moveTimeS), higlight);
            st.setFromX(1);
            st.setToX(0);

            st.play();


        }


        if(lastHighlight != null){
            mainRef.getChildren().remove(lastHighlight);
        }
        lastHighlight = higlight;
        mainRef.getChildren().add(higlight);


    }


    private record NodeWrapper(Node n, double percentX, double percentY, double animationTimeSeconds) {


    }

    private record Popup(VBox popupBox,boolean isSkippable,int popupIdx,Runnable runOnClose){

    }

}
