package chessengine.Records;

import chessserver.ChessRepresentations.ChessMove;
import chessengine.Enums.Flag;

public record HashEntry(long zobristKey, int depth, Flag flag, int value, ChessMove bestMove) {

}
