package chessengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class chessPosition {
    private long[] whitePeices;

    public chessMove getMoveThatCreatedThis() {
        return moveThatCreatedThis;
    }

    private long[] blackPeices;

    chessMove moveThatCreatedThis = null;

    public long[] getWhitePeices() {
        return whitePeices;
    }

    public long[] getBlackPeices() {
        return blackPeices;
    }


    public chessPosition(long[] whitePeices, long[] blackPeices){
        this.whitePeices = whitePeices;
        this.blackPeices = blackPeices;
    }

    public chessPosition(long[] whitePeicesM, long[] blackPeicesM,int peiceType, boolean isWhite, boolean isCastle,boolean isPawnPromo, int oldX, int oldY, int newX, int newY,int promoIndex){
        long[] currentBoardMod = isWhite ? whitePeicesM : blackPeicesM;
        long[] enemyBoardMod = isWhite ? blackPeicesM : whitePeicesM;
        if(isCastle){
            // check if short or long castle and move appropiately
            boolean isShortCastle = newX == 6;
            if(isShortCastle){
                currentBoardMod[3] = chessFunctions.RemovePeice(7,newY,currentBoardMod[3]);
                currentBoardMod[3] = chessFunctions.AddPeice(newX-1,newY,currentBoardMod[3]);
            }
            else{
                currentBoardMod[3] = chessFunctions.RemovePeice(0,newY,currentBoardMod[3]);
                currentBoardMod[3] = chessFunctions.AddPeice(newX+1,newY,currentBoardMod[3]);
            }
        }

        boolean isEating = false;
        if(chessFunctions.checkIfContains(newX,newY,!isWhite,whitePeicesM,blackPeicesM)){
            // eating enemyPeice
            isEating = true;
            int boardWithPiece = chessFunctions.getBoardWithPiece(newX,newY,!isWhite,whitePeicesM,blackPeicesM);
            enemyBoardMod[boardWithPiece] = chessFunctions.RemovePeice(newX,newY,enemyBoardMod[boardWithPiece]);
        }
        // remove peice at old spot
        currentBoardMod[peiceType] = chessFunctions.RemovePeice(oldX,oldY,currentBoardMod[peiceType]);

        if(isPawnPromo){
            // promo with new peice at new location
            currentBoardMod[promoIndex] = chessFunctions.AddPeice(newX,newY,currentBoardMod[promoIndex]);
        }
        else{
            // move to new place as usual
            currentBoardMod[peiceType] = chessFunctions.AddPeice(newX,newY,currentBoardMod[peiceType]);
        }
        this.whitePeices = whitePeicesM;
        this.blackPeices = blackPeicesM;
        moveThatCreatedThis = new chessMove(oldX,oldY,newX,newY,isCastle,isEating,isPawnPromo,peiceType);
        moveThatCreatedThis.setPawnPromo(isPawnPromo);
        moveThatCreatedThis.setPromoIndx(promoIndex);
        //System.out.println(moveThatCreatedThis.toString());
    }

    public List<chessPosition> getAllChildPositions(pieceLocationHandler myPeiceHandler, boolean isWhite){
        List<chessPosition> childPositions = new ArrayList<>();
        List<XYcoord> peices = chessFunctions.getPieceCoordsForComputer(isWhite ? whitePeices : blackPeices);
        for(XYcoord coord : peices){
            List<XYcoord> piecePossibleMoves = myPeiceHandler.getPossibleMoves(coord.x,coord.y,isWhite,whitePeices,blackPeices);
            int peiceType = coord.peiceType;
            for(XYcoord move : piecePossibleMoves){
                if(chessFunctions.getBoardWithPiece(move.x,move.y,!isWhite,whitePeices,blackPeices) != 5) {
                    if (move.isPawnPromo()) {
                        // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                        for (int i = 1; i < 5; i++) {
                            chessPosition childPos = new chessPosition(Arrays.copyOf(whitePeices, whitePeices.length), Arrays.copyOf(blackPeices, blackPeices.length), peiceType, isWhite, move.isCastleMove(), true, coord.x, coord.y, move.x, move.y, i);
                            childPositions.add(childPos);

                        }

                    } else {
                        chessPosition childPos = new chessPosition(Arrays.copyOf(whitePeices, whitePeices.length), Arrays.copyOf(blackPeices, blackPeices.length), peiceType, isWhite, move.isCastleMove(), false, coord.x, coord.y, move.x, move.y, -10);
                        childPositions.add(childPos);
                    }
                }


            }
        }
        return childPositions;
    }


}
