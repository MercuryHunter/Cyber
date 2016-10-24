package com.uja.game.states.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.states.State;
import com.uja.game.states.StateManager;

/**
 * A MessageState overlays a message to the screen and can be dismissed to return back.
 * Created by jonathanalp on 2016/09/27.
 */

public class MessageState extends State {

    private static BitmapFont font = new BitmapFont(Gdx.files.internal("menu/segoe.fnt"));
    private Texture background;
    private GlyphLayout messageLabel;

    public MessageState(StateManager stateManager, String message) {
        super(stateManager);

        background = new Texture("levels/pause/transparent_bg.png");
        messageLabel = new GlyphLayout(font, message);
    }


    @Override
    protected void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))// || Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            stateManager.unoverlay();
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.enableBlending();
        sb.begin();
        sb.draw(background, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        font.draw(sb, messageLabel, MainGame.WIDTH / 2 - messageLabel.width / 2, MainGame.HEIGHT / 2 + messageLabel.height / 2);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
