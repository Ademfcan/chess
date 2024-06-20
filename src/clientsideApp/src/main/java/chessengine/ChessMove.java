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



    private boolean isCustomMove;







    public ChessMove(int oldX, int oldY, int newX, int newY,int promoIndx, int boardIndex, boolean isWhite,boolean isCastleMove, boolean isEating,boolean isCustomMove) {
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

    }
    private final int boardSize = 7;
    public ChessMove invertMove(){
        return new ChessMove(oldX,boardSize-oldY,newX,boardSize-newX,promoIndx,boardIndex,!isWhite,isCastleMove,isEating,isCustomMove);
    }

    public ChessMove reverseMove(){
        return new ChessMove(newX,newY,oldX,oldY,promoIndx,boardIndex,isWhite,isCastleMove,isEating,isCastleMove);
    }


    public boolean isPawnPromo() {
        return isPawnPromo;
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
        return new ChessMove(this.oldX,this.oldY,this.newX,this.newY,this.promoIndx,this.boardIndex,this.isWhite,this.isCastleMove,this.isEating,this.isCustomMove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return oldX == chessMove.oldX && oldY == chessMove.oldY && newX == chessMove.newX && newY == chessMove.newY && isCastleMove == chessMove.isCastleMove && chessMove.isWhite == isWhite && chessMove.isCustomMove == isCustomMove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldX, oldY, newX, newY, isCastleMove,isCustomMove);
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
                ", pieceType=" + GeneralChessFunctions.getPieceType(boardIndex) +
                ", isWhite=" + isWhite +
                ", isCustomMove=" + isCustomMove +
                '}';
    }








}
