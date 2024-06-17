package chessserver;

public class InputMessage {
    private FrontendClient client;
    private INTENT intent;

    public FrontendClient getClient() {
        return client;
    }

    public INTENT getIntent() {
        return intent;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setClient(FrontendClient client) {
        this.client = client;
    }

    public void setIntent(INTENT intent) {
        this.intent = intent;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }

    private String extraInformation;
    public InputMessage(){
        // for json serializable
    };

    public InputMessage(FrontendClient client,INTENT intent,String extraInformation){
        this.client = client;
        this.intent = intent;
        this.extraInformation = extraInformation;
    }
}
