package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;
import chessengine.Functions.GeneralChessFunctions;

public class Killer {
    private killerRecord[] killers;
    private int putIndex = 0;
    private int filledIndex = 0;
    public Killer(int nKillers){
        killers = new killerRecord[nKillers];
    }

    public void saveKiller(ChessMove m){
        int startIndex = GeneralChessFunctions.positionToBitIndex(m.getOldX(),m.getOldY());
        int endIndex = GeneralChessFunctions.positionToBitIndex(m.getNewX(),m.getNewY());
        killers[putIndex] = new killerRecord(startIndex,endIndex);
        putIndex++;
        putIndex%=killers.length;
        filledIndex++;
        filledIndex = Math.min(filledIndex,killers.length);
    }

    public boolean isKiller(ChessMove m){
        int startIndex = GeneralChessFunctions.positionToBitIndex(m.getOldX(),m.getOldY());
        int endIndex = GeneralChessFunctions.positionToBitIndex(m.getNewX(),m.getNewY());
        if(killers[putIndex] == null){
            // no entries yet
            return false;
        }
        for(int i = 0;i<filledIndex;i++){
            killerRecord kr = killers[i];
            if(kr.startIndex() == startIndex && kr.endIndex() == endIndex){
                return true;
            }
        }

        return false;

    }
}


record killerRecord(int startIndex,int endIndex){

}