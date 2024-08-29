package chessserver;

public class OutputMessage {
    ServerResponseType serverResponseType;
    String extraInformation;

    public OutputMessage() {
        // for json serialize
    }

    public OutputMessage(ServerResponseType serverResponseType, String extraInformation) {
        this.serverResponseType = serverResponseType;
        this.extraInformation = extraInformation;
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
}
