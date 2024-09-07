package chessengine.ChessRepresentations;

import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Functions.ZobristHasher;
import chessengine.Misc.ChessConstants;

import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class ChessStates {
    private final ZobristHasher hasher;
    // posMap used for checking draw by repetition
    private final HashMap<Long, Integer> posMap;
    private final Stack<Integer> movesWhenResetted;
    private int movesSinceNoCheckOrNoPawn = 0;
    private boolean isCheckMated = false;
    private boolean isWhiteWin = false;
    private int checkMateIndex = 1000;
    private boolean isStaleMated = false;
    private int staleMateIndex = 1000;
    private boolean whiteCastleRight = true;
    private boolean blackCastleRight = true;


    // movesSinceCheckOrPawn used for 50 move rule
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
    private int currentIndex = -1;
    public ChessStates() {
        this.hasher = new ZobristHasher();
        this.posMap = new HashMap<>();
        this.movesWhenResetted = new Stack<>();
    }
    private ChessStates(boolean whiteCastleRight, boolean blackCastleRight, boolean whiteShortRookRight, boolean whiteLongRookRight, boolean blackShortRookRight, boolean blackLongRookRight, int blackCastleIndx, int whiteCastleIndx, int whiteShortRookIndx, int whiteLongRookIndx, int blackShortRookIndx, int blackLongRookIndx, int currentIndex, boolean isCheckMated, boolean isWhiteWin, boolean isStaleMated, HashMap<Long, Integer> posMap, Stack<Integer> movesWhenResetted, int movesSinceNoCheckOrNoPawn) {
        this.hasher = new ZobristHasher();
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
        this.isWhiteWin = isWhiteWin;
        this.isStaleMated = isStaleMated;
        this.posMap = posMap;
        this.movesWhenResetted = movesWhenResetted;
        this.movesSinceNoCheckOrNoPawn = movesSinceNoCheckOrNoPawn;

    }

    @Override
    public String toString() {
        return "ChessStates{" +
                "movesSinceNoCheckOrNoPawn=" + movesSinceNoCheckOrNoPawn +
                ", isCheckMated=" + isCheckMated +
                ", isWhiteWin=" + isWhiteWin +
                ", checkMateIndex=" + checkMateIndex +
                ", isStaleMated=" + isStaleMated +
                ", staleMateIndex=" + staleMateIndex +
                ", whiteCastleRight=" + whiteCastleRight +
                ", blackCastleRight=" + blackCastleRight +
                ", whiteShortRookRight=" + whiteShortRookRight +
                ", whiteLongRookRight=" + whiteLongRookRight +
                ", blackShortRookRight=" + blackShortRookRight +
                ", blackLongRookRight=" + blackLongRookRight +
                ", blackCastleIndx=" + blackCastleIndx +
                ", whiteCastleIndx=" + whiteCastleIndx +
                ", whiteShortRookIndx=" + whiteShortRookIndx +
                ", whiteLongRookIndx=" + whiteLongRookIndx +
                ", blackShortRookIndx=" + blackShortRookIndx +
                ", blackLongRookIndx=" + blackLongRookIndx +
                ", currentIndex=" + currentIndex +
                '}';
    }

    public void clearIndexes(int newMoveIndex) {
        // if you undo a move, clear the indexes for flags;
        checkMateIndex = checkMateIndex > newMoveIndex ? 1000 : checkMateIndex;
        staleMateIndex = staleMateIndex > newMoveIndex ? 1000 : staleMateIndex;
        whiteCastleIndx = whiteCastleIndx >= newMoveIndex ? 1000 : whiteCastleIndx;
        blackCastleIndx = blackCastleIndx >= newMoveIndex ? 1000 : blackCastleIndx;
        whiteLongRookIndx = whiteLongRookIndx >= newMoveIndex ? 1000 : whiteLongRookIndx;
        whiteShortRookIndx = whiteShortRookIndx >= newMoveIndex ? 1000 : whiteShortRookIndx;
        blackLongRookIndx = blackLongRookIndx >= newMoveIndex ? 1000 : blackLongRookIndx;
        blackShortRookIndx = blackShortRookIndx >= newMoveIndex ? 1000 : blackShortRookIndx;
        updateAllStatesToNewIndex(newMoveIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessStates that = (ChessStates) o;
        return isWhiteWin == that.isWhiteWin && isCheckMated == that.isCheckMated && isStaleMated == that.isStaleMated && whiteCastleRight == that.whiteCastleRight && blackCastleRight == that.blackCastleRight && whiteShortRookRight == that.whiteShortRookRight && whiteLongRookRight == that.whiteLongRookRight && blackShortRookRight == that.blackShortRookRight && blackLongRookRight == that.blackLongRookRight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isWhiteWin, isCheckMated, isStaleMated, whiteCastleRight, blackCastleRight, whiteShortRookRight, whiteLongRookRight, blackShortRookRight, blackLongRookRight);
    }

    public HashMap<Long, Integer> getPosMap() {
        return posMap;
    }

    public Stack<Integer> getMovesWhenResetted() {
        return movesWhenResetted;
    }

    public boolean makeNewMoveAndCheckDraw(ChessPosition newPosition) {
        clearIndexes(currentIndex + 1);
        // first check draw by insufficient material

        // second check draw by repetition
        // white move not important in this case so just set constant
//        int hash = newPosition.board.hashCode();
        long hash = hasher.computeHash(newPosition.board, false);
        Integer posCount = posMap.getOrDefault(hash, 0);
        posMap.put(hash, posCount + 1);
        if (posCount + 1 > 2) {
            // draw by repetition
//            ChessConstants.mainLogger.debug("Draw by repetition triggered");
            return true;
        }
        // next check 50 move rule
        if (!newPosition.equals(ChessConstants.startBoardState)) {
            ChessMove moveThatCreated = newPosition.getMoveThatCreatedThis();
            // check that the move is not a pawn move or a check
            if (moveThatCreated.getBoardIndex() != ChessConstants.PAWNINDEX || AdvancedChessFunctions.isAnyChecked(newPosition.board)) {
                // increment moves since no check or pawn move
                movesSinceNoCheckOrNoPawn++;
                // 100 moves in total == 50 moves per side
                if(movesSinceNoCheckOrNoPawn > 99){
//                    staleMateIndex = currentIndex;
//                    isStaleMated = true;
//                    ChessConstants.mainLogger.debug("50 move rule triggered");
                }
                return movesSinceNoCheckOrNoPawn > 99;

            } else {
                // just checked or made a pawn move

                movesWhenResetted.push(movesSinceNoCheckOrNoPawn);
                movesSinceNoCheckOrNoPawn = 0;
            }
        }

        return GeneralChessFunctions.isInsufiicientMaterial(newPosition.board);

    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getMovesSinceNoCheckOrNoPawn() {
        return movesSinceNoCheckOrNoPawn;
    }

    public void reset() {
        currentIndex = -1;
        posMap.clear();
        movesWhenResetted.clear();
        movesSinceNoCheckOrNoPawn = 0;

        isCheckMated = false;
        isWhiteWin = false;

        checkMateIndex = 1000;

        isStaleMated = false;

        staleMateIndex = 1000;

        whiteCastleRight = true;
        blackCastleRight = true;

        whiteShortRookRight = true;

        whiteLongRookRight = true;
        blackShortRookRight = true;
        blackLongRookRight = true;
        blackCastleIndx = 1000;
        whiteCastleIndx = 1000;
        whiteShortRookIndx = 1000;
        whiteLongRookIndx = 1000;
        blackShortRookIndx = 1000;
        blackLongRookIndx = 1000;
    }

    public ChessStates cloneState() {
        HashMap<Long, Integer> clonedPosMap = new HashMap<>((HashMap<Long,Integer>)posMap.clone());
        Stack<Integer> clonedMovesWhenResetted = cloneStack(movesWhenResetted);

        return new ChessStates(
                whiteCastleRight, blackCastleRight, whiteShortRookRight, whiteLongRookRight,
                blackShortRookRight, blackLongRookRight, blackCastleIndx, whiteCastleIndx,
                whiteShortRookIndx, whiteLongRookIndx, blackShortRookIndx, blackLongRookIndx,
                currentIndex, isCheckMated, isWhiteWin, isStaleMated, clonedPosMap, clonedMovesWhenResetted, movesSinceNoCheckOrNoPawn);
    }

    private Stack<Integer> cloneStack(Stack<Integer> oldStack) {
        Stack<Integer> newStack = new Stack<>();
        newStack.addAll(oldStack);
        return newStack;
    }

    public boolean isGameOver() {
        return isCheckMated || isStaleMated;
    }

    public boolean[] isCheckMated() {
        return new boolean[]{this.isCheckMated, this.isWhiteWin};
    }

    public void setCheckMated(boolean isWhiteWin) {
        this.checkMateIndex = currentIndex;
        this.isWhiteWin = isWhiteWin;
        isCheckMated = true;
    }

    public boolean isStaleMated() {
        return this.isStaleMated;
    }

    public void setStaleMated() {
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


    private void updateAllStatesToNewIndex(int newMoveIndex) {
        currentIndex = newMoveIndex;
        whiteCastleRight = newMoveIndex <= whiteCastleIndx;
        blackCastleRight = newMoveIndex <= blackCastleIndx;
        whiteLongRookRight = newMoveIndex <= whiteLongRookIndx;
        whiteShortRookRight = newMoveIndex <= whiteShortRookIndx;
        blackLongRookRight = newMoveIndex <= blackLongRookIndx;
        blackShortRookRight = newMoveIndex <= blackShortRookIndx;
        isCheckMated = newMoveIndex >= checkMateIndex;
        isStaleMated = newMoveIndex >= staleMateIndex;
    }

    public void moveBackward(ChessPosition oldPositionToRemove) {
        // remove the position from posmap
//        int key = oldPositionToRemove.board.hashCode();
        long key = hasher.computeHash(oldPositionToRemove.board, false);

        int count = posMap.getOrDefault(key, ChessConstants.EMPTYINDEX);
        if (count != ChessConstants.EMPTYINDEX) {
            if (count <= 1) {
                // remove the position from posmap altogether as the count will now be zero
                posMap.remove(key);
            } else {
                posMap.put(key, count - 1);
            }
        } else {
            ChessConstants.mainLogger.error("Position not found in posmap, weird");
        }

        // now handle the 50 move rule
        if (!oldPositionToRemove.equals(ChessConstants.startBoardState)) {
            ChessMove moveThatCreated = oldPositionToRemove.getMoveThatCreatedThis();
            if (moveThatCreated.getBoardIndex() != ChessConstants.PAWNINDEX || AdvancedChessFunctions.isAnyChecked(oldPositionToRemove.board)) {
                // moves since check or pawn move was not reset so we just decrement
                movesSinceNoCheckOrNoPawn--;
            } else {
                // we need to recover the movesSinceNoCheckOrNoPawn value before reset
                if (movesWhenResetted.isEmpty()) {
                    ChessConstants.mainLogger.error("moveswhen ressetted does not have needed elements, are you moving backward before forward");
                }
                else{
                    movesSinceNoCheckOrNoPawn = movesWhenResetted.pop();
                }
            }
        }


        // finaly update all flags

        updateAllStatesToNewIndex(currentIndex - 1);

    }


    public void moveForward(ChessPosition newPositionToAdd) {
        // add the position to posmap
//        int hash = newPositionToAdd.board.hashCode();
        long hash = hasher.computeHash(newPositionToAdd.board, false);
        Integer posCount = posMap.getOrDefault(hash, 0);
        posMap.put(hash, posCount + 1);

        // next check 50 move rule
        if (!newPositionToAdd.equals(ChessConstants.startBoardState)) {
            ChessMove moveThatCreated = newPositionToAdd.getMoveThatCreatedThis();
            // check that the move is not a pawn move or a check
            if (moveThatCreated.getBoardIndex() != ChessConstants.PAWNINDEX || AdvancedChessFunctions.isAnyChecked(newPositionToAdd.board)) {
                // increment moves since no check or pawn move
                movesSinceNoCheckOrNoPawn++;

            } else {
                // just checked or made a pawn move
                movesWhenResetted.push(movesSinceNoCheckOrNoPawn);
                movesSinceNoCheckOrNoPawn = 0;
            }
        }


        // finaly update all flags
        updateAllStatesToNewIndex(currentIndex + 1);

    }

    public void updateMoveIndex(int newMoveIndex) {
        currentIndex = newMoveIndex;
    }

    public void updateMoveIndexFoward() {
        currentIndex++;
    }

    public ChessStates updateMoveIndexForwardHack() {
        currentIndex++;
        return this;
    }

    public void removeCastlingRight(boolean isWhite) {
        if (isWhite) {
            // if already false then we dont overwrite as we need to keep the earliest index
            if (whiteCastleRight) {
                whiteCastleRight = false;
                whiteCastleIndx = currentIndex;
            }

        } else {
            if (blackCastleRight) {
                blackCastleRight = false;
                blackCastleIndx = currentIndex;
            }

        }

    }

    public void checkRemoveRookMoveRight(int x, int y) {
        checkRemoveRookMoveRight(x, y, y == 7);
    }

    public void checkRemoveRookMoveRight(int x, int y, boolean isWhite) {
        boolean isShort = x == 7;
        boolean isValidX = isShort || x == 0;
        boolean isValidY = isWhite || y == 0;
        if (isValidX && isValidY) {
            if (isShort) {
                if (isWhite) {
                    if (whiteShortRookRight) {
                        whiteShortRookIndx = currentIndex;
                        whiteShortRookRight = false;
                    }
                } else {
                    if (blackShortRookRight) {
                        blackShortRookIndx = currentIndex;
                        blackShortRookRight = false;
                    }
                }
            } else {
                if (isWhite) {
                    if (whiteLongRookRight) {
                        whiteLongRookIndx = currentIndex;
                        whiteLongRookRight = false;
                    }
                } else {
                    if (blackLongRookRight) {
                        blackLongRookIndx = currentIndex;
                        blackLongRookRight = false;
                    }
                }
            }

        }

    }


}
