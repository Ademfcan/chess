package chessengine.Misc;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Computation.Computer;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Functions.PgnFunctions;
import chessengine.Computation.Stockfish;
import chessserver.ComputerDifficulty;

public class EloEstimator {
    private final int numRuns = 30;
    private final int stockFishElo = 1500;

    public int testElo(ComputerDifficulty testDifficulty, boolean isShow) {
        Computer testComputer = new Computer(7); // eval depth not used
        testComputer.setCurrentDifficulty(testDifficulty);
        Stockfish stockfish = new Stockfish();
        int numComputerWins = 0;
        int numStockFishWins = 0;
        int numDraws = 0;
        if (stockfish.startEngine()) {
            boolean isComputerFirst = true;
            for (int i = 0; i < numRuns; i++) {
                ChessGame testGame = ChessGame.createTestGame("", true);
                boolean isComputerTurn = isComputerFirst;
                while (!testGame.gameState.isGameOver()) {
                    boolean isWhiteTurn = isComputerTurn == isComputerFirst;
                    if (isComputerTurn) {
                        ChessMove move = testComputer.getComputerMove(isWhiteTurn, testGame.currentPosition, testGame.gameState,null); // wont use computers stockfish
                        testGame.makeNewMove(move, !isShow, false);
                    } else {
                        String moveUci = stockfish.getBestMove(PgnFunctions.positionToFEN(testGame.currentPosition, testGame.gameState, isWhiteTurn),stockFishElo ,300);

                        ChessMove move = PgnFunctions.uciToChessMove(moveUci, isWhiteTurn, testGame.currentPosition.board);

                        testGame.makeNewMove(move, !isShow, false);
                    }
                    isComputerTurn = !isComputerTurn;
                }
                if (testGame.gameState.isStaleMated()) {
                    // draw
                    numDraws++;
                }
                // else one side must have one,
                boolean isPlayer1Win = testGame.gameState.isCheckMated()[1];
                if (isPlayer1Win) {
                    if (isComputerFirst) {
                        // computer is player 1 and won (Computer win)
                        numComputerWins++;
                    } else {
                        // stockfish is player 1 and won (Stockfish Win)
                        numStockFishWins++;
                    }
                } else {
                    if (isComputerFirst) {
                        // computer is player 1 but lost (Stockfish win)
                        numStockFishWins++;
                    } else {
                        // stockfish player 1 but lost (Computer win)
                        numStockFishWins++;
                    }
                }

                isComputerFirst = !isComputerFirst;
                System.out.println("Results---------------------\nCompW: " + numComputerWins + " StockF'W: " + numStockFishWins + " Draws: " + numDraws);
            }

        }
        int total = numComputerWins + numStockFishWins + numDraws;
        double p1WinProb = (double) numComputerWins / total;
        double drawProb = (double) numDraws / total;
        int estimatedEloDiff = estimateEloDiffOnWinProb(p1WinProb, drawProb, stockFishElo);
        return stockFishElo - estimatedEloDiff;
    }

    // returns estimated elo of player 1
    public static int estimateEloDiffOnWinProb(double winProbP1, double drawProb, int player2elo) {
        double expectedScore = winProbP1 + drawProb / 2;
        return player2elo + (int) (400 * Math.log10(expectedScore / (1 - expectedScore)));
    }



}
