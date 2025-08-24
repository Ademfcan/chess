package chessserver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JWTGenerator {
    private static final String SECRET;
    static {
        byte[] randomBytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(randomBytes);
        SECRET = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static final String ISSUERNAME = "Chess-app";

    public static final long expirySeconds = TimeUnit.MINUTES.toSeconds(30);

    public static String createAccessToken(UUID userUUID) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expirySeconds * 1000);

        return JWT.create()
                .withIssuer(ISSUERNAME)
                .withSubject(userUUID.toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public static boolean verifySignature(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWT.require(algorithm)
                    .withIssuer(ISSUERNAME)
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
