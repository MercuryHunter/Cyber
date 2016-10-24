package com.uja.game.levels;

/**
 * A Level represents a grouping of challenges - A Puzzle, Minigame and Spotquiz.
 * It runs each one and switches state between them according to the level.
 * If the level hasn't been made, it returns and displays a message.
 * Created by jonathanalp on 2016/08/14.
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.levels.Challenge.ChallengeType;
import com.uja.game.loaders.ChallengeLoader;

import java.io.File;

public class Level {

    public static final int NUM_LEVELS = 9;

    public static final float DEFAULT_VOLUME = 0.1F;
    private static final float TIME_DEAD_BEFORE_RESET = 1.0F, TIME_WAITING_STATE_SWITCH = 1.5F;
    private Puzzle puzzle;
    private Minigame minigame;
    private Spotquiz spotquiz;
    private boolean hasSpotquiz;
    private String puzzleFile, minigameFile, spotquizFile;
    private ChallengeType currentState;
    private float time_waited_for_reset, time_waited_after_won;

    private boolean finished; // Stores whether the current level is over and we need to go to the next one
    private boolean finishedUnwillingly, showLevelNotMadeMessage;

    public Level(int levelNumber) {
        this(levelString(ChallengeType.Puzzle, levelNumber),
                levelString(ChallengeType.Minigame, levelNumber),
                levelString(ChallengeType.Spotquiz, levelNumber));
    }

    public Level(String puzzleFile, String minigameFile, String spotquizFile) {
        this.puzzleFile = puzzleFile;
        this.minigameFile = minigameFile;
        this.spotquizFile = spotquizFile;

        finished = false;
        finishedUnwillingly = false;
        showLevelNotMadeMessage = false;

        time_waited_for_reset = 0;
        time_waited_after_won = 0;

        loadInPuzzle(0);

        minigame = new Minigame(minigameFile);

        File sqFile = new File(spotquizFile);
        if (sqFile.exists() && !sqFile.isDirectory()) {
            hasSpotquiz = true;
            spotquiz = new Spotquiz(spotquizFile);
        } else {
            hasSpotquiz = false;
            spotquiz = null;
        }

        currentState = ChallengeType.Puzzle;
    }

    public static String levelString(ChallengeType type, int levelNumber) {
        switch (type) {
            case Puzzle:
                return "levels/puzzle" + levelNumber + ".lvl";
            case Minigame:
                return "levels/minigame" + levelNumber + ".lvl";
            default:
                return "levels/spotquiz" + levelNumber + ".lvl";
        }
    }

    private void loadInPuzzle(float timePassed) {
        try {
            puzzle = new Puzzle(puzzleFile, timePassed);
        } catch (ChallengeLoader.LevelNotMadeException e) {
            puzzle = null;
            finishedUnwillingly = true;
            showLevelNotMadeMessage = true;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isFinishedUnwillingly() {
        return finishedUnwillingly;
    }

    public boolean shouldShowLevelNotMadeMessage() {
        // Gets and resets so only one message displays
        if (showLevelNotMadeMessage) {
            showLevelNotMadeMessage = false;
            return true;
        }
        return false;
    }

    public void update(float dt) {
        switch (currentState) {
            case Puzzle:
                if (puzzle == null) return;
                puzzle.update(dt);
                if (puzzle.needsReset()) {
                    // TODO: Show level is resetting
                    // TODO: Better resets - store initial state or something.
                    time_waited_for_reset += dt;
                    if (time_waited_for_reset > TIME_DEAD_BEFORE_RESET) {
                        time_waited_for_reset = 0;
                        loadInPuzzle(puzzle.getTimePassed());
                    }
                } else if (puzzle.won()) {
                    // TODO: Scoring, showing won, etc
                    // System.out.println("Finished in: " + puzzle.getTimeWon());
                    time_waited_after_won += dt;
                    if (time_waited_after_won > TIME_WAITING_STATE_SWITCH) {
                        time_waited_after_won = 0;
                        currentState = ChallengeType.Minigame;
                        // TODO: Change to minigame state without finishing.
                        finished = true;
                    }
                }
                break;
            case Minigame:
                minigame.update(dt);
                break;
            case Spotquiz:
                spotquiz.update(dt);
                break;
        }
    }

    public void render(SpriteBatch sb) {
        switch (currentState) {
            case Puzzle:
                if (puzzle != null) puzzle.render(sb);
                break;
            case Minigame:
                minigame.render(sb);
                break;
            case Spotquiz:
                spotquiz.render(sb);
                break;
        }
    }

    public void dispose() {
        if (puzzle != null) puzzle.dispose();
        minigame.dispose();
        if (hasSpotquiz) spotquiz.dispose();
    }

}
