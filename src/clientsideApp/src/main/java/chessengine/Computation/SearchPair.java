package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

public record SearchPair(ChessMove pvMove,int pvEval) {
}
