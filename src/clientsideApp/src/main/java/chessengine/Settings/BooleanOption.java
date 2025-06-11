package chessengine.Settings;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class BooleanOption extends Option {
    private boolean defaultValue;
    public BooleanOption(String name, String description, boolean defaultValue) {
        super(name, description);
        this.defaultValue = defaultValue;
    }

    @Override
    protected Region createInnerNode() {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(defaultValue);

        return checkBox;
    }
}
