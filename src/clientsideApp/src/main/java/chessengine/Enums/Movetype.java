package chessengine.Enums;

import chessengine.ChessRepresentations.ChessMove;
import chessengine.ChessRepresentations.ChessPosition;
import chessengine.Misc.ChessConstants;

public enum Movetype {
    FORK,
    SHADOW,
    NONE;

    public static Movetype getMoveType(ChessPosition pos){
        ChessMove move = pos.getMoveThatCreatedThis();
//        if(move.equals(ChessConstants.startMove)){
//            return Movetype.NONE;
//        }

        // todo
        return Movetype.NONE;



    }

}
