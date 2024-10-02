package chessengine.Enums;

import chessengine.ChessRepresentations.ChessMove;
import chessengine.Misc.ChessConstants;

public enum PromotionType {
    ALL(new int[]{ChessConstants.KNIGHTINDEX,ChessConstants.BISHOPINDEX,ChessConstants.ROOKINDEX,ChessConstants.QUEENINDEX}),
    QUEENONLY(new int[]{ChessConstants.QUEENINDEX}),
    ROOKONLY(new int[]{ChessConstants.ROOKINDEX}),
    BISHOPONLY(new int[]{ChessConstants.BISHOPINDEX}),
    KNIGHTONLY(new int[]{ChessConstants.KNIGHTINDEX}),
    QUEENANDKNIGHT(new int[]{ChessConstants.KNIGHTINDEX,ChessConstants.QUEENINDEX}),
    QUEENANDBISHOP(new int[]{ChessConstants.BISHOPINDEX,ChessConstants.QUEENINDEX}),
    ROOKANDKNIGHT(new int[]{ChessConstants.KNIGHTINDEX,ChessConstants.ROOKINDEX});

    public int[] promotionRange;
    private PromotionType(int[] promotionRange){
        this.promotionRange = promotionRange;
    }




}

