package com.uja.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.uja.game.MainGame;

/**
 * A State represents a view into the game, be it a menu, a pause screen etc.
 * It makes use of a state manager to switch between states, and must implement
 * handling input, updating and rendering.
 * Created by jonathanalp on 2016/08/03.
 */

public abstract class State {

    protected StateManager stateManager;
    protected Vector3 mouse;
    protected OrthographicCamera cam;

    protected State(StateManager stateManager) {
        this.stateManager = stateManager;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, MainGame.WIDTH, MainGame.HEIGHT);
        cam.update();
        mouse = new Vector3();
    }

    protected abstract void handleInput();

    public abstract void update(float dt);

    public abstract void render(SpriteBatch sb);

    public abstract void dispose();

}
