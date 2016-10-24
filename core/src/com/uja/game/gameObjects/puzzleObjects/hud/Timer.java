package com.uja.game.gameObjects.puzzleObjects.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A timer is a HUD element that takes in and formats the current time of the level to be displayed.
 * It can return the time for accurate use once it stops updating.
 * Created by jonathanalp on 2016/09/29.
 */

public class Timer extends GameObject {

    private static BitmapFont font = new BitmapFont(Gdx.files.internal("menu/segoe.fnt"));
    float time;
    private GlyphLayout label;

    // Covers whole bottom of the screen.
    public Timer() {
        this(0, 0, 110, 70, new TextureManager("levels/pause/transparent_bg.png", false));
    }

    public Timer(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
        label = new GlyphLayout(font, "");
    }

    private String timeFormat(float time) {
        float minutes = (int) time / 60;
        float seconds = (int) (time - 60 * minutes);
        return String.format("%02.0f:%02.0f", minutes, seconds);
    }

    public float getTime() {
        return time;
    }

    @Override
    public void update(float time) {
        this.time = time;
        label = new GlyphLayout(font, timeFormat(time));
    }

    public void render(SpriteBatch sb) {
        super.render(sb);
        font.draw(sb, label, 20, 50);
    }
}
