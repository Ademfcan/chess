package chessengine.Graphics;

import chessserver.ChessRepresentations.GameInfo;

import java.util.List;

public interface ChessGameDisplayable {
    void onChessGames(List<GameInfo> gameInfo);
}
