package chessserver.Communication;

import chessserver.Enums.ServerResponseType;

public class OutputMessage {
    ServerResponseType serverResponseType;
    String extraInformation;
    int uniqueId;

    public OutputMessage() {
        // for json serialize
    }

    public OutputMessage(ServerResponseType serverResponseType, String extraInformation, int uniqueId) {
        this.serverResponseType = serverResponseType;
        this.extraInformation = extraInformation;
        this.uniqueId = uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public ServerResponseType getServerResponseType() {
        return serverResponseType;
    }

    public void setServerResponseType(ServerResponseType serverResponseType) {
        this.serverResponseType = serverResponseType;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }

    @Override
    public String toString() {
        return "OutputMessage{" +
                "serverResponseType=" + serverResponseType +
                ", extraInformation='" + extraInformation + '\'' +
                ", uniqueId=" + uniqueId +
                '}';
    }
}
