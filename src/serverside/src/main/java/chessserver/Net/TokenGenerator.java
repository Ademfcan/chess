package chessserver.Net;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    private static final int byteLength = 32; // 256 bits
    public static String generateSecureToken() {
        byte[] randomBytes = new byte[byteLength];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
