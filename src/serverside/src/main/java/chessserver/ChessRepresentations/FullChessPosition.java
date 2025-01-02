package chessserver.ChessRepresentations;

import chessserver.ChessRepresentations.ChessGameState;
import chessserver.ChessRepresentations.ChessPosition;

public record FullChessPosition(ChessPosition position, ChessGameState gameState,boolean isWhiteTurn){
}
