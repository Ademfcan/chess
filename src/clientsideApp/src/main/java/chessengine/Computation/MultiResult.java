package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

import java.util.HashMap;

public record MultiResult(SearchResult[] results, HashMap<ChessMove,CachedPv> moveValues) {
}
