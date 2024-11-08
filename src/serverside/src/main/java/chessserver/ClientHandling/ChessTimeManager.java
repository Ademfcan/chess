package chessserver.ClientHandling;

import chessserver.ServerChessGame;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChessTimeManager {
    private final ScheduledExecutorService timeManager;
    public ChessTimeManager(){
        timeManager = Executors.newScheduledThreadPool(10);
    }

    public void startGameTick(ServerChessGame game){
        timeManager.scheduleAtFixedRate(game::timeTick,0,1,TimeUnit.SECONDS);
    }

}
