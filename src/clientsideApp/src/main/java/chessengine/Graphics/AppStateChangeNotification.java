package chessengine.Graphics;

import javafx.application.Preloader;

public class AppStateChangeNotification extends Preloader.StateChangeNotification {
    private final String message;

    public AppStateChangeNotification(String message) {
        super(Type.BEFORE_LOAD);  // You can choose any type like BEFORE_LOAD, etc.
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
