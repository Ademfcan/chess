package chessengine.Graphics;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public interface AppWindow {
    public ReadOnlyDoubleProperty getWindowWidth();
    public ReadOnlyDoubleProperty getWindowHeight();
    public Region getWindow();

    public Pane getMessageBoard();
}
