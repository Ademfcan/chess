package chessengine;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import org.w3c.dom.Node;


public class BindingController {
    public static void bindChildWidthToParentWidthWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        int threshold =  (int)(maxSize/percentExpectedSize);
        int minScaleFactor = maxSize/threshold;
        child.prefWidthProperty().bind(Bindings.when(parent.widthProperty().greaterThan(threshold)).then(maxSize).otherwise(parent.heightProperty().multiply(minScaleFactor)));

    }
    public static void bindChildHeightToParentWidthWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        int threshold =  (int)(maxSize/percentExpectedSize);
        int minScaleFactor = maxSize/threshold;
        child.prefHeightProperty().bind(Bindings.when(parent.widthProperty().greaterThan(threshold)).then(maxSize).otherwise(parent.heightProperty().multiply(minScaleFactor)));

    }

    public static void bindChildWidthToParentHeightWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        int threshold =  (int)(maxSize/percentExpectedSize);
        int minScaleFactor = maxSize/threshold;
        child.prefWidthProperty().bind(Bindings.when(parent.heightProperty().greaterThan(threshold)).then(maxSize).otherwise(parent.heightProperty().multiply(minScaleFactor)));

    }
    public static void bindChildHeightToParentHeightWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        int threshold =  (int)(maxSize/percentExpectedSize);
        int minScaleFactor = maxSize/threshold;
        child.prefHeightProperty().bind(Bindings.when(parent.heightProperty().greaterThan(threshold)).then(maxSize).otherwise(parent.heightProperty().multiply(minScaleFactor)));

    }

    public static void bindChildTextToParentWidth(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString()));
    }

    public static void bindChildTextToParentHeight(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString()));
    }

    public static void bindChildTextToParentWidth(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString(),"; -fx-text-fill: ",textColor));
    }

    public static void bindChildTextToParentHeight(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString(),"; -fx-text-fill: ",textColor));
    }

    public static void bindChildTextToParentMin(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString()));
    }

    public static void bindChildTextToParentMax(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString()));
    }

    public static void bindChildTextToParentValuesMin(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString(),"; -fx-text-fill: ",textColor));
    }

    public static void bindChildTextToParentValuesMax(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.toString(),"; -fx-text-fill: ",textColor));
    }








    // since when one scene is shown, the size of the hidden scene is not guaranteed, we bind to parent elements respective of that scene
    public static void bindSmallText(Region text,boolean isMainScreen){
        bindChildTextToParentMin(isMainScreen ? App.mainScreenController.fullScreen : App.startScreenController.fullscreen,text,.05);
    }
    public static void bindMediumText(Region text,boolean isMainScreen){
        bindChildTextToParentMin(isMainScreen ? App.mainScreenController.fullScreen : App.startScreenController.fullscreen,text,.10);

    }
    public static void bindLargeText(Region text,boolean isMainScreen){
        bindChildTextToParentMin(isMainScreen ? App.mainScreenController.fullScreen : App.startScreenController.fullscreen,text,.15);

    }
    public static void bindXLargeText(Region text,boolean isMainScreen){
        bindChildTextToParentMin(isMainScreen ? App.mainScreenController.fullScreen : App.startScreenController.fullscreen,text,.20);
    }
}
