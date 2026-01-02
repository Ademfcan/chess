package chessengine.Start;

import chessengine.Graphics.BindingController;
import chessengine.Graphics.StartScreenSubWindow;
import chessserver.ChessRepresentations.GameInfo;
import chessserver.User.UserWGames;
import javafx.geometry.Pos;

public class OldGamesScreen extends StartScreenSubWindow {
    public OldGamesScreen(StartScreenController controller) {
        super(controller);
    }

    @Override
    public void initLayout(){
        BindingController.bindCustom(controller.mainArea.widthProperty(), controller.oldGamesPanel.prefWidthProperty(), 350, .5);
        controller.oldGamesPanel.prefHeightProperty().bind(controller.mainArea.heightProperty());
        controller.oldGamesPanelContent.prefWidthProperty().bind(controller.oldGamesPanel.widthProperty());
        controller.oldGamesPanelContent.setAlignment(Pos.TOP_CENTER);
        controller.oldGamesPanelContent.setSpacing(3);
    }

    @Override
    public void updateWithUser(UserWGames userWGames){

        // clear and update
        controller.oldGamesPanelContent.getChildren().clear();
        for(GameInfo info : userWGames.games()){
            controller.AddNewGameToSaveGui(info, controller.oldGamesPanelContent);
        }
    }


}
