package chessengine.Functions;

import chessengine.Misc.ChessTrie;
import javafx.scene.paint.Paint;
import org.nd4j.shade.protobuf.MapEntry;

import java.util.HashMap;
import java.util.Map;

public class LineLabeler {
    private final ChessTrie trie;

    private final String[][] parsedMoves = {
            {"e4", "e5", "Nf3", "Nc6", "Bb5", "Ruy Lopez"},
            {"e4", "e5", "Nf3", "Nc6", "d4", "exd4", "Nxd4", "Four Knights Game"},
            {"e4", "c5", "Sicilian Defense"},
            {"d4", "d5", "c4", "Queen's Gambit"},
            {"d4", "d5", "c4", "e6", "Queen's Gambit Declined"},
            {"e4", "c6", "Caro-Kann Defense"},
            {"e4", "e5", "Nf3", "d6", "Philidor Defense"},
            {"e4", "e5", "Nf3", "Nc6", "d4", "exd4", "Nxd4", "Nf6", "Two Knights Defense"},
            {"d4", "d5", "c4", "c6", "Slav Defense"},
            {"e4", "e5", "Nf3", "d6", "d4", "Bg4", "dxe5", "Bxf3", "Qxf3", "dxe5", "Open Philidor Defense"},
            {"e4", "c5", "Nf3", "d6", "Sicilian Defense: Najdorf Variation"},
            {"e4", "e5", "Nf3", "Nc6", "Bc4", "Nf6", "Italian Game"},
            {"e4", "e5", "Nf3", "Nc6", "d4", "exd4", "Nxd4", "Nf6", "Nc3", "Italian Game: Knight Variation"},
            {"d4", "d5", "c4", "e6", "Nc3", "Nf6", "Queen's Gambit Declined: Classical Variation"},
            {"e4", "e5", "Nf3", "d6", "d4", "Bg4", "dxe5", "Bxf3", "Qxf3", "Open Philidor Defense"},
            {"d4", "d5", "c4", "c6", "Nf3", "dxc4", "Slav Defense: Exchange Variation"},
            {"e4", "e5", "Nf3", "d6", "d4", "Bg4", "dxe5", "Bxf3", "Qxf3", "dxe5", "Bc4", "Philidor Defense: Modern Variation"},
            {"e4", "e5", "Nf3", "Nc6", "Bb5", "a6", "Ruy Lopez: Morphy Defense"},
            {"e4", "e5", "Nf3", "d6", "d4", "Bg4", "Philidor Defense: Lasker Variation"},
            {"e4", "e5", "Nf3", "d6", "d4", "Bg4", "dxe5", "dxe5", "Open Philidor Defense"},
            {"e4", "e5", "Nf3", "Nc6", "Bc4", "Nf6", "d3", "Be7", "Italian Game: Classical Variation"},
            {"e4", "e5", "Nf3", "Nc6", "Bb5", "a6", "Ba4", "Nf6", "Ruy Lopez: Exchange Variation"},
            {"e4", "e5", "Nf3", "Nc6", "d4", "exd4", "Nxd4", "Nf6", "Nc3", "Be7", "Italian Game: Classical Variation"},
            {"d4", "d5", "c4", "e6", "Nf3", "Nf6", "g3", "Queen's Gambit Declined: Fianchetto Variation"},
            {"d4", "d5", "c4", "e6", "Nf3", "Nf6", "g3", "Be7", "Queen's Gambit Declined: Fianchetto Variation"},
            {"e4", "c5", "Nf3", "d6", "d4", "cxd4", "Nxd4", "Nf6", "Nc3", "a6", "Sicilian Defense: Najdorf Variation"},
            {"e4", "e5", "Nf3", "d6", "d4", "Bg4", "dxe5", "dxe5", "Qxd8+", "Rxd8", "Open Philidor Defense: Modern Variation"},
            {"e4", "e5", "Nf3", "Nc6", "Bb5", "a6", "Ba4", "Nf6", "O-O", "Be7", "Ruy Lopez: Classical Variation"}
    };

    public LineLabeler(){
        trie = new ChessTrie();
        for(String[] line : parsedMoves){
            trie.addNewLine(line);
        }
    }



    public String getLineName(String[] lineAsPgn){
        return trie.getLineName(lineAsPgn);
    }
}
