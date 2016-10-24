package com.uja.game.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.gameObjects.puzzleObjects.characters.Guard;
import com.uja.game.gameObjects.puzzleObjects.triggering.FloorButton;
import com.uja.game.gameObjects.puzzleObjects.triggering.LaserShooter;
import com.uja.game.gameObjects.puzzleObjects.unmoving.Door;
import com.uja.game.gameObjects.puzzleObjects.unmoving.EndingBook;
import com.uja.game.gameObjects.puzzleObjects.unmoving.InvisibleWall;
import com.uja.game.gameObjects.puzzleObjects.unmoving.Tile;
import com.uja.game.levels.Challenge.ChallengeType;
import com.uja.game.objectUtilities.TextureManager;
import com.uja.game.objectUtilities.TileMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A ChallengeLoader is used by the challenge to load in the appropriate objects.
 * For each challenge, see the appropriate layout file to understand what it loads in.
 * Created by jonathanalp on 2016/08/14.
 */

public class ChallengeLoader {

    private Vector2 playerPosition;
    private TileMap tilemap;
    private GameObject[] untouchableObjects, touchableObjects;
    private Guard[] guards;

    // Loads in the game objects to create a level.
    public ChallengeLoader(String challengeFile, ChallengeType type) throws LevelNotMadeException {
        switch (type) {
            case Puzzle:
                loadPuzzle(challengeFile);
                break;
            case Minigame:
                loadMinigame(challengeFile);
                break;
            case Spotquiz:
                loadSpotquiz(challengeFile);
                break;
        }
    }

    // TODO: Load in spotquiz
    private void loadSpotquiz(String challengeFile) {

    }

    // TODO: Load in minigame
    private void loadMinigame(String challengeFile) {

    }

    private void loadPuzzle(String challengeFile) throws LevelNotMadeException {
        try {
            String[] line;

            BufferedReader br = new BufferedReader(new FileReader(challengeFile));

            Texture tilesheet = new Texture(Gdx.files.internal("levels/tilesheets/" + br.readLine()));

            line = br.readLine().split("#");
            playerPosition = new Vector2(convertCoordinate(line[0]), convertCoordinate(line[1]));

            line = br.readLine().split("#");
            int numTilesSheetX = Integer.parseInt(line[0]);
            int numTilesSheetY = Integer.parseInt(line[1]);

            int tileWidth = tilesheet.getWidth() / numTilesSheetX;
            int tileHeight = tilesheet.getHeight() / numTilesSheetY;
            TextureRegion[][] tmp = TextureRegion.split(tilesheet, tileWidth, tileHeight);

            line = br.readLine().split("#");
            int numTilesMapX = Integer.parseInt(line[0]);
            int numTilesMapY = Integer.parseInt(line[1]);

            String[] disallowedTiles = br.readLine().split("#");
            List<String> disallowedList = Arrays.asList(disallowedTiles);

            Tile[][] tiles = new Tile[numTilesMapY][numTilesMapX];
            boolean[][] valid = new boolean[numTilesMapY][numTilesMapX];
            for (int y = 0; y < numTilesMapY; y++) {
                line = br.readLine().split("#");
                for (int x = 0; x < numTilesMapX; x++) {
                    // Validity
                    valid[numTilesMapY - y - 1][x] = !disallowedList.contains(line[x]);

                    // Tile
                    int tile = Integer.parseInt(line[x]);
                    int yPos = Math.floorDiv(tile, numTilesSheetX);
                    int xPos = tile - yPos * numTilesSheetX;
                    //int yPos = Math.floorDiv(tile, numTilesSheetX) - (tile % numTilesSheetX == 0 ? 1 : 0);
                    //int xPos = tile - yPos*numTilesSheetX - 1;
                    tiles[numTilesMapY - y - 1][x] = new Tile(x * tileWidth, (numTilesMapY - y - 1) * tileHeight, tileWidth, tileHeight, new TextureManager(tmp[yPos][xPos], false));
                    //if(tile != 18) System.out.printf("New Tile at: x,y: %d,%d and width,height: %d,%d, position in sheet x,y: %d,%d\n", x * tileWidth, (numTilesMapY - y - 1) * tileHeight, tileWidth, tileHeight, xPos, yPos);
                }
            }

            tilemap = new TileMap(tiles, valid, tileWidth, tileHeight);

            int numUntouchableObjects = Integer.parseInt(br.readLine());
            untouchableObjects = new GameObject[numUntouchableObjects];
            for (int i = 0; i < numUntouchableObjects; i++) {
                line = br.readLine().split("#");
                if (line[0].equals("Door")) {
                    float x = convertCoordinate(line[1]);
                    float y = convertCoordinate(line[2]);
                    boolean vertical = Boolean.parseBoolean(line[3]);
                    List<Integer> ids = splitIDs(line[4]);
                    untouchableObjects[i] = new Door(x, y, vertical, ids);
                    if (line.length == 6) ((Door) untouchableObjects[i]).switchState();
                }
                if (line[0].equals("InvisibleWall")) {
                    float x1 = convertCoordinate(line[1]);
                    float y1 = convertCoordinate(line[2]);
                    float x2 = convertCoordinate(line[3]);
                    float y2 = convertCoordinate(line[4]);
                    untouchableObjects[i] = new InvisibleWall(x1, y1, x2, y2);
                }
            }

            int numTouchableObjects = Integer.parseInt(br.readLine());
            touchableObjects = new GameObject[numTouchableObjects];
            for (int i = 0; i < numTouchableObjects; i++) {
                line = br.readLine().split("#");
                if (line[0].equals("LaserShooter")) {
                    float x1 = convertCoordinate(line[1]);
                    float y1 = convertCoordinate(line[2]);
                    float x2 = convertCoordinate(line[3]);
                    float y2 = convertCoordinate(line[4]);
                    List<Integer> ids = splitIDs(line[5]);
                    String moveDirection = line[6];
                    float amountToMove = convertCoordinate(line[7]);
                    float speed = Float.parseFloat(line[8]);
                    touchableObjects[i] = new LaserShooter(x1, y1, x2, y2, ids, moveDirection, amountToMove, speed);
                    if (line.length == 10) ((LaserShooter) touchableObjects[i]).trigger(ids.get(0));
                }
                if (line[0].equals("FloorButton")) {
                    float x = convertCoordinate(line[1]);
                    float y = convertCoordinate(line[2]);
                    int affectedID = Integer.parseInt(line[3]);
                    touchableObjects[i] = new FloorButton(x, y, affectedID, line.length == 5);
                }
                if (line[0].equals("EndingBook")) {
                    float x = convertCoordinate(line[1]);
                    float y = convertCoordinate(line[2]);
                    touchableObjects[i] = new EndingBook(x, y);
                }
            }

            int numGuards = Integer.parseInt(br.readLine());
            guards = new Guard[numGuards];
            for (int i = 0; i < numGuards; i++) {
                line = br.readLine().split("#");
                if (line[0].equals("Guard")) {
                    float x = convertCoordinate(line[1]);
                    float y = convertCoordinate(line[2]);
                    float speed = Float.parseFloat(line[3]);
                    Guard.Algorithm algorithm = Guard.convertStringToAlgorithm(line[4]);
                    List<Integer> ids = splitIDs(line[5]);
                    guards[i] = new Guard(x, y, speed, algorithm, ids, tilemap);
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            throw new LevelNotMadeException();
        } catch (IOException e) {
            System.err.println("Error reading file: " + challengeFile);
            e.printStackTrace();
        }
    }

    // Convert between block and screen coordinates
    private float convertCoordinate(String coord) {
        // If it ends with X, it's in block coordinates
        if (coord.endsWith("X"))
            return Float.parseFloat(coord.replace("X", "")) * 32;
        // Else it's in normal coordinates.
        return Float.parseFloat(coord);
    }

    private List<Integer> splitIDs(String ids) {
        String[] split = ids.split(",");
        List<Integer> returnIDs = new ArrayList<Integer>();
        for (String id : split)
            returnIDs.add(Integer.parseInt(id));
        return returnIDs;
    }

    public Vector2 getPlayerPosition() {
        return playerPosition;
    }

    public TileMap getTileMap() {
        return tilemap;
    }

    public GameObject[] getUntouchableGameObjects() {
        return untouchableObjects;
    }

    public GameObject[] getTouchableGameObjects() {
        return touchableObjects;
    }

    public Guard[] getGuards() {
        return guards;
    }

    public static class LevelNotMadeException extends Exception {
    }
}
