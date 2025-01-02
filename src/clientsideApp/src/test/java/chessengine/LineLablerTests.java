package chessengine;

import chessengine.Functions.LineLabeler;
import chessengine.Misc.ChessTrie;
import org.junit.jupiter.api.Test;

public class LineLablerTests {
    @Test
    void testLabling(){
        LineLabeler labeler = new LineLabeler();
        System.out.println(labeler.getLineName(new String[]{"e4","c6"}));
    }

    @Test
    void testAddingALine(){
        ChessTrie trie = new ChessTrie();
        trie.addNewLine(new String[]{"e4", "e5", "Nf3", "Nc6", "Bb5", "Ruy Lopez"});
    }
}
