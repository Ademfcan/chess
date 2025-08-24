package chessengine.Graphics;

import chessengine.MainP.MainScreenController;

public abstract class MainScreenSubWindow extends AppSubWindow{
    private final MainScreenController controller;

    public MainScreenSubWindow(MainScreenController controller){
        this.controller = controller;
    }

}
