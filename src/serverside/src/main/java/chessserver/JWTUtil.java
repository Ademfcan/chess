package chessserver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.UUID;

public class JWTUtil {
    public static boolean isExpired(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date exp = decodedJWT.getExpiresAt();
        if (exp == null) return false; // no expiration means "no expiry"
        return exp.before(new Date());
    }

    public static UUID getTokenUUID(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return UUID.fromString(decodedJWT.getSubject());
    }

}
