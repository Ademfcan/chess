package chessengine.Functions;

import chessengine.ChessRepresentations.BitBoardWrapper;
import chessengine.Misc.ChessConstants;
import chessengine.ChessRepresentations.XYcoord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GeneralChessFunctions {

    private static final Logger logger = LogManager.getLogger("General_Chess_Functions");

    public static long AddPeice(int x, int y, long bitboard) {
        return AddPeice(positionToBitIndex(x, y), bitboard);
    }

    // use bitwise OR operator to add a bit representation of piece at the bitIndex
    public static long AddPeice(int bitIndex, long bitboard) {
        return bitboard | (1L << bitIndex);
    }

    public static long RemovePeice(int x, int y, long bitboard) {
        return RemovePeice(positionToBitIndex(x, y), bitboard);
    }

    // use bitwise And operator to remove a bit representation of piece at the bitIndex
    public static long RemovePeice(int bitIndex, long bitboard) {
        return bitboard & ~(1L << bitIndex);
    }

    public static boolean checkIfContains(int x, int y, long bitboard) {
        return checkIfContains(positionToBitIndex(x, y), bitboard);
    }

    public static boolean checkIfContains(int bitIndex, long bitboard) {
        return (positionToBitboard(bitIndex) & bitboard) != 0L;
    }
    // [0] = isHitPiece [1] = isWhitePiece

    /**
     * Checks the board for each color at a square [0] = isHitPiece [1] = isWhiteHitPiece
     **/
    public static boolean[] checkIfContains(int x, int y, BitBoardWrapper board, String callLoc) {
        long boardPosition = positionToBitboard(x, y);
        if (boardPosition == ChessConstants.EMPTYINDEX) {
            logger.error("Checkifcontains array is guilty: " + callLoc);
        }
        long[] whitePieces = board.getWhitePiecesBB();
        long[] blackPieces = board.getBlackPiecesBB();
        long bigWhiteSum = whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5];
        long bigBlackSum = blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5];


        if ((bigBlackSum & boardPosition) != 0L) {
            return new boolean[]{true, false};
        }
        if ((bigWhiteSum & boardPosition) != 0L) {
            return new boolean[]{true, true};
        }
        return new boolean[]{false, false};
    }

    public static boolean checkIfContains(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long boardPos = positionToBitboard(x, y);
        if (boardPos == ChessConstants.EMPTYINDEX) {
            logger.error("Check if contains nonarray is guity");
        }
        if (isWhite) {
            long[] whitePieces = board.getWhitePiecesBB();
            long bigWhiteSum = whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5];
            return (bigWhiteSum & boardPos) != 0L;

        } else {
            long[] blackPieces = board.getBlackPiecesBB();
            long bigBlackSum = blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5];
            return (bigBlackSum & boardPos) != 0L;
        }
    }


    public static long positionToBitboard(int x, int y) {
        if (isValidCoord(x, y)) {
            return positionToBitboard(positionToBitIndex(x, y));
        }
        logger.error("Invalid coords provided1: " + x + "," + y);
        return ChessConstants.EMPTYINDEX;
    }

    public static long positionToBitboard(int bitIndex) {
        // Create a long with the corresponding bit set to 1.
        return 1L << bitIndex;
    }

    public static int positionToBitIndex(int x, int y) {
        if (isValidCoord(x, y)) {
            return x + y * 8;
        }
        logger.error("Invalid coords provided2: " + x + "," + y);
        return ChessConstants.EMPTYINDEX;
    }

    public static List<XYcoord> getPieceCoordsForComputer(long[] Peices) {
        List<XYcoord> totalCoords = new ArrayList<>();
        for (int i = Peices.length - 1; i >= 0; i--) {
            List<XYcoord> peiceCoords = getPieceCoords(Peices[i]);
            int finalI = i;
            peiceCoords.forEach(c -> c.peiceType = finalI);
            totalCoords.addAll(peiceCoords);
        }
        return totalCoords;
    }

    public static List<XYcoord> getPieceCoords(long board) {
        List<XYcoord> coord = new ArrayList<>();
        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                int[] coords = BitFunctions.bitindexToXY(z);
                coord.add(new XYcoord(coords[0], coords[1]));
            }
        }

        return coord;
    }

    public static int[] getAllPieceIndexes(long board) {
        int[] indexes = new int[Long.bitCount(board)];
        int cnt = 0;
        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                indexes[cnt] = z;
                cnt++;
            }
        }

        return indexes;
    }



    public static boolean isValidCoord(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public static String getPieceType(int indx) {
        return switch (indx) {
            case 0 -> "Pawn";
            case 1 -> "Knight";
            case 2 -> "Bishop";
            case 3 -> "Rook";
            case 4 -> "Queen";
            case 5 -> "King";
            default -> null;
        };
    }

    public static int getBoardWithPiece(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long bitIndex = GeneralChessFunctions.positionToBitboard(x, y);
        if (bitIndex == ChessConstants.EMPTYINDEX) {
            logger.error("Get board with piece specific is guilty ");
        }
        long[] whitePieces = board.getWhitePiecesBB();
        long[] blackPieces = board.getBlackPiecesBB();
        if (isWhite) {
            for (int i = 0; i < whitePieces.length; i++) {
                long sum = bitIndex & whitePieces[i];
                if (sum != 0L) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < blackPieces.length; i++) {
                long sum = bitIndex & blackPieces[i];
                if (sum != 0L) {
                    return i;
                }
            }
        }
//        logger.debug("No piece found");
//      mainLogger.error(String.format("call from %s: no x,y satisified the getboardwith piece, index will be -10\ninfo: looking for coord x:%d y:%d isWhite?: %b kingLocation on boardfor color = X:%d Y:%d",callLocation,x,y,isWhite,isWhite ? board.getWhiteKingLocation().x : board.getBlackKingLocation().x,isWhite ? board.getWhiteKingLocation().y : board.getBlackKingLocation().y));
        return ChessConstants.EMPTYINDEX;
    }


    public static int getBoardWithPiece(int x, int y, BitBoardWrapper board) {
        long bitIndex = GeneralChessFunctions.positionToBitboard(x, y);
        long[] whitePieces = board.getWhitePiecesBB();
        long[] blackPieces = board.getBlackPiecesBB();
        for (int i = 0; i < blackPieces.length; i++) {
            long sum = bitIndex & blackPieces[i];
            if (sum != 0L) {
                return i;
            }
        }
        for (int i = 0; i < whitePieces.length; i++) {
            long sum = bitIndex & whitePieces[i];
            if (sum != 0L) {
                return i;
            }
        }
//      mainLogger.error(callLocation);
//      mainLogger.error(String.format("call from %s: no x,y satisified the getboardwith piece, index will be -10\ninfo: looking for coord x:%d y:%d isWhite?: %b kingLocation on boardfor color = X:%d Y:%d",callLocation,x,y,isWhite,isWhite ? board.getWhiteKingLocation().x : board.getBlackKingLocation().x,isWhite ? board.getWhiteKingLocation().y : board.getBlackKingLocation().y));
        return ChessConstants.EMPTYINDEX;
    }

    public static boolean isValidIndex(int index) {
        return index >= 0 && index <= 7;
    }

    public static void saveToFile(String fileName, String content) {
        File file = new File(fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(content);
            ChessConstants.mainLogger.debug("saving to file named: " + fileName);
        } catch (IOException e) {
            ChessConstants.mainLogger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    public static void printBoardSimple(BitBoardWrapper board) {
        logger.debug("\n" + getBoardSimpleString(board));

    }

    public static void printBoardDetailed(BitBoardWrapper board) {
        logger.debug("\n" + getBoardDetailedString(board));

    }


    public static String getBoardSimpleString(BitBoardWrapper board) {
        StringBuilder sb = new StringBuilder();
        sb.append("Board simple print: \n");
        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {
                boolean boardInfoW = GeneralChessFunctions.checkIfContains(file, row, true, board);
                boolean boardInfoB = GeneralChessFunctions.checkIfContains(file, row, false, board);
                if (boardInfoW && boardInfoB) {
                    sb.append("M");
                }
                if (boardInfoW || boardInfoB) {
                    int pieceIndex;
                    if (boardInfoW) {
                        pieceIndex = GeneralChessFunctions.getBoardWithPiece(file, row, true, board);

                    } else {
                        pieceIndex = GeneralChessFunctions.getBoardWithPiece(file, row, false, board);

                    }
                    String pieceString;
                    if (boardInfoW) {
                        // means a white piece
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex, false);

                    } else {
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex, false).toLowerCase();

                    }
                    sb.append(pieceString);
                } else {
                    // no piece
                    sb.append(" ");
                }
                if (file != 7) {
                    sb.append("|");
                } else {
                    sb.append("\n");
                }
            }


        }
        return sb.toString();

    }

    public static String getBoardDetailedString(BitBoardWrapper board) {
        StringBuilder sb = new StringBuilder();
        sb.append("Board detailed print: \n");
        for (int row = 0; row < 8; row++) {
            for (int file = 0; file < 8; file++) {
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(file, row, board, "gbd");
                if (boardInfo[0]) {
                    int pieceIndex = GeneralChessFunctions.getBoardWithPiece(file, row, boardInfo[1], board);
                    String pieceString;
                    if (boardInfo[1]) {
                        // means a white piece
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex, false);

                    } else {
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex, false).toLowerCase();

                    }
                    sb.append(pieceString);
                } else {
                    // no piece
                    sb.append(" ");
                }
                if (file != 7) {
                    sb.append("|");
                } else {
                    sb.append("\n");
                }
            }


        }
        return sb.toString();
    }

    public static int getPieceCount(long[] pieces) {
        int totalCount = 0;
        for (long piece : pieces) {
            totalCount += getPieceCount(piece);
        }
        return totalCount;

    }

    public static int getPieceCount(long board) {
        return Long.bitCount(board);
    }

    public static boolean isInsufiicientMaterial(BitBoardWrapper board) {
        if (getPieceCount(board.getWhitePiecesBB()[ChessConstants.PAWNINDEX]) > 0 || getPieceCount(board.getBlackPiecesBB()[ChessConstants.PAWNINDEX]) > 0) {
            return false; // only can be true if all pawns are off the board
        }
        int wqC = getPieceCount(board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        int bqC = getPieceCount(board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]);
        int wrC = getPieceCount(board.getWhitePiecesBB()[ChessConstants.ROOKINDEX]);
        int brC = getPieceCount(board.getBlackPiecesBB()[ChessConstants.ROOKINDEX]);
        int wbC = getPieceCount(board.getWhitePiecesBB()[ChessConstants.BISHOPINDEX]);
        int bbC = getPieceCount(board.getBlackPiecesBB()[ChessConstants.BISHOPINDEX]);
        int wnC = getPieceCount(board.getWhitePiecesBB()[ChessConstants.KNIGHTINDEX]);
        int bnC = getPieceCount(board.getBlackPiecesBB()[ChessConstants.KNIGHTINDEX]);
        if(wqC + bqC + wrC + brC > 0){
            // any of these pieces on the board means no draw
            return false;
        }
        if(wbC + wnC > 1 || bbC + bnC > 1){
            return false; // one side has a bishop + knight
        }
        if(wbC == 2 || bbC == 2){
            return false;
        }
        // Two knights can't force a checkmate unless the opponent has material
        if(wnC == 2 && bbC + bnC > 0 || bnC == 2 && bbC + bnC > 0){
            return false;
        }
        return true;
    }


}
