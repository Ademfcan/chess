package chessengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralChessFunctions {



    public static long AddPeice(int x, int y, long bitboard){
        return AddPeice(positionToBitIndex(x,y),bitboard);
    }

    // use bitwise OR operator to add a bit representation of piece at the bitIndex
    public static long AddPeice(int bitIndex, long bitboard){
        return bitboard | (1L << bitIndex);
    }

    public static long RemovePeice(int x, int y, long bitboard){
        return RemovePeice(positionToBitIndex(x,y), bitboard);
    }
    // use bitwise And operator to remove a bit representation of piece at the bitIndex
    public static long  RemovePeice(int bitIndex, long bitboard){
        return bitboard & ~(1L << bitIndex);
    }

    public static boolean checkIfContains(int x, int y, long bitboard){
        return checkIfContains(positionToBitIndex(x,y), bitboard);
    }

    public static boolean checkIfContains(int bitIndex , long bitboard){
        return (positionToBitboard(bitIndex) & bitboard) != 0L;
    }
    // [0] = isHitPiece [1] = isWhitePiece
    public static boolean[] checkIfContains(int x, int y, BitBoardWrapper board){
        long boardPosition  = GeneralChessFunctions.positionToBitboard(x,y);
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        for(long l : whitePieces){
            long sum = boardPosition & l;
            if(sum != 0L){
                return new boolean[]{true, true};
            }
        }
        for(long l : blackPieces){
            long sum = boardPosition & l;
            if(sum != 0L){
                return new boolean[]{true, false};
            }
        }
        return new boolean[]{false,false};
    }

    public static boolean checkIfContains(int x, int y, boolean isWhite, BitBoardWrapper board){
        long boardPos  = positionToBitboard(x,y);
        if(isWhite){
            for(long l : board.getWhitePieces()){
                long sum = boardPos & l;
                if(sum != 0L){
                    return true;
                }
            }
        }
        else{
            for(long l : board.getBlackPieces()){
                long sum = boardPos & l;
                if(sum != 0L){
                    return true;
                }
            }
        }
        return false;
    }


    public static long positionToBitboard(int x, int y) {
        return positionToBitboard(positionToBitIndex(x,y));
    }

    public static long positionToBitboard(int bitIndex) {
        // Create a long with the corresponding bit set to 1.
        return 1L << bitIndex;
    }

    public static int positionToBitIndex(int x, int y){
        return  x + y * 8;
    }

    public static List<XYcoord> getPieceCoordsForComputer(long[] Peices){
        List<XYcoord> totalCoords = new ArrayList<>();
        for(int i = Peices.length-1; i>=0;i--){
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
                int[] coords = bitindexToXY(z);
                coord.add(new XYcoord(coords[0],coords[1]));
            }
        }

        return coord;
    }



    public static int[] bitindexToXY(int bitIndex){
        return new int[] {bitIndex%8, bitIndex/8};
    }

    public static boolean isValidMove(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public static String getPieceType(int indx){
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

    public static int getBoardWithPiece(int x, int y, boolean isWhite, BitBoardWrapper board){
        long bitIndex = GeneralChessFunctions.positionToBitboard(x,y);
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        if(isWhite){
            for(int i = 0; i<whitePieces.length;i++){
                long sum = bitIndex & whitePieces[i];
                if(sum != 0L){
                    return i;
                }
            }
        }
        else{
            for(int i = 0; i<blackPieces.length;i++){
                long sum = bitIndex & blackPieces[i];
                if(sum != 0L){
                    return i;
                }
            }
        }
//        mainLogger.error(callLocation);
//        mainLogger.error(String.format("call from %s: no x,y satisified the getboardwith piece, index will be -10\ninfo: looking for coord x:%d y:%d isWhite?: %b kingLocation on boardfor color = X:%d Y:%d",callLocation,x,y,isWhite,isWhite ? board.getWhiteKingLocation().x : board.getBlackKingLocation().x,isWhite ? board.getWhiteKingLocation().y : board.getBlackKingLocation().y));
        return -10;
    }

    public static boolean isValidIndex(int index){
        return index >= 0 && index <= 7;
    }

    public static void saveToFile(String fileName, String content) {
        File file = new File(fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(content);
            ChessConstants.mainLogger.debug("saving to file named: "  + fileName);
        } catch (IOException e) {
            ChessConstants.mainLogger.error(Arrays.toString(e.getStackTrace()));
        }
    }



}
