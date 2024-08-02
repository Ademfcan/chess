package chessengine;

import chessserver.*;
import chessserver.GlobalTheme;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class ChessConstants {
    private static final long blackPawns = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    private static final long blackKnights = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    private static final long blackBishops = 0b00000000000000000000000000000000000000000000000000000000000100100L;
    private static final long blackRooks = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    private static final long blackQueens = 0b0000000000000000000000000000000000000000000000000000000000001000L;
    private static final long blackKings = 0b0000000000000000000000000000000000000000000000000000000000010000L;

    private static final long whitePawns = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    private static final long whiteKnights = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    private static final long whiteBishops = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    private static final long whiteRooks = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    private static final long whiteQueens = 0b0000100000000000000000000000000000000000000000000000000000000000L;
    private static final long whiteKings = 0b0001000000000000000000000000000000000000000000000000000000000000L;

    // Flipped positioning
    public static final long[] blackPiecesC = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    public static final long[] whitePiecesC = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};
    public static final XYcoord whiteKingStart = new XYcoord(4,7);
    public static final XYcoord blackKingStart = new XYcoord(4,0);
    public static final ChessPosition startBoardState = new ChessPosition(new BitBoardWrapper(whitePiecesC,blackPiecesC,whiteKingStart,blackKingStart),new ChessMove(-10,-10,-10,-10,ChessConstants.EMPTYINDEX,ChessConstants.EMPTYINDEX,false,false,false,ChessConstants.EMPTYINDEX,false,false));

    public static final ChessMove startMove = new ChessMove(-10,-10,-10,-10,ChessConstants.EMPTYINDEX,ChessConstants.EMPTYINDEX,false,false,false,ChessConstants.EMPTYINDEX,false,false);
    public static final Logger mainLogger = LogManager.getLogger("Central Logger");

    public static ChessStates NEWGAMESTATE = new ChessStates();

    public static final Random generalRandom = new Random(1226891211);

    public static final Computer generalComp = new Computer(10);
    // only used for simple evals, no minimax so depth dosent matter

    public static final int EMPTYINDEX = -10;

    public static final int KINGINDEX = 5;
    public static final int QUEENINDEX = 4;
    public static final int ROOKINDEX = 3;
    public static final int BISHOPINDEX = 2;
    public static final int KNIGHTINDEX = 1;
    public static final int PAWNINDEX = 0;

    public static final String FIRSTGAMENAME = "StartGame";

    public static final int ONESIDEPIECECOUNT = 16;
    public static final int BOTHSIDEPIECECOUNT = 32;

    public static final int WHITECHECKMATEVALUE = 1000000;
    public static final int BLACKCHECKMATEVALUE = -1000000;

    public static final String NOEMAIL = "no-email";
    public static final String NOPASS = "no-password";

    public static final UserPreferences defaultPreferences = new UserPreferences(true,.75,true,.75,5,5, GlobalTheme.Light, ChessboardTheme.TRADITIONAL, ChessPieceTheme.TRADITIONAL);
    public static final FrontendClient defaultUser = new FrontendClient(new UserInfo(0,"anonymous",NOEMAIL,NOPASS,ProfilePicture.DEFAULT,new CampaignProgress(123)));
    public static final ComputerOutput emptyOutput = new ComputerOutput(new ChessMove(-10,-10,-10,-10,ChessConstants.EMPTYINDEX,ChessConstants.EMPTYINDEX,false,false,false,ChessConstants.EMPTYINDEX,false,false),0);

    public static final double borderRadFactor = .0025;
    public static final double borderWidthFactor = .0005;
    public static final double borderWidthFactorExp = .0015;

    public static final int[] valueMap = {1,3,3,5,9,10000000};

    public static final String arrowColor = "rgba(255,181,10,.8)";


    public static final String InnerMoveCircleColor = "rgba(60,60,60, 0.40)";

    public static final Background labelUnactive = new Background(new BackgroundFill(Paint.valueOf("transparent"), null,null));
    public static final Background blackTurnActive = new Background(new BackgroundFill(Paint.valueOf("rgb(0,0,0)"), new CornerRadii(5),null));
    public static final Background whiteTurnActive = new Background(new BackgroundFill(Paint.valueOf("rgb(255,255,255)"), new CornerRadii(5),null));
}
