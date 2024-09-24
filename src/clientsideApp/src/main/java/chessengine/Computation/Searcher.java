package chessengine.Computation;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.EvaluationFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class Searcher {
    private final Logger logger = LogManager.getLogger(this.toString());
    long startTime;
    BackendChessPosition chessPosition;
    int maxTimeMs;
    ChessMove bestMove;
    int bestEval;
    int CurrentDepth;
    ChessMove bestMoveIter;
    int bestEvalIter;
    boolean hasSearchedAtLeastOneThisIter;
    private boolean stop = false;
    private boolean isSearching = false;

    TranspositionTable transpositionTable;
    MoveOrderer orderer;
    MoveGenerator moveGenerator;
    public SearchInfoAggregator searchInfo;
    StopWatch stopwatch;

    PromotionType promotionType = PromotionType.ALL;
    public Searcher(){
        transpositionTable = new TranspositionTable(1000000);
        orderer = new MoveOrderer();
        searchInfo = new SearchInfoAggregator();
        moveGenerator = new MoveGenerator();
        stopwatch = new StopWatch();
        stopwatch.start();
    }

    private boolean checkStopSearch(){
        long currentTime = stopwatch.getTime();
        if(currentTime - startTime > maxTimeMs){
            System.out.println("Stopping");
            stop = true;
            isSearching = false;
            return true;
        }
        return false;
    }

    private void resetSearch(){
        startTime = stopwatch.getTime();
        bestEvalIter = Integer.MIN_VALUE+1;
        bestEval = Integer.MIN_VALUE+1;
        bestMove = null;
        bestMoveIter = null;
        stop = false;
    }


    public SearchResult search(BackendChessPosition pos,int maxTimeMs){
        this.maxTimeMs = maxTimeMs;
        resetSearch();
        chessPosition = pos;
        isSearching = true;
        runIterativeDeepening();

        if(bestMove == null){
            bestMove = moveGenerator.generateMoves(pos, false,promotionType)[0];
        }
        return new SearchResult(bestMove,bestEval, CurrentDepth);
    }

    private void runIterativeDeepening(){
        for(int searchDepth = 1;searchDepth<256;searchDepth++){
            hasSearchedAtLeastOneThisIter = false;
            search(searchDepth,0,Integer.MIN_VALUE+1,Integer.MAX_VALUE,0);

            if(stop || checkStopSearch()){
                if(hasSearchedAtLeastOneThisIter && bestEvalIter > bestEval){
                    bestMove = bestMoveIter;
                    bestEval = bestEvalIter;
                }
                break;
            }
            else{
                bestEval = bestEvalIter;
                bestMove = bestMoveIter;
                CurrentDepth = searchDepth;

                if(EvaluationFunctions.isMateScore(bestEval) && EvaluationFunctions.extractDepthFromMateScore(bestEval) <= CurrentDepth){
                    break;
                }

                bestMoveIter = null;
                bestEvalIter = Integer.MIN_VALUE+1;




            }
        }
    }

    private int search(int currentPly,int plyFromRoot,int alpha,int beta,int numExtensions){
        if(chessPosition.isDraw() || stop || checkStopSearch()){
            return 0;
        }

//        if(plyFromRoot > 0){
//            alpha = Math.max(alpha, EvaluationFunctions.baseMateScore - plyFromRoot);
//            beta = Math.min(beta, -EvaluationFunctions.baseMateScore + plyFromRoot);
//            if (alpha <= beta)
//            {
//                System.out.println("break");
//                return alpha;
//            }
//        }

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
//            System.out.println("Using transposed value of" + transpositionEvaluation);
//            return transpositionEvaluation;
//        }
        transpositionTable.clearProbe();
        searchInfo.incrementUniquePositionsSearched();

        if(currentPly <= 0){
            int eval = quiscenceSearch(alpha,beta);
            if(EvaluationFunctions.isMateScore(eval)){
                eval -= plyFromRoot;
            }
            return eval;
        }


        ChessMove[] moves = moveGenerator.generateMoves(chessPosition, false,promotionType);
        orderer.sortMoves(bestMove,chessPosition.board,moves,transpositionTable.getMarkedMove1(), transpositionTable.getMarkedMove2());
        boolean isChecked = AdvancedChessFunctions.isChecked(chessPosition.isWhiteTurn,chessPosition.board);
        if(moves[0] == null){
//            System.out.println("Game over!");
            if(isChecked){
                return EvaluationFunctions.baseMateScore-plyFromRoot;
            }
            return 0;
        }



        Flag evaluation = Flag.UPPERBOUND;
        ChessMove bestMoveSoFar = null;
        for(int i = 0;i<moves.length;i++){
            ChessMove move = moves[i];
            if(move == null){
                break;
            }
            chessPosition.makeLocalPositionMove(move);
            // move extensions
            int extension = 0;
            if(isChecked){
                extension = 1;
            }
            else if(move.getBoardIndex() == ChessConstants.PAWNINDEX && (move.getNewX() == 6 || move.getNewY() == 1)){
                extension = 1;
            }

            // move reductions
            int eval = 0;
            boolean needsFullSearch = true;
            if(extension == 0 && plyFromRoot > 3 && i > 3 && !move.isEating()){
                final int reducedDepth = 1;
                eval = -search(currentPly-1-reducedDepth,plyFromRoot+1,-alpha-1,-alpha,numExtensions);
                needsFullSearch = eval > alpha;
            }
            if(needsFullSearch){
                eval = -search(currentPly-1+extension,plyFromRoot+1,-beta,-alpha,numExtensions+extension);
            }
            chessPosition.undoLocalPositionMove();

            if(stop || checkStopSearch()){
                return 0;
            }


            // fail high means its a lowerbound, opponent can avoid this move so you might have even better unsearched
            if(eval >= beta){
                transpositionTable.recordHash(chessPosition.zobristKey,currentPly,beta,move,Flag.LOWERBOUND);

                // history + killer heuristics
                if(!move.isEating()){ // see if this is needed
                    int fromSquare = GeneralChessFunctions.positionToBitIndex(move.getOldX(),move.getOldY());
                    int toSquare = GeneralChessFunctions.positionToBitIndex(move.getNewX(),move.getNewY());
                    orderer.history[chessPosition.isWhiteTurn ? 0 :1][fromSquare][toSquare] += currentPly*currentPly;

                }
                searchInfo.incrementNumBetaCutoffs();
                return beta;
            }

            if(eval > alpha){
//                System.out.println("Alpha: " + alpha + " to -> " + eval);
                evaluation = Flag.EXACT;
                bestMoveSoFar = move;
                alpha = eval;

                if(plyFromRoot == 0){
                    bestMoveIter = move;
                    bestEvalIter = alpha;
                    hasSearchedAtLeastOneThisIter = true;
                }
            }

            if(EvaluationFunctions.isMateScore(eval)){
                break;
            }



        }

        transpositionTable.recordHash(chessPosition.zobristKey, currentPly, alpha,bestMoveSoFar,evaluation);
        return alpha;


    }

    private int quiscenceSearch(int alpha,int beta){
        if (AdvancedChessFunctions.isAnyNotMovePossible(chessPosition.isWhiteTurn, chessPosition, chessPosition.gameState)) {
            if (AdvancedChessFunctions.isChecked(chessPosition.isWhiteTurn, chessPosition.board)) {
                return EvaluationFunctions.baseMateScore;
            }
            return 0;  // Stalemate
        }

        if(stop || checkStopSearch()){
            return 0;
        }

        int evaluation = EvaluationFunctions.getStaticEvaluation(chessPosition);

        if(evaluation >= beta){
            searchInfo.incrementNumBetaCutoffs();

            return beta;
        }
        if(evaluation > alpha){
            alpha = evaluation;
        }

        ChessMove[] moves = moveGenerator.generateMoves(chessPosition, true,promotionType);
        orderer.sortMoves(null,chessPosition.board,moves,null,null);
        for(ChessMove move : moves){
            if(move == null){
                break;
            }
            chessPosition.makeLocalPositionMove(move);
            int eval = -quiscenceSearch(-beta,-alpha);
            chessPosition.undoLocalPositionMove();

            if(eval >= beta){
                searchInfo.incrementNumBetaCutoffs();
                return beta;
            }

            if(eval > alpha){
                alpha = eval;
            }
        }
        return alpha;


    }

}


