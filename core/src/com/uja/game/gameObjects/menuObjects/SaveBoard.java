package com.uja.game.gameObjects.menuObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.loaders.SaveLoader;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A SaveBoard is a specialised HoverableObject that holds a save object.
 * Created by jonathanalp on 2016/09/22.
 */

public class SaveBoard extends HoverableObject {

    private static BitmapFont font = new BitmapFont(Gdx.files.internal("menu/segoe.fnt"));
    private SaveLoader save;
    private GlyphLayout label;

    public SaveBoard(float x, float y, SaveLoader save) {
        this(x, y, 300, 210, new TextureManager(new String[]{"saves/images/blackboard.png", "saves/images/blackboard-hover.png"}, false), null, save);
    }

    public SaveBoard(float x, float y, int width, int height, TextureManager textureManager, Sound switchSound, SaveLoader save) {
        super(x, y, width, height, textureManager, switchSound);
        this.save = save;
        label = new GlyphLayout(font, save.getName());
    }

    public void updateLabel() {
        label = new GlyphLayout(font, save.getName());
    }

    public SaveLoader getSave() {
        return save;
    }

    public void render(SpriteBatch sb) {
        super.render(sb);
        font.draw(sb, label, x + 150 - label.width / 2, y + 125 - label.height / 2);
    }
}
