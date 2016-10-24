package com.uja.game.gameObjects.puzzleObjects.unmoving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.gameObjects.puzzleObjects.triggering.Triggerable;
import com.uja.game.objectUtilities.TextureManager;

import java.util.List;

/**
 * A Door is a triggerable object that opens and closes and plays a whoosh sound.
 * Created by jonathanalp on 2016/09/21.
 */

public class Door extends GameObject implements Triggerable {

    private List<Integer> ids;
    private boolean open;
    private Sound whoosh;

    public Door(float x, float y, boolean vertical, List<Integer> ids) {
        this(x, y, 32, 32, getAppropriateTextureManager(vertical), ids);
    }

    public Door(float x, float y, int width, int height, TextureManager textureManager, List<Integer> ids) {
        super(x, y, width, height, textureManager);
        this.ids = ids;
        open = false;
        whoosh = Gdx.audio.newSound(Gdx.files.internal("levels/sounds/door_whoosh.wav"));
    }

    // Get appropriate textures depending on whether vertical or horizontal
    private static TextureManager getAppropriateTextureManager(boolean vertical) {
        if (vertical)
            return new TextureManager(new String[]{"levels/objects/sliding_door_vertical.png", "levels/objects/sliding_door_vertical_open.png"}, true);
        return new TextureManager(new String[]{"levels/objects/sliding_door_horizontal.png", "levels/objects/sliding_door_horizontal_open.png"}, true);
    }

    @Override
    public void update(float dt) {

    }

    // Switch between open and closed
    public void switchState() {
        if (open) textureManager.switchTexture(0);
        else textureManager.switchTexture(1);

        open = !open;
    }

    // Switch state if triggered and play the sound if necessary.
    @Override
    public void trigger(int id) {
        if (ids.contains(id)) {
            if (!MainGame.muted) whoosh.play(0.1F);
            switchState();
        }
    }
}
