package chessengine.Graphics;

import chessengine.TriggerRegistry;
import chessengine.Triggers.Loginable;
import chessengine.Triggers.Onlineable;
import chessserver.Communication.User;
import chessserver.User.UserInfo;
import chessserver.User.UserPreferences;
import chessserver.User.UserWGames;

public abstract class AppSubWindow implements Onlineable, Loginable, UserConfigurable, Resettable, StartupSequence {

    public AppSubWindow() {
        TriggerRegistry.addTriggerable(this);
    }

    @Override
    public void initLayout() {

    }

    @Override
    public void initGraphics() {

    }

    @Override
    public void afterInit() {

    }

    @Override
    public void resetState() {

    }

    @Override
    public void updateWithUser(UserWGames user) {

    }

    @Override
    public void onLogin() {

    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onOnline() {

    }

    @Override
    public void onOffline() {

    }
}
