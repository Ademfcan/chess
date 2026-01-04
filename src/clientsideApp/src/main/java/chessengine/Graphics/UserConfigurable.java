package chessengine.Graphics;

import chessserver.Communication.User;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;

public interface UserConfigurable {
    void updateWithUser(UserWGames user);
}
