package chessengine.Records;

import chessserver.ChessRepresentations.ChessMove;
// todo eradicate this for searchresult

public record MoveOutput(ChessMove move, double advantage, int depth) {
}
