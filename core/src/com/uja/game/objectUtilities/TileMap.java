package com.uja.game.objectUtilities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uja.game.gameObjects.puzzleObjects.characters.Entity;
import com.uja.game.gameObjects.puzzleObjects.unmoving.Tile;

/**
 * A TileMap is a set of tiles that can be rendered and asked about tile collisions
 * Created by jonathanalp on 2016/09/08.
 */

public class TileMap {

    private Tile[][] tilemap;
    private boolean[][] valid;
    private int tileWidth, tileHeight;

    public TileMap(Tile[][] tilemap, boolean[][] valid, int tileWidth, int tileHeight) {
        this.tilemap = tilemap;
        this.valid = valid;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public boolean tileCollides(Entity entity, float newX, float newY) {
        // Determine affected tiles
        int x1 = (int) Math.floor(Math.min(entity.getX(), newX) / tileWidth);
        int y1 = (int) Math.floor(Math.min(entity.getY(), newY) / tileHeight);
        int x2 = (int) Math.floor((Math.max(entity.getX(), newX) + entity.getWidth() - 0.1f) / tileWidth);
        int y2 = (int) Math.floor((Math.max(entity.getY(), newY) + entity.getHeight() - 0.1f) / tileHeight);

        // Out of bounds checks
        if (x1 < 0 || x2 >= tilemap[0].length) return true;
        if (y1 < 0 || y2 >= tilemap.length) return true;

        // Tile checks
        for (int y = y1; y <= y2; y++)
            for (int x = x1; x <= x2; x++)
                if (!valid[y][x])
                    return true;

        return false;
    }

    public boolean validTile(int y, int x) {
        if (x < 0 || x >= tilemap[0].length) return false;
        if (y < 0 || y >= tilemap.length) return false;
        return valid[y][x];
    }

    public Integer[] positionToTileCoordinates(float x, float y) {
        int xBlock = (int) (x / tileWidth);
        int yBlock = (int) (y / tileHeight);
        return new Integer[]{xBlock, yBlock};
    }

    public boolean fullyEnclosedInTile(Entity entity) {
        int x1 = (int) Math.floor(entity.getX() / tileWidth);
        int y1 = (int) Math.floor(entity.getY() / tileHeight);
        int x2 = (int) Math.floor((entity.getX() + entity.getWidth() - 0.1f) / tileWidth);
        int y2 = (int) Math.floor((entity.getY() + entity.getHeight() - 0.1f) / tileHeight);

        return x1 == x2 && y1 == y2;
    }

    public float convertToMidpointX(int x) {
        return (float) (x * tileWidth + 0.5 * tileWidth);
    }

    public float convertToMidpointY(int y) {
        return (float) (y * tileHeight + 0.5 * tileHeight);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public boolean tileCollides(Entity entity) {
        return tileCollides(entity, entity.newX(), entity.newY());
    }

    public void render(SpriteBatch sb) {
        for (Tile[] row : tilemap)
            for (Tile tile : row)
                tile.render(sb);
    }
}
