package com.uja.game.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.MainGame;
import com.uja.game.gameObjects.menuObjects.HoverableObject;
import com.uja.game.gameObjects.menuObjects.SaveBoard;
import com.uja.game.loaders.SaveLoader;
import com.uja.game.objectUtilities.TextureManager;
import com.uja.game.states.overlays.MessageState;
import com.uja.game.states.overlays.SaveNameInputState;

/**
 * The SaveChooserState presents SaveBoards for use with changing save.
 * There is also a set of accompanying delete buttons.
 * Created by jonathanalp on 2016/09/05.
 */

public class SaveChooserState extends State {

    private Texture background;
    private HoverableObject backButton;

    private HoverableObject[] deleteButtons;
    private SaveBoard[] saves;

    protected SaveChooserState(StateManager stateManager) {
        super(stateManager);

        background = new Texture("saves/images/library.jpg");
        backButton = new HoverableObject(1050, 15, 200, 74, new TextureManager(new String[]{"saves/images/back_button.png", "saves/images/back_button_hover.png"}, false), null);

        saves = new SaveBoard[SaveLoader.NUM_SAVES];
        deleteButtons = new HoverableObject[saves.length];
        for (int i = 0; i < saves.length; i++) {
            SaveLoader forButton;
            if (!SaveLoader.saveExists(i + 1))
                forButton = new SaveLoader("Empty", i + 1);
            else forButton = SaveLoader.loadSave(i + 1);
            saves[i] = new SaveBoard(490, i * 220 + 100, forButton);
            deleteButtons[i] = new HoverableObject(800, i * 220 + 170, 50, 50, new TextureManager(new String[]{"saves/images/bin.png", "saves/images/bin-hover.png"}, false), null);
        }
    }

    @Override
    protected void handleInput() {
        if (backButton.retrieveClick())
            stateManager.set(new MainMenuState(stateManager));

        for (SaveBoard saveBoard : saves) {
            saveBoard.updateLabel();
            if (saveBoard.retrieveClick()) {
                SaveLoader save = saveBoard.getSave();
                if (!save.getName().equals("Empty")) {
                    SaveLoader.setLastSave(save.getSaveFile());
                    stateManager.overlay(new MessageState(stateManager, "Save Changed"));
                } else {
                    stateManager.overlay(new SaveNameInputState(stateManager, save));

                }
            }
        }

        for (int i = 0; i < deleteButtons.length; i++) {
            if (deleteButtons[i].retrieveClick()) {
                int newSave = saves[i].getSave().deleteSave();
                if (newSave == -1)
                    stateManager.overlay(new MessageState(stateManager, "Save Deleted"));
                else
                    stateManager.overlay(new MessageState(stateManager, "Save Deleted\nSet to save number: " + newSave));
                stateManager.set(new SaveChooserState(stateManager));
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        backButton.update(dt);
        for (SaveBoard saveBoard : saves) saveBoard.update(dt);
        for (HoverableObject deleteButton : deleteButtons) deleteButton.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, MainGame.WIDTH, MainGame.HEIGHT);
        backButton.render(sb);
        for (SaveBoard saveBoard : saves) saveBoard.render(sb);
        for (HoverableObject deleteButton : deleteButtons) deleteButton.render(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        backButton.dispose();
        for (SaveBoard saveBoard : saves) saveBoard.dispose();
        for (HoverableObject deleteButton : deleteButtons) deleteButton.dispose();
    }
}
