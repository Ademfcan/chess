package chessengine.Managers;

import chessengine.App;
import chessengine.Crypto.PersistentSaveManager;
import chessengine.Enums.Window;
import chessserver.Enums.ChessPieceTheme;
import chessserver.Enums.ChessboardTheme;
import chessserver.Enums.ComputerDifficulty;
import chessserver.Enums.GlobalTheme;
import chessserver.Misc.ChessConstants;
import chessserver.User.UserPreferences;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

public class UserPreferenceManager {

    private static final Logger logger = LogManager.getLogger("User_Preference_Manager");
    private UserPreferences userPref;
    private boolean hasChanged = false;

    public UserPreferenceManager() {
        userPref = PersistentSaveManager.readUserprefFromSave();
        if (Objects.isNull(userPref)) {
            logger.debug("Loading default");
            userPref = ChessConstants.defaultPreferences;
        }


    }

    public static void setupUserSettingsScreen(ChoiceBox<String> themeSelection, ComboBox<String> bgColorSelector, ComboBox<String> pieceSelector, Button audioMuteBGButton, Slider audioSliderBG, Button audioMuteEffButton, Slider audioSliderEff, ComboBox<String> evalOptions, ComboBox<String> nMovesOptions, ComboBox<String> computerOptions, Window window) {
        if (themeSelection != null) {
            themeSelection.getItems().addAll("Light", "Dark");
            App.bindingController.bindSmallText(themeSelection, window);
            themeSelection.setOnAction(e -> {
                if (!themeSelection.getSelectionModel().isEmpty()) {
                    boolean isLight = themeSelection.getValue().equals("Light");
                    App.userPreferenceManager.setGlobalTheme(isLight ? GlobalTheme.Light : GlobalTheme.Dark);
                    if (App.userPreferenceManager.hasChanged()) {
                        App.messager.sendMessage("Changed Global Theme to: " + themeSelection.getValue().toLowerCase(), App.currentWindow);
                    }

                }
            });
        }

        if (bgColorSelector != null) {
            App.bindingController.bindSmallText(bgColorSelector, window);
            bgColorSelector.getItems().addAll(Arrays.stream(ChessboardTheme.values()).map(Enum::toString).toList());
            bgColorSelector.setOnAction(e -> {
                if (!bgColorSelector.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setChessboardTheme(ChessboardTheme.getCorrespondingTheme(bgColorSelector.getValue()));
                    if (App.userPreferenceManager.hasChanged()) {
                        App.messager.sendMessage("Changed chessboard theme to: " + bgColorSelector.getValue(), App.currentWindow);
                    }
                }
            });
        }

        if (pieceSelector != null) {
            App.bindingController.bindSmallText(pieceSelector, window);
            pieceSelector.getItems().addAll(
                    Arrays.stream(ChessPieceTheme.values()).map(ChessPieceTheme::toString).toList()
            );
            pieceSelector.setOnAction(e -> {
                if (!pieceSelector.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setPieceTheme(ChessPieceTheme.getCorrespondingTheme(pieceSelector.getValue()));
                    if (App.userPreferenceManager.hasChanged()) {
                        App.messager.sendMessage("Changed piece theme to: " + pieceSelector.getValue(), App.currentWindow);
                    }
                }
            });
        }

        if (audioMuteBGButton != null) {
            App.bindingController.bindSmallText(audioMuteBGButton, window);
            audioMuteBGButton.setOnMouseClicked(e -> {
                boolean isBgCurMuted = App.soundPlayer.isUserPrefBgPaused();
                // isbgCurMuted is the opposite of setbackgroundmusics arg, so no flip needed
                App.userPreferenceManager.setBackgroundmusic(isBgCurMuted);
                if (App.userPreferenceManager.hasChanged()) {
                    App.messager.sendMessage(isBgCurMuted ? "Background Audio Unmuted" : "Background Audio Muted", App.currentWindow);
                    audioMuteBGButton.setText(isBgCurMuted ? "🔉" : "✖");
                }
            });
        }

        if (audioSliderBG != null) {

            audioSliderBG.setMin(0);
            audioSliderBG.setMax(1);
            audioSliderBG.setOnMouseReleased(e -> {
                App.userPreferenceManager.setBackgroundVolume(audioSliderBG.getValue());
                if (App.userPreferenceManager.hasChanged()) {
                    App.messager.sendMessage("Background volume: " + audioSliderBG.getValue(), App.currentWindow);
                }
            });
        }

        if (audioMuteEffButton != null) {
            App.bindingController.bindSmallText(audioMuteEffButton, window);
            audioMuteEffButton.setOnMouseClicked(e -> {
                boolean isEffCurMuted = App.soundPlayer.isEffectsMuted();
                // isEffCurMuted is the opposite of effectson
                App.userPreferenceManager.setEffectsOn(isEffCurMuted);
                if (App.userPreferenceManager.hasChanged()) {
                    App.messager.sendMessage(isEffCurMuted ? "Effects Unmuted" : "Effects Muted", App.currentWindow);
                    audioMuteEffButton.setText(isEffCurMuted ? "🔉" : "✖");
                }
            });
        }

        if (audioSliderEff != null) {
            audioSliderEff.setMin(0);
            audioSliderEff.setMax(1);
            audioSliderEff.setOnMouseReleased(e -> {
                App.userPreferenceManager.setEffectVolume(audioSliderEff.getValue());
                if (App.userPreferenceManager.hasChanged()) {
                    App.messager.sendMessage("Effect volume: " + audioSliderEff.getValue(), App.currentWindow);
                }
            });
        }

        if (evalOptions != null) {
            App.bindingController.bindSmallText(evalOptions, window);
            evalOptions.getItems().addAll(
                    "Stockfish", "My Computer"
            );
            evalOptions.setOnAction(e -> {
                if (!evalOptions.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setEvalStockfishBased(evalOptions.getValue().equals("Stockfish"));
                    if (App.userPreferenceManager.hasChanged()) {
                        App.messager.sendMessage("Changing eval base to: " + evalOptions.getValue(), App.currentWindow);
                    }
                }
            });
        }

        if (nMovesOptions != null) {
            App.bindingController.bindSmallText(nMovesOptions, window);
            nMovesOptions.getItems().addAll(
                    "Stockfish", "My Computer"
            );
            nMovesOptions.setOnAction(e -> {
                if (!nMovesOptions.getSelectionModel().isEmpty()) {
                    App.userPreferenceManager.setNMovesStockfishBased(nMovesOptions.getValue().equals("Stockfish"));
                    if (App.userPreferenceManager.hasChanged()) {
                        App.messager.sendMessage("Changing nMoves base to: " + nMovesOptions.getValue(), App.currentWindow);
                    }
                }
            });
        }

        if (computerOptions != null) {
            App.bindingController.bindSmallText(computerOptions, window);
            computerOptions.setOnAction(e -> {
                if (!computerOptions.getSelectionModel().isEmpty()) {
                    String out = computerOptions.getValue();
                    if (out.contains("(S*)")) {
                        out = out.substring(0, out.indexOf("(S*)"));
                    }
                    App.userPreferenceManager.setComputerMoveDiff(ComputerDifficulty.getDifficultyOffOfElo(Integer.parseInt(out), false));
                    if (App.userPreferenceManager.hasChanged()) {
                        App.messager.sendMessage("New computer elo: " + out, App.currentWindow);
                        // if you change the computer, remove cached centralcontrol entries
                        App.ChessCentralControl.clearCache();
                    }
                }
            });

            computerOptions.getItems().addAll(
                    Arrays.stream(ComputerDifficulty.values()).map(c -> c.eloRange + (c.isStockfishBased ? "(S*)" : "")).toList()
            );
        }

    }

    public boolean hasChanged() {
        return hasChanged;
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

    public void setBackgroundmusic(boolean isBackgroundMusic) {
        if (userPref.isBackgroundmusic() != isBackgroundMusic) {
            userPref.setBackgroundmusic(isBackgroundMusic);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setBackgroundVolume(double backgroundVolume) {
        if (userPref.getBackgroundVolume() != backgroundVolume) {
            userPref.setBackgroundVolume(backgroundVolume);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setEffectsOn(boolean effectSounds) {
        if (userPref.isEffectSounds() != effectSounds) {
            userPref.setEffectSounds(effectSounds);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setEffectVolume(double effectVolume) {
        if (userPref.getEffectVolume() != effectVolume) {
            userPref.setEffectVolume(effectVolume);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setEvalStockfishBased(boolean isEvalStockfishBased) {
        if (userPref.getEvalStockfishBased() != isEvalStockfishBased) {
            userPref.setEvalStockfishBased(isEvalStockfishBased);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setNMovesStockfishBased(boolean isNMovesStockfishBased) {
        if (userPref.getNMovesStockfishBased() != isNMovesStockfishBased) {
            userPref.setNMovesStockfishBased(isNMovesStockfishBased);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }

    }

    public void setComputerMoveDiff(ComputerDifficulty difficulty) {
        if (!userPref.getComputerMoveDiff().equals(difficulty)) {
            userPref.setComputerMoveDiff(difficulty);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    private void markNoChanges() {
        hasChanged = false;
    }

    public ComputerDifficulty getPrefDifficulty() {
        return userPref.getComputerMoveDiff();
    }

    public void setGlobalTheme(GlobalTheme globalTheme) {
        if (userPref.getGlobalTheme() != globalTheme) {
            userPref.setGlobalTheme(globalTheme);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setChessboardTheme(ChessboardTheme chessboardTheme) {
        if (userPref.getChessboardTheme() != chessboardTheme) {
            userPref.setChessboardTheme(chessboardTheme);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void setPieceTheme(ChessPieceTheme pieceTheme) {
        if (userPref.getPieceTheme() != pieceTheme) {
            userPref.setPieceTheme(pieceTheme);
            pushChangesToDatabase();
            loadChanges();
        } else {
            markNoChanges();
        }
    }

    public void resetToDefault(boolean isLogout) {
        userPref = ChessConstants.defaultPreferences;
        if (!isLogout) {
            pushChangesToDatabase();
        }
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
        if (App.userManager.isLoggedIn()) {
            App.partialDatabaseUpdateRequest(userPref);
        } else {
            logger.debug("Not pushing to database, not signed in");
        }
    }

    private void loadChanges() {
        hasChanged = true;
        App.adjustGameToUserPreferences(userPref);
        PersistentSaveManager.writeUserprefToSave(userPref);
//        App.messager.sendMessageQuick("Setting updated",App.isStartScreen);

    }

    public void init() {
        loadChanges();
    }

    public boolean isNoAnimate() {
        return false; // todo
    }
}
