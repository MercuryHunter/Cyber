package com.uja.game.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A GameObject is an abstract class that represents any object within the game
 * that can be rendered to the screen, has a debug rendering, and must be updated.
 * Created by jonathanalp on 2016/08/14.
 */

public abstract class GameObject {

    protected float x, y;
    protected int width, height;
    protected TextureManager textureManager;
    protected ShapeRenderer shapeRenderer;

    // When a specific gameObject is made, it will deal with generating
    // the right textureManager to pass into the super constructor.
    public GameObject(float x, float y, int width, int height, TextureManager textureManager) {
        shapeRenderer = new ShapeRenderer();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureManager = textureManager;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getCurrentBitMask() {
        return textureManager.getCurrentBitMask();
    }

    // A pixel collision test using the current bitmask from the texture manager object.
    public boolean pixelCollision(GameObject object2, float newX, float newY) {
        int[][] thisMask = getCurrentBitMask();
        int[][] theirMask = object2.getCurrentBitMask();

        for (int x_num = (int) Math.max(x, newX); x_num <= Math.min(x + width - 1, newX + object2.width - 1); x_num++) {
            for (int y_num = (int) Math.max(y, newY); y_num <= Math.min(y + height - 1, newY + object2.height - 1); y_num++) {
                // System.out.printf("Accessing at: e2: x,y: %d,%d\ne1: x,y: %d,%d\n", x_num - (int)e2.x, y_num - (int)e2.y, x_num - (int)newX, y_num - (int)newY);
                if ((theirMask[y_num - (int) newY][x_num - (int) newX] & thisMask[y_num - (int) y][x_num - (int) x]) > 0)
                    return true;
            }
        }

        return false;
    }

    public abstract void update(float dt);

    // Game Objects will be called to render in-between a SpriteBatch start and end.
    public void render(SpriteBatch sb) {
        sb.draw(textureManager.getCurrentTextureRegion(), x, y, width, height);
    }

    // This needs to be called and extended upon from many GameObjects
    public void dispose() {
        shapeRenderer.dispose();
    }

    // Display BitMasks and Bounding Rectangle
    public void renderDebug(SpriteBatch sb) {
        int[][] bitMask = getCurrentBitMask();

        sb.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(sb.getProjectionMatrix());

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int y1 = 0; y1 < bitMask.length; y1++) {
            for (int x1 = 0; x1 < bitMask[y1].length; x1++) {
                if (bitMask[y1][x1] == 1) shapeRenderer.rect(x1 + x, y1 + y, 1, 1);
            }
        }
        shapeRenderer.end();

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
        sb.begin();
    }

}
