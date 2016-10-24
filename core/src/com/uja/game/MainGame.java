package com.uja.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.states.MainMenuState;
import com.uja.game.states.StateManager;

public class MainGame extends ApplicationAdapter {
    public static final int WIDTH = 1280, HEIGHT = 768;
    public static final String TITLE = "Cyber";

    public static boolean muted;

    SpriteBatch batch;
    private StateManager stateManager;

    @Override
    public void create() {
        muted = false;
        batch = new SpriteBatch();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        stateManager = new StateManager();
        stateManager.push(new MainMenuState(stateManager));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateManager.update(Gdx.graphics.getDeltaTime());
        stateManager.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
