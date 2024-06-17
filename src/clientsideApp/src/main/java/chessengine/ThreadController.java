package chessengine;

public class ThreadController {
    private Computer chessAiForBestMove;
    private Computer chessAiForEvalBar;
    private Computer chessAiForNMoves;

    private Computer chessAiForViewerAnalysis;

    public EvaluationBarTask evalTask;
    public GetComputerMoveTask computerTask;
    public BestNMovesTask nMovesTask;
    public ThreadController(int defaultComputerDepth, int defaultEvaluationDepth){
        chessAiForBestMove = new Computer(defaultComputerDepth);
        chessAiForEvalBar = new Computer(defaultEvaluationDepth);
        chessAiForNMoves = new Computer(defaultEvaluationDepth);
        evalTask = new EvaluationBarTask(chessAiForEvalBar, App.mainScreenController,defaultEvaluationDepth);
        new Thread(evalTask).start();
        computerTask = new GetComputerMoveTask(chessAiForBestMove, App.mainScreenController);
        new Thread(computerTask).start();
        nMovesTask = new BestNMovesTask(chessAiForNMoves,App.mainScreenController,4);
        new Thread((nMovesTask)).start();
    }




    public void stopAll(){
        evalTask.stop();
        computerTask.stop();
        nMovesTask.stop();

    }

    public void killAll(){
        evalTask.endThread();
        computerTask.endThread();
        nMovesTask.endThread();
    }

    public void setComputerDepth(int depth){
        if(!chessAiForBestMove.isRunning()){
            computerTask.stop();
            chessAiForBestMove.setEvalDepth(depth);

        }
    }
    public void setEvalDepth(int depth){
        if(!chessAiForEvalBar.isRunning()){
            evalTask.stop();
            chessAiForEvalBar.setEvalDepth(depth);

        }
    }
    public void setNmovesDepth(int depth){
        if(!nMovesTask.isRunning()){
            nMovesTask.stop();
            chessAiForNMoves.setEvalDepth(depth);

        }
    }
}
