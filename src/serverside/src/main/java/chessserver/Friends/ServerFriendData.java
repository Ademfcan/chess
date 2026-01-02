package chessserver.Friends;

import chessserver.Communication.User;
import chessserver.User.UserWGames;

import java.util.UUID;

public record ServerFriendData(boolean isOnline, UUID UUID, String currentUsername, UserWGames dataEntry) {}
