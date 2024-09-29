package chessengine.Graphics;

import chessengine.ChessRepresentations.ChessMove;
import javafx.scene.shape.SVGPath;

public class MoveArrow extends Arrow{

    private final double heightScaleFactor = 2.5d;
    private final double arrowWidth = .22d;
    private final double tipWidthF = 2.5d;

    public MoveArrow(int startX, int startY, int endX, int endY, String color) {
        super(startX, startY, endX, endY, color);
    }
    public MoveArrow(ChessMove m, String color) {
        super(m.getOldX(), m.getOldY(), m.getNewX(), m.getNewY(), color);
    }
    @Override
    public SVGPath generateSvg(double containerHeight, double containerWidth) {
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

    private String getSVG(double containerHeight, double containerWidth, int startX, int startY, int endX, int endY, boolean isArrowTip){
        double boxHeight = containerHeight / 8;
        double boxWidth = containerWidth / 8;
        double bHeightHalf = boxHeight / 2;
        double bWidthHalf = boxWidth / 2;
//        System.out.println("BoxHeight: " + boxHeight);
//        System.out.println("BoxWidth: " + boxWidth);

        double startXCoordCenter = bWidthHalf + startX * boxWidth;
        double startYCoordCenter = bHeightHalf + startY * boxHeight;
        double endXCoordCenter = bWidthHalf + endX * boxWidth;
        double endYCoordCenter = bHeightHalf + endY * boxHeight;

        double arrowbaseWidth = ((boxWidth + boxHeight) / 4) * arrowWidth; // half of the average of width and height;

        return super.getSVG(startXCoordCenter,startYCoordCenter, endXCoordCenter, endYCoordCenter,arrowbaseWidth,tipWidthF,heightScaleFactor,isArrowTip);
    }
}
