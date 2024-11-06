package chessengine.Misc;

import chessserver.DatabaseEntry;
import chessserver.FriendDataResponse;

import java.util.ArrayList;
import java.util.List;

public class ClientsideFriendDataResponse{
    List<DatabaseEntry> entries;
    public ClientsideFriendDataResponse(){
        entries = new ArrayList<>();
    }

    public List<DatabaseEntry> readDatabaseEntries(){
        return entries;
    }

    public void addDatabaseEntry(DatabaseEntry entry){
        entries.add(entry);
    }
}
