package chessserver;

import java.util.List;

public class FriendDataResponse {
    List<FriendDataPair> dataResponse;

    public FriendDataResponse(List<FriendDataPair> dataResponse) {
        this.dataResponse = dataResponse;
    }

    public FriendDataResponse() {
    }

    public List<FriendDataPair> getDataResponse() {
        return dataResponse;
    }

    public void setDataResponse(List<FriendDataPair> dataResponse) {
        this.dataResponse = dataResponse;
    }
}
