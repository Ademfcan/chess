package chessengine.Records;

import chessengine.ChessRepresentations.ChessMove;
import chessengine.Enums.Movetype;

public record PVEntry(ChessMove pvMove, int pvEval, Movetype pvMoveType) {
}
