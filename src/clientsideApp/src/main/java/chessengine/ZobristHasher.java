package chessengine;

import java.util.Random;

public class ZobristHasher {
    private final long[][][] zobristTable;
    private final Random random;

    public ZobristHasher() {
        random = new Random(123456789); // Fixed seed for reproducibility
        zobristTable = new long[2][6][64]; // [color][pieceType][square]

        for (int color = 0; color < 2; color++) {
            for (int pieceType = 0; pieceType < 6; pieceType++) {
                for (int square = 0; square < 64; square++) {
                    zobristTable[color][pieceType][square] = random.nextLong();
                }
            }
        }
    }

    public long computeHash(ChessPosition pos,boolean isWhiteMove) {
        long hash = 0L;

        for (int color = 0; color < 2; color++) {
            long[] piecesClr = color == 0 ? pos.board.getWhitePieces() : pos.board.getBlackPieces();
            for (int pieceType = 0; pieceType < 6; pieceType++) {
                long pieces = piecesClr[pieceType];
                while (pieces != 0) {
                    int square = Long.numberOfTrailingZeros(pieces);
                    hash ^= zobristTable[color][pieceType][square];
                    pieces &= pieces - 1;
                }
            }
        }

        return hash + (isWhiteMove ? -193 : 193);
    }
}
