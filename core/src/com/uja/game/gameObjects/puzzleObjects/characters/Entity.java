package com.uja.game.gameObjects.puzzleObjects.characters;

import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * An Entity is a gameObject can move around and has the ability to die.
 * Created by jonathanalp on 2016/09/08.
 */

public class Entity extends GameObject {

    protected static final float TIME_TO_SWITCH = 0.3F;
    private static final float TIME_TO_LASER_DEATH = 0.10F;
    protected float time_between_flips;
    protected float dx, dy;
    protected float speed;
    protected boolean dead;
    protected Direction lastDirection;
    protected boolean moveable;
    protected boolean up, down, left, right;
    private float timeTouchingLaser;

    public Entity(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
        dx = 0;
        dy = 0;
        speed = 120.0F;

        timeTouchingLaser = 0;
        dead = false;

        lastDirection = Direction.Forward;
        moveable = true;

        time_between_flips = 0;
    }

    public void stopMoving() {
        moveable = false;
    }

    public void move(float newX, float newY) {
        x = newX;
        y = newY;
    }

    public void move() {
        x = newX();
        y = newY();
    }

    public float newX() {
        return x + dx;
    }

    public float newY() {
        return y + dy;
    }

    public boolean isDead() {
        return dead;
    }

    public void touchLaser(float dt) {
        timeTouchingLaser += dt;
        if (timeTouchingLaser >= TIME_TO_LASER_DEATH) {
            die();
        }
    }

    protected void die() {
        dead = true;
    }

    @Override
    public void update(float dt) {
        if (dead) {
            textureManager.switchTexture(12);
            return;
        }

        time_between_flips += dt;

        if (!moveable) return;

        // The textures follow a set pattern that it needs to provided in for animation.
        // Ugly Animations
        int index = 0;
        boolean flipNow = false;
        if (!up && !down && !left && !right) {
            index = 1;
            lastDirection = Direction.None;
            flipNow = true;
        } else if (right) {
            if (lastDirection.equals(Direction.Right)) {
                index = textureManager.getIndex() + 1;
                if (index < 6 || index >= 9) index = 6;
            } else {
                index = 6;
                flipNow = true;
            }

            lastDirection = Direction.Right;
        } else if (left) {
            if (lastDirection.equals(Direction.Left)) {
                index = textureManager.getIndex() + 1;
                if (index < 3 || index >= 6) index = 3;
            } else {
                index = 3;
                flipNow = true;
            }
            lastDirection = Direction.Left;
        } else if (up) {
            if (lastDirection.equals(Direction.Backward)) {
                index = textureManager.getIndex() + 1;
                if (index < 9 || index >= 12) index = 9;
            } else {
                index = 9;
                flipNow = true;
            }
            lastDirection = Direction.Backward;
        } else if (down) {
            if (lastDirection.equals(Direction.Forward)) {
                index = textureManager.getIndex() + 1;
                if (index < 0 || index >= 3) index = 0;
            } else {
                index = 0;
                flipNow = true;
            }
            lastDirection = Direction.Forward;
        }

        if (flipNow || time_between_flips > TIME_TO_SWITCH) {
            textureManager.switchTexture(index);
            time_between_flips = 0;
        }
    }

    protected enum Direction {Left, Right, Backward, Forward, None}

}
