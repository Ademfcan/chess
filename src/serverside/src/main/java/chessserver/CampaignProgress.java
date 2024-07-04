package chessserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CampaignProgress {
    CampaignTier currentTier;

    int currentLevelOfTier;

    public int getStarsForALevel(CampaignTier tier,int LevelOfTier) {
        return starsPerLevel.get(tier.ordinal())[LevelOfTier];
    }

    // 1-3 stars per level, like a score
    List<int[]> starsPerLevel;

    public CampaignProgress(){
        this.currentTier = CampaignTier.values()[0];
        // zero indexed!
        this.currentLevelOfTier = 6;
        starsPerLevel = new ArrayList<>(CampaignTier.values().length);
        for(int i = 0;i<CampaignTier.values().length;i++){
            int[] stars = new int[CampaignTier.values()[i].NLevels];
            starsPerLevel.add(stars);
        }
        setStarsPerLevelAll3(starsPerLevel.get(0),currentLevelOfTier);
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

    public void moveToNextLevel(int numStarsForPrev){
        if(currentTier != CampaignTier.LastTier && currentLevelOfTier != currentTier.NLevels){
            // max level already
            hasTierChanged = false;
            starsPerLevel.get(currentTier.ordinal())[currentLevelOfTier] = numStarsForPrev;
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
