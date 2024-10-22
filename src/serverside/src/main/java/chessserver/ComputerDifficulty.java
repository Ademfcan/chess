package chessserver;

public enum ComputerDifficulty {
    // for stockfish none of these values matter at all
    STOCKFISHMax(3000, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 3500),
    STOCKFISHD1(2900, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 3200),
    STOCKFISHD2(2800, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2900),
    STOCKFISHD3(2700, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2800),
    STOCKFISHD4(2600, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2700),
    STOCKFISHD5(2500, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2600),
    StockfishD6(2400, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2500),
    StockfishD7(2300, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2400),
    StockfishD8(2200, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2300),
    StockfishD9(2100, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2200),
    StockfishD10(2000, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2100),
    StockfishD11(1900, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 2000),
    StockfishD12(1800, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 1900),
    StockfishD13(1700, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 1800),
    StockfishD14(1600, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, true, 1700),

//    D1(2800, 4, -0.5, 4, 1.5, 0.015, 15, 1, 0.8, 0.9, ServerConstants.QUEENINDEX, .2, true, false, false, 0),
//    D2(2600, 4, 0.4, 4, 1.5, 0.02, 15, 1, 1, .3, ServerConstants.ROOKINDEX, .3, true, false, false, 0),
//    D3(2400, 4, -1, 4, 1.5, 0.09, 10, 0.9, 0.4, 0.9, ServerConstants.ROOKINDEX, .3, true, false, false, 0),
//    D4(2200, 4, 10, 4, 1.5, 0.1, 15, 0.85, 0.9, 0.9, ServerConstants.PAWNINDEX, .4, true, false, false, 0),
//    D5(2100, 4, 0.2, 4, 1.5, 0.1, 15, 0.8, 0.3, 0.7, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
//
//    // Moderate levels with some randomness and balanced strategies
//    D6(2000, 4, -10000, 4, 1.5, 0, 10, 1, 0.5, 1, ServerConstants.KNIGHTINDEX, .3, true, false, false, 0),
//    D7(1900, 4, 0.2, 4, 1.5, 0.01, 9, 0.75, 1, 0.96, ServerConstants.QUEENINDEX, .5, true, false, false, 0),
//    D8(1800, 4, 0.1, 4, 1.5, .2, 13, 0.68, 0.7, 0.65, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
//    D9(1700, 4, 0.1, 4, 1.5, .2, 1, 0.69, 0.3, 0.3, ServerConstants.ROOKINDEX, .2, true, false, false, 0),
//    D10(1600, 4, 0.1, 4, 1.5, 0.4, 1, 1, 0.75, 0.95, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),

    // Lower intermediate levels with more randomness and less depth
    MaxDifficulty(1500, 5, 0.0, 5, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    D12(1400, 3, 0.0, 100, 1.5, 0.3, 8, 1, 0.75, 0.8, ServerConstants.QUEENINDEX, .3, true, false, false, 0),
    D13(1300, 3, 0.0, 100, 1.5, 0.85, 10, 1, 0.3, 0.85, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    D14(1200, 3, 0.0, 100, 1.5, 0.4, 7, 0.35, 0.65, 0.6, ServerConstants.KINGINDEX, .2, true, false, false, 0),
    D15(1100, 3, 0.0, 100, 1.5, 0.45, 7, 0.63, 0.62, 0.29, ServerConstants.PAWNINDEX, .4, true, false, false, 0),

    // Beginner levels with high randomness and little depth
    D16(1000, 3, 0.0, 2, 1.5, 0.7, 7, 0.75, 0.6, 0.8, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    D17(900, 3, 0.0, 2, 1.5, 0.75, 7, 0.22, 1, 0.8, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    D18(800, 3, 0.0, 2, 1.5, 0.8, 7, 0.75, 0.3, 0.9, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    D19(700, 3, 0.0, 2, 1.5, 0.85, 7, 0.9, 0.6, 0.9, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    D20(650, 3, 0.0, 2, 1.5, 0.9, 7, 0.6, 0.6, 0.9, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),

    // Trainer bots with more randomness and personality quirks
    T1(600, 3, 1, 10, 1.5, 0.3, 5, .6, 0.5, 0.7, ServerConstants.KNIGHTINDEX, .5, true, false, false, 0),
    T2(550, 2, 0.0, 15, 1.5, 0.1, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    T3(500, 2, 1, 15, 1.5, 0.85, 15, .5, .5, .5, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    T4(450, 2, 0.0, 15, 1.5, 0.6, 5, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    T5(400, 3, -10, 2, 1.5, .6, 1, .9, .6, .5, ServerConstants.EMPTYINDEX, 0, true, false, false, 0),
    T6(350, 2, -1, 9, 1.5, .6, 8, .9, .2, .6, ServerConstants.QUEENINDEX, .7, true, false, false, 0),
    T7(300, 2, 1, 6, 1.5, .4, 3, .5, 1, .3, ServerConstants.KINGINDEX, .7, true, false, false, 0),
    T8(250, 3, 3, 9, 1.5, .3, 8, 1, 1, 1, ServerConstants.QUEENINDEX, 1, true, false, false, 0),
    T9(200, 2, -3, 8, 1.5, .7, 5, 1, 1, 1, ServerConstants.KNIGHTINDEX, .3, false, false, false, 0),
    T10(100, 2, 3, 5, 5, .9, 3, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, false, false, false, 0),
    /**
     * Custom Bots, these bots have custom personalities and can be thought of as special events
     **/
    MRSACRIFICE(600, 6, -10, 6, 0, .7, 3, 1, 0, 1, ServerConstants.QUEENINDEX, 0, true, true, false, 0),
    QUEENSIMP(1500, 6, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.QUEENINDEX, 1, true, true, false, 0);
    public final int eloRange;
    public final int depth;
    public final double drawConst;
    public final int maxRandomChoices;
    public final double advantageThreshold;
    public final double randomnessFactor;

    public final int minRandomChoices;


    public final double maxAgressiveness;
    public final double maxDefensiveness;
    public final double maxRisk;
    public final int favoritePieceIndex;
    public final double favoritePieceWeight;
    public final boolean canWin;
    public final boolean isCustom;
    public final boolean isStockfishBased;
    public final int stockfishElo;


    ComputerDifficulty(int eloRange, int depth, double drawConst, int maxRandomChoices, double advantageThreshold, double randomnessFactor, int minRandomChoices, double maxAgressiveness, double maxDefensiveness, double maxRisk, int favoritePieceIndex, double favoritePieceWeight, boolean canWin, boolean isCustomBot, boolean isStockfishBased, int stockfishElo) {
        this.eloRange = eloRange;
        this.depth = depth;
        this.drawConst = drawConst;
        this.maxRandomChoices = maxRandomChoices;
        this.advantageThreshold = advantageThreshold;
        this.randomnessFactor = randomnessFactor;
        this.minRandomChoices = minRandomChoices;
        this.maxAgressiveness = maxAgressiveness;
        this.maxDefensiveness = maxDefensiveness;
        this.maxRisk = maxRisk;
        this.favoritePieceIndex = favoritePieceIndex;
        this.favoritePieceWeight = favoritePieceWeight;
        this.canWin = canWin;
        this.isCustom = isCustomBot;
        this.isStockfishBased = isStockfishBased;
        this.stockfishElo = stockfishElo;
    }

    public static ComputerDifficulty getDifficultyOffOfElo(int elo, boolean isCustom) {
        int closestDiff = 10000000;
        ComputerDifficulty closestDifficulty = null;
        for (ComputerDifficulty difficulty : ComputerDifficulty.values()) {
            if (difficulty.isCustom == isCustom) {
                int eloDiff = Math.abs(difficulty.eloRange - elo);
                if (eloDiff < closestDiff) {
                    closestDifficulty = difficulty;
                    closestDiff = eloDiff;
                }
            }
        }
        return closestDifficulty;
    }
}

