package chessserver;

public class UserPreferences {


    private boolean isBackgroundmusic;

    private double backgroundVolume;

    private boolean isEffectSounds;
    private double effectVolume;

    private int evalDepth;

    private int computerMoveDepth;

    private GlobalTheme globalTheme;

    private ChessboardTheme chessboardTheme;
    private ChessPieceTheme pieceTheme;


    public UserPreferences(boolean isBackgroundmusic, double backgroundVolume, boolean isEffectSounds, double effectVolume,
                                int evalDepth, int computerMoveDepth, GlobalTheme globalTheme,
                                ChessboardTheme chessboardTheme, ChessPieceTheme pieceTheme) {
        this.isBackgroundmusic = isBackgroundmusic;
        this.backgroundVolume = backgroundVolume;
        this.isEffectSounds = isEffectSounds;
        this.effectVolume = effectVolume;
        this.evalDepth = evalDepth;
        this.computerMoveDepth = computerMoveDepth;
        this.globalTheme = globalTheme;
        this.chessboardTheme = chessboardTheme;
        this.pieceTheme = pieceTheme;
    }

    public UserPreferences(){
        // empty constructor for object serialization
    }

    public boolean isBackgroundmusic() {
        return isBackgroundmusic;
    }

    public void setBackgroundmusic(boolean backgroundmusic) {
        isBackgroundmusic = backgroundmusic;
    }

    public double getBackgroundVolume() {
        return backgroundVolume;
    }

    public void setBackgroundVolume(double backgroundVolume) {
        this.backgroundVolume = backgroundVolume;
    }

    public boolean isEffectSounds() {
        return isEffectSounds;
    }

    public void setEffectSounds(boolean effectSounds) {
        isEffectSounds = effectSounds;
    }

    public double getEffectVolume() {
        return effectVolume;
    }

    public void setEffectVolume(double effectVolume) {
        this.effectVolume = effectVolume;
    }

    public int getEvalDepth() {
        return evalDepth;
    }

    public void setEvalDepth(int evalDepth) {
        this.evalDepth = evalDepth;
    }

    public int getComputerMoveDepth() {
        return computerMoveDepth;
    }

    public void setComputerMoveDepth(int computerMoveDepth) {
        this.computerMoveDepth = computerMoveDepth;
    }

    public GlobalTheme getGlobalTheme() {
        return globalTheme;
    }

    public void setGlobalTheme(GlobalTheme globalTheme) {
        this.globalTheme = globalTheme;
    }

    public ChessboardTheme getChessboardTheme() {
        return chessboardTheme;
    }

    public void setChessboardTheme(ChessboardTheme chessboardTheme) {
        this.chessboardTheme = chessboardTheme;
    }

    public ChessPieceTheme getPieceTheme() {
        return pieceTheme;
    }

    public void setPieceTheme(ChessPieceTheme pieceTheme) {
        this.pieceTheme = pieceTheme;
    }
}
