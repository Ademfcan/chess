package chessengine;

import chessengine.Puzzle.PuzzleEntry;
import chessengine.Puzzle.PuzzleManager;
import chessengine.Puzzle.PuzzleReader;
import chessengine.Puzzle.SequentialList;
import chessserver.ChessRepresentations.ChessGame;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PuzzleReaderTests {
    @Test
    void testBinnedReading(){
        PuzzleReader reader = new PuzzleReader();
        Map<Integer, SequentialList<PuzzleEntry>> binnedBatch = reader.readNewBatchBinned(1000000,50,2000);
        int[] values = new int[3200/50];
        for(int key : binnedBatch.keySet()){
            values[key/50] = binnedBatch.get(key).size();
        }
        System.out.println(Arrays.toString(values));
    }

    @Test
    void testCreateGame(){
        PuzzleReader reader = new PuzzleReader();
        Map<Integer, SequentialList<PuzzleEntry>> binnedBatch = reader.readNewBatchBinned(1000000,50,2000);
        int cnt = 0;
        for(int key : binnedBatch.keySet()){
            for(PuzzleEntry p : binnedBatch.get(key)){
                ChessGame g = p.getPuzzleGame();
                System.out.println(++cnt);
            }
        }

    }

    @Test
    void getSequentialGames(){
        PuzzleReader reader = new PuzzleReader();
        Map<Integer, SequentialList<PuzzleEntry>> binnedBatch = reader.readNewBatchBinned(1000000,50,2000);
        for(int key : binnedBatch.keySet()){
            SequentialList<PuzzleEntry> list = binnedBatch.get(key);
            int cnt = list.size();
            while(cnt-- > 0){
                ChessGame g = list.getNext().getPuzzleGame();
            }
        }
    }

    @Test
    void managerGetGames(){
        PuzzleManager manager = new PuzzleManager();
        for(int i = 0;i<3400;i++){
            System.out.println(manager.getNewPuzzle(i));
        }
    }
}
