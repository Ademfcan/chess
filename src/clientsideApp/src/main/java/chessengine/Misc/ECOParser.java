package chessengine.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ECOParser {

    public record ECOEntry(String ecoCode, String openingName, String variation, String pgn) {}

    public static List<ECOEntry> parseEcoFile(String path, int skipLines) throws IOException {
        List<ECOEntry> entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // Skip header
            for (int i = 0; i < skipLines; i++) {
                reader.readLine();
            }

            String line;
            String ecoCode = null;
            String openingName = null;
            String variation = null;
            StringBuilder pgnBuilder = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("[ECO ")) {
                    ecoCode = extractQuoted(line);
                } else if (line.startsWith("[Opening ")) {
                    openingName = extractQuoted(line);
                } else if (line.startsWith("[Variation ")) {
                    variation = extractQuoted(line);
                } else if (line.isEmpty()) {
                    // Start reading PGN
                    pgnBuilder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.endsWith("*")) {
                            pgnBuilder.append(line, 0, line.length() - 1).append(" ");
                            break;
                        } else {
                            pgnBuilder.append(line).append(" ");
                        }
                    }
                    if (ecoCode != null && openingName != null && pgnBuilder != null) {
                        entries.add(new ECOEntry(ecoCode, openingName, variation, pgnBuilder.toString().trim()));
                    }
                    ecoCode = null;
                    openingName = null;
                    variation = null;
                    pgnBuilder = null;
                }
            }
        }

        return entries;
    }

    private static String extractQuoted(String line) {
        int start = line.indexOf('"') + 1;
        int end = line.lastIndexOf('"');
        return (start > 0 && end > start) ? line.substring(start, end) : null;
    }

    // Example usage:
    public static void main(String[] args) throws IOException {
        List<ECOEntry> entries = parseEcoFile("eco.pgn", 2); // Adjust skipLines as needed
        for (ECOEntry entry : entries) {
            System.out.println(entry);
        }
    }
}
