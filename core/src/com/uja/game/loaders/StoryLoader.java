package com.uja.game.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * The StoryLoader retrieves the text of a particular story
 * Created by jonathanalp on 2016/10/04.
 */

public class StoryLoader {

    private String story;

    public StoryLoader(int number) {
        File file = new File(String.format("story/%d.txt", number));
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder storyText = new StringBuilder();
            while ((line = br.readLine()) != null) storyText.append(line + "\n");
            story = storyText.toString();
            br.close();
        } catch (Exception e) {
            System.err.println("Error reading story number: " + number);
        }
    }

    public String getStory() {
        return story;
    }

    public static String getStory(int number) {
        return (new StoryLoader(number)).getStory();
    }

}
