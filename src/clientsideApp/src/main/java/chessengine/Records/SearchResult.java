package chessengine.Records;

import chessengine.ChessRepresentations.ChessMove;

public record SearchResult(ChessMove move, int evaluation, int depth, PVEntry[] pV) {

}
