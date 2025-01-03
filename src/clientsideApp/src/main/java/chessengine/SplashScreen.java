package chessengine;

import chessengine.Graphics.AppStateChangeNotification;
import chessengine.Graphics.BindingController;
import chessserver.Enums.GlobalTheme;
import javafx.application.Preloader;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class SplashScreen extends Preloader {

    Label currentState;
    ProgressBar progressBar;
    Stage primaryState;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryState = primaryStage;
        Label title = new Label("Chess");
        currentState = new Label("Loading...");

        progressBar = new ProgressBar(0);
        progressBar.prefWidthProperty().bind(primaryStage.widthProperty().multiply(.8));

        VBox root = new VBox(title, currentState, progressBar);
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() / 3, Screen.getPrimary().getVisualBounds().getHeight() / 3);
        scene.getStylesheets().add(GlobalTheme.Dark.cssLocation);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Loading...");
        primaryStage.show();
        primaryStage.getIcons().add(new Image("/appIcon/icon.png"));


        StringExpression textSizeBinding = BindingController.getBinding1Style(Bindings.min(primaryStage.widthProperty(), primaryStage.heightProperty()), "-fx-font-size: ", (float) 1 / 20, "");
        currentState.styleProperty().bind(textSizeBinding);

        StringExpression titleSizeBinding = BindingController.getBinding1Style(Bindings.min(primaryStage.widthProperty(), primaryStage.heightProperty()), "-fx-font-size: ", (float) 1 / 5, "");
        title.styleProperty().bind(titleSizeBinding);


    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn){
        if (pn instanceof AppStateChangeNotification) {
            currentState.setText(((AppStateChangeNotification) pn).getMessage());
            primaryState.setTitle(((AppStateChangeNotification) pn).getMessage());
        }
        else if(pn instanceof  ProgressNotification){
            progressBar.setProgress(((ProgressNotification) pn).getProgress());
        }

    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification sn){
        if (sn.getType() == StateChangeNotification.Type.BEFORE_START) {
            // Hide the preloader when the application is ready
            primaryState.hide();
        }
    }
}
