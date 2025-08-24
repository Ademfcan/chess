package chessserver.Net.MessageTypes;

import chessserver.Net.DirectionalMesageType;
import chessserver.Net.Payload;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;
import chessserver.Net.TypedRequest;

import javax.annotation.Nullable;

public class UserMessageTypes {
    public enum ClientRequest implements DirectionalMesageType, TypedRequest {
        GETJWTTOKEN(UserMessagePayloadTypes.RefreshAcessTokenPayload.class, UserMessagePayloadTypes.JWTAcessTokenPayload.class),
        SIGNUP(UserMessagePayloadTypes.SignUpPayload.class, Payload.BooleanPayload.class),
        LOGIN(UserMessagePayloadTypes.LoginPayload.class, UserMessagePayloadTypes.RefreshAcessTokenPayload.class),
        LOGOUT(UserMessagePayloadTypes.RefreshAcessTokenPayload.class, Payload.BooleanPayload.class),
        REFRESHVALID(UserMessagePayloadTypes.RefreshAcessTokenPayload.class, Payload.BooleanPayload.class),
        CHECKUSERNAMEOPEN(Payload.StringPayload.class, Payload.BooleanPayload.class);




        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ClientRequest(Class<? extends Payload> payloadClass, @Nullable Class<? extends Payload> responseClass) {
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

    public enum ServerRequest implements DirectionalMesageType, TypedRequest {
        ;
        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ServerRequest(Class<? extends Payload> payloadClass, @Nullable  Class<? extends Payload> responseClass) {
            this.payloadClass = payloadClass;
            this.responseClass = responseClass;        }

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
