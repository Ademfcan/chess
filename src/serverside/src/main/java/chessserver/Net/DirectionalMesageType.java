package chessserver.Net;

public interface DirectionalMesageType {
    default MessagePath getDirection(){
        return switch (this.getClass().getSimpleName()) {
            case "ServerRequest" -> MessagePath.SERVER_REQUEST;
            case "ClientRequest" -> MessagePath.CLIENT_REQUEST;
            default ->
                    throw new IllegalArgumentException("Class implementing directionalMesageType must be named either ServerRequest or ClientRequest OR override getDirection");
        };
    };

}
