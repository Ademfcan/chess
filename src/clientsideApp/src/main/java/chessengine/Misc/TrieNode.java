package chessengine.Misc;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessserver.Misc.ChessConstants;

import java.util.*;

public class TrieNode{
    private int nodeDepth;
    private final List<String> terminalChildren; // likely will at most be one
    private final Map<String,TrieNode> childMap;
    public TrieNode(int nodeDepth){
        this.nodeDepth = nodeDepth;
        childMap = new HashMap<>();
        terminalChildren = new ArrayList<>();
    }

    public TrieNode getChild(String pgnPiece,boolean createNew){
        if(childMap.containsKey(pgnPiece)){
            return childMap.get(pgnPiece);
        }
        if(createNew){
            TrieNode childNode = new TrieNode(nodeDepth+1);
            childMap.put(pgnPiece,childNode);
            return childNode;
        }
        return null;
    }

    public String getTerminalChild(){
        if(terminalChildren.isEmpty()){
            return null;
        }
        // if there are multiple terminal children, that means that they are both equal depth, so just pick one
        int randidx = ChessConstants.generalRandom.nextInt(terminalChildren.size());
        return terminalChildren.get(randidx);
    }

    public List<String> getAllTerminalChildren(){
        return terminalChildren;
    }

    public boolean hasTerminalChild(){
        return !terminalChildren.isEmpty();
    }

    public void addTerminalChild(String terminalName){
        terminalChildren.add(terminalName);
    }

    public Collection<? extends TrieNode> getAllChildren() {
        return childMap.values();
    }

    public int getNodeDepth() {
        return nodeDepth;
    }
}
