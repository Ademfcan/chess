package chessserver.ClientHandling;

import chessserver.ServerChessGame;
import chessserver.User.Client;

import java.util.HashMap;

public class GameHandler {
    HashMap<Client[], ServerChessGame> games;

    public GameHandler() {
        games = new HashMap<>();
    }


}
