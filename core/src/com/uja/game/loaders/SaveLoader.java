package com.uja.game.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * A SaveLoader represents a save that is loaded in from a file.
 * It is messy and needs refactoring.s
 * Created by jonathanalp on 2016/08/14.
 */

public class SaveLoader {
    // TODO: Refactor saving.
    // TODO: Save Upgrades/Items and Best Stats
    public static final int NUM_SAVES = 3;

    private String saveFile, playerName;
    private int currentLevel, favour;

    // New Save
    public SaveLoader(String playerName, int number) {
        this.saveFile = getSaveFileString(number);
        this.playerName = playerName;
        currentLevel = 1;
        favour = 0;
    }

    // Load Old Save
    public SaveLoader(String saveFile) {
        this.saveFile = saveFile;
        try {
            BufferedReader br = new BufferedReader(new FileReader(saveFile));
            playerName = br.readLine();
            currentLevel = Integer.parseInt(br.readLine());
            favour = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            System.err.println("Error reading puzzle file: " + saveFile);
            e.printStackTrace();
            //this("Empty", getSaveNumber(saveFile));
        }
    }

    public static SaveLoader getLastSave() {
        try {
            File lastSaveFile = new File("saves/lastSave.txt");
            if (!lastSaveFile.exists() || lastSaveFile.isDirectory())
                setLastSave(1);
            BufferedReader br = new BufferedReader(new FileReader("saves/lastSave.txt"));
            int saveNum = Integer.parseInt(br.readLine());
            if (!saveExists(saveNum)) return new SaveLoader("Empty", saveNum);
            br.close();
            return loadSave(saveNum);
        } catch (Exception e) {
            System.err.println("Error loading default save");
            e.printStackTrace();
        }
        return null;
    }

    public static void setLastSave(int number) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("saves/lastSave.txt"));
            pw.println(number);
            pw.close();
        } catch (Exception e) {
            System.err.println("Error saving last save's number to file");
            System.err.println("Number was: " + number);
            e.printStackTrace();
        }
    }

    public static void setLastSave(String saveFile) {
        int number = getSaveNumber(saveFile);
        //System.out.println("Setting last save to: " + number);
        setLastSave(number);
    }

    public static int getSaveNumber(String fileString) {
        return Integer.parseInt(fileString.substring(6, fileString.length() - 4));
    }

    public static String getSaveFileString(int number) {
        return "saves/" + number + ".sav";
    }

    public static boolean saveExists(int number) {
        File file = new File(getSaveFileString(number));
        return file.exists() && !file.isDirectory();
    }

    public static SaveLoader loadSave(int number) {
        return new SaveLoader(getSaveFileString(number));
    }

    public String getName() {
        return playerName;
    }

    public void setName(String playerName) {
        this.playerName = playerName;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getFavour() {
        return favour;
    }

    public void setFavour(int favour) {
        this.favour = favour;
    }

    public String getSaveFile() {
        return saveFile;
    }

    public boolean completeLevel(int levelNumber) {
        if (levelNumber >= currentLevel) {
            currentLevel = levelNumber + 1;
            return true;
        }
        return false;
    }

    public void save() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(saveFile, false));
            pw.println(playerName);
            pw.println(currentLevel);
            pw.println(favour);
            pw.close();
        } catch (Exception e) {
            System.err.println("Error saving to file: " + saveFile);
            e.printStackTrace();
        }
    }

    // Deletes save and returns the save it was set to. -1 if no other saves exist.
    public int deleteSave() {
        File file = new File(saveFile);
        file.delete();
        for (int i = 1; i <= NUM_SAVES; i++) {
            if (saveExists(i)) {
                setLastSave(i);
                return i;
            }
        }
        setLastSave(1);
        return -1;
    }
}
