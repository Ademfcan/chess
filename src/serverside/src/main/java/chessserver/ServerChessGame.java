package chessserver;

import chessserver.ChessRepresentations.ChessGame;
import chessserver.ClientHandling.ClientHandler;
import chessserver.ClientHandling.GameHandler;
import chessserver.Enums.Gametype;
import chessserver.Enums.ServerResponseType;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.User.BackendClient;

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
        ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.ENTEREDGAME, client2.getInfo().getUserName() + "," + client2.getInfo().getUserelo() + "," + client2.getInfo().getProfilePictureUrl() + "," + isClient1Turn,Integer.MAX_VALUE);
        ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.ENTEREDGAME, client1.getInfo().getUserName() + "," + client1.getInfo().getUserelo() + "," + client1.getInfo().getProfilePictureUrl() + "," + !isClient1Turn,Integer.MAX_VALUE);
        timeTickFuture = GameHandler.sceduleNewTimeTick(this::oneSecondtimeTick);
        client1.setCurrentGame(this);
        client2.setCurrentGame(this);

    }

    private void oneSecondtimeTick(){
        if(isClient1Turn){
            client1TimeLeft--;
            ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.TIMETICK, Integer.toString(client1TimeLeft),Integer.MAX_VALUE);
            ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.TIMETICK, Integer.toString(client1TimeLeft),Integer.MAX_VALUE);
            if(client1TimeLeft <= 0){
                handleTimeRunout(false);
            }
        }
        else{
            client2TimeLeft--;
            ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.TIMETICK, Integer.toString(client2TimeLeft),Integer.MAX_VALUE);
            ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.TIMETICK, Integer.toString(client2TimeLeft),Integer.MAX_VALUE);
            if(client2TimeLeft <= 0){
                handleTimeRunout(true);
            }
        }
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
            ClientHandler.sendMessage(send.getClientSession(), ServerResponseType.GAMEMOVEFROMOPPONENT, pgn,Integer.MAX_VALUE);
        } else {
            ClientHandler.sendMessage(player.getClientSession(), ServerResponseType.INVALIDOPERATION, "not your turn!",Integer.MAX_VALUE);
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
        ClientHandler.sendMessage(receiever.getClientSession(), ServerResponseType.CHATFROMOPPONENT, message,Integer.MAX_VALUE);
    }

    public void handleGameEnd(BackendClient player, boolean isClient1Winner, boolean isDraw, boolean isEarlyExit) {
        stopTimeTick();
        boolean isClient1Request = player.equals(client1);
        int[] finalElos;
        if (isEarlyExit) {
            // if a client quits the game early, the other player automatically wins and the game is forfeited
            finalElos = calcEloWin(client1.getInfo().getUserelo(), client2.getInfo().getUserelo(), !isClient1Request, false);
        } else {
            finalElos = calcEloWin(client1.getInfo().getUserelo(), client2.getInfo().getUserelo(), isClient1Winner, isDraw);
        }

        String requester = isClient1Request ? client1.getInfo().getUserName() : client2.getInfo().getUserName();
        String opponent = isClient1Request ? client2.getInfo().getUserName() : client1.getInfo().getUserName();
        String winner = isEarlyExit ? (opponent) : (isClient1Winner ? client1.getInfo().getUserName() : client2.getInfo().getUserName());

        if(isEarlyExit){
            // update clients about game closing
            ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.GAMECLOSED, requester + " closed the game early",Integer.MAX_VALUE);
            ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.GAMECLOSED, requester + " closed the game early",Integer.MAX_VALUE);
        }
        else {
            String extraInfo = isDraw ? "Tie game" :  winner + " won the game";
            ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.GAMEFINISHED, extraInfo,Integer.MAX_VALUE);
            ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.GAMEFINISHED, extraInfo,Integer.MAX_VALUE);
        }

        // update clients about how much elo they won/lost
        ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.ELOUPDATE, Integer.toString(finalElos[0]),Integer.MAX_VALUE);
        ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.ELOUPDATE, Integer.toString(finalElos[1]),Integer.MAX_VALUE);


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
        if(requester.equals(client1)){
            ClientHandler.sendMessage(client2.getClientSession(),ServerResponseType.ASKINGFORDRAW,"",Integer.MAX_VALUE);
        }
        else{
            ClientHandler.sendMessage(client1.getClientSession(),ServerResponseType.ASKINGFORDRAW,"",Integer.MAX_VALUE);
        }
    }

    public void handleDrawUpdate(BackendClient updater,boolean isDraw) {
        BackendClient otherPlayer = updater.equals(client1) ? client2 : client1;
        ClientHandler.sendMessage(otherPlayer.getClientSession(),ServerResponseType.DRAWACCEPTANCEUPDATE,Boolean.toString(isDraw),Integer.MAX_VALUE);
        if(isDraw){
            handleGameEnd(updater,false,true,false);
        }
        // else we do nothing;
    }
}
