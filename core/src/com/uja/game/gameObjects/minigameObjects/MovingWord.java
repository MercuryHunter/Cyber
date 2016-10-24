package com.uja.game.gameObjects.minigameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

/**
 * Created by jonathanalp on 2016/08/15.
 */

public class MovingWord extends GameObject {

    private String word, enteredWord;
    private float speed;
    private boolean won;

    public MovingWord(float x, float y, int width, int height, String word, float speed) {
        super(x, y, width, height, new TextureManager("riddles/textbg.png", false));
        this.word = word;
        this.speed = speed;
        won = false;
    }

    public void enterLetter(char letter) {
        if (word.charAt(enteredWord.length() - 1) == letter) {
            enteredWord = enteredWord + letter;
            if (enteredWord.equals(word)) won = true;
        }
    }

    public boolean hasWon() {
        return won;
    }

    // TODO: Handle entering word letters
    // TODO: Handle off-screen (loss)
    @Override
    public void update(float dt) {
        x -= speed * dt;
        y += speed * dt * Math.signum(Math.random() * 2 - 1);
    }

    public void render(SpriteBatch sb) {
        super.dispose();
        // TODO: Draw Text
    }
}
