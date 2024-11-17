package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessMove;
import chessengine.Computation.CustomMultiSearcher;
import chessengine.Computation.Searcher;
import chessengine.Enums.MainScreenState;
import chessserver.Functions.PgnFunctions;
import chessengine.Misc.EloEstimator;
import chessengine.Records.SearchResult;
import chessserver.Enums.ComputerDifficulty;
import chessserver.Enums.ProfilePicture;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class SimulationTask extends Task<Void> {

    private final int testStockfishElo = 1500; // todo make changeable in gui maybe?

    private final CustomMultiSearcher multiSearcher;
    private final ChessCentralControl control;
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
    private volatile ComputerDifficulty player1Difficulty;
    private volatile ComputerDifficulty player2Difficulty;
    private volatile boolean running = true;
    private volatile boolean evaluating = false;
    private boolean isMakingMove = false;
    private boolean isPlayer1WhitePlayer = true; // starts true
    private boolean isPlayer1Turn = true; // starts true
    private int numPlayer1Wins = 0;
    private int numPlayer2Wins = 0;
    private int numDraws = 0;
    private ChessGame currentSimGame = null;
    private final Searcher searcher;
    private volatile boolean stop = false;
    private final int baseWaitTime = 50;

    public SimulationTask(ChessCentralControl control) {
        this.logger = LogManager.getLogger(this.toString());
        this.multiSearcher = new CustomMultiSearcher();
        this.player1Difficulty = ComputerDifficulty.MaxDifficulty;
        this.player2Difficulty = ComputerDifficulty.StockfishD10;
        this.control = control;

        random = new Random();
        searcher = new Searcher();

    }

    public boolean isEvaluating() {
        return evaluating;
    }

    public boolean isMakingMove() {
        return isMakingMove;
    }

    public void setPlayer1SimulationDifficulty(ComputerDifficulty diff) {
        player1Difficulty = diff;
        updateNames();
    }

    public void setPlayer2SimulationDifficulty(ComputerDifficulty diff) {
        player2Difficulty = diff;
        updateNames();
    }

    private void updateNames(){
        if(currentSimGame != null){
            control.mainScreenController.setPlayerLabels(isPlayer1WhitePlayer ? player1Difficulty.name() : player2Difficulty.name(), isPlayer1WhitePlayer ? player1Difficulty.eloRange : player2Difficulty.eloRange, !isPlayer1WhitePlayer ? player1Difficulty.name() : player2Difficulty.name(), !isPlayer1WhitePlayer ? player1Difficulty.eloRange : player2Difficulty.eloRange, currentSimGame.isWhiteOriented());
        }
    }

    public void startSimulation() {
        logger.debug("Starting sim");
        evaluating = true;
    }

    public void toggleSimulation() {
        evaluating = !evaluating;

    }

    public void stopAndReset() {
        stop = true;
        logger.debug("Stopping sim");
        evaluating = false;
        searcher.stopSearch();
        multiSearcher.stopSearch();

        if (App.getMoveStockfish.isCalling()) {
            App.getMoveStockfish.stop.set(true);
        }
        isPlayer1WhitePlayer = true;
        isPlayer1Turn = true;
        currentSimGame = null;
        numPlayer1Wins = 0;
        numPlayer2Wins = 0;
        numDraws = 0;
    }

    @Override
    public Void call() {
        while (running) {
//            System.out.println("Sim");
            if (evaluating) {
                try {
                    stop = false;
                    makeNewMove();
                    isMakingMove = false;
                    Thread.sleep(200);
                } catch (Exception e) {
                    logger.error("Error", e);
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    logger.error("Error when waiting", e);
                }
            }

        }
//        System.out.println("Ending sim thread");
        return null;

    }

    private void makeNewMove() {
        isMakingMove = true;
        if (currentSimGame == null) {
            logger.debug("Creating new game");
            // start new game
            // start from random opening as to make the sim different
            String randomOpening = top50Openings[random.nextInt(top50Openings.length)];
            ChessGame simGame = ChessGame.createSimpleGameWithNameAndPgn(randomOpening, "Sim Game", isPlayer1WhitePlayer ? player1Difficulty.name() : player2Difficulty.name(), !isPlayer1WhitePlayer ? player1Difficulty.name() : player2Difficulty.name(), isPlayer1WhitePlayer ? player1Difficulty.eloRange : player2Difficulty.eloRange, !isPlayer1WhitePlayer ? player1Difficulty.eloRange : player2Difficulty.eloRange, ProfilePicture.ROBOT.urlString, ProfilePicture.ROBOT.urlString, true, isPlayer1WhitePlayer);
            if (stop) {
                return;
            }
            Platform.runLater(() -> {
                control.chessActionHandler.reset();
                control.chessBoardGUIHandler.resetEverything(isPlayer1WhitePlayer);
                control.mainScreenController.setupWithGame(simGame, MainScreenState.SIMULATION ,true);
                control.gameHandler.gameWrapper.moveToEndOfGame(App.userPreferenceManager.isNoAnimate());
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

            if (currentSimGame.getGameState().isGameOver()) {
                // end game
                try {
                    Thread.sleep(200); // show end of game for a little
                } catch (Exception e) {
                    logger.error("Error on thread sleep", e);
                }
                if (currentSimGame.getGameState().isStaleMated()) {
                    // draw
                    numDraws++;
                } else {
                    // else one side must have one,
                    boolean isPlayer1Win = currentSimGame.getGameState().isCheckMated()[1];
                    if (isPlayer1Win) {
                        if (isPlayer1WhitePlayer) {
                            // computer is player 1 and won (Computer win)
                            numPlayer1Wins++;
                        } else {
                            // stockfish is player 1 and won (Stockfish Win)
                            numPlayer2Wins++;
                        }
                    } else {
                        if (isPlayer1WhitePlayer) {
                            // computer is player 1 but lost (Stockfish win)
                            numPlayer2Wins++;
                        } else {
                            // stockfish player 1 but lost (Computer win)
                            numPlayer1Wins++;
                        }
                    }
                }

                // set start for next game (flip first player)
                isPlayer1WhitePlayer = !isPlayer1WhitePlayer;
                isPlayer1Turn = isPlayer1WhitePlayer;
                int totalCount = numDraws + numPlayer1Wins + numPlayer2Wins;
                int estimatedElo = EloEstimator.estimateEloDiffOnWinProb((double) numPlayer1Wins / totalCount, (double) numDraws / totalCount, player2Difficulty.eloRange);
                if (estimatedElo < 0) {
                    // no wins so formula breaks :(
                    estimatedElo = -1;
                }
                int finalEst = estimatedElo;
                // update info
                Platform.runLater(() -> control.mainScreenController.setSimScore(numPlayer1Wins, numPlayer2Wins, numDraws, finalEst));
                currentSimGame = null;
            } else {
                // else just change turn as normall
                boolean isWhiteTurn = isPlayer1Turn == isPlayer1WhitePlayer;
                boolean animateIfPossible = App.userPreferenceManager.isNoAnimate();
                if (isPlayer1Turn) {
                    logger.debug("Player 1 turn");
                    ChessMove move = getMove(player1Difficulty, currentSimGame, isWhiteTurn);
                    if (move == null) {
                        // stopped
                        logger.error("Stopped sim comp output");
                        return;
                    }
                    Platform.runLater(() -> {
                        control.gameHandler.gameWrapper.makeNewMove(move, true, false,animateIfPossible);
                    });
                } else {
                    logger.debug("Player 2 turn");
                    ChessMove move = getMove(player2Difficulty, currentSimGame, isWhiteTurn);
                    if (move == null) {
                        // stopped
                        logger.error("Stopped sim comp output");
                        return;
                    }
                    Platform.runLater(() -> {
                        control.gameHandler.gameWrapper.makeNewMove(move, true, false,animateIfPossible);
                    });
                }
                isPlayer1Turn = !isPlayer1Turn;
            }

        }


    }

    private ChessMove getMove(ComputerDifficulty currentPlayerDifficulty, ChessGame game, boolean isWhiteTurn) {
        int waitTime = (int) (baseWaitTime * control.chessActionHandler.timeSlider.getValue());
        if (currentPlayerDifficulty.isStockfishBased) {
            String moveUci = App.getMoveStockfish.getBestMove(game.getCurrentFen(), currentPlayerDifficulty.stockfishElo, waitTime);
            if (moveUci != null) {
                ChessMove move = PgnFunctions.uciToChessMove(moveUci, game.isWhiteTurn(), game.getCurrentPosition().board);
                Platform.runLater(() -> {
                    control.gameHandler.gameWrapper.makeNewMove(move, true, false,App.userPreferenceManager.isNoAnimate());
                });
            }
        } else if (currentPlayerDifficulty == ComputerDifficulty.MaxDifficulty) {
            SearchResult out = searcher.search(game.getCurrentPosition().toBackend(game.getGameState(), isWhiteTurn), waitTime);
            if(out == null || stop){
                return null;
            }
            ChessMove move = out.move();
            if (!searcher.wasForcedStop()) {
                return move;
            }
        } else {
            ChessMove move = multiSearcher.search(game.getCurrentPosition().toBackend(game.getGameState(), isWhiteTurn), waitTime, 1).results()[0].move();
            if (stop) {
                return null;
            }
            if (!multiSearcher.wasForcedStop()) {
                return move;
            }
        }
        return null;
    }


    public void endThread() {
        running = false;
        stopAndReset();
    }


    public void setMakingMoveFalse() {
        isMakingMove = false;
    }
}
