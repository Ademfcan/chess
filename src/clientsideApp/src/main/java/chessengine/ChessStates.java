package chessengine;

import chessserver.GameStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChessStates {
    public ChessStates(){
        this.posMap = new HashMap<>();
    }

    private ChessStates(boolean whiteCastleRight, boolean blackCastleRight, boolean whiteShortRookRight, boolean whiteLongRookRight, boolean blackShortRookRight, boolean blackLongRookRight, int blackCastleIndx, int whiteCastleIndx, int whiteShortRookIndx, int whiteLongRookIndx, int blackShortRookIndx, int blackLongRookIndx, int currentIndex,boolean isCheckMated,boolean isStaleMated,HashMap<Integer,Integer> posMap) {
        this.whiteCastleRight = whiteCastleRight;
        this.blackCastleRight = blackCastleRight;
        this.whiteShortRookRight = whiteShortRookRight;
        this.whiteLongRookRight = whiteLongRookRight;
        this.blackShortRookRight = blackShortRookRight;
        this.blackLongRookRight = blackLongRookRight;
        this.blackCastleIndx = blackCastleIndx;
        this.whiteCastleIndx = whiteCastleIndx;
        this.whiteShortRookIndx = whiteShortRookIndx;
        this.whiteLongRookIndx = whiteLongRookIndx;
        this.blackShortRookIndx = blackShortRookIndx;
        this.blackLongRookIndx = blackLongRookIndx;
        this.currentIndex = currentIndex;
        this.isCheckMated = isCheckMated;
        this.isStaleMated = isStaleMated;
        this.posMap = cloneMap(posMap);
    }

    private HashMap<Integer,Integer> cloneMap(HashMap<Integer,Integer> oldMap){
        HashMap<Integer,Integer> newMap = new HashMap<>(oldMap.size());
        for(Map.Entry<Integer,Integer> me : oldMap.entrySet()){
            // by value
            int i = me.getValue();
            int newHash = me.getKey();
            newMap.put(newHash,i);

        }
        return newMap;
    }

    public void clearIndexes(int newMoveIndex,boolean isChangeMoveIndex){
        // if you undo a move, clear the indexes for flags;
        checkMateIndex = checkMateIndex > newMoveIndex ? 1000 : checkMateIndex;
        staleMateIndex = staleMateIndex > newMoveIndex ? 1000 : staleMateIndex;
        whiteCastleIndx = whiteCastleIndx > newMoveIndex ? 1000 : whiteCastleIndx;
        blackCastleIndx = blackCastleIndx > newMoveIndex ? 1000 : blackCastleIndx;
        whiteLongRookIndx = whiteLongRookIndx > newMoveIndex ? 1000 : whiteLongRookIndx;
        whiteShortRookIndx = whiteShortRookIndx > newMoveIndex ? 1000 : whiteShortRookIndx;
        blackLongRookIndx = blackLongRookIndx > newMoveIndex ? 1000 : blackLongRookIndx;
        blackShortRookIndx = blackShortRookIndx > newMoveIndex ? 1000 : blackShortRookIndx;
        updateAllStates(newMoveIndex,isChangeMoveIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessStates that = (ChessStates) o;
        return isCheckMated == that.isCheckMated && isStaleMated == that.isStaleMated && whiteCastleRight == that.whiteCastleRight && blackCastleRight == that.blackCastleRight && whiteShortRookRight == that.whiteShortRookRight && whiteLongRookRight == that.whiteLongRookRight && blackShortRookRight == that.blackShortRookRight && blackLongRookRight == that.blackLongRookRight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCheckMated, isStaleMated, whiteCastleRight, blackCastleRight, whiteShortRookRight, whiteLongRookRight, blackShortRookRight, blackLongRookRight);
    }

    // posMap used for checking draw by repetition
    public HashMap<Integer,Integer> posMap;

    private boolean isCheckMated = false;

    private int checkMateIndex = 1000;

    private boolean isStaleMated = false;

    private int staleMateIndex = 1000;
    private boolean whiteCastleRight = true;

    private boolean blackCastleRight = true;

    private boolean whiteShortRookRight = true;
    private boolean whiteLongRookRight = true;
    private boolean blackShortRookRight = true;
    private boolean blackLongRookRight = true;
    private int blackCastleIndx = 1000;
    private int whiteCastleIndx = 1000;
    private int whiteShortRookIndx = 1000;
    private int whiteLongRookIndx = 1000;
    private int blackShortRookIndx = 1000;

    private int blackLongRookIndx = 1000;

    public boolean makeNewMoveAndCheckDrawRep(ChessPosition newPosition, boolean incrementIndex){
        if(incrementIndex){
            currentIndex++;
        }
        int hash = newPosition.board.hashCode();
        Integer posCount = posMap.getOrDefault(hash,0);
        posMap.put(hash,posCount+1);
        if(posCount+1 > 2){
            // draw by repetition
            return true;
        }
        return false;

    }

    public int getCurrentIndex() {
        return currentIndex;
    }



    private int currentIndex = -1;

    public ChessStates cloneState(){
        return new ChessStates(whiteCastleRight,blackCastleRight,whiteShortRookRight,whiteLongRookRight,blackShortRookRight,blackLongRookRight,blackCastleIndx,whiteCastleIndx,whiteShortRookIndx,whiteLongRookIndx,blackShortRookIndx,blackLongRookIndx,currentIndex,isCheckMated,isStaleMated,posMap);
    }

    public boolean isCheckMated() {
        return this.isCheckMated;
    }
    public void setCheckMated() {
        this.checkMateIndex = currentIndex;
        isCheckMated = true;
    }

    public boolean isStaleMated() {
        return this.isStaleMated;
    }

    public void setStaleMated(){
        this.staleMateIndex = currentIndex;
        isStaleMated = true;
    }

    public boolean isWhiteCastleRight() {
        return whiteCastleRight;
    }

    public boolean isBlackCastleRight() {
        return blackCastleRight;
    }

    public boolean isWhiteShortRookRight() {
        return whiteShortRookRight;
    }

    public boolean isWhiteLongRookRight() {
        return whiteLongRookRight;
    }

    public boolean isBlackShortRookRight() {
        return blackShortRookRight;
    }

    public boolean isBlackLongRookRight() {
        return blackLongRookRight;
    }

    public void updateAllStates(int newMoveIndex,boolean isChangeMoveIndex){
        if(isChangeMoveIndex){
            currentIndex = newMoveIndex;
        }
        whiteCastleRight = newMoveIndex <= whiteCastleIndx;
        blackCastleRight = newMoveIndex <= blackCastleIndx;
        whiteLongRookRight = newMoveIndex <= whiteLongRookIndx;
        whiteShortRookRight = newMoveIndex <= whiteShortRookIndx;
        blackLongRookRight = newMoveIndex <= blackLongRookIndx;
        blackShortRookRight = newMoveIndex <= blackShortRookIndx;
        isCheckMated = newMoveIndex >= checkMateIndex;
        isStaleMated = newMoveIndex >= staleMateIndex;
    }

    public void updateMoveIndex(int newMoveIndex){
        currentIndex = newMoveIndex;
    }
    public void updateMoveIndex(){
        currentIndex++;
    }

    public ChessStates updateMoveIndexHack(){
        currentIndex++;
        return this;
    }

    public void removeCastlingRight(boolean isWhite){
        if(isWhite){
            if(whiteCastleRight){
                whiteCastleRight = false;
                whiteCastleIndx = currentIndex;
            }

        }
        else{
            if(blackCastleRight){
                blackCastleRight = false;
                blackCastleIndx = currentIndex;
            }

        }

    }

    public void checkRemoveRookMoveRight(int x, int y){
        checkRemoveRookMoveRight(x,y,y == 7);
    }
    public void checkRemoveRookMoveRight(int x, int y,boolean isWhite){
        boolean isShort = x == 7;
        boolean isValidX = isShort || x == 0;
        boolean isValidY = isWhite || y == 0;
        if(isValidX && isValidY){
            if(isShort){
                if(isWhite){
                    whiteShortRookIndx = currentIndex;
                    whiteShortRookRight = false;
                }
                else{
                    blackShortRookIndx = currentIndex;
                    blackShortRookRight = false;
                }
            }
            else{
                if(isWhite){
                    whiteLongRookIndx = currentIndex;
                    whiteLongRookRight = false;
                }
                else{
                    blackShortRookIndx = currentIndex;
                    blackLongRookRight = false;
                }
            }

            }

    }
}
