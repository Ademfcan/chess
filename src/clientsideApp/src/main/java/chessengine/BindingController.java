package chessengine;

import javafx.beans.binding.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;



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

    public static void bindRegionToStyle(Region region,DoubleExpression reference,String style,double scaleFactor,String extraStyles) {
        region.styleProperty().unbind();
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor));
        StringExpression binding = Bindings.concat(style,b1.asString(),";",extraStyles);
        region.styleProperty().bind(binding);

    }

    public static void bindRegionTo2Styles(Region region,DoubleExpression reference,String style1,String style2,double scaleFactor1,double scaleFactor2,String extraStyles) {
        region.styleProperty().unbind();
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor1));
        DoubleProperty b2 = new SimpleDoubleProperty();
        b2.bind(reference.multiply(scaleFactor2));
        StringExpression binding = Bindings.concat(style1,b1.asString(),";",style2,b2.asString(),";",extraStyles);

        region.styleProperty().bind(binding);

    }

    public static StringExpression getBinding1Style(DoubleExpression reference,String style,double scaleFactor,String extraStyles) {
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor));
        StringExpression binding = Bindings.concat(style,b1.asString(),";",extraStyles);
        binding = binding.concat(extraStyles);
        return binding;

    }

    public static StringExpression getBinding2Styles(DoubleExpression reference, String style1, String style2, double scaleFactor1, double scaleFactor2, String extraStyles) {
        DoubleProperty b1 = new SimpleDoubleProperty();
        b1.bind(reference.multiply(scaleFactor1));
        DoubleProperty b2 = new SimpleDoubleProperty();
        b2.bind(reference.multiply(scaleFactor2));
        StringExpression binding = Bindings.concat(style1,b1.asString(),";",style2,b2.asString(),";",extraStyles);
        binding = binding.concat(extraStyles);

        return binding;

    }


    public static void bindChildTextToParentWidth(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentHeight(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentWidth(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.widthProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }

    public static void bindChildTextToParentHeight(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(parent.heightProperty().multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }

    public static void bindChildTextToParentMin(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentMax(Region parent,Region child, double parentToChildScaleFactor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.max(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
    }

    public static void bindChildTextToParentValuesMin(Region parent,Region child, double parentToChildScaleFactor,String textColor){
        DoubleProperty fontSize = new SimpleDoubleProperty();
        fontSize.bind(Bindings.min(parent.heightProperty(),parent.widthProperty()).multiply(parentToChildScaleFactor));
        child.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(),"; -fx-text-fill: ",textColor));
    }

    public static void bindChildTextToParentValuesMax(Region parent,Region child, double parentToChildScaleFactor,String textColor){
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


    public void bindSmallTextCustom(Region text,boolean isMainScreen,String extraCss){
        Region parent = isMainScreen ? mainScreenFullScreen : startScreenFullScreen;
        bindTextToParentWidthWithMaxSizeCustomCss(parent,text,smallMaxSize,smallParentToTextSize,extraCss);
    }

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


    public void bindTextToParentWidthWithMaxSizeCustomCss(Region parent,Region text,double maxSize,double percentExpectedSize,String extraCss){
        NumberBinding sizeBinding = getMaxSizeBindingWidth(parent,maxSize,percentExpectedSize);
        text.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString(),"; ",extraCss));

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

    /** This method creates a text object then bases the width off of that. For now only small binding is needed**/
    // this is a very specific method created for only one purpose
    public void smallTextMinWidthBindingSpecial(String text, Label label){

        DoubleBinding textWidthBinding = new DoubleBinding() {
            {
                super.bind(label.textProperty(), label.fontProperty());
            }

            @Override
            protected double computeValue() {
                Text helperText = new Text(label.getText());
                NumberBinding sizeBinding = getMaxSizeBindingWidth(mainScreenFullScreen,smallMaxSize,smallParentToTextSize);
                helperText.styleProperty().bind(Bindings.concat("-fx-font-size: ", sizeBinding.asString()));
                helperText.setFont(label.getFont()); // Match the label's font
                return helperText.getLayoutBounds().getWidth();
            }
        };
        label.minWidthProperty().bind(textWidthBinding);
    }
}
