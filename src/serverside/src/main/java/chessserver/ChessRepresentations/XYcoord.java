package chessserver.ChessRepresentations;

import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;

import java.util.Objects;

public class XYcoord {
    public final int direction;
    private final boolean isCastleMove;
    public int x;
    public int y;
    public int peiceType;
    private boolean isPawnPromo;
    private final boolean enPassant;
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

    public XYcoord(int bitIndex) {
        int[] xy = BitFunctions.bitindexToXY(bitIndex);
        this.x = xy[0];
        this.y = xy[1];
        this.isCastleMove = false;
        this.enPassant = false;
        this.direction = ChessConstants.EMPTYINDEX;
        this.peiceType = ChessConstants.EMPTYINDEX;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCastleMove, x, y, isPawnPromo, enPassant);
    }

    public void setPawnPromo(boolean pawnPromo) {
        isPawnPromo = pawnPromo;
    }

    /**
     * inline setting pawn promo
     **/

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


    public String toString() {
        return "X: " + x + " Y: " + y + " Is castle move?: " + isCastleMove;
    }

}
