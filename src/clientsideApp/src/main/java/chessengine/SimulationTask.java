package chessengine;

import chessserver.ComputerDifficulty;
import chessserver.ProfilePicture;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class SimulationTask extends Task<Void> {

    private final int testStockfishElo = 1500; // todo make changeable in gui maybe?

    private final Computer c;
    private final ChessCentralControl control;
    private final Stockfish stockfish;
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
    private boolean isMyComputerWhitePlayer = true; // starts true
    private boolean isMyComputerTurn = true; // starts true
    private int numComputerWins = 0;
    private int numStockFishWins = 0;
    private int numDraws = 0;
    private ChessGame currentSimGame = null;

    public SimulationTask(Computer c, ChessCentralControl control) {
        this.logger = LogManager.getLogger(this.toString());
        this.c = c;
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

    public void setSimulationDifficulty(ComputerDifficulty diff) {
        c.setCurrentDifficulty(diff);
    }

    public void startSimulation() {
        logger.debug("Starting sim");
        System.out.println("is already evaluating? " + evaluating);
        evaluating = true;
    }

    public void toggleSimulation() {
        evaluating = !evaluating;

    }

    public void stopAndReset() {
        logger.debug("Stopping sim");
        evaluating = false;
        if (c.isRunning()) {
            c.stop.set(true);
        }
        isMyComputerWhitePlayer = true;
        isMyComputerTurn = true;
        currentSimGame = null;
        numComputerWins = 0;
        numStockFishWins = 0;
        numDraws = 0;
    }

    @Override
    public Void call() {
        while (running) {
            if (evaluating) {
                makeNewMove();
                try {
                    Thread.sleep(1000);
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
        isMakingMove = true;
        if (currentSimGame == null) {
            logger.debug("Creating new game");
            // start new game
            // start from random opening as to make the sim different
            String randomOpening = top50Openings[random.nextInt(top50Openings.length)];
            ChessGame simGame = ChessGame.createSimpleGameWithNameAndPgn(randomOpening, "Sim Game", isMyComputerWhitePlayer ? "My Computer" : "Stockfish", !isMyComputerWhitePlayer ? "My Computer" : "Stockfish", isMyComputerWhitePlayer ? 9999 : 3400, !isMyComputerWhitePlayer ? 9999 : 3400, ProfilePicture.ROBOT.urlString, ProfilePicture.ROBOT.urlString, true, isMyComputerWhitePlayer);
            Platform.runLater(() -> {
                control.chessActionHandler.reset();
                control.chessBoardGUIHandler.resetEverything(isMyComputerWhitePlayer);
                control.mainScreenController.setupWithGame(simGame, MainScreenState.SIMULATION, true);
                simGame.moveToEndOfGame(false);
            });
            currentSimGame = simGame;
            try {
                Thread.sleep(1500); // sleep a bit to show starting position
            } catch (Exception e) {
                logger.error("Error on sleep before simgame creation", e);
            }
            // set white turn based on starting position
            isMyComputerTurn = simGame.isWhiteTurn() == isMyComputerWhitePlayer;
        } else {
            // if in current game then play
            boolean isWhiteTurn = isMyComputerTurn == isMyComputerWhitePlayer;
            if (isMyComputerTurn) {
                logger.debug("Getting my computer move");
                ChessMove move = c.getComputerMove(isWhiteTurn, currentSimGame.currentPosition, currentSimGame.gameState);
                if(move == null){
                    // stopped
                    logger.debug("Stopped sim comp output");
                    return;
                }
                currentSimGame.makeNewMove(move, true, false);

//                logger.debug("Getting stockfish move");
//                String moveUci = stockfish.getBestMove(PgnFunctions.positionToFEN(currentSimGame.currentPosition, currentSimGame.gameState, isWhiteTurn), testStockfishElo,350);
//
//                System.out.println("Stockfish uci: " + moveUci);
//                ChessMove move = PgnFunctions.uciToChessMove(moveUci, isWhiteTurn, currentSimGame.currentPosition.board);
//
//                System.out.println("Stockfish move: " + move);
//                currentSimGame.makeNewMove(move, true, false);

            } else {
                logger.debug("Getting stockfish move");
                String moveUci = stockfish.getBestMove(PgnFunctions.positionToFEN(currentSimGame.currentPosition, currentSimGame.gameState, isWhiteTurn), testStockfishElo,350);

                System.out.println("Stockfish uci: " + moveUci);
                ChessMove move = PgnFunctions.uciToChessMove(moveUci, isWhiteTurn, currentSimGame.currentPosition.board);

                System.out.println("Stockfish move: " + move);
                currentSimGame.makeNewMove(move, true, false);
//
//                ChessMove move = c.getComputerMove(isWhiteTurn, currentSimGame.currentPosition, currentSimGame.gameState);
//                if(move == null){
//                    // stopped
//                    logger.debug("Stopped sim comp output");
//                    return;
//                }
//                currentSimGame.makeNewMove(move, true, false);
            }

            // check end conditions
            if (currentSimGame.gameState.isGameOver()) {
                // end game
                try {
                    Thread.sleep(4000); // show end of game for a little
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
                    if (isMyComputerWhitePlayer) {
                        // computer is player 1 and won (Computer win)
                        numComputerWins++;
                    } else {
                        // stockfish is player 1 and won (Stockfish Win)
                        numStockFishWins++;
                    }
                } else {
                    if (isMyComputerWhitePlayer) {
                        // computer is player 1 but lost (Stockfish win)
                        numStockFishWins++;
                    } else {
                        // stockfish player 1 but lost (Computer win)
                        numStockFishWins++;
                    }
                }
                // set start for next game (flip first player)
                isMyComputerWhitePlayer = !isMyComputerWhitePlayer;
                isMyComputerTurn = isMyComputerWhitePlayer;

                int estimatedElo = EloEstimator.estimateEloDiffOnWinProb(numComputerWins,numDraws,testStockfishElo);
                if(estimatedElo < 0){
                    // no wins so formula breaks :(
                    estimatedElo = -1;
                }
                int finalEst = estimatedElo;
                // update info
                Platform.runLater(() -> {
                    control.mainScreenController.setSimScore(numComputerWins, numStockFishWins, numDraws,finalEst);
                });
                currentSimGame = null;
            } else {
                // else just change turn as normall
                isMyComputerTurn = !isMyComputerTurn;
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
