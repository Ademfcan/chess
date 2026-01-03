package chessserver.Net.PayloadTypes;

import chessserver.ChessRepresentations.PlayerInfo;
import chessserver.Enums.Gametype;
import chessserver.Net.Payload;

public class ChessGamePayloadTypes {
    public record GameStartPayload(PlayerInfo playerInfo, boolean areYouFirst) implements Payload {}
    public record GamePlayerPayload(String playerName, int playerElo, String pfpUrl) implements Payload {}
    public record PgnPayload(String pgnMove) implements Payload {}
    public record GameTypePayload(Gametype gameType) implements Payload {}

}

