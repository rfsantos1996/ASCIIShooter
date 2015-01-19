package com.jabyftw.gameclient.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jabyftw.gameclient.Main;

public class DesktopLauncher {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = Main.WINDOW_TITLE;
        config.resizable = false;
        config.fullscreen = false;
        config.width = Main.V_WIDTH;
        config.height = Main.V_HEIGHT;
        new LwjglApplication(new Main(), config);
    }

}
