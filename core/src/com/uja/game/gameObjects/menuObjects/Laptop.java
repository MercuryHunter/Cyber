package com.uja.game.gameObjects.menuObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.puzzleObjects.characters.Entity;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A Laptop is what is clicked to start a level.
 * It is different from a HoverableObject in that it also makes use of pixel collisions
 * and can be enabled or disabled.
 * Created by jonathanalp on 2016/09/18.
 */

public class Laptop extends HoverableObject {

    private static BitmapFont font = new BitmapFont(Gdx.files.internal("menu/segoe.fnt"));
    private boolean enabled;
    private Entity temporaryPointer;
    private int levelNumber;
    private GlyphLayout label;

    public Laptop(float x, float y, int width, int height, Sound switchSound, boolean enabled, int levelNumber) {
        super(x, y, width, height, new TextureManager(new String[]{"menu/laptop-closed.png", "menu/laptop-open.png", "menu/laptop-open-shining.png"}, true), switchSound);
        this.enabled = enabled;
        temporaryPointer = new Entity(0, 0, 1, 1, new TextureManager("menu/block.png", true));
        if (enabled) textureManager.switchTexture(1);
        this.levelNumber = levelNumber;
        label = new GlyphLayout(font, levelNumber + "");
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    @Override
    public void handleInput() {
        if (enabled) {
            boolean initialHover = hover;

            float mouseX = mouseX();
            float mouseY = mouseY();

            // Bounding box and pixel collision check
            temporaryPointer.move(mouseX, mouseY);
            if (inBoundingBox() && this.pixelCollision(temporaryPointer, mouseX, mouseY)) {
                hover = true;
                if (trueClickHappened()) {
                    clicked = true;
                    time_passed = 0;
                }

            } else hover = false;

            boolean finalHover = hover;
            if (initialHover ^ finalHover)
                if (switchSound != null && !MainGame.muted)
                    switchSound.play(0.5F);

            if (hover) textureManager.switchTexture(2);
            else textureManager.switchTexture(1);
        }
    }

    public void render(SpriteBatch sb) {
        super.render(sb);
        // Draw the level number
        if (hover) font.draw(sb, label, x + 25, y + 70);
    }

    public void dispose() {
        super.dispose();
        temporaryPointer.dispose();
    }

}
