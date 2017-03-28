package edu.cornell.gdiac.game.levelLoading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.util.FileReaderWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lu on 2/27/2017.
 *
 * The Level Parser class
 *  - loads data of a level
 *  - parses data to a standard format for our game controllers
 *  - returns the information
 *
 * TODO: figure out what the values in WALLS and PLATFORMS represent
 * TODO: figure out how to make the medium level with this level format from WALLS and PLATFORMS
 * TODO: (extra) abstract the data out into a data file that can be loaded with this class with a string of the file name
 */
public class LevelParser {
    // Since these appear only once, we do not care about the magic numbers.
    // In an actual game, this information would go in a data file.
    // Wall vertices
    private JsonValue platforms;
    private JsonValue walls;
    private JsonValue player;
    private JsonValue enemies;
    private JsonValue resources;
    private JsonValue target;
    private int startingAmmo;


    private static final float[][] WALLS = {
            {16.0f, 18.0f, 16.0f, 17.0f,  1.0f, 17.0f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f, 18.0f},
            {32.0f, 18.0f, 32.0f,  0.0f, 31.0f,  0.0f, 31.0f, 17.0f, 16.0f, 17.0f, 16.0f, 18.0f}
    };

    /** The outlines of all of the platforms */
    private static final float[][] PLATFORMS = {
            // (x top left, y top left, x top right, y top right, x bottom right, y bottom left, x bottom left, y bottom right)

            { 1.0f, 1.0f, 6.0f, 1.0f, 6.0f, 0.0f, 1.0f, 0.0f}, // starting platform
            {23.0f, 0.0f,31.0f, 0.0f,31.0f, 1.0f,23.0f, 1.0f}, // right of bridge

            {0.0f, 5.5f,4.0f, 5.5f,4.0f, 5.0f,0.0f, 5.0f}, // 6th platform on the right
            {0.0f, 9.0f,7.0f, 9.0f,7.0f, 8.5f,0.0f, 8.5f}, // 5th platform on the right
            { 1.0f,12.5f, 10.0f,12.5f, 10.0f,12.0f, 1.0f,12.0f} // finish platform
    };

    /** Creates a level parser object*/
    public LevelParser(){}

    //* Loads a json level file into class variables
    public void loadLevel(String JsonFile){
        try {
            FileReaderWriter f = new FileReaderWriter();
            String content = f.readJson(JsonFile);
            JsonReader reader = new JsonReader();
            JsonValue objects = reader.parse(content);

            platforms = objects.get("platforms");
            walls = objects.get("walls");
            player = objects.get("player");
            enemies = objects.get("enemies");
            target = objects.get("target");
            resources = objects.get("resources");
            startingAmmo = objects.get("starting ammo").asInt();
        }
        catch (Exception e){
            Gdx.app.error("LevelParser", "Improper Json", new IllegalStateException());
        }
    }

    /** Returns the walls of a level. Currently we just have one level*/
    public JsonValue getWalls(){
        return walls;
    }

    /** Returns the platforms of a level. Currently we just have one level*/
    public JsonValue getPlatforms(){
        return platforms;
    }

    /** Returns the data for the enemies*/
    public JsonValue getEnemies() { return enemies; }

    /** Returns the resources */
    public JsonValue getResources() { return resources; }

    public JsonValue getTarget() { return target; }

    /** Returns the player location*/
    public JsonValue getPlayer() { return player; }

    /** Returns the starting ammo*/
    public int getStartingAmmo() { return startingAmmo; }

}
