package com.uja.game.gameObjects.puzzleObjects.unmoving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * An invisible wall exists to prevent walking through walls that exist in textures
 * but not actually there.
 * Created by jonathanalp on 2016/09/25.
 */

public class InvisibleWall extends GameObject {

    public InvisibleWall(float x1, float y1, float x2, float y2) {
        this(x1, y1, (int) (x2 - x1), (int) (y2 - y1), null);
    }

    public InvisibleWall(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);

        /*
        if(width == 0) {
            this.x -= 3;
            this.width = 6;
        }
        if(height == 0) {
            this.y -= 3;
            this.height = 6;
        }
        */
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
    }

    // Have to override debug rendering, as it is just for the box.
    @Override
    public void renderDebug(SpriteBatch sb) {
        sb.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(sb.getProjectionMatrix());

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
        sb.begin();
    }

    @Override
    public boolean pixelCollision(GameObject object2, float newX, float newY) {
        // If you're in this shapes bounding box, you're colliding.
        return true;
    }

}
