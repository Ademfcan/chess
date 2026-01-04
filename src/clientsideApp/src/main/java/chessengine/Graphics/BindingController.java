package chessengine.Graphics;

import chessengine.App;
import chessengine.FXInitQueue;
import javafx.beans.binding.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import javax.annotation.Nullable;


public class BindingController {

    private static final int extraSmallMaxSize = 10;
    private static final double extraSmallParentToTextSize = (double) 1 / 63;
    private static final int smallMaxSize = 15;
    private static final double smallParentToTextSize = (double) 1 / 50;
    private static final int medMaxSize = 22;
    private static final double medParentToTextSize = (double) 1 / 40;
    private static final int lgMaxSize = 30;
    private static final double lgParentToTextSize = (double) 1 / 30;
    private static final int xlMaxSize = 35;

    private static final double xlParentToTextSize = (double) 1 / 25;
    private static final DoubleProperty stageHeight = new SimpleDoubleProperty();
    private static final DoubleProperty stageWidth = new SimpleDoubleProperty();

    static {
        FXInitQueue.runAfterInit(() -> {
            stageHeight.bind(App.mainScene.heightProperty());
            stageWidth.bind(App.mainScene.widthProperty());
        });
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



    private static NumberBinding getMaxSizeMin(Region parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(getMaxSizeBindingWidth(parent, maxSize, percentExpectedSize), getMaxSizeBindingHeight(parent, maxSize, percentExpectedSize));
    }

    private static NumberBinding getMaxSizeBindingWidth(Region parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(parent.widthProperty().multiply(percentExpectedSize), maxSize);
    }

    private static NumberBinding getMaxSizeBindingHeight(Region parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(parent.heightProperty().multiply(percentExpectedSize), maxSize);

    }


    public static void bindCustom(NumberExpression parent, DoubleProperty child, double maxSize, double percentExpectedSize){
        NumberBinding sizeBinding = getMaxSizeBindingCustom(parent,maxSize*App.dpiScaleFactor,percentExpectedSize);
        child.bind(sizeBinding);
    }

    private static NumberBinding getMaxSizeBindingCustom(NumberExpression parent, double maxSize, double percentExpectedSize) {
        return Bindings.min(parent.multiply(percentExpectedSize), maxSize);
    }





    // text bindings relative to stage

    public static NumberBinding stageMin() {
        return Bindings.min(stageHeight, stageWidth);
    }


    public static void bindCustomTextCss(NumberBinding parent, Node child, double maxSize, double percentExpectedSize, @Nullable String extraCss){
        NumberBinding sizeBinding = getMaxSizeBindingCustom(parent,maxSize*App.dpiScaleFactor, percentExpectedSize);

        if(extraCss != null){
            child.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(), "; ", extraCss));
        }
        else{
            child.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));
        }
    }


    public static void bindXSmallText(Node text, String textColor) {
        bindCustomTextCss(stageMin(), text, extraSmallMaxSize, extraSmallParentToTextSize, " -fx-text-fill: "+ textColor);
    }

    public static void bindSmallTextCustom(Node text, String extraCss) {
        bindCustomTextCss(stageMin(), text, extraSmallMaxSize, extraSmallParentToTextSize, extraCss);
    }

    public static void bindSmallText(Node text, String textColor) {
        bindCustomTextCss(stageMin(), text, smallMaxSize, smallParentToTextSize, " -fx-text-fill: "+ textColor);
    }

    public static void bindMediumTextCustom(Node text, String extraCss) {
        bindCustomTextCss(stageMin(), text, medMaxSize, medParentToTextSize, extraCss);
    }

    public static void bindMediumText(Node text, String textColor) {
        bindCustomTextCss(stageMin(), text, medMaxSize, medParentToTextSize, " -fx-text-fill: "+ textColor);
    }

    public static void bindLargeTextCustom(Node text, String extraCss) {
        bindCustomTextCss(stageMin(), text, lgMaxSize, lgParentToTextSize, extraCss);
    }

    public static void bindLargeText(Node text, String textColor) {
        bindCustomTextCss(stageMin(), text, lgMaxSize, lgParentToTextSize, " -fx-text-fill: "+ textColor);
    }

    public static void bindXLargeText(Node text, String textColor) {
        bindCustomTextCss(stageMin(), text, xlMaxSize, xlParentToTextSize, " -fx-text-fill: "+ textColor);
    }
    // since when one scene is shown, the size of the hidden scene is not guaranteed, we bind to parent elements respective of that scene
    public static void bindXSmallText(Node text) {
        bindCustomTextCss(stageMin(), text, extraSmallMaxSize, extraSmallParentToTextSize, null);
    }

    public static void bindSmallText(Node text) {
        bindCustomTextCss(stageMin(), text, smallMaxSize, smallParentToTextSize, null);
    }

    public static void bindMediumText(Node text) {
        bindCustomTextCss(stageMin(), text, medMaxSize, medParentToTextSize, null);
    }

    public static void bindLargeText(Node text) {
        bindCustomTextCss(stageMin(), text, lgMaxSize, lgParentToTextSize, null);
    }

    public static void bindXLargeText(Node text) {
        bindCustomTextCss(stageMin(), text, xlMaxSize, xlParentToTextSize, null);
    }



}
