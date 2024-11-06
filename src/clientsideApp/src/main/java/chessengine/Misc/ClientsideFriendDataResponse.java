package chessengine.Misc;

import chessserver.DatabaseEntry;
import chessserver.FriendDataResponse;

import java.util.ArrayList;
import java.util.List;

public class ClientsideFriendDataResponse{
    List<ClientsideDataEntry> entries;
    public ClientsideFriendDataResponse(){
        entries = new ArrayList<>();
    }

    public List<ClientsideDataEntry> readDatabaseEntries(){
        return entries;
    }

    public void addDatabaseEntry(ClientsideDataEntry entry){
        entries.add(entry);
    }
}
