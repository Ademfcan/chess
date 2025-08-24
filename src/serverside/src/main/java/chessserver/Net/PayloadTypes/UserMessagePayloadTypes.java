package chessserver.Net.PayloadTypes;

import chessserver.Communication.User;
import chessserver.Friends.FriendDataResponse;
import chessserver.Net.Payload;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;

import java.util.List;
import java.util.UUID;

public class UserMessagePayloadTypes {
    public record Friend(String userName, UUID userUUID) implements Payload {}
    public record FriendPayload(Friend friend) implements Payload {};
    public record FriendListPayload(List<Friend> payload) implements Payload {}
    public record FriendDataResponsePayload(FriendDataResponse response) implements Payload {}
    public record UserInfoPayload(UserInfo info) implements Payload {}
    public record UserPrefPayload(UserPreferences info) implements Payload {}
    public record UserPayload(User user) implements Payload {}
    public record RefreshAcessTokenPayload(String refreshToken, String deviceID) implements Payload {}
    public record JWTAcessTokenPayload(String JwtToken) implements Payload {}
    public record SignUpPayload(String userName, String userEmail, String passwordPlaintext, String deviceID) implements Payload {}
    public record LoginPayload(String userName, String passwordPlaintext, String deviceID) implements Payload {}
}
