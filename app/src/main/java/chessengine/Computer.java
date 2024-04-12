package chessengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Computer{
    pieceLocationHandler gameHandler;
    public int evalDepth = 7;

    private HashMap<Integer,Double> transTable;
    private int repetitionCount;
    private chessMove lastMove;

    public volatile boolean stop;
    public Computer(pieceLocationHandler gameHandler){
        transTable = new HashMap<Integer,Double>();
        this.gameHandler = gameHandler;
        reset();
    }

    public void reset(){
        repetitionCount = 0;
        lastMove = null;
    }

    public chessMove getComputerMove(boolean isWhite, long[] whitePeices,long[] blackPieces){
        System.out.println("Transtable size: " + transTable.size());
        chessPosition currentPos = new chessPosition(whitePeices,blackPieces);
        chessPosition bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;

        for(chessPosition childPos : currentPos.getAllChildPositions(gameHandler,isWhite)){
            //System.out.println(childPos);
            if(childPos.moveThatCreatedThis.equals(lastMove)){
                repetitionCount++;
                if(repetitionCount > 1){
                    // at 2 repetitions now, means that this move would lead to a third repetition so break
                    repetitionCount = 0;
                    break;
                }
            }

            double miniMaxEval = miniMax(childPos,evalDepth-1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,!isWhite);

            if(isWhite){
                if(miniMaxEval > bestEval){
                    //System.out.println("Changing to better move with eval of: " + miniMaxEval);
                    bestEval = miniMaxEval;
                    bestMove = childPos;
                }
            }
            else{

                if(miniMaxEval < bestEval){
                    //System.out.println("Changing to better move with eval of: " + miniMaxEval);
                    bestEval = miniMaxEval;
                    bestMove = childPos;
                }
            }
        }
        lastMove = bestMove.moveThatCreatedThis;
        return bestMove.moveThatCreatedThis;

    }
    public final double Stopped = -999999;
    //alpha default -inf beta default +inf
    public double miniMax(chessPosition position, int depth, double alpha, double beta, boolean isWhite){
        if(stop){
            System.out.println("Stopping");
            stop = false;
            return Stopped;
        }
        int key = position.hashCode() + (isWhite ? 1000 : -1000);

        if(depth == 0 || gameHandler.isCheckmated(position.getWhitePeices(),position.getBlackPeices())){
            double eval = getFullEval(position.getWhitePeices(),position.getBlackPeices());
            return eval;
        }
        if(transTable.containsKey(key)){
            return transTable.get(key);
        }


        if(isWhite){
            double maxEval = Double.NEGATIVE_INFINITY;
            List<chessPosition> childPos = position.getAllChildPositions(gameHandler, true);
            for(chessPosition c : childPos){
                double eval = miniMax(c, depth - 1, alpha, beta, false);

                maxEval = Math.max(eval, maxEval);
                alpha = Math.max(alpha, eval);  // Update alpha after the recursive call
                if(beta <= alpha){
                    break;
                }
            }
            transTable.put(key,maxEval);
            return maxEval;
        }
        else{
            double minEval = Double.POSITIVE_INFINITY;
            List<chessPosition> childPos = position.getAllChildPositions(gameHandler, false);
            for(chessPosition c : childPos){
                double eval = miniMax(c, depth - 1, alpha, beta, true);
                minEval = Math.min(eval, minEval);
                beta = Math.min(beta, eval);  // Update beta after the recursive call
                if(beta <= alpha){
                    break;
                }
            }
            transTable.put(key,minEval);
            return minEval;
        }



    }


    private final double[][] pawnMap = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private final double[][] knightMap = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private final double[][] bishopMap = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private final double[][] rookMap = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private final double[][] kingMap = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private final double[][] queenMap = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };


    double[][][] maps = {pawnMap, knightMap, bishopMap, rookMap, queenMap, kingMap};
    int[] valueMap = {1,3,3,5,9,10000000};

    public double getFullEvalMinimax(long[] whiteP,long[] blackP, int depth,boolean isWhite){
        chessPosition currentPos = new chessPosition(whiteP,blackP);
        double bestEval = isWhite ? Double.MIN_VALUE : Double.MAX_VALUE;
        for(chessPosition p : currentPos.getAllChildPositions(gameHandler,isWhite)){
            double eval =  miniMax(p,depth-1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,!isWhite);
            if(isWhite){
                bestEval = Math.max(eval,bestEval);
            }
            else{
                bestEval = Math.min(eval,bestEval);
            }
        }
        return bestEval;
    }

    public double getFullEval(long[] whitep, long[] blackP){
        // todo: test against known positions
        if(gameHandler.isCheckmated(blackP,whitep)){
            if(gameHandler.isCheckmated(false,blackP,whitep)){
                return -1000000;
            }
            else{
                return 1000000;
            }
        }


        double sum1 = 0;
        for(int i = 0; i< whitep.length; i++){
            List<XYcoord> coords = getPieceCoords(whitep[i]);
            for(XYcoord s : coords){

                sum1 += valueMap[i] + maps[i][s.x][s.y];
                if(i == 0){
                    float extraPawnPushValue = (float) (16 - coords.size()) /16;
                    sum1 += extraPawnPushValue;
                }
                if(i < 1 && i < 5){
                    if(i == 4){
                        sum1 += addOpenFileValue(s.x,s.y,3,whitep,blackP);
                        sum1 += addOpenFileValue(s.x,s.y,2,whitep,blackP);
                    }
                    sum1 += addOpenFileValue(s.x,s.y,i,whitep,blackP);
                }




            }
        }
        double sum2 = 0;
        for(int i = 0; i< blackP.length; i++){
            List<XYcoord> coords = getPieceCoords(blackP[i]);
            for(XYcoord s : coords){

                if(i == 0){
                    float extraPawnPushValue = (float) (16 - coords.size()) /16;
                    sum2 += extraPawnPushValue;
                }
                // reverse coordinates to match white peices

                int Normx = s.x;
                int Normy = 7-s.y;
                sum2 += valueMap[i] + maps[i][Normx][Normy];
                if(i < 1 && i < 5){
                    if(i == 4){
                        sum2 += addOpenFileValue(s.x,s.y,3,whitep,blackP);
                        sum2 += addOpenFileValue(s.x,s.y,2,whitep,blackP);
                    }
                    sum2 += addOpenFileValue(s.x,s.y,i,whitep,blackP);
                }



            }

        }

        return sum1 - sum2;

    }



    private List<XYcoord> getPieceCoords(long board) {
        List<XYcoord> coord = new ArrayList<>();
        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                int[] coords = chessFunctions.bitindexToXY(z);
                coord.add(new XYcoord(coords[0],coords[1]));
            }
        }

        return coord;
    }
    // bishop then rook
    int[] bishopDx = {1,-1,-1,1};
    int[] bishopDy = {1,-1,1,-1};
    int[] rookDx = {1,-1,0,0};
    int[] rookDy = {0,0,-1,1};

    private double squareWorth = 0.05d;

    private double addOpenFileValue(int x, int y, int piecetype, long[] whitePeices, long[] blackPieces){
        double totalValue = 0d;
        int[] dxs = piecetype == 2 ? bishopDx : rookDx;
        int[] dys = piecetype == 2 ? bishopDy : rookDy;
        for(int i = 0; i<4;i++){
            int dx = dxs[i] +x;
            int dy = dys[i] +y;
            while(chessFunctions.isValidMove(dx,dy)){
                if(chessFunctions.checkIfContains(dx,dy,whitePeices,blackPieces)[0]){
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