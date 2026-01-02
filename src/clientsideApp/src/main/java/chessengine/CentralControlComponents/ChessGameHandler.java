package chessengine.CentralControlComponents;

import chessengine.ChessRepresentations.ClientsideChessGameWrapper;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.Enums.CampaignAttempt;
import chessserver.Enums.CampaignLevel;
import chessserver.Enums.Gametype;

public class ChessGameHandler {
    private boolean isCurrentGameFirstSetup;
    // for campaign mode only
    private CampaignAttempt campaignAttempt;
    //

    public ClientsideChessGameWrapper gameWrapper;
    public ChessGameHandler(ChessCentralControl control) {
        gameWrapper = new ClientsideChessGameWrapper(control);
    }

    public boolean shouldSaveGame(){
        return isActiveGame() && isCurrentGameFirstSetup() && !gameWrapper.getGame().isEmpty();
    }

    public boolean isActiveGame(){
        return gameWrapper.getGame() != null;
    }

    public CampaignAttempt getCampaignAttempt() {
        return campaignAttempt;
    }

    public void setCampaignAttempt(CampaignAttempt campaignAttempt) {
        this.campaignAttempt = campaignAttempt;
    }

    public boolean isCurrentGameFirstSetup() {
        return isCurrentGameFirstSetup;
    }

    public void clearGame() {
        campaignAttempt = null;
        gameWrapper.clearGame();
    }


    public void switchToNewGame(ChessGame newGame, boolean isVsComputer) {
        setUpGameGui(newGame, isVsComputer, false,null, true);
    }

    public void switchToGame(ChessGame newGame, boolean isVsComputer,  boolean isFirstLoad) {
        setUpGameGui(newGame, isVsComputer,false,null, isFirstLoad);

    }
    public void switchToOnlineGame(ChessGame newGame, Gametype gametype, boolean isFirstLoad) {
        setUpGameGui(newGame, false, true, gametype, isFirstLoad);

    }


    private void setUpGameGui(ChessGame newSetup, boolean isVsComputer, boolean isWebGame, Gametype gametype, boolean isFirstSave) {
        isCurrentGameFirstSetup = isFirstSave;
        gameWrapper.loadInNewGame(newSetup, isVsComputer, isWebGame, gametype);



    }
}
