package chessengine.Graphics;

import chessengine.Enums.StartScreenState;
import chessengine.Start.StartScreenController;

public abstract class StartScreenSubWindow extends AppSubWindow{
    protected final StartScreenController controller;

    public StartScreenSubWindow(StartScreenController controller) {
        this.controller = controller;
        controller.addSubWindow(this);
    }

    protected StartScreenState getCurrentState() {
        return controller.navigationScreen.getCurrentState();
    }

    protected void setCurrentState(StartScreenState newState) {
        if(getCurrentState() != newState) {
            controller.navigationScreen.setSelection(newState);
        }
    }
}
