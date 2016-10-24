package com.uja.game.gameObjects.menuObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A HoverableObject represents an object that acts like a button.
 * On hover it switches to another texture, and it can be clicked.
 * Created by jonathanalp on 2016/09/17.
 */

public class HoverableObject extends GameObject {
    public static final float DEFAULT_VOLUME = 0.1F;
    protected static final float CLICK_COOLDOWN = 0.5F;
    protected boolean hover, clicked;
    protected float time_passed;

    protected Sound switchSound;

    public HoverableObject(float x, float y, int width, int height, TextureManager textureManager, Sound switchSound) {
        super(x, y, width, height, textureManager);
        hover = false;
        clicked = false;
        time_passed = CLICK_COOLDOWN;
        this.switchSound = switchSound;
    }

    // Methods to retrieve click status
    // retrieveClick() allows for one click to only be registered once and not multiple times.
    public boolean isClicked() {
        return clicked;
    }

    public void declick() {
        clicked = false;
    }

    public boolean retrieveClick() {
        boolean clickStatus = isClicked();
        declick();
        return clickStatus;
    }

    public void handleInput() {
        boolean initialHover = hover;

        // Simple bounding box check for hovering.
        if (inBoundingBox()) {
            hover = true;
            if (trueClickHappened()) {
                clicked = true;
                time_passed = 0;
            }
        } else hover = false;

        boolean finalHover = hover;

        // Whenever hover changes, switch texture and if necessary, play the sound
        if (initialHover ^ finalHover) {
            switchTexture();
            if (switchSound != null && !MainGame.muted) switchSound.play(DEFAULT_VOLUME);
        }
    }

    protected boolean inBoundingBox() {
        // Get Mouse Coordinates (Y is inverted)
        float mouseX = mouseX();
        float mouseY = mouseY();
        return x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height;
    }

    protected float mouseX() {
        float xInput = Gdx.input.getX();
        int screenWidth = Gdx.graphics.getWidth();
        xInput = xInput / screenWidth * MainGame.WIDTH;
        return xInput;
    }

    protected float mouseY() {
        float yInput = MainGame.HEIGHT - Gdx.input.getY();
        int screenHeight = Gdx.graphics.getHeight();
        yInput = yInput / screenHeight * MainGame.HEIGHT;
        return yInput;
    }

    protected boolean trueClickHappened() {
        return time_passed >= CLICK_COOLDOWN && Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }

    public void switchTexture() {
        textureManager.switchTexture();
    }

    @Override
    public void update(float dt) {
        time_passed += dt;
        handleInput();
    }

    public void render(SpriteBatch sb) {
        sb.draw(textureManager.getCurrentTexture(), x, y, width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (switchSound != null) switchSound.dispose();
    }

}
