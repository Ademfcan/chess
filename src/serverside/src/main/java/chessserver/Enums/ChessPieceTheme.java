package chessserver.Enums;

public enum ChessPieceTheme {
    TRADITIONAL;

    public static ChessPieceTheme getCorrespondingTheme(String value) {
        for (ChessPieceTheme t : ChessPieceTheme.values()) {
            if (t.toString().equals(value)) {
                return t;
            }
        }
        // todo log error
        return null;
    }
}
