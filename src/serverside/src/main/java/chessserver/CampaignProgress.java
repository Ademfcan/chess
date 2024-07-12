package chessserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CampaignProgress {
    CampaignTier currentTier;

    public List<int[]> getStarsPerLevel() {
        return starsPerLevel;
    }

    public void setStarsPerLevel(List<int[]> starsPerLevel) {
        this.starsPerLevel = starsPerLevel;
    }

    int currentLevelOfTier;

    public int getStarsForALevel(CampaignTier tier,int LevelOfTier) {
        return starsPerLevel.get(tier.ordinal())[LevelOfTier];
    }

    public void setStarsForALevel(CampaignTier tier,int LevelOfTier,int numStars) {
        int curStars = getStarsForALevel(tier,LevelOfTier);
        if(numStars > curStars){
            // dont penalize if you have done better before
            starsPerLevel.get(tier.ordinal())[LevelOfTier] = numStars;
        }
        else{
            System.out.println("No change needed");
        }
    }

    // 1-3 stars per level, like a score
    List<int[]> starsPerLevel;

    public CampaignProgress(CampaignTier currentTier,int currentLevelOfTier,List<int[]> starsPerLevel){
        this.currentTier = currentTier;
        // zero indexed!
        this.currentLevelOfTier = currentLevelOfTier;
        this.starsPerLevel = starsPerLevel;
        for(int i = 0;i<CampaignTier.values().length;i++){
            int[] stars = new int[CampaignTier.values()[i].NLevels];
            starsPerLevel.add(stars);
        }
    }

    public CampaignProgress(){
        // json serialization
        this.starsPerLevel = new ArrayList<>();
        for (int i = 0; i < CampaignTier.values().length; i++) {
            int[] stars = new int[CampaignTier.values()[i].NLevels];
            starsPerLevel.add(stars);
        }
    }

    public CampaignProgress(int flag){
        // empty constructor without json serialization
        this.currentTier = CampaignTier.values()[0];
        // zero indexed!
        this.currentLevelOfTier = 0;
        this.starsPerLevel = new ArrayList<>();
        for(int i = 0;i<CampaignTier.values().length;i++){
            int[] stars = new int[CampaignTier.values()[i].NLevels];
            starsPerLevel.add(stars);
        }
    }



    boolean hasTierChanged = false;

    public CampaignTier getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(CampaignTier currentTier) {
        this.currentTier = currentTier;
        setAllPreviousTo3Stars();
    }

    public int getCurrentLevelOfTier() {
        return currentLevelOfTier;
    }

    public void setCurrentLevelOfTier(int currentLevelOfTier) {
        this.currentLevelOfTier = currentLevelOfTier;
        setStarsPerLevelAll3(starsPerLevel.get(currentTier.ordinal()),currentLevelOfTier);
    }

    public void moveToNextLevel(){
        if(currentTier != CampaignTier.LastTier && currentLevelOfTier != currentTier.NLevels){
            // max level already
            hasTierChanged = false;
            currentLevelOfTier++;
            if(currentLevelOfTier > currentTier.NLevels){
                // move to next tier
                currentTier =  CampaignTier.getNextTier(currentTier);
                currentLevelOfTier = 1;
                hasTierChanged = true;
            }
        }
    }

    public boolean HasTierChanged() {
        return hasTierChanged;
    }

    private void setAllPreviousTo3Stars(){
        int curOrdinal = currentTier.ordinal();
        for(int i = curOrdinal;i>= 0;i--){
            setStarsPerLevelAll3(starsPerLevel.get(i));
        }
    }
    private void setStarsPerLevelAll3(int[] starsPerLevel){
        Arrays.fill(starsPerLevel, 3);
    }

    private void setStarsPerLevelAll3(int[] starsPerLevel,int currentLevelTier){
        // sets all previous to 3 stars
        for(int i = currentLevelTier-1;i>=0;i--){
            starsPerLevel[i] = 3;
        }
    }
}
