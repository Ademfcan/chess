package chessserver;

import jakarta.websocket.Session;
import java.util.HashMap;

public class GameHandler {
    HashMap<Client[],ChessGame> games;
    public GameHandler(){
        games = new HashMap<>();
    }



}
