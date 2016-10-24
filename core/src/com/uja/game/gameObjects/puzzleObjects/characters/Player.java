package com.uja.game.gameObjects.puzzleObjects.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.uja.game.objectUtilities.TextureManager;

/**
 * The Player is the character the player controls, which responds to keyboard input.
 * Created by jonathanalp on 2016/09/05.
 */

public class Player extends Entity {

    private static final float TIME_TO_GUARD_DEATH = 0.10F;
    private static final String[] textures = new String[]
            {"levels/player/forward1.png", "levels/player/forward2.png", "levels/player/forward3.png",
                    "levels/player/left1.png", "levels/player/left2.png", "levels/player/left3.png",
                    "levels/player/right1.png", "levels/player/right2.png", "levels/player/right3.png",
                    "levels/player/backward1.png", "levels/player/backward2.png", "levels/player/backward3.png",
                    "levels/player/dead.png"};
    private float timeTouchingGuard;

    public Player(Vector2 position) {
        this(position.x, position.y, 26, 26, new TextureManager(textures, true));
    }

    private Player(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
        timeTouchingGuard = 0;
    }

    public void touchGuard(float dt) {
        timeTouchingGuard += dt;
        if (timeTouchingGuard >= TIME_TO_GUARD_DEATH) {
            die();
        }
    }

    @Override
    public void update(float dt) {
        dx = 0;
        dy = 0;

        up = Gdx.input.isKeyPressed(Input.Keys.UP);
        down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (up) dy = speed * dt;
        if (down) dy = -speed * dt;
        if (left) dx = -speed * dt;
        if (right) dx = speed * dt;

        super.update(dt);
    }

}
