package chessengine.Graphics;


import javafx.scene.shape.SVGPath;

public class ShowArrow extends Arrow{
    public static final double heightScaleFactor = 2.5d;
    public static final double tipWidthF = 2.3d;

    private final double arrowWidth;


    private final double shiftX;
    private final double shiftY;


    public ShowArrow(double startX, double startY, double endX, double endY, double arrowWidth, double shiftX, double shiftY, String color) {
        super((int) startX, (int) startY, (int) endX, (int) endY, color);
        this.arrowWidth = arrowWidth;
        this.shiftX = shiftX;
        this.shiftY = shiftY;

    }


    @Override
    public SVGPath generateSvg(double containerHeight, double containerWidth) {

        return singleSvg(super.getSVG((startX+shiftX),(startY+shiftY),(endX+shiftX),(endY+shiftY), arrowWidth,tipWidthF,heightScaleFactor,true));
    }
}
