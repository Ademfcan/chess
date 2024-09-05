package chessengine.Graphics;

import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

public abstract class Arrow {
    public int startX;
    public int startY;


    public int endX;
    public int endY;
    public final String color;


    public Arrow(int startX, int startY, int endX, int endY, String color) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
    }

    abstract public SVGPath generateSvg(double containerHeight, double containerWidth);


    protected SVGPath concatenateSvgs(String path1, String path2) {
        String totalPath = path1 + " " + path2 + " z";
        SVGPath svg = new SVGPath();
        svg.setContent(totalPath);
        svg.setFill(Paint.valueOf(color));
        return svg;
    }

    protected SVGPath singleSvg(String path1) {
        String totalPath = path1 + " z";
        SVGPath svg = new SVGPath();
        svg.setContent(totalPath);
        svg.setFill(Paint.valueOf(color));
        return svg;
    }


    protected String getSVG(double startXCoordCenter, double startYCoordCenter, double endXCoordCenter, double endYCoordCenter,double arrowBaseWidth,double tipWidthF,double heightScaleFactor, boolean isArrowTip) {

//        System.out.println("Start X" + startXCoordCenter);
//        System.out.println("Start Y" + startYCoordCenter);
//        System.out.println("End X" + endXCoordCenter);
//        System.out.println("End Y" + endYCoordCenter);

        double xDiff = endXCoordCenter - startXCoordCenter;
        double yDiff = startYCoordCenter - endYCoordCenter;
        double angArrow = Math.atan(yDiff / xDiff);
//        System.out.println("Angle: " + Math.toDegrees(angArrow));
        if (xDiff < 0) {
            angArrow = Math.PI + angArrow;
        }
        double sinArrow = Math.sin(-(Math.PI - angArrow));
        double cosArrow = Math.cos(-(Math.PI - angArrow));


//        System.out.println("Arrow base width: " + arrowbaseWidth);
        // start of arrow (absolute)
        int m1X = (int) startXCoordCenter;
        int m1Y = (int) startYCoordCenter;
        // bottom left corner of arrow base (absolute)
        int l2X = (int) (startXCoordCenter + arrowBaseWidth * sinArrow);
        int l2Y = (int) (startYCoordCenter + arrowBaseWidth * cosArrow);


        if (!isArrowTip) {

            // no arrow tip, so we adjust the end slightly

            endXCoordCenter -= arrowBaseWidth * cosArrow;
            endYCoordCenter += arrowBaseWidth * sinArrow;
        }

        // bottom right corner of arrow base (absolute)
        int l3X = (int) (endXCoordCenter + arrowBaseWidth * sinArrow);
        int l3Y = (int) (endYCoordCenter + arrowBaseWidth * cosArrow);


        // top right corner of arrow base
        int l7x = (int) (endXCoordCenter - arrowBaseWidth * sinArrow);
        int l7y = (int) (endYCoordCenter - arrowBaseWidth * cosArrow);
        // top left corner of arrow base
        int l8X = (int) (startXCoordCenter - arrowBaseWidth * sinArrow);
        int l8Y = (int) (startYCoordCenter - arrowBaseWidth * cosArrow);

        String path;
        if (isArrowTip) {
            // draw arrow tip
            double tipWidth = (int) (arrowBaseWidth * tipWidthF);
            double arrowHeight = heightScaleFactor * arrowBaseWidth;

            // bottom of arrow tip (absolute)
            int l4X = (int) (endXCoordCenter + tipWidth * sinArrow);
            int l4Y = (int) (endYCoordCenter + tipWidth * cosArrow);
            // point of arrow  tip
            int l5x = (int) (endXCoordCenter - arrowHeight * cosArrow);
            int l5y = (int) (endYCoordCenter + arrowHeight * sinArrow);
            // top of arrow tip
            int l6x = (int) (endXCoordCenter - tipWidth * sinArrow);
            int l6y = (int) (endYCoordCenter - tipWidth * cosArrow);
            path = String.format("M %d,%d L %d,%d L %d,%d L %d,%d L %d,%d L %d,%d L %d,%d L %d,%d", m1X, m1Y, l2X, l2Y, l3X, l3Y, l4X, l4Y, l5x, l5y, l6x, l6y, l7x, l7y, l8X, l8Y);
        } else {

            path = String.format("M %d,%d L %d,%d L %d,%d L %d,%d L %d,%d", m1X, m1Y, l2X, l2Y, l3X, l3Y, l7x, l7y, l8X, l8Y);
        }
        return path;

    }

    @Override
    public String toString() {
        return "Arrow{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", color='" + color + '\'' +
                '}';
    }


}
