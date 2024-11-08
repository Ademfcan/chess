package chessserver.Friends;

public class FriendDataPair {
    boolean isOnline;
    int UUID;
    String friendDatabaseEntryAsString;
    public FriendDataPair(int UUID, String friendDatabaseEntryAsString, boolean isOnline) {
        this.UUID = UUID;
        this.isOnline = isOnline;
        this.friendDatabaseEntryAsString = friendDatabaseEntryAsString;
    }
    public FriendDataPair() {

    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
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
