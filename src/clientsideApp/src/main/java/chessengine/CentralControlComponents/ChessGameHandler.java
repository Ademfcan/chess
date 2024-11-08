package chessengine.CentralControlComponents;

import chessengine.ChessRepresentations.ClientsideChessGameWrapper;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Enums.CampaignTier;

import java.util.Objects;

public class ChessGameHandler {
    private final ChessCentralControl control;
    public ClientsideChessGameWrapper gameWrapper;

    public boolean currentlyGameActive(){
        return gameWrapper.getGame() != null;
    }
    // for campaign mode only
    private int gameDifficulty;
    private int levelOfCampaignTier;
    private CampaignTier campaignTier;
    private boolean isCurrentGameFirstSetup;

    public ChessGameHandler(ChessCentralControl control) {
        this.control = control;
        gameWrapper = new ClientsideChessGameWrapper(control);

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
        campaignTier = null;
        levelOfCampaignTier = -1;
        gameDifficulty = -1;
        gameWrapper.clearGame();
    }


    public void switchToNewGame(ChessGame newGame,boolean isWebGame) {
        setUpGameGui(newGame,isWebGame, true);

    }

    public void switchToGame(ChessGame newGame,boolean isWebGame, boolean isFirstLoad) {
        setUpGameGui(newGame,isWebGame, isFirstLoad);

    }


    private void setUpGameGui(ChessGame newSetup,boolean isWebGame, boolean isFirstSave) {
        isCurrentGameFirstSetup = isFirstSave;
        gameWrapper.loadInNewGame(newSetup,isWebGame);



    }
}
