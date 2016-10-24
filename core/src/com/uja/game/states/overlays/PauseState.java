package com.uja.game.states.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.menuObjects.HoverableObject;
import com.uja.game.gameObjects.menuObjects.SoundButton;
import com.uja.game.objectUtilities.TextureManager;
import com.uja.game.states.MainMenuState;
import com.uja.game.states.State;
import com.uja.game.states.StateManager;

/**
 * A PauseState is the menu overlayed while playing, with an exit and resume button.
 * Created by jonathanalp on 2016/09/05.
 */

public class PauseState extends State {

    private Texture background;
    private HoverableObject resumeButton, exitButton;
    private SoundButton soundButton;

    public PauseState(StateManager stateManager, Music music) {
        super(stateManager);

        background = new Texture("levels/pause/transparent_bg.png");
        resumeButton = new HoverableObject(MainGame.WIDTH / 2 - 215, 400, 430, 160, new TextureManager(new String[]{"levels/pause/resume_button.png", "levels/pause/resume_button_hover.png"}, false), null);
        exitButton = new HoverableObject(MainGame.WIDTH / 2 - 215, 200, 430, 160, new TextureManager(new String[]{"levels/pause/exit_button.png", "levels/pause/exit_button_hover.png"}, false), null);
        soundButton = new SoundButton(MainGame.WIDTH - 150, 50, 100, 100, new TextureManager(new String[]{"menu/sound-white.png", "menu/sound-hover.png", "menu/sound-empty.png"}, false), Gdx.audio.newSound(Gdx.files.internal("menu/sounds/speaker_sound.wav")), music);
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || resumeButton.retrieveClick())
            stateManager.unoverlay();
        if (exitButton.retrieveClick()) {
            stateManager.unoverlay();
            stateManager.set(new MainMenuState(stateManager));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        resumeButton.update(dt);
        exitButton.update(dt);
        soundButton.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.enableBlending();
        sb.begin();
        sb.draw(background, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        resumeButton.render(sb);
        exitButton.render(sb);
        soundButton.render(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        resumeButton.dispose();
        exitButton.dispose();
    }

}
