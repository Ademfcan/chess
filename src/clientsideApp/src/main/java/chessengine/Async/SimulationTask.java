package chessengine.Async;

import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Computation.Computer;
import chessengine.Misc.EloEstimator;
import chessengine.Enums.MainScreenState;
import chessengine.Computation.Stockfish;
import chessserver.ComputerDifficulty;
import chessserver.ProfilePicture;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class SimulationTask extends Task<Void> {

    private final int testStockfishElo = 1500; // todo make changeable in gui maybe?

    private final Computer player1Computer;
    private final Computer player2Computer;
    private final ChessCentralControl control;
    public final Stockfish stockfish;
    private final Random random;
    private final Logger logger;
    private final String[] top50Openings = {
            "1. e4 e5 2. Nf3 Nc6 3. Bb5", // Ruy Lopez
            "1. d4 d5 2. c4", // Queen's Gambit
            "1. e4 c5", // Sicilian Defense
            "1. e4 e6", // French Defense
            "1. d4 Nf6 2. c4 g6", // King's Indian Defense
            "1. d4 d5 2. Nf3 Nf6 3. c4", // Slav Defense
            "1. e4 c6", // Caro-Kann Defense
            "1. d4 Nf6 2. c4 e6 3. Nf3 b6", // Queen's Indian Defense
            "1. d4 Nf6 2. c4 e6 3. Nc3 Bb4", // Nimzo-Indian Defense
            "1. c4", // English Opening
            "1. Nf3", // Reti Opening
            "1. e4 d6", // Pirc Defense
            "1. d4 Nf6 2. c4 g6 3. Nc3 d5", // Grunfeld Defense
            "1. e4 d5", // Scandinavian Defense
            "1. e4 c5 2. Nf3 Nc6 3. d4 cxd4 4. Nxd4", // Open Sicilian
            "1. e4 c5 2. Nf3 d6", // Sicilian Defense, Najdorf Variation
            "1. d4 f5", // Dutch Defense
            "1. e4 e5 2. Nf3 Nc6 3. Bc4", // Italian Game
            "1. d4 Nf6 2. c4 e6 3. Nf3 d5", // Queen's Gambit Declined
            "1. e4 e5 2. Nf3 Nc6 3. d4", // Scotch Game
            "1. d4 Nf6 2. c4 c5", // Benoni Defense
            "1. e4 g6", // Modern Defense
            "1. e4 c5 2. Nf3 Nc6", // Sicilian Defense, Open Variations
            "1. d4 e6", // Queen's Pawn Opening
            "1. e4 c5 2. Nf3 e6", // Sicilian Defense, Paulsen Variation
            "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6", // Ruy Lopez, Morphy Defense
            "1. e4 e5 2. Nf3 d6", // Philidor Defense
            "1. d4 d5 2. c4 c6", // Slav Defense, Main Line
            "1. e4 e5 2. Nf3 Nc6 3. Bb5 Nf6", // Ruy Lopez, Berlin Defense
            "1. d4 Nf6 2. c4 e6", // Indian Game
            "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 Nf6 5. Nc3 a6", // Sicilian Defense, Najdorf Variation
            "1. d4 Nf6 2. c4 g6 3. Nc3 Bg7 4. e4 d6", // King's Indian Defense, Classical Variation
            "1. e4 c6 2. d4 d5", // Caro-Kann Defense, Main Line
            "1. d4 Nf6 2. c4 e6 3. Nf3 b6", // Queen's Indian Defense, Main Line
            "1. e4 c5 2. Nf3 e6 3. d4 cxd4 4. Nxd4 a6", // Sicilian Defense, Kan Variation
            "1. d4 d5 2. c4 c6 3. Nc3 Nf6 4. e3", // Slav Defense, Main Line
            "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 Nf6", // Sicilian Defense, Open Variations
            "1. e4 e5 2. Nf3 d6 3. d4 exd4 4. Nxd4", // Philidor Defense, Exchange Variation
            "1. d4 Nf6 2. c4 e6 3. Nf3 b6", // Queen's Indian Defense, Main Line
            "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4", // Sicilian Defense, Open
            "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7", // Ruy Lopez, Closed
            "1. d4 Nf6 2. c4 g6 3. Nc3 Bg7", // King's Indian Defense
            "1. e4 c5 2. Nf3 Nc6 3. d4", // Sicilian Defense, Open Variations
            "1. e4 e5 2. Nf3 Nc6 3. d4 exd4 4. Nxd4 Nf6", // Scotch Game, Main Line
            "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5", // Italian Game, Giuoco Piano
            "1. d4 Nf6 2. c4 e6 3. Nf3 d5 4. Nc3 c6", // Semi-Slav Defense
            "1. e4 e5 2. Nf3 Nc6 3. d4 exd4 4. Nxd4 Bc5", // Scotch Game, Classical Variation
            "1. d4 d5 2. Nf3", // Queen's Pawn Game
            "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 Nf6 5. Nc3", // Sicilian Defense, Open
            "1. e4 c5 2. Nf3 d6 3. d4", // Sicilian Defense, Open Variations
            "1. d4 d5 2. Nf3 Nf6 3. c4", // Queen's Gambit Declined
            "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6", // Ruy Lopez, Berlin Defense
    };
    private volatile boolean running = true;
    private volatile boolean evaluating = false;
    private boolean isMakingMove = false;
    private boolean isPlayer1WhitePlayer = true; // starts true
    private boolean isPlayer1Turn = true; // starts true
    private int numComputerWins = 0;
    private int numStockFishWins = 0;
    private int numDraws = 0;
    private ChessGame currentSimGame = null;

    public SimulationTask(ChessCentralControl control) {
        this.logger = LogManager.getLogger(this.toString());
        this.player1Computer = new Computer(1);
        this.player2Computer = new Computer(1);
        this.player1Computer.setCurrentDifficulty(ComputerDifficulty.MAXDIFFICULTY);
        this.player2Computer.setCurrentDifficulty(ComputerDifficulty.STOCKFISHLOL); // defaults
        this.control = control;
        stockfish = new Stockfish();
        if (stockfish.startEngine()) {
            logger.debug("Started stockfish succesfully");
        } else {
            logger.error("Stockfish start failed");
        }
        random = new Random();

    }

    public boolean isEvaluating() {
        return evaluating;
    }

    public boolean isMakingMove() {
        return isMakingMove;
    }

    public void setPlayer1SimulationDifficulty(ComputerDifficulty diff) {
        player1Computer.setCurrentDifficulty(diff);
    }
    public void setPlayer2SimulationDifficulty(ComputerDifficulty diff) {
        player2Computer.setCurrentDifficulty(diff);
    }

    public void startSimulation() {
        logger.debug("Starting sim");
        System.out.println("is already evaluating? " + evaluating);
        evaluating = true;
    }

    public void toggleSimulation() {
        evaluating = !evaluating;

    }

    private volatile boolean stop = false;

    public void stopAndReset() {
        stop = true;
        logger.debug("Stopping sim");
        evaluating = false;
        if (player1Computer.isRunning()) {
            player1Computer.stop.set(true);
        }
        isPlayer1WhitePlayer = true;
        isPlayer1Turn = true;
        currentSimGame = null;
        numComputerWins = 0;
        numStockFishWins = 0;
        numDraws = 0;
    }

    @Override
    public Void call() {
        while (running) {
            if (evaluating) {
                stop = false;
                System.out.println("loopin");
                makeNewMove();
                isMakingMove = false;
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    logger.error("Error when waiting", e);
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    logger.error("Error when waiting", e);
                }
            }

        }
        System.out.println("Ending sim thread");
        return null;

    }

    private void makeNewMove() {
//        System.out.println("In loop: " + (currentSimGame != null));
        System.out.println("Here mf");
        isMakingMove = true;
        if (currentSimGame == null) {
            logger.debug("Creating new game");
            // start new game
            // start from random opening as to make the sim different
            String randomOpening = top50Openings[random.nextInt(top50Openings.length)];
            ChessGame simGame = ChessGame.createSimpleGameWithNameAndPgn(randomOpening, "Sim Game", isPlayer1WhitePlayer ? "My Computer" : "Stockfish", !isPlayer1WhitePlayer ? "My Computer" : "Stockfish", isPlayer1WhitePlayer ? 9999 : testStockfishElo, !isPlayer1WhitePlayer ? 9999 : testStockfishElo, ProfilePicture.ROBOT.urlString, ProfilePicture.ROBOT.urlString, true, isPlayer1WhitePlayer);
            if(stop){
                return;
            }
            Platform.runLater(() -> {
                control.chessActionHandler.reset();
                control.chessBoardGUIHandler.resetEverything(isPlayer1WhitePlayer);
                control.mainScreenController.setupWithGame(simGame, MainScreenState.SIMULATION, true);
                simGame.moveToEndOfGame(false);
            });
            currentSimGame = simGame;
            try {
                Thread.sleep(800); // sleep a bit to show starting position
            } catch (Exception e) {
                logger.error("Error on sleep before simgame creation", e);
            }
            // set white turn based on starting position
            isPlayer1Turn = simGame.isWhiteTurn() == isPlayer1WhitePlayer;
        } else {

            if (currentSimGame.gameState.isGameOver()) {
                System.out.println("game over");
                // end game
                try {
                    Thread.sleep(200); // show end of game for a little
                } catch (Exception e) {
                    logger.error("Error on thread sleep", e);
                }
                if (currentSimGame.gameState.isStaleMated()) {
                    // draw
                    numDraws++;
                }
                // else one side must have one,
                boolean isPlayer1Win = currentSimGame.gameState.isCheckMated()[1];
                if (isPlayer1Win) {
                    if (isPlayer1WhitePlayer) {
                        // computer is player 1 and won (Computer win)
                        numComputerWins++;
                    } else {
                        // stockfish is player 1 and won (Stockfish Win)
                        numStockFishWins++;
                    }
                } else {
                    if (isPlayer1WhitePlayer) {
                        // computer is player 1 but lost (Stockfish win)
                        numStockFishWins++;
                    } else {
                        // stockfish player 1 but lost (Computer win)
                        numStockFishWins++;
                    }
                }
                // set start for next game (flip first player)
                isPlayer1WhitePlayer = !isPlayer1WhitePlayer;
                isPlayer1Turn = isPlayer1WhitePlayer;
                int totalCount = numDraws + numComputerWins + numStockFishWins;
                int estimatedElo = EloEstimator.estimateEloDiffOnWinProb((double) numComputerWins /totalCount, (double) numDraws /totalCount,3200);
                if(estimatedElo < 0){
                    // no wins so formula breaks :(
                    estimatedElo = -1;
                }
                int finalEst = estimatedElo;
                // update info
                Platform.runLater(() -> control.mainScreenController.setSimScore(numComputerWins, numStockFishWins, numDraws,finalEst));
                currentSimGame = null;
            }
            else {
                // else just change turn as normall
                boolean isWhiteTurn = isPlayer1Turn == isPlayer1WhitePlayer;
                if (isPlayer1Turn) {
                    logger.error("Player 1 turn");
                    ChessMove move = player1Computer.getComputerMove(isWhiteTurn, currentSimGame.currentPosition, currentSimGame.gameState,this.stockfish);
                    if(stop){
                        return;
                    }
                    if(move == null){
                        // stopped
                        logger.error("Stopped sim comp output");
                        return;
                    }
                    Platform.runLater(()->{
                        currentSimGame.makeNewMove(move, true, false);
                    });
                } else {
                    logger.error("Player 2 turn");
                    ChessMove move = player2Computer.getComputerMove(isWhiteTurn, currentSimGame.currentPosition, currentSimGame.gameState,this.stockfish);
                    if(move == null){
                        // stopped
                        logger.error("Stopped sim comp output");
                        return;
                    }
                    Platform.runLater(()->{
                        currentSimGame.makeNewMove(move, true, false);
                    });

                }
                isPlayer1Turn = !isPlayer1Turn;
            }

        }


    }


    public void endThread() {
        stockfish.stopEngine();
        running = false;
    }


    public void setMakingMoveFalse() {
        isMakingMove = false;
    }
}
