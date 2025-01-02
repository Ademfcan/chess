package chessengine.Puzzle;

import java.util.LinkedList;

public class SequentialList<T> extends LinkedList<T> {
    public SequentialList(){
        super();
    }

    public T getNext(){
        T nextElement =  super.poll();
        if(nextElement != null){
            super.offerLast(nextElement);
        }
        return nextElement;
    }

}
