package chessserver.Net;

public enum MessagePath {
    CLIENT_REQUEST(Endpoint.CLIENT, Endpoint.SERVER, 1),
    SERVER_RESPONSE(Endpoint.SERVER, Endpoint.CLIENT, -1),

    SERVER_REQUEST(Endpoint.SERVER, Endpoint.CLIENT, 1),
    CLIENT_RESPONSE(Endpoint.CLIENT, Endpoint.SERVER, -1),
    // TODO make this more elegant
    BOTH(Endpoint.CLIENT, Endpoint.CLIENT, 0);

    // request = 1 (->) , response = -1 (<-), both = 0 (<->) (the "weighted sum" of directions)
    private final int direction;
    public final Endpoint from;
    public final Endpoint to;

    private MessagePath(Endpoint from, Endpoint to, int direction) {
        this.from = from;
        this.to = to;
        this.direction = direction;
    }

    public MessagePath responsePath(){
        return switch (this) {
            case CLIENT_REQUEST -> SERVER_RESPONSE;
            case CLIENT_RESPONSE -> SERVER_REQUEST;
            case SERVER_RESPONSE -> CLIENT_REQUEST;
            case SERVER_REQUEST -> CLIENT_RESPONSE;
            case BOTH -> BOTH;
        };
    }

    public enum Endpoint{
        CLIENT,
        SERVER,
    }

    public boolean isRequest(){
        return direction > -1;
    }

    public boolean isResponse(){
        return direction < 1;
    }
}
