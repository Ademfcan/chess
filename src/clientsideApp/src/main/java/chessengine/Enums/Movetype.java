package chessengine.Enums;

import chessserver.ChessRepresentations.ChessMove;
import chessserver.ChessRepresentations.ChessPosition;

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
