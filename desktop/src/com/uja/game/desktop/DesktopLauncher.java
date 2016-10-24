package com.uja.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.uja.game.MainGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.height = MainGame.HEIGHT;
        config.width = MainGame.WIDTH;
        config.title = MainGame.TITLE;
        config.fullscreen = true;

        new LwjglApplication(new MainGame(), config);
    }
}
