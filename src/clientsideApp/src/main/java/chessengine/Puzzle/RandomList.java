package chessengine.Puzzle;

import chessserver.Misc.ChessConstants;

import java.util.ArrayList;
import java.util.LinkedList;

public class RandomList<T> extends ArrayList<T> {
    public RandomList(){
        super();
    }

    public T getNext(){
        return super.get(ChessConstants.generalRandom.nextInt(super.size()));
    }

}
