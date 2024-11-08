package chessengine.Misc;

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
