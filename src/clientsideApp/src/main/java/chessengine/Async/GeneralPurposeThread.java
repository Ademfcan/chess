package chessengine.Async;

import chessengine.App;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.Computation.Computer;
import chessengine.Computation.EvalOutput;
import chessengine.Computation.SearchResult;
import chessengine.Computation.Searcher;
import chessengine.Misc.ChessConstants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class GeneralPurposeThread extends Task<Void> {

    private final Logger logger = LogManager.getLogger(this.toString());
    private boolean running = true;
    private Queue<Runnable> tasks;

    public GeneralPurposeThread() {
        this.tasks = new LinkedBlockingQueue<>();

    }

    public void addTask(Runnable r) {
        tasks.add(r);
    }


    public void stop() {
        tasks.clear();
    }

    @Override
    public Void call() {
        while (running) {
            if (!tasks.isEmpty()) {
                Runnable nextTask = tasks.poll();
                nextTask.run();


            }
            try {
                Thread.sleep(100);

            } catch (Exception e) {
                logger.error("Error general task sleep:", e);
            }
        }
        logger.debug("general task ending");
        return null;

    }


    public void endThread() {
        running = false;
        stop();
    }


}
