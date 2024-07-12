package chessengine;

public class ThreadController {
    private Computer chessAiForBestMove;
    private Computer chessAiForEvalBar;
    private Computer chessAiForNMoves;

    private Computer chessAiForViewerAnalysis;

    public EvaluationBarTask evalTask;
    public GetComputerMoveTask computerTask;
    public BestNMovesTask nMovesTask;
    public ThreadController(int defaultComputerDepth, int defaultEvaluationDepth,mainScreenController mainScreenController){
        chessAiForBestMove = new Computer(defaultComputerDepth);
        chessAiForEvalBar = new Computer(defaultEvaluationDepth);
        chessAiForNMoves = new Computer(defaultEvaluationDepth);
        evalTask = new EvaluationBarTask(chessAiForEvalBar, mainScreenController,defaultEvaluationDepth);
        new Thread(evalTask).start();
        computerTask = new GetComputerMoveTask(chessAiForBestMove, mainScreenController);
        new Thread(computerTask).start();
        nMovesTask = new BestNMovesTask(chessAiForNMoves,mainScreenController,4);
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
        if(chessAiForBestMove.isRunning()) {
            computerTask.setOnSucceeded(e -> {
                chessAiForBestMove.setEvalDepth(depth);
            });
        }
        else{
            chessAiForBestMove.setEvalDepth(depth);
        }
    }
    public void setEvalDepth(int depth){
        if(chessAiForEvalBar.isRunning()){
            evalTask.setOnScheduled(e->{
                chessAiForEvalBar.setEvalDepth(depth);
            });

        }
        else{
            chessAiForEvalBar.setEvalDepth(depth);
        }
    }
    public void setNmovesDepth(int depth){
        if(nMovesTask.isRunning()){
            nMovesTask.setOnSucceeded(e->{
                chessAiForNMoves.setEvalDepth(depth);
            });

        }
        else{
            chessAiForNMoves.setEvalDepth(depth);

        }
    }
}
