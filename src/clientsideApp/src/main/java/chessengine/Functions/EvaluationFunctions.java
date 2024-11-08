package chessengine.Functions;

import chessserver.ChessRepresentations.BackendChessPosition;
import chessserver.ChessRepresentations.BitBoardWrapper;
import chessserver.ChessRepresentations.XYcoord;
import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import chessserver.Misc.pieceMapHandler;

public class EvaluationFunctions {
    public static  final int baseMateScore = -1000000;

    public static boolean isMateScore(int currentScore){
        return Math.abs(currentScore) >= -(baseMateScore);
    }

    public static  int extractDepthFromMateScore(int mateScore){
        return (Math.abs(mateScore)+baseMateScore);
    }


    public static int getStaticEvaluation(BackendChessPosition pos){
        int[][][] startPieceMap = pieceMapHandler.startPieceMapsCp1;
        int[][][] endPieceMap = pieceMapHandler.endPieceMapsCp1;
        BitBoardWrapper board = pos.board;
        double startWeight = (board.getWhitePieceCount() + board.getBlackPieceCount())/(double) ChessConstants.BOTHSIDEPIECECOUNT;
        long[] whitePiecesBB = board.getWhitePiecesBB();
        long[] blackPiecesBB = board.getBlackPiecesBB();
        int whiteScore = 0;
        int blackScore = 0;
        for(int i = 0;i<whitePiecesBB.length;i++){
            XYcoord[] coords = GeneralChessFunctions.getPieceCoordsArray(whitePiecesBB[i]);
            for(XYcoord coord : coords){
                if(i == 0){
                    if(BitFunctions.isPassedPawn(coord.x,coord.y,true,pos.board)){
                        whiteScore += 100;
                    }
                }
                int flippedX = 7-coord.x;
                int flippedY = 7-coord.y;
                int positionalValue = (int)(startPieceMap[i][flippedX][flippedY]*startWeight + endPieceMap[i][flippedX][flippedY] * (1-startWeight));
                // todo adjust square values
                whiteScore += positionalValue/2;
            }
            whiteScore += ChessConstants.valueMapCentiPawn[i]*coords.length;
        }

        for(int i = 0;i<blackPiecesBB.length;i++){
            XYcoord[] coords = GeneralChessFunctions.getPieceCoordsArray(blackPiecesBB[i]);
            for(XYcoord coord : coords){
                if(BitFunctions.isPassedPawn(coord.x,coord.y,false,pos.board)){
                    blackScore += 100;
                }
                // todo
                int positionalValue = (int)(startPieceMap[i][coord.x][coord.y]*startWeight + endPieceMap[i][coord.x][coord.y] * (1-startWeight));
                // todo adjust square values
                blackScore += positionalValue/2;
            }
            blackScore += ChessConstants.valueMapCentiPawn[i]*coords.length;
        }
        int enemyPieceCount = pos.isWhiteTurn ? pos.board.getBlackPieceCount() : pos.board.getWhitePieceCount();
        XYcoord whiteKingLocation = pos.board.getWhiteKingLocation();
        XYcoord blackKingLocation = pos.board.getBlackKingLocation();
        int eval =  (whiteScore - blackScore);
        eval += kingDistanceScore(enemyPieceCount,whiteKingLocation,blackKingLocation);
        eval += kingSafetyScores(whiteKingLocation,blackKingLocation,pos.board, board.getWhitePieceCount() + board.getBlackPieceCount());
        eval += mobilityScore(pos.board);
        int perspectiveFlip = (pos.isWhiteTurn ? 1 : -1);
        return eval * perspectiveFlip;
        // todo alot here
    }


    private static int kingDistanceScore(int enemyPieceCount,XYcoord whiteKingLocation,XYcoord blackKingLocation){
        float scoreWeight = (ChessConstants.ONESIDEPIECECOUNT - enemyPieceCount)/(float)ChessConstants.ONESIDEPIECECOUNT;
        int dx = Math.abs(whiteKingLocation.x-blackKingLocation.x);
        int dy = Math.abs(whiteKingLocation.y-blackKingLocation.y);
        // euclidian
        double dist = Math.sqrt(dx*dx+dy*dy);
        final float maxDist = 8*1.414f; // root 2
        int score =(int)((maxDist-dist)*12*scoreWeight);
        return score;
    }
    final static int squareValue = 2;

    private static int kingSafetyScores(XYcoord whiteKingLocation,XYcoord blackKingLocation,BitBoardWrapper bitBoardWrapper,int numPieces){


        float weight = (float) numPieces /ChessConstants.BOTHSIDEPIECECOUNT;
        long whiteKingMoveMap = BitFunctions.calculateQueenAtackBitboard(GeneralChessFunctions.positionToBitIndex(whiteKingLocation.x,whiteKingLocation.y),true,false,bitBoardWrapper);
        long blackKingMoveMap = BitFunctions.calculateQueenAtackBitboard(GeneralChessFunctions.positionToBitIndex(blackKingLocation.x,blackKingLocation.y),false,false,bitBoardWrapper);
        int whiteScore = (32-Long.bitCount(whiteKingMoveMap));
        int blackScore = (32-Long.bitCount(blackKingMoveMap));

        return (int)((whiteScore-blackScore)*weight);


    }

    private static int mobilityScore(BitBoardWrapper bitBoardWrapper){
        int whiteValue = Long.bitCount(bitBoardWrapper.getWhiteAttackTableCombined() & ~bitBoardWrapper.getWhitePiecesBB()[ChessConstants.KINGINDEX])*squareValue;
        int blackValue = Long.bitCount(bitBoardWrapper.getBlackAttackTableCombined() & ~bitBoardWrapper.getBlackPiecesBB()[ChessConstants.KINGINDEX])*squareValue;
        return whiteValue-blackValue;
    }
}

