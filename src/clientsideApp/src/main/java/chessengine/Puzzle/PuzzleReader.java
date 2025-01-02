package chessengine.Puzzle;

import chessengine.Misc.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class PuzzleReader {
    private static final Logger logger = LogManager.getLogger("Puzzle_Reader");
    private ChunkedFileReader fileReader;
    public PuzzleReader(){
        createFileReader();
    }

    private void createFileReader(){
        try{
            fileReader = new ChunkedFileReader(Constants.puzzleFileLocation);
        }
        catch (IOException e){
            logger.error("Failed to read from puzzles!",e);
        }
    }

    /**Returns a binned map of puzzle entries, where each bin is populated by the entries elo.
     * The map will contain the lowerbound of each bin and up to lowerbound+binSize-1
     * Each list of puzzleentries will at most be binCap;
     * **/
    public Map<Integer, SequentialList<PuzzleEntry>> readNewBatchBinned(int batchSize,int binSize,int binCap){
        return readNewBatchBinned(batchSize,binSize,binCap,false);
    }

    private Map<Integer, SequentialList<PuzzleEntry>> readNewBatchBinned(int batchSize,int binSize,int binCap,boolean isRetry){
        if(fileReader != null){
            try {
                Map<Integer,SequentialList<PuzzleEntry>> binnedMap = new HashMap<>();
                int numFailed = 0;
                String[] chunk = fileReader.readChunk(batchSize);
                for(String puzzleLine : chunk){
                    PuzzleEntry puzzleEntry = PuzzleEntry.getFromCSV(puzzleLine);
                    if(puzzleEntry == null){
                        numFailed++;
                        continue;
                    }
                    // todo factor in stddev of puzzle
                    int binIdx = (puzzleEntry.puzzleRating/binSize)*binSize; // int cast will get lower bound
                    SequentialList<PuzzleEntry> entryList = binnedMap.getOrDefault(binIdx,new SequentialList<>());
                    if(entryList.size() < binCap){
                        entryList.add(puzzleEntry);
                    }
                    binnedMap.put(binIdx,entryList);
                }
                logger.info(String.format("Read chunk of length %d with %d failed lines",chunk.length,numFailed));
                return binnedMap;
            }
            catch (IOException e){
                logger.error("Failed to read from puzzles!",e);
            }
        }
        else{
            if(!isRetry){
                logger.warn("Filereader null, creating new one!");
                createFileReader();
                readNewBatchBinned(batchSize,binSize,binCap,true);
            }
            else{
                logger.warn("Not able to create filereader!");
            }
        }
        return null;

    }

    public void close(){
        try {
            fileReader.close();
        } catch (IOException e) {
            logger.error("Failed to close puzzle reader!",e);
        }
    }
}
