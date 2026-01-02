package chessserver.ChessRepresentations;

import chessserver.Enums.ProfilePicture;
import chessserver.User.UserInfo;

import java.util.UUID;

public record PlayerInfo(UUID playerID, String playerName, int playerElo, String playerPfpUrl) {
    public static PlayerInfo defaultInfo = new PlayerInfo(UUID.nameUUIDFromBytes("defaultPlayer".getBytes()), "defaultPlayer", 0, ProfilePicture.DEFAULT.urlString);

    public static PlayerInfo fromUser(UserInfo userInfo) {
        return new PlayerInfo(userInfo.getUuid(), userInfo.getUserName(), userInfo.getUserelo(), ProfilePicture.DEFAULT.urlString);
    }
}
