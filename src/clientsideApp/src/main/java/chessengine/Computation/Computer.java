package chessengine.Computation;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.ChessStates;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.ZobristHasher;
import chessengine.Misc.ChessConstants;
import chessserver.ComputerDifficulty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Computer {


    public static final EvalOutput Stopped = new EvalOutput(Double.NaN);
    public static final EvalOutput TIMEOUT = new EvalOutput(Double.MIN_VALUE);
    protected final ZobristHasher hasher;
    private final Logger logger;
    private final int maxEntries = 600000;
    private final Random randomComp = new Random();
    private final int maxExtensions = 6;
    public volatile AtomicBoolean stop;
    public boolean TIMEOUTFLAG;
    public ComputerDifficulty currentDifficulty = ComputerDifficulty.STOCKFISHMax;
    protected Map<Long, Double> transTable;
    /// dynamically updated stuff
    protected double callTimeEval;
    private long callTimeMs;
    int cnt = 0;
    private boolean running = false;

    public Computer() {
        this.transTable = new ConcurrentHashMap<>();
        this.hasher = new ZobristHasher();
        logger = LogManager.getLogger(this.toString());
        stop = new AtomicBoolean(false);
    }

    public boolean isRunning() {
        return running;
    }

    private void setCallTimeInformation(ChessPosition posAtCallTime, ChessStates gameStateAtCallTime, boolean whiteTurnAtCallTime) {
        callTimeEval = ComputerHelperFunctions.getFullEval(posAtCallTime, gameStateAtCallTime, whiteTurnAtCallTime, true);
        callTimeMs = System.currentTimeMillis();

    }

    public void clearFlags() {
//        logger.debug("Clearing flags");
        stop.set(false);
        TIMEOUTFLAG = false;
        running = false;
        cnt = 0;

    }

    public void setCurrentDifficulty(ComputerDifficulty currentDifficulty) {
        logger.debug("Updating difficulty to: " + currentDifficulty.toString());
        this.currentDifficulty = currentDifficulty;
    }


    public MoveOutput getComputerMoveWithFlavors(boolean isWhite, ChessPosition pos, ChessStates gameState) {
        setCallTimeInformation(pos, gameState, isWhite);
        transTable.clear();

        MoveOutput bestMove = null;
        double bestEval = isWhite ? -10000000 : 10000000;
        double bestDepth = 1000;
        running = true;


        List<BackendChessPosition> positions = pos.getAllChildPositions(isWhite, gameState);
        if (positions == null) {
            return null;
        }
        List<BackendChessPosition> filteredPositions = null;
        if(currentDifficulty.equals(ComputerDifficulty.MaxDifficulty)) {
            filteredPositions = positions;
        }
        else{
            filteredPositions = positions.stream().filter(p -> ComputerHelperFunctions.doesMoveFitRestrictions(p, currentDifficulty)).toList();
            if (filteredPositions.isEmpty()) {
                filteredPositions = positions;
            }
            double randProb = randomComp.nextDouble();
            // now introduce some entropy
            if (randProb < currentDifficulty.randomnessFactor && filteredPositions.size() > currentDifficulty.minRandomChoices) {
                int randomCutoff = randomComp.nextInt(currentDifficulty.minRandomChoices, filteredPositions.size() + 1);
                int randomOffset = randomComp.nextInt(0, filteredPositions.size() - randomCutoff + 1);
                filteredPositions = filteredPositions.subList(randomOffset, randomCutoff + randomOffset);
            }
        }

        // last check
        if (filteredPositions.size() < 2) {
            // one position left means we dont even need to consider any other moves, there is only one option
            return new MoveOutput(filteredPositions.get(0).getMoveThatCreatedThis(), Double.NaN, 1);
        }

        int threadCount = Math.min(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1), filteredPositions.size());
        logger.debug("Thread count: " + threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<MoveOutput>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    return null;
                }
                EvalOutput miniMaxOut = miniMax(childPos, currentDifficulty.depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite, 0);
                if (miniMaxOut == Stopped) {
                    return null;
                }

                return new MoveOutput(move, miniMaxOut.getAdvantage(), miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
        try {
            boolean isOver = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Wait for all tasks to complete
            if (!isOver) {
                logger.error("Could not wait for all threads to finish");
            }
            for (Future<MoveOutput> future : futures) {
                MoveOutput result = future.get();
                if (result == null) {
                    return null;
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
                        bestMove = result;
                        bestDepth = depth;
                    } else if (advtg == bestEval && depth < bestDepth) {
                        bestMove = result;
                        bestDepth = depth;
                    }
                } else {
                    if (advtg < bestEval) {
                        bestEval = advtg;
                        bestMove = result;
                        bestDepth = depth;
                    } else if (advtg == bestEval && depth < bestDepth) {
                        bestMove = result;
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

    public MoveOutput[] getComputerMove(ChessPosition pos, ChessStates gameState, boolean isWhite, int NMoves) {
        setCallTimeInformation(pos, gameState, isWhite);

//        logger.debug("Transtable size: " + transTable.size());

        MoveOutput[] bestMoves = new MoveOutput[NMoves];
        running = true;
        List<BackendChessPosition> filteredPositions = pos.getAllChildPositions(isWhite, gameState);
        int threadCount = Math.min(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1), filteredPositions.size());
        logger.debug("Nmoves thread count: " + threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<MoveOutput>> futures = new ArrayList<>();
        for (BackendChessPosition childPos : filteredPositions) {
            futures.add(executor.submit(() -> {
                ChessMove move = childPos.getMoveThatCreatedThis();
                if (stop.get()) {
                    logger.error("Stop get");
                    return null;
                }
                EvalOutput miniMaxOut = miniMax(childPos, currentDifficulty.depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite, 0);
                if (miniMaxOut == Stopped) {
                    logger.error("minimax stopped");
                    return null;
                }

                return new MoveOutput(move, miniMaxOut.getAdvantage(), miniMaxOut.getOutputDepth());
            }));
        }

        executor.shutdown();
        try {
            List<MoveOutput> results = new ArrayList<>();
            for (Future<MoveOutput> future : futures) {
                MoveOutput result = future.get();
                if (result == null) {
                    return null; // stop flag
                }
                results.add(result);
            }
            bestMoves = results.stream().sorted((c1, c2) -> Double.compare(c1.getAdvantage(), c2.getAdvantage())*(isWhite ? 1 : -1) - Integer.compare(c1.getDepth(), c2.getDepth())).toList().subList(results.size() - NMoves, results.size()).toArray(MoveOutput[]::new);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Executor error on get n moves funct", e);
        }


        clearFlags();
        return bestMoves;
    }

    //alpha default -inf beta default +inf
    protected EvalOutput miniMax(BackendChessPosition position, int depth, double alpha, double beta, boolean isWhiteTurn, int extension) {
//        System.out.println(currentDifficulty.depth-depth);
        // all recursive stop cases

        if (stop.get()) {
            logger.info("Stopping Minimax due to flag");
            return Stopped;
        }
        if (position.isDraw()) {
            return new EvalOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
        }
        List<ChessMove> possibleMoves = position.getAllChildMoves(isWhiteTurn, position.gameState);
        if (possibleMoves == null) {
            logger.error("Childmoves error!");
            return Stopped;
        }

        if (possibleMoves.isEmpty()) {
            boolean isChecked = AdvancedChessFunctions.isChecked(isWhiteTurn, position.board);
            // possiblity of a black winning from checkmate, else draw
            if (isChecked) {
                return new EvalOutput(isWhiteTurn ? ChessConstants.BLACKCHECKMATEVALUE : ChessConstants.WHITECHECKMATEVALUE);
            }
            return new EvalOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
        }
//
        if (depth == 0) {
//            // first check move extensions
//            int moveExtension = 0;
//            if (extension <= maxExtensions) {
//                moveExtension = ComputerHelperFunctions.calculateMoveExtension(position, isWhiteTurn, isChecked);
//
//            }
//            if (moveExtension == 0) {
                return new EvalOutput(ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true));
//            return new EvalOutput(quiescenceSearch(position,alpha,beta,isWhiteTurn));
//            }
//            depth += moveExtension;
//            extension += 2;
        }
        long currentTime = System.currentTimeMillis();
//        if(currentTime-callTimeMs > ChessConstants.MAXTIMEMS){
//            TIMEOUTFLAG = true;
//            return new EvalOutput(ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true));
//        }

//        long key = hasher.computeHash(position.board, isWhiteTurn);
////         flip board for inverse position check
//        position.board.flipBoard();
//        long flippedKey = hasher.computeHash(position.board, !isWhiteTurn);
//        position.board.flipBoard(); // flip back
//
//
//        // todo fix transtables
//        if (transTable.containsKey(key)) {
//            return new EvalOutput(transTable.get(key));
//        }
//        if (transTable.containsKey(flippedKey)) {
//            return new EvalOutput(-transTable.get(flippedKey));
//        }

//        if (depth <= currentDifficulty.depth - currentDifficulty.depthThreshold) {
//            // do a check to see if there is any noticeable advantage diff.  If not then return
//            double posEval = ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true);
//            double diff = posEval - callTimeEval;
//            double advtgThresholdCalc = currentDifficulty.advantageThreshold + (double) (currentDifficulty.depth - currentDifficulty.depthThreshold - depth) / 8;
//            if (isWhiteTurn) {
//                // only stay if diff greater than advtgThreshold
//                if (diff < advtgThresholdCalc) {
//                    // not worth it to go deeper
//                    return new EvalOutput(posEval);
//                }
//                // else go deeper ;)
//
//            } else {
//                // only stay if diff less than advtgThreshold
//                if (diff > -advtgThresholdCalc) {
//                    // not worth it to go deeper
//                    return new EvalOutput(posEval);
//                }
//                // else go deeper ;)
//            }
//
//        }



        // recursive part
        EvalOutput bestEval = isWhiteTurn ?  new EvalOutput(Double.NEGATIVE_INFINITY) : new EvalOutput(Double.POSITIVE_INFINITY) ;
        for (ChessMove c : possibleMoves) {
            position.makeLocalPositionMove(c);
            EvalOutput out = miniMax(position, depth - 1, alpha, beta, !isWhiteTurn, extension);
            position.undoLocalPositionMove();
            if (out == Stopped) {
                return Stopped;
            }

            if (isWhiteTurn) {
                bestEval = max(out, bestEval);
                alpha = Math.max(alpha, out.getAdvantage());  // Update alpha after the recursive call

            } else {
                bestEval = min(out, bestEval);
                beta = Math.min(beta, out.getAdvantage());  // Update beta after the recursive call
            }
            if (beta <= alpha) {
                break;
            }
            if (TIMEOUTFLAG) {
                break;
            }
//            if (depth == currentDifficulty.depth - 1) {
//                transTable.put(key, out.getAdvantage());
//            }

        }

        return bestEval.incrementAndReturn();



    }

    public double quiescenceSearch(BackendChessPosition position,double alpha, double beta,boolean isWhiteTurn) {
        // Static evaluation of the current position
        double eval = ComputerHelperFunctions.getFullEval(position,position.gameState,isWhiteTurn,false)*(isWhiteTurn ? 1 : -1);

        // Alpha-Beta pruning: if the evaluation already exceeds beta, cut off search
        if (eval >= beta) {
            return beta;
        }

        // Raise alpha if the position's static evaluation is better than alpha
        if (eval > alpha) {
            alpha = eval;
        }

        // Generate all legal capture moves
        List<ChessMove> captureMoves = position.getAllChildMoves(isWhiteTurn,position.gameState).stream().filter(m -> ComputerHelperFunctions.isNotQuiet(position.board,m)).toList();

        // Evaluate all capture moves
        for (ChessMove move : captureMoves) {
            // Apply the move
            position.makeLocalPositionMove(move);

            // Recursively search this position
            double score = -quiescenceSearch(position,-beta, -alpha,!isWhiteTurn);

            // Undo the move
            position.undoLocalPositionMove();

            // If the score exceeds beta, cut off search (beta-cutoff)
            if (score >= beta) {
                return beta;
            }

            // If the score is better than alpha, update alpha
            if (score > alpha) {
                alpha = score;
            }
        }

        // Return the best evaluation found
        return alpha;
    }

    protected EvalOutput min(EvalOutput m1, EvalOutput m2) {
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

    protected EvalOutput max(EvalOutput m1, EvalOutput m2) {
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


    public EvalOutput getFullEvalMinimax(ChessPosition pos, ChessStates gameState, int depth, boolean isWhite) {
        setCallTimeInformation(pos, gameState, isWhite);
        transTable.clear();

        EvalOutput bestEval = isWhite ? new EvalOutput(Double.MIN_VALUE) : new EvalOutput(Double.MAX_VALUE);
        running = true;
        if (AdvancedChessFunctions.isAnyNotMovePossible(true, pos, gameState)) {
            // possiblity of a checkmate, else draw
            clearFlags();
            if (AdvancedChessFunctions.isChecked(true, pos.board)) {
                return new EvalOutput(ChessConstants.BLACKCHECKMATEVALUE);
            }
            return new EvalOutput(0);
        }
        if (AdvancedChessFunctions.isAnyNotMovePossible(false, pos, gameState)) {
            // possiblity of a checkmate, else draw
            clearFlags();
            if (AdvancedChessFunctions.isChecked(false, pos.board)) {
                return new EvalOutput(ChessConstants.WHITECHECKMATEVALUE);
            }
            return new EvalOutput(0);
        }
        for (BackendChessPosition c : pos.getAllChildPositions(isWhite, gameState)) {
            if (stop.get()) {
                clearFlags();
                return Stopped;
            }
            EvalOutput eval = miniMax(c, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isWhite, 0);
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