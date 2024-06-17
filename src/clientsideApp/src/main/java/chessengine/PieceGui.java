package chessengine;

public class PieceGui {
    int x;
    int y;
    int pieceType;

    boolean isWhite;

    public PieceGui(int x, int y, int pieceType,boolean isWhite) {
        this.x = x;
        this.y = y;
        this.pieceType = pieceType;
        this.isWhite = isWhite;
    }
}
