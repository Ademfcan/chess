package chessengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Computer{



    private int evalDepth;

    private HashMap<Integer,Double> transTable;
    private int repetitionCount;
    private ChessMove lastMove;

    public volatile boolean stop;

    private boolean running = false;

    public boolean isRunning(){
        return running;
    }

    private Logger logger;
    public Computer(int evalDepth){
        transTable = new HashMap<Integer,Double>();
        logger = LogManager.getLogger(this.toString());
        reset();
        this.evalDepth = evalDepth;
    }

    public void reset(){
        repetitionCount = 0;
        lastMove = null;
    }

    private void clearFlags(){
        stop = false;
        running = false;

    }

    public void setEvalDepth(int evalDepth) {
        this.evalDepth = evalDepth;
    }

    public List<ComputerOutput> getNMoves(boolean isWhite, ChessPosition pos,ChessStates gameState,int nMoves){
        List<ComputerOutput> BestMoves = new ArrayList<>(nMoves);
        HashSet<ChessMove> prevMoves = new HashSet<>(nMoves);
        running = true;
        for(int i = 0;i<nMoves;i++){
            ComputerOutput out = getComputerMoveWithCondiditon(isWhite,pos,gameState,prevMoves,true);
            if(out.equals(ChessConstants.emptyOutput)){
                clearFlags();
                return null;
            }
            BestMoves.add(out);
            prevMoves.add(out.move);
        }
        clearFlags();
        return BestMoves;
    }

    public ChessMove getComputerMove(boolean isWhite, ChessPosition pos,ChessStates gameState){
        return getComputerMoveWithCondiditon(isWhite,pos,gameState,null,false).move;

    }

    private ComputerOutput getComputerMoveWithCondiditon(boolean isWhite, ChessPosition pos, ChessStates gameState, HashSet<ChessMove> alreadyPlayedMoves, boolean isHashCheck){
        logger.debug("Transtable size: " + transTable.size());
        ChessPosition bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;
        running = true;
        List<BackendChessPosition> filteredPositions;
        // this is how i avoid duplicate best moves
        if(isHashCheck){
            filteredPositions = pos.getAllChildPositions(isWhite,gameState).stream().filter(p->!alreadyPlayedMoves.contains(p.getMoveThatCreatedThis())).toList();
        }
        else{
            filteredPositions = pos.getAllChildPositions(isWhite,gameState);
        }
        for(BackendChessPosition childPos : filteredPositions){
            if(stop){
                clearFlags();
                System.out.println("Stopped nmoves1");
                return ChessConstants.emptyOutput;
            }
            double miniMaxEval = miniMax(childPos, childPos.gameState,evalDepth-1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,!isWhite);
            if(miniMaxEval == Stopped){
                System.out.println("Stopped nmoves2");
                clearFlags();
                return ChessConstants.emptyOutput;
            }
            if(isWhite){
                if(miniMaxEval > bestEval){
                    //logger.debug("Changing to better move with eval of: " + miniMaxEval);
                    bestEval = miniMaxEval;
                    bestMove = childPos;
                }
            }
            else{

                if(miniMaxEval < bestEval){
                    //logger.debug("Changing to better move with eval of: " + miniMaxEval);
                    bestEval = miniMaxEval;
                    bestMove = childPos;
                }
            }


        }
        if(bestMove == null){
            clearFlags();
            ChessConstants.mainLogger.error("null bestmovesoutput");
            return ChessConstants.emptyOutput;
        }
        lastMove = bestMove.getMoveThatCreatedThis();
        clearFlags();
        return new ComputerOutput(lastMove,bestEval);
    }




    public static final double Stopped = -999999;

    //alpha default -inf beta default +inf
    public double miniMax(BackendChessPosition position,ChessStates gameState, int depth, double alpha, double beta, boolean isWhiteTurn){
        // all recursive stop cases
        if(stop){
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }
        int key = position.board.hashCode() + (isWhiteTurn ? 1000 : -1000);
        if(AdvancedChessFunctions.isAnyNotMovePossible(true,position.board,gameState)){
            // possiblity of a checkmate, else draw
            if(AdvancedChessFunctions.isChecked(true,position.board)){
                return ChessConstants.BLACKCHECKMATEVALUE;
            }
            return 0;
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(false,position.board,gameState)){
            // possiblity of a checkmate, else draw
            if(AdvancedChessFunctions.isChecked(false,position.board)){
                return ChessConstants.WHITECHECKMATEVALUE;
            }
            return 0;
        }

        if(depth == 0){
            return getFullEval(position.board,gameState,isWhiteTurn,true);
        }
        if(transTable.containsKey(key)){
//            logger.info("Transtable value being used");
            return transTable.get(key);
        }



        if(isWhiteTurn){
            double maxEval = Double.NEGATIVE_INFINITY;
            List<BackendChessPosition> childPos = position.getAllChildPositions(true,gameState);
            for(BackendChessPosition c : childPos){
                double eval = miniMax(c,c.gameState, depth - 1, alpha, beta, false);
                if(eval == Stopped){
                    return Stopped;
                }
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
            List<BackendChessPosition> childPos = position.getAllChildPositions(false,gameState);
            for(BackendChessPosition c : childPos){
                double eval = miniMax(c,c.gameState, depth - 1, alpha, beta, true);
                if(eval == Stopped){
                    return Stopped;
                }
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



    int[] valueMap = {1,3,3,5,9,10000000};

    public double getFullEvalMinimax(ChessPosition pos,ChessStates gameState, int depth, boolean isWhite){
        double bestEval = isWhite ? Double.MIN_VALUE : Double.MAX_VALUE;
        running = true;
        if(AdvancedChessFunctions.isAnyNotMovePossible(true,pos.board,gameState)){
            // possiblity of a checkmate, else draw
            clearFlags();
            if(AdvancedChessFunctions.isChecked(true,pos.board)){
                return ChessConstants.BLACKCHECKMATEVALUE;
            }
            return 0;
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(false,pos.board,gameState)){
            // possiblity of a checkmate, else draw
            clearFlags();
            if(AdvancedChessFunctions.isChecked(false,pos.board)){
                return ChessConstants.WHITECHECKMATEVALUE;
            }
            return 0;
        }
        for(BackendChessPosition c : pos.getAllChildPositions(isWhite,gameState)){
            if(stop){
                clearFlags();
                return Stopped;
            }
            double eval =  miniMax(c,c.gameState,depth-1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,!isWhite);
            if(eval == Stopped){
                clearFlags();
                return Stopped;
            }
            if(isWhite){
                bestEval = Math.max(eval,bestEval);
            }
            else{
                bestEval = Math.min(eval,bestEval);
            }
        }
        clearFlags();
        return bestEval;
    }

    public double getFullEval(BitBoardWrapper board,ChessStates gameState, boolean isWhiteTurn,boolean isCheckmateKnown){
        // todo: test against known positions
        long[] whiteP = board.getWhitePieces();
        long[] blackP = board.getBlackPieces();
        int whitePieceCount = getPieceCount(whiteP);
        int blackPieceCount = getPieceCount(blackP);
        double[][][] currentMap = pieceMapHandler.getMap(whitePieceCount+blackPieceCount);

        if(!isCheckmateKnown){
            if(AdvancedChessFunctions.isCheckmated(false,board,gameState)){
                return ChessConstants.WHITECHECKMATEVALUE;

            }
            else if(AdvancedChessFunctions.isCheckmated(true,board,gameState)){
                return -ChessConstants.BLACKCHECKMATEVALUE;
            }

        }

        XYcoord king1 = null;// = board.getWhiteKingLocation();
        XYcoord king2 = null;// = board.getBlackKingLocation();

        double sum1 = 0;
        for(int i = 0; i< whiteP.length; i++){
            List<XYcoord> coords = getPieceCoords(whiteP[i]);
            if(i == 5){
               king1 = coords.get(0);
            }
            for(XYcoord s : coords){

                sum1 += valueMap[i] + currentMap[i][s.x][s.y];
                if(i == 0){
                    float extraPawnPushValue = (float) (16 - whitePieceCount) /16;
                    sum1 += extraPawnPushValue;
                }
                if(i < 1 && i<5){
                    if(i == 4){
                        sum1 += addOpenFileValue(s.x,s.y,3,board);
                        sum1 += addOpenFileValue(s.x,s.y,2,board);
                    }
                    sum1 += addOpenFileValue(s.x,s.y,i,board);
                }




            }
        }
        double sum2 = 0;
        for(int i = 0; i< blackP.length; i++){
            List<XYcoord> coords = getPieceCoords(blackP[i]);
            for(XYcoord s : coords){
                if(i == 5){
                    king2 = coords.get(0);
                }
                if(i == 0){
                    float extraPawnPushValue = (float) (16 - blackPieceCount) /16;
                    sum2 += extraPawnPushValue;
                }
                // reverse coordinates to match white peices

                int Normx = s.x;
                int Normy = 7-s.y;
                sum2 += valueMap[i] + currentMap[i][Normx][Normy];
                if(i < 1 && i < 5){
                    if(i == 4){
                        sum2 += addOpenFileValue(s.x,s.y,3,board);
                        sum2 += addOpenFileValue(s.x,s.y,2,board);
                    }
                    sum2 += addOpenFileValue(s.x,s.y,i,board);
                }



            }

        }
        double total = sum1-sum2;
        total += kingDistanceAncCornerEval(king1,king2,isWhiteTurn ? whitePieceCount : blackPieceCount,isWhiteTurn) * (isWhiteTurn ? 1 : -1);
        return total;

    }

    private double kingDistanceAncCornerEval(XYcoord king1, XYcoord king2, int piecesOnBoard,boolean isWhite){
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





    private List<XYcoord> getPieceCoords(long board) {
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

    private int getPieceCount(long[] pieces){
        int totalCount = 0;
        for(long piece : pieces){
            totalCount += getPieceCount(piece);
        }
        return totalCount;

    }

    private int getPieceCount(long board) {
        int count = 0;
        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                count++;
            }
        }

        return count;
    }
    // bishop then rook
    int[] bishopDx = {1,-1,-1,1};
    int[] bishopDy = {1,-1,1,-1};
    int[] rookDx = {1,-1,0,0};
    int[] rookDy = {0,0,-1,1};

    private double squareWorth = 0.05d;

    private double addOpenFileValue(int x, int y, int piecetype, BitBoardWrapper board){
        double totalValue = 0d;
        int[] dxs = piecetype == 2 ? bishopDx : rookDx;
        int[] dys = piecetype == 2 ? bishopDy : rookDy;
        for(int i = 0; i<4;i++){
            int dx = dxs[i] +x;
            int dy = dys[i] +y;
            while(GeneralChessFunctions.isValidMove(dx,dy)){
                if(GeneralChessFunctions.checkIfContains(dx,dy,board)[0]){
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