package chessengine.CentralControlComponents;

public interface Resettable {
    /**This method will be called when you want to fully reset everything in a class. Eg reloading a new game**/
    void fullReset();
    /**This method will be called when you make a move, and won't do things like reload the chessboard**/
    void partialReset();
}
