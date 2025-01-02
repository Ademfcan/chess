package chessserver.ChessRepresentations;

import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class BitBoardWrapper {
    private final static Logger logger = LogManager.getLogger("BitboardWrapper_Logger"); 
    private final HashSet<Integer>[] whitePieces;
    private final HashSet<Integer>[] blackPieces;
    private long[] whitePiecesBB;
    private long[] blackPiecesBB;
    private final long[] whiteAttackTables;
    private final long[] blackAttackTables;
    private int whitePieceCount = 0;
    private int blackPieceCount = 0;
    private int tempOldIndex;
    private int tempBoardIndex;
    private int tempNewIndex;
    private boolean tempIsWhite;
    public BitBoardWrapper(long[] whitePiecesBB, long[] blackPiecesBB) {
        this.whitePiecesBB = whitePiecesBB;
        this.blackPiecesBB = blackPiecesBB;
        this.whitePieces = new HashSet[ChessConstants.NUMPIECES];
        this.blackPieces = new HashSet[ChessConstants.NUMPIECES];
        for (int i = 0; i <= ChessConstants.KINGINDEX; i++) {
            int[] whitePieceLocations = GeneralChessFunctions.getAllPieceIndexes(whitePiecesBB[i]);
            int[] blackPieceLocations = GeneralChessFunctions.getAllPieceIndexes(blackPiecesBB[i]);
            whitePieces[i] = new HashSet<>();
            blackPieces[i] = new HashSet<>();
            for (int index : whitePieceLocations) {
//                System.out.println("White index: " + index);
                whitePieceCount++;
                whitePieces[i].add(index);
            }
            for (int index : blackPieceLocations) {
//                System.out.println("Black index: " + index);
                blackPieceCount++;
                blackPieces[i].add(index);
            }
        }
        this.whiteAttackTables = new long[6];
        this.blackAttackTables = new long[6];
        for (int i = 0; i < whiteAttackTables.length; i++) {
            updateAttackMask(i, true);
            updateAttackMask(i, false);
        }
        this.tempNewIndex = ChessConstants.EMPTYINDEX;
    }

    public BitBoardWrapper(HashSet<Integer>[] whitePieces, HashSet<Integer>[] blackPieces, long[] whitePiecesBB,
                           long[] blackPiecesBB, long[] whiteAttackTables, long[] blackAttackTables,
                           int tempChange, int tempIndex, int tempBoardIndex, boolean tempIsWhite) {
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.whitePiecesBB = whitePiecesBB;
        this.blackPiecesBB = blackPiecesBB;
        this.whiteAttackTables = whiteAttackTables;
        this.blackAttackTables = blackAttackTables;
        this.tempOldIndex = tempChange;
        this.tempNewIndex = tempIndex;
        this.tempIsWhite = tempIsWhite;
        this.tempBoardIndex = tempBoardIndex;
    }
    /**Constructs a Fully empty board**/
    private BitBoardWrapper(){
        this.whitePiecesBB = new long[ChessConstants.NUMPIECES];
        this.blackPiecesBB = new long[ChessConstants.NUMPIECES];
        this.whitePieces = new HashSet[ChessConstants.NUMPIECES];
        this.blackPieces = new HashSet[ChessConstants.NUMPIECES];
        for (int i = 0; i <= ChessConstants.KINGINDEX; i++) {
            whitePieces[i] = new HashSet<>();
            blackPieces[i] = new HashSet<>();
        }
        this.whiteAttackTables = new long[6];
        this.blackAttackTables = new long[6];
        this.tempNewIndex = ChessConstants.EMPTYINDEX;
    }

    public int getWhitePieceCount() {
        return whitePieceCount;
    }

    public int getBlackPieceCount() {
        return blackPieceCount;
    }

    public void updateAttackMasks() {
        for (int i = 0; i < whiteAttackTables.length; i++) {
            updateAttackMask(i, true);
            updateAttackMask(i, false);
        }
    }

    public HashSet<Integer>[] getWhitePieces() {
        return whitePieces;
    }

    public HashSet<Integer>[] getBlackPieces() {
        return blackPieces;
    }

    public long getWhitePieceMask() {
        return whitePiecesBB[0] | whitePiecesBB[1] | whitePiecesBB[2] | whitePiecesBB[3] | whitePiecesBB[4] | whitePiecesBB[5];
    }

    public long getBlackPieceMask() {
        return blackPiecesBB[0] | blackPiecesBB[1] | blackPiecesBB[2] | blackPiecesBB[3] | blackPiecesBB[4] | blackPiecesBB[5];
    }

    public long[] getWhiteAttackTables() {
        return whiteAttackTables;
    }

    public long[] getBlackAttackTables() {
        return blackAttackTables;
    }

    public long getWhiteAttackTableCombined() {
        return whiteAttackTables[0] | whiteAttackTables[1] | whiteAttackTables[2] | whiteAttackTables[3] | whiteAttackTables[4] | whiteAttackTables[5];
    }

    public long getBlackAttackTableCombined() {
        return blackAttackTables[0] | blackAttackTables[1] | blackAttackTables[2] | blackAttackTables[3] | blackAttackTables[4] | blackAttackTables[5];
    }

    public long getWhiteSlidingAttackers() {
        return whiteAttackTables[2] | whiteAttackTables[3] | whiteAttackTables[4] | whiteAttackTables[5];
    }

    public long getBlackSlidingAttackers() {
        return blackAttackTables[2] | blackAttackTables[3] | blackAttackTables[4] | blackAttackTables[5];
    }

    /**
     * adds a piece to the given bitindex
     **/
    public void updateAttackMask(int pieceIndex, boolean isWhitePiece) {
        long mask = 0L;
        HashSet<Integer>[] pieces = isWhitePiece ? whitePieces : blackPieces;
        for (int index : pieces[pieceIndex]) {
            switch (pieceIndex) {
                case ChessConstants.PAWNINDEX -> {
                    mask |= BitFunctions.calculatePawnAttackMask(index, isWhitePiece, this);
                }
                case ChessConstants.KNIGHTINDEX -> {
                    mask |= BitFunctions.calculateKnightAttackBitBoard(index, isWhitePiece, this);
                }
                case ChessConstants.BISHOPINDEX -> {
                    mask |= BitFunctions.calculateBishopAttackBitBoard(index, isWhitePiece, true, this);
                }
                case ChessConstants.ROOKINDEX -> {
                    mask |= BitFunctions.calculateRookAttackBitBoard(index, isWhitePiece, true, this);
                }
                case ChessConstants.QUEENINDEX -> {
                    mask |= BitFunctions.calculateQueenAtackBitboard(index, isWhitePiece, true, this);
                }
                case ChessConstants.KINGINDEX -> {
                    mask |= BitFunctions.calculateKingAttackBitboard(index, isWhitePiece, this);
                }
            }
        }
//        System.out.println(GeneralChessFunctions.getPieceType(pieceIndex) + " Mask");
//        System.out.println(BitFunctions.getBitStr(mask));
        if (isWhitePiece) {
            whiteAttackTables[pieceIndex] = mask;
        } else {
            blackAttackTables[pieceIndex] = mask;
        }

    }

    public void addPiece(int bitIndex, int pieceIndex, boolean isWhitePiece) {
//        System.out.println("adding : " + isWhitePiece + " " + GeneralChessFunctions.getPieceType(pieceIndex) + " at : " + bitIndex);
        if (isWhitePiece) {
            whitePieceCount++;
            whitePiecesBB[pieceIndex] = GeneralChessFunctions.AddPeice(bitIndex, whitePiecesBB[pieceIndex]);
            whitePieces[pieceIndex].add(bitIndex);
        } else {
            blackPieceCount++;
            blackPiecesBB[pieceIndex] = GeneralChessFunctions.AddPeice(bitIndex, blackPiecesBB[pieceIndex]);
            blackPieces[pieceIndex].add(bitIndex);
        }
//        updateAttackMask(pieceIndex, isWhitePiece);
    }

    public void removePiece(int bitIndex, int pieceIndex, boolean isWhitePiece) {
//        System.out.println("removing : " + isWhitePiece + " " + GeneralChessFunctions.getPieceType(pieceIndex) + " at : " + bitIndex);
        if (isWhitePiece) {
            whitePieceCount--;
            whitePiecesBB[pieceIndex] = GeneralChessFunctions.RemovePeice(bitIndex, whitePiecesBB[pieceIndex]);
            whitePieces[pieceIndex].remove(bitIndex);
        } else {
            blackPieceCount--;
            blackPiecesBB[pieceIndex] = GeneralChessFunctions.RemovePeice(bitIndex, blackPiecesBB[pieceIndex]);
            blackPieces[pieceIndex].remove(bitIndex);
        }
//        updateAttackMask(pieceIndex, isWhitePiece);

    }

    /**
     * Checks whether a certain bit index is in a certain colors board, returns boolean.
     **/
    public boolean[] contains(int bitIndex) {
        for (int i = 0; i < whitePieces.length; i++) {
            if (whitePieces[i].contains(bitIndex)) {
                return new boolean[]{true, true};
            }
            if (blackPieces[i].contains(bitIndex)) {
                return new boolean[]{true, false};
            }
        }
        return new boolean[]{false, false};
    }

    /**
     * Checks whether a certain bit index is in any board, returns boolean array. First value is whether the bitindex is present, and the second dictates whether it is white.
     **/
    public boolean contains(int bitIndex, boolean isWhite) {
        if (isWhite) {
            for (long l : whitePiecesBB) {
                if (GeneralChessFunctions.checkIfContains(bitIndex, l)) {
                    return true;
                }
            }
        } else {
            for (long l : blackPiecesBB) {
                if (GeneralChessFunctions.checkIfContains(bitIndex, l)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Flips each bitboard one by one then also updates king location
     **/
    public void flipBoard() {
        if (tempNewIndex == ChessConstants.EMPTYINDEX) {
            for (int i = 0; i <= ChessConstants.KINGINDEX; i++) {
                whitePiecesBB[i] = Long.reverse(whitePiecesBB[i]);
                blackPiecesBB[i] = Long.reverse(blackPiecesBB[i]);
            }
        } else {
            logger.error("cannot flip board you are in a temp change!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitBoardWrapper that = (BitBoardWrapper) o;
        return Arrays.equals(whitePiecesBB, that.whitePiecesBB) && Arrays.equals(blackPiecesBB, that.blackPiecesBB);
    }

    public long[] getWhitePiecesBB() {
        return whitePiecesBB;
    }

    public void setWhitePiecesBB(long[] whitePiecesBB) {
        this.whitePiecesBB = whitePiecesBB;
    }

    public long[] getBlackPiecesBB() {
        return blackPiecesBB;
    }

    public void setBlackPiecesBB(long[] blackPiecesBB) {
        this.blackPiecesBB = blackPiecesBB;
    }

    public XYcoord getWhiteKingLocation() {
        return new XYcoord(GeneralChessFunctions.getFirstPieceIndex(whitePiecesBB[ChessConstants.KINGINDEX]));
    }


    public XYcoord getBlackKingLocation() {
        return new XYcoord(GeneralChessFunctions.getFirstPieceIndex(blackPiecesBB[ChessConstants.KINGINDEX]));
    }


    public BitBoardWrapper cloneBoard() {
        HashSet<Integer>[] newWhitePieces = new HashSet[whitePieces.length];
        HashSet<Integer>[] newBlackPieces = new HashSet[blackPieces.length];
        for (int i = 0; i < whitePieces.length; i++) {
            newWhitePieces[i] = new HashSet<>(whitePieces[i]);
            newBlackPieces[i] = new HashSet<>(blackPieces[i]);
        }
        return new BitBoardWrapper(newWhitePieces, newBlackPieces,
                Arrays.copyOf(this.whitePiecesBB, this.whitePiecesBB.length), Arrays.copyOf(this.blackPiecesBB, this.blackPiecesBB.length),
                Arrays.copyOf(this.whiteAttackTables, whiteAttackTables.length), Arrays.copyOf(this.blackAttackTables, blackAttackTables.length),
                tempOldIndex, tempNewIndex, tempBoardIndex, tempIsWhite);
    }

    public void makeTempChange(int oldX, int oldY, int newX, int newY, int boardIndex, boolean isWhiteBoard) {
        if (tempNewIndex == ChessConstants.EMPTYINDEX) {
//            System.out.println(a++);
            int oldBitIndex = GeneralChessFunctions.positionToBitIndex(oldX, oldY);
            int newBitIndex = GeneralChessFunctions.positionToBitIndex(newX, newY);
            addPiece(newBitIndex, boardIndex, isWhiteBoard);
            removePiece(oldBitIndex, boardIndex, isWhiteBoard);
            updateAttackMasks();
            tempNewIndex = newBitIndex;
            tempOldIndex = oldBitIndex;
            tempBoardIndex = boardIndex;
            tempIsWhite = isWhiteBoard;
        } else {
            logger.error("Cannot make another temp change, one is already present");
        }


    }


    public void popTempChange() {
        if (tempNewIndex != ChessConstants.EMPTYINDEX) {
//            System.out.println("popping");
            removePiece(tempNewIndex, tempBoardIndex, tempIsWhite);
            addPiece(tempOldIndex, tempBoardIndex, tempIsWhite);
            updateAttackMasks();
            tempNewIndex = ChessConstants.EMPTYINDEX;
            tempOldIndex = ChessConstants.EMPTYINDEX;
        } else {
            logger.error("Trying to pop temp change when not active!");
        }


    }

    public void keepTempChange() {
        tempNewIndex = ChessConstants.EMPTYINDEX;
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(whitePiecesBB) + Arrays.hashCode(blackPiecesBB);
    }

    public static BitBoardWrapper getEmptyBoard(){
        return new BitBoardWrapper();
    }

}
