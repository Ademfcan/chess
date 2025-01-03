package chessserver.ChessRepresentations;

import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Functions.ZobristHasher;
import chessserver.Misc.ChessConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class ChessGameState {
    private final static Logger logger = LogManager.getLogger("Chess_State_Logger");
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
    private boolean whiteKingSideRight = true;
    private boolean whiteQueenSideRight = true;
    private boolean blackKingSideRight = true;
    private boolean blackQueenSideRight = true;
    private int blackCastleIndx = 1000;
    private int whiteCastleIndx = 1000;
    private int whiteKingSideIndx = 1000;
    private int whiteQueenSideIndex = 1000;
    private int blackKingSideIndx = 1000;
    private int blackQueenSideIndx = 1000;
    private int currentIndex = -1;

    public ChessGameState() {
        this.hasher = new ZobristHasher();
        this.posMap = new HashMap<>();
        this.movesWhenResetted = new Stack<>();
    }

    private ChessGameState(boolean whiteCastleRight, boolean blackCastleRight, boolean whiteKingSideRight, boolean whiteQueenSideRight, boolean blackKingSideRight, boolean blackQueenSideRight, int blackCastleIndx, int whiteCastleIndx, int whiteKingSideIndx, int whiteQueenSideIndx, int blackKingSideIndx, int blackQueenSideIndx, int currentIndex, boolean isCheckMated, int checkMateIndex, boolean isWhiteWin, boolean isStaleMated, int staleMateIndex, HashMap<Long, Integer> posMap, Stack<Integer> movesWhenResetted, int movesSinceNoCheckOrNoPawn) {
        this.hasher = new ZobristHasher();
        this.whiteCastleRight = whiteCastleRight;
        this.blackCastleRight = blackCastleRight;
        this.whiteKingSideRight = whiteKingSideRight;
        this.whiteQueenSideRight = whiteQueenSideRight;
        this.blackKingSideRight = blackKingSideRight;
        this.blackQueenSideRight = blackQueenSideRight;
        this.blackCastleIndx = blackCastleIndx;
        this.whiteCastleIndx = whiteCastleIndx;
        this.whiteKingSideIndx = whiteKingSideIndx;
        this.whiteQueenSideIndex = whiteQueenSideIndx;
        this.blackKingSideIndx = blackKingSideIndx;
        this.blackQueenSideIndx = blackQueenSideIndx;
        this.currentIndex = currentIndex;
        this.isCheckMated = isCheckMated;
        this.isWhiteWin = isWhiteWin;
        this.isStaleMated = isStaleMated;
        this.staleMateIndex = staleMateIndex;
        this.checkMateIndex = checkMateIndex;
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
                ", whiteShortRookRight=" + whiteKingSideRight +
                ", whiteLongRookRight=" + whiteQueenSideRight +
                ", blackShortRookRight=" + blackKingSideRight +
                ", blackLongRookRight=" + blackQueenSideRight +
                ", blackCastleIndx=" + blackCastleIndx +
                ", whiteCastleIndx=" + whiteCastleIndx +
                ", whiteShortRookIndx=" + whiteKingSideIndx +
                ", whiteLongRookIndx=" + whiteQueenSideIndex +
                ", blackShortRookIndx=" + blackKingSideIndx +
                ", blackLongRookIndx=" + blackQueenSideIndx +
                ", currentIndex=" + currentIndex +
                ", stack for movessincecheckornoPawn=" + movesWhenResetted.toString() +
                '}';
    }

    public void clearIndexes(int newMoveIndex) {
        // if you undo a move, clear the indexes for flags;
        checkMateIndex = checkMateIndex > newMoveIndex ? 1000 : checkMateIndex;
        staleMateIndex = staleMateIndex > newMoveIndex ? 1000 : staleMateIndex;
        whiteCastleIndx = whiteCastleIndx >= newMoveIndex ? 1000 : whiteCastleIndx;
        blackCastleIndx = blackCastleIndx >= newMoveIndex ? 1000 : blackCastleIndx;
        whiteQueenSideIndex = whiteQueenSideIndex >= newMoveIndex ? 1000 : whiteQueenSideIndex;
        whiteKingSideIndx = whiteKingSideIndx >= newMoveIndex ? 1000 : whiteKingSideIndx;
        blackQueenSideIndx = blackQueenSideIndx >= newMoveIndex ? 1000 : blackQueenSideIndx;
        blackKingSideIndx = blackKingSideIndx >= newMoveIndex ? 1000 : blackKingSideIndx;
        updateAllStatesToNewIndex(newMoveIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGameState that = (ChessGameState) o;
        return isWhiteWin == that.isWhiteWin && isCheckMated == that.isCheckMated && isStaleMated == that.isStaleMated && whiteCastleRight == that.whiteCastleRight && blackCastleRight == that.blackCastleRight && whiteKingSideRight == that.whiteKingSideRight && whiteQueenSideRight == that.whiteQueenSideRight && blackKingSideRight == that.blackKingSideRight && blackQueenSideRight == that.blackQueenSideRight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isWhiteWin, isCheckMated, isStaleMated, whiteCastleRight, blackCastleRight, whiteKingSideRight, whiteQueenSideRight, blackKingSideRight, blackQueenSideRight);
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
        long hash = hasher.computeHash(newPosition.board);
        Integer posCount = posMap.getOrDefault(hash, 0);
        posMap.put(hash, posCount + 1);
        if (posCount + 1 > 2) {
            // draw by repetition
//            logger.debug("Draw by repetition triggered");
            setStaleMated();
            return true;
        }
        // next check 50 move rule
        if (!newPosition.getMoveThatCreatedThis().equals(ChessConstants.startMove)) {
            ChessMove moveThatCreated = newPosition.getMoveThatCreatedThis();
            // check that the move is not a pawn move or a check
            if (moveThatCreated.getBoardIndex() != ChessConstants.PAWNINDEX && !AdvancedChessFunctions.isAnyChecked(newPosition.board)) {
                // increment moves since no check or pawn move
                movesSinceNoCheckOrNoPawn++;
                // 100 moves in total == 50 moves per side
                if (movesSinceNoCheckOrNoPawn > 99) {
                    setStaleMated();
                    return true;
                }

            } else {
                // just checked or made a pawn move

                movesWhenResetted.push(movesSinceNoCheckOrNoPawn);
                movesSinceNoCheckOrNoPawn = 0;
            }
        }

        if (GeneralChessFunctions.isInsufiicientMaterial(newPosition.board)) {
            setStaleMated();
            return true;
        }
        return false;
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

        whiteKingSideRight = true;

        whiteQueenSideRight = true;
        blackKingSideRight = true;
        blackQueenSideRight = true;
        blackCastleIndx = 1000;
        whiteCastleIndx = 1000;
        whiteKingSideIndx = 1000;
        whiteQueenSideIndex = 1000;
        blackKingSideIndx = 1000;
        blackQueenSideIndx = 1000;
    }

    public ChessGameState cloneState() {
        HashMap<Long, Integer> clonedPosMap = new HashMap<>(posMap);
        Stack<Integer> clonedMovesWhenResetted = cloneStack(movesWhenResetted);

        return new ChessGameState(
                whiteCastleRight, blackCastleRight, whiteKingSideRight, whiteQueenSideRight,
                blackKingSideRight, blackQueenSideRight, blackCastleIndx, whiteCastleIndx,
                whiteKingSideIndx, whiteQueenSideIndex, blackKingSideIndx, blackQueenSideIndx,
                currentIndex, isCheckMated, checkMateIndex, isWhiteWin, isStaleMated, staleMateIndex, clonedPosMap, clonedMovesWhenResetted, movesSinceNoCheckOrNoPawn);
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

    public void setCheckMated(boolean isWhiteWin, int customIndex) {
        if (customIndex != ChessConstants.EMPTYINDEX) {
            this.checkMateIndex = customIndex;
        } else {
            this.checkMateIndex = currentIndex;
        }
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

    public boolean isWhiteKingSideRight() {
        return whiteKingSideRight;
    }

    public boolean isWhiteQueenSideRight() {
        return whiteQueenSideRight;
    }

    public boolean isBlackKingSideRight() {
        return blackKingSideRight;
    }

    public boolean isBlackQueenSideRight() {
        return blackQueenSideRight;
    }


    private void updateAllStatesToNewIndex(int newMoveIndex) {
        currentIndex = newMoveIndex;
        whiteCastleRight = newMoveIndex <= whiteCastleIndx;
        blackCastleRight = newMoveIndex <= blackCastleIndx;
        whiteQueenSideRight = newMoveIndex <= whiteQueenSideIndex;
        whiteKingSideRight = newMoveIndex <= whiteKingSideIndx;
        blackQueenSideRight = newMoveIndex <= blackQueenSideIndx;
        blackKingSideRight = newMoveIndex <= blackKingSideIndx;
        isCheckMated = newMoveIndex >= checkMateIndex;
        isStaleMated = newMoveIndex >= staleMateIndex;
    }

    public void moveBackward(ChessPosition oldPositionToRemove) {
        // remove the position from posmap
        long key = hasher.computeHash(oldPositionToRemove.board);

        int count = posMap.getOrDefault(key, ChessConstants.EMPTYINDEX);
        if (count != ChessConstants.EMPTYINDEX) {
            if (count <= 1) {
                // remove the position from posmap altogether as the count will now be zero
                posMap.remove(key);
            } else {
                posMap.put(key, count - 1);
            }
        } else {
            logger.error("Position not found in posmap, weird");
        }

        // now handle the 50 move rule
        if (!oldPositionToRemove.getMoveThatCreatedThis().equals(ChessConstants.startMove)) {
            ChessMove moveThatCreated = oldPositionToRemove.getMoveThatCreatedThis();
            if (moveThatCreated.getBoardIndex() != ChessConstants.PAWNINDEX && !AdvancedChessFunctions.isAnyChecked(oldPositionToRemove.board)) {
                // moves since check or pawn move was not reset so we just decrement
                movesSinceNoCheckOrNoPawn--;
            } else {
                // we need to recover the movesSinceNoCheckOrNoPawn value before reset
                if (movesWhenResetted.isEmpty()) {
                    logger.error("Is checked: " + AdvancedChessFunctions.isAnyChecked(oldPositionToRemove.board));
                    logger.error("Index: " + movesSinceNoCheckOrNoPawn);
                    logger.error("AttackMask\n" + BitFunctions.getBitStr(oldPositionToRemove.board.getWhiteAttackTableCombined() | oldPositionToRemove.board.getBlackAttackTableCombined()));
                    logger.error("moveswhen ressetted does not have needed elements, are you moving backward before forward");
                    logger.error(oldPositionToRemove.getMoveThatCreatedThis().toString());
                    logger.error(this.toString());
                    logger.error(GeneralChessFunctions.getBoardDetailedString(oldPositionToRemove.board));
                } else {
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
        long hash = hasher.computeHash(newPositionToAdd.board);
        Integer posCount = posMap.getOrDefault(hash, 0);
        posMap.put(hash, posCount + 1);

        // next check 50 move rule
        if (!newPositionToAdd.getMoveThatCreatedThis().equals(ChessConstants.startMove)) {
            ChessMove moveThatCreated = newPositionToAdd.getMoveThatCreatedThis();
            // check that the move is not a pawn move or a check
            if (moveThatCreated.getBoardIndex() != ChessConstants.PAWNINDEX && !AdvancedChessFunctions.isAnyChecked(newPositionToAdd.board)) {
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

    public ChessGameState updateMoveIndexForwardHack() {
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

    public void removeRookRight(boolean isWhite, boolean isQueenSide) {
        if (isWhite) {
            if (isQueenSide) {
                // if already false then we dont overwrite as we need to keep the earliest index
                if (whiteQueenSideRight) {
                    whiteQueenSideRight = false;
                    whiteQueenSideIndex = currentIndex;
                }
            } else {
                if (whiteKingSideRight) {
                    whiteKingSideRight = false;
                    whiteKingSideIndx = currentIndex;
                }
            }
        }
        else {
            if (isQueenSide) {
                if (blackQueenSideRight) {
                    blackQueenSideRight = false;
                    blackQueenSideIndx = currentIndex;
                }
            } else {
                if (blackKingSideRight) {
                    blackKingSideRight = false;
                    blackKingSideIndx = currentIndex;
                }
            }


        }

    }

    public void checkRemoveRookMoveRight(int x, int y, boolean isWhite) {
        boolean isKingSide = x == 7;
        boolean isValidX = isKingSide || x == 0;
        boolean isValidY = isWhite || y == 0;
        if (isValidX && isValidY) {
            if (isKingSide) {
                if (isWhite) {
                    if (whiteKingSideRight) {
                        whiteKingSideIndx = currentIndex;
                        whiteKingSideRight = false;
                    }
                } else {
                    if (blackKingSideRight) {
                        blackKingSideIndx = currentIndex;
                        blackKingSideRight = false;
                    }
                }
            } else {
                if (isWhite) {
                    if (whiteQueenSideRight) {
                        whiteQueenSideIndex = currentIndex;
                        whiteQueenSideRight = false;
                    }
                } else {
                    if (blackQueenSideRight) {
                        blackQueenSideIndx = currentIndex;
                        blackQueenSideRight = false;
                    }
                }
            }

        }

    }

    public void giveCastleRight(boolean isWhite){
        if(isWhite){
            whiteCastleRight = true;
            whiteCastleIndx = 1000;
        }
        else{
            blackCastleRight = true;
            blackCastleIndx = 1000;
        }
    }

    public void giveRookRight(boolean isWhite, boolean isQueenSide) {
        if (isWhite) {
            if (isQueenSide) {
                whiteQueenSideRight = true;
                whiteQueenSideIndex = 1000;
            } else {
                whiteKingSideRight = true;
                whiteKingSideIndx = 1000;
            }
        }
        else {
            if (isQueenSide) {
                blackQueenSideRight = true;
                blackQueenSideIndx = 1000;
            } else {
                blackKingSideRight = true;
                blackKingSideIndx = 1000;
            }


        }

    }




    public void updateRightsBasedOnMove(ChessMove move) {
        if (move.isCustomMove() || move.getBoardIndex() == ChessConstants.KINGINDEX) {
            removeCastlingRight(move.isWhite());
        } else if (move.getBoardIndex() == ChessConstants.ROOKINDEX) {
            checkRemoveRookMoveRight(move.getOldX(), move.getOldY(), move.isWhite());
        } else if (move.isEating() && move.getEatingIndex() == ChessConstants.ROOKINDEX) {
            checkRemoveRookMoveRight(move.getNewX(), move.getNewY(), !move.isWhite());
        }
    }

    public void removeAllRightsInstantly() {
        removeCastlingRight(true);
        removeCastlingRight(false);
        removeRookRight(true,true);
        removeRookRight(true,false);
        removeRookRight(false,true);
        removeRookRight(false,false);
    }

    public void setMovesSinceNoCheckOrNoPawn(int movesSinceNoCheckOrNoPawn) {
        this.movesSinceNoCheckOrNoPawn = movesSinceNoCheckOrNoPawn;
    }
}
