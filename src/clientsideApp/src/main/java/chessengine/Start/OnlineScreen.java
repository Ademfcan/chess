package chessengine.Start;

import chessengine.App;
import chessengine.Enums.StartScreenState;
import chessengine.FXInitQueue;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.GraphicsFunctions;
import chessengine.Graphics.StartScreenSubWindow;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Enums.Gametype;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageTypes.MetricMessageTypes;
import chessserver.Net.Payload;
import javafx.application.Platform;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class OnlineScreen extends StartScreenSubWindow {

    private enum OnlineState{
        DISABLED_OFFLINE,
        DISABLED_LOGGEDOUT,
        ENABLED_CONNECTED_ONLINE
    }

    private OnlineState currentState = OnlineState.ENABLED_CONNECTED_ONLINE;

    public OnlineScreen(StartScreenController controller) {
        super(controller);
    }

    @Override
    public void initLayout() {
        controller.onlineErrorButton.prefHeightProperty().bind(controller.onlineErrorButton.prefWidthProperty().multiply(0.75));



    }

    @Override
    public void initGraphics() {
        BindingController.bindMediumText(controller.onlineErrorLabel);
        BindingController.bindSmallText(controller.onlineErrorButton);
        BindingController.bindXLargeText(controller.onlineErrorTitle);

    }

    @Override
    public void afterInit() {
        // online options
        controller.gameTypes.getItems().addAll(Arrays.stream(Gametype.values()).map(Gametype::getStrVersion).toList());
        controller.gameTypes.setOnAction(e -> {
            updateOnlineInfo();
        });

        // starting online game
        controller.multiplayerStart.setOnMouseClicked(e -> {
            if (!controller.gameTypes.getSelectionModel().isEmpty()) {
                String gameType = controller.gameTypes.getValue();
                App.changeToMainScreenOnline(
                        ChessGame.getOnlinePreInit(gameType, App.userManager.userInfoManager.getCurrentPlayerInfo()), gameType);             }

        });

        App.scheduledExecutorService.scheduleAtFixedRate(this::updateOnlineInfo, 0, 30, TimeUnit.SECONDS);
    }

    private void updateOnlineInfo(){
        if(!loggedin){
            return;
        }

        if(!controller.gameTypes.getSelectionModel().isEmpty()){
            App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(
                    new MessageConfig(
                            new Message(MetricMessageTypes.ClientRequest.GETNUMINPOOL,
                                    new Payload.StringPayload(controller.gameTypes.getValue())))
                            .onDataResponse((Payload.IntegerPayload numPoolers) -> {
                                Platform.runLater(() -> controller.numInPool.setText("In Gamemode: " + numPoolers.payload()));
                            }));

        }
        else{
            Platform.runLater(() -> controller.numInPool.setText("In Gamemode: - "));
        }

        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(
                new MessageConfig(
                        new Message(MetricMessageTypes.ClientRequest.GETNUMACTIVE, new Payload.Empty()))
                        .onDataResponse((Payload.IntegerPayload numOnline) -> {
                            Platform.runLater(() -> controller.numOnline.setText("Online Players: " + numOnline.payload()));
                        }));
    }


    private void showErrorScreen(boolean isError){
        GraphicsFunctions.toggleHideAndDisable(controller.connectedScreen, isError);
        GraphicsFunctions.toggleHideAndDisable(controller.onlineErrorScreen, !isError);
    }

    private void configureErrorScreen(String errorText, String onErrorText, Runnable onErrorClick){
        controller.onlineErrorLabel.setText(errorText);
        controller.onlineErrorButton.setText(onErrorText);
        controller.onlineErrorButton.setOnAction(event -> {onErrorClick.run();});
    }

    private void changeOnlineState(OnlineState state) {
        if(currentState == state){
            return;
        }
        currentState = state;


        System.out.println("Changing state: " +state);
        switch (state){
            case DISABLED_OFFLINE ->{
                configureErrorScreen("Disconnected from server", "Try to reconnect", App::reconnectClient);
                showErrorScreen(true);

            }
            case DISABLED_LOGGEDOUT -> {
                configureErrorScreen("You are logged out.", "Login", () -> setCurrentState(StartScreenState.USERSETTINGS));
                showErrorScreen(true);

            }
            case ENABLED_CONNECTED_ONLINE -> {
                showErrorScreen(false);
            }
        }
    }

    @Override
    public void resetState() {
        // nothing going on for now
    }


    private boolean online = true;
    private boolean loggedin = true;



    @Override
    public void onLogin(){
        loggedin = true;

        if(online){
            changeOnlineState(OnlineState.ENABLED_CONNECTED_ONLINE);
        }
        else{
            changeOnlineState(OnlineState.DISABLED_OFFLINE);
        }
    }

    @Override
    public void onLogout(){
        loggedin = false;

        // prescedence to offline. eg if offline thats the most important one
        if(online){
            changeOnlineState(OnlineState.DISABLED_LOGGEDOUT);
        }
        else{
            // offline always has prescedence
            changeOnlineState(OnlineState.DISABLED_OFFLINE);
        }
    }

    @Override
    public void onOnline(){
        online = true;

        if(loggedin){
            changeOnlineState(OnlineState.ENABLED_CONNECTED_ONLINE);
        }
        else{
            changeOnlineState(OnlineState.DISABLED_LOGGEDOUT);
        }
    }

    public void onOffline(){
        online = false;
        changeOnlineState(OnlineState.DISABLED_OFFLINE);
    }
}
