package com.uja.game.gameObjects.puzzleObjects.triggering;

import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * A FloorButton is a triggering object that can be pushed down and resets after a time.
 * It has an ID it triggers, but the specific interaction happens in the level.
 * Created by jonathanalp on 2016/09/19.
 */

public class FloorButton extends GameObject {

    private static final float TIME_TO_PUSH = 0.15F, TIME_TO_RESET = 1.0F;
    private boolean pushed, triggeredOtherSide, onceOff;
    private int affectedID;
    private float time_pushed_for, time_set_for;

    public FloorButton(float x, float y, int affectedID, boolean onceOff) {
        this(x, y, 32, 32, new TextureManager(new String[]{"levels/objects/floorButton.png", "levels/objects/floorButtonPushed.png"}, true), affectedID, onceOff);
    }

    private FloorButton(float x, float y, int width, int height, TextureManager textureManager, int affectedID, boolean onceOff) {
        super(x, y, width, height, textureManager);
        this.pushed = false;
        triggeredOtherSide = false;
        this.affectedID = affectedID;
        time_pushed_for = 0;
        time_set_for = 0;
        this.onceOff = onceOff;
    }

    // Deal with being pushed down.
    public void push(float dt) {
        time_pushed_for += dt;
        if (!pushed && time_pushed_for >= TIME_TO_PUSH) {
            textureManager.switchTexture();
            pushed = true;
        }
    }

    // Return the ID it affects if it's currently being pushed.
    public int getAffectedID() {
        if (pushed && !triggeredOtherSide) {
            triggeredOtherSide = true;
            return affectedID;
        }
        return -1;
    }

    // Deal with resetting and pushing
    @Override
    public void update(float dt) {
        if (pushed) time_set_for += dt;
        if (!onceOff && time_set_for >= TIME_TO_RESET) {
            pushed = false;
            time_set_for = 0;
            time_pushed_for = 0;
            triggeredOtherSide = false;
            textureManager.switchTexture();
        }
    }

}
