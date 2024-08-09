package chessengine;

import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Computer{



    protected int evalDepth;

    protected Map<Long,Double> transTable;

    public volatile AtomicBoolean stop;

    private boolean running = false;

    public boolean isRunning(){
        return running;
    }

    private Logger logger;

    private final int maxEntries = 800000;

    protected final ZobristHasher hasher;
    public Computer(int evalDepth){
        this.transTable = Collections.synchronizedMap(new LimitedSizeMap<>(maxEntries));
        this.hasher = new ZobristHasher();
        logger = LogManager.getLogger(this.toString());
        stop = new AtomicBoolean(false);
        this.evalDepth = evalDepth;
    }



    private void clearFlags(){
        logger.debug("Clearing flags");
        stop.set(false);
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
            if(!out.equals(ChessConstants.emptyOutput)){
                BestMoves.add(out);
                prevMoves.add(out.move);
            }

        }
        clearFlags();
        return BestMoves;
    }

    public ChessMove getComputerMove(boolean isWhite, ChessPosition pos,ChessStates gameState){
        return getComputerMoveWithCondiditon(isWhite,pos,gameState,null,false).move;

    }
    private final Random randomComp = new Random();
    private ComputerOutput getRandMove(int start,List<ComputerOutput> moves){
        int end = moves.size();
        if(start<end){
            int randIndx = randomComp.nextInt(start,end);
            return moves.get(randIndx);
        }
        else{
            ChessConstants.mainLogger.error("Start cannot be greater than end!! (Get rand move)");
            return null;
        }
    }


    private ComputerOutput getComputerMoveWithCondiditon(boolean isWhite, ChessPosition pos, ChessStates gameState, HashSet<ChessMove> alreadyPlayedMoves, boolean isHashCheck) {
        setCallTimeEval(pos, gameState, isWhite);

        int evalDepthCalc = evalDepth;
        System.out.println("edc" + evalDepthCalc);
        logger.debug("Transtable size: " + transTable.size());

        ChessMove bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;
        double bestDepth = 1000;
        running = true;

        List<BackendChessPosition> filteredPositions;
        if (isHashCheck) {
            filteredPositions = pos.getAllChildPositions(isWhite, gameState).stream().filter(p -> !alreadyPlayedMoves.contains(p.getMoveThatCreatedThis())).toList();
        } else {
            filteredPositions = pos.getAllChildPositions(isWhite, gameState);
        }
        cnt= 0;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-2);
//        ExecutorService executor = Executors.newFixedThreadPool(1);
        List<Future<MinimaxResult>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    return null;
                }
                MinimaxOutput miniMaxOut = miniMax(childPos, childPos.gameState, evalDepthCalc - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite);
                if (miniMaxOut == Stopped) {
                    return null;
                }

                return new MinimaxResult(move,miniMaxOut.getAdvantage(),miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
//        executor.awaitTermination()
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to complete
            for (Future<MinimaxResult> future : futures) {
                MinimaxResult result = future.get();
                if (result == null) {
                    continue;
                }

                double advtg = result.getAdvantage();
                ChessMove childMove = result.getMove();
                double depth = result.getDepth();

                if (isWhite) {
                    if (advtg > bestEval) {
                        bestEval = advtg;
                        bestMove = childMove;
                        bestDepth = depth;
                    } else if (advtg == bestEval && depth < bestDepth) {
                        bestMove = childMove;
                        bestDepth = depth;
                    }
                } else {
                    if (advtg < bestEval) {
                        bestEval = advtg;
                        bestMove = childMove;
                        bestDepth = depth;
                    } else if (advtg == bestEval && depth < bestDepth) {
                        bestMove = childMove;
                        bestDepth = depth;
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (bestMove == null) {

            ChessConstants.mainLogger.error("null bestmovesoutput");
            return ChessConstants.emptyOutput;
        }

        clearFlags();
        return new ComputerOutput(bestMove, bestEval);
    }






    public static final MinimaxOutput Stopped = new MinimaxOutput(Double.NaN);

    protected final int depthThreshold = 4;

    protected final double advtgThreshold = 1.5d;

    protected double callTimeEval;

    private void setCallTimeEval(ChessPosition posAtCallTime,ChessStates gameStateAtCallTime,boolean whiteTurnAtCallTime){
        callTimeEval = getFullEval(posAtCallTime,gameStateAtCallTime,whiteTurnAtCallTime,true);

    }

    private int cnt = 0;

    protected final double drawConst = 0;

    //alpha default -inf beta default +inf
    protected MinimaxOutput miniMax(BackendChessPosition position, ChessStates gameState, int depth, double alpha, double beta, boolean isWhiteTurn){
        // all recursive stop cases
//        System.out.println(cnt++);
        if(stop.get()){
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }
//        int key = Objects.hash(gameState.hashCode(),position.hashCode(),isWhiteTurn);
        long key = hasher.computeHash(position,isWhiteTurn);
        if(position.isDraw()){
            return new MinimaxOutput(isWhiteTurn ? drawConst : -drawConst);
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(isWhiteTurn,position,gameState)){
            // possiblity of a black winning from checkmate, else draw
            if(AdvancedChessFunctions.isChecked(isWhiteTurn,position.board)){
                return new MinimaxOutput(isWhiteTurn ? ChessConstants.BLACKCHECKMATEVALUE : ChessConstants.WHITECHECKMATEVALUE);
            }
            return new MinimaxOutput(isWhiteTurn ? drawConst : -drawConst);
        }
//

        if(depth == 0){
            return new MinimaxOutput(getFullEval(position,gameState,isWhiteTurn,true));
        }
        // todo fix transtable erratic behaviour
//        if(transTable.containsKey(key)){
////            logger.info("Transtable value being used");
//            return new MinimaxOutput(transTable.get(key));
//        }

        if(depth <= evalDepth-depthThreshold){
            // do a check to see if there is any noticeable advantage diff.  If not then return
            double posEval = getFullEval(position,gameState,isWhiteTurn,true);
            double diff = posEval-callTimeEval;
            double advtgThresholdCalc = advtgThreshold + (double) (evalDepth-depthThreshold - depth) /8;
            if(isWhiteTurn){
                // only stay if diff greater than advtgThreshold
                if(diff < advtgThresholdCalc){
                    // not worth it to go deeper
//                    System.out.println("Failed thresh: " + depth);
                    return new MinimaxOutput(posEval);
                }
                // else go deeper ;)

            }
            else{
                // only stay if diff less than advtgThreshold
                if(diff > -advtgThresholdCalc){
                    // not worth it to go deeper
//                    System.out.println("failed thresh: "  + depth);
                    return new MinimaxOutput(posEval);
                }
                // else go deeper ;)
            }

        }

        // once again  check stop flag
        if(stop.get()){
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }
        // recursive part
        if(isWhiteTurn){
            MinimaxOutput maxEval = new MinimaxOutput(Double.NEGATIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(true,gameState);
            List<BackendChessPosition> childPositions = position.getAllChildPositions(true,gameState);
            assertTrue(childMoves.size(),childPositions.size(),true);
            for(int i = 0;i<childMoves.size();i++){
                ChessMove c = childMoves.get(i);
                String og = GeneralChessFunctions.getBoardDetailedString(position.board);
                String ogMove = position.getMoveThatCreatedThis().toString();
                position.makeLocalPositionMove(c);
                if(!assertTrue(position,childPositions.get(i),true,og + "\n" + ogMove)){
                    System.out.println("whywhywhywhywhywhywhy");
                    return Stopped;


                }
                assertTrue(position.getMoveThatCreatedThis(),childPositions.get(i).getMoveThatCreatedThis(),true);
//              assertTrue(position.gameState.toString(),childPositions.get(i).gameState);
                MinimaxOutput out = miniMax(position,position.gameState, depth - 1, alpha, beta, false);
                position.undoLocalPositionMove();
                if(out == Stopped){
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    System.out.println(c.toString());
                    String outStr = GeneralChessFunctions.getBoardDetailedString(position.board);
                    System.out.println(outStr);
                    return Stopped;
                }
                maxEval = max(out, maxEval);
                alpha = Math.max(alpha, out.getAdvantage());  // Update alpha after the recursive call
                if(beta <= alpha){
                    break;
                }
            }
            transTable.put(key,maxEval.getAdvantage());
            return maxEval.incrementAndReturn();
        }
        else{
            MinimaxOutput minEval = new MinimaxOutput(Double.POSITIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(false,gameState);
            List<BackendChessPosition> childPositions = position.getAllChildPositions(false,gameState);
            assertTrue(childMoves.size(),childPositions.size(),true);
            for(int i = 0;i<childMoves.size();i++){
                ChessMove c = childMoves.get(i);
                String og = GeneralChessFunctions.getBoardDetailedString(position.board);
                String ogMove = position.getMoveThatCreatedThis().toString();
                position.makeLocalPositionMove(c);
                if(!assertTrue(position,childPositions.get(i),false,og + "\n" + ogMove)){
                    System.out.println(c.toString());
                    System.out.println("whywhywhywhywhywhywhy");
                    return Stopped;
                }

                assertTrue(position.getMoveThatCreatedThis(),childPositions.get(i).getMoveThatCreatedThis(),true);
//                assertTrue(position.gameState.toString(),childPositions.get(i).gameState);
                MinimaxOutput out = miniMax(position,position.gameState, depth - 1, alpha, beta, true);
                position.undoLocalPositionMove();
                if(out == Stopped){
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:  " + depth);
                    String outStr = GeneralChessFunctions.getBoardDetailedString(position.board);
                    System.out.println(outStr);
                    return Stopped;

                }

                minEval = min(out,minEval);
                beta = Math.min(beta, out.getAdvantage());  // Update beta after the recursive call
                if(beta <= alpha){
                    break;
                }

            }
            transTable.put(key,minEval.getAdvantage());
            return minEval.incrementAndReturn();
        }



    }

    private <T> void  assertTrue(T a, T b,boolean isSerious) {
        if(isSerious && !a.equals(b)){
            System.out.println("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR!!!!");
            System.out.println("a\n" + a);
            System.out.println("b\n" +b);
        }
        
    }

    private boolean assertTrue(ChessPosition a, ChessPosition b,boolean isWhite,String og) {
        String aboard = GeneralChessFunctions.getBoardDetailedString(a.board);
        String bboard = GeneralChessFunctions.getBoardDetailedString(b.board);
        if(!aboard.equals(bboard) && !a.getMoveThatCreatedThis().isCastleMove()) {
            System.out.println("OG:\n" + og);
            System.out.println("IsWhite: " + isWhite);
            System.out.println("amove\n" + a.getMoveThatCreatedThis().toString());
            System.out.println("a\n" + aboard);
            System.out.println("bmove\n" + b.getMoveThatCreatedThis().toString());
            System.out.println("b\n" + bboard);
            System.out.println("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR!!!!");
            return false;
        }
        return true;

    }

    protected MinimaxOutput min(MinimaxOutput m1, MinimaxOutput m2){
        if(m1.getAdvantage() == m2.getAdvantage()){
            // always return the one with less depth if equal
            if(m1.getOutputDepth() < m2.getOutputDepth()){
                return m1;
            }
            return m2;
        }
        if(m1.getAdvantage() < m2.getAdvantage()){
            return m1;
        }
        return m2;

    }

    protected MinimaxOutput max(MinimaxOutput m1, MinimaxOutput m2){
        if(m1.getAdvantage() == m2.getAdvantage()){
            // always return the one with less depth if equal
            if(m1.getOutputDepth() < m2.getOutputDepth()){
                return m1;
            }
            return m2;
        }

        if(m1.getAdvantage() > m2.getAdvantage()){
            return m1;
        }
        else{
            return m2;
        }
    }



    int[] valueMap = {1,3,3,5,9,10000000};

    public MinimaxOutput getFullEvalMinimax(ChessPosition pos, ChessStates gameState, int depth, boolean isWhite){
        setCallTimeEval(pos,gameState,isWhite);
        logger.debug(transTable.size());
        MinimaxOutput bestEval = isWhite ? new MinimaxOutput(Double.MIN_VALUE) : new MinimaxOutput(Double.MAX_VALUE);
        running = true;
        if(AdvancedChessFunctions.isAnyNotMovePossible(true,pos,gameState)){
            // possiblity of a checkmate, else draw
            clearFlags();
            if(AdvancedChessFunctions.isChecked(true,pos.board)){
                return new MinimaxOutput(ChessConstants.BLACKCHECKMATEVALUE);
            }
            return new MinimaxOutput(0);
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(false,pos,gameState)){
            // possiblity of a checkmate, else draw
            clearFlags();
            if(AdvancedChessFunctions.isChecked(false,pos.board)){
                return new MinimaxOutput(ChessConstants.WHITECHECKMATEVALUE);
            }
            return new MinimaxOutput(0);
        }
        for(BackendChessPosition c : pos.getAllChildPositions(isWhite,gameState)){
            if(stop.get()){
                clearFlags();
                return Stopped;
            }
            MinimaxOutput eval = miniMax(c,c.gameState,depth-1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,!isWhite);

            if(eval == Stopped){
                clearFlags();
                return Stopped;
            }

            if(isWhite){
                bestEval = max(eval,bestEval);
            }
            else{
                bestEval = min(eval,bestEval);
            }
        }
        clearFlags();
        return bestEval;
    }

    public double getFullEval(ChessPosition pos,ChessStates gameState, boolean isWhiteTurn,boolean isCheckmateKnown){
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

                sum1 += valueMap[i] + currentMap[i][s.x][s.y];
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
                sum2 += valueMap[i] + currentMap[i][Normx][Normy];
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

    private int getPieceCount(BitBoardWrapper board){
        int totalCount = 0;
        for(long piece : board.getWhitePieces()){
            totalCount += getPieceCount(piece);
        }
        for(long piece : board.getBlackPieces()){
            totalCount += getPieceCount(piece);
        }
        return totalCount;

    }

    private int getPieceCount(long[] pieces){
        int totalCount = 0;
        for(long piece : pieces){
            totalCount += getPieceCount(piece);
        }
        return totalCount;

    }

    private int getPieceCount(long board) {
        return Long.bitCount(board);
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