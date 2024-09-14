package chessengine.Computation;

import chessengine.ChessRepresentations.BitBoardWrapper;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Functions.PgnFunctions;
import chessengine.Misc.ChessConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Stockfish {
    private static final String ENGINE_PATH = "stockfish/stockfish-windows-x86-64-avx2.exe";
    private static final Logger logger = LogManager.getLogger("Stockfish Logger");

    private boolean isCalling = false;

    public boolean isCalling() {
        return isCalling;
    }
    public volatile AtomicBoolean stop = new AtomicBoolean(false);
    private Process engineProcess;
    private BufferedReader engineReader;
    private OutputStreamWriter processWriter;

    public boolean startEngine() {
        try {
            URL stockfishLocation = getClass().getClassLoader().getResource(ENGINE_PATH);
            if (stockfishLocation == null) {
                logger.error("Stockfish executable not found at " + ENGINE_PATH);
                return false;
            }

            // Create a temporary file to extract the executable
            File tempFile = File.createTempFile("stockfish", ".exe");
            try (InputStream in = stockfishLocation.openStream();
                 OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }

            tempFile.deleteOnExit(); // Ensure the file is deleted on exit

            ProcessBuilder processBuilder = new ProcessBuilder(tempFile.getAbsolutePath());
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
            for(int i = 0;i<waitTimeMillis;i++){
                TimeUnit.MILLISECONDS.sleep(1);
                if(stop.get()){
                    stop.set(false);
                    return null;
                }
            }
            // Wait for the engine to process the command

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

    public String getBestMove(String fen, int stockfishElo, int timeLimitMillis) {
        isCalling = true;
        sendCommand("uci");
        if (stockfishElo <= 3200) {
            sendCommand("setoption name UCI_LimitStrength value true");
            sendCommand("setoption name UCI_Elo value " + stockfishElo);
        }
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + timeLimitMillis);
        String out = getOutput(timeLimitMillis);
        isCalling = false;
        if(out == null){
            return null;
        }
        String[] s = out.split("\n");
        String last = s[s.length - 1];
        return last.split("bestmove ")[1].split(" ")[0];
    }

    public MoveOutput[] getBestNMoves(String fen, boolean isWhite, BitBoardWrapper board, int timeLimitMillis, int nMoves) {
        isCalling = true;
        sendCommand("uci");
        sendCommand("position fen " + fen);
        sendCommand("setoption name MultiPV value " + nMoves);
        sendCommand("go movetime " + timeLimitMillis);
        String uciOut = getOutput(timeLimitMillis);
        isCalling = false;
        if(uciOut == null){
            return null;
        }
        String[] split = uciOut.split("\n");
        MoveOutput[] ret = new MoveOutput[nMoves];
        if (split.length > 5) {
            int cnt = nMoves - 1;
            for (int i = split.length - 2; i >=0; i--) {
                String[] evalSplit = split[i].split(" ");

                if(split.length >= minStockfishLen) {
                    ChessMove move = null;
                    int evalScore = 0;
                    int depth = 0;
                    for(int j = 0;j<evalSplit.length-1;j++ ){
                        String entry = evalSplit[j];
                        String next = evalSplit[j+1];
                        switch (entry) {
                            case "depth" -> depth = Integer.parseInt(next);
                            case "pv" -> {
                                String moveUci = next;
                                move = PgnFunctions.uciToChessMove(moveUci, isWhite, board);
                            }
                            case "cp" -> {
                                if (next.equals("move")) {
                                    int mateInfo = Integer.parseInt(evalSplit[j + 2]);
                                    evalScore = mateInfo > 0 ? ChessConstants.WHITECHECKMATEVALUE : ChessConstants.BLACKCHECKMATEVALUE;
                                    depth = Math.abs(mateInfo);
                                } else {
                                    evalScore = Integer.parseInt(next);
                                }
                            }
                        }
                    }
                    ret[cnt] = new MoveOutput(move,(double)evalScore/100*(isWhite ? 1 : -1),depth);
                    cnt--;
                    if(cnt < 0){
                        break;
                    }

                }


            }
        } else {
            logger.error("Not enough lines out of stockfish best n moves");
        }

        return ret;

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

    private final int minStockfishLen = 23;


    public EvalOutput getEvalScore(String fen,boolean isWhite, int waitTime) {
        isCalling = true;
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);
        int depth = 0;
        float evalScore = 0.0f;
        String out = getOutput(waitTime);
        isCalling = false;
        if(out == null){
            return null;
        }
        String[] dump = out.split("\n");
        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith("info depth ")) {
                String focus = dump[i];
                String[] split = focus.split(" ");
                for(int j = 0;j<split.length-1;j++ ){
                    String entry = split[j];
                    String next = split[j+1];
                    if(entry.equals("depth")){
                        depth = Integer.parseInt(next);
                    }
                    else if(entry.equals("cp")){
                        if(next.equals("move")){
                            int mateInfo = Integer.parseInt(split[j + 2]);
                            evalScore = mateInfo > 0 ? ChessConstants.WHITECHECKMATEVALUE : ChessConstants.BLACKCHECKMATEVALUE;
                            depth = Math.abs(mateInfo);
                        }
                        else{
                            evalScore = Integer.parseInt(next);
                        }
                    }
                    if(evalScore != 0 && depth != 0){
                        break;
                    }
                }
            }
        }

        return new EvalOutput(evalScore / 100 * (isWhite ? 1 : -1), depth);
    }



}
