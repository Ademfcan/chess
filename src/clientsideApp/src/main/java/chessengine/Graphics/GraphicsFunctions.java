package chessengine.Graphics;

import javafx.scene.Node;

public class GraphicsFunctions {
    public static void toggleHideAndDisable(Node node, boolean isHiddenAndDisabled){
        node.setVisible(!isHiddenAndDisabled);
        node.setMouseTransparent(isHiddenAndDisabled);
        node.setDisable(isHiddenAndDisabled);
    }
}
