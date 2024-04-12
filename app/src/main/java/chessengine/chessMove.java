package chessengine;

import java.util.Objects;

public class chessMove {
    // 0 = pawn, 1 = knight, 2 = bishop, 3 = rook, 4 = queen, 5 = king
    private int oldX;
    private int oldY;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        chessMove chessMove = (chessMove) o;
        return oldX == chessMove.oldX && oldY == chessMove.oldY && newX == chessMove.newX && newY == chessMove.newY && isCastleMove == chessMove.isCastleMove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldX, oldY, newX, newY, isCastleMove);
    }

    private int newX;
    private int newY;
    private boolean isCastleMove;

    public boolean isPawnPromo() {
        return isPawnPromo;
    }


    public void setPawnPromo(boolean pawnPromo) {
        isPawnPromo = pawnPromo;
    }

    private boolean isPawnPromo;

    public int getPromoIndx() {
        return promoIndx;
    }

    public void setPromoIndx(int promoIndx) {
        this.promoIndx = promoIndx;
    }

    private int promoIndx;

    private int boardIndex;

    private boolean isEating;
    public boolean isEating() {
        return isEating;
    }

    @Override
    public String toString() {
        return "chessMove{" +
                "oldX=" + oldX +
                ", oldY=" + oldY +
                ", newX=" + newX +
                ", newY=" + newY +
                ", isCastleMove=" + isCastleMove +
                ", isEating=" + isEating +
                ", isPawnPromo=" + isPawnPromo +
                ", peiceType=" + chessFunctions.getPieceType(boardIndex) +
                '}';
    }




    public chessMove(int oldX, int oldY, int newX, int newY,boolean isCastleMove,boolean isEating,boolean isPawnPromo,int boardIndex) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.isPawnPromo = isPawnPromo;
        this.isCastleMove = isCastleMove;
        this.isEating = isEating;
        this.boardIndex = boardIndex;

    }



    public boolean isCastleMove() {
        return isCastleMove;
    }


    public int getBoardIndex() {
        return boardIndex;
    }

    public int getOldX() {
        return oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }








}
