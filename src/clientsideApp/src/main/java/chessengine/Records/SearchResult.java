package chessengine.Records;

import chessserver.ChessRepresentations.ChessMove;

public record SearchResult(ChessMove move, int evaluation, int depth, PVEntry[] pV) {

}
