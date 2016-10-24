package com.uja.game.gameObjects.puzzleObjects.unmoving;

import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * The EndingBook is the final object in the levels that is considered the win condition/
 * Created by jonathanalp on 2016/09/21.
 */

public class EndingBook extends GameObject {

    public EndingBook(float x, float y) {
        this(x, y, 32, 32, new TextureManager("levels/objects/final_book.png", true));
    }

    private EndingBook(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
    }

    @Override
    public void update(float dt) {

    }

}
