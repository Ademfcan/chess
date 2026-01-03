package chessserver.Net.MessageTypes;

import chessserver.Net.DirectionalMesageType;
import chessserver.Net.MessagePath;
import chessserver.Net.Payload;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import chessserver.Net.TypedRequest;

import javax.annotation.Nullable;

public class MetricMessageTypes {
    public enum ClientRequest implements DirectionalMesageType, TypedRequest {
        GETNUMACTIVE(Payload.Empty.class, Payload.IntegerPayload.class),
        GETNUMINPOOL(Payload.StringPayload.class, Payload.IntegerPayload.class );

        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ClientRequest(Class<? extends Payload> payloadClass, @Nullable  Class<? extends Payload> responseClass) {
            this.payloadClass = payloadClass;
            this.responseClass = responseClass;
        }

        @Override
        public Class<? extends Payload> getPayloadClass() {
            return payloadClass;
        }

        @Override
        public Class<? extends Payload> getResponseClass() {
            return responseClass;
        }

        @Override
        public MessagePath getDirection() {
            return MessagePath.CLIENT_REQUEST;
        }

    }

    public enum ServerRequest implements DirectionalMesageType, TypedRequest {
        ;

        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ServerRequest(Class<? extends Payload> payloadClass, @Nullable Class<? extends Payload> responseClass) {
            this.payloadClass = payloadClass;
            this.responseClass = responseClass;
        }

        @Override
        public Class<? extends Payload> getPayloadClass() {
            return payloadClass;
        }

        @Nullable
        @Override
        public Class<? extends Payload> getResponseClass() {
            return responseClass;
        }
    }
}
