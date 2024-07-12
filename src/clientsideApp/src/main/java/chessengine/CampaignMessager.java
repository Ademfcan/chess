package chessengine;

import chessserver.CampaignProgress;
import chessserver.CampaignTier;

public class CampaignMessager {


    private final ChessCentralControl centralControl;

    public CampaignMessager(ChessCentralControl centralControl){
        this.centralControl = centralControl;
    }


    private String getName(){
        if(centralControl.gameHandler.currentGame != null && centralControl.mainScreenController.currentState.equals(MainScreenState.CAMPAIGN)){
            CampaignTier currentTier = centralControl.gameHandler.getCampaignTier();
            int currentTierLevel = centralControl.gameHandler.getLevelOfCampaignTier();
            return currentTier.levelNames[currentTierLevel];
        }
        else{
            ChessConstants.mainLogger.error("Not in a campaign game, cannot create message");
            return "";
        }

    }

    private String formatNameMessage(String name,String message){
        return "(" + name + "): " + message;
    }

    // todo make these actually good
    public String getCheckMessage(boolean isWhiteCheck){
        String name = getName();
        if(isWhiteCheck){
            return formatNameMessage(name,"ooh scary!");
        }
        else{
            return formatNameMessage(name,"hahahaha!");
        }

    }

    public String getCheckmateMessage(boolean isWhiteCheckMate){
        String name = getName();
        if(isWhiteCheckMate){
            return formatNameMessage(name,"good game!");
        }
        else{
            return formatNameMessage(name,"next time you will win!");
        }

    }

    public String getStalemateMessage(){
        String name = getName();
        return formatNameMessage(name,"shall we call it a tie for now?");

    }

    public String getEatingMessage(int eatenPieceIndex,boolean isWhiteMove){
        String name = getName();
        if(isWhiteMove){
            return formatNameMessage(name,"Oh no! My" + GeneralChessFunctions.getPieceType(eatenPieceIndex) + " !");
        }
        else{
            return formatNameMessage(name,"haha i took your " + GeneralChessFunctions.getPieceType(eatenPieceIndex) + ";)");
        }
    }


    public String getPromoMessage(int promoIndex,boolean isWhiteMove){
        String name = getName();
        if(isWhiteMove){
            return formatNameMessage(name,"oh no! you have a new " + GeneralChessFunctions.getPieceType(promoIndex));
        }
        else{
            return formatNameMessage(name,"i will defeat you with my new " + GeneralChessFunctions.getPieceType(promoIndex));
        }
    }



    public String getCastleMessage(boolean isWhiteMove){
        String name = getName();
        if(isWhiteMove){
            return formatNameMessage(name,"Hiding your king i see!");
        }
        else{
            return formatNameMessage(name,"Bringing my king to safety!");
        }
    }

    public String getMoveMessage(int simpleEval,boolean isWhiteMove){
        String name = getName();
        if(isWhiteMove){
            return formatNameMessage(name,"lets see how you do!");
        }
        else{
            return formatNameMessage(name,"im coming for you! :]");
        }
    }
}
