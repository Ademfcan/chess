package chessengine.Settings;

import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class RangeOption extends Option {
    private double min;
    private double max;
    private double defaultValue;
    public RangeOption(String name, String description, double defaultValue, double min, double max) {
        super(name, description);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;

        if (defaultValue < min || defaultValue > max){
            throw new IllegalArgumentException("Default Value must be between " + min + " and " + max);
        }
    }

    @Override
    protected Region createInnerNode() {
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);

        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        return slider;
    }
}
