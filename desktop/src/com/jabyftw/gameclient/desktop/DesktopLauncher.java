package com.jabyftw.gameclient.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.util.Constants;

public class DesktopLauncher {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = Constants.Display.WINDOW_TITLE;
        config.resizable = false;
        config.fullscreen = false;
        config.width = Constants.Display.V_WIDTH;
        config.height = Constants.Display.V_HEIGHT;
        new LwjglApplication(new Main(), config);
    }

}
