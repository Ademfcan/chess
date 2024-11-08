package chessengine.Computation;

import chessserver.ChessRepresentations.ChessMove;
import chessengine.Enums.Flag;
import chessserver.Misc.ChessConstants;
import chessengine.Records.HashEntry;

public class TranspositionTable {
    HashEntry markedIndex1 = null;
    HashEntry markedIndex2 = null;
    private final HashEntry[] alwaysReplaceTable;
    private final HashEntry[] conditionalyReplaceTable;
    private final int maxEntries;

    public TranspositionTable(int maxEntries) {
        this.maxEntries = maxEntries;
        alwaysReplaceTable = new HashEntry[maxEntries];
        conditionalyReplaceTable = new HashEntry[maxEntries];
    }

    public ChessMove getMarkedMove1() {
        return markedIndex1 != null ? markedIndex1.bestMove() : null;
    }

    public ChessMove getMarkedMove2() {
        return markedIndex2 != null ? markedIndex2.bestMove() : null;
    }

    public void recordHash(long zobristKey, int depth, int val, ChessMove bestMove, Flag f) {
        int key = (int) (zobristKey % maxEntries);
        if (key < 0) {
            key += maxEntries;
        }
        HashEntry hashEntry = new HashEntry(zobristKey, depth, f, val, bestMove);
        alwaysReplaceTable[key] = hashEntry;
        if (conditionalyReplaceTable[key] == null || conditionalyReplaceTable[key].depth() <= depth) {
            conditionalyReplaceTable[key] =
                    hashEntry;
        }
    }

    public int probeHash(long zobristKey, int depth, int alpha, int beta) {
        int key = (int) (zobristKey % maxEntries);
        if (key < 0) {
            key += maxEntries;
        }

        HashEntry alwaysReplaceEntry = alwaysReplaceTable[key];
        HashEntry conditionalyReplaceEntry = conditionalyReplaceTable[key];
        if (alwaysReplaceEntry == null) {
            // both must be null
            return ChessConstants.NONE;
        }

        if (conditionalyReplaceEntry != null) {
            if (zobristKey == conditionalyReplaceEntry.zobristKey() && conditionalyReplaceEntry.depth() >= depth) {
                markedIndex2 = conditionalyReplaceEntry;
                int value = conditionalyReplaceEntry.value();
                switch (conditionalyReplaceEntry.flag()) {
                    case EXACT -> {
                        return value;
                    }
                    case LOWERBOUND -> {
                        if (value >= beta) {
                            return beta;
                        }
                    }
                    case UPPERBOUND -> {
                        if (value <= alpha) {
                            return alpha;
                        }
                    }
                }
            }

        }
        if (zobristKey == alwaysReplaceEntry.zobristKey() && alwaysReplaceEntry.depth() >= depth) {
            markedIndex1 = alwaysReplaceEntry;
            int value = alwaysReplaceEntry.value();
            switch (alwaysReplaceEntry.flag()) {
                case EXACT -> {
                    return value;
                }
                case LOWERBOUND -> {
                    if (value >= beta) {
                        return beta;
                    }
                }
                case UPPERBOUND -> {
                    if (value <= alpha) {
                        return alpha;
                    }
                }
            }
        }
        return ChessConstants.NONE;
    }

    public void clearProbe() {
        markedIndex1 = null;
        markedIndex2 = null;
    }

}


