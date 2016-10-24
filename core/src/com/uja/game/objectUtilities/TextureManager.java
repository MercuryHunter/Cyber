package com.uja.game.objectUtilities;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Class to handle textures and provide bitmasks for the frames.
 * There can be animations by switching textures appropriately.
 * Created by jonathanalp on 2016/09/05.
 */

public class TextureManager {
    // TODO: Static cache of bitmasks to copy with related string files?!

    private TextureRegion[] textureRegions;
    private int[][][] bitMasks;
    private int index;

    public TextureManager(String textureFile, boolean needsMask) {
        this(new TextureRegion(new Texture(textureFile)), needsMask);
    }

    public TextureManager(TextureRegion textureRegion, boolean needsMask) {
        this(new TextureRegion[]{textureRegion}, needsMask);
    }

    public TextureManager(String[] textureRegionStrings, boolean needsMask) {
        this(textureRegionsFromStrings(textureRegionStrings), needsMask);
    }

    public TextureManager(TextureRegion[] textureRegions, boolean needsMask) {
        this.textureRegions = textureRegions;

        if (needsMask) initializeBitMasks();
        else bitMasks = null;

        index = 0;
    }

    // Helper method to convert array of strings to array of textureRegions
    private static TextureRegion[] textureRegionsFromStrings(String[] textureRegionStrings) {
        TextureRegion[] textureRegions = new TextureRegion[textureRegionStrings.length];
        for (int i = 0; i < textureRegionStrings.length; i++)
            textureRegions[i] = new TextureRegion(new Texture(textureRegionStrings[i]));
        return textureRegions;
    }

    // For animations and hoverable objects they need to switch between textures
    public void switchTexture() {
        index = (index + 1) % textureRegions.length;
    }

    public void switchTexture(int index) {
        this.index = index % textureRegions.length;
    }

    // Initialise a bitmask for every region.
    private void initializeBitMasks() {
        bitMasks = new int[textureRegions.length][][];

        for (int t = 0; t < textureRegions.length; t++) {
            TextureRegion textureRegion = textureRegions[t];

            Texture texture = textureRegion.getTexture();
            if (!texture.getTextureData().isPrepared()) {
                texture.getTextureData().prepare();
            }
            Pixmap pixmap = texture.getTextureData().consumePixmap();

            int heightPixmap = pixmap.getHeight();
            int widthPixmap = pixmap.getWidth();

            int[][] currentBitMask = new int[heightPixmap][widthPixmap];
            for (int y = 0; y < heightPixmap; y++) {
                for (int x = 0; x < widthPixmap; x++) {
                    currentBitMask[heightPixmap - 1 - y][x] = (Math.abs(pixmap.getPixel(x, y) % 16)) > 0 ? 1 : 0;
                }
            }
            bitMasks[t] = currentBitMask;
        }
    }

    public int getIndex() {
        return index;
    }

    public int[][] getCurrentBitMask() {
        return bitMasks[index];
    }

    public Texture getCurrentTexture() {
        return textureRegions[index].getTexture();
    }

    public TextureRegion getCurrentTextureRegion() {
        return textureRegions[index];
    }
}
