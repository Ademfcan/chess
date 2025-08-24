package chessserver.ChessRepresentations;

import chessserver.User.UserInfo;

import java.util.UUID;

public record GameInfo (UUID gameUUID, String gameName, PlayerInfo whitePlayer, PlayerInfo blackPlayer, String gamePgn) {

    public ChessGame toChessGame() {
        boolean isWhiteOriented = true; // default
        return ChessGame.createGameFromSaveLoad(gamePgn, gameName, whitePlayer, blackPlayer, isWhiteOriented);
    }

    public ChessGame toChessGame(UserInfo currentUserInfo, boolean isCurrentUserWhiteOriented) {
        boolean isWhiteOriented = PlayerInfo.fromUser(currentUserInfo).equals(whitePlayer);
        if(!isCurrentUserWhiteOriented){
            isWhiteOriented = false;
        }
        return ChessGame.createGameFromSaveLoad(gamePgn, gameName, whitePlayer, blackPlayer, isWhiteOriented);
    }

    public static GameInfo fromChessGame(ChessGame game) {
        return new GameInfo(game.getGameID(), game.getGameName(), game.getWhitePlayer(), game.getBlackPlayer(), game.gameToPgn());
    }

}
