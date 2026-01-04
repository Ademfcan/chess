package chessserver.Net;

import javax.annotation.Nullable;

public interface TypedRequest{
    Class<? extends Payload> getPayloadClass();
    @Nullable
    Class<? extends Payload> getResponseClass();

    @SuppressWarnings("unchecked")
    default <T extends Payload> T castPayload(Payload p) {
        if(p == null){
            return null;
        }

        if (!getPayloadClass().isAssignableFrom(p.getClass())) {
            throw new IllegalArgumentException("Invalid payload type, can't cast " + p.getClass().getName() + " to " + getPayloadClass().getName());
        }

        return (T) p;
    }

    default boolean expectsResponse(){
       return getResponseClass() != null;
    };


}
