package chessserver.Enums;

public enum CampaignTier {
    TRAINING(10, "BackgroundImages/campaignBackgroundTraining.jpg", new int[]{4, 15, 3, 14, 7, 10, 1, 19, 6, 12}, new String[]{"Alex", "Beth", "Chris", "Dana", "Eli", "Faye", "Gabe",
            "Hana", "Ian", "Jill"}, new int[]{100, 150, 200, 300, 350, 400, 500, 600, 700, 800}),
    GENERAL(15, "BackgroundImages/campaignBackgroundGeneral.jpg", new int[]{2, 11, 13, 0, 19, 18, 17, 16, 1, 2, 3, 4, 8, 9, 10}, new String[]{"Lana", "Max", "Nina", "Omar", "Pete", "Quinn",
            "Rina", "Sam", "Tina", "Uma", "Vera", "Walt", "Xena", "Yara", "Zane"}, new int[]{850, 900, 950, 1000, 1050, 1100, 1150, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900}),
    MASTER(5, "BackgroundImages/campaignBackgroundMaster.jpg", new int[]{3, 4, 5, 6, 7}, new String[]{"Leah", "Nico", "Owen", "Maya", "Kyle"}, new int[]{2000, 2100, 2400, 2700, 3000});
    public static final CampaignTier LastTier = CampaignTier.values()[CampaignTier.values().length - 1];
    public final int NLevels;
    public final String bgUrl;
    public final int[] pfpIndexes;
    public final int[] eloIndexes;
    public final String[] levelNames;

    CampaignTier(int nLevels, String bgUrl, int[] pfpIndexes, String[] levelNames, int[] eloIndexes) {
        this.NLevels = nLevels;
        this.bgUrl = bgUrl;
        this.pfpIndexes = pfpIndexes;
        this.levelNames = levelNames;
        this.eloIndexes = eloIndexes;
        if (pfpIndexes.length != NLevels) {
            System.out.println("Error pfp length does not match nlevels!, for enum:" + this);
        }
        if (levelNames.length != NLevels) {
            System.out.println("Error level name length does not match nlevels!, for enum:" + this);
        }
    }

    public static CampaignTier getNextTier(CampaignTier currentTier) {
        int nextOrdinal = currentTier.ordinal() + 1;
        if (nextOrdinal >= CampaignTier.values().length) {
            return LastTier;
        }
        return CampaignTier.values()[nextOrdinal];
    }
}
