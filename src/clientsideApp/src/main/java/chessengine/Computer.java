package chessengine;

import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Computer{




    protected Map<Long,Double> transTable;

    public volatile AtomicBoolean stop;

    private boolean running = false;
    public boolean isRunning(){
        return running;
    }


    private Logger logger;

    private final int maxEntries = 800000;

    protected final ZobristHasher hasher;
    public static final MinimaxOutput Stopped = new MinimaxOutput(Double.NaN);
    public Computer(int evalDepth){
        this.transTable = Collections.synchronizedMap(new LimitedSizeMap<>(maxEntries));
        this.hasher = new ZobristHasher();
        logger = LogManager.getLogger(this.toString());
        stop = new AtomicBoolean(false);
        this.evalDepth = evalDepth;
    }

    /// dynamically updated stuff

    protected int evalDepth;
    protected ComputerDifficulty currentDifficulty = ComputerDifficulty.MAXDIFFICULTY;

    protected double callTimeEval;


    private void setCallTimeEval(ChessPosition posAtCallTime,ChessStates gameStateAtCallTime,boolean whiteTurnAtCallTime){
        callTimeEval = ComputerHelperFunctions.getFullEval(posAtCallTime,gameStateAtCallTime,whiteTurnAtCallTime,true);

    }



    private void clearFlags(){
        logger.debug("Clearing flags");
        stop.set(false);
        running = false;

    }

    public void setEvalDepth(int evalDepth) {
        this.evalDepth = evalDepth;
    }

    public void setCurrentDifficulty(ComputerDifficulty currentDifficulty){
        this.currentDifficulty = currentDifficulty;
    }



    public ChessMove getComputerMove(boolean isWhite, ChessPosition pos,ChessStates gameState){
        setCallTimeEval(pos, gameState, isWhite);
        logger.debug("Transtable size: " + transTable.size());

        ChessMove bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;
        double bestDepth = 1000;
        running = true;


        List<BackendChessPosition> positions = pos.getAllChildPositions(isWhite, gameState);
        List<BackendChessPosition> filteredPositions = positions.stream().filter(p -> ComputerHelperFunctions.doesMoveFitRestrictions(p,currentDifficulty)).toList();
        if(filteredPositions.isEmpty()){
            filteredPositions = positions;
        }
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors()-2,filteredPositions.size());
        logger.debug("Thread count: " + threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<MinimaxResult>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    return null;
                }
                MinimaxOutput miniMaxOut = miniMax(childPos, currentDifficulty.depth- 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite);
                if (miniMaxOut == Stopped) {
                    return null;
                }

                return new MinimaxResult(move,miniMaxOut.getAdvantage(),miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
        try {
            boolean isOver = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to complete
            if(!isOver){
                logger.error("Could not wait for all threads to finish");
            }
            for (Future<MinimaxResult> future : futures) {
                MinimaxResult result = future.get();
                if (result == null) {
                    continue;
                }

                double advtg = result.getAdvantage();
                ChessMove childMove = result.getMove();

                // move priority
                if(currentDifficulty.favoritePieceIndex != ChessConstants.EMPTYINDEX){
                    if(childMove.getBoardIndex() == currentDifficulty.favoritePieceIndex){
                        System.out.println("Queen priority");
                        advtg += (15*currentDifficulty.favoritePieceWeight) * (childMove.isWhite() ? 1 : -1);
                    }
                }

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


        clearFlags();
        return bestMove;
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


        ExecutorService executor = Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors()-2,filteredPositions.size()));
        List<Future<MinimaxResult>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    return null;
                }
                MinimaxOutput miniMaxOut = miniMax(childPos, evalDepthCalc - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite);
                if (miniMaxOut == Stopped) {
                    return null;
                }

                return new MinimaxResult(move,miniMaxOut.getAdvantage(),miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
        try {
            boolean isOver = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to complete
            if(!isOver){
                logger.error("Could not wait for all threads to finish");
            }
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








    //alpha default -inf beta default +inf
    protected MinimaxOutput miniMax(BackendChessPosition position, int depth, double alpha, double beta, boolean isWhiteTurn){
        // all recursive stop cases
//        System.out.println(cnt++);
        if(stop.get()){
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }
//        int key = Objects.hash(gameState.hashCode(),position.hashCode(),isWhiteTurn);
        long key = hasher.computeHash(position,isWhiteTurn);
        if(position.isDraw()){
            return new MinimaxOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
        }
        if(AdvancedChessFunctions.isAnyNotMovePossible(isWhiteTurn,position,position.gameState)){
            // possiblity of a black winning from checkmate, else draw
            if(AdvancedChessFunctions.isChecked(isWhiteTurn,position.board)){
                return new MinimaxOutput(isWhiteTurn ? ChessConstants.BLACKCHECKMATEVALUE : ChessConstants.WHITECHECKMATEVALUE);
            }
            return new MinimaxOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
        }
//

        if(depth == 0){
            return new MinimaxOutput(ComputerHelperFunctions.getFullEval(position,position.gameState,isWhiteTurn,true));
        }
        // todo fix transtable erratic behaviour
//        if(transTable.containsKey(key)){
////            logger.info("Transtable value being used");
//            return new MinimaxOutput(transTable.get(key));
//        }

        if(depth <= evalDepth- currentDifficulty.depthThreshold){
            // do a check to see if there is any noticeable advantage diff.  If not then return
            double posEval = ComputerHelperFunctions.getFullEval(position,position.gameState,isWhiteTurn,true);
            double diff = posEval-callTimeEval;
            double advtgThresholdCalc = currentDifficulty.advantageThreshold + (double) (evalDepth- currentDifficulty.depthThreshold - depth) /8;
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
        String og = GeneralChessFunctions.getBoardDetailedString(position.board);
        String ogMove = position.getMoveThatCreatedThis().toString();

        // recursive part
        if(isWhiteTurn){
            MinimaxOutput maxEval = new MinimaxOutput(Double.NEGATIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(true,position.gameState);
            List<BackendChessPosition> childPositions = position.getAllChildPositions(true,position.gameState);
//            assertTrue(childMoves.size(),childPositions.size(),true);
            for(int i = 0;i<childMoves.size();i++){
                ChessMove c = childMoves.get(i);
                position.makeLocalPositionMove(c);
                if(!assertTrue(position,childPositions.get(i),true,og + "\n" + ogMove)){
                    System.out.println("Expected index: " + (App.ChessCentralControl.gameHandler.currentGame.curMoveIndex + (currentDifficulty.depth-depth)+1));
                    assertTrue(position.gameState.toString().trim(),childPositions.get(i).gameState.toString().trim(),true,"Before");
                    System.out.println("whywhywhywhywhywhywhy");
                    return Stopped;


                }
//                assertTrue(position.getMoveThatCreatedThis(),childPositions.get(i).getMoveThatCreatedThis(),true);
//                assertTrue(position.gameState.toString().trim(),childPositions.get(i).gameState.toString(),true,"Before");
                MinimaxOutput out = miniMax(position, depth - 1, alpha, beta, false);
                position.undoLocalPositionMove();
                if(out == Stopped){
//                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:    " + depth);
//                    System.out.println(c.toString());
//                    String outStr = GeneralChessFunctions.getBoardDetailedString(position.board);
//                    System.out.println(outStr);
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
            List<ChessMove> childMoves = position.getAllChildMoves(false,position.gameState);
            List<BackendChessPosition> childPositions = position.getAllChildPositions(false,position.gameState);
//            assertTrue(childMoves.size(),childPositions.size(),true);
            for(int i = 0;i<childMoves.size();i++){
                ChessMove c = childMoves.get(i);
                position.makeLocalPositionMove(c);
                if(!assertTrue(position,childPositions.get(i),false,og + "\n" + ogMove)){
                    System.out.println("Expected index: " + (App.ChessCentralControl.gameHandler.currentGame.curMoveIndex + (currentDifficulty.depth-depth)+1));
                    assertTrue(position.gameState.toString().trim(),childPositions.get(i).gameState.toString().trim(),true,"Before");
                    System.out.println("whywhywhywhywhywhywhy");
                    return Stopped;
                }

//                assertTrue(position.getMoveThatCreatedThis(),childPositions.get(i).getMoveThatCreatedThis(),true);
                MinimaxOutput out = miniMax(position, depth - 1, alpha, beta, true);
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

    private <T> void  assertTrue(T a, T b,boolean isSerious,String s) {
        if(isSerious && !a.equals(b)){
            int n = randomComp.nextInt(0,100);
            System.out.println(s + "\nERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR!!!!\na"  + n + "\n" + a + "\nb" + n +"\n" +b);
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
            MinimaxOutput eval = miniMax(c,depth-1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,!isWhite);

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




}