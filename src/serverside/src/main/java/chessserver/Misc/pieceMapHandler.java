package chessserver.Misc;

public class pieceMapHandler {
    private static final double[][] pawnMap1 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {1, 2, 2, 2, 2, 2, 2, 2}
    };


    private static final double[][] knightMap1 = {
            {-0.4, -0.2, 0.0, 0.1, 0.1, 0.0, -0.2, -0.4},
            {-0.2, 0.0, 0.1, 0.3, 0.3, 0.1, 0.0, -0.2},
            {0.0, 0.1, 0.3, 0.4, 0.4, 0.3, 0.1, 0.0},
            {0.1, 0.3, 0.4, 0.6, 0.6, 0.4, 0.3, 0.1},
            {0.1, 0.3, 0.4, 0.6, 0.6, 0.4, 0.3, 0.1},
            {0.0, 0.1, 0.3, 0.4, 0.4, 0.3, 0.1, 0.0},
            {-0.2, 0.0, 0.1, 0.3, 0.3, 0.1, 0.0, -0.2},
            {-0.4, -0.2, 0.0, 0.1, 0.1, 0.0, -0.2, -0.4}
    };


    private static final double[][] bishopMap1 = {
            {-0.2, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.2},
            {-0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.1},
            {-0.1, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.1},
            {-0.1, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.1},
            {-0.1, 0.6, 0.7, 0.7, 0.7, 0.7, 0.6, -0.1},
            {-0.1, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.1},
            {-0.1, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.1},
            {-0.2, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.2}
    };


    private static final double[][] rookMap1 = {
            {0.0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.0},
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.3, 0.3, 0.4, 0.4, 0.3, 0.3, 0.1},
            {0.1, 0.3, 0.3, 0.4, 0.4, 0.3, 0.3, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.0}
    };


    private static final double[][] kingMap1 = {
            {0.3, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, 0, 0, 0.0, 0.0, 0.3}
    };


    private static final double[][] queenMap1 = {
            {-0.2, -0.1, 0.0, 0.1, 0.1, 0.0, -0.1, -0.2},
            {-0.1, 0.0, 0.1, 0.2, 0.2, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.3, 0.4, 0.4, 0.3, 0.1, 0.0},
            {0.1, 0.2, 0.4, 0.6, 0.6, 0.4, 0.2, 0.1},
            {0.1, 0.2, 0.4, 0.6, 0.6, 0.4, 0.2, 0.1},
            {0.0, 0.1, 0.3, 0.4, 0.4, 0.3, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.2, 0.2, 0.1, 0.0, -0.1},
            {-0.2, -0.1, 0.0, 0.1, 0.1, 0.0, -0.1, -0.2}
    };


    private static final double[][] pawnMap2 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private static final double[][] knightMap2 = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private static final double[][] bishopMap2 = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private static final double[][] rookMap2 = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private static final double[][] queenMap2 = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };

    private static final double[][] kingMap2 = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private static final double[][] queenMap3 = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };


    private static final double[][] pawnMap3 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private static final double[][] knightMap3 = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private static final double[][] bishopMap3 = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private static final double[][] rookMap3 = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private static final double[][] kingMap3 = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private static final double[][] queenMap4 = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };


    private static final double[][] pawnMap4 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private static final double[][] knightMap4 = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private static final double[][] bishopMap4 = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private static final double[][] rookMap4 = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private static final double[][] kingMap4 = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private static final double[][] queenMap5 = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };
    private static final double[][] pawnMap5 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private static final double[][] knightMap5 = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private static final double[][] bishopMap5 = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private static final double[][] rookMap5 = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private static final double[][] kingMap5 = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private static final double[][] queenMap6 = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };
    private static final double[][] pawnMap6 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private static final double[][] knightMap6 = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private static final double[][] bishopMap6 = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private static final double[][] rookMap6 = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private static final double[][] kingMap6 = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private static final double[][] queenMap7 = {
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0}

    };
    private static final double[][] pawnMap7 = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private static final double[][] knightMap7 = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private static final double[][] bishopMap7 = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private static final double[][] rookMap7 = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private static final double[][] kingMap7 = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, 0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };
    private static final int[][] startPawnMapCp1 = {
            {10, 20, 20, 20, 20, 20, 20, 10},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {10, 10, 10, 30, 30, 10, 10, 10},
            {20, 20, 20, 40, 40, 20, 20, 20},
            {30, 30, 30, 50, 50, 30, 30, 30},
            {40, 40, 40, 60, 60, 40, 40, 40},
            {50, 50, 50, 70, 70, 50, 50, 50},
            {50, 50, 50, 80, 80, 50, 50, 50},
            {100, 200, 200, 200, 200, 200, 200, 200}
    };
    private static final int[][] startKnightMapCp1 = {
            {-40, -20, 0, 10, 10, 0, -20, -40},
            {-20, 0, 10, 30, 30, 10, 0, -20},
            {0, 10, 30, 40, 40, 30, 10, 0},
            {10, 30, 40, 60, 60, 40, 30, 10},
            {10, 30, 40, 60, 60, 40, 30, 10},
            {0, 10, 30, 40, 40, 30, 10, 0},
            {-20, 0, 10, 30, 30, 10, 0, -20},
            {-40, -20, 0, 10, 10, 0, -20, -40}
    };
    private static final int[][] startBishopMapCp1 = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 20, 30, 30, 30, 30, 20, -10},
            {-10, 30, 50, 60, 60, 50, 30, -10},
            {-10, 60, 60, 70, 70, 60, 60, -10},
            {-10, 60, 70, 70, 70, 70, 60, -10},
            {-10, 60, 60, 70, 70, 60, 60, -10},
            {-10, 30, 20, 20, 20, 20, 30, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };
    private static final int[][] startRookMapCp1 = {
            {0, 10, 10, 10, 10, 10, 10, 0},
            {10, 20, 20, 20, 20, 20, 20, 10},
            {10, 20, 30, 30, 30, 30, 20, 10},
            {10, 30, 30, 40, 40, 30, 30, 10},
            {10, 30, 30, 40, 40, 30, 30, 10},
            {10, 20, 30, 30, 30, 30, 20, 10},
            {10, 20, 20, 20, 20, 20, 20, 10},
            {0, 10, 10, 10, 10, 10, 10, 0}
    };
    private static final int[][] startKingMapCp1 = {
            {30, 0, 0, 0, 0, 0, 0, 30},
            {0, -50, -50, -50, -50, -50, -50, 0},
            {0, -50, -30, -30, -30, -30, -50, 0},
            {-30, -50, -30, -30, -30, -30, -50, -30},
            {-50, -50, -30, -30, -30, -30, -50, -50},
            {0, -30, -30, -30, -30, -30, -50, 0},
            {0, -50, -50, -50, -50, -50, -50, 0},
            {30, 0, 0, 0, 0, 0, 0, 30}
    };
    private static final int[][] startQueenMapCp1 = {
            {-20, -10, 0, 10, 10, 0, -10, -20},
            {-10, 0, 10, 20, 20, 10, 0, -10},
            {0, 10, 30, 40, 40, 30, 10, 0},
            {10, 20, 40, 60, 60, 40, 20, 10},
            {10, 20, 40, 60, 60, 40, 20, 10},
            {0, 10, 30, 40, 40, 30, 10, 0},
            {-10, 0, 10, 20, 20, 10, 0, -10},
            {-20, -10, 0, 10, 10, 0, -10, -20}
    };
    public static final int[][][] startPieceMapsCp1 = {startPawnMapCp1, startKnightMapCp1, startBishopMapCp1, startRookMapCp1, startQueenMapCp1, startKingMapCp1};
    private static final int[][] endPawnMapCp1 = {
            {10, 20, 20, 20, 20, 20, 20, 10},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {10, 10, 10, 30, 30, 10, 10, 10},
            {20, 20, 20, 40, 40, 20, 20, 20},
            {30, 30, 30, 50, 50, 30, 30, 30},
            {40, 40, 40, 60, 60, 40, 40, 40},
            {50, 50, 50, 70, 70, 50, 50, 50},
            {50, 50, 50, 80, 80, 50, 50, 50},
            {100, 200, 200, 200, 200, 200, 200, 200}
    };
    private static final int[][] endKnightMapCp1 = {
            {0, 10, 20, 30, 30, 20, 10, 0},
            {10, 20, 30, 40, 40, 30, 20, 10},
            {20, 30, 50, 60, 60, 50, 30, 20},
            {30, 40, 60, 70, 70, 60, 40, 30},
            {30, 40, 60, 70, 70, 60, 40, 30},
            {20, 30, 50, 60, 60, 50, 30, 20},
            {10, 20, 30, 40, 40, 30, 20, 10},
            {0, 10, 20, 30, 30, 20, 10, 0}
    };
    private static final int[][] endBishopMapCp1 = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 20, 30, 30, 30, 30, 20, -10},
            {-10, 30, 50, 60, 60, 50, 30, -10},
            {-10, 60, 60, 70, 70, 60, 60, -10},
            {-10, 60, 70, 70, 70, 70, 60, -10},
            {-10, 60, 60, 70, 70, 60, 60, -10},
            {-10, 30, 20, 20, 20, 20, 30, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };
    private static final int[][] endRookMapCp1 = {
            {0, 10, 10, 10, 10, 10, 10, 0},
            {10, 20, 20, 20, 20, 20, 20, 10},
            {10, 20, 30, 30, 30, 30, 20, 10},
            {10, 30, 30, 40, 40, 30, 30, 10},
            {10, 30, 30, 40, 40, 30, 30, 10},
            {10, 20, 30, 30, 30, 30, 20, 10},
            {10, 20, 20, 20, 20, 20, 20, 10},
            {0, 10, 10, 10, 10, 10, 10, 0}
    };
    private static final int[][] endKingMapCp1 = {
            {10, 10, 15, 20, 20, 15, 10, 10},
            {20, 20, 30, 40, 40, 30, 20, 20},
            {30, 30, 50, 60, 60, 50, 30, 30},
            {40, 40, 60, 70, 70, 60, 40, 40},
            {40, 40, 60, 70, 70, 60, 40, 40},
            {50, 50, 50, 60, 60, 50, 50, 50},
            {40, 40, 40, 40, 40, 40, 40, 40},
            {40, 40, 40, 40, 40, 40, 40, 40}
    };
    private static final int[][] endQueenMapCp1 = {
            {-10, 0, 10, 20, 20, 10, 0, -10},
            {0, 10, 20, 30, 30, 20, 10, 0},
            {10, 20, 40, 50, 50, 40, 20, 10},
            {20, 30, 50, 60, 60, 50, 30, 20},
            {20, 30, 50, 60, 60, 50, 30, 20},
            {10, 20, 40, 50, 50, 40, 20, 10},
            {0, 10, 20, 30, 30, 20, 10, 0},
            {-10, 0, 10, 20, 20, 10, 0, -10}
    };
    public static final int[][][] endPieceMapsCp1 = {endPawnMapCp1, endKnightMapCp1, endBishopMapCp1, endRookMapCp1, endQueenMapCp1, endKingMapCp1};
    public static double[][][] maps1 = {pawnMap1, knightMap1, bishopMap1, rookMap1, queenMap1, kingMap1};
    public static double[][][] maps2 = {pawnMap2, knightMap2, bishopMap2, rookMap2, queenMap2, kingMap2};
    public static double[][][] maps3 = {pawnMap3, knightMap3, bishopMap3, rookMap3, queenMap3, kingMap3};
    public static double[][][] maps4 = {pawnMap4, knightMap4, bishopMap4, rookMap4, queenMap4, kingMap4};
    public static double[][][] maps5 = {pawnMap5, knightMap5, bishopMap5, rookMap5, queenMap5, kingMap5};
    public static double[][][] maps6 = {pawnMap6, knightMap6, bishopMap6, rookMap6, queenMap6, kingMap6};
    public static double[][][] maps7 = {pawnMap7, knightMap7, bishopMap7, rookMap7, queenMap7, kingMap7};

    public static double[][][] getMap(int piecesOnTheBoardCount) {
//        if(piecesOnTheBoardCount > 30){
//            // still at start of game
//            return maps1;
//        }
//        if(piecesOnTheBoardCount > 25){
//            return maps2;
//        }
//        if(piecesOnTheBoardCount > 20){
//            return maps3;
//        }
//        if(piecesOnTheBoardCount > 15){
//            return maps2;
//        }
//        if(piecesOnTheBoardCount > 10){
//            return maps4;
//        }
//        if(piecesOnTheBoardCount > 8){
//            return maps5;
//        }
//        if(piecesOnTheBoardCount > 6){
//            return maps6;
//        }
//        else{
//            return maps7;
//        }
        return maps1;

    }

}
