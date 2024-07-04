package chessserver;

public enum CampaignTier {
    TRAINING(10,"BackgroundImages/campaignBackgroundTraining.jpg",new int[]{4,15,3,14,7,10,1,19,6,12},new String[]{"Alex", "Beth", "Chris", "Dana", "Eli", "Faye", "Gabe",
                                                                                                                        "Hana", "Ian", "Jill"}),
    GENERAL(15,"BackgroundImages/campaignBackgroundGeneral.jpg",new int[]{2,11,13,0,19,18,17,16,1,2,3,4,8,9,10},new String[]{"Lana", "Max", "Nina", "Omar", "Pete", "Quinn",
                                                                                                                    "Rina", "Sam", "Tina", "Uma", "Vera", "Walt", "Xena", "Yara", "Zane"}),
    MASTER(5,"BackgroundImages/campaignBackgroundMaster.jpg",new int[]{3,4,5,6,7},new String[]{"Leah", "Nico", "Owen", "Maya", "Kyle"});
    public int NLevels;
    public String bgUrl;

    public int[] pfpIndexes;
    public String[] levelNames;
    private CampaignTier(int nLevels,String bgUrl,int[] pfpIndexes,String[] levelNames){
        this.NLevels = nLevels;
        this.bgUrl = bgUrl;
        this.pfpIndexes = pfpIndexes;
        this.levelNames = levelNames;
        if(pfpIndexes.length != NLevels){
            System.out.println("Error pfp length does not match nlevels!, for enum:" + this.toString());
        }
        if(levelNames.length != NLevels){
            System.out.println("Error level name length does not match nlevels!, for enum:" + this.toString());
        }
    }

    public static CampaignTier getNextTier(CampaignTier currentTier){
        int nextOrdinal = currentTier.ordinal() + 1;
        if (nextOrdinal >= CampaignTier.values().length) {
            return LastTier;
        }
        return CampaignTier.values()[nextOrdinal];
    }

    public static CampaignTier LastTier = CampaignTier.values()[CampaignTier.values().length-1];
}
