package chessengine.Settings;

import chessengine.Graphics.TextUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class Option {
    public String name;
    public String description;
    public Option(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected abstract Region createInnerNode();

    public Region getOptionNode(){
        HBox outerContainer = new HBox();
        outerContainer.setAlignment(Pos.CENTER);

        VBox infoContainer = new VBox();
        infoContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        infoContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        infoContainer.setAlignment(Pos.TOP_CENTER);
        infoContainer.prefWidthProperty().bind(infoContainer.widthProperty().multiply(0.4));
        infoContainer.prefHeightProperty().bind(infoContainer.heightProperty());

        Label title = new Label(name);
        Label desc = new Label(description);
        TextUtils.addTooltipOnElipsis(title, desc);
        infoContainer.getChildren().addAll(title, desc);

        Region innerNode = createInnerNode();
        HBox innerNodeWrapper = new HBox(innerNode);
        innerNodeWrapper.setAlignment(Pos.CENTER);
        innerNodeWrapper.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        innerNodeWrapper.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        innerNodeWrapper.prefWidthProperty().bind(outerContainer.widthProperty().divide(0.6));
        innerNodeWrapper.prefHeightProperty().bind(outerContainer.heightProperty());

        outerContainer.getChildren().addAll(infoContainer, innerNodeWrapper);

        return outerContainer;

    }

    @Override
    public String toString() {
        return String.format("%s: %s \nType:%s", name, description, getClass().getSimpleName());
    }
}
