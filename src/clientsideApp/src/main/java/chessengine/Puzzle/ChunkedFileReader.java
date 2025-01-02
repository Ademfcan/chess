package chessengine.Puzzle;

import java.io.*;

public class ChunkedFileReader {
    private BufferedReader reader;

    public ChunkedFileReader(String filePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IOException("File not found: " + filePath);
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));
        reader.readLine(); // read initial csv line
    }

    public String[] readChunk(int chunkLength) throws IOException {
        String[] chunk = new String[chunkLength];
        int count = 0;
        String line;

        while (count < chunkLength && (line = reader.readLine()) != null) {
            chunk[count++] = line;
        }

        // Resize the array if the chunk is smaller than requested
        if (count < chunkLength) {
            String[] resizedChunk = new String[count];
            System.arraycopy(chunk, 0, resizedChunk, 0, count);
            return resizedChunk;
        }

        return chunk;
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
