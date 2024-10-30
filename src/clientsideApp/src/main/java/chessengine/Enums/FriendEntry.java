package chessengine.Enums;

public enum FriendEntry {
    UNCONNECTED("UserSettingIcons/add-user.png"),
    OUTGOINGREQUESTED("UserSettingIcons/outgoing.png"),
    INCOMINGREQUESTED("UserSettingIcons/incoming.png"),
    CONNECTED("UserSettingIcons/add-friend.png");

    public final String urlString;

    private FriendEntry(String urlString){
        this.urlString = urlString;
    }

    public static FriendEntry getEntryTypeFromInt(int i){
        switch (i){
            case 0 -> {
                return FriendEntry.UNCONNECTED;
            }
            case 1 ->{
                return FriendEntry.CONNECTED;
            }
            case 2 ->{
                return FriendEntry.OUTGOINGREQUESTED;
            }
            case 3 ->{
                return FriendEntry.INCOMINGREQUESTED;
            }

            default -> {
                return null;
            }

        }
    }
}
