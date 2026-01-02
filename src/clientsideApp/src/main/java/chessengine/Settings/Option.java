package chessengine.Settings;

import chessengine.Graphics.TextUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public abstract class Option {
    public String name;
    public String description;
    public Option(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected abstract Region createInnerNode();

    public Region getOptionNode(Pane parent){
        HBox outerContainer = new HBox();
        outerContainer.setAlignment(Pos.CENTER);

        outerContainer.prefHeightProperty().bind(parent.widthProperty().divide(2));

        VBox infoContainer = new VBox();
        infoContainer.setAlignment(Pos.CENTER);

        Label title = new Label(name);
        Label desc = new Label(description);
        TextUtils.addTooltipOnElipsis(title, desc);
        infoContainer.getChildren().addAll(title, desc);

        Region innerNode = createInnerNode();
        HBox innerNodeWrapper = new HBox(innerNode);
        innerNodeWrapper.setAlignment(Pos.CENTER);
        outerContainer.getChildren().addAll(infoContainer, innerNodeWrapper);

        outerContainer.setSpacing(10);

        return outerContainer;

    }

    @Override
    public String toString() {
        return String.format("%s: %s \nType:%s", name, description, getClass().getSimpleName());
    }
}
