package chessengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Stockfish {
    private Process engineProcess;
    private BufferedReader engineReader ;
    private OutputStreamWriter processWriter;

    private static final String ENGINE_PATH = "stockfish/stockfish-windows-x86-64-avx2.exe";

    public boolean startEngine() {
        try {
            URL stockfishLocation = getClass().getClassLoader().getResource(ENGINE_PATH);
            File file = Paths.get(stockfishLocation.toURI()).toFile();
            ProcessBuilder processBuilder = new ProcessBuilder(file.getAbsolutePath());
            engineProcess = processBuilder.start();
            engineReader  = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getOutput(int waitTimeMillis) {
        StringBuilder output = new StringBuilder();
        try {
            // Wait for the engine to process the command
            TimeUnit.MILLISECONDS.sleep(waitTimeMillis);

            String line;
            while ((line = engineReader.readLine()) != null) {
                output.append(line).append("\n");
                if (line.contains("bestmove")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public String getOutputEval(int waitTime) {
        StringBuilder buffer = new StringBuilder();
        try {
            TimeUnit.MILLISECONDS.sleep(waitTime);
            sendCommand("isready");
            while (true) {
                String text = engineReader.readLine();
                if (text.equals("readyok")) break;
                buffer.append(text).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public String getBestMove(String fen, int timeLimitMillis) {
        sendCommand("uci");
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + timeLimitMillis);
        return getOutput(timeLimitMillis + 100); // Wait a bit longer than the time limit to ensure response
    }

    public void stopEngine() {
        try {
            sendCommand("quit");
            engineReader.close();
            processWriter.close();
            engineProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLegalMoves(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");
        return getOutput(0).split("Legal moves: ")[1];
    }

    public float getEvalScore(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        float evalScore = 0.0f;
        String[] dump = getOutput(waitTime + 20).split("\n");

        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith("info depth ")) {
                String[] parts = dump[i].split("score cp ");
                if (parts.length > 1) {
                    String scoreString = parts[1].split(" ")[0];
                    try {
                        evalScore = Float.parseFloat(scoreString);
                    } catch (NumberFormatException e) {
                        // Handle parsing error gracefully, if needed
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        return evalScore / 100;
    }





}
