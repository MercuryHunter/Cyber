package com.uja.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.gameObjects.menuObjects.HoverableObject;
import com.uja.game.gameObjects.menuObjects.Laptop;
import com.uja.game.gameObjects.menuObjects.ProfessorChatBox;
import com.uja.game.gameObjects.menuObjects.SoundButton;
import com.uja.game.levels.Level;
import com.uja.game.loaders.SaveLoader;
import com.uja.game.loaders.StoryLoader;
import com.uja.game.objectUtilities.TextureManager;
import com.uja.game.states.overlays.MessageState;
import com.uja.game.states.overlays.SaveNameInputState;

import java.util.ArrayList;
import java.util.List;

/**
 * The MainMenuState is for the main menu - the initial screen displayed to the player.
 * From here you can navigate to other aspects of the game - saves, levels, etc.
 * Created by jonathanalp on 2016/08/13.
 */

public class MainMenuState extends State {

    public static final float DEFAULT_VOLUME = 0.1F;
    private Texture background;
    private HoverableObject exitDoor, cabinet;
    private SoundButton speakers;
    private List<Laptop> levels;
    private ProfessorChatBox chatBox;
    private int[][] positions = {{300, 0, 100, 100},
            {400, 35, 100, 100},
            {475, 70, 100, 100},
            {560, 105, 100, 100},
            {640, 140, 100, 100},
            {850, 0, 100, 100},
            {920, 50, 100, 100},
            {980, 105, 100, 100},
            {1010, 175, 100, 100}};

    private SaveLoader save;

    public MainMenuState(StateManager stateManager) {
        this(stateManager, SaveLoader.getLastSave());
    }

    public MainMenuState(StateManager stateManager, SaveLoader save) {
        super(stateManager);

        background = new Texture("menu/lecture-theatre-stairs.jpg");

        // Exit doors
        exitDoor = new HoverableObject(265, 205, 140, 190, new TextureManager(new String[]{"menu/exit-doors.png", "menu/exit-doors-hover.png"}, false), Gdx.audio.newSound(Gdx.files.internal("menu/sounds/door_squeak.wav")));

        // Load in the latest save
        cabinet = new HoverableObject(0, 100, 100, 200, new TextureManager(new String[]{"menu/cabinet-closed.png", "menu/cabinet-open.png"}, false), Gdx.audio.newSound(Gdx.files.internal("menu/sounds/cabinet_sound.wav")));
        this.save = save;

        // Music and Speaker Button for Sound
        Music music = Gdx.audio.newMusic(Gdx.files.internal("menu/SleepyMusic.mp3"));
        speakers = new SoundButton(725, 430, 100, 100, new TextureManager(new String[]{"menu/sound.png", "menu/sound-hover.png", "menu/sound-empty.png"}, false), Gdx.audio.newSound(Gdx.files.internal("menu/sounds/speaker_sound.wav")), music);
        music.setLooping(true);
        music.play();

        // Load Laptops for Levels
        levels = new ArrayList<Laptop>();
        for (int i = 0; i < Level.NUM_LEVELS; i++) {
            int[] position = positions[i];
            levels.add(new Laptop(position[0], position[1], position[2], position[3], Gdx.audio.newSound(Gdx.files.internal("menu/sounds/laptop_sound.wav")), (i + 1) <= save.getCurrentLevel(), i + 1));
        }

        // Professor says stuff
        chatBox = new ProfessorChatBox(90, 275, 150, 120);
    }

    @Override
    protected void handleInput() {
        //stateManager.set(new GameState(stateManager));
        if (exitDoor.retrieveClick())
            Gdx.app.exit();
        if (cabinet.retrieveClick())
            stateManager.set(new SaveChooserState(stateManager));
        for (Laptop level : levels)
            if (level.retrieveClick())
                stateManager.set(new GameState(stateManager, level.getLevelNumber(), save));
    }

    @Override
    public void update(float dt) {
        // If there's no save, get their name.
        if (save.getName().equals("Empty")) {
            stateManager.overlay(new SaveNameInputState(stateManager, save));
            return;
        }

        if (save.getCurrentLevel() == 0) {
            stateManager.overlay(new MessageState(stateManager, StoryLoader.getStory(0)));
            save.setCurrentLevel(1);
            save.save();
            stateManager.set(new MainMenuState(stateManager));
            return;
        }

        handleInput();
        exitDoor.update(dt);
        cabinet.update(dt);
        speakers.update(dt);
        for (Laptop level : levels) level.update(dt);

        chatBox.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        exitDoor.render(sb);
        cabinet.render(sb);
        speakers.render(sb);
        for (Laptop level : levels) level.render(sb);
        chatBox.render(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        exitDoor.dispose();
        cabinet.dispose();
        speakers.dispose();
        chatBox.dispose();
        for (Laptop level : levels) level.dispose();
    }
}
