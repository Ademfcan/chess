package chessengine.Computation;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Functions.ZobristHasher;
import chessengine.Misc.ChessConstants;
import chessengine.Misc.LimitedSizeMap;
import chessserver.ComputerDifficulty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Computer {


    public static final MinimaxEvalOutput Stopped = new MinimaxEvalOutput(Double.NaN);
    protected final ZobristHasher hasher;
    private final Logger logger;
    private final int maxEntries = 600000;
    private final Random randomComp = new Random();
    public volatile AtomicBoolean stop;
    protected Map<Long, Double> transTable;
    protected int evalDepth;
    public ComputerDifficulty currentDifficulty = ComputerDifficulty.MAXDIFFICULTY;

    /// dynamically updated stuff
    protected double callTimeEval;
    private boolean running = false;

    public Computer(int evalDepth) {
        this.transTable = Collections.synchronizedMap(new LimitedSizeMap<>(maxEntries));
        this.hasher = new ZobristHasher();
        logger = LogManager.getLogger(this.toString());
        stop = new AtomicBoolean(false);
        this.evalDepth = evalDepth;
    }

    public boolean isRunning() {
        return running;
    }

    private void setCallTimeEval(ChessPosition posAtCallTime, ChessStates gameStateAtCallTime, boolean whiteTurnAtCallTime) {
        callTimeEval = ComputerHelperFunctions.getFullEval(posAtCallTime, gameStateAtCallTime, whiteTurnAtCallTime, true);

    }

    private void clearFlags() {
//        logger.debug("Clearing flags");
        stop.set(false);
        running = false;

    }

    public void setEvalDepth(int evalDepth) {
        this.evalDepth = evalDepth;
    }

    public void setCurrentDifficulty(ComputerDifficulty currentDifficulty) {
        logger.debug("Updating difficulty to: " + currentDifficulty.toString());
        this.currentDifficulty = currentDifficulty;
    }

    public ChessMove getComputerMove(boolean isWhite, ChessPosition pos, ChessStates gameState) {
        setCallTimeEval(pos, gameState, isWhite);

        logger.debug("Transtable size: " + transTable.size());

        ChessMove bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;
        double bestDepth = 1000;
        running = true;


        List<BackendChessPosition> positions = pos.getAllChildPositions(isWhite, gameState);
        if(positions == null){
            return null;
        }
        List<BackendChessPosition> filteredPositions = null;
        if(!currentDifficulty.equals(ComputerDifficulty.MAXDIFFICULTY)){
            filteredPositions = positions.stream().filter(p -> ComputerHelperFunctions.doesMoveFitRestrictions(p, currentDifficulty)).toList();
        }
        if (currentDifficulty.equals(ComputerDifficulty.MAXDIFFICULTY) || filteredPositions.isEmpty()) {
            filteredPositions = positions;
        }
        double randProb = randomComp.nextDouble();
        // now  we will introduce entropy
        if (randProb < currentDifficulty.randomnessFactor && filteredPositions.size() > currentDifficulty.minRandomChoices) {
            int randomCutoff = randomComp.nextInt(currentDifficulty.minRandomChoices, filteredPositions.size() + 1);
            int randomOffset = randomComp.nextInt(0, filteredPositions.size() - randomCutoff + 1);
            filteredPositions = filteredPositions.subList(randomOffset, randomCutoff + randomOffset);
        }
        // last check
        if (filteredPositions.size() < 2) {
            // one position left means we dont even need to consider any other moves, there is only one option
            return filteredPositions.get(0).getMoveThatCreatedThis();
        }

        int threadCount = Math.min(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1), filteredPositions.size());
//        int threadCount = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);
        logger.debug("Thread count: " + threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<MinimaxMoveResult>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    return null;
                }
                MinimaxEvalOutput miniMaxOut = miniMax(childPos, currentDifficulty.depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite);
                if (miniMaxOut == Stopped) {
                    return null;
                }

                return new MinimaxMoveResult(move, miniMaxOut.getAdvantage(), miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
        try {
            boolean isOver = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to complete
            if (!isOver) {
                logger.error("Could not wait for all threads to finish");
            }
            for (Future<MinimaxMoveResult> future : futures) {
                MinimaxMoveResult result = future.get();
                if (result == null) {
                    continue;
                }

                double advtg = result.getAdvantage();

                ChessMove childMove = result.getMove();

                // move priority
                if (currentDifficulty.favoritePieceIndex != ChessConstants.EMPTYINDEX) {
                    if (childMove.getBoardIndex() == currentDifficulty.favoritePieceIndex) {
                        advtg += (15 * currentDifficulty.favoritePieceWeight) * (childMove.isWhite() ? 1 : -1);
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
            logger.error("Error in best move executor", e);
        }


        clearFlags();
        return bestMove;
    }



    public List<ComputerOutput> getNMoves(boolean isWhite, ChessPosition pos, ChessStates gameState, int nMoves) {
        List<ComputerOutput> BestMoves = new ArrayList<>(nMoves);
        HashSet<ChessMove> prevMoves = new HashSet<>(nMoves);
        List<BackendChessPosition> possiblePositions = pos.getAllChildPositions(isWhite, gameState);

        running = true;
        for (int i = 0; i < Math.min(nMoves,possiblePositions.size()); i++) {
            ComputerOutput out = getComputerMoveWithCondiditon(isWhite, pos, gameState, prevMoves,possiblePositions);
            if (!out.equals(ChessConstants.emptyOutput)) {
                BestMoves.add(out);
                prevMoves.add(out.move);
            }

        }
        clearFlags();
        return BestMoves;
    }

    private ComputerOutput getComputerMoveWithCondiditon(boolean isWhite, ChessPosition pos, ChessStates gameState, HashSet<ChessMove> alreadyPlayedMoves,List<BackendChessPosition> chessPositions) {
        setCallTimeEval(pos, gameState, isWhite);

//        logger.debug("Transtable size: " + transTable.size());

        ChessMove bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;
        double bestDepth = 1000;
        running = true;
        List<BackendChessPosition> filteredPositions = chessPositions.stream().filter(p -> !alreadyPlayedMoves.contains(p.getMoveThatCreatedThis())).toList();
        int threadCount = Math.min(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1), filteredPositions.size());
        logger.debug("Nmoves thread count: " + threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<MinimaxMoveResult>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    return null;
                }
                MinimaxEvalOutput miniMaxOut = miniMax(childPos, evalDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite);
                if (miniMaxOut == Stopped) {
                    return null;
                }

                return new MinimaxMoveResult(move, miniMaxOut.getAdvantage(), miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
        try {
            boolean isOver = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to complete
            if (!isOver) {
                logger.error("Could not wait for all threads to finish");
            }
            for (Future<MinimaxMoveResult> future : futures) {
                MinimaxMoveResult result = future.get();
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
            logger.error("Executor error on get n moves funct",e);
        }

        if (bestMove == null) {

            logger.debug("null bestmovesoutput n moves funct, likely stop flag");
            return ChessConstants.emptyOutput;
        }

        clearFlags();
        return new ComputerOutput(bestMove, bestEval);
    }


    //alpha default -inf beta default +inf
    protected MinimaxEvalOutput miniMax(BackendChessPosition position, int depth, double alpha, double beta, boolean isWhiteTurn) {
        // all recursive stop cases
        if (stop.get()) {
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }
        if (position.isDraw()) {
            return new MinimaxEvalOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
        }
        boolean isAnyMoveNotPossible = AdvancedChessFunctions.isAnyNotMovePossible(isWhiteTurn, position, position.gameState);
        boolean isChecked = AdvancedChessFunctions.isChecked(isWhiteTurn, position.board);
        if (isAnyMoveNotPossible) {
            // possiblity of a black winning from checkmate, else draw
            if (isChecked) {
                return new MinimaxEvalOutput(isWhiteTurn ? ChessConstants.BLACKCHECKMATEVALUE : ChessConstants.WHITECHECKMATEVALUE);
            }
            return new MinimaxEvalOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
        }
//
        depth+= ComputerHelperFunctions.calculateMoveExtension(position,isWhiteTurn,isChecked);
        if (depth == 0) {
            // first check move extensions
            return new MinimaxEvalOutput(ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true));
        }

        long key = hasher.computeHash(position.board, isWhiteTurn);
        // flip board for inverse position check
        position.board.flipBoard();
        long flippedKey = hasher.computeHash(position.board, !isWhiteTurn);
        position.board.flipBoard(); // flip back


//        // todo fix transtable erratic behaviour
        if (transTable.containsKey(key)) {
            return new MinimaxEvalOutput(transTable.get(key));
        }
        if (transTable.containsKey(flippedKey)) {
            return new MinimaxEvalOutput(-transTable.get(flippedKey));
        }

        if (depth <= evalDepth - currentDifficulty.depthThreshold) {
            // do a check to see if there is any noticeable advantage diff.  If not then return
            double posEval = ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true);
            double diff = posEval - callTimeEval;
            double advtgThresholdCalc = currentDifficulty.advantageThreshold + (double) (evalDepth - currentDifficulty.depthThreshold - depth) / 8;
            if (isWhiteTurn) {
                // only stay if diff greater than advtgThreshold
                if (diff < advtgThresholdCalc) {
                    // not worth it to go deeper
                    return new MinimaxEvalOutput(posEval);
                }
                // else go deeper ;)

            } else {
                // only stay if diff less than advtgThreshold
                if (diff > -advtgThresholdCalc) {
                    // not worth it to go deeper
                    return new MinimaxEvalOutput(posEval);
                }
                // else go deeper ;)
            }

        }

        // once again  check stop flag
        if (stop.get()) {
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }

        // recursive part
        if (isWhiteTurn) {
            MinimaxEvalOutput maxEval = new MinimaxEvalOutput(Double.NEGATIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(true, position.gameState);
           if(childMoves == null){
               logger.error("Childmoves error!");
               return Stopped;
           }
            for (int i = 0; i < childMoves.size(); i++) {
                ChessMove c = childMoves.get(i);
                position.makeLocalPositionMove(c);
                MinimaxEvalOutput out = miniMax(position, depth - 1, alpha, beta, false);
                position.undoLocalPositionMove();
                if (out == Stopped) {
                    return Stopped;
                }
                maxEval = max(out, maxEval);
                alpha = Math.max(alpha, out.getAdvantage());  // Update alpha after the recursive call
                if (beta <= alpha) {
                    break;
                }

            }
            if(depth == evalDepth-1){
                transTable.put(key,maxEval.getAdvantage());
            }
            return maxEval.incrementAndReturn();
        } else {
            MinimaxEvalOutput minEval = new MinimaxEvalOutput(Double.POSITIVE_INFINITY);
            List<ChessMove> childMoves = position.getAllChildMoves(false, position.gameState);
            if(childMoves == null){
                logger.error("Childmoves error!");
                return Stopped;
            }
            for (int i = 0; i < childMoves.size(); i++) {
                ChessMove c = childMoves.get(i);
                position.makeLocalPositionMove(c);
//

                MinimaxEvalOutput out = miniMax(position, depth - 1, alpha, beta, true);
                position.undoLocalPositionMove();
                if (out == Stopped) {
                    return Stopped;

                }

                minEval = min(out, minEval);
                beta = Math.min(beta, out.getAdvantage());  // Update beta after the recursive call
                if (beta <= alpha) {
                    break;
                }

            }
            if(depth == evalDepth-1){
                transTable.put(key,minEval.getAdvantage());
            }

            return minEval.incrementAndReturn();
        }


    }

    protected MinimaxEvalOutput min(MinimaxEvalOutput m1, MinimaxEvalOutput m2) {
        if (m1.getAdvantage() == m2.getAdvantage()) {
            // always return the one with less depth if equal
            if (m1.getOutputDepth() < m2.getOutputDepth()) {
                return m1;
            }
            return m2;
        }
        if (m1.getAdvantage() < m2.getAdvantage()) {
            return m1;
        }
        return m2;

    }

    protected MinimaxEvalOutput max(MinimaxEvalOutput m1, MinimaxEvalOutput m2) {
        if (m1.getAdvantage() == m2.getAdvantage()) {
            // always return the one with less depth if equal
            if (m1.getOutputDepth() < m2.getOutputDepth()) {
                return m1;
            }
            return m2;
        }

        if (m1.getAdvantage() > m2.getAdvantage()) {
            return m1;
        } else {
            return m2;
        }
    }


    public MinimaxEvalOutput getFullEvalMinimax(ChessPosition pos, ChessStates gameState, int depth, boolean isWhite) {
        setCallTimeEval(pos, gameState, isWhite);


        MinimaxEvalOutput bestEval = isWhite ? new MinimaxEvalOutput(Double.MIN_VALUE) : new MinimaxEvalOutput(Double.MAX_VALUE);
        running = true;
        if (AdvancedChessFunctions.isAnyNotMovePossible(true, pos, gameState)) {
            // possiblity of a checkmate, else draw
            clearFlags();
            if (AdvancedChessFunctions.isChecked(true, pos.board)) {
                return new MinimaxEvalOutput(ChessConstants.BLACKCHECKMATEVALUE);
            }
            return new MinimaxEvalOutput(0);
        }
        if (AdvancedChessFunctions.isAnyNotMovePossible(false, pos, gameState)) {
            // possiblity of a checkmate, else draw
            clearFlags();
            if (AdvancedChessFunctions.isChecked(false, pos.board)) {
                return new MinimaxEvalOutput(ChessConstants.WHITECHECKMATEVALUE);
            }
            return new MinimaxEvalOutput(0);
        }
        for (BackendChessPosition c : pos.getAllChildPositions(isWhite, gameState)) {
            if (stop.get()) {
                clearFlags();
                return Stopped;
            }
            MinimaxEvalOutput eval = miniMax(c, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite);
            if (eval == Stopped) {
                clearFlags();
                return Stopped;
            }

            if (isWhite) {
                bestEval = max(eval, bestEval);
            } else {
                bestEval = min(eval, bestEval);
            }
        }

        clearFlags();
        return bestEval;
    }


}