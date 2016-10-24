package com.uja.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * A StateManager is a stack of states, wherein you can change state and overlay states.
 * Created by jonathanalp on 2016/08/03.
 */

public class StateManager {

    private Stack<State> states;
    private State overlayState;

    public StateManager() {
        states = new Stack<State>();
        overlayState = null;
    }

    public void push(State state) {
        states.push(state);
    }

    public void pop() {
        states.pop().dispose();
    }

    public void set(State state) {
        pop();
        push(state);
    }

    public void overlay(State state) {
        overlayState = state;
    }

    public void unoverlay() {
        overlayState = null;
    }

    public void update(float dt) {
        if (overlayState != null) overlayState.update(dt);
        else states.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        states.peek().render(sb);
        if (overlayState != null) overlayState.render(sb);
    }

}
