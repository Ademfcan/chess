package chessengine;

import javafx.application.Platform;

import java.util.LinkedList;
import java.util.Queue;

public class FXInitQueue {
    private static final Queue<Runnable> initQueue = new LinkedList<>();
    private static boolean initialized = false;

    public static void runAfterInit(Runnable task) {
        if(initialized){
            Platform.runLater(task);
        }
        else{
            initQueue.add(task);
        }
    }

    public static void flush() {
        if(!initialized){
            initialized = true;
            while (!initQueue.isEmpty()) {
                initQueue.poll().run();
            }
        }
        else{
            throw new IllegalStateException("FXInitQueue already flushed, cannot flush again");
        }

    }
}
