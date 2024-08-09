package chessengine;

public enum ComputerDifficulty {
    MAXDIFFICULTY(3200,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D1(2800,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D2(2600,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D3(2400,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D4(2200,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D5(2100,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D6(2000,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D7(1900,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D8(1800,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D9(1700,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D10(1600,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D11(1500,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D12(1400,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D13(1300,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D14(1200,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D15(1100,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D16(1000,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D17(900,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D18(800,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D19(700,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    D20(600,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    /**trainers (These bots will have specific personalies aswell, to make begginers chess more fun)**/
    T1(600,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T2(550,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T3(500,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T4(450,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T5(400,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T6(350,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T7(300,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T8(250,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T9(200,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    T10(100,10,0,4,1.5,0,.5,0,ChessConstants.EMPTYINDEX,false),
    /** Custom Bots, these bots have custom personalities and can be thought of as special events**/
    MRSACRIFICE(600,6,-10,6,0,.7,7,1,ChessConstants.QUEENINDEX,true);
    private int eloRange;
    private int depth;
    private double drawConst;
    private int depthThreshold;
    private double advantageThreshold;
    private double randomnessFactor;

    private boolean isCustom;


    ComputerDifficulty(int eloRange, int depth, double drawConst, int depthThreshold, double advantageThreshold, double randomnessFactor,double agressiveness,double stupidity,int favoritePieceIndex,boolean isCustomBot) {
        this.eloRange = eloRange;
        this.depth = depth;
        this.drawConst = drawConst;
        this.depthThreshold = depthThreshold;
        this.advantageThreshold = advantageThreshold;
        this.randomnessFactor = randomnessFactor;
    }

    public static ComputerDifficulty getDifficulyOffOfElo(int elo,boolean isCustom){
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

