package chessengine.Crypto;

import chessserver.ChessRepresentations.ChessGame;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtils {
    public static String sha256AndBase64(String input){
        try{
            // Get a SHA-256 message digest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute the hash of the input string
            byte[] hash = digest.digest(input.trim().getBytes(StandardCharsets.UTF_8));

            // Encode the byte array into a Base64 string
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (NoSuchAlgorithmException e){
            // given fixed algorithm, should never hit this
            e.printStackTrace();
            return null;
        }
    }
}
