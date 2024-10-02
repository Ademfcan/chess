package chessengine.CentralControlComponents;

import chessengine.Async.GeneralPurposeThread;
import chessengine.Async.GetComputerMoveTask;
import chessengine.Async.SimulationTask;
import chessserver.ComputerDifficulty;

public class ThreadController {

    //    public EvaluationBarTask evalTask;
    public GetComputerMoveTask computerTask;
    //    public BestNMovesTask nMovesTask;
    public SimulationTask simTask;
    public GeneralPurposeThread generalTask;

    public ThreadController(ChessCentralControl control) {

        simTask = new SimulationTask(control);
        new Thread((simTask)).start();

//        evalTask = new EvaluationBarTask(new Computer(),control);
//        new Thread(evalTask).start();

        computerTask = new GetComputerMoveTask(ComputerDifficulty.MaxDifficulty, control);
        new Thread(computerTask).start();

//        nMovesTask = new BestNMovesTask(new Computer(),control);
//        new Thread(nMovesTask).start();

        generalTask = new GeneralPurposeThread();
        new Thread(generalTask).start();

    }


    public void stopAll() {
        computerTask.stop();
        simTask.stopAndReset();
//        evalTask.stop();
//        nMovesTask.stop();
        generalTask.stop();

    }

    public void killAll() {
        stopAll();
        computerTask.endThread();
        simTask.endThread();
//        evalTask.endThread();
//        nMovesTask.endThread();
        generalTask.endThread();
    }

    public void setComputerDifficulty(ComputerDifficulty newDiff) {
        computerTask.difficulty = newDiff;
    }


    public void toggleSimPlay() {
        simTask.toggleSimulation();
    }

    public void startSimPlay() {
        simTask.startSimulation();
    }
}
