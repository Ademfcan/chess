package chessengine.Functions;

import javafx.scene.paint.Paint;
import org.nd4j.shade.protobuf.MapEntry;

import java.util.HashMap;
import java.util.Map;

public class LineLabeler {
    private static final Map<String, String> lineToName = Map.ofEntries(
            // Previous entries
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.Bb5", "Ruy Lopez"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.d4 exd4 4.Nxd4", "Four Knights Game"),
            Map.entry("1.e4 c5", "Sicilian Defense"),
            Map.entry("1.d4 d5 2.c4", "Queen's Gambit"),
            Map.entry("1.d4 d5 2.c4 e6", "Queen's Gambit Declined"),
            Map.entry("1.e4 c6", "Caro-Kann Defense"),
            Map.entry("1.e4 e5 2.Nf3 d6", "Philidor Defense"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.d4 exd4 4.Nxd4 Nf6", "Two Knights Defense"),
            Map.entry("1.d4 d5 2.c4 c6", "Slav Defense"),
            Map.entry("1.e4 e5 2.Nf3 d6 3.d4 Bg4 4.dxe5 Bxf3 5.Qxf3 dxe5", "Open Philidor Defense"),
            Map.entry("1.e4 c5 2.Nf3 d6", "Sicilian Defense: Najdorf Variation"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6", "Italian Game"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.d4 exd4 4.Nxd4 Nf6 5.Nc3", "Italian Game: Knight Variation"),
            Map.entry("1.d4 d5 2.c4 e6 3.Nc3 Nf6", "Queen's Gambit Declined: Classical Variation"),

            // Additional entries
            Map.entry("1.e4 e5 2.Nf3 d6 3.d4 Bg4 4.dxe5 Bxf3 5.Qxf3", "Open Philidor Defense"),
            Map.entry("1.d4 d5 2.c4 c6 3.Nf3 dxc4", "Slav Defense: Exchange Variation"),
            Map.entry("1.e4 e5 2.Nf3 d6 3.d4 Bg4 4.dxe5 Bxf3 5.Qxf3 dxe5 6.Bc4", "Philidor Defense: Modern Variation"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.Bb5 a6", "Ruy Lopez: Morphy Defense"),
            Map.entry("1.e4 e5 2.Nf3 d6 3.d4 Bg4", "Philidor Defense: Lasker Variation"),
            Map.entry("1.e4 e5 2.Nf3 d6 3.d4 Bg4 4.dxe5 dxe5", "Open Philidor Defense"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.Bc4 Nf6 4.d3 Be7", "Italian Game: Classical Variation"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6", "Ruy Lopez: Exchange Variation"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.d4 exd4 4.Nxd4 Nf6 5.Nc3 Be7", "Italian Game: Classical Variation"),
            Map.entry("1.d4 d5 2.c4 e6 3.Nf3 Nf6 4.g3", "Queen's Gambit Declined: Fianchetto Variation"),
            Map.entry("1.d4 d5 2.c4 e6 3.Nf3 Nf6 4.g3 Be7", "Queen's Gambit Declined: Fianchetto Variation"),
            Map.entry("1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6", "Sicilian Defense: Najdorf Variation"),
            Map.entry("1.e4 e5 2.Nf3 d6 3.d4 Bg4 4.dxe5 dxe5 5.Qxd8+ Rxd8", "Open Philidor Defense: Modern Variation"),
            Map.entry("1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7", "Ruy Lopez: Classical Variation")
    );


    public static String getLineName(String lineAsPgn){
        for(String key : lineToName.keySet()){
            if(key.contains(lineAsPgn)){
                return lineToName.get(key);
            }
        }
        return "";
    }
}
