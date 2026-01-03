package chessserver.Net.MessageTypes;

import chessserver.Net.DirectionalMesageType;
import chessserver.Net.MessagePath;
import chessserver.Net.Payload;
import chessserver.Net.PayloadTypes.ChessGamePayloadTypes;
import chessserver.Net.TypedRequest;

import javax.annotation.Nullable;

public class ChessGameMessageTypes{
    public enum ClientRequest implements DirectionalMesageType, TypedRequest{
        LEAVEWAITINGPOOL(Payload.Empty.class, null),
        SENDCHAT(Payload.StringPayload.class, null),
        CREATEGAME(Payload.StringPayload.class, null),
        MAKEMOVE(Payload.StringPayload.class, null),
        REQUESTDRAW(Payload.Empty.class, null),
        DRAWACCEPTANCEUPDATE(Payload.BooleanPayload.class, null),
        LEAVEGAME(Payload.StringPayload.class, null);


        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ClientRequest(Class<? extends Payload> payloadClass, @Nullable Class<? extends Payload> responseClass){
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
    }

    public enum ServerRequest implements DirectionalMesageType, TypedRequest {
        ASKINGFORDRAW(Payload.Empty.class, null),
        DRAWACCEPTANCEUPDATE(Payload.BooleanPayload.class, null),
        GAMECLOSED(Payload.StringPayload.class, null),
        GAMEFINISHED(Payload.StringPayload.class, null),
        TIMETICK(Payload.IntegerPayload.class, null),
        ENTEREDGAME(ChessGamePayloadTypes.GameStartPayload.class, null),
        CHATFROMOPPONENT(Payload.StringPayload.class, null),
        GAMEMOVEFROMOPPONENT(Payload.StringPayload.class, null),
        SENDCHAT(Payload.StringPayload.class, null),
        ELOUPDATE(Payload.IntegerPayload.class, null),
        GAMEEXITEDSUCESS(Payload.Empty.class, null),
        INVALIDREQUEST(Payload.StringPayload.class, null);

        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ServerRequest(Class<? extends Payload> payloadClass, @Nullable Class<? extends Payload> responseClass){
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
