package chessengine.Misc;

import chessengine.App;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessMove;
import chessengine.Computation.CustomMultiSearcher;
import chessengine.Computation.Searcher;
import chessengine.Computation.Stockfish;
import chessserver.Functions.PgnFunctions;
import chessengine.Records.SearchResult;
import chessserver.Enums.ComputerDifficulty;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EloEstimator {
    private final Logger logger = LogManager.getLogger(this.toString());
    private final int timeLimit = 1000;

    private final CustomMultiSearcher multiSearcher = new CustomMultiSearcher();

    private final Searcher searcher = new Searcher();
    private final Stockfish stockfish = new Stockfish();

    public EloEstimator() {
        boolean isStart = stockfish.startEngine();
        if (!isStart) {
            logger.error("Failed to start stockfish engine");
        }
    }

    // returns estimated elo of player 1
    public static int estimateEloDiffOnWinProb(double winProbP1, double drawProb, int player2elo) {
        double expectedScore = winProbP1 + drawProb / 2;
        return player2elo + (int) (400 * Math.log10(expectedScore / (1 - expectedScore)));
    }

    /**
     * Tests a specific computer difficulty against another, given that player 2's elo is somewhat accurate. Returns estimate elo for player 1
     **/
    public int testElo(ComputerDifficulty player1TestDifficulty, ComputerDifficulty player2TestDifficulty, int numRuns) {

        int numPlayer1Wins = 0;
        int numPlayer2Wins = 0;
        int numDraws = 0;
        boolean isPlayer1First = true;
        for (int i = 0; i < numRuns; i++) {
            ChessGame testGame = ChessGame.createTestGame("");
            boolean isPlayer1Turn = isPlayer1First;
            while (!testGame.getGameState().isGameOver()) {
                boolean isWhiteTurn = isPlayer1Turn == isPlayer1First;
                ChessMove move = getMove(isPlayer1Turn ? player1TestDifficulty : player2TestDifficulty, testGame, isWhiteTurn);
                if (move == null) {
                    logger.error("Null move encountered, redoing turn");
                    continue;
                }
                testGame.makeNewMove(move, true, false);
                isPlayer1Turn = !isPlayer1Turn;
            }

            // game over

            if (testGame.getGameState().isStaleMated()) {
                // draw
                numDraws++;
            } else {
                // else one side must have one,
                boolean isPlayer1Win = testGame.getGameState().isCheckMated()[1];
                if (isPlayer1Win) {
                    if (isPlayer1First) {
                        // computer is player 1 and won (Computer win)
                        numPlayer1Wins++;
                    } else {
                        // stockfish is player 1 and won (Stockfish Win)
                        numPlayer2Wins++;
                    }
                } else {
                    if (isPlayer1First) {
                        // computer is player 1 but lost (Stockfish win)
                        numPlayer2Wins++;
                    } else {
                        // stockfish player 1 but lost (Computer win)
                        numPlayer2Wins++;
                    }
                }
            }


            isPlayer1First = !isPlayer1First;
            System.out.println("Results---------------------\nCompW: " + numPlayer1Wins + " StockF'W: " + numPlayer2Wins + " Draws: " + numDraws);
        }

        int total = numPlayer1Wins + numPlayer2Wins + numDraws;
        double p1WinProb = (double) numPlayer1Wins / total;
        double drawProb = (double) numDraws / total;
        int estimatedEloDiff = estimateEloDiffOnWinProb(p1WinProb, drawProb, player2TestDifficulty.eloRange);
        return player2TestDifficulty.eloRange - estimatedEloDiff;
    }

    private ChessMove getMove(ComputerDifficulty currentPlayerDifficulty, ChessGame game, boolean isWhiteTurn) {
        if (currentPlayerDifficulty.isStockfishBased) {
            String moveUci = App.getMoveStockfish.getBestMove(game.getCurrentFen(), currentPlayerDifficulty.stockfishElo, timeLimit);
            if (moveUci != null) {
                ChessMove move = PgnFunctions.uciToChessMove(moveUci, game.isWhiteTurn(), game.getCurrentPosition().board);
                Platform.runLater(() -> {
                    game.makeNewMove(move, true, false);
                });
            }
        } else if (currentPlayerDifficulty == ComputerDifficulty.MaxDifficulty) {
            SearchResult out = searcher.search(game.getCurrentPosition().toBackend(game.getGameState(), isWhiteTurn), timeLimit);
            if(out != null){
                ChessMove move = out.move();
                if (!searcher.wasForcedStop()) {
                    return move;
                }
            }
        } else {
            ChessMove move = multiSearcher.search(game.getCurrentPosition().toBackend(game.getGameState(), isWhiteTurn), timeLimit, 1).results()[0].move();
            if (!multiSearcher.wasForcedStop()) {
                return move;
            }
        }
        return null;
    }


}
