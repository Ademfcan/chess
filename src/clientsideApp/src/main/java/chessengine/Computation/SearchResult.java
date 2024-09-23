package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

public record SearchResult(ChessMove bestMove, int evaluation, int depth) {

}
