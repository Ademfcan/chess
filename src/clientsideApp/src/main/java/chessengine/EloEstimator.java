package chessengine;

import chessserver.ComputerDifficulty;

public class EloEstimator {
    private final int numRuns = 30;
    private final int stockFishElo = 3200;
    public int testElo(ComputerDifficulty testDifficulty,boolean isShow){
        Computer testComputer = new Computer(7); // eval depth not used
        testComputer.setCurrentDifficulty(testDifficulty);
        Stockfish stockfish = new Stockfish();
        int numComputerWins = 0;
        int numStockFishWins = 0;
        int numDraws = 0;
        if(stockfish.startEngine()){
            boolean isComputerFirst = true;
            for(int i = 0;i<numRuns;i++){
                ChessGame testGame = ChessGame.createTestGame("",true);
                boolean isComputerTurn = isComputerFirst;
                while(!testGame.gameState.isGameOver()){
                    boolean isWhiteTurn = isComputerTurn == isComputerFirst;
                    if(isComputerTurn){
                        ChessMove move = testComputer.getComputerMove(isWhiteTurn,testGame.currentPosition,testGame.gameState);
                        testGame.makeNewMove(move,!isShow,false);
                    }
                    else{
                        String moveUci = stockfish.getBestMove(PgnFunctions.positionToFEN(testGame.currentPosition,testGame.gameState,isWhiteTurn),300);

                        System.out.println("Stockfish uci: " + moveUci);
                        ChessMove move = PgnFunctions.uciToChessMove(moveUci,isWhiteTurn,testGame.currentPosition.board);

                        System.out.println("Stockfish move: " + move);
                        testGame.makeNewMove(move,!isShow,false);
                    }
                    System.out.println("Current position:\n" + GeneralChessFunctions.getBoardDetailedString(testGame.currentPosition.board));
                    isComputerTurn = !isComputerTurn;
                }
                if(testGame.gameState.isStaleMated()){
                    // draw
                    numDraws++;
                }
                // else one side must have one,
                boolean isPlayer1Win = testGame.gameState.isCheckMated()[1];
                if(isPlayer1Win){
                    if(isComputerFirst){
                        // computer is player 1 and won (Computer win)
                        numComputerWins++;
                    }
                    else{
                        // stockfish is player 1 and won (Stockfish Win)
                        numStockFishWins++;
                    }
                }
                else{
                    if(isComputerFirst){
                        // computer is player 1 but lost (Stockfish win)
                        numStockFishWins++;
                    }
                    else{
                        // stockfish player 1 but lost (Computer win)
                        numStockFishWins++;
                    }
                }

                isComputerFirst = !isComputerFirst;
                System.out.println("Results---------------------\nCompW: " + numComputerWins + " StockF'W: " + numStockFishWins + " Draws: " + numDraws);
            }

        }
        int total = numComputerWins + numStockFishWins + numDraws;
        double p1WinProb = (double) numComputerWins/total;
        double drawProb = (double) numDraws/total;
        int estimatedEloDiff  = estimateEloDiffOnWinProb(p1WinProb,drawProb,stockFishElo);
        return stockFishElo-estimatedEloDiff;
    }
    // returns estimated elo of player 1
    private int estimateEloDiffOnWinProb(double winProbP1,double drawProb,int player2elo){
        return (int) (player2elo-(400*(Math.log(1/(drawProb/2+winProbP1)-1)/Math.log(10))));
    }


}
