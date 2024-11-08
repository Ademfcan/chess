package chessengine.Records;

import chessserver.ChessRepresentations.ChessMove;

import java.util.HashMap;

public record MultiResult(SearchResult[] results, HashMap<ChessMove, CachedPv> moveValues) {
}
