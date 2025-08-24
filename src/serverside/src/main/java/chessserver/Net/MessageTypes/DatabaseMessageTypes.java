package chessserver.Net.MessageTypes;

import chessserver.Net.*;
import chessserver.Net.PayloadTypes.DatabaseMessagePayloadTypes;
import chessserver.Net.PayloadTypes.UserMessagePayloadTypes;

import javax.annotation.Nullable;

public class DatabaseMessageTypes {
    public enum ClientRequest implements DirectionalMesageType, TypedRequest {
        GETUSER(Payload.Empty.class, DatabaseMessagePayloadTypes.UserPayload.class),
        GETCHESSGAMES(Payload.IntegerPayload.class, DatabaseMessagePayloadTypes.GamesPayload.class),
        SaveChessGame(DatabaseMessagePayloadTypes.SaveGamePayload.class, null),
        SaveChessGames(DatabaseMessagePayloadTypes.SaveGamesPayload.class, null),
        LOADFULLUSER(Payload.IntegerPayload.class, DatabaseMessagePayloadTypes.UserWGamesPayload.class),

        UPDATEUSERPREF(DatabaseMessagePayloadTypes.UserPrefPayload.class, null),
        UPDATEUSERINFO(DatabaseMessagePayloadTypes.UserInfoPayload.class, null),
        UPDATEUSER(DatabaseMessagePayloadTypes.UserPayload.class, null),

        SENDFRIENDREQUEST(Payload.StringPayload.class, null),
        GETUUIDS(Payload.StringListPayload.class, Payload.IntegerListPayload.class),
        READINCOMINGFRIENDREQUESTS(Payload.Empty.class, DatabaseMessagePayloadTypes.UUIDSPayload.class),
        READOUTGOINGFRIENDREQUESTS(Payload.Empty.class, DatabaseMessagePayloadTypes.UUIDSPayload.class),
        READFRIENDS(Payload.Empty.class, DatabaseMessagePayloadTypes.UUIDSPayload.class),
        GETUSERNAMES(Payload.StringListPayload.class, Payload.StringListPayload.class),
        GETFRIENDDATA(DatabaseMessagePayloadTypes.UUIDSPayload.class, UserMessagePayloadTypes.FriendDataResponsePayload.class),
        GETRANK(Payload.IntegerPayload.class, Payload.IntegerPayload.class),
        MATCHALLUSERNAMES(Payload.StringPayload.class, UserMessagePayloadTypes.FriendDataResponsePayload.class),
        SENDACCEPTEDFRIENDREQUEST(DatabaseMessagePayloadTypes.UUIDPayload.class, null),
        ;

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

        @Override
        public MessagePath getDirection() {
            return MessagePath.CLIENT_REQUEST;
        }

    }

    public enum ServerRequest implements DirectionalMesageType, TypedRequest {
        SQLSUCESS(Payload.Empty.class, null), // todo possibly remove as not useful
        SQLMESSAGE(Payload.StringPayload.class, null),
        UPDATEFRIENDS(Payload.Empty.class, null);

        private final Class<? extends Payload> payloadClass;
        private final Class<? extends Payload> responseClass;

        ServerRequest(Class<? extends Payload> payloadClass, @Nullable  Class<? extends Payload> responseClass) {
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