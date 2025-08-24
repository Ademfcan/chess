//package simulatedplayer;
//
//import chessengine.Computation.Computer;
//import chessserver.ChessRepresentations.BackendChessPosition;
//import chessserver.ChessRepresentations.ChessMove;
//import chessserver.Enums.ComputerDifficulty;
//import chessserver.Enums.Gametype;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.*;
//import java.util.function.Function;
//
//public class SimulatedPlayer {
//    public static final int MaxConcurrentGames = 10;
//
//    private final List<SimulatedGame> playerGames;
//    private final Computer computer;
//
//    // Move request queue and processing thread
//    private final BlockingQueue<MoveRequest> moveQueue;
//    private final ExecutorService moveWorker;
//
//    public SimulatedPlayer() {
//        this.computer = new Computer();
//        this.playerGames = new LinkedList<>();
//
//        this.moveQueue = new LinkedBlockingQueue<>();
//        this.moveWorker = Executors.newSingleThreadExecutor();
//
//        startMoveProcessor();
//    }
//
//    private void startMoveProcessor() {
//        moveWorker.submit(() -> {
//            try {
//                while (true) {
//                    MoveRequest request = moveQueue.take(); // blocking
//                    ChessMove result = computer.getMove(request.difficulty, request.position);
//                    request.future.complete(result);
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        });
//    }
//
//    public boolean isFull() {
//        return numGames() >= MaxConcurrentGames;
//    }
//
//    public int numGames() {
//        return playerGames.size();
//    }
//
//    public void joinNewGame(Gametype gametype, ComputerDifficulty difficulty) {
//        if (playerGames.size() >= MaxConcurrentGames) {
//            throw new RuntimeException("Too many games for this player!");
//        }
//
//        Function<BackendChessPosition, ChessMove> newMove = (BackendChessPosition position) -> {
//            try {
//                CompletableFuture<ChessMove> future = new CompletableFuture<>();
//                moveQueue.put(new MoveRequest(position, difficulty, future));
//                return future.get(); // wait for result
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to compute move", e);
//            }
//        };
//
//        SimulatedGame[] holder = new SimulatedGame[1];
//        Runnable gameOver = () -> playerGames.remove(holder[0]);
//
//        SimulatedGame newGame = new SimulatedGame(gametype, difficulty, newMove, gameOver);
//        holder[0] = newGame;
//        playerGames.add(newGame);
//    }
//
//    // Clean up background thread
//    public void shutdown() {
//        for(SimulatedGame game : playerGames) {
//            game.endGame();
//        }
//        moveWorker.shutdownNow();
//    }
//
//    // Internal request class
//    private record MoveRequest (BackendChessPosition position, ComputerDifficulty difficulty, CompletableFuture<ChessMove> future) {
//    }
//}
