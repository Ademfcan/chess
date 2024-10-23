package chessengine.Graphics;

import chessengine.App;
import javafx.beans.binding.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;


public class BindingController {

    private final int smallMaxSize = 15;
    private final double smallParentToTextSize = (double) 1 / 50;
    private final int medMaxSize = 22;
    private final double medParentToTextSize = (double) 1 / 40;
    private final int lgMaxSize = 30;
    private final double lgParentToTextSize = (double) 1 / 30;
    private final int xlMaxSize = 35;

//    /** Provided a region and java fx styles this method will bind each style respectively to its scale factor based off the reference provided (Styles and Scale Factors must be same length!)**/
//    public void bindRegionWithCustomStyles(Region region,DoubleExpression reference,String[] styles,double[] scaleFactors,String extraStyle){
//        if (styles.length != scaleFactors.length) {
//            throw new IllegalArgumentException("Styles and scaleFactors arrays must have the same length");
//        }
//
//        StringBinding[] respectiveBindings = new StringBinding[styles.length];
//        for(int i = 0;i<styles.length;i++){
//            DoubleProperty b1 = new SimpleDoubleProperty();
//            b1.bind(b1.multiply(scaleFactors[i]));
//            respectiveBindings[i] = b1.asString();
//        }
//        StringExpression binding = Bindings.concat();
//        for (int i = 0; i < respectiveBindings.length; i++) {
//            if(i < respectiveBindings.length-1){
//                binding = Bindings.concat(binding, styles[i], respectiveBindings[i], ";");
//            }
//            else{
//                binding = Bindings.concat(binding, styles[i], respectiveBindings[i], ";",extraStyle);
//            }
//        }
//
//        region.styleProperty().bind(binding);
//
//    }
    private final double xlParentToTextSize = (double) 1 / 25;
    private final Region mainScreenFullScreen;
    private final Region startScreenFullScreen;

    public BindingController(Region mainScreenFullScreen, Region startScreenFullScreen) {
        this.mainScreenFullScreen = mainScreenFullScreen;
        this.startScreenFullScreen = startScreenFullScreen;
    }

    public static void bindRegionToStyle(Region region, NumberExpression reference, String style, double scaleFactor, String extraStyles) {
        region.styleProperty().unbind();
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor));
        StringExpression binding = Bindings.concat(style, b1.asString(), ";", extraStyles);
        region.styleProperty().bind(binding);

    }

    public static void bindRegionTo2Styles(Region region, NumberExpression reference, String style1, String style2, double scaleFactor1, double scaleFactor2, String extraStyles) {
        region.styleProperty().unbind();
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor1));
        DoubleProperty b2 = new SimpleDoubleProperty();
        b2.bind(reference.multiply(scaleFactor2));
        StringExpression binding = Bindings.concat(style1, b1.asString(), ";", style2, b2.asString(), ";", extraStyles);

        region.styleProperty().bind(binding);

    }

    public static StringExpression getBinding1Style(NumberExpression reference, String style, double scaleFactor, String extraStyles) {
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor));
        StringExpression binding = Bindings.concat(style, b1.asString(), ";", extraStyles);
        binding = binding.concat(extraStyles);
        return binding;

    }

    public static StringExpression getBinding2Styles(NumberExpression reference, String style1, String style2, double scaleFactor1, double scaleFactor2, String extraStyles) {
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor1));
        DoubleProperty b2 = new SimpleDoubleProperty();
        b2.bind(reference.multiply(scaleFactor2));
        StringExpression binding = Bindings.concat(style1, b1.asString(), ";", style2, b2.asString(), ";", extraStyles);
        binding = binding.concat(extraStyles);

        return binding;

    }

    public static void bindChildTextToParentWidth(Region parent, Region child, double parentToChildScaleFactor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentHeight(Region parent, Region child, double parentToChildScaleFactor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentWidth(Region parent, Region child, double parentToChildScaleFactor, String textColor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), "; -fx-text-fill: ", textColor));
    }

    public static void bindChildTextToParentHeight(Region parent, Region child, double parentToChildScaleFactor, String textColor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), "; -fx-text-fill: ", textColor));
    }

    public static void bindChildTextToParentMin(Region parent, Region child, double parentToChildScaleFactor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(), parent.widthProperty()).multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentMax(Region parent, Region child, double parentToChildScaleFactor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(), parent.widthProperty()).multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentValuesMin(Region parent, Region child, double parentToChildScaleFactor, String textColor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(), parent.widthProperty()).multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), "; -fx-text-fill: ", textColor));
    }

    public static void bindChildTextToParentValuesMax(Region parent, Region child, double parentToChildScaleFactor, String textColor) {
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(), parent.widthProperty()).multiply(parentToChildScaleFactor).multiply(App.dpiScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), "; -fx-text-fill: ", textColor));
    }

    public void bindChildWidthToParentWidthWithMaxSize(Region parent, Region child, int maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize);
        child.prefWidthProperty().bind(sizeBinding);

    }

    public void bindChildHeightToParentWidthWithMaxSize(Region parent, Region child, int maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize);
        child.prefHeightProperty().bind(sizeBinding);

    }

    public void bindChildWidthToParentHeightWithMaxSize(Region parent, Region child, int maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent, maxSize, percentExpectedSize);
        child.prefWidthProperty().bind(sizeBinding);

    }

    public void bindChildHeightToParentHeightWithMaxSize(Region parent, Region child, int maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent, maxSize, percentExpectedSize);
        child.prefHeightProperty().bind(sizeBinding);

    }

    public void bindSmallTextCustom(Node text, boolean isMainScreen, String extraCss) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSizeCustomCss(parent, text, smallMaxSize, smallParentToTextSize, extraCss);
    }

    public void bindSmallText(Node text, boolean isMainScreen, String textColor) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSizeCustomCss(parent, text, smallMaxSize, smallParentToTextSize, " -fx-text-fill: "+ textColor);
    }

    public void bindMediumText(Node text, boolean isMainScreen, String textColor) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSizeCustomCss(parent, text, medMaxSize, medParentToTextSize, " -fx-text-fill: "+ textColor);


    }

    public void bindLargeText(Node text, boolean isMainScreen, String textColor) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSizeCustomCss(parent, text, lgMaxSize, lgParentToTextSize, " -fx-text-fill: "+ textColor);


    }

    public void bindXLargeText(Node text, boolean isMainScreen, String textColor) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentMinwMaxSizeCustomCss(parent, text, xlMaxSize, xlParentToTextSize, " -fx-text-fill: "+ textColor);

    }

    // since when one scene is shown, the size of the hidden scene is not guaranteed, we bind to parent elements respective of that scene
    public void bindSmallText(Node text, boolean isMainScreen) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentMinwMaxSize(parent, text, smallMaxSize, smallParentToTextSize);
    }

    public void bindMediumText(Node text, boolean isMainScreen) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentMinwMaxSize(parent, text, medMaxSize, medParentToTextSize);


    }

    public void bindLargeText(Node text, boolean isMainScreen) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentMinwMaxSize(parent, text, lgMaxSize, lgParentToTextSize);


    }

    public void bindXLargeText(Node text, boolean isMainScreen) {
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentMinwMaxSize(parent, text, xlMaxSize, xlParentToTextSize);


    }

    public void bindTextToParentWidthWithMaxSize(Region parent, Node text, double maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));

    }

    public void bindTextToParentHeightWithMaxSize(Region parent, Node text, double maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));

    }

    public void bindTextToParentMinwMaxSizeCustomCss(Region parent, Node text, double maxSize, double percentExpectedSize, String extraCss) {
        NumberBinding sizeBinding = getMaxSizeMin(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(), "; ", extraCss));

    }

    public void bindTextToParentMinwMaxSize(Region parent, Node text, double maxSize, double percentExpectedSize) {
        NumberBinding sizeBinding = getMaxSizeMin(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));

    }

    public void bindTextToParentWidthWithMaxSizeCustomCss(Region parent, Node text, double maxSize, double percentExpectedSize, String extraCss) {
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(), "; ", extraCss));

    }

    public void bindTextToParentWidthWithMaxSize(Region parent, Node text, double maxSize, double percentExpectedSize, String color) {
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(), "; -fx-text-fill: ", color));

    }

    public void bindTextToParentHeightWithMaxSize(Region parent, Node text, double maxSize, double percentExpectedSize, String color) {
        NumberBinding sizeBinding = getMaxSizeBindingHeight(parent, maxSize, percentExpectedSize).multiply(App.dpiScaleFactor);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(), "; -fx-text-fill: ", color));

    }

    private NumberBinding getMaxSizeMin(Region parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize), getMaxSizeBindingHeight(parent, maxSize, percentExpectedSize));
    }

    private NumberBinding getMaxSizeBindingWidth(Region parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(parent.widthProperty().multiply(percentExpectedSize), maxSize);
    }

    private NumberBinding getMaxSizeBindingHeight(Region parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(parent.heightProperty().multiply(percentExpectedSize), maxSize);

    }

    private NumberBinding getMaxSizeBindingCustom(ReadOnlyDoubleProperty parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(parent.multiply(percentExpectedSize), maxSize);
    }

    public void bindCustom(ReadOnlyDoubleProperty parent, DoubleProperty child, double maxSize, double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingCustom(parent,maxSize,percentExpectedSize);
        child.bind(sizeBinding);
    }



}
