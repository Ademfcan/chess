package chessengine.Functions;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.Crypto.CryptoUtils;
import chessengine.Misc.ChessConstants;
import chessengine.Misc.ClientsideDataEntry;
import chessengine.Misc.ClientsideFriendDataResponse;
import chessserver.*;

import java.util.ArrayList;
import java.util.List;

public class UserHelperFunctions {
    public static List<ChessGame> readSavedGames(List<String> savedGamesStr) {
        List<ChessGame> out = new ArrayList<>();
        for (String save : savedGamesStr) {
            out.add(CryptoUtils.gameFromSaveString(save));
        }

        return out;
    }

    public static ClientsideFriendDataResponse readFriendDataResponse(FriendDataResponse friendDataResponse){
        if(friendDataResponse == null){
            return null;
        }
        ClientsideFriendDataResponse clientsideFriendDataResponse = new ClientsideFriendDataResponse();
        for(FriendDataPair friendDataPair : friendDataResponse.getDataResponse()){
            if(friendDataPair.getFriendDatabaseEntryAsString().isEmpty()){
                continue;
            }
            clientsideFriendDataResponse.addDatabaseEntry(new ClientsideDataEntry(friendDataPair.isOnline(),ChessConstants.readFromObjectMapper(friendDataPair.getFriendDatabaseEntryAsString(), DatabaseEntry.class)));
        }

        return clientsideFriendDataResponse;
    }

    public static ClientsideFriendDataResponse createPlaceholderFriends(List<? extends Friend> friendReferences) {
        // if no internet connection, create a fake placeholder with default values
        ClientsideFriendDataResponse response = new ClientsideFriendDataResponse();
        for(Friend f : friendReferences){
            UserPreferences pref = ChessConstants.defaultPreferences;
            UserInfo info = UserInfo.getPartiallyDefaultUserInfo(f.getCurrentUsername(),f.getUUID());
            DatabaseEntry newEntry = new DatabaseEntry(info,pref);
            response.addDatabaseEntry(new ClientsideDataEntry(false,newEntry));
        }
        return response;
    }
}
