package com.uja.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.levels.Level;
import com.uja.game.loaders.SaveLoader;
import com.uja.game.loaders.StoryLoader;
import com.uja.game.states.overlays.MessageState;
import com.uja.game.states.overlays.PauseState;

/**
 * A GameState represents where play occurs.
 * It contains the level it displays - it's basically a container for it.
 * If a level is complete, it saves your progress to your save.
 * Created by jonathanalp on 2016/09/05.
 */

public class GameState extends State {
    public static boolean DEBUG = false;

    private int levelNumber;
    private Level level;
    private SaveLoader save;
    private Music levelMusic;

    protected GameState(StateManager stateManager, int levelNumber, SaveLoader save) {
        super(stateManager);
        this.levelNumber = levelNumber;
        level = new Level(levelNumber);
        this.save = save;

        levelMusic = Gdx.audio.newMusic(Gdx.files.internal("levels/level_music.mp3"));
        levelMusic.setLooping(true);
        levelMusic.setVolume(MainGame.muted ? 0 : Level.DEFAULT_VOLUME);
        levelMusic.play();
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.D))
            DEBUG = !DEBUG;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            stateManager.overlay(new PauseState(stateManager, levelMusic));
    }

    @Override
    public void update(float dt) {
        handleInput();

        if (level.shouldShowLevelNotMadeMessage()) {
            stateManager.overlay(new MessageState(stateManager, "Sorry, this level hasn't been made yet..."));
        }
        if (level.isFinishedUnwillingly()) {
            stateManager.set(new MainMenuState(stateManager));
        }
        if (level.isFinished()) {
            boolean newLevelCompleted = save.completeLevel(levelNumber);
            save.save();
            if (newLevelCompleted) {
                // TODO: Add favour
                stateManager.overlay(new MessageState(stateManager, StoryLoader.getStory(levelNumber)));
            }
            stateManager.set(new MainMenuState(stateManager));
        }
        level.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        if (!level.isFinished()) level.render(sb);
    }

    @Override
    public void dispose() {
        if (levelMusic != null) levelMusic.dispose();
        level.dispose();
    }
}
