package chessengine.ChessRepresentations;

import chessengine.Misc.ChessConstants;

public class XYcoord {
    private final boolean isCastleMove;
    public int x;
    public int y;
    public final int direction;
    public int peiceType;
    private boolean isPawnPromo;
    private boolean enPassant;
    public XYcoord(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.isCastleMove = false;
        this.enPassant = false;
        this.direction = direction;
        this.peiceType = ChessConstants.EMPTYINDEX;
    }

    public XYcoord(int x, int y, boolean isCastleMove) {
        this.x = x;
        this.y = y;
        this.isCastleMove = isCastleMove;
        this.enPassant = false;
        this.direction = ChessConstants.EMPTYINDEX;
        this.peiceType = ChessConstants.EMPTYINDEX;
    }

    public XYcoord(int x, int y) {
        this.x = x;
        this.y = y;
        this.isCastleMove = false;
        this.enPassant = false;
        this.direction = ChessConstants.EMPTYINDEX;
        this.peiceType = ChessConstants.EMPTYINDEX;
    }

    public boolean isCastleMove() {
        return isCastleMove;
    }

    public boolean isPawnPromo() {
        return isPawnPromo;
    }

    public void setPawnPromo(boolean pawnPromo) {
        isPawnPromo = pawnPromo;
    }

    /**
     * inline setting pawn promo
     **/
    public XYcoord setPromoHack(boolean pawnPromo) {
        isPawnPromo = pawnPromo;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;

        }
        XYcoord xy = (XYcoord) obj;
        return (this.x == xy.x && this.y == xy.y);

    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }

    public String toString() {
        return "X: " + x + " Y: " + y + " Is castle move?: " + isCastleMove;
    }

}
