package chessengine;

import java.util.ArrayList;
import java.util.List;

public class ComputerHelperFunctions {

    public static boolean doesMoveFitRestrictions(BackendChessPosition newPos, ComputerDifficulty diff){
        ChessMove moveThatCreated = newPos.getMoveThatCreatedThis();

        double agressiveness = 0;
        double defensiveness = 0;
        double risk =  0;

        Boolean isChecked = null;
        if(AdvancedChessFunctions.isAnyNotMovePossible(!moveThatCreated.isWhite(),newPos,newPos.gameState)){
            // checkmate
            if(AdvancedChessFunctions.isChecked(!moveThatCreated.isWhite(),newPos.board)){
                isChecked = true;
                if(!diff.canWin){
                    // computer cannot checkmate
                    return false;
                }
            }
            // draw
            else{
                isChecked = false;
                if(diff.maxDefensiveness < .5){
                    return false;
                }
                defensiveness += .5;
            }
        }
        if(isChecked == null){
            isChecked = AdvancedChessFunctions.isChecked(!moveThatCreated.isWhite(),newPos.board);
        }

        agressiveness += isChecked ?  .5 : 0; // check value

        double moveForwardValue = (moveThatCreated.getNewX()-moveThatCreated.getOldX())*squareWorth;
        if(moveThatCreated.isWhite()){
            // moving forward means moveforward value should be negative
            agressiveness += Math.max(-moveForwardValue,0);
            defensiveness += Math.max(moveForwardValue,0);
        }
        else{
            agressiveness += Math.max(moveForwardValue,0);
            defensiveness += Math.max(-moveForwardValue,0);
        }
        if(moveThatCreated.isEating()){
            agressiveness += .3;
            double tradeValue = moveThatCreated.getEatingIndex()-moveThatCreated.getBoardIndex();

            risk += -Math.min(tradeValue,0);
        }
        risk += AdvancedChessFunctions.getNumAttackers(moveThatCreated.getNewX(),moveThatCreated.getNewY(),moveThatCreated.isWhite(),newPos.board)*squareWorth;

        defensiveness += moveThatCreated.isCastleMove() ? .2 : 0;
        agressiveness += moveThatCreated.isPawnPromo() ? .2 : 0;

        // clipping
        agressiveness = Math.min(agressiveness,1);
        defensiveness = Math.min(defensiveness,1);
        risk = Math.min(risk,1);

        return agressiveness <= diff.maxAgressiveness && defensiveness <= diff.maxDefensiveness && risk <= diff.maxRisk;

    }

    public static double getFullEval(ChessPosition pos,ChessStates gameState, boolean isWhiteTurn,boolean isCheckmateKnown){
        // todo: test against known positions
        long[] whiteP = pos.board.getWhitePieces();
        long[] blackP = pos.board.getBlackPieces();
        int whitePieceCount = getPieceCount(whiteP);
        int blackPieceCount = getPieceCount(blackP);
        double[][][] currentMap = pieceMapHandler.getMap(whitePieceCount+blackPieceCount);

        if(!isCheckmateKnown){
            if(AdvancedChessFunctions.isCheckmated(false,pos,gameState)){
                return ChessConstants.WHITECHECKMATEVALUE;

            }
            else if(AdvancedChessFunctions.isCheckmated(true,pos,gameState)){
                return -ChessConstants.BLACKCHECKMATEVALUE;
            }

        }

        XYcoord king1 = pos.board.getWhiteKingLocation();
        XYcoord king2 = pos.board.getBlackKingLocation();

        double sum1 = 0;
        double sum2 = 0;
        for(int i = 0; i< whiteP.length-1; i++){
            List<XYcoord> coordsW = getPieceCoords(whiteP[i]);
            for(XYcoord s : coordsW){

                sum1 += ChessConstants.valueMap[i] + currentMap[i][s.x][s.y];
                if(i == 0){
                    float extraPawnPushValue = (float) (16 - whitePieceCount) /16;
                    sum1 += extraPawnPushValue;
                }
                if(i > 1){
                    if(i == 4){
                        sum1 += addOpenFileValue(s.x,s.y,3,pos.board);
                        sum1 += addOpenFileValue(s.x,s.y,2,pos.board);
                    }
                    sum1 += addOpenFileValue(s.x,s.y,i,pos.board);
                }




            }
            List<XYcoord> coordsB = getPieceCoords(blackP[i]);
            for(XYcoord s : coordsB){
                if(i == 0){
                    float extraPawnPushValue = (float) (16 - blackPieceCount) /16;
                    sum2 += extraPawnPushValue;
                }
                // reverse coordinates to match white peices

                int Normx = s.x;
                int Normy = 7-s.y;
                sum2 += ChessConstants.valueMap[i] + currentMap[i][Normx][Normy];
                if(i > 1){
                    if(i == 4){
                        sum2 += addOpenFileValue(s.x,s.y,3,pos.board);
                        sum2 += addOpenFileValue(s.x,s.y,2,pos.board);
                    }
                    sum2 += addOpenFileValue(s.x,s.y,i,pos.board);
                }



            }
        }
        double total = sum1-sum2;
        total += kingDistanceAncCornerEval(king1,king2,isWhiteTurn ? whitePieceCount : blackPieceCount,isWhiteTurn) * (isWhiteTurn ? 1 : -1);
        return total;

    }

    private static double kingDistanceAncCornerEval(XYcoord king1, XYcoord king2, int piecesOnBoard,boolean isWhite){
        XYcoord enemyKingCoord = isWhite? king2 : king1;
        int xDistFromCenter = Math.abs(enemyKingCoord.x -3);
        int yDistFromCenter = Math.abs(enemyKingCoord.y -3);
        int distFromCenterValue = 6-(xDistFromCenter+yDistFromCenter);
        int xDist = Math.abs(king1.x-king2.x);
        int yDist = Math.abs(king1.y-king2.y);
        int distanceKingsvalue = 14-(xDist+yDist);
        double weight = 1-((double) piecesOnBoard /ChessConstants.ONESIDEPIECECOUNT);
        return (distanceKingsvalue + distFromCenterValue)*weight;
    }





    private static List<XYcoord> getPieceCoords(long board) {
        List<XYcoord> coord = new ArrayList<>();
        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                int[] coords = GeneralChessFunctions.bitindexToXY(z);
                coord.add(new XYcoord(coords[0],coords[1]));
            }
        }

        return coord;
    }

    private static int getPieceCount(long[] pieces){
        int totalCount = 0;
        for(long piece : pieces){
            totalCount += getPieceCount(piece);
        }
        return totalCount;

    }

    private static int getPieceCount(long board) {
        return Long.bitCount(board);
    }
    // bishop then rook
    private static int[] bishopDx = {1,-1,-1,1};
    private static int[] bishopDy = {1,-1,1,-1};
    private static int[] rookDx = {1,-1,0,0};
    private static int[] rookDy = {0,0,-1,1};

    private static double squareWorth = 0.05d;

    private static double addOpenFileValue(int x, int y, int piecetype, BitBoardWrapper board){
        double totalValue = 0d;
        int[] dxs = piecetype == 2 ? bishopDx : rookDx;
        int[] dys = piecetype == 2 ? bishopDy : rookDy;
        for(int i = 0; i<4;i++){
            int dx = dxs[i] +x;
            int dy = dys[i] +y;
            while(GeneralChessFunctions.isValidCoord(dx,dy)){
                if(GeneralChessFunctions.checkIfContains(dx,dy,board,"fileVals")[0]){
                    // hit peice
                    break;
                }
                dx += dxs[i];
                dy += dys[i];
                totalValue+= squareWorth;
            }
        }
        return totalValue;
    }
}
