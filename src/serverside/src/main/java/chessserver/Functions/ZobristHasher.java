package chessserver.Functions;

import chessserver.ChessRepresentations.BitBoardWrapper;

import java.util.Random;

public class ZobristHasher {
    private final long[][][] zobristTable;
    private final long[] zobristWhiteToMove;
    private final Random random;

    public ZobristHasher() {
        random = new Random(123456789); // Fixed seed for reproducibility
        zobristTable = new long[2][6][64]; // [color][pieceType][square]
        zobristWhiteToMove = new long[2];

        for (int color = 0; color < 2; color++) {
            for (int pieceType = 0; pieceType < 6; pieceType++) {
                for (int square = 0; square < 64; square++) {
                    zobristTable[color][pieceType][square] = random.nextLong();
                }
            }
        }
        zobristWhiteToMove[0] = random.nextLong();
        zobristWhiteToMove[1] = random.nextLong();
    }

    public long computeHash(BitBoardWrapper board) {
        long hash = 0L;

        // Loop over the two colors (white and black)
        for (int color = 0; color < 2; color++) {
            long[] piecesClr = color == 0 ? board.getWhitePiecesBB() : board.getBlackPiecesBB();

            // Loop over the six piece types (pawn, knight, bishop, rook, queen, king)
            for (int pieceType = 0; pieceType < 6; pieceType++) {
                long pieces = piecesClr[pieceType];

                // Loop over the set bits in the bitboard
                while (pieces != 0) {
                    int square = Long.numberOfTrailingZeros(pieces);
                    hash ^= zobristTable[color][pieceType][square];
                    pieces &= pieces - 1; // Clear the least significant bit
                }
            }
        }


        return hash;
    }

}
