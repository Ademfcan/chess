package chessengine.Misc;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Computation.Computer;
import chessengine.Computation.Searcher;
import chessengine.Functions.PgnFunctions;
import chessengine.Computation.Stockfish;
import chessserver.ComputerDifficulty;

public class EloEstimator {
    private final int numRuns = 30;
    private final int stockFishElo = 2000;
    private final int timeLimit = 1000;

    public int testElo(ComputerDifficulty testDifficulty, boolean isShow) {
        Computer testComputer = new Computer();
        Searcher searcher = new Searcher();
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
                        ChessMove move;
                        if(testDifficulty.equals(ComputerDifficulty.MaxDifficulty)){
                            move = searcher.search(testGame.currentPosition.toBackend(testGame.gameState,isWhiteTurn),timeLimit).move();
                        }
                        else{
                            move = testComputer.getComputerMoveWithFlavors(isWhiteTurn, testGame.currentPosition, testGame.gameState).getMove();
                        }
                        testGame.makeNewMove(move, true, false);
                    } else {
                        String moveUci = stockfish.getBestMove(PgnFunctions.positionToFEN(testGame.currentPosition, testGame.gameState, isWhiteTurn),stockFishElo ,timeLimit);
                        if(moveUci != null){
                            ChessMove move = PgnFunctions.uciToChessMove(moveUci, isWhiteTurn, testGame.currentPosition.board);
                            testGame.makeNewMove(move, true, false);
                        }
                    }
                    isComputerTurn = !isComputerTurn;
                }

                if (testGame.gameState.isStaleMated()) {
                    // draw
                    numDraws++;
                }
                else{
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
