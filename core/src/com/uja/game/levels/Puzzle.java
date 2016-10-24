package com.uja.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.gameObjects.puzzleObjects.characters.Entity;
import com.uja.game.gameObjects.puzzleObjects.characters.Guard;
import com.uja.game.gameObjects.puzzleObjects.characters.Player;
import com.uja.game.gameObjects.puzzleObjects.hud.Timer;
import com.uja.game.gameObjects.puzzleObjects.triggering.FloorButton;
import com.uja.game.gameObjects.puzzleObjects.triggering.LaserShooter;
import com.uja.game.gameObjects.puzzleObjects.triggering.Triggerable;
import com.uja.game.gameObjects.puzzleObjects.unmoving.EndingBook;
import com.uja.game.loaders.ChallengeLoader;
import com.uja.game.objectUtilities.TileMap;
import com.uja.game.states.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * A puzzle represents a hacking challenge, wherein there is a player, and obstacles between
 * reaching the win condition (Ending Book).
 * It makes use of the ChallengeLoader to load in objects accordingly.
 * Created by jonathanalp on 2016/08/14.
 */

public class Puzzle extends Challenge {

    private TileMap tileMap;

    private boolean needsReset, won;

    private Player player;
    private Guard[] guards;

    private Timer timer;

    public Puzzle(String challengeFile, float timePassed) throws ChallengeLoader.LevelNotMadeException {
        super();
        this.timePassed = timePassed;
        needsReset = false;
        won = false;
        timer = new Timer();

        ChallengeLoader challengeLoader = new ChallengeLoader(challengeFile, ChallengeType.Puzzle);
        tileMap = challengeLoader.getTileMap();

        GameObject[] loadedUntouchableObjects = challengeLoader.getUntouchableGameObjects();
        for (GameObject object : loadedUntouchableObjects) addCollidingObject(object);

        GameObject[] loadedTouchableObjects = challengeLoader.getTouchableGameObjects();
        for (GameObject object : loadedTouchableObjects) addTouchableObject(object);

        player = new Player(challengeLoader.getPlayerPosition());
        addCollidingObject(player);

        guards = challengeLoader.getGuards();
        for (Guard guard : guards) touchableTree.add(guard);
    }

    public float getTimeWon() {
        return timer.getTime();
    }

    public boolean needsReset() {
        return needsReset;
    }

    public boolean won() {
        return won;
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            player.stopMoving();
            needsReset = true;
        }
    }

    public void update(float dt) {
        if (dt > 0.5) return; // Don't allow for initial big skip

        super.update(dt);
        handleInput();

        // Movement Collision
        // Entity Collision followed by tile collision, in each axis
        if (!player.isDead() && !movementCollides(player, player.newX(), player.getY()) && !tileMap.tileCollides(player, player.newX(), player.getY()))
            player.move(player.newX(), player.getY());
        if (!player.isDead() && !movementCollides(player, player.getX(), player.newY()) && !tileMap.tileCollides(player, player.getX(), player.newY()))
            player.move(player.getX(), player.newY());
        collisionTree.update(player);

        // Touchable Collision
        touchableCollisions(player, dt);
        if (player.isDead()) {
            needsReset = true;
        }

        // Perform object interactions (e.g. buttons)
        for (GameObject object : objects) {
            if (object instanceof FloorButton)
                triggerObject(((FloorButton) object).getAffectedID());
            if (object instanceof LaserShooter)
                touchableTree.update(object);
        }

        if (won || needsReset) return;
        // This stuff stops updating once the game is done with.

        // Update guards
        for (Guard guard : guards) {
            guard.update(dt, player, collisionTree);
            if (!guard.isDead() && !movementCollides(guard, guard.newX(), guard.getY()) && !tileMap.tileCollides(guard, guard.newX(), guard.getY()))
                guard.move(guard.newX(), guard.getY());
            if (!guard.isDead() && !movementCollides(guard, guard.getX(), guard.newY()) && !tileMap.tileCollides(guard, guard.getX(), guard.newY()))
                guard.move(guard.getX(), guard.newY());
            touchableTree.update(guard);
        }

        timer.update(timePassed);
    }

    private boolean movementCollides(Entity entity, float newX, float newY) {
        // Spatial Structure
        List<GameObject> neighbours = new ArrayList<GameObject>();
        collisionTree.retrieve(neighbours, entity);
        //System.out.println("Number of neighbours: " + neighbours.size());
        for (GameObject neighbour : neighbours) {
            if (neighbour == entity || neighbour == player) continue;
            // Bounding Shape
            if (newX < neighbour.getX() + neighbour.getWidth() && neighbour.getX() < newX + entity.getWidth() &&
                    newY < neighbour.getY() + neighbour.getHeight() && neighbour.getY() < newY + entity.getHeight()) {
                // Pixel Level Test
                if (neighbour.pixelCollision(entity, newX, newY)) return true;
            }
        }
        return false;
    }

    public void touchableCollisions(Entity entity, float dt) {
        List<GameObject> neighbours = new ArrayList<GameObject>();
        touchableTree.retrieve(neighbours, entity);
        for (GameObject neighbour : neighbours) {
            if (neighbour == entity) continue;

            if (entity.getX() < neighbour.getX() + neighbour.getWidth() && neighbour.getX() < entity.getX() + entity.getWidth() &&
                    entity.getY() < neighbour.getY() + neighbour.getHeight() && neighbour.getY() < entity.getY() + entity.getHeight()) {
                // Pixel Level Test
                if (neighbour.pixelCollision(entity, entity.getX(), entity.getY())) {
                    // Deal with different object touches.
                    if (neighbour instanceof FloorButton)
                        ((FloorButton) neighbour).push(dt);
                    if (neighbour instanceof LaserShooter)
                        entity.touchLaser(dt);
                    if (neighbour instanceof EndingBook && entity instanceof Player) {
                        entity.move(neighbour.getX(), neighbour.getY());
                        entity.stopMoving();
                        if (!won && !MainGame.muted)
                            Gdx.audio.newSound(Gdx.files.internal("levels/sounds/you_win_medium.wav")).play();
                        won = true;
                    }
                    if (neighbour instanceof Guard && entity instanceof Player) {
                        ((Player) entity).touchGuard(dt);
                    }
                }
            }
        }
    }

    // Trigger objects appropriately.
    // TODO: Give each triggerable object a reference to the object(s) it triggers so this isn't required
    private void triggerObject(int id) {
        if (id != -1) {
            for (GameObject object : objects)
                if (object instanceof Triggerable)
                    ((Triggerable) object).trigger(id);
            for (Guard guard : guards)
                guard.trigger(id);
        }

    }

    public void render(SpriteBatch sb) {
        sb.begin();
        tileMap.render(sb);
        sb.end();

        super.render(sb);

        sb.begin();
        for (Guard guard : guards) {
            guard.render(sb);
            if (GameState.DEBUG) guard.renderDebug(sb);
        }
        timer.render(sb);
        sb.end();
    }
}
