package com.jabyftw.gameserver;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jabyftw.gameclient.Main;

/**
 * Created by Rafael on 12/01/2015.
 */
public class ServerLauncher {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = Main.WINDOW_TITLE;
        config.resizable = true;
        config.fullscreen = false;
        config.width = Main.V_WIDTH;
        config.height = Main.V_HEIGHT;
        new LwjglApplication(new Server(), config);
    }

}
