package chessengine.CentralControlComponents;

import chessengine.Async.BestNMovesTask;
import chessengine.Async.EvaluationBarTask;
import chessengine.Async.GetComputerMoveTask;
import chessengine.Computation.Computer;
import chessengine.Async.SimulationTask;
import chessserver.ComputerDifficulty;

public class ThreadController {
    private final Computer chessAiForEvalBar;
    private final Computer chessAiForNMoves;
    public Computer chessAiForBestMove;

//    private Computer chessAiForViewerAnalysis;
    public EvaluationBarTask evalTask;
    public GetComputerMoveTask computerTask;
    public BestNMovesTask nMovesTask;
    public SimulationTask simTask;

    public ThreadController(int defaultComputerDepth, int defaultEvaluationDepth, ChessCentralControl control) {
        chessAiForBestMove = new Computer(defaultComputerDepth);
        chessAiForEvalBar = new Computer(defaultEvaluationDepth);
        chessAiForNMoves = new Computer(defaultEvaluationDepth);

        simTask = new SimulationTask(new Computer(5), control);
        new Thread((simTask)).start();

        evalTask = new EvaluationBarTask(chessAiForEvalBar, control.mainScreenController, defaultEvaluationDepth);
        new Thread(evalTask).start();

        computerTask = new GetComputerMoveTask(chessAiForBestMove, control.mainScreenController,simTask);
        new Thread(computerTask).start();

        nMovesTask = new BestNMovesTask(chessAiForNMoves, control.mainScreenController, 4);
        new Thread((nMovesTask)).start();
    }


    public void stopAll() {
        evalTask.stop();
        computerTask.stop();
        nMovesTask.stop();
        simTask.stopAndReset();

    }

    public void killAll() {
        stopAll();
        evalTask.endThread();
        computerTask.endThread();
        nMovesTask.endThread();
        simTask.endThread();
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


    public void setEvalDepth(int depth) {
        if (chessAiForEvalBar.isRunning()) {
            evalTask.setOnScheduled(e -> {
                chessAiForEvalBar.setEvalDepth(depth);
            });

        } else {
            chessAiForEvalBar.setEvalDepth(depth);
        }
    }

    public void setNmovesDepth(int depth) {
        if (nMovesTask.isRunning()) {
            nMovesTask.setOnSucceeded(e -> {
                chessAiForNMoves.setEvalDepth(depth);
            });

        } else {
            chessAiForNMoves.setEvalDepth(depth);

        }
    }


    public void toggleSimPlay() {
        simTask.toggleSimulation();
    }

    public void startSimPlay() {
        simTask.startSimulation();
    }
}
