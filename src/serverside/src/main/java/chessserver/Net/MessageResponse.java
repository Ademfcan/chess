package chessserver.Net;

public interface MessageResponse extends Payload {
    record FailedToSendMessage() implements MessageResponse {}
    record FailedToRecieveResponse() implements MessageResponse { }

}
