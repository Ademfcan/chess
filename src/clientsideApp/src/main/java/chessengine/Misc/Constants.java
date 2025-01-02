package chessengine.Misc;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Constants {
    public static final Background labelUnactive = new Background(new BackgroundFill(Paint.valueOf("transparent"), null, null));
    public static final Background blackTurnActive = new Background(new BackgroundFill(Paint.valueOf("rgb(0,0,0)"), new CornerRadii(5), null));
    public static final Background whiteTurnActive = new Background(new BackgroundFill(Paint.valueOf("rgb(255,255,255)"), new CornerRadii(5), null));
    public static final Background gameOverBackground = new Background(new BackgroundFill(Paint.valueOf("rgba(175,175,175,.67)"), CornerRadii.EMPTY, null));
    private static final BackgroundFill backgroundFillDefault = new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(3), null);
    public static final Background defaultBg = new Background(backgroundFillDefault);
    private static final BackgroundFill backgroundFillHighlight = new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(3), null);
    public static final Background highlightBg = new Background(backgroundFillHighlight);
    public static final String puzzleFileLocation = "LargeFiles/lichess_db_puzzle.csv";
}
