package chessengine.Computation;

import chessengine.ChessRepresentations.ChessMove;

import java.util.Arrays;

public class PvBuffer {
    private final SearchResult[] results;
    private int head;
    private int tail = 0;
    public PvBuffer(int nMoves){
        results = new SearchResult[nMoves];
        head = -nMoves;
    }

    public boolean isFull(){
        return head >= 0;
    }


    public void putResult(SearchResult result){
        System.out.println("Putting");
        results[tail] = result;
        tail++;
        tail %= results.length;
        head++;
        if(head > 0){
            head %= results.length;
        }
    }

    public SearchResult[] getResults(){
        int size = head >= 0 ? results.length : tail;
        SearchResult[] clippedResults = new SearchResult[size];
        int start = Math.max(head,0);
        for(int i = 0;i<size;i++){
            clippedResults[i] = results[start];
            start++;
            start %= results.length;
        }

        return clippedResults;


    }



}
