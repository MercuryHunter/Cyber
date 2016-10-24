package com.uja.game.gameObjects.puzzleObjects.unmoving;

import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A Tile represents a tile of the tilemap and is used for differentiation.
 * Tile x and y represent actual screen coordinates.
 * Created by jonathanalp on 2016/09/08.
 */
public class Tile extends GameObject {

    public Tile(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
    }

    @Override
    public void update(float dt) {
    }

}
