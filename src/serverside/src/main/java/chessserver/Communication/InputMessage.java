package chessserver.Communication;

import chessserver.Enums.INTENT;
import chessserver.User.FrontendClient;

public class InputMessage {
    private static int currentUniqueId = Integer.MIN_VALUE;
    private FrontendClient client;
    private INTENT intent;
    private String extraInformation;
    private int uniqueId;

    public InputMessage() {
        // for json serializable
    }

    public InputMessage(FrontendClient client, INTENT intent, String extraInformation) {
        this.client = client;
        this.intent = intent;
        this.extraInformation = extraInformation;
        this.uniqueId = getMessageUUID();
    }

    public static int getCurrentUniqueId() {
        return currentUniqueId;
    }

    public static void setCurrentUniqueId(int currentUniqueId) {
        InputMessage.currentUniqueId = currentUniqueId;
    }

    private static int getMessageUUID() {
        currentUniqueId++;
        if (currentUniqueId == Integer.MAX_VALUE) {
            currentUniqueId = Integer.MIN_VALUE;
        }
        return currentUniqueId;

    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public FrontendClient getClient() {
        return client;
    }

    public void setClient(FrontendClient client) {
        this.client = client;
    }

    public INTENT getIntent() {
        return intent;
    }

    public void setIntent(INTENT intent) {
        this.intent = intent;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }
}
