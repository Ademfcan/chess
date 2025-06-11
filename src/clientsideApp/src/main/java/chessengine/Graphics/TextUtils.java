package chessengine.Graphics;

import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TextUtils{
    public static void addTooltipOnElipsis(Labeled... textElements){
        for(Labeled textElement : textElements){
            addTooltipOnElipsis(textElement);
        }
    }

    public static void addTooltipOnElipsis(Labeled textElement){
        textElement.needsLayoutProperty().addListener((observable, oldP, newP) -> {
            String originalString = textElement.getText();
            Text textNode = (Text) textElement.lookup(".text"); // "text" is the style class of Text
            String actualString = textNode.getText();

            boolean clipped = !originalString.equals(actualString);

            if(clipped){
                Tooltip tp = new Tooltip(originalString);
                tp.setShowDelay(Duration.millis(100));
                tp.setHideDelay(Duration.seconds(0.2));
                tp.setShowDuration(Duration.INDEFINITE); // stays while hovered

                textElement.setTooltip(tp);
            }
        });
    }

}
