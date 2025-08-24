package chessengine.Puzzle;

import chessengine.TriggerRegistry;
import chessengine.Triggers.Closable;

import java.util.Map;

public class PuzzleManager implements Closable {
    private final int binSize = 50;
    private final int maxBinLen = 2000;
    private final int batchSize = (int) 1e6;
    private final PuzzleReader reader = new PuzzleReader();
    private final Map<Integer, RandomList<PuzzleEntry>> puzzleMap;
    public PuzzleManager() {
        TriggerRegistry.addTriggerable(this);
        puzzleMap = reader.readNewBatchBinned(batchSize, binSize, maxBinLen);
    }

    public int getBinSize() {
        return binSize;
    }

    public PuzzleEntry getNewPuzzle(int elo) {
        int key = (elo / binSize) * binSize;
        if (puzzleMap.containsKey(key)) {
            return puzzleMap.get(key).getNext(); // can be null if you try to get a invalid puzzle el
        } else {
            return null;
        }
    }

    public int getMinBin() {
        int min = Integer.MAX_VALUE;
        for (int key : puzzleMap.keySet()) {
            if (key < min) {
                min = key;
            }
        }
        return min;
    }

    public int getMaxBin() {
        int max = Integer.MIN_VALUE;
        for (int key : puzzleMap.keySet()) {
            if (key > max) {
                max = key;
            }
        }
        return max;
    }

    @Override
    public void onClose() {
        System.out.println("b");
        reader.close();

        System.out.println("Reader closed.");
    }
}
