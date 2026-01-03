package chessserver.Communication;

import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;

import java.util.UUID;

public record User(UserInfo userInfo, UserPreferences preferences) { }
