package com.uja.game.gameObjects.menuObjects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.uja.game.MainGame;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A SoundButton is a specialised HoverableObject to change the global mute state
 * and also handles playing music.
 * Created by jonathanalp on 2016/09/19.
 */

public class SoundButton extends HoverableObject {

    private Music music;

    public SoundButton(float x, float y, int width, int height, TextureManager textureManager, Sound switchSound, Music music) {
        super(x, y, width, height, textureManager, switchSound);

        this.music = music;
        if (MainGame.muted) music.setVolume(0);
        else music.setVolume(DEFAULT_VOLUME);

        setTexture();
    }

    public void handleInput() {
        // Simple bounding box check for hovering.
        if (inBoundingBox()) {
            hover = true;
            if (trueClickHappened()) {
                switchMusic();
                time_passed = 0;
            }
        } else hover = false;

        setTexture();
    }

    private void setTexture() {
        if (hover) {
            if (MainGame.muted) textureManager.switchTexture(0);
            else textureManager.switchTexture(1);
        } else {
            if (MainGame.muted) textureManager.switchTexture(2);
            else textureManager.switchTexture(0);
        }
    }

    private void switchMusic() {
        if (switchSound != null) switchSound.play(0.5F);
        MainGame.muted = !MainGame.muted;
        music.setVolume(MainGame.muted ? 0 : DEFAULT_VOLUME);
    }

    @Override
    public void dispose() {
        super.dispose();
        music.dispose();
    }

}
