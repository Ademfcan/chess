package chessengine.Managers;

import chessengine.App;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Misc.ChessConstants;
import chessserver.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class UserPreferenceManager {

    private static final Logger logger = LogManager.getLogger("User_Preference_Manager");
    private UserPreferences userPref;

    public UserPreferenceManager() {
        userPref = PersistentSaveManager.readUserprefFromSave();
        if (Objects.isNull(userPref)) {
            ChessConstants.mainLogger.debug("Loading default");
            userPref = ChessConstants.defaultPreferences;
        }


    }

    public static void setupUserSettingsScreen(ChoiceBox<String> themeSelection, ComboBox<String> bgColorSelector, ComboBox<String> pieceSelector, Button audioMuteBGButton, Slider audioSliderBG, Button audioMuteEffButton, Slider audioSliderEff, ComboBox<String> evalOptions, ComboBox<String> nMovesOptions, ComboBox<String> computerOptions, boolean isMainScreen) {
        if (themeSelection != null) {
            themeSelection.getItems().addAll("Light", "Dark");
            App.bindingController.bindSmallText(themeSelection, isMainScreen);
            themeSelection.setOnAction(e -> {
                if (!themeSelection.getSelectionModel().isEmpty()) {
                    boolean isLight = themeSelection.getValue().equals("Light");
                    App.userPreferenceManager.setGlobalTheme(isLight ? GlobalTheme.Light : GlobalTheme.Dark);
                    App.messager.sendMessageQuick("Changed Global Theme to: " + themeSelection.getValue().toLowerCase(), App.isStartScreen);

                }
            });
        }

        if (bgColorSelector != null) {
            App.bindingController.bindSmallText(bgColorSelector, isMainScreen);
            bgColorSelector.getItems().addAll(Arrays.stream(ChessboardTheme.values()).map(Enum::toString).toList());
            bgColorSelector.setOnAction(e -> {
                if (!bgColorSelector.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setChessboardTheme(ChessboardTheme.getCorrespondingTheme(bgColorSelector.getValue()));
                    App.messager.sendMessageQuick("Changed chessboard theme to: " + bgColorSelector.getValue(), App.isStartScreen);
                }
            });
        }

        if (pieceSelector != null) {
            App.bindingController.bindSmallText(pieceSelector, isMainScreen);
            pieceSelector.getItems().addAll(
                    Arrays.stream(ChessPieceTheme.values()).map(ChessPieceTheme::toString).toList()
            );
            pieceSelector.setOnAction(e -> {
                if (!pieceSelector.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setPieceTheme(ChessPieceTheme.getCorrespondingTheme(pieceSelector.getValue()));
                    App.messager.sendMessageQuick("Changed piece theme to: " + pieceSelector.getValue(), App.isStartScreen);
                }
            });
        }

        if (audioMuteBGButton != null) {
            App.bindingController.bindSmallText(audioMuteBGButton, isMainScreen);
            audioMuteBGButton.setOnMouseClicked(e -> {
                boolean isBgCurMuted = App.soundPlayer.isUserPrefBgPaused();
                // isbgCurMuted is the opposite of setbackgroundmusics arg, so no flip needed
                App.userPreferenceManager.setBackgroundmusic(isBgCurMuted);
                App.messager.sendMessageQuick(isBgCurMuted ? "Background Audio Unmuted" : "Background Audio Muted", App.isStartScreen);
                audioMuteBGButton.setText(isBgCurMuted ? "🔉" : "✖");
            });
        }

        if (audioSliderBG != null) {

            audioSliderBG.setMin(0);
            audioSliderBG.setMax(1);
            audioSliderBG.setOnMouseReleased(e -> {
                App.userPreferenceManager.setBackgroundVolume(audioSliderBG.getValue());
                App.messager.sendMessageQuick("Background volume: " + audioSliderBG.getValue(), App.isStartScreen);
            });
        }

        if (audioMuteEffButton != null) {
            App.bindingController.bindSmallText(audioMuteEffButton, isMainScreen);
            audioMuteEffButton.setOnMouseClicked(e -> {
                boolean isEffCurMuted = App.soundPlayer.isEffectsMuted();
                // isEffCurMuted is the opposite of effectson
                App.userPreferenceManager.setEffectsOn(isEffCurMuted);
                App.messager.sendMessageQuick(isEffCurMuted ? "Effects Unmuted" : "Effects Muted", App.isStartScreen);
                audioMuteEffButton.setText(isEffCurMuted ? "🔉" : "✖");
            });
        }

        if (audioSliderEff != null) {
            audioSliderEff.setMin(0);
            audioSliderEff.setMax(1);
            audioSliderEff.setOnMouseReleased(e -> {
                App.userPreferenceManager.setEffectVolume(audioSliderEff.getValue());
                App.messager.sendMessageQuick("Effect volume: " + audioSliderEff.getValue(), App.isStartScreen);
            });
        }

        if (evalOptions != null) {
            App.bindingController.bindSmallText(evalOptions, isMainScreen);
            evalOptions.getItems().addAll(
                    "Stockfish", "My Computer"
            );
            evalOptions.setOnAction(e -> {
                if (!evalOptions.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setEvalStockfishBased(evalOptions.getValue().equals("Stockfish"));
                    App.messager.sendMessageQuick("Changing eval base to: " + evalOptions.getValue(), App.isStartScreen);
                }
            });
        }

        if (nMovesOptions != null) {
            App.bindingController.bindSmallText(nMovesOptions, isMainScreen);
            nMovesOptions.getItems().addAll(
                    "Stockfish", "My Computer"
            );
            nMovesOptions.setOnAction(e -> {
                if (!nMovesOptions.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setNMovesStockfishBased(nMovesOptions.getValue().equals("Stockfish"));
                    App.messager.sendMessageQuick("Changing nMoves base to: " + nMovesOptions.getValue(), App.isStartScreen);
                }
            });
        }

        if (computerOptions != null) {
            App.bindingController.bindSmallText(computerOptions, isMainScreen);
            computerOptions.setOnAction(e -> {
                if (!computerOptions.getSelectionModel().isEmpty()) {
                    String out = computerOptions.getValue();
                    if (out.contains("(S*)")) {
                        out = out.substring(0, out.indexOf("(S*)"));
                    }
                    App.userPreferenceManager.setComputerMoveDiff(ComputerDifficulty.getDifficultyOffOfElo(Integer.parseInt(out), false));
                    App.messager.sendMessageQuick("New computer elo: " + out, App.isStartScreen);
                }
            });

            computerOptions.getItems().addAll(
                    Arrays.stream(ComputerDifficulty.values()).map(c -> c.eloRange + (c.isStockfishBased ? "(S*)" : "")).toList()
            );
        }

    }

    public UserPreferences getUserPref() {
        return userPref;
    }

    public boolean isBackgroundMusic() {
        return userPref.isBackgroundmusic();
    }

    public void setDefaultSelections() {
        App.startScreenController.themeSelection.getSelectionModel().select(userPref.getGlobalTheme().toString());
        App.startScreenController.computerOptions.getSelectionModel().select(userPref.getComputerMoveDiff().eloRange + (userPref.getComputerMoveDiff().isStockfishBased ? "(S*)" : ""));
        App.startScreenController.evalOptions.getSelectionModel().select(userPref.getEvalStockfishBased() ? "Stockfish" : "My Computer");
        App.startScreenController.nMovesOptions.getSelectionModel().select(userPref.getNMovesStockfishBased() ? "Stockfish" : "My Computer");
        App.startScreenController.audioSliderBG.setValue(userPref.getBackgroundVolume());
        App.startScreenController.audioSliderEff.setValue(userPref.getEffectVolume());
        App.startScreenController.bgColorSelector.setValue(userPref.getChessboardTheme().toString());
        App.startScreenController.pieceSelector.setValue(userPref.getPieceTheme().toString());
        App.startScreenController.audioMuteBGButton.setText(userPref.isBackgroundmusic() ? "🔉" : "✖");
        App.startScreenController.audioMuteEffButton.setText(userPref.isEffectSounds() ? "🔉" : "✖");


        App.mainScreenController.themeSelection.getSelectionModel().select(userPref.getGlobalTheme().toString());
        App.mainScreenController.computerOptions.getSelectionModel().select(userPref.getComputerMoveDiff().eloRange + (userPref.getComputerMoveDiff().isStockfishBased ? "(S*)" : ""));
        App.mainScreenController.evalOptions.getSelectionModel().select(userPref.getEvalStockfishBased() ? "Stockfish" : "My Computer");
        App.mainScreenController.nMovesOptions.getSelectionModel().select(userPref.getNMovesStockfishBased() ? "Stockfish" : "My Computer");
        App.mainScreenController.audioSliderEff.setValue(userPref.getEffectVolume());
        App.mainScreenController.bgColorSelector.setValue(userPref.getChessboardTheme().toString());
        App.mainScreenController.pieceSelector.setValue(userPref.getPieceTheme().toString());
        App.mainScreenController.audioMuteEffButton.setText(userPref.isEffectSounds() ? "🔉" : "✖");

    }

    public void setBackgroundmusic(boolean backgroundmusic) {
        userPref.setBackgroundmusic(backgroundmusic);
        pushChangesToDatabase();
        loadChanges();
    }

    public void setBackgroundVolume(double backgroundVolume) {
        userPref.setBackgroundVolume(backgroundVolume);
        pushChangesToDatabase();
        loadChanges();
    }

    public void setEffectsOn(boolean effectSounds) {
        userPref.setEffectSounds(effectSounds);
        pushChangesToDatabase();
        loadChanges();
    }

    public void setEffectVolume(double effectVolume) {
        userPref.setEffectVolume(effectVolume);
        pushChangesToDatabase();
        loadChanges();
    }

    public boolean getEvalStockfishBased() {
        return userPref.getEvalStockfishBased();
    }

    public void setEvalStockfishBased(boolean isEvalStockfishBased) {
        userPref.setEvalStockfishBased(isEvalStockfishBased);
        pushChangesToDatabase();
        loadChanges();
    }

    public boolean getNMovesStockfishBased() {
        return userPref.getNMovesStockfishBased();
    }

    public void setNMovesStockfishBased(boolean isNMovesStockfishBased) {
        userPref.setNMovesStockfishBased(isNMovesStockfishBased);
        pushChangesToDatabase();
        loadChanges();
    }

    public void setComputerMoveDiff(ComputerDifficulty difficulty) {
        userPref.setComputerMoveDiff(difficulty);
        pushChangesToDatabase();
        loadChanges();
    }

    public ComputerDifficulty getPrefDifficulty() {
        return userPref.getComputerMoveDiff();
    }

    public void setGlobalTheme(GlobalTheme globalTheme) {
        userPref.setGlobalTheme(globalTheme);
        pushChangesToDatabase();
        loadChanges();
    }

    public void setChessboardTheme(ChessboardTheme chessboardTheme) {
        userPref.setChessboardTheme(chessboardTheme);
        pushChangesToDatabase();
        loadChanges();
    }

    public void setPieceTheme(ChessPieceTheme pieceTheme) {
        userPref.setPieceTheme(pieceTheme);
        pushChangesToDatabase();
        loadChanges();
    }

    public void resetToDefault() {
        userPref = ChessConstants.defaultPreferences;
//        pushChangesToDatabase();
        loadChanges();
    }

    public void reloadWithUser(UserPreferences newUserPreferences, boolean updateDatabase) {
        userPref = newUserPreferences;
        loadChanges();
        if (updateDatabase) {
            pushChangesToDatabase();
        }
    }

    private void pushChangesToDatabase() {
        if(App.userManager.isLoggedIn()){
            App.partialDatabaseUpdateRequest(userPref);
        }
        else{
            logger.debug("Not pushing to database, not signed in");
        }
    }

    private void loadChanges() {
        App.adjustGameToUserPreferences(userPref);
        PersistentSaveManager.writeUserprefToSave(userPref);
//        App.messager.sendMessageQuick("Setting updated",App.isStartScreen);

    }

    public void init() {
        loadChanges();
    }

}
