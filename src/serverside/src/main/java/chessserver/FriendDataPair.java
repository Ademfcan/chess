package chessserver;

public class FriendDataPair {
    int UUID;
    String friendDatabaseEntryAsString;

    public FriendDataPair(int UUID, String friendDatabaseEntryAsString) {
        this.UUID = UUID;
        this.friendDatabaseEntryAsString = friendDatabaseEntryAsString;
    }

    public FriendDataPair() {

    }

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public String getFriendDatabaseEntryAsString() {
        return friendDatabaseEntryAsString;
    }

    public void setFriendDatabaseEntryAsString(String friendDatabaseEntryAsString) {
        this.friendDatabaseEntryAsString = friendDatabaseEntryAsString;
    }
}
