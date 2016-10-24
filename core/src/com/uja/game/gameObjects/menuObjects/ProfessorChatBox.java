package com.uja.game.gameObjects.menuObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.objectUtilities.TextureManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanalp on 2016/10/09.
 */

public class ProfessorChatBox extends GameObject {

    private static final float TIME_BETWEEN_SWITCHES = 6.0f;
    private float timePassed;
    private List<String> talkingPoints;

    private static BitmapFont font = new BitmapFont(Gdx.files.internal("menu/small_text.fnt"));
    private GlyphLayout messageLabel;

    public ProfessorChatBox(float x, float y, int width, int height) {
        this(x, y, width, height, new TextureManager("menu/chat/chatbox.png", false));
    }

    public ProfessorChatBox(float x, float y, int width, int height, TextureManager textureManager) {
        super(x, y, width, height, textureManager);
        talkingPoints = new ArrayList<String>();
        timePassed = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("menu/chat/strings.txt"));
            String line;
            while ((line = br.readLine()) != null) talkingPoints.add(line.replace("\\n", "\n"));
            br.close();
        } catch (Exception e) {
            System.err.println("Error reading chat");
        }

        newTextLabel();
    }

    @Override
    public void update(float dt) {
        timePassed += dt;
        if (timePassed >= TIME_BETWEEN_SWITCHES) {
            timePassed = 0;
            newTextLabel();
        }
    }

    private void newTextLabel() {
        messageLabel = new GlyphLayout(font, talkingPoints.get((int) (Math.random() * talkingPoints.size())));
    }

    public void render(SpriteBatch sb) {
        super.render(sb);
        font.draw(sb, messageLabel, x + width / 2 - messageLabel.width / 2, y + height / 2 + messageLabel.height / 2 + 7);
    }
}
