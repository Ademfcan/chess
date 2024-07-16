package chessengine;

import org.checkerframework.checker.units.qual.C;

import java.util.Objects;

public class ChessMove {
    // 0 = pawn, 1 = knight, 2 = bishop, 3 = rook, 4 = queen, 5 = king
    private int oldX;
    private int oldY;

    private boolean isWhite;

    private int newX;
    private int newY;
    private boolean isCastleMove;



    private boolean isPawnPromo;



    private int promoIndx;

    private int boardIndex;

    private boolean isEating;

    private int eatingIndex;

    private boolean isEnPassant;


    private boolean isCustomMove;


    public int getEatingIndex() {
        return eatingIndex;
    }

    public ChessMove(int oldX, int oldY, int newX, int newY, int promoIndx, int boardIndex, boolean isWhite, boolean isCastleMove, boolean isEating, int eatingIndex, boolean isEnPassant, boolean isCustomMove) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.isPawnPromo = promoIndx != ChessConstants.EMPTYINDEX;
        this.promoIndx = promoIndx;
        this.isCastleMove = isCastleMove;
        this.isEating = isEating;
        this.boardIndex = boardIndex;
        this.isWhite = isWhite;
        this.isCustomMove = isCustomMove;
        this.isEnPassant = isEnPassant;
        this.eatingIndex = eatingIndex;

    }

    private final int boardSize = 7;
    public ChessMove invertMove(){
        return new ChessMove(oldX,boardSize-oldY,newX,boardSize-newX,promoIndx,boardIndex,!isWhite,isCastleMove,isEating,eatingIndex,isEnPassant,isCustomMove);
    }
    public ChessMove reverseMove(){
        return new ChessMove(newX,newY,oldX,oldY,promoIndx,boardIndex,isWhite,isCastleMove,isEating,eatingIndex,isEnPassant,isCustomMove);
    }


    public boolean isPawnPromo() {
        return isPawnPromo;
    }


    public boolean isEnPassant() {
        return isEnPassant;
    }


    public int getPromoIndx() {
        return promoIndx;
    }


    public boolean isEating() {
        return isEating;
    }


    public boolean isCastleMove() {
        return isCastleMove;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isCustomMove() {
        return isCustomMove;
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

    public ChessMove cloneMove(){
        // all by value
        return new ChessMove(this.oldX,this.oldY,this.newX,this.newY,this.promoIndx,this.boardIndex,this.isWhite,this.isCastleMove,this.isEating,this.eatingIndex,this.isEnPassant,this.isCustomMove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return oldX == chessMove.oldX && oldY == chessMove.oldY && newX == chessMove.newX && newY == chessMove.newY && isCastleMove == chessMove.isCastleMove && chessMove.isWhite == isWhite && chessMove.isCustomMove == isCustomMove && chessMove.boardIndex == boardIndex && chessMove.eatingIndex == eatingIndex;
    }



    @Override
    public int hashCode() {
        return Objects.hash(oldX, oldY, newX, newY,boardIndex,isEating,eatingIndex,isWhite, isCastleMove);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "oldX=" + oldX +
                ", oldY=" + oldY +
                ", isWhite=" + isWhite +
                ", newX=" + newX +
                ", newY=" + newY +
                ", isCastleMove=" + isCastleMove +
                ", isPawnPromo=" + isPawnPromo +
                ", promoIndx=" + promoIndx +
                ", boardIndex=" + boardIndex +
                ", isEating=" + isEating +
                ", eatingIndex=" + eatingIndex +
                ", isEnPassant=" + isEnPassant +
                ", isCustomMove=" + isCustomMove +
                '}';
    }








}
