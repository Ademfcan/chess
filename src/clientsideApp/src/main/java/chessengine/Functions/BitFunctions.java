package chessengine.Functions;

import chessengine.ChessRepresentations.BitBoardWrapper;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.ChessRepresentations.XYcoord;
import chessengine.Computation.MagicBitboardGenerator;
import chessengine.Misc.ChessConstants;

import java.util.List;

public class BitFunctions {
    private static MagicBitboardGenerator m = new MagicBitboardGenerator();


    public static long generateRookMoveMask(int bitindex){
        int[] xy = bitindexToXY(bitindex);
        long yourPosBoard = 1L << bitindex;
        // shift a horizontal line bitmask from the bottom upwards by the y component of the bitindex
        long horizontalMask = 0xFFL << 8*xy[1];
        // shift a vertical line bitmask from the left to right by the x component of the bitindex
        // then & it with another bitmask to avoid overflow
        // lastly use bitwise or to handle wraparound
        long verticalMask = (0x0101010101010101L << (xy[0]));
//        verticalMask |= (0x8080808080808080L << (64-xy[0])) & 0x8080808080808080L;
        // remove your own square
        return (verticalMask | horizontalMask) & ~yourPosBoard;
    }

    public static long generateBishopMoveMask(int bitindex){
        int[] xy = bitindexToXY(bitindex);
        long yourPosBoard = 1L << bitindex;
        // find the bottom point of the line with slope 1 as to find the origin of bishop files
        int rightMinDist = Math.min(xy[0],xy[1]);
        int rightOriginX = xy[0]-rightMinDist;
        int rightOriginY = xy[1]-rightMinDist;
        // now find the bottom point of line with slope -1
        int leftMinDist = Math.min(7-xy[0],xy[1]);
        int leftOriginX = xy[0]+leftMinDist;
        int leftOriginY = xy[1]-leftMinDist;
        // shift the right mask by the right origin values
        long bishopRightToLeftMask = 0x8040201008040201L;
        bishopRightToLeftMask <<= rightOriginX;
        bishopRightToLeftMask &= generateClipMaskForRightX(rightOriginX); // Ensure no wrap-around occurs
        bishopRightToLeftMask <<= (8 * rightOriginY); // shift upward
        // shift the left mask by the left origin values
        long bishopLeftToRightMask = 0x0102040810204080L;
        bishopLeftToRightMask >>= (7-leftOriginX); // shift
        bishopLeftToRightMask &= generateClipMaskForLeftX(leftOriginX); // shift
        bishopLeftToRightMask <<= (8* leftOriginY); // shift upward

        // remove your own square
        return (bishopLeftToRightMask | bishopRightToLeftMask) & ~yourPosBoard;
    }

    public static long generateClipMaskForRightX(int x) {
        // This generates a mask to ensure the bishop's moves stay within valid columns (0-7)
        final long[] masks = {
                ~0L, // Column 0
                ~0L, // Column 1
                ~0x0100000000000000L, // Column 2
                ~0x0201000000000000L, // Column 3
                ~0x0402010000000000L, // Column 4
                ~0x0804020100000000L, // Column 5
                ~0x1008040201000000L, // Column 6
                ~0x2010080402010000L, // Column 7
        };

        return masks[x];
    }
    public static long generateClipMaskForLeftX(int x) {
        // This generates a mask to ensure the bishop's moves stay within valid columns (0-7)
        final long[] masks = {
                ~0x0102040810204080L, // Column 0
                ~0x0204081020408000L, // Column 1
                ~0x0408102040800000L, // Column 2
                ~0x0810204080000000L, // Column 3
                ~0x1020408000000000L, // Column 4
                ~0x2040800000000000L, // Column 5
                ~0x4080000000000000L, // Column 6
                ~0x8000000000000000L, // Column 7

        };

        return masks[x];
    }

    public static long generateRookMoveMaskNoEdges(int bitindex){
        long yourPosBoard = 1L << bitindex;
        // generates rook moves but excludes the edges of the board
        int[] xy = bitindexToXY(bitindex);
        // shift a horizontal line bitmask from the bottom upwards by the y component of the bitindex
        long horizontalMask = 0x7EL << 8*xy[1];
        // shift a vertical line bitmask from the left to right by the x component of the bitindex
        // then & it with another bitmask to avoid overflow
        // lastly use bitwise or to handle wraparound
        long verticalMask = (0x0001010101010100L << (xy[0]));
        return (verticalMask | horizontalMask) & ~yourPosBoard;
    }

    public static long generateBishopMoveMaskNoEdges(int bitindex){
        // generates bishop moves but excludes the edges of the board
        long normalBishopMoveMask = generateBishopMoveMask(bitindex);
        long cornerRemoveMask = 0xFF818181818181FFL;
        return normalBishopMoveMask & ~cornerRemoveMask;
    }

    public static long[] createAllBlockerBitMasks(long moveMask){
        int[] indexes = GeneralChessFunctions.getAllPieceIndexes(moveMask);
        int numBlockerPositions = 1 << indexes.length; // 2^(nz)
        long[] blockerPositions = new long[numBlockerPositions];
        for(int i = 0;i<numBlockerPositions;i++){
            for(int bitIndex = 0;bitIndex<indexes.length;bitIndex++){
                int bit  = (i >> bitIndex) & 1;
                blockerPositions[i] |= (long) bit << indexes[bitIndex];
            }
        }
        return blockerPositions;
    }

    public static int[] bitindexToXY(int bitIndex) {
        return new int[]{bitIndex % 8, bitIndex / 8};
    }

    public static long generateLegalRookMoveBitboard(int bitIndex, long blockerPosition) {
        long moveBitBoard = 0L;
        int[] xy = bitindexToXY(bitIndex);
        int[] dirsX = new int[]{-1,1,0,0};
        int[] dirsY = new int[]{0,0,1,-1};
        for(int i = 0;i<4;i++){
            int newX = xy[0] + dirsX[i];
            int newY = xy[1] + dirsY[i];
            while(GeneralChessFunctions.isValidCoord(newX,newY)){
                moveBitBoard = GeneralChessFunctions.AddPeice(newX,newY,moveBitBoard);
                if(GeneralChessFunctions.checkIfContains(newX,newY,blockerPosition)){
                    break;
                }
                newX += dirsX[i];
                newY += dirsY[i];

            }
        }
        return moveBitBoard;
    }
    public static long generateLegalBishopMoveBitboard(int bitIndex, long blockerPosition) {
        long moveBitBoard = 0L;
        int[] xy = bitindexToXY(bitIndex);
        int[] dirsX = new int[]{-1,1,-1,1};
        int[] dirsY = new int[]{-1,1,1,-1};
        for(int i = 0;i<4;i++){
            int newX = xy[0] + dirsX[i];
            int newY = xy[1] + dirsY[i];
            while(GeneralChessFunctions.isValidCoord(newX,newY)){
                moveBitBoard = GeneralChessFunctions.AddPeice(newX,newY,moveBitBoard);
                if(GeneralChessFunctions.checkIfContains(newX,newY,blockerPosition)){
                    break;
                }
                newX += dirsX[i];
                newY += dirsY[i];

            }
        }
        return moveBitBoard;
    }

    public static String getBitStr(long board){
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<64;i++){
            if(GeneralChessFunctions.checkIfContains(i,board)){
                sb.append("1");
            }
            else {
                sb.append("0");
            }
            if((i+1)%8 == 0){
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    public static String getBitStr(long board,int highlightBit){
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<64;i++){
            if(i == highlightBit){
                sb.append("X");
            }
            else if(GeneralChessFunctions.checkIfContains(i,board)){
                sb.append("1");
            }
            else {
                sb.append("0");
            }
            if((i+1)%8 == 0){
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static Long calculateRookAttackBitBoard(int bitIndex,boolean isWhite,boolean includeFriends,BitBoardWrapper board){
        long whitePieceMask = board.getWhitePieceMask();
        long blackPieceMask = board.getBlackPieceMask();
        long allPieceMask = whitePieceMask | blackPieceMask;
        long rookMaskNoEdges = BitFunctions.generateRookMoveMaskNoEdges(bitIndex);
        long blockerBitBoard = rookMaskNoEdges & allPieceMask;
        long key = m.getRookMagicKey(blockerBitBoard,bitIndex);
        long possibleMoves = m.rookMap[bitIndex][(int)key];
        // remove friendly pieces
        if(!includeFriends){
            return possibleMoves & ~ (isWhite ? whitePieceMask : blackPieceMask);
        }
        return possibleMoves;
    }

    public static Long calculateBishopAttackBitBoard(int bitIndex,boolean isWhite,boolean includeFriends,BitBoardWrapper board){
        long whitePieceMask = board.getWhitePieceMask();
        long blackPieceMask = board.getBlackPieceMask();
        long allPieceMask = whitePieceMask | blackPieceMask;
        long bishopMaskNoEdges = BitFunctions.generateBishopMoveMaskNoEdges(bitIndex);
        long blockerBitBoard = bishopMaskNoEdges & allPieceMask;
        long key = m.getBishopMagicKey(blockerBitBoard,bitIndex);
        Long possibleMoves = m.bishopMap[bitIndex][(int)key];
        // remove friendly pieces
        if(!includeFriends){
            return possibleMoves & ~ (isWhite ? whitePieceMask : blackPieceMask);
        }
        return possibleMoves;

    }

    // todo make these bitwise

    public static long calculatePawnMask(int index, boolean isWhitePiece, BitBoardWrapper bitBoardWrapper) {
        int[] xy = bitindexToXY(index);
        List<XYcoord> pawnMoves = AdvancedChessFunctions.calculatePawnMoves(xy[0],xy[1],isWhitePiece,new ChessPosition(bitBoardWrapper, ChessConstants.startMove),true, true);
        long mask = 0L;
        for(XYcoord c : pawnMoves){
            mask = GeneralChessFunctions.AddPeice(c.x,c.y,mask);
        }

        return mask;

    }

    public static long calculateKnightAttackBitBoard(int index, boolean isWhitePiece, BitBoardWrapper bitBoardWrapper) {
        int[] xy = bitindexToXY(index);
        List<XYcoord> pawnMoves = AdvancedChessFunctions.calculateKnightMoves(xy[0],xy[1],isWhitePiece,false,bitBoardWrapper,true, true);
        long mask = 0L;
        for(XYcoord c : pawnMoves){
           mask =  GeneralChessFunctions.AddPeice(c.x,c.y,mask);
        }
        return mask;
    }

    public static long calculateKingAttackBitboard(int index, boolean isWhitePiece, BitBoardWrapper bitBoardWrapper) {
        int[] xy = bitindexToXY(index);
        List<XYcoord> pawnMoves = AdvancedChessFunctions.basicKingMoveCalc(xy[0],xy[1],isWhitePiece,bitBoardWrapper, true);
        long mask = 0L;
        for(XYcoord c : pawnMoves){
            mask = GeneralChessFunctions.AddPeice(c.x,c.y,mask);
        }
        return mask;
    }
}
