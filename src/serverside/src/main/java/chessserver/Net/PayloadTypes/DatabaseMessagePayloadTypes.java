package chessserver.Net.PayloadTypes;

import chessserver.ChessRepresentations.GameInfo;
import chessserver.Communication.User;
import chessserver.Net.Payload;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;

import java.util.List;
import java.util.UUID;

public class DatabaseMessagePayloadTypes {
    public record UUIDPayload(UUID uuid) implements Payload {}
    public record UUIDSPayload(List<UUID> uuids) implements Payload {}
    public record UserPayload(User user) implements Payload {}
    public record UserWGamesPayload(UserWGames userWGames) implements Payload {}
    // user related pauloads
    public record UserInfoPayload(UserInfo userInfo) implements Payload{}

    public record UserPrefPayload(UserPreferences userPreferences) implements Payload{}
    public record UserAsStrPayload(String userPrefStr, String userInfoStr) implements Payload {}

    public record GamesPayload(List<GameInfo> games) implements Payload{}

    public record SaveGamesPayload(List<SaveGamePayload> games) implements Payload{}
    public record SaveGamePayload(GameInfo game, boolean isLocal) implements Payload{}

    // user update requests
}
