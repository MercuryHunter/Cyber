package com.uja.game.gameObjects.puzzleObjects.triggering;

import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * For use with LaserShooter
 * Created by jonathanalp on 2016/09/20.
 */

public class LaserBeamPart extends GameObject {

    // This isn't special, just used for differentiation.

    public LaserBeamPart(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
    }

    @Override
    public void update(float dt) {

    }

    public void moveX(float amount) {
        x += amount;
    }

    public void moveY(float amount) {
        y += amount;
    }
}
