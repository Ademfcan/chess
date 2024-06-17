package chessengine;

import java.util.HashMap;
import java.util.Objects;

public class ChessGameHandler {
    public ChessGame currentGame;

    private ChessCentralControl control;

    public boolean isCurrentGameFirstSetup() {
        return isCurrentGameFirstSetup;
    }

    private boolean isCurrentGameFirstSetup;
    public ChessGameHandler(ChessCentralControl control){
        this.control = control;


    }

    public void switchToNewGame(String gamePgn,String name,boolean isVsComputer){
        ChessGame newGame = new ChessGame(gamePgn,name,isVsComputer);
        setUpGameGui(newGame,true);
    }

    public void switchToNewGame(ChessGame newGame){
        setUpGameGui(newGame,true);

    }

    public void switchToGame(ChessGame newGame){
        setUpGameGui(newGame,false);

    }

    private void setUpGameGui(ChessGame newSetup,boolean isFirstSave){
        isCurrentGameFirstSetup = isFirstSave;
        if(Objects.isNull(currentGame)){
            newSetup.setMainGame(control);

        }
        else{
            currentGame.clearMainGame();
            newSetup.setMainGame(control);

        }
        if(isFirstSave){
            if(!App.mainScreenController.currentState.equals(MainScreenState.SANDBOX)){
                App.startScreenController.AddNewGameToSaveGui(newSetup);
            }
        }
        currentGame = newSetup;





    }
}
