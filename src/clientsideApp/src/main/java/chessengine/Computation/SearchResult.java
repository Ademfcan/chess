package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

public record SearchResult(ChessMove move, int evaluation, int depth, SearchPair[] pV) {

}
