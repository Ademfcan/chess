package chessengine.Misc;

import java.util.ArrayDeque;
import java.util.Queue;

/**Limited use case trie with pgn openings**/
public class ChessTrie {
    public TrieNode head;
    public ChessTrie(){
        head = new TrieNode(0);
    }
    /**Expects a pgn array where the entries are like ["e4","e5",...,"Chess opening"] **/
    public void addNewLine(String[] pgnLine){
        // last value is line name
        String pgnLineName = pgnLine[pgnLine.length-1];
        TrieNode finalNode = head;
        for(int i = 0;i<pgnLine.length-1;i++){
            finalNode = finalNode.getChild(pgnLine[i],true);
        }
        finalNode.addTerminalChild(pgnLineName);
    }

    public String getLineName(String[] pgnLine){
        TrieNode finalNode = head;
        for(String pgn : pgnLine){
            if(finalNode == null){
                return "";
            }
            finalNode = finalNode.getChild(pgn,false);
        }
        if(finalNode == null){
            return "";
        }
        // now find closest terminal child[s]
        Queue<TrieNode> search = new ArrayDeque<>();
        search.add(finalNode);
        while(!search.isEmpty()){
            TrieNode next = search.poll();
            if(next.hasTerminalChild()){
                return next.getTerminalChild();
            }
            search.addAll(next.getAllChildren());
        }
        return "";
    }

}
