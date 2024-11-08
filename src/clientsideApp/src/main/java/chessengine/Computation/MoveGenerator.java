package chessengine.Computation;

import chessserver.ChessRepresentations.BackendChessPosition;
import chessserver.ChessRepresentations.ChessMove;
import chessserver.ChessRepresentations.XYcoord;
import chessengine.Enums.PromotionType;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;

import java.util.List;

public class MoveGenerator {
    public MoveGenerator() {

    }

    public ChessMove[] generateMoves(BackendChessPosition pos, boolean onlyCaptures, PromotionType promotionType) {
        ChessMove[] moves = new ChessMove[218];
        int cnt = 0;
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(pos.isWhiteTurn ? pos.board.getWhitePiecesBB() : pos.board.getBlackPiecesBB());
        for (XYcoord coord : peices) {
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x, coord.y, pos.isWhiteTurn, pos, pos.getGameState(), coord.peiceType, onlyCaptures);
            for (XYcoord move : piecePossibleMoves) {
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x, move.y, !pos.isWhiteTurn, pos.board);
                if (endSquarePiece == ChessConstants.KINGINDEX) {
                    System.out.println("You fucked up generate moves");
                    System.out.println(coord.peiceType);
                    System.out.println(pos.getMoveThatCreatedThis() != null ? pos.getMoveThatCreatedThis() : "null move");
                    System.out.println(coord);
                    System.out.println(move);
                    System.out.println(GeneralChessFunctions.getBoardDetailedString(pos.board));
                }
                int pawnEnd = pos.isWhiteTurn ? 0 : 7;
                boolean isCastle = coord.peiceType == ChessConstants.KINGINDEX && Math.abs(coord.x - move.x) > 1;
                boolean isPromo = coord.peiceType == ChessConstants.PAWNINDEX && move.y == pawnEnd;
                boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                boolean isEnPassant = coord.peiceType == ChessConstants.PAWNINDEX && !isEating && coord.x != move.x;
                if (!isPromo) {
                    ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y, ChessConstants.EMPTYINDEX, coord.peiceType, pos.isWhiteTurn, isCastle, isEating, endSquarePiece, isEnPassant, false);
                    moves[cnt++] = childMove;
                } else {
                    // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                    for (int allowedPromotionIndex : promotionType.promotionRange) {
                        ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y, allowedPromotionIndex, coord.peiceType, pos.isWhiteTurn, false, isEating, endSquarePiece, false, false);
                        moves[cnt++] = childMove;
                    }

                }
            }


        }
        return moves;
    }
}
