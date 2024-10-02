package chessengine.Computation;

import chessengine.ChessRepresentations.BackendChessPosition;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Enums.Movetype;
import chessengine.Records.CachedPv;
import chessengine.Records.MultiResult;
import chessengine.Records.PVEntry;
import chessengine.Records.SearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class MultiSearcher {
    protected final Logger logger = LogManager.getLogger(this.toString());
    protected final int numThreads;
    protected final Set<Searcher> searchers;
    protected LinkedBlockingQueue<BackendChessPosition> positionsToEvaluate;

    public MultiSearcher() {
        numThreads = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors() / 2, 8));
        searchers = new HashSet<>();
        for (int i = 0; i < numThreads; i++) {
            searchers.add(new Searcher());
        }
    }

    public void stopSearch() {
        for (Searcher s : searchers) {
            s.stopSearch();
        }
    }

    public boolean wasForcedStop() {
        for (Searcher s : searchers) {
            if (s.wasForcedStop()) {
                return true;
            }
        }
        return false;
    }

    public MultiResult search(BackendChessPosition position, int waitTimeMs, int nPvs) {
        List<BackendChessPosition> chessPositions = position.getAllChildPositions(position.isWhiteTurn, position.gameState);
        positionsToEvaluate = new LinkedBlockingQueue<>(chessPositions);
        ConcurrentLinkedQueue<SearchResult> outputs = new ConcurrentLinkedQueue<>();
        int timePerBatch = calculateTimePerBatch(chessPositions.size(), waitTimeMs);
//        System.out.println("Time per batch: " + timePerBatch);
        try (ExecutorService executorService = Executors.newFixedThreadPool(numThreads)) {
            for (Searcher searcher : searchers) {
                executorService.submit(() -> {
                    try {
                        while (!positionsToEvaluate.isEmpty()) {
                            Thread.sleep(10);
                            BackendChessPosition positionToEvaluate = positionsToEvaluate.poll();
                            if (positionToEvaluate != null) {
                                SearchResult result = searcher.search(positionToEvaluate, timePerBatch);
                                PVEntry[] pvBuffer = result.pV();
                                // reshift everything by 1
                                int end = getNullIndex(pvBuffer);
                                PVEntry[] trimmedBuffer = new PVEntry[end + 2];
                                Movetype movetype = Movetype.getMoveType(positionToEvaluate);
                                System.arraycopy(pvBuffer, 0, trimmedBuffer, 1, end + 1);
                                trimmedBuffer[0] = new PVEntry(positionToEvaluate.getMoveThatCreatedThis(), -result.evaluation(), movetype);
                                outputs.add(new SearchResult(positionToEvaluate.getMoveThatCreatedThis(), -result.evaluation(), result.depth() + 1, trimmedBuffer));
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


        return processOutput(outputs, nPvs);


    }

    protected int getNullIndex(PVEntry[] pv) {
        int index = 0;
        while (index < pv.length) {
            if (pv[index] == null) {
                return index - 1;
            }
            index++;
        }
        return index;
    }

    protected int calculateTimePerBatch(int nPositions, int waitTimeMs) {
        return (int) (waitTimeMs * (numThreads / (double) nPositions));
    }

    protected MultiResult processOutput(ConcurrentLinkedQueue<SearchResult> results, int nPvs) {
        HashMap<ChessMove, CachedPv> moveEvals = new HashMap<>(results.size());
//        PriorityQueue<Integer> minBound = new PriorityQueue<>(nPvs+1);
//        minBound.add(Integer.MIN_VALUE+1);
//        PvBuffer buffer = new PvBuffer(nPvs);
//        for(SearchResult searchResult : results){
//            int eval = searchResult.evaluation();
//            moveEvals.put(searchResult.move(),eval);
//            if(eval > minBound.peek()){
//                buffer.putResult(searchResult);
//                minBound.poll();
//                minBound.add(eval);
//            }
//
//        }
        List<SearchResult> sorted = results.stream().sorted(Comparator.comparingInt(SearchResult::evaluation)).toList();
        int cnt = Math.min(nPvs, sorted.size());
        SearchResult[] out = new SearchResult[cnt];
//        System.out.println(sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            SearchResult result = sorted.get(i);
            moveEvals.put(result.move(), new CachedPv(result.evaluation(), result.pV()));
            if (i >= sorted.size() - nPvs) {
                out[--cnt] = result;
            }
        }
        return new MultiResult(out, moveEvals);


    }
}
