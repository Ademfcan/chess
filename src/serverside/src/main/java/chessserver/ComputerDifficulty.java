package chessserver;

public enum ComputerDifficulty {
    // for stockfish none of these values matter at all
    STOCKFISHLOL(3400, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false),
    MAXDIFFICULTY(3000, 4, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false),
    D1(2800, 4, -0.5, 4, 1.5, 0.015, 15, 1, 0.8, 0.9, ServerConstants.QUEENINDEX, .2, true, false),
    D2(2600, 4, 0.4, 4, 1.5, 0.02, 15, 1, 1, .3, ServerConstants.ROOKINDEX, .3, true, false),
    D3(2400, 4, -1, 4, 1.5, 0.09, 10, 0.9, 0.4, 0.9, ServerConstants.ROOKINDEX, .3, true, false),
    D4(2200, 4, 10, 4, 1.5, 0.1, 15, 0.85, 0.9, 0.9, ServerConstants.PAWNINDEX, .4, true, false),
    D5(2100, 4, 0.2, 4, 1.5, 0.1, 15, 0.8, 0.3, 0.7, ServerConstants.EMPTYINDEX, 0, true, false),

    // Moderate levels with some randomness and balanced strategies
    D6(2000, 4, -10000, 4, 1.5, 0, 10, 1, 0.5, 1, ServerConstants.KNIGHTINDEX, .3, true, false),
    D7(1900, 4, 0.2, 4, 1.5, 0.01, 9, 0.75, 1, 0.96, ServerConstants.QUEENINDEX, .5, true, false),
    D8(1800, 4, 0.1, 4, 1.5, .2, 13, 0.68, 0.7, 0.65, ServerConstants.EMPTYINDEX, 0, true, false),
    D9(1700, 4, 0.1, 4, 1.5, .2, 1, 0.69, 0.3, 0.3, ServerConstants.ROOKINDEX, .2, true, false),
    D10(1600, 4, 0.1, 4, 1.5, 0.4, 1, 1, 0.75, 0.95, ServerConstants.EMPTYINDEX, 0, true, false),

    // Lower intermediate levels with more randomness and less depth
    D11(1500, 4, 0.0, 4, 1.5, 0.45, 1, 1, .8, 0.65, ServerConstants.EMPTYINDEX, 0, true, false),
    D12(1400, 3, 0.0, 3, 1.5, 0.5, 1, 1, 0.75, 0.8, ServerConstants.QUEENINDEX, .3, true, false),
    D13(1300, 3, 0.0, 3, 1.5, 0.55, 1, 1, 0.3, 0.85, ServerConstants.EMPTYINDEX, 0, true, false),
    D14(1200, 3, 0.0, 3, 1.5, 0.6, 1, 0.35, 0.65, 0.6, ServerConstants.KINGINDEX, .2, true, false),
    D15(1100, 3, 0.0, 3, 1.5, 0.65, 1, 0.63, 0.62, 0.29, ServerConstants.PAWNINDEX, .4, true, false),

    // Beginner levels with high randomness and little depth
    D16(1000, 3, 0.0, 2, 1.5, 0.7, 1, 0.75, 0.6, 0.8, ServerConstants.EMPTYINDEX, 0, true, false),
    D17(900, 3, 0.0, 2, 1.5, 0.75, 1, 0.22, 1, 0.8, ServerConstants.EMPTYINDEX, 0, true, false),
    D18(800, 3, 0.0, 2, 1.5, 0.8, 1, 0.75, 0.3, 0.9, ServerConstants.EMPTYINDEX, 0, true, false),
    D19(700, 3, 0.0, 2, 1.5, 0.85, 1, 0.9, 0.6, 0.9, ServerConstants.EMPTYINDEX, 0, true, false),
    D20(650, 3, 0.0, 2, 1.5, 0.9, 1, 0.6, 0.6, 0.9, ServerConstants.EMPTYINDEX, 0, true, false),

    // Trainer bots with more randomness and personality quirks
    T1(600, 3, 1, 2, 1.5, 0.3, 5, .6, 0.5, 0.7, ServerConstants.KNIGHTINDEX, .5, true, false),
    T2(550, 2, 0.0, 2, 1.5, 0.1, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false),
    T3(500, 2, 1, 2, 1.5, 0.85, 15, .5, .5, .5, ServerConstants.EMPTYINDEX, 0, true, false),
    T4(450, 2, 0.0, 2, 1.5, 0.6, 5, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false),
    T5(400, 3, -10, 4, 1.5, .6, 1, .9, .6, .5, ServerConstants.EMPTYINDEX, 0, true, false),
    T6(350, 2, -1, 4, 1.5, .6, 8, .9, .2, .6, ServerConstants.QUEENINDEX, .7, true, false),
    T7(300, 2, 1, 4, 1.5, .4, 3, .5, 1, .3, ServerConstants.KINGINDEX, .7, true, false),
    T8(250, 3, 3, 4, 1.5, .3, 8, 1, 1, 1, ServerConstants.QUEENINDEX, 1, true, false),
    T9(200, 2, -3, 4, 1.5, .7, 5, 1, 1, 1, ServerConstants.KNIGHTINDEX, .3, false, false),
    T10(100, 2, 3, 2, 5, .9, 3, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, false, false),
    /**
     * Custom Bots, these bots have custom personalities and can be thought of as special events
     **/
    MRSACRIFICE(600, 6, -10, 6, 0, .7, 3, 1, 0, 1, ServerConstants.QUEENINDEX, 0, true, true),
    QUEENSIMP(1500, 6, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.QUEENINDEX, 1, true, true);
    public final int eloRange;
    public final int depth;
    public final double drawConst;
    public final int depthThreshold;
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


    ComputerDifficulty(int eloRange, int depth, double drawConst, int depthThreshold, double advantageThreshold, double randomnessFactor, int minRandomChoices, double maxAgressiveness, double maxDefensiveness, double maxRisk, int favoritePieceIndex, double favoritePieceWeight, boolean canWin, boolean isCustomBot) {
        this.eloRange = eloRange;
        this.depth = depth;
        this.drawConst = drawConst;
        this.depthThreshold = depthThreshold;
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

