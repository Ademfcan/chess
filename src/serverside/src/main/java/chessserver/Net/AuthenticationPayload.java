package chessserver.Net;

public interface AuthenticationPayload extends Payload {

    record EmptyAuthentication() implements AuthenticationPayload {}
    record JWTAuthentication(String JwtToken, String device_ID) implements AuthenticationPayload {}
}
