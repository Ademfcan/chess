package chessengine.Functions;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.BitBoardWrapper;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.XYcoord;
import chessengine.Misc.ChessConstants;
import chessengine.Misc.pieceMapHandler;

public class EvaluationFunctions {
    public static  final int baseMateScore = -1000000;
    public static  final int maxDepthPossible = 256;

    public static boolean isMateScore(int currentScore){
        return Math.abs(currentScore) >= -(baseMateScore);
    }

    public static  int extractDepthFromMateScore(int mateScore){
        return (Math.abs(mateScore)+baseMateScore);
    }


    public static int getStaticEvaluation(BackendChessPosition pos){
        int[][][] pieceMap = pieceMapHandler.pieceMapsCp1;
        BitBoardWrapper board = pos.board;
        long[] whitePiecesBB = board.getWhitePiecesBB();
        long[] blackPiecesBB = board.getBlackPiecesBB();
        int whiteScore = 0;
        int blackScore = 0;
        for(int i = 0;i<whitePiecesBB.length;i++){
            XYcoord[] coords = GeneralChessFunctions.getPieceCoordsArray(whitePiecesBB[i]);
            for(XYcoord coord : coords){
                int flippedX = 7-coord.x;
                int flippedY = 7-coord.y;
                whiteScore += pieceMap[i][flippedX][flippedY];
                // todo
            }
            whiteScore += ChessConstants.valueMapCentiPawn[i]*coords.length;
        }

        for(int i = 0;i<blackPiecesBB.length;i++){
            XYcoord[] coords = GeneralChessFunctions.getPieceCoordsArray(blackPiecesBB[i]);
            for(XYcoord coord : coords){
                // todo
                blackScore += pieceMap[i][coord.x][coord.y];
            }
            blackScore += ChessConstants.valueMapCentiPawn[i]*coords.length;
        }
        int enemyPieceCount = pos.isWhiteTurn ? pos.board.getBlackPieceCount() : pos.board.getWhitePieceCount();
        XYcoord whiteKingLocation = pos.board.getWhiteKingLocation();
        XYcoord blackKingLocation = pos.board.getBlackKingLocation();
        int eval =  (whiteScore - blackScore) + kingDistanceScore(enemyPieceCount,whiteKingLocation,blackKingLocation);
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
        int score =(int)((maxDist-dist)*10*scoreWeight);
        return score;
    }
}

