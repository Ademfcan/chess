package chessserver.Misc;

import chessserver.ChessRepresentations.BitBoardWrapper;
import chessserver.ChessRepresentations.ChessMove;
import chessserver.ChessRepresentations.ChessPosition;
import chessserver.ChessRepresentations.XYcoord;
import chessserver.Enums.*;
import chessserver.User.CampaignProgress;
import chessserver.User.FrontendClient;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

public class ChessConstants {
    public static final XYcoord whiteKingStart = new XYcoord(4, 7);
    public static final XYcoord blackKingStart = new XYcoord(4, 0);
    public static final Logger mainLogger = LogManager.getLogger("Central Logger");
    public static final Random generalRandom = new Random(1226891211);
    public static final int EMPTYINDEX = -10;
    public static final ChessMove startMove = new ChessMove(-10, -10, -10, -10, ChessConstants.EMPTYINDEX, ChessConstants.EMPTYINDEX, false, false, false, ChessConstants.EMPTYINDEX, false, false);
    public static final int KINGINDEX = 5;
    public static final int QUEENINDEX = 4;
    public static final int ROOKINDEX = 3;
    public static final int BISHOPINDEX = 2;
    public static final int KNIGHTINDEX = 1;
    public static final int PAWNINDEX = 0;
    public static final String FIRSTGAMENAME = "StartGame";
    public static final int NUMPIECES = 6;
    public static final int ONESIDEPIECECOUNT = 16;
    public static final int BOTHSIDEPIECECOUNT = 32;
    public static final int WHITECHECKMATEVALUE = 1000000;
    public static final int BLACKCHECKMATEVALUE = -1000000;
    public static final String DEFAULTEMAIL = "no-email";
    public static final String DEFAULTUSERNAME = "anonymous";
    public static final UserPreferences defaultPreferences = new UserPreferences(true, .50, true, .75, false, false, ComputerDifficulty.MaxDifficulty, GlobalTheme.Dark, ChessboardTheme.TRADITIONAL, ChessPieceTheme.TRADITIONAL);
    public static final FrontendClient defaultClient = new FrontendClient(new UserInfo(DEFAULTUSERNAME, 0, DEFAULTEMAIL,Integer.MAX_VALUE, new CampaignProgress(123), ProfilePicture.DEFAULT,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));
    public static final double borderRadFactor = .0025;
    public static final double borderWidthFactor = .0005;
    public static final double borderWidthFactorExp = .0015;
    public static final double[] valueMap = {1.5, 3.25, 3.3, 5.25, 10, 100};
    public static final int[] valueMapCentiPawn = {100, 400, 410, 600, 1200, 1000000};
    public static final String arrowColor = "rgba(255,181,10,.8)";
    public static final String moveArrowColor = "rgba(20,181,255,.8)";
    public static final String InnerMoveCircleColor = "rgba(60,60,60, 0.40)";

    public static final int NMOVES = 4;
    public static final int NONE = Integer.MIN_VALUE;
    private static final long blackPawns = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    private static final long blackKnights = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    private static final long blackBishops = 0b00000000000000000000000000000000000000000000000000000000000100100L;
    private static final long blackRooks = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    private static final long blackQueens = 0b0000000000000000000000000000000000000000000000000000000000001000L;
    private static final long blackKings = 0b0000000000000000000000000000000000000000000000000000000000010000L;
    // Flipped positioning
    public static final long[] blackPiecesC = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    private static final long whitePawns = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    private static final long whiteKnights = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    private static final long whiteBishops = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    private static final long whiteRooks = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    private static final long whiteQueens = 0b0000100000000000000000000000000000000000000000000000000000000000L;
    private static final long whiteKings = 0b0001000000000000000000000000000000000000000000000000000000000000L;
    public static final long[] whitePiecesC = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};
    public static final ChessPosition startBoardState = new ChessPosition(new BitBoardWrapper(whitePiecesC, blackPiecesC), new ChessMove(-10, -10, -10, -10, ChessConstants.EMPTYINDEX, ChessConstants.EMPTYINDEX, false, false, false, ChessConstants.EMPTYINDEX, false, false));

    public static int ComputerEloEstimate = 2400;
    public static int DefaultWaitTime = 1000; // ms;

    public static String formatSeconds(int timeSeconds){
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String secondsStr = Integer.toString(seconds);
        if(seconds < 10){
            secondsStr = "0" + secondsStr;
        }
        return String.format("%d:%s",minutes,secondsStr);
    }




}
