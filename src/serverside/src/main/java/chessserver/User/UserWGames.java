package chessserver.User;

import chessserver.ChessRepresentations.GameInfo;
import chessserver.Communication.User;

import java.util.List;

public record UserWGames (User user, List<GameInfo> games) { }
