package com.uja.game.gameObjects.puzzleObjects.triggering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A LaserShooter is a Triggerable GameObject that consists of beam parts.
 * It can move in a set pattern, switching back and forth.
 * Methods that apply to it apply to every beam part.
 * Created by jonathanalp on 2016/09/19.
 */

public class LaserShooter extends GameObject implements Triggerable {

    private List<LaserBeamPart> beamParts;

    private boolean vertical;

    private boolean triggered;
    private List<Integer> ids;

    // One of "Up", "Down", "Left", "Right"
    private String direction;
    private float amountToMove, currentlyMoved, speed;

    public LaserShooter(float x1, float y1, float x2, float y2, List<Integer> ids, String moveDirection, float amountToMove, float speed) {
        this(x1, y1, (int) (x2 - x1), (int) (y2 - y1), getAppropriateTextureString(x1, y1, x2, y2), ids, moveDirection, amountToMove, speed);
    }

    private LaserShooter(float x, float y, int width, int height, String texture, List<Integer> ids, String moveDirection, float amountToMove, float speed) {
        super(x, y, width, height, null);

        this.ids = ids;
        triggered = false;

        // For the bounding box
        vertical = (width == 0);
        if (vertical) this.width = 32;
        else this.height = 32;

        // Set up the LaserBeamParts
        beamParts = new ArrayList<LaserBeamPart>();
        for (int i = 0; i < (vertical ? height : width) / 32; i++) {
            beamParts.add(new LaserBeamPart(x + (vertical ? 0 : i * 32), y + (vertical ? i * 32 : 0), 32, 32, new TextureManager(texture, true)));
        }

        currentlyMoved = 0;
        direction = moveDirection;
        this.amountToMove = amountToMove;
        this.speed = speed;
    }

    private static String getAppropriateTextureString(float x1, float y1, float x2, float y2) {
        if (x1 - x2 == 0) return "levels/objects/laser-vertical.png";
        else return "levels/objects/laser-horizontal.png";
    }

    @Override
    public void update(float dt) {
        // Move the LaserShooter when not triggered if necessary.
        if (direction.equals("none") || triggered) return;

        float moveAmount = speed * dt;
        moveAmount = Math.min(amountToMove - currentlyMoved, moveAmount);
        currentlyMoved += moveAmount;

        // Move appropriately.
        switch (direction) {
            case "Up":
                y += moveAmount;
                for (LaserBeamPart beam : beamParts) beam.moveY(moveAmount);
                break;
            case "Down":
                y -= moveAmount;
                for (LaserBeamPart beam : beamParts) beam.moveY(-moveAmount);
                break;
            case "Left":
                x -= moveAmount;
                for (LaserBeamPart beam : beamParts) beam.moveX(-moveAmount);
                break;
            case "Right":
                x += moveAmount;
                for (LaserBeamPart beam : beamParts) beam.moveX(moveAmount);
                break;
        }

        if (currentlyMoved >= amountToMove) {
            direction = flipDirection(direction);
            currentlyMoved = 0;
        }
    }

    // Helper method for movement to flip directions.
    private String flipDirection(String direction) {
        switch (direction) {
            case "Up":
                return "Down";
            case "Down":
                return "Up";
            case "Left":
                return "Right";
            case "Right":
                return "Left";
        }
        return "none";
    }

    public List<LaserBeamPart> getBeams() {
        return beamParts;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (triggered) return;
        for (LaserBeamPart beam : beamParts)
            beam.render(sb);
    }

    @Override
    public void renderDebug(SpriteBatch sb) {
        if (triggered) return;
        sb.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(sb.getProjectionMatrix());

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
        sb.begin();
        for (LaserBeamPart beam : beamParts) beam.renderDebug(sb);
    }

    @Override
    public boolean pixelCollision(GameObject object2, float newX, float newY) {
        if (triggered) return false;
        for (LaserBeamPart beam : beamParts)
            if (beam.pixelCollision(object2, newX, newY))
                return true;
        return false;
    }

    @Override
    public void trigger(int id) {
        if (ids.contains(id))
            triggered = !triggered;
    }
}
