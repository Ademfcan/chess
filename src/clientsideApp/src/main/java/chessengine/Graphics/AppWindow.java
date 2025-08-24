package chessengine.Graphics;

import chessengine.App;
import chessengine.FXInitQueue;
import chessengine.Settings.SettingsManager;
import chessengine.TriggerRegistry;
import chessengine.Triggers.Loginable;
import chessengine.Triggers.Onlineable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AppWindow implements Initializable, Loginable, Onlineable, UserConfigurable, Resettable {
    private final List<AppSubWindow> subWindows = new ArrayList<>();

    public AppWindow(){
        TriggerRegistry.addTriggerable(this);
        FXInitQueue.runAfterInit(() -> {
            SettingsManager.addSettingsWrapper(getSettingsWrapper());
            addRootBinding(App.mainScene);

            afterInitialize();

            subWindows.forEach(AppSubWindow::afterInit);
        });
    }

    public void addSubWindow(AppSubWindow window){
        subWindows.add(window);
    }

    void addRootBinding(Scene scene){
        getRoot().prefWidthProperty().bind(scene.widthProperty());
        getRoot().prefHeightProperty().bind(scene.heightProperty());
    };

    public abstract VBox getSettingsWrapper();
    public abstract ReadOnlyDoubleProperty getRootWidth();
    public abstract ReadOnlyDoubleProperty getRootHeight();
    public abstract Region getRoot();
    public abstract Pane getMessageBoard();
    public abstract Group getMessageGroup();

    protected abstract void initLayout();
    protected abstract void initGraphics();

    protected abstract void afterInitialize();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLayout();
        for(AppSubWindow window : subWindows){
            window.initLayout();
        }

        initGraphics();
        for(AppSubWindow window : subWindows){
            window.initGraphics();
        }

    }
}
