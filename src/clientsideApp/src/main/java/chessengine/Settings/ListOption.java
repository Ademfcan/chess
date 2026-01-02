package chessengine.Settings;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.util.Arrays;
import java.util.List;

public class ListOption<T> extends Option{
    private final T defaultValue;
    private final List<? extends T> options;
    public ListOption(String name, String description, T defaultValue, List<? extends T> options) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.options = options;


        if(options.stream().noneMatch(t -> t.equals(defaultValue))){
            throw new IllegalArgumentException("Option " + name + "'s default value is not present in the list");
        }
    }


    @Override
    protected Region createInnerNode() {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(options);
        comboBox.getSelectionModel().select(defaultValue);

        return comboBox;
    }

}
