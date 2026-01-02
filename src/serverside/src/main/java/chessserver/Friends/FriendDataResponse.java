package chessserver.Friends;

import java.util.ArrayList;
import java.util.List;

public record FriendDataResponse(List<ServerFriendData> dataResponse){
    public static FriendDataResponse empty(){
        return new FriendDataResponse(new ArrayList<>());
    }
}
