package chessengine.CentralControlComponents;

import chessengine.Async.*;
import chessengine.Computation.Computer;
import chessserver.ComputerDifficulty;

public class ThreadController {
    public Computer chessAiForBestMove;

//    public EvaluationBarTask evalTask;
    public GetComputerMoveTask computerTask;
//    public BestNMovesTask nMovesTask;
    public SimulationTask simTask;
    public GeneralPurposeThread generalTask;

    public ThreadController(ChessCentralControl control) {
        chessAiForBestMove = new Computer();

        simTask = new SimulationTask(control);
        new Thread((simTask)).start();

//        evalTask = new EvaluationBarTask(new Computer(),control);
//        new Thread(evalTask).start();

        computerTask = new GetComputerMoveTask(chessAiForBestMove, control);
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
        if (chessAiForBestMove.isRunning()) {
            computerTask.setOnSucceeded(e -> {
                chessAiForBestMove.setCurrentDifficulty(newDiff);
            });
        } else {
            chessAiForBestMove.setCurrentDifficulty(newDiff);
        }
    }


    public void toggleSimPlay() {
        simTask.toggleSimulation();
    }

    public void startSimPlay() {
        simTask.startSimulation();
    }
}
