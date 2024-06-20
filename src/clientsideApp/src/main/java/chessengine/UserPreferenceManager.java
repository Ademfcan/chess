package chessengine;

import chessserver.ChessPieceTheme;
import chessserver.ChessboardTheme;
import chessserver.GlobalTheme;
import chessserver.UserPreferences;

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
        System.out.println(userPref.getEvalDepth());
        App.startScreenController.audioSliderBG.setValue(userPref.getBackgroundVolume());
        App.startScreenController.audioSliderEff.setValue(userPref.getEffectVolume());
        App.startScreenController.bgColorSelector.setValue(userPref.getChessboardTheme().toString());
        App.startScreenController.pieceSelector.setValue(userPref.getPieceTheme().toString());

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


    public void setEffectSounds(boolean effectSounds) {
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
        App.messager.sendMessageQuick("Setting updated",App.isStartScreen);

    }

    public void init(){
        loadChanges();
    }


}
