package chessengine.Graphics;

import chessserver.User.UserPreferences;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public interface AppWindow {
    public default void addRootBinding(Scene scene){
        getRoot().prefWidthProperty().bind(scene.widthProperty());
        getRoot().prefHeightProperty().bind(scene.heightProperty());
    };
    public ReadOnlyDoubleProperty getRootWidth();
    public ReadOnlyDoubleProperty getRootHeight();
    public Region getRoot();


    public void setDefaultSelections(UserPreferences userPref);
    public Pane getMessageBoard();
}
