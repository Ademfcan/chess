package chessengine;

import chessserver.ChessPieceTheme;
import chessserver.ChessboardTheme;
import chessserver.GlobalTheme;
import chessserver.UserPreferences;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

import java.util.Arrays;
import java.util.Objects;

public class UserPreferenceManager {


    private UserPreferences userPref;
    public UserPreferenceManager(){
        userPref = PersistentSaveManager.readUserprefFromSave();
        if(Objects.isNull(userPref)){
            System.out.println("Loading default");
            userPref = ChessConstants.defaultPreferences;
        }


    }

    public void setDefaultSelections(){
        App.startScreenController.themeSelection.getSelectionModel().select(userPref.getGlobalTheme().toString());
        App.startScreenController.computerOptions.getSelectionModel().select(userPref.getComputerMoveDepth()-1);
        App.startScreenController.evalOptions.getSelectionModel().select(userPref.getEvalDepth()-1);
        App.startScreenController.audioSliderBG.setValue(userPref.getBackgroundVolume());
        App.startScreenController.audioSliderEff.setValue(userPref.getEffectVolume());
        App.startScreenController.bgColorSelector.setValue(userPref.getChessboardTheme().toString());
        App.startScreenController.pieceSelector.setValue(userPref.getPieceTheme().toString());
        App.startScreenController.audioMuteBGButton.setText(userPref.isBackgroundmusic() ? "🔉" : "✖");
        App.startScreenController.audioMuteEffButton.setText(userPref.isEffectSounds() ? "🔉" : "✖");

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


    public void setEvalDepth(int evalDepth) {
        userPref.setEvalDepth(evalDepth);
        pushChangesToDatabase();
        loadChanges();
    }


    public void setComputerMoveDepth(int computerMoveDepth) {
        userPref.setComputerMoveDepth(computerMoveDepth);
        pushChangesToDatabase();
        loadChanges();
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

    public void resetToDefault()
    {
        userPref = ChessConstants.defaultPreferences;
        pushChangesToDatabase();
        loadChanges();
    }

    private void pushChangesToDatabase(){
        // todo
    }

    private void loadChanges(){
        App.adjustGameToUserPreferences(userPref);
        PersistentSaveManager.writeUserprefToSave(userPref);
//        App.messager.sendMessageQuick("Setting updated",App.isStartScreen);

    }

    public void init(){
        loadChanges();
    }

    public static void setupUserSettingsScreen(ChoiceBox<String> themeSelection, ComboBox<String> bgColorSelector, ComboBox<String> pieceSelector, Button audioMuteBGButton, Slider audioSliderBG, Button audioMuteEffButton, Slider audioSliderEff, ComboBox<Integer> evalOptions, ComboBox<Integer> computerOptions){
        themeSelection.getItems().addAll("Light","Dark");
        themeSelection.setOnAction(e->{
            if(!themeSelection.getSelectionModel().isEmpty()){
                boolean isLight = themeSelection.getValue().equals("Light");
                App.userPreferenceManager.setGlobalTheme(isLight ? GlobalTheme.Light : GlobalTheme.Dark);
                App.messager.sendMessageQuick("Changed Global Theme to: " + themeSelection.getValue().toLowerCase(),true);

            }
        });

        bgColorSelector.getItems().addAll(Arrays.stream(ChessboardTheme.values()).map(Enum::toString).toList());
        bgColorSelector.setOnAction(e ->{
            if(!bgColorSelector.getSelectionModel().isEmpty()){
                App.userPreferenceManager.setChessboardTheme(ChessboardTheme.getCorrespondingTheme(bgColorSelector.getValue()));
                App.messager.sendMessageQuick("Changed chessboard theme to: " + bgColorSelector.getValue(),true);
            }
        });

        pieceSelector.getItems().addAll(
                Arrays.stream(ChessPieceTheme.values()).map(ChessPieceTheme::toString).toList()
        );
        pieceSelector.setOnAction(e ->{
            if(!pieceSelector.getSelectionModel().isEmpty()){
                App.userPreferenceManager.setPieceTheme(ChessPieceTheme.getCorrespondingTheme(pieceSelector.getValue()));
                App.messager.sendMessageQuick("Changed piece theme to: " + pieceSelector.getValue(),true);
            }
        });

        audioMuteBGButton.setOnMouseClicked(e->{
            boolean isBgCurMuted = App.soundPlayer.isUserPrefBgPaused();
            // isbgCurMuted is the opposite of setbackgroundmusics arg, so no flip needed
            App.userPreferenceManager.setBackgroundmusic(isBgCurMuted);
            App.messager.sendMessageQuick(isBgCurMuted ? "Background Audio Unmuted" : "Background Audio Muted",true);
            audioMuteBGButton.setText(isBgCurMuted ? "🔉":"✖");
        });
        audioSliderBG.setMin(0);
        audioSliderBG.setMax(100);
        audioSliderBG.setOnMouseReleased(e->{
            App.userPreferenceManager.setBackgroundVolume(audioSliderBG.getValue());
            App.messager.sendMessageQuick("Background volume: " + audioSliderBG.getValue(),true);
        });

        audioMuteEffButton.setOnMouseClicked(e->{
            boolean isEffCurMuted = App.soundPlayer.isEffectsMuted();
            // isEffCurMuted is the opposite of effectson
            App.userPreferenceManager.setEffectsOn(isEffCurMuted);
            App.messager.sendMessageQuick(isEffCurMuted ? "Effects Unmuted" : "Effects Muted",true);
            audioMuteEffButton.setText(isEffCurMuted ? "🔉":"✖");
        });
        audioSliderEff.setMin(0);
        audioSliderEff.setMax(100);
        audioSliderEff.setOnMouseReleased(e->{
            App.userPreferenceManager.setEffectVolume(audioSliderBG.getValue());
            App.messager.sendMessageQuick("Effect volume: " + audioSliderBG.getValue(),true);
        });

        evalOptions.getItems().addAll(
                1,2,3,4,5,6,7,8
        );
        evalOptions.setOnAction(e ->{
            if(!evalOptions.getSelectionModel().isEmpty()){
                App.userPreferenceManager.setEvalDepth(evalOptions.getValue());
                App.messager.sendMessageQuick("New Evalation Depth: " + evalOptions.getValue(),true);
            }
        });

        computerOptions.setOnAction(e ->{
            if(!computerOptions.getSelectionModel().isEmpty()){
                App.userPreferenceManager.setComputerMoveDepth(computerOptions.getValue());
                App.messager.sendMessageQuick("New computer Depth: " + computerOptions.getValue(),true);
            }
        });

        computerOptions.getItems().addAll(
                1,2,3,4,5,6,7,8
        );
    }


}
