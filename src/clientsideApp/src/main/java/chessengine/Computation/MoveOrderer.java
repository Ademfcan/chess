package chessengine.Computation;

import chessengine.ChessRepresentations.BitBoardWrapper;
import chessengine.ChessRepresentations.ChessMove;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;
import chessengine.Misc.pieceMapHandler;
import javafx.geometry.Pos;

import java.util.Arrays;

public class MoveOrderer {

    private final int mill = 1000000;
    private final int hashMoveWeight = 100*mill;
    private final int transpositionWeight = 80*mill;
    private final int killerWeight = 4*mill;
    private final int promotionWeight = 6*mill;
    private final int winningCaptureWeight = 10*mill;
    private final int loosingCaptureWeight = 2*mill;


    int[][][] history;

    Killer[] killers;

    public MoveOrderer(){
        clearHistory();
        clearKillers();
    }

    public void clearHistory(){
        history = new int[2][64][64];
    }
    private final int killerDepth = 100;
    private final int nKillers = 3; // test this
    public void clearKillers(){killers = new Killer[killerDepth];
        for(int i = 0;i<killerDepth;i++){
            killers[i] = new Killer(nKillers);
        }
    }

    public void sortMoves(ChessMove previousBestMove, BitBoardWrapper board,ChessMove[] moves,ChessMove hashMove1,ChessMove hashMove2,int plyFromRoot) {
        if(moves[0] == null){
            return;
        }
        int nonNullMoveLen = 0;
        while(nonNullMoveLen < moves.length && moves[nonNullMoveLen] != null){
            nonNullMoveLen++;

        }
        int[] moveScores = new int[nonNullMoveLen];
        boolean isWhite = moves[0].isWhite();
        int[][][] currentValueMap = pieceMapHandler.pieceMapsCp1;
        for(int i = 0;i<moves.length;i++){
            ChessMove move = moves[i];
            if(move == null){
                break;
            }
            if(move.equals(previousBestMove)){
                moveScores[i] += hashMoveWeight;
                continue;
            }
            if(move.equals(hashMove1) || move.equals(hashMove2)){
                moveScores[i] += transpositionWeight;
                continue;
            }

            int score = 0;
            int fromSquareIndex = GeneralChessFunctions.positionToBitIndex(move.getOldX(),move.getOldY());
            int toSquareIndex = GeneralChessFunctions.positionToBitIndex(move.getNewX(),move.getNewY());
            long opponentAttacks = isWhite ? board.getBlackAttackTableCombined() : board.getWhiteAttackTableCombined();
            if(move.isEating()){
                int captureDifference = ChessConstants.valueMapCentiPawn[move.getEatingIndex()]-ChessConstants.valueMapCentiPawn[move.getBoardIndex()];
                boolean opponentCanRecapture = GeneralChessFunctions.checkIfContains(toSquareIndex,opponentAttacks);

                if(opponentCanRecapture){
                    score += (captureDifference > 0 ? winningCaptureWeight : loosingCaptureWeight) + captureDifference;
                }
                else{
                    score += winningCaptureWeight + captureDifference;
                }
            }

            if(move.isPawnPromo()){
                score += promotionWeight;
            }
            int pieceType = move.getBoardIndex();
            int oldXSideAdjusted = isWhite ? 7-move.getOldX() : move.getOldX();
            int oldYSideAdjusted = isWhite ? 7-move.getOldY() : move.getOldY();
            int newXSideAdjusted = isWhite ? 7-move.getNewX() : move.getNewX();
            int newYSideAdjusted = isWhite ? 7-move.getNewY() : move.getNewY();

            int currentSquareValue = currentValueMap[pieceType][oldXSideAdjusted][oldYSideAdjusted];
            int newSquareValue = currentValueMap[pieceType][newXSideAdjusted][newYSideAdjusted];

            score += newSquareValue-currentSquareValue;

            if(move.getBoardIndex() != ChessConstants.KINGINDEX){
                // todo move difference values

                long enemyPawnAttackMask = isWhite ? board.getBlackAttackTables()[ChessConstants.PAWNINDEX] : board.getWhiteAttackTables()[ChessConstants.PAWNINDEX];

                if(GeneralChessFunctions.checkIfContains(toSquareIndex,enemyPawnAttackMask)){
                    score -= 50;
                }
                else if(GeneralChessFunctions.checkIfContains(toSquareIndex,opponentAttacks)){
                    score -= 25;
                }

            }

            if(!move.isEating()){
                Killer killer = killers[plyFromRoot];
                if(killer.isKiller(move)){
                    score += killerWeight;
                }

                score += history[isWhite ? 0 : 1][fromSquareIndex][toSquareIndex];
            }
            moveScores[i] += score;


        }
        Quicksort(moves,moveScores,0,moveScores.length-1);
    }

    private void Quicksort(ChessMove[] moves,int[] moveValues,int left,int right){
        if(left < right){
           int p = partition(moves,moveValues,left,right);
           Quicksort(moves,moveValues,left,p-1);
           Quicksort(moves,moveValues,p+1,right);
        }

    }

    private int partition(ChessMove[] moves,int[] moveValues,int left,int right){
        int pivot = moveValues[left];
        int i = right;
        for(int j = right;j>=left;j--){
            if(moveValues[j] < pivot){
                swap(moves,moveValues,i,j);
                i--;
            }
        }
        swap(moves,moveValues,i,left);
        return i;
    }

    private void swap(ChessMove[] moves,int[] moveValue,int leftSwap,int rightSwap){
        ChessMove temp = moves[leftSwap];
        int tempMoveValue = moveValue[leftSwap];
        moves[leftSwap] = moves[rightSwap];
        moveValue[leftSwap] = moveValue[rightSwap];
        moves[rightSwap] = temp;
        moveValue[rightSwap] = tempMoveValue;
    }
}
