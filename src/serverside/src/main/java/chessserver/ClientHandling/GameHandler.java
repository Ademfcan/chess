package chessserver.ClientHandling;

import chessserver.ServerChessGame;
import chessserver.User.Client;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameHandler {
    public final static ScheduledExecutorService timeTickExecutor = Executors.newScheduledThreadPool(4);


    public static ScheduledFuture<?> sceduleNewTimeTick(Runnable timeTick){
        return timeTickExecutor.scheduleAtFixedRate(timeTick,0,1, TimeUnit.SECONDS);
    }


}
