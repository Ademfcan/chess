package chessserver;

public class UserPreferences {


    private boolean isBackgroundmusic;

    private double backgroundVolume;

    private boolean isEffectSounds;
    private double effectVolume;
    private boolean isEvalStockfishBased;
    private boolean isNMovesStockfishBased;

    private ComputerDifficulty computerMoveDiff;

    private GlobalTheme globalTheme;

    private ChessboardTheme chessboardTheme;
    private ChessPieceTheme pieceTheme;


    public UserPreferences(boolean isBackgroundmusic, double backgroundVolume, boolean isEffectSounds, double effectVolume,
                           boolean isEvalStockfishBased, boolean isNMovesStockfishBased, ComputerDifficulty computerDiff, GlobalTheme globalTheme,
                           ChessboardTheme chessboardTheme, ChessPieceTheme pieceTheme) {
        this.isBackgroundmusic = isBackgroundmusic;
        this.backgroundVolume = backgroundVolume;
        this.isEffectSounds = isEffectSounds;
        this.effectVolume = effectVolume;
        this.isEvalStockfishBased = isEvalStockfishBased;
        this.isNMovesStockfishBased = isNMovesStockfishBased;
        this.computerMoveDiff = computerDiff;
        this.globalTheme = globalTheme;
        this.chessboardTheme = chessboardTheme;
        this.pieceTheme = pieceTheme;
    }

    public UserPreferences() {
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

    public boolean getNMovesStockfishBased() {
        return isNMovesStockfishBased;
    }

    public void setNMovesStockfishBased(boolean isNMovesStockfishBased) {
        this.isNMovesStockfishBased = isNMovesStockfishBased;
    }

    public boolean getEvalStockfishBased() {
        return isEvalStockfishBased;
    }

    public void setEvalStockfishBased(boolean isEvalStockfishBased) {
        this.isEvalStockfishBased = isEvalStockfishBased;
    }

    public ComputerDifficulty getComputerMoveDiff() {
        return computerMoveDiff;
    }

    public void setComputerMoveDiff(ComputerDifficulty computerMoveDiff) {
        this.computerMoveDiff = computerMoveDiff;
    }
}
