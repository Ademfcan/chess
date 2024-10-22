package chessserver;

public class DatabaseRequest {
    String entryAsString;
    String userName;
    int UUID;
    long requestTimeStampMS;
    String passwordHash;

    public DatabaseRequest(String entryAsString, String userName, String passwordHash, int UUID, long requestTimeStamp) {
        this.entryAsString = entryAsString;
        this.userName = userName;
        this.UUID = UUID;
        this.requestTimeStampMS = requestTimeStamp;
        this.passwordHash = passwordHash;
    }

    public DatabaseRequest() {

    }

    public String getEntryAsString() {
        return entryAsString;
    }

    public void setEntryAsString(String entryAsString) {
        this.entryAsString = entryAsString;
    }

    public long getRequestTimeStampMS() {
        return requestTimeStampMS;
    }

    public void setRequestTimeStampMS(long requestTimeStampMS) {
        this.requestTimeStampMS = requestTimeStampMS;
    }

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public String getEntry() {
        return entryAsString;
    }

    public void setEntry(String entry) {
        this.entryAsString = entry;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}
