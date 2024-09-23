package chessengine.Computation;

public class SearchInfoAggregator {
    int numBetaCutoffs;
    int uniquePositionsSearched;
    int numTranspositionUses;
    public SearchInfoAggregator(){
    }

    public void clear(){
        numBetaCutoffs = 0;
        uniquePositionsSearched = 0;
        numTranspositionUses = 0;
    }

    public int getNumBetaCutoffs() {
        return numBetaCutoffs;
    }

    public void incrementNumBetaCutoffs() {
        this.numBetaCutoffs++;
    }

    public int getUniquePositionsSearched() {
        return uniquePositionsSearched;
    }

    public void incrementUniquePositionsSearched() {
        this.uniquePositionsSearched++;
    }

    public int getNumTranspositionUses() {
        return numTranspositionUses;
    }

    public void incrementNumTranspositionUses() {
        this.numTranspositionUses++;
    }

}
