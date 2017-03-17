package edu.cornell.gdiac.util;

import com.badlogic.gdx.utils.*;
import java.util.Scanner;
import java.util.*;
import java.io.*;

/**
 * Created by Xu on 3/16/2017.
 *
 * The FileReaderWriterClass
 *  - reads the json files and converts them to game objects
 *  - writes to json files given game objects
 */

//
public class FileReaderWriter {

    private float[][] platforms;
    private float[][] walls;
    private float[][] enemies;
    private float[] player;
    private float[] target;
    private float[][] resources;

    /**
     * loads all level data
     * populates platforms, walls, enemies, player, target, resources
     */
    public void loadLevel(String JSON){
        try {
            String content = new Scanner(new File(JSON)).useDelimiter("\\Z").next();
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

            //populate each array
        }
        catch (FileNotFoundException e){
            System.out.println("FILE NOT FOUND");
        }
    }

}


