package chessserver;

import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.ChessRepresentations.PlayerInfo;
import chessserver.ClientHandling.GameHandler;
import chessserver.Enums.Gametype;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Net.Message;
import chessserver.Net.MessageConfig;
import chessserver.Net.MessageTypes.ChessGameMessageTypes;
import chessserver.Net.Payload;
import chessserver.Net.PayloadTypes.ChessGamePayloadTypes;
import chessserver.User.BackendClient;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

public class ServerChessGame {
    Gametype gametype;
    BackendClient client1;
    BackendClient client2;
    ChessGame game;
    boolean isClient1Turn;
    int client1TimeLeft;
    int client2TimeLeft;
    ScheduledFuture<?> timeTickFuture;

    public ServerChessGame(BackendClient client1, BackendClient client2, boolean isClient1Turn, Gametype gametype) {
        this.gametype = gametype;
        this.client1 = client1;
        this.client2 = client2;
        this.isClient1Turn = isClient1Turn;
        this.client1TimeLeft = (int) gametype.getTimeUnit().toSeconds(gametype.getLength());
        this.client2TimeLeft = (int) gametype.getTimeUnit().toSeconds(gametype.getLength());
        game = ChessGame.createServerFollowerGame();
        ChessEndpoint.serverChessGameMessageHandler.sendMessage(
                new MessageConfig(
                    new Message(ChessGameMessageTypes.ServerRequest.ENTEREDGAME,
                    new ChessGamePayloadTypes.GameStartPayload(PlayerInfo.fromUser(client2.getClientInfo()), isClient1Turn)
                )).to(client1.getClientSession())
        );

        ChessEndpoint.serverChessGameMessageHandler.sendMessage(
                new MessageConfig(
                new Message(ChessGameMessageTypes.ServerRequest.ENTEREDGAME,
                        new ChessGamePayloadTypes.GameStartPayload(PlayerInfo.fromUser(client1.getClientInfo()), !isClient1Turn)
                )).to(client2.getClientSession())
        );

        timeTickFuture = GameHandler.sceduleNewTimeTick(this::oneSecondtimeTick);
        client1.setCurrentGame(this);
        client2.setCurrentGame(this);

    }

    private void oneSecondtimeTick(){
        int timeLeft;
        if(isClient1Turn){
            client1TimeLeft--;
            timeLeft = client1TimeLeft;

            if(client1TimeLeft <= 0){
                handleTimeRunout(false);
                return;
            }
        }
        else{
            client2TimeLeft--;
            timeLeft = client2TimeLeft;

            if(client2TimeLeft <= 0){
                handleTimeRunout(true);
                return;
            }
        }

        ChessEndpoint.serverChessGameMessageHandler.sendMessage(
                new MessageConfig(
                        new Message(ChessGameMessageTypes.ServerRequest.TIMETICK,
                        new Payload.IntegerPayload(timeLeft)))
                .to(client1.getClientSession())
        );

        ChessEndpoint.serverChessGameMessageHandler.sendMessage(
                new MessageConfig(
                        new Message(ChessGameMessageTypes.ServerRequest.TIMETICK,
                                new Payload.IntegerPayload(timeLeft)))
                        .to(client2.getClientSession())
        );
    }

    private void handleTimeRunout(boolean isClient1Win) {
        boolean isDraw = false;
        boolean[] gameInsufficientMaterial = GeneralChessFunctions.isTimedInsufiicientMaterial(game.getCurrentPosition().board);

        if(gameInsufficientMaterial[0]){
            // client won on time, but had insufficient material to checkmate. Thus this results in a draw
            if(isClient1Win == gameInsufficientMaterial[1]){
                isDraw = true;
            }
        }
        handleGameEnd(client1,isClient1Win,isDraw,false);
    }

    private void stopTimeTick(){
        if(timeTickFuture != null) {
            timeTickFuture.cancel(true);
            timeTickFuture = null;
        }
    }
    
    private void resetTimeTick(){
        if(timeTickFuture != null) {
            timeTickFuture.cancel(true);
        }
        timeTickFuture = GameHandler.sceduleNewTimeTick(this::oneSecondtimeTick);
    }

    public void makeMove(String pgn, BackendClient player) {
        stopTimeTick();
        game.makePgnMove(pgn);
        BackendClient turn = isClient1Turn ? client1 : client2;
        if (turn.equals(player)) {
            BackendClient send = isClient1Turn ? client2 : client1;
            isClient1Turn = !isClient1Turn;
            ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(
                    new Message(ChessGameMessageTypes.ServerRequest.GAMEMOVEFROMOPPONENT, new Payload.StringPayload(pgn)))
                    .to(send.getClientSession()));
        } else {
            ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(
                    new Message(ChessGameMessageTypes.ServerRequest.INVALIDREQUEST, new Payload.StringPayload("not your turn!")))
                    .to(player.getClientSession()));
        }


        if(game.getGameState().isGameOver()){
            handleGameEnd(client1,game.getGameState().isCheckMated()[1],game.getGameState().isStaleMated(),false);
        }
        else {
            resetTimeTick();
        }


    }

    public void sendChat(String message, BackendClient sender) {
        BackendClient receiever = sender.equals(client1) ? client2 : client1;

        ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message(ChessGameMessageTypes.ServerRequest.SENDCHAT,
                        new Payload.StringPayload(message))).to(receiever.getClientSession()));
    }

    public void handleGameEnd(BackendClient player, boolean isClient1Winner, boolean isDraw, boolean isEarlyExit) {
        stopTimeTick();
        boolean isClient1Request = player.equals(client1);
        int[] finalElos;
        if (isEarlyExit) {
            // if a client quits the game early, the other player automatically wins and the game is forfeited
            finalElos = calcEloWin(client1.getClientInfo().getUserelo(), client2.getClientInfo().getUserelo(), !isClient1Request, false);
        } else {
            finalElos = calcEloWin(client1.getClientInfo().getUserelo(), client2.getClientInfo().getUserelo(), isClient1Winner, isDraw);
        }

        String requester = isClient1Request ? client1.getClientInfo().getUserName() : client2.getClientInfo().getUserName();
        String opponent = isClient1Request ? client2.getClientInfo().getUserName() : client1.getClientInfo().getUserName();
        String winner = isEarlyExit ? (opponent) : (isClient1Winner ? client1.getClientInfo().getUserName() : client2.getClientInfo().getUserName());

        if(isEarlyExit){
            // update clients about game closing
            ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message( ChessGameMessageTypes.ServerRequest.GAMECLOSED,
                            new Payload.StringPayload(requester + " closed the game early"))).to(client1.getClientSession()));

            ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message( ChessGameMessageTypes.ServerRequest.GAMECLOSED,
                    new Payload.StringPayload(requester + " closed the game early"))).to(client2.getClientSession()));
        }
        else {
            String extraInfo = isDraw ? "Tie game" :  winner + " won the game";
            ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message( ChessGameMessageTypes.ServerRequest.GAMEFINISHED,
                    new Payload.StringPayload(extraInfo))).to(client1.getClientSession()));

            ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message( ChessGameMessageTypes.ServerRequest.GAMEFINISHED,
                    new Payload.StringPayload(extraInfo))).to(client2.getClientSession()));
        }

        // update clients about how much elo they won/lost
        ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message( ChessGameMessageTypes.ServerRequest.ELOUPDATE,
                new Payload.IntegerPayload(finalElos[0]))).to(client1.getClientSession()));

        ChessEndpoint.serverChessGameMessageHandler.sendMessage(new MessageConfig(new Message( ChessGameMessageTypes.ServerRequest.ELOUPDATE,
                new Payload.IntegerPayload(finalElos[1]))).to(client2.getClientSession()));

        // add game to database
        try {
            ChessEndpoint.serverDatabaseMessageHandler.addSavedGame(GameInfo.fromChessGame(game), false);
        }
        catch (SQLException e){
            e.printStackTrace(System.err);
        }

    }

    // 0 = p1 1 = p2
    private int[] calcEloWin(int player1elo, int player2elo, boolean isClient1Winner, boolean isDraw) {
        double p1Outcome = .5d, p2Outcome = .5d;
        // if draw both .5 else winner 1 loser 0
        if (!isDraw) {
            if (isClient1Winner) {
                p1Outcome = 1;
                p2Outcome = 0;
            } else {
                p1Outcome = 0;
                p2Outcome = 1;
            }
        }
        int kFactor = getKFactor(Math.min(player1elo, player2elo));
        int p1Win = player1elo + (int) (kFactor * (p1Outcome - getWinProbability(player1elo, player2elo)));
        int p2Win = player2elo + (int) (kFactor * (p2Outcome - getWinProbability(player2elo, player1elo)));
        return new int[]{p1Win, p2Win};
    }

    private int getKFactor(int minElo) {
        // todo include game history
        // eg higher k factor when less game history
        if (minElo >= 2400) {
            return 10;
        }
        return 30;

    }

    private double getWinProbability(int currentPlayerElo, int opponentElo) {
        return 1 / (1 + Math.pow(10, (currentPlayerElo - opponentElo) / 400.0d));
    }

    public void handleDrawRequest(BackendClient requester) {
        ChessEndpoint.serverChessGameMessageHandler.sendMessage(
                new MessageConfig(
                        new Message(ChessGameMessageTypes.ServerRequest.ASKINGFORDRAW))
                            .to((requester.equals(client1) ? client2 : client1).getClientSession()));
    }

    public void handleDrawUpdate(BackendClient updater,boolean isDraw) {
        ChessEndpoint.serverChessGameMessageHandler.sendMessage(
                new MessageConfig(
                        new Message(ChessGameMessageTypes.ServerRequest.DRAWACCEPTANCEUPDATE, new Payload.BooleanPayload(isDraw)))
                        .to((updater.equals(client1) ? client2 : client1).getClientSession()));
        if(isDraw){
            handleGameEnd(updater,false,true,false);
        }
        // else we do nothing;
    }
}
