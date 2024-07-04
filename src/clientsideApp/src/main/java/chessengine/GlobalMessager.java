package chessengine;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.util.Duration;


public class GlobalMessager {
    private Pane startMessager;
    private Pane mainMessager;



    private boolean isInit;

    public GlobalMessager(){
        isInit = false;
    }

    public void Init(Pane startMessager, Pane mainMessager){
        this.startMessager = startMessager;
        this.mainMessager = mainMessager;
        isInit = true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void sendMessageQuick(String message,boolean isforStart){
        sendMessage(message,isforStart,Duration.seconds(2));



    }

    public void sendMessage(String message,boolean isforStart,Duration duration){
        if(isInit){
            Label messageL = new Label(message);
            Pane theOne = isforStart ? startMessager : mainMessager;
            PathTransition transition = new PathTransition();
            FadeTransition ftransition = new FadeTransition();

            Line path = new Line(theOne.getWidth()/2,theOne.getHeight()/2,theOne.getWidth()/2,0);
            transition.setPath(path);
            transition.setDuration(duration);
            transition.setInterpolator(Interpolator.EASE_OUT);
            transition.setOnFinished(e->{
                theOne.getChildren().remove(messageL);
            });
            theOne.getChildren().add(messageL);

            ftransition.setFromValue(1.0);
            ftransition.setToValue(0);
            ftransition.setDuration(duration);

            ParallelTransition pTrans = new ParallelTransition(messageL,transition,ftransition);

            pTrans.play();

        }
        else{
            ChessConstants.mainLogger.error("Trying to send a global message before it is init");

        }


    }
}
