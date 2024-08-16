package chessengine;

public enum ComputerDifficulty {
    MAXDIFFICULTY(3200,6,0,4,1.5,0,1, 1, 1,ChessConstants.EMPTYINDEX, 0, true, false),
    D1(2800,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D2(2600,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D3(2400,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D4(2200,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D5(2100,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D6(2000,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D7(1900,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D8(1800,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D9(1700,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D10(1600,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D11(1500,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D12(1400,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D13(1300,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D14(1200,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D15(1100,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D16(1000,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D17(900,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D18(800,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D19(700,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    D20(600,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    /**trainers (These bots will have specific personalities as well, to make beginners chess more fun)**/
    T1(600,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T2(550,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T3(500,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T4(450,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T5(400,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T6(350,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T7(300,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T8(250,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T9(200,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    T10(100,10,0,4,1.5,0,.5, 0, 0,ChessConstants.EMPTYINDEX, 0, true, false),
    /** Custom Bots, these bots have custom personalities and can be thought of as special events**/
    MRSACRIFICE(600,6,-10,6,0,.7,1, 0, 1,ChessConstants.QUEENINDEX, 0, true, true),
    QUEENSIMP(1500,6,0,4,1.5,0,1, 1, 1,ChessConstants.QUEENINDEX, 1, true, true);
    public final int eloRange;
    public final int depth;
    public final double drawConst;
    public final int depthThreshold;
    public final double advantageThreshold;
    public final double randomnessFactor;


    public final double maxAgressiveness;
    public final double maxDefensiveness;
    public final double maxRisk;
    public final int favoritePieceIndex;
    public final double favoritePieceWeight;
    public final boolean canWin;
    public final boolean isCustom;


    ComputerDifficulty(int eloRange, int depth, double drawConst, int depthThreshold, double advantageThreshold, double randomnessFactor, double maxAgressiveness, double maxDefensiveness, double maxRisk, int favoritePieceIndex, double favoritePieceWeight, boolean canWin, boolean isCustomBot) {
        this.eloRange = eloRange;
        this.depth = depth;
        this.drawConst = drawConst;
        this.depthThreshold = depthThreshold;
        this.advantageThreshold = advantageThreshold;
        this.randomnessFactor = randomnessFactor;
        this.maxAgressiveness = maxAgressiveness;
        this.maxDefensiveness = maxDefensiveness;
        this.maxRisk = maxRisk;
        this.favoritePieceIndex = favoritePieceIndex;
        this.favoritePieceWeight = favoritePieceWeight;
        this.canWin = canWin;
        this.isCustom = isCustomBot;
    }

    public static ComputerDifficulty getDifficultyOffOfElo(int elo,boolean isCustom){
        int closestDiff = 10000000;
        ComputerDifficulty closestDifficulty = null;
        for(ComputerDifficulty difficulty : ComputerDifficulty.values()){
            if(difficulty.isCustom == isCustom){
                int eloDiff = Math.abs(difficulty.eloRange-elo);
                if(eloDiff < closestDiff){
                    closestDifficulty = difficulty;
                    closestDiff = eloDiff;
                }
            }
        }
        return closestDifficulty;
    }
}

