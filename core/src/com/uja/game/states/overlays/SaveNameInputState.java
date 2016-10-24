package com.uja.game.states.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.loaders.SaveLoader;
import com.uja.game.states.State;
import com.uja.game.states.StateManager;

/**
 * The SaveNameInputState is a state used to get the user's name for the save, and set it accordingly.
 * Created by jonathanalp on 2016/09/22.
 */

public class SaveNameInputState extends State {

    private static final float TIME_BETWEEN_DELETES = 0.15F;
    private static BitmapFont font = new BitmapFont(Gdx.files.internal("menu/segoe.fnt"));
    private Texture background;
    private SaveLoader save;
    private GlyphLayout label, enteredTextLabel;
    private StringBuilder enteredText;
    private float waited_time;

    public SaveNameInputState(StateManager stateManager, SaveLoader save) {
        super(stateManager);

        background = new Texture("levels/pause/transparent_bg.png");
        label = new GlyphLayout(font, "What's your name?");
        enteredTextLabel = new GlyphLayout(font, "");

        this.save = save;

        enteredText = new StringBuilder();

        waited_time = 0;
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            stateManager.unoverlay();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && enteredText.length() > 0) {
            save.setName(enteredText.toString());
            save.setCurrentLevel(0);
            save.save();
            SaveLoader.setLastSave(save.getSaveFile());
            stateManager.unoverlay();
            stateManager.overlay(new MessageState(stateManager, "Save Created"));
        }

        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 29; i < 55; i++) {
            if (Gdx.input.isKeyJustPressed(i)) enteredText.append(letters.charAt(i - 29));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) && waited_time > TIME_BETWEEN_DELETES && enteredText.length() > 0) {
            waited_time = 0;
            enteredText.deleteCharAt(enteredText.length() - 1);
        }
    }

    @Override
    public void update(float dt) {
        waited_time += dt;
        handleInput();
        enteredTextLabel = new GlyphLayout(font, enteredText.toString());
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.enableBlending();
        sb.begin();
        sb.draw(background, MainGame.WIDTH / 2 - 150, MainGame.HEIGHT / 2 - 45, 300, 90);
        font.draw(sb, label, MainGame.WIDTH / 2 - label.width / 2, MainGame.HEIGHT / 2 + 35);
        font.draw(sb, enteredTextLabel, MainGame.WIDTH / 2 - enteredTextLabel.width / 2, MainGame.HEIGHT / 2 + 45 - label.height - 30);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
