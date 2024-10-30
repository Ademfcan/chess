package chessengine.Enums;

public enum NavIcons {
    CAMPAGIN("NavIcons/campaign.png"),
    HOME("NavIcons/home.png"),
    ONLINE("NavIcons/online.png"),
    PGN("NavIcons/pgn.png"),
    MORE("NavIcons/more.png");



    public final String urlString;

    private NavIcons(String urlString){
        this.urlString = urlString;
    }
}
