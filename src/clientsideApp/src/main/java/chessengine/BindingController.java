package chessengine;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import org.w3c.dom.Node;


public class BindingController {

    private Region mainScreenFullScreen;
    private Region startScreenFullScreen;

    public BindingController(Region mainScreenFullScreen,Region startScreenFullScreen){
        this.mainScreenFullScreen = mainScreenFullScreen;
        this.startScreenFullScreen = startScreenFullScreen;
    }

    public void bindChildWidthToParentWidthWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent,maxSize,percentExpectedSize);
        child.prefWidthProperty().bind(sizeBinding);

    }
    public void bindChildHeightToParentWidthWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent,maxSize,percentExpectedSize);
        child.prefHeightProperty().bind(sizeBinding);

    }

    public void bindChildWidthToParentHeightWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent,maxSize,percentExpectedSize);
        child.prefWidthProperty().bind(sizeBinding);

    }
    public void bindChildHeightToParentHeightWithMaxSize(Region parent, Region child, int maxSize,double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent,maxSize,percentExpectedSize);
        child.prefHeightProperty().bind(sizeBinding);

    }

    public void bindChildTextToParentWidth(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public void bindChildTextToParentHeight(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public void bindChildTextToParentWidth(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }

    public void bindChildTextToParentHeight(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }

    public void bindChildTextToParentMin(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public void bindChildTextToParentMax(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public void bindChildTextToParentValuesMin(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }

    public void bindChildTextToParentValuesMax(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }



    private final int smallMaxSize = 15;
    private final double smallParentToTextSize = (double) 1 /50;

    private final int medMaxSize = 22;
    private final double medParentToTextSize = (double) 1 /40;

    private final int lgMaxSize = 30;

    private final double lgParentToTextSize = (double) 1 /30;

    private final int xlMaxSize = 35;

    private final double xlParentToTextSize = (double) 1 /25;


    public void bindSmallText(Region text,boolean isMainScreen,String textColor){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,smallMaxSize,smallParentToTextSize,textColor);
    }
    public void bindMediumText(Region text,boolean isMainScreen,String textColor){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,medMaxSize,medParentToTextSize,textColor);


    }
    public void bindLargeText(Region text,boolean isMainScreen,String textColor){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,lgMaxSize,lgParentToTextSize,textColor);


    }
    public void bindXLargeText(Region text,boolean isMainScreen,String textColor){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,xlMaxSize,xlParentToTextSize,textColor);

    }

    // since when one scene is shown, the size of the hidden scene is not guaranteed, we bind to parent elements respective of that scene
    public void bindSmallText(Region text,boolean isMainScreen){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,smallMaxSize,smallParentToTextSize);
    }
    public void bindMediumText(Region text,boolean isMainScreen){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,medMaxSize,medParentToTextSize);


    }
    public void bindLargeText(Region text,boolean isMainScreen){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,lgMaxSize,lgParentToTextSize);


    }
    public void bindXLargeText(Region text,boolean isMainScreen){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSize(parent,text,xlMaxSize,xlParentToTextSize);


    }

    public void bindTextToParentWidthWithMaxSize(Region parent,Region text,double maxSize,double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent,maxSize,percentExpectedSize);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));

    }
    public void bindTextToParentHeightWithMaxSize(Region parent,Region text,double maxSize,double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent,maxSize,percentExpectedSize);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));

    }

    public void bindTextToParentWidthWithMaxSize(Region parent,Region text,double maxSize,double percentExpectedSize,String color){
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent,maxSize,percentExpectedSize);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(),"; -fx-text-fill: ",color));

    }
    public void bindTextToParentHeightWithMaxSize(Region parent,Region text,double maxSize,double percentExpectedSize,String color){
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent,maxSize,percentExpectedSize);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(),"; -fx-text-fill: ",color));

    }

    private NumberBinding getMaxSizeBindingWidth(Region parent,double maxSize,double percentExpectedSize){
        return Bindings.min(parent.widthProperty().multiply(percentExpectedSize),maxSize);
    }

    private NumberBinding getMaxSizeBindingHeight(Region parent, double maxSize, double percentExpectedSize){
        return Bindings.min(parent.heightProperty().multiply(percentExpectedSize),maxSize);

    }
}
