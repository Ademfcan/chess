package chessengine.Crypto;

import chessengine.ChessRepresentations.ChessGame;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtils {
    public static String sha256AndBase64(String input) throws NoSuchAlgorithmException {
        // Get a SHA-256 message digest instance
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Compute the hash of the input string
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        // Encode the byte array into a Base64 string
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String chessGameToSaveString(ChessGame game){
        return game.hashCode() + "," + game.getGameName() + "," + game.getWhitePlayerName() + "," + game.getBlackPlayerName() + "," + game.getWhiteElo() + "," + game.getBlackElo() + "," + game.getWhitePlayerPfpUrl() + "," + game.getBlackPlayerPfpUrl() + "," + game.gameToPgn() + "," + game.isVsComputer() + "," + game.isWhiteOriented();
    }

    public static ChessGame gameFromSaveString(String saveString){
        String[] split = saveString.split(",");
        Arrays.stream(split).forEach(s -> s = s.trim());
        // index 0 = hashcode 1 = name 2 = player1 name 3 = player2 name 4 = player1 elo 5 = player2 elo 6 = player1pfp, 7 = player2pfp, 8 = game pgn 9 = isvsComputer 10  = isWhiteOriented
        return ChessGame.createGameFromSaveLoad(split[8], split[1], split[2], split[3], Integer.parseInt(split[4]), Integer.parseInt(split[5]), split[6], split[7], Boolean.parseBoolean(split[9]), Boolean.parseBoolean(split[10]), split[0]);
    }
}
