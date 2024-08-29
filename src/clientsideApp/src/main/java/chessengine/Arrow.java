package chessengine;

import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

public class Arrow {
    int startX;
    int startY;
    int endX;
    int endY;
    String color;
    private final double heightScaleFactor = 2.5d;
    private final double arrowWidth = .22d;
    private final double tipWidthF = 2.5d;

    public Arrow(int startX, int startY, int endX, int endY, String color) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
    }

    public SVGPath generateSvg(int containerHeight, int containerWidth) {
        int xDiffAbs = Math.abs(endX - startX);
        int yDiffAbs = Math.abs(endY - startY);
        if (xDiffAbs == 2 && yDiffAbs == 1) {
            // knight moves
            String path1 = getSVG(containerHeight, containerWidth, startX, startY, endX, startY, false);
            String path2 = getSVG(containerHeight, containerWidth, endX, startY, endX, endY, true);
            return concatenateSvgs(path1, path2);
        } else if (xDiffAbs == 1 && yDiffAbs == 2) {
            // also knight move
            String path1 = getSVG(containerHeight, containerWidth, startX, startY, startX, endY, false);
            String path2 = getSVG(containerHeight, containerWidth, startX, endY, endX, endY, true);
            return concatenateSvgs(path1, path2);
        } else {
            // just a normal svg
            String path1 = getSVG(containerHeight, containerWidth, startX, startY, endX, endY, true);
            return singleSvg(path1);
        }
    }

    private SVGPath concatenateSvgs(String path1, String path2) {
        String totalPath = path1 + " " + path2 + " z";
        SVGPath svg = new SVGPath();
        svg.setContent(totalPath);
        svg.setFill(Paint.valueOf(color));
        return svg;
    }

    private SVGPath singleSvg(String path1) {
        String totalPath = path1 + " z";
        SVGPath svg = new SVGPath();
        svg.setContent(totalPath);
        svg.setFill(Paint.valueOf(color));
        return svg;
    }


    private String getSVG(int containerHeight, int containerWidth, int startX, int startY, int endX, int endY, boolean isArrowTip) {
        double boxHeight = (double) containerHeight / 8;
        double boxWidth = (double) containerWidth / 8;
        double bHeightHalf = boxHeight / 2;
        double bWidthHalf = boxWidth / 2;
//        System.out.println("BoxHeight: " + boxHeight);
//        System.out.println("BoxWidth: " + boxWidth);

        double startXCoordCenter = bWidthHalf + startX * boxWidth;
        double startYCoordCenter = bHeightHalf + startY * boxHeight;
        double endXCoordCenter = bWidthHalf + endX * boxWidth;
        double endYCoordCenter = bHeightHalf + endY * boxHeight;
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


        double arrowbaseWidth = ((boxWidth + boxHeight) / 4) * arrowWidth; // half of the average of width and height;
//        System.out.println("Arrow base width: " + arrowbaseWidth);
        // start of arrow (absolute)
        int m1X = (int) startXCoordCenter;
        int m1Y = (int) startYCoordCenter;
        // bottom left corner of arrow base (absolute)
        int l2X = (int) (startXCoordCenter + arrowbaseWidth * sinArrow);
        int l2Y = (int) (startYCoordCenter + arrowbaseWidth * cosArrow);


        if (!isArrowTip) {

            // no arrow tip, so we adjust the end slightly

            endXCoordCenter -= bWidthHalf / 4.8 * cosArrow;
            endYCoordCenter += bHeightHalf / 4.8 * sinArrow;
        }

        // bottom right corner of arrow base (absolute)
        int l3X = (int) (endXCoordCenter + arrowbaseWidth * sinArrow);
        int l3Y = (int) (endYCoordCenter + arrowbaseWidth * cosArrow);


        // top right corner of arrow base
        int l7x = (int) (endXCoordCenter - arrowbaseWidth * sinArrow);
        int l7y = (int) (endYCoordCenter - arrowbaseWidth * cosArrow);
        // top left corner of arrow base
        int l8X = (int) (startXCoordCenter - arrowbaseWidth * sinArrow);
        int l8Y = (int) (startYCoordCenter - arrowbaseWidth * cosArrow);

        String path;
        if (isArrowTip) {
            // draw arrow tip
            double tipWidth = (int) (arrowbaseWidth * tipWidthF);
            double arrowHeight = heightScaleFactor * arrowbaseWidth;

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


}
