package chessengine.Computation;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Enums.Flag;
import chessengine.Enums.Movetype;
import chessengine.Enums.PromotionType;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.BitFunctions;
import chessengine.Functions.EvaluationFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;
import chessengine.Records.PVEntry;
import chessengine.Records.SearchResult;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Searcher {
    private final Logger logger = LogManager.getLogger(this.toString());
    private final AtomicBoolean stopSearch = new AtomicBoolean(false);
    private final int defaultMaxDepth = 25;
    private int maxSearchDepth = defaultMaxDepth;
    public SearchInfoAggregator searchInfo;
    long startTime;
    BackendChessPosition chessPosition;
    int maxTimeMs;
    boolean hasSearchedAtLeastOne;

    boolean isNoMoves;
    int bestEvaluation;
    ChessMove bestMove;
    int CurrentDepth;
    int bestEvaluationIter;
    ChessMove bestMoveIter;
    PVEntry[] pV;
    PVEntry[] pVIter;
    TranspositionTable transpositionTable;
    MoveOrderer orderer;
    MoveGenerator moveGenerator;
    StopWatch stopwatch;
    PromotionType promotionType = PromotionType.ALL;
    private boolean stop = false;
    public Searcher() {
        transpositionTable = new TranspositionTable(1000000);
        orderer = new MoveOrderer();
        searchInfo = new SearchInfoAggregator();
        moveGenerator = new MoveGenerator();
        stopwatch = new StopWatch();
        stopwatch.start();

    }

    public void stopSearch() {
        stopSearch.set(true);
    }

    public boolean wasForcedStop() {
        return stopSearch.get();
    }

    private boolean checkStopSearch() {
        long currentTime = stopwatch.getTime();
        if (stopSearch.get() || currentTime - startTime > maxTimeMs) {
            if(stopSearch.get()){
                logger.debug("Prematurely stopped");
            }
//            System.out.println("Stopping");
            stop = true;
            return true;
        }
        return false;
    }

    private void resetSearch() {
        pV = new PVEntry[maxSearchDepth];
        pVIter = new PVEntry[maxSearchDepth];
        stopSearch.set(false);
        startTime = stopwatch.getTime();
        orderer.clearKillers();
        orderer.clearHistory();
        bestMoveIter = null;
        bestMove = null;
        isNoMoves = false;
        bestEvaluation = Integer.MIN_VALUE + 1;
        bestEvaluationIter = Integer.MIN_VALUE + 1;
        stop = false;
    }


    public SearchResult search(BackendChessPosition pos, int maxTimeMs) {
        this.maxTimeMs = maxTimeMs;
        this.maxSearchDepth = defaultMaxDepth;
        resetSearch();
        chessPosition = pos.clonePosition();
        runIterativeDeepening();

        if (bestMove == null) {
            if(isNoMoves){
                return null;
            }
            logger.error("Not able to seach at all!!!");
            bestMove = moveGenerator.generateMoves(pos, false, promotionType)[0];
            return new SearchResult(bestMove, 0, -1, new PVEntry[]{new PVEntry(bestMove, 0, Movetype.NONE)});
        }
        return new SearchResult(bestMove, bestEvaluation, CurrentDepth, pV);
    }

    public SearchResult search(BackendChessPosition pos, int maxTimeMs,int maxSearchDepth) {
        this.maxTimeMs = maxTimeMs;
        this.maxSearchDepth = maxSearchDepth;
        resetSearch();
        chessPosition = pos.clonePosition();
        runIterativeDeepening();

        if (bestMove == null) {
            if(isNoMoves){
                return null;
            }
            logger.error("Not able to seach at all!!!");
            bestMove = moveGenerator.generateMoves(pos, false, promotionType)[0];
            return new SearchResult(bestMove, 0, -1, new PVEntry[]{new PVEntry(bestMove, 0, Movetype.NONE)});
        }
        return new SearchResult(bestMove, bestEvaluation, CurrentDepth, pV);
    }

    private void runIterativeDeepening() {
        for (int searchDepth = 1; searchDepth < maxSearchDepth; searchDepth++) {
            hasSearchedAtLeastOne = false;
            search(searchDepth, 0, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, 0);

//            System.out.println("Search depth: " + searchDepth);
            if (stop || checkStopSearch()) {
                if (bestEvaluationIter >= bestEvaluation) {
                    bestEvaluation = bestEvaluationIter;
                    bestMove = bestMoveIter;
                    pV = pVIter;
                }
                break;
            } else {
                pV = pVIter;
                bestEvaluation = bestEvaluationIter;
                bestMove = bestMoveIter;
                CurrentDepth = searchDepth;

                if (EvaluationFunctions.isMateScore(bestEvaluation) && EvaluationFunctions.extractDepthFromMateScore(bestEvaluation) <= CurrentDepth) {
                    break;
                }

                bestEvaluationIter = Integer.MIN_VALUE + 1;
                bestMoveIter = null;
                pVIter = new PVEntry[maxSearchDepth];
                hasSearchedAtLeastOne = false;


            }
        }
    }

    private int search(int currentPly, int plyFromRoot, int alpha, int beta, int numExtensions) {
        if (chessPosition.isDraw() || stop || checkStopSearch()) {
            return 0;
        }

        if (plyFromRoot > 0) {
            alpha = Math.max(alpha, EvaluationFunctions.baseMateScore - plyFromRoot);
            beta = Math.min(beta, -EvaluationFunctions.baseMateScore + plyFromRoot);
            if (alpha >= beta) {
                return alpha;
            }
        }

//        int transpositionEvaluation = transpositionTable.probeHash(chessPosition.zobristKey,currentPly,alpha,beta);
//        if(transpositionEvaluation != ChessConstants.NONE){
//            searchInfo.incrementNumTranspositionUses();
//            if(plyFromRoot == 0){
//                if(transpositionTable.markedIndex2 != null){
//                    bestEvalIter = transpositionTable.markedIndex2.value();
//                    bestMove = transpositionTable.markedIndex2.bestMove();
//                }
//                else if(transpositionTable.markedIndex1 != null){
//                    bestEvalIter = transpositionTable.markedIndex1.value();
//                    bestMove = transpositionTable.markedIndex1.bestMove();
//                }
//            }
////            System.out.println("Using transposed value of" + transpositionEvaluation);
//            return transpositionEvaluation;
//        }
//        transpositionTable.clearProbe();
        searchInfo.incrementUniquePositionsSearched();

        if (currentPly <= 0) {
            int eval = quiscenceSearch(alpha, beta);
            if (EvaluationFunctions.isMateScore(eval)) {
                eval -= plyFromRoot;
            }
            return eval;
        }


        ChessMove[] moves = moveGenerator.generateMoves(chessPosition, false, promotionType);
        orderer.sortMoves(bestMove, chessPosition.board, moves, transpositionTable.getMarkedMove1(), transpositionTable.getMarkedMove2(), plyFromRoot);
        boolean isChecked = AdvancedChessFunctions.isChecked(chessPosition.isWhiteTurn, chessPosition.board);
        if (moves[0] == null) {
            if(plyFromRoot == 0){
                isNoMoves = true;
            }
//            System.out.println("Game over!");
            if (isChecked) {
                return EvaluationFunctions.baseMateScore - plyFromRoot;
            }

            return 0;
        }


        Flag evaluation = Flag.UPPERBOUND;
        ChessMove bestMoveSoFar = null;
        for (int i = 0; i < moves.length; i++) {
            ChessMove move = moves[i];
            if (move == null) {
                break;
            }
            chessPosition.makeLocalPositionMove(move);
            // move extensions
            int extension = 0;

            final int maxExtensions = 8;
            if(numExtensions < maxExtensions){
                if (isChecked) {
                    extension = 1;
                } else if (move.getBoardIndex() == ChessConstants.PAWNINDEX){
                    boolean isPasser = BitFunctions.isPassedPawn(move.getNewX(),move.getNewY(),move.isWhite(),chessPosition.board);
                    if(move.isWhite() && move.getNewY() <= 1){
                        if(isPasser){
                            extension = 2;
                        }
                        else{
                            extension = 1;
                        }
                    }
                    else if(move.getNewY() >= 6){
                        if(isPasser){
                            extension = 2;
                        }
                        else{
                            extension = 1;
                        }
                    }
                }
            }

            // move reductions
            Movetype movetype = Movetype.getMoveType(chessPosition);
            int eval = 0;
            boolean needsFullSearch = true;
            if (extension == 0 && plyFromRoot > 3 && i > 3 && !move.isEating()) {
                final int reducedDepth = 1;
                eval = -search(currentPly - 1 - reducedDepth, plyFromRoot + 1, -alpha - 1, -alpha, numExtensions);
                needsFullSearch = eval > alpha;
            }
            if (needsFullSearch) {
                eval = -search(currentPly - 1 + extension, plyFromRoot + 1, -beta, -alpha, numExtensions + extension);
            }
            chessPosition.undoLocalPositionMove();

            if (stop || checkStopSearch()) {
                return 0;
            }


            // fail high means its a lowerbound, opponent can avoid this move so you might have even better unsearched
            if (eval >= beta) {
                transpositionTable.recordHash(chessPosition.zobristKey, currentPly, beta, move, Flag.LOWERBOUND);

                // history + killer heuristics
                if (!move.isEating()) { // see if this is needed
                    int fromSquare = GeneralChessFunctions.positionToBitIndex(move.getOldX(), move.getOldY());
                    int toSquare = GeneralChessFunctions.positionToBitIndex(move.getNewX(), move.getNewY());

                    orderer.killers[plyFromRoot].saveKiller(move);

                    orderer.history[chessPosition.isWhiteTurn ? 0 : 1][fromSquare][toSquare] += currentPly * currentPly;

                }
                searchInfo.incrementNumBetaCutoffs();
                return beta;
            }

            if (eval > alpha) {
//                System.out.println("Alpha: " + alpha + " to -> " + eval);
                evaluation = Flag.EXACT;
                bestMoveSoFar = move;
                alpha = eval;
                pVIter[plyFromRoot] = new PVEntry(move, eval, movetype);

                if (plyFromRoot == 0) {
                    bestEvaluationIter = alpha;
                    bestMoveIter = move;
                    hasSearchedAtLeastOne = true;
                }
            }


        }

        transpositionTable.recordHash(chessPosition.zobristKey, currentPly, alpha, bestMoveSoFar, evaluation);
        return alpha;


    }

    private int quiscenceSearch(int alpha, int beta) {
        if (stop || checkStopSearch()) {
            return 0;
        }

        int evaluation = EvaluationFunctions.getStaticEvaluation(chessPosition);

        if (evaluation >= beta) {
            searchInfo.incrementNumBetaCutoffs();

            return beta;
        }
        if (evaluation > alpha) {
            alpha = evaluation;
        }

        ChessMove[] moves = moveGenerator.generateMoves(chessPosition, true, promotionType);
        orderer.sortMoves(null, chessPosition.board, moves, null, null, 19);
        for (ChessMove move : moves) {
            if (move == null) {
                break;
            }
            chessPosition.makeLocalPositionMove(move);
            int eval = -quiscenceSearch(-beta, -alpha);
            chessPosition.undoLocalPositionMove();

            if (eval >= beta) {
                searchInfo.incrementNumBetaCutoffs();
                return beta;
            }

            if (eval > alpha) {
                alpha = eval;
            }
        }
        return alpha;


    }

}


