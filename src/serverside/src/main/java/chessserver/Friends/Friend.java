package chessserver.Friends;

import java.util.Objects;

public class Friend {
    private int UUID;
    private String currentUsername;

    public Friend(String currentUsername, int UUID) {
        this.UUID = UUID;
        this.currentUsername = currentUsername;
    }

    public Friend() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return UUID == friend.UUID && currentUsername.equals(friend.currentUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UUID, currentUsername);
    }

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
