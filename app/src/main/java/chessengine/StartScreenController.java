package chessengine;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class StartScreenController implements Initializable {

    @FXML
    Button vsPlayer;

    @FXML
    Button vsComputer;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vsPlayer.setOnMouseClicked(e -> {
            App.controller.isVsComputer = false;
            App.changeScene(false);

        });
        vsComputer.setOnMouseClicked(e -> {
            App.controller.isVsComputer = true;
            App.changeScene(false);
        });
    }

}
