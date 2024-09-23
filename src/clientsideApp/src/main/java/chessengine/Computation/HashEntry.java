package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

public record HashEntry(long zobristKey, int depth, Flag flag, int value, ChessMove bestMove) {

}
