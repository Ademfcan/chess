package chessserver.Communication;

import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;

public class DatabaseEntry {
    private UserInfo userInfo;
    private UserPreferences preferences;

    public DatabaseEntry(UserInfo userInfo, UserPreferences preferences) {
        this.userInfo = userInfo;
        this.preferences = preferences;
    }

    public DatabaseEntry() {

    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }
}
