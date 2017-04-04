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
 */
public class LevelParser {
    // Variables to store information of the loaded json file.
    private JsonValue platforms;
    private JsonValue walls;
    private JsonValue player;
    private JsonValue enemies;
    private JsonValue resources;
    private JsonValue target;
    private int startingAmmo;


    /** Creates a level parser object*/
    public LevelParser(){}

    //* Loads a json level file into class variables
    public void loadLevel(String JsonFile){
        try {
            FileReaderWriter f = new FileReaderWriter();
            String content = f.readJson(JsonFile);
            System.out.println(content);
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
