package chessserver;

public class ChessGame {
    public ChessGame(BackendClient client1, BackendClient client2, boolean isClient1Turn, int gameLength) {
        this.client1 = client1;
        this.client2 = client2;
        this.isClient1Turn = isClient1Turn;
        this.gameLength = gameLength;
        ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.ENTEREDGAME, client2.getName() + "," + client2.getElo());
        ClientHandler.sendMessage(client2.getClientSession(), ServerResponseType.ENTEREDGAME, client1.getName() + "," + client1.getElo());
        ClientHandler.sendMessage(client1.getClientSession(), ServerResponseType.TURNINDICATOR,"");
        client1.setCurrentGame(this);
        client2.setCurrentGame(this);

    }

    BackendClient client1;
    BackendClient client2;
    boolean isClient1Turn;
    int gameLength;

    public void makeMove(String pgn,int timeElapsed,BackendClient player){

        BackendClient turn = isClient1Turn ? client1 : client2;
        if(turn.equals(player)){
            BackendClient send = isClient1Turn ? client2 : client1;
            isClient1Turn = !isClient1Turn;
            gameLength-=timeElapsed;
            ClientHandler.sendMessage(send.getClientSession(),ServerResponseType.GAMEMOVEFROMOPPONENT,pgn);
            ClientHandler.sendMessage(send.getClientSession(),ServerResponseType.TURNINDICATOR,"");
        }
        else{
            ClientHandler.sendMessage(player.getClientSession(), ServerResponseType.INVALIDOPERATION,"not your turn!");
        }


    }

    public void sendChat(String message,BackendClient sender){
        BackendClient receiever = sender.equals(client1) ? client2 : client1;
        ClientHandler.sendMessage(receiever.getClientSession(), ServerResponseType.CHATFROMOPPONENT,message);
    }

    public void closeGame(BackendClient player,boolean isClient1Winner,boolean isDraw,boolean isEarlyExit){
        boolean isClient1Request = player.equals(client1);
        int[] finalElos;
        if(isEarlyExit){
            // if a client quits the game early, the other player automatically wins and the game is forfeited
            finalElos = calcEloWin(client1.getElo(),client2.getElo(),!isClient1Request,false);
        }
        else{
            finalElos = calcEloWin(client1.getElo(),client2.getElo(),isClient1Winner,isDraw);
        }

        String requester = isClient1Request ? client1.getName() : client2.getName();
        String opponent = isClient1Request ? client2.getName() : client1.getName();
        String winner = isEarlyExit ? (opponent) : (isClient1Winner ?  client1.getName() : client2.getName());
        String extraInfo = isEarlyExit ? requester + " closed the game early" : winner + " won the game";

        // update clients about game closing
        ClientHandler.sendMessage(client1.getClientSession(),ServerResponseType.GAMECLOSED, extraInfo);
        ClientHandler.sendMessage(client2.getClientSession(),ServerResponseType.GAMECLOSED, extraInfo);

        // update clients about how much elo they won/lost
        ClientHandler.sendMessage(client1.getClientSession(),ServerResponseType.ELOUPDATE,Integer.toString(finalElos[0]));
        ClientHandler.sendMessage(client2.getClientSession(),ServerResponseType.ELOUPDATE,Integer.toString(finalElos[1]));


    }

    // 0 = p1 1 = p2
    private int[] calcEloWin(int player1elo,int player2elo,boolean isClient1Winner,boolean isDraw){
        double p1Outcome = .5d,p2Outcome = .5d;
        // if draw both .5 else winner 1 loser 0
        if(!isDraw){
            if(isClient1Winner){
                p1Outcome = 1;
                p2Outcome = 0;
            }
            else{
                p1Outcome = 0;
                p2Outcome = 1;
            }
        }
        int kFactor = getKFactor(Math.min(player1elo,player2elo));
        int p1Win = player1elo + (int) (kFactor*(p1Outcome-getWinProbability(player1elo,player2elo)));
        int p2Win = player2elo + (int) (kFactor*(p2Outcome-getWinProbability(player2elo,player1elo)));
        return new int[]{p1Win,p2Win};
    }

    private int getKFactor(int minElo){
        if(minElo >= 2400){
            return 10;
        }
        return 30;

    }

    private double getWinProbability(int currentPlayerElo,int opponentElo){
        return 1/(1+Math.pow(10,(currentPlayerElo-opponentElo)/400.0d));
    }

}
