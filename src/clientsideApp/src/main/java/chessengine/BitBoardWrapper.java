package chessengine;

import java.util.Arrays;

public class BitBoardWrapper {
    private long[] whitePieces;
    private long[] blackPieces;
    private XYcoord whiteKingLocation;

    private XYcoord blackKingLocation;

    private long tempChange;

    private int tempIndex;

    private boolean tempIsWhite;

    public long[] getWhitePieces() {
        return whitePieces;
    }

    public void setWhitePieces(long[] whitePieces) {
        this.whitePieces = whitePieces;
    }

    public long[] getBlackPieces() {
        return blackPieces;
    }

    public void setBlackPieces(long[] blackPieces) {
        this.blackPieces = blackPieces;
    }

    public XYcoord getWhiteKingLocation() {
        return whiteKingLocation;
    }

    public void setWhiteKingLocation(XYcoord whiteKingLocation) {
        this.whiteKingLocation = whiteKingLocation;
    }

    public XYcoord getBlackKingLocation() {
        return blackKingLocation;
    }

    public void setBlackKingLocation(XYcoord blackKingLocation) {
        this.blackKingLocation = blackKingLocation;
    }


    public BitBoardWrapper(long[] whitePieces, long[] blackPieces) {
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.whiteKingLocation = GeneralChessFunctions.getPieceCoords(whitePieces[5]).get(0);
        this.blackKingLocation = GeneralChessFunctions.getPieceCoords(blackPieces[5]).get(0);
        this.tempIndex = ChessConstants.EMPTYINDEX;
    }

    public BitBoardWrapper(long[] whitePieces, long[] blackPieces, XYcoord whitekingLocation, XYcoord blackkingLocation) {
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.whiteKingLocation = whitekingLocation;
        this.blackKingLocation = blackkingLocation;
        this.tempIndex = ChessConstants.EMPTYINDEX;
    }




    public BitBoardWrapper cloneBoard(){
        return new BitBoardWrapper(Arrays.copyOf(this.whitePieces,this.whitePieces.length),Arrays.copyOf(this.blackPieces,this.blackPieces.length),new XYcoord(this.whiteKingLocation.x,this.whiteKingLocation.y),new XYcoord(this.blackKingLocation.x,this.blackKingLocation.y));
    }

    public void setKingLocation(boolean isWhite, XYcoord kingLocation){
        if(isWhite){
            whiteKingLocation = kingLocation;
        }
        else{
            blackKingLocation = kingLocation;
        }
    }


    public void makeTempChange(int oldX, int oldY, int newX, int newY,int boardIndex, boolean isWhiteBoard){
        if(tempIndex == ChessConstants.EMPTYINDEX){
            long changingBoard = isWhiteBoard ? whitePieces[boardIndex] : blackPieces[boardIndex];
            tempChange = changingBoard;
            changingBoard = GeneralChessFunctions.RemovePeice(oldX,oldY,changingBoard);
            changingBoard = GeneralChessFunctions.AddPeice(newX,newY,changingBoard);
            if(isWhiteBoard){
                whitePieces[boardIndex] = changingBoard;
            }
            else{
                blackPieces[boardIndex] = changingBoard;
            }
            tempIndex = boardIndex;
            tempIsWhite = isWhiteBoard;
        }
        else{
            ChessConstants.mainLogger.error("Cannot make another temp change, one is already present");
        }


    }



    public void popTempChange(){
        if(tempIndex != ChessConstants.EMPTYINDEX){
            if(tempIsWhite){
                whitePieces[tempIndex] = tempChange;
            }
            else{
                blackPieces[tempIndex] = tempChange;
            }
            tempIndex = ChessConstants.EMPTYINDEX;

        }
        else{
            ChessConstants.mainLogger.error("Trying to pop temp change when not active!");
        }


    }

    public void keepTempChange(){
        tempIndex = ChessConstants.EMPTYINDEX;
    }





}
