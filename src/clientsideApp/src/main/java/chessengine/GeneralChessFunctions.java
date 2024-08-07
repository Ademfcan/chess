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

    private static Logger logger = LogManager.getLogger("General_Chess_Functions");

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

    // [0] = isHitPiece [1] = isWhitePiece
    public static boolean[] checkIfContains(int x, int y, BitBoardWrapper board,String callLoc){
        long boardPosition  = positionToBitboard(x,y);
        if(boardPosition == ChessConstants.EMPTYINDEX){
            logger.error("Checkifcontains array is guilty: " + callLoc);
        }
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        long bigWhiteSum = whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5];
        long bigBlackSum = blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5];

        if((bigWhiteSum & boardPosition) != 0L){
            return new boolean[]{true, true};
        }
        if((bigBlackSum & boardPosition) != 0L){
            return new boolean[]{true, false};
        }
        return new boolean[]{false,false};
    }

    public static boolean checkIfContains(int x, int y, boolean isWhite, BitBoardWrapper board){
        long boardPos  = positionToBitboard(x,y);
        if(boardPos == ChessConstants.EMPTYINDEX){
            logger.error("Check if contains nonarray is guity");
        }
        if(isWhite){
            long[] whitePieces = board.getWhitePieces();
            long bigWhiteSum = whitePieces[0] | whitePieces[1] | whitePieces[2] | whitePieces[3] | whitePieces[4] | whitePieces[5];
            return (bigWhiteSum & boardPos) != 0L;

        }
        else{
            long[] blackPieces = board.getBlackPieces();
            long bigBlackSum = blackPieces[0] | blackPieces[1] | blackPieces[2] | blackPieces[3] | blackPieces[4] | blackPieces[5];
            return (bigBlackSum & boardPos) != 0L;
        }
    }


    public static long positionToBitboard(int x, int y) {
        if(isValidCoord(x,y)){
            return positionToBitboard(positionToBitIndex(x,y));
        }
        logger.error("Invalid coords provided1: " + x + "," + y);
        return ChessConstants.EMPTYINDEX;
    }

    public static long positionToBitboard(int bitIndex) {
        // Create a long with the corresponding bit set to 1.
        return 1L << bitIndex;
    }

    public static int positionToBitIndex(int x, int y){
        if(isValidCoord(x,y)){
            return  x + y * 8;
        }
        logger.error("Invalid coords provided2: " + x + "," + y);
        return ChessConstants.EMPTYINDEX;
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

    public static boolean isValidCoord(int x, int y) {
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
        if(bitIndex == ChessConstants.EMPTYINDEX){
            logger.error("Get board with piece specific is guilty");
        }
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
//        logger.debug("No piece found");
//      mainLogger.error(String.format("call from %s: no x,y satisified the getboardwith piece, index will be -10\ninfo: looking for coord x:%d y:%d isWhite?: %b kingLocation on boardfor color = X:%d Y:%d",callLocation,x,y,isWhite,isWhite ? board.getWhiteKingLocation().x : board.getBlackKingLocation().x,isWhite ? board.getWhiteKingLocation().y : board.getBlackKingLocation().y));
        return ChessConstants.EMPTYINDEX;
    }


    public static int getBoardWithPiece(int x, int y, BitBoardWrapper board){
        long bitIndex = GeneralChessFunctions.positionToBitboard(x,y);
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        for(int i = 0; i<whitePieces.length;i++){
            long sum = bitIndex & whitePieces[i];
            if(sum != 0L){
                return i;
            }
        }
        for(int i = 0; i<blackPieces.length;i++){
            long sum = bitIndex & blackPieces[i];
            if(sum != 0L){
                return i;
            }
        }
//      mainLogger.error(callLocation);
//      mainLogger.error(String.format("call from %s: no x,y satisified the getboardwith piece, index will be -10\ninfo: looking for coord x:%d y:%d isWhite?: %b kingLocation on boardfor color = X:%d Y:%d",callLocation,x,y,isWhite,isWhite ? board.getWhiteKingLocation().x : board.getBlackKingLocation().x,isWhite ? board.getWhiteKingLocation().y : board.getBlackKingLocation().y));
        return ChessConstants.EMPTYINDEX;
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

    public static void printBoardSimple(BitBoardWrapper board){
        System.out.println("Board simple print: ");
        for(int row  = 0;row<8;row++){
            for(int file = 0;file<8;file++){
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(file,row,board,"pbs");
                if(boardInfo[0]){
                    System.out.print("X");
                }
                else{
                    // no piece
                    System.out.print(" ");
                }
                if(file != 7){
                    System.out.print("|");
                }
                else{
                    System.out.println();
                }
            }


        }

    }

    public static void printBoardDetailed(BitBoardWrapper board){
        System.out.println("Board detailed print: ");
        for(int row  = 0;row<8;row++){
            for(int file = 0;file<8;file++){
                boolean boardInfoW = GeneralChessFunctions.checkIfContains(file,row,true,board);
                boolean boardInfoB = GeneralChessFunctions.checkIfContains(file,row,false,board);
                if(boardInfoW && boardInfoB){
                    System.out.print("M");
                }
                if(boardInfoW || boardInfoB){
                    int pieceIndex;
                    if(boardInfoW){
                       pieceIndex = GeneralChessFunctions.getBoardWithPiece(file,row,true,board);

                    }
                    else{
                        pieceIndex = GeneralChessFunctions.getBoardWithPiece(file,row,false,board);

                    }
                    String pieceString;
                    if(boardInfoW){
                        // means a white piece
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex,false);

                    }
                    else{
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex,false).toLowerCase();

                    }
                    System.out.print(pieceString);
                }
                else{
                    // no piece
                    System.out.print("_");
                }
                if(file != 7){
                    System.out.print("|");
                }
                else{
                    System.out.println();
                }
            }


        }

    }



    public static String getBoardSimpleString(BitBoardWrapper board){
        StringBuilder sb = new StringBuilder();
        sb.append("Board simple print: \n");
        for(int row  = 0;row<8;row++){
            for(int file = 0;file<8;file++){
                boolean boardInfoW = GeneralChessFunctions.checkIfContains(file,row,true,board);
                boolean boardInfoB = GeneralChessFunctions.checkIfContains(file,row,false,board);
                if(boardInfoW && boardInfoB){
                    sb.append("M");
                }
                if(boardInfoW || boardInfoB){
                    int pieceIndex;
                    if(boardInfoW){
                        pieceIndex = GeneralChessFunctions.getBoardWithPiece(file,row,true,board);

                    }
                    else{
                        pieceIndex = GeneralChessFunctions.getBoardWithPiece(file,row,false,board);

                    }
                    String pieceString;
                    if(boardInfoW){
                        // means a white piece
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex,false);

                    }
                    else{
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex,false).toLowerCase();

                    }
                    sb.append(pieceString);
                }
                else{
                    // no piece
                    sb.append(" ");
                }
                if(file != 7){
                    sb.append("|");
                }
                else{
                    sb.append("\n");
                }
            }


        }
        return sb.toString();

    }

    public static String getBoardDetailedString(BitBoardWrapper board){
        StringBuilder sb = new StringBuilder();
        sb.append("Board detailed print: \n");
        for(int row  = 0;row<8;row++){
            for(int file = 0;file<8;file++){
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(file,row,board,"gbd");
                if(boardInfo[0]){
                    int pieceIndex = GeneralChessFunctions.getBoardWithPiece(file,row,boardInfo[1],board);
                    String pieceString;
                    if(boardInfo[1]){
                        // means a white piece
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex,false);

                    }
                    else{
                        pieceString = PgnFunctions.turnPieceIndexToStr(pieceIndex,false).toLowerCase();

                    }
                    sb.append(pieceString);
                }
                else{
                    // no piece
                    sb.append(" ");
                }
                if(file != 7){
                    sb.append("|");
                }
                else{
                    sb.append("\n");
                }
            }


        }
        return sb.toString();
    }



}
