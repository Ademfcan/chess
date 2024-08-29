package chessserver;

public enum ComputerDifficulty {
    MAXDIFFICULTY(3200, 5, 0, 4, 1.5, 0, 1, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false),
    D1(2800, 4, 0.5, 4, 1.5, 0.005, 25, 1, 0.8, 0.9, ServerConstants.EMPTYINDEX, 0, true, false),
    D2(2600, 4, 0.4, 4, 1.5, 0.05, 20, 1, 1, 1, ServerConstants.EMPTYINDEX, 0, true, false),
    D3(2400, 4, 0.3, 4, 1.5, 0.1, 1, 0.9, 0.7, 0.8, ServerConstants.EMPTYINDEX, 0, true, false),
    D4(2200, 4, 0.3, 4, 1.5, 0.1, 1, 0.85, 0.7, 0.75, ServerConstants.EMPTYINDEX, 0, true, false),
    D5(2100, 4, 0.2, 4, 1.5, 0.15, 1, 0.8, 0.65, 0.7, ServerConstants.EMPTYINDEX, 0, true, false),

    // Moderate levels with some randomness and balanced strategies
    D6(2000, 4, 0.2, 4, 1.5, 0.2, 1, 0.7, 0.6, 0.6, ServerConstants.EMPTYINDEX, 0, true, false),
    D7(1900, 4, 0.2, 4, 1.5, 0.25, 1, 0.65, 0.55, 0.6, ServerConstants.EMPTYINDEX, 0, true, false),
    D8(1800, 4, 0.1, 4, 1.5, 0.3, 1, 0.6, 0.5, 0.55, ServerConstants.EMPTYINDEX, 0, true, false),
    D9(1700, 4, 0.1, 4, 1.5, 0.35, 1, 0.6, 0.5, 0.5, ServerConstants.EMPTYINDEX, 0, true, false),
    D10(1600, 4, 0.1, 4, 1.5, 0.4, 1, 0.55, 0.45, 0.5, ServerConstants.EMPTYINDEX, 0, true, false),

    // Lower intermediate levels with more randomness and less depth
    D11(1500, 4, 0.0, 4, 1.5, 0.45, 1, 0.5, 0.4, 0.45, ServerConstants.EMPTYINDEX, 0, true, false),
    D12(1400, 3, 0.0, 3, 1.5, 0.5, 1, 0.45, 0.35, 0.4, ServerConstants.EMPTYINDEX, 0, true, false),
    D13(1300, 3, 0.0, 3, 1.5, 0.55, 1, 0.4, 0.3, 0.35, ServerConstants.EMPTYINDEX, 0, true, false),
    D14(1200, 3, 0.0, 3, 1.5, 0.6, 1, 0.35, 0.25, 0.3, ServerConstants.EMPTYINDEX, 0, true, false),
    D15(1100, 3, 0.0, 3, 1.5, 0.65, 1, 0.3, 0.2, 0.25, ServerConstants.EMPTYINDEX, 0, true, false),

    // Beginner levels with high randomness and little depth
    D16(1000, 2, 0.0, 2, 1.5, 0.7, 1, 0.25, 0.2, 0.2, ServerConstants.EMPTYINDEX, 0, true, false),
    D17(900, 2, 0.0, 2, 1.5, 0.75, 1, 0.2, 0.15, 0.15, ServerConstants.EMPTYINDEX, 0, true, false),
    D18(800, 2, 0.0, 2, 1.5, 0.8, 1, 0.15, 0.1, 0.1, ServerConstants.EMPTYINDEX, 0, true, false),
    D19(700, 2, 0.0, 2, 1.5, 0.85, 1, 0.1, 0.05, 0.05, ServerConstants.EMPTYINDEX, 0, true, false),
    D20(600, 2, 0.0, 2, 1.5, 0.9, 1, 0.05, 0.05, 0.05, ServerConstants.EMPTYINDEX, 0, true, false),

    // Trainer bots with more randomness and personality quirks
    T1(600, 2, 0.0, 2, 1.5, 0.95, 1, 0.05, 0.05, 0.05, ServerConstants.EMPTYINDEX, 0, true, false),
    T2(550, 2, 0.0, 2, 1.5, 0.9, 1, 0.05, 0.05, 0.05, ServerConstants.EMPTYINDEX, 0, true, false),
    T3(500, 2, 0.0, 2, 1.5, 0.85, 1, 0.05, 0.05, 0.05, ServerConstants.EMPTYINDEX, 0, true, false),
    T4(450, 2, 0.0, 2, 1.5, 0.8, 1, 0.05, 0.05, 0.05, ServerConstants.EMPTYINDEX, 0, true, false),
    T5(400, 3, 1, 4, 1.5, .6, 1, .2, .6, .5, ServerConstants.EMPTYINDEX, 0, true, false),
    T6(350, 2, 1, 4, 1.5, .6, 1, .5, 0, 0, ServerConstants.QUEENINDEX, .7, true, false),
    T7(300, 2, 1, 4, 1.5, .6, 3, .5, 0, 0, ServerConstants.EMPTYINDEX, 0, true, false),
    T8(250, 2, 3, 4, 1.5, .6, 3, .5, 0, 0, ServerConstants.EMPTYINDEX, 0, false, false),
    T9(200, 2, 3, 4, 1.5, .7, 2, .5, 0, 0, ServerConstants.EMPTYINDEX, 0, false, false),
    T10(100, 2, 3, 2, 5, .8, 2, .5, .5, 1, ServerConstants.EMPTYINDEX, 0, false, false),
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

