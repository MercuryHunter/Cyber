package com.uja.game.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.QuadTree;
import com.uja.game.states.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * A Challenge is an abstract class to represent what is played in the game
 * It can have colliding objects (that can't be gone through),
 * and touchable objects (which can be gone through).
 * Created by jonathanalp on 2016/08/14.
 */
public abstract class Challenge {

    protected List<GameObject> objects;
    protected QuadTree collisionTree, touchableTree;
    protected float timePassed;

    public Challenge() {
        objects = new ArrayList<GameObject>();
        collisionTree = new QuadTree(0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        touchableTree = new QuadTree(0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        timePassed = 0;
    }

    public void addObject(GameObject gameObject) {
        objects.add(gameObject); // Un-interactable object
    }

    // Colliding Objects can't be interacted with or moved through
    public void addCollidingObject(GameObject gameObject) {
        objects.add(gameObject);
        collisionTree.add(gameObject);
    }

    // Colliding objects can be gone through and interacted with
    public void addTouchableObject(GameObject gameObject) {
        objects.add(gameObject);
        touchableTree.add(gameObject);
    }

    public float getTimePassed() {
        return timePassed;
    }

    // Update every object
    public void update(float dt) {
        timePassed += dt;

        for (GameObject object : objects)
            object.update(dt);
    }

    // Render every object and debug render if necessary.
    public void render(SpriteBatch sb) {
        sb.begin();

        for (GameObject object : objects) {
            object.render(sb);
            if (GameState.DEBUG) object.renderDebug(sb);
        }

        if (GameState.DEBUG) {
            collisionTree.renderDebug(sb);
            touchableTree.renderDebug(sb);
        }

        sb.end();
    }

    // Dispose of every object
    public void dispose() {
        for (GameObject object : objects) object.dispose();
    }

    // Represents the different types of challenges.
    public enum ChallengeType {
        Puzzle, Minigame, Spotquiz
    }
}
