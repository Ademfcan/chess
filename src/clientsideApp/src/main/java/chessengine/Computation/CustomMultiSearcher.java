package chessengine.Computation;

import chessserver.ChessRepresentations.BackendChessPosition;
import chessengine.Enums.Movetype;
import chessserver.Functions.AdvancedChessFunctions;
import chessengine.Functions.EvaluationFunctions;
import chessserver.Functions.ComputerHelperFunctions;
import chessserver.Misc.ChessConstants;
import chessengine.Records.MultiResult;
import chessengine.Records.PVEntry;
import chessengine.Records.SearchResult;
import chessserver.Enums.ComputerDifficulty;

import java.util.List;
import java.util.concurrent.*;

public class CustomMultiSearcher extends MultiSearcher {
    public MultiResult search(BackendChessPosition position, int waitTimeMs, int nPvs, ComputerDifficulty difficulty) {
        List<BackendChessPosition> chessPositions = position.getAllChildPositions(position.isWhiteTurn, position.getGameState());
        List<BackendChessPosition> filteredPositions;
        if (difficulty.equals(ComputerDifficulty.MaxDifficulty)) {
            filteredPositions = chessPositions;
        } else {
            filteredPositions = chessPositions.stream().filter(p -> ComputerHelperFunctions.doesMoveFitRestrictions(p, difficulty)).toList();
            if (filteredPositions.isEmpty()) {
                filteredPositions = chessPositions;
            }
            double randProb = Math.random();
            // now introduce some entropy
            if (randProb < difficulty.randomnessFactor && filteredPositions.size() > difficulty.minRandomChoices) {
                int randomCutoff = ChessConstants.generalRandom.nextInt(difficulty.minRandomChoices, Math.min(filteredPositions.size() + 1,difficulty.maxRandomChoices));
                int randomOffset = ChessConstants.generalRandom.nextInt(0, filteredPositions.size() - randomCutoff + 1);
                filteredPositions = filteredPositions.subList(randomOffset, randomCutoff + randomOffset);
            }
        }
        positionsToEvaluate = new LinkedBlockingQueue<>(filteredPositions);
        ConcurrentLinkedQueue<SearchResult> outputs = new ConcurrentLinkedQueue<>();
        int timePerBatch = calculateTimePerBatch(chessPositions.size(), waitTimeMs);
//        System.out.println("Time per batch: " + timePerBatch);
        long startTimeMs = System.currentTimeMillis();
        try (ExecutorService executorService = Executors.newFixedThreadPool(numThreads)) {
            for (Searcher searcher : searchers) {
                executorService.submit(() -> {
                    try {
                        while (!positionsToEvaluate.isEmpty()) {
                            Thread.sleep(10);
                            BackendChessPosition positionToEvaluate = positionsToEvaluate.poll();
                            if (positionToEvaluate != null) {
                                if(positionToEvaluate.isDraw() || AdvancedChessFunctions.isAnyNotMovePossible(positionToEvaluate.isWhiteTurn,positionToEvaluate,positionToEvaluate.getGameState())){
                                    int evaluation = 0;
                                    if(AdvancedChessFunctions.isChecked(positionToEvaluate.isWhiteTurn,positionToEvaluate.board)){
                                        evaluation = -EvaluationFunctions.baseMateScore;
                                    }

                                    outputs.add(new SearchResult(positionToEvaluate.getMoveThatCreatedThis(), evaluation, 1, new PVEntry[]{}));

                                    continue;


                                }
                                SearchResult result = searcher.search(positionToEvaluate, timePerBatch,difficulty.depth);
                                if(result != null){
                                    PVEntry[] pvBuffer = result.pV();
                                    // reshift everything by 1
                                    int end = getRightBeforeNullIndex(pvBuffer);
                                    PVEntry[] trimmedBuffer = new PVEntry[end + 2];
                                    System.arraycopy(pvBuffer, 0, trimmedBuffer, 1, end + 1);
                                    Movetype movetype = Movetype.getMoveType(positionToEvaluate);
                                    trimmedBuffer[0] = new PVEntry(positionToEvaluate.getMoveThatCreatedThis(), -result.evaluation(), movetype);
                                    int advtg = -result.evaluation();
                                    // favorite moves
                                    if (difficulty.favoritePieceIndex != ChessConstants.EMPTYINDEX) {
                                        if (positionToEvaluate.getMoveThatCreatedThis().getBoardIndex() == difficulty.favoritePieceIndex) {
                                            advtg += (int) (15 * difficulty.favoritePieceWeight);
                                        }
                                    }

                                    outputs.add(new SearchResult(positionToEvaluate.getMoveThatCreatedThis(), advtg, result.depth() + 1, trimmedBuffer));
                                }

                            }
                        }

                    } catch (InterruptedException e) {
                        logger.error("queue interrupted", e);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                });
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e);
        }


        return processOutput(outputs, nPvs,startTimeMs,waitTimeMs);


    }
}
