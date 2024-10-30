package chessserver;

public class Friend {
    private int UUID;

    public Friend(String currentUsername,int UUID) {
        this.UUID = UUID;
        this.currentUsername = currentUsername;
    }
    public Friend() {
    }

    private String currentUsername;

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }
}
