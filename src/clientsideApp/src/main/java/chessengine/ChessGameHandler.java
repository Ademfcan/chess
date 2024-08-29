package chessengine;

import chessserver.CampaignTier;

import java.util.Objects;

public class ChessGameHandler {
    private final ChessCentralControl control;
    public ChessGame currentGame;
    // for campaign mode only
    private int gameDifficulty;
    private int levelOfCampaignTier;
    private CampaignTier campaignTier;
    private boolean isCurrentGameFirstSetup;

    public ChessGameHandler(ChessCentralControl control) {
        this.control = control;

    }

    public int getLevelOfCampaignTier() {
        return levelOfCampaignTier;
    }

    public void setLevelOfCampaignTier(int levelOfCampaignTier) {
        this.levelOfCampaignTier = levelOfCampaignTier;
    }

    public CampaignTier getCampaignTier() {
        return campaignTier;
    }

    public void setCampaignTier(CampaignTier campaignTier) {
        this.campaignTier = campaignTier;
    }

    public int getGameDifficulty() {
        return this.gameDifficulty;
    }

    public void setGameDifficulty(int gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public boolean isCurrentGameFirstSetup() {
        return isCurrentGameFirstSetup;
    }

    public void clearGame() {
        currentGame = null;
        campaignTier = null;
        levelOfCampaignTier = -1;
        gameDifficulty = -1;
    }


    public void switchToNewGame(ChessGame newGame) {
        setUpGameGui(newGame, true);

    }

    public void switchToGame(ChessGame newGame, boolean isFirstLoad) {
        setUpGameGui(newGame, isFirstLoad);

    }

    private void setUpGameGui(ChessGame newSetup, boolean isFirstSave) {
        isCurrentGameFirstSetup = isFirstSave;
        if (Objects.isNull(currentGame)) {
            newSetup.setMainGame(control);

        } else {
            currentGame.clearMainGame();
            newSetup.setMainGame(control);

        }
        currentGame = newSetup;


    }
}
