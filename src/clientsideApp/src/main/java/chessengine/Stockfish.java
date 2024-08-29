package chessengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Stockfish {
    private static final String ENGINE_PATH = "stockfish/stockfish-windows-x86-64-avx2.exe";
    private static final Logger logger = LogManager.getLogger("Stockfish Logger");
    private Process engineProcess;
    private BufferedReader engineReader;
    private OutputStreamWriter processWriter;

    public boolean startEngine() {
        try {
            URL stockfishLocation = getClass().getClassLoader().getResource(ENGINE_PATH);
            File file = Paths.get(stockfishLocation.toURI()).toFile();
            ProcessBuilder processBuilder = new ProcessBuilder(file.getAbsolutePath());
            engineProcess = processBuilder.start();
            engineReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());
        } catch (Exception e) {
            logger.error("Error on engine start", e);
            return false;
        }
        return true;
    }

    private void restartProcess() {
        clearStreams();
        if (startEngine()) {
            logger.debug("Restarted stockfish succesfully");
        }

    }


    private void clearStreams() {
        if (engineReader != null && processWriter != null) {
            try {
                engineReader.close();
                processWriter.close();
                engineProcess.destroy();
            } catch (IOException e) {
                logger.error("Exception on engine reader close", e);
            }
        }
    }

    private void checkProcess() {
        if (!engineProcess.isAlive()) {
            logger.debug("stockfish process closed, starting up a new one");
            restartProcess();
        }

    }

    public void sendCommand(String command) {
        checkProcess();
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (Exception e) {
            logger.error("Error on send command", e);
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
            logger.error("Error on get output", e);
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
            logger.error("Error on get output eval", e);
        }
        return buffer.toString();
    }

    public String getBestMove(String fen, int stockfishElo,int timeLimitMillis) {
        sendCommand("uci");
        sendCommand("setoption name UCI_Elo value " + stockfishElo);
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + timeLimitMillis);
        String out = getOutput(timeLimitMillis);
        String[] s = out.split("\n");
        String last = s[s.length - 1];
        return last.split("bestmove ")[1].split(" ")[0];
    }

    public void stopEngine() {
        try {
            sendCommand("quit");
            engineReader.close();
            processWriter.close();
            engineProcess.destroy();
        } catch (Exception e) {
            logger.error("Error on stopEngine", e);
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
                        logger.error("Error parsing eval", e);
                    }
                    break;
                }
            }
        }

        return evalScore / 100;
    }


}
