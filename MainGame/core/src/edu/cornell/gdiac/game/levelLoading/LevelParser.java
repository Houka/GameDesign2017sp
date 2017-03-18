package edu.cornell.gdiac.game.levelLoading;

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
    private float[][] platforms;
    private float[][] walls;
    private float[][] enemies;
    private float[] player;
    private float[] target;
    private float[][] resources;

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
            JsonValue objects = reader.parse(content).get("objects");

            //load player info
            JsonValue playerJson = objects.get("player");
            player = new float[]{playerJson.get("x").asFloat(), playerJson.get("y").asFloat()};

            //load enemy info
            JsonValue enemyJson = objects.get("enemies");
            ArrayList<float[]> enemyList = new ArrayList<float[]>();
            JsonValue.JsonIterator iter = enemyJson.iterator();
            JsonValue tempJson;
            float[] tempArray;
            while (iter.hasNext()){
                tempJson = iter.next();
                tempArray = new float[] {tempJson.get("x").asFloat(), tempJson.get("y").asFloat(),
                        tempJson.get("isFacingRight").asFloat()};
                enemyList.add(tempArray);
            }
            float[][] newEnemiesArray = new float[enemyList.size()][3];
            for (int i = 0; i < enemyList.size(); i++)
                newEnemiesArray[i] = enemyList.get(i);

            enemies = newEnemiesArray;


            //load target info
            JsonValue targetJson = objects.get("target");
            target = new float[]{playerJson.get("x").asFloat(), playerJson.get("y").asFloat()};

            //load resource info
            JsonValue resourceJson = objects.get("resources");
            ArrayList<float[]> resourceList = new ArrayList<float[]>();
            iter = resourceJson.iterator();
            while (iter.hasNext()){
                tempJson = iter.next();
                tempArray = new float[] {tempJson.get("type").asFloat(), tempJson.get("x").asFloat(),
                        tempJson.get("y").asFloat()};
                resourceList.add(tempArray);
            }
            float[][] newResourceArray = new float[resourceList.size()][3];
            for (int i = 0; i < resourceList.size(); i++)
                newResourceArray[i] = resourceList.get(i);

            resources = newResourceArray;

            //load platform info
            JsonValue platformJson = objects.get("platforms");
            ArrayList<float[]> platformList = new ArrayList<float[]>();
            iter = platformJson.iterator();
            while (iter.hasNext()){
                platformJson = iter.next();
                platformList.add(platformJson.get("vertices").asFloatArray());
            }
            float[][] newPlatformArray = new float[platformList.size()][];
            for (int i = 0; i < platformList.size(); i++)
                newPlatformArray[i] = platformList.get(i);
            System.out.println(newPlatformArray.length);
            System.out.println(newPlatformArray[0].length);
            platforms = newPlatformArray;

            //load wall info
            JsonValue wallJson = objects.get("walls");
            ArrayList<float[]> wallList = new ArrayList<float[]>();
            iter = wallJson.iterator();
            while (iter.hasNext()){
                wallJson = iter.next();
                wallList.add(wallJson.get("vertices").asFloatArray());
            }
            float[][] newWallArray = new float[wallList.size()][];
            for (int i = 0; i < wallList.size(); i++)
                newWallArray[i] = wallList.get(i);

            walls = newWallArray;
        }
        catch (Exception e){
            System.out.println("Improper Json");
        }
    }

    /** Returns the walls of a level. Currently we just have one level*/
    public float[][] getWalls(){
        return WALLS;
    }

    /** Returns the platforms of a level. Currently we just have one level*/
    public float[][] getPlatforms(){
        return PLATFORMS;
    }

    /** Returns the data for the enemies*/
    public float[][] getEnemies() { return enemies; }

    /** Returns the resources */
    public float[][] getResources() { return resources; }

    /** Returns the  for the enemies*/
    public float[] getTarget() { return target; }

    /** Returns the  for the enemies*/
    public float[] getPlayer() { return player; }
}
