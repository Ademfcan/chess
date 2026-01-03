package chessengine.Managers;

import chessengine.Crypto.PersistentSaveManager;
import chessserver.ChessRepresentations.PlayerInfo;
import chessserver.Enums.ComputerDifficulty;
import chessserver.Misc.ChessConstants;
import chessserver.User.UserPreferences;

public class UserPreferenceManager{

    // getters

    public UserPreferences getUserPref() {
        return PersistentSaveManager.userPreferenceTracker.getTracked();
    }

    public ComputerDifficulty getPrefDifficulty() {
        return getUserPref().getComputerMoveDiff();
    }

    public PlayerInfo getCurrentComputerPlayer(){
        return ChessConstants.getComputerPlayerInfoWElo(getPrefDifficulty().eloRange);
    }

    public boolean isAnimationsOff() {
        return false; // maybe scrap?
    }

}
