package chessengine.Graphics;

import chessserver.Friends.FriendDataResponse;

public interface OnFriendUpdate {
    void onCurrentFriends(FriendDataResponse response);
    void onCurrentOutgoingRequests(FriendDataResponse response);
    void onCurrentIncomingRequests(FriendDataResponse response);
    void onSuggestedFriends(FriendDataResponse response);
}
