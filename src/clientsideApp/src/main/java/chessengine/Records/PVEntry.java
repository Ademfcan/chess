package chessengine.Records;

import chessserver.ChessRepresentations.ChessMove;
import chessengine.Enums.Movetype;

public record PVEntry(ChessMove pvMove, int pvEval, Movetype pvMoveType) {
}
