package chessserver.User;

import chessserver.Enums.*;
import chessserver.Misc.OptionMarker;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPreferences {

    public enum booleanEnum{
        True, False;

        public boolean isTrue() {
            return this == True;
        }
    }

    public enum StepEnum{
        LOW(.10), MED(.5), HIGH(.75), MAX(1.0);

        public final double value;
        StepEnum(double value) {
            // empty constructor for object serialization
            this.value = value;
        }
    }

    @JsonProperty("isBackgroundmusic")
    @OptionMarker(displayName = "Background Music", description = "Enable or disable background music")
    private booleanEnum isBackgroundmusic;
    @OptionMarker(displayName = "Background Volume", description = "Volume of the background music")
    @JsonProperty("backgroundVolume")
    private StepEnum backgroundVolume;
    @OptionMarker(displayName = "Effect Sounds", description = "Enable or disable effect sounds")
    @JsonProperty("isEffectSounds")
    private booleanEnum isEffectSounds;
    @OptionMarker(displayName = "Effect Volume", description = "Volume of the effect sounds")
    @JsonProperty("effectVolume")
    private StepEnum effectVolume;
    @OptionMarker(displayName = "Eval Stockfish Based", description = "Use Stockfish for evaluation in analysis")
    @JsonProperty("isEvalStockfishBased")
    private booleanEnum isEvalStockfishBased;
    @OptionMarker(displayName = "N Moves Stockfish Based", description = "Use Stockfish for N moves in analysis")
    @JsonProperty("isNMovesStockfishBased")
    private booleanEnum isNMovesStockfishBased;
    @OptionMarker(displayName = "Computer Difficulty", description = "Difficulty level for computer moves")
    @JsonProperty("computerDiff")
    private ComputerDifficulty computerMoveDiff;
    @OptionMarker(displayName = "Global Theme", description = "Global theme for the application")
    @JsonProperty("globalTheme")
    private GlobalTheme globalTheme;
    @OptionMarker(displayName = "Chessboard Theme", description = "Theme for the chessboard")
    @JsonProperty("chessboardTheme")
    private ChessboardTheme chessboardTheme;



    @OptionMarker(displayName = "Piece Theme", description = "Theme for the chess pieces")
    @JsonProperty("pieceTheme")
    private ChessPieceTheme pieceTheme;

    @JsonCreator
    public UserPreferences(@JsonProperty("isBackgroundmusic") booleanEnum isBackgroundmusic,
                           @JsonProperty("backgroundVolume") StepEnum backgroundVolume,
                           @JsonProperty("isEffectSounds") booleanEnum isEffectSounds,
                           @JsonProperty("effectVolume") StepEnum effectVolume,
                           @JsonProperty("isEvalStockfishBased")  booleanEnum isEvalStockfishBased,
                           @JsonProperty("isNMovesStockfishBased") booleanEnum isNMovesStockfishBased,
                           @JsonProperty("computerDiff") ComputerDifficulty computerDiff,
                           @JsonProperty("globalTheme") GlobalTheme globalTheme,
                           @JsonProperty("chessboardTheme") ChessboardTheme chessboardTheme,
                           @JsonProperty("pieceTheme") ChessPieceTheme pieceTheme) {
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


    @JsonIgnore
    public boolean isBackgroundMusic() {
        return isBackgroundmusic.isTrue();
    }
    @JsonIgnore
    public double getBackgroundVolume() {
        return backgroundVolume.value;
    }
    @JsonIgnore
    public boolean isEffectSounds() {
        return isEffectSounds.isTrue();
    }
    @JsonIgnore
    public double getEffectVolume() {
        return effectVolume.value;
    }

    @JsonIgnore
    public GlobalTheme getGlobalTheme() {
        return globalTheme;
    }
    @JsonIgnore
    public ChessboardTheme getChessboardTheme() {
        return chessboardTheme;
    }
    @JsonIgnore
    public ChessPieceTheme getPieceTheme() {
        return pieceTheme;
    }
    @JsonIgnore
    public boolean getNMovesStockfishBased() {
        return isNMovesStockfishBased.isTrue();
    }

    @JsonIgnore
    public boolean getEvalStockfishBased() {
        return isEvalStockfishBased.isTrue();
    }

    @JsonIgnore
    public ComputerDifficulty getComputerMoveDiff() {
        return computerMoveDiff;
    }




    private static final UserPreferences defaultPreferences = new UserPreferences(booleanEnum.True, StepEnum.MED, booleanEnum.True, StepEnum.MED, booleanEnum.True, booleanEnum.True, ComputerDifficulty.MaxDifficulty, GlobalTheme.Dark, ChessboardTheme.TRADITIONAL, ChessPieceTheme.TRADITIONAL);

    public static UserPreferences getDefaultPreferences() {
        return defaultPreferences;

    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "isBackgroundmusic=" + isBackgroundmusic +
                ", backgroundVolume=" + backgroundVolume +
                ", isEffectSounds=" + isEffectSounds +
                ", effectVolume=" + effectVolume +
                ", isEvalStockfishBased=" + isEvalStockfishBased +
                ", isNMovesStockfishBased=" + isNMovesStockfishBased +
                ", computerMoveDiff=" + computerMoveDiff +
                ", globalTheme=" + globalTheme +
                ", chessboardTheme=" + chessboardTheme +
                ", pieceTheme=" + pieceTheme +
                '}';
    }
}
