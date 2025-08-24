package chessengine.Net.ClientMessageHandlers;

import chessengine.App;
import chessengine.Audio.Effect;
import chessserver.ChessRepresentations.GameInfo;
import chessengine.Enums.Window;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Net.*;
import chessserver.Net.MessageTypes.ChessGameMessageTypes;
import chessserver.Net.PayloadTypes.ChessGamePayloadTypes;
import javafx.application.Platform;

import java.io.IOException;
import java.util.UUID;

public class ClientChessGameMessageHandler extends MessageHandler<ChessGameMessageTypes.ServerRequest> {

    public ClientChessGameMessageHandler(WebSocketConnection connection) {
        super(false, connection, ChessGameMessageTypes.ServerRequest.class, MessagePath.Endpoint.CLIENT);
    }

    public void leaveWaitingPool(){
        App.authenticatedMessageSender.sendAuthenticatedRetryableMessage(new MessageConfig(new Message(ChessGameMessageTypes.ClientRequest.LEAVEWAITINGPOOL, new Payload.Empty())));
        App.messager.removeLoadingCircles(Window.Main);
    }

    public void saveChessGame(GameInfo game){

    }

    public void readChessGames(UUID userID){

    }




    @Override
    protected void handleMessage(ChessGameMessageTypes.ServerRequest messageOption, Message message, boolean validJWT, UUID jwtUUID) throws IOException {
        Payload messagePayload = message.getMessagePayload();
        switch (messageOption) {
            case ASKINGFORDRAW -> Platform.runLater(() -> App.chessCentralControl.chessActionHandler.handleDrawRequest());
            case DRAWACCEPTANCEUPDATE -> {
                boolean drawAccepted = ((Payload.BooleanPayload) messagePayload).payload();

                if (drawAccepted) {
                    Platform.runLater(() -> {
                        App.chessCentralControl.chessActionHandler.appendNewMessageToChat("Draw Accepted");
                    });
                } else {
                    Platform.runLater(() -> {
                        App.chessCentralControl.chessActionHandler.appendNewMessageToChat("Draw Request Rejected");
                    });
                }
            }
            case GAMECLOSED -> {
                String closeMessage = ((Payload.StringPayload )messagePayload).payload();
                Platform.runLater(() -> {
                    App.chessCentralControl.chessActionHandler.appendNewMessageToChat(closeMessage);
                    App.chessCentralControl.gameHandler.gameWrapper.setOnlineGameFinished();
                    logger.debug("Game closed: " + closeMessage);
                });
            }
            case GAMEFINISHED -> {
                String finishMessage = ((Payload.StringPayload )messagePayload).payload();
                Platform.runLater(() -> {
                    App.chessCentralControl.mainScreenController.showGameOver(finishMessage);
                    App.chessCentralControl.gameHandler.gameWrapper.setOnlineGameFinished();
                    logger.debug("Game finished: " + finishMessage);
                });
            }
            case TIMETICK -> {
                int timeLeft = ((Payload.IntegerPayload) messagePayload).payload();
                Platform.runLater(() -> {
                    App.chessCentralControl.chessActionHandler.timeTick(timeLeft);
                    logger.debug("Time tick: " + timeLeft);
                });
            }

            case ENTEREDGAME -> {
                ChessGamePayloadTypes.GameStartPayload gamePlayer = (ChessGamePayloadTypes.GameStartPayload) messagePayload;
                Platform.runLater(() -> {
                    App.messager.removeLoadingCircles(Window.Main);
                    App.chessCentralControl.gameHandler.gameWrapper.initWebGame(
                            gamePlayer.playerInfo(), gamePlayer.areYouFirst());
                    App.chessCentralControl.chessActionHandler.appendNewMessageToChat(
                            "Game Started!\nName: " + gamePlayer.playerInfo().playerName() + "\nElo: " + gamePlayer.playerInfo().playerElo());
                });
            }
            case CHATFROMOPPONENT -> {
                String chatMessage = ((Payload.StringPayload )messagePayload).payload();
                // if linkedGame.isWhiteOriented() our player is white (as the board is "white" oriented)
                // so other player would be opposite of that
                ChessGame currentGame = App.chessCentralControl.gameHandler.gameWrapper.getGame();
                String otherPlayerName = currentGame.isWhiteOriented() ? currentGame.getBlackPlayerName() : currentGame.getWhitePlayerName();
                Platform.runLater(() -> {
                    App.chessCentralControl.chessActionHandler.appendNewMessageToChat("(%s): %s".formatted(otherPlayerName, chatMessage));
                    App.soundPlayer.playEffect(Effect.MESSAGE);
                });
            }
            case GAMEMOVEFROMOPPONENT -> {
                String pgnMove = ((Payload.StringPayload )messagePayload).payload();
                Platform.runLater(() -> {
                    App.chessCentralControl.gameHandler.gameWrapper.makePgnMove(pgnMove, true, App.userManager.userPreferenceManager.isAnimationsOff());
                });
            }
            case ELOUPDATE -> {
                int newElo = ((Payload.IntegerPayload) messagePayload).payload();
                Platform.runLater(() -> {
                    App.messager.sendMessage("New Elo: " + newElo);
                    App.userManager.userInfoManager.updateUserElo(newElo);
                });
            }


            case GAMEEXITEDSUCESS -> {
                logger.debug("Game exited sucessfully");
            }
        }
    }


}
