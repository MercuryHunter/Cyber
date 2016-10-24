package com.uja.game.gameObjects.puzzleObjects.triggering;

/**
 * The triggerable interface applies to objects that can be triggered to perform an action.
 * These objects respond to the trigger method, taking an id that was triggered.
 * Created by jonathanalp on 2016/09/21.
 */

public interface Triggerable {

    void trigger(int id);

}
