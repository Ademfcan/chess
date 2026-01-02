package chessengine.Functions;

import chessserver.Misc.ChessConstants;
import chessserver.Friends.Friend;
import chessserver.Friends.ServerFriendData;
import chessserver.Friends.FriendDataResponse;
import chessserver.Communication.User;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;

import java.util.ArrayList;
import java.util.List;

public class UserHelperFunctions {
    public static FriendDataResponse createPlaceholderFriends(List<? extends Friend> friendReferences) {
        // if no internet connection, create a fake placeholder with default values
        FriendDataResponse response = new FriendDataResponse(new ArrayList<>());
        for(Friend f : friendReferences){
            UserPreferences pref = UserPreferences.getDefaultPreferences();
            UserInfo info = UserInfo.getPartiallyDefaultUserInfo(f.getCurrentUsername(), ChessConstants.DEFAULTEMAIL, f.getUUID());
            UserWGames newEntry = new UserWGames(new User(info,pref), new ArrayList<>());
            response.dataResponse().add(new ServerFriendData(false, f.getUUID(), f.getCurrentUsername(), newEntry));
        }
        return response;
    }
}
