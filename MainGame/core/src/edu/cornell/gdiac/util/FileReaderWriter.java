package edu.cornell.gdiac.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

/**
 * Created by Xu on 3/16/2017.
 *
 * The FileReaderWriterClass
 *  - reads the json files and converts them to game objects
 *  - writes to json files given game objects
 */
public class FileReaderWriter {
    /** Director of json files */
    public static final String JSON_DIRECTORY = "JSON";
    public static final String JSON_LEVELS = JSON_DIRECTORY+"/levelsDirectory.json";

    /**
     * loads all level data
     * populates platforms, walls, enemies, player, target, resources
     */
    public static String readJson(String JsonFile){
        try {
            FileHandle handler = Gdx.files.local(JsonFile);
            String content = handler.readString();
            return content;
        }
        catch(Exception e){
            System.out.println("Error: Json file not found.");
            return null;
        }

    }

    /**
     * Gets a list of all files in the JSON directory in the assets folder.
     * @return arraylist of all current files in the JSON directory
     */
    public static String[] getJsonFiles(){
        try{
            String content = FileReaderWriter.readJson(JSON_LEVELS);
            JsonReader reader = new JsonReader();
            JsonValue objects = reader.parse(content);

            String[] result = objects.get("levels").asStringArray();
            return result;
        }catch (Exception e){
            System.out.println("Error: Json directory not found");
            return new String[]{};
        }
    }

    /**
     * Gets a list of all files in the JSON directory in the assets folder.
     * @return arraylist of all current files in the JSON directory
     */
    public static void addJsonFile(String jsonFile){
        try{
            // get old file
            String content = FileReaderWriter.readJson(JSON_LEVELS);
            JsonReader reader = new JsonReader();
            JsonValue objects = reader.parse(content);
            String[] result = objects.get("levels").asStringArray();

            // write new file with old file stuff
            FileHandle fileHandle = new FileHandle(new File(JSON_LEVELS));
            JsonWriter writer = new JsonWriter(fileHandle.writer(false));
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            json.setWriter(writer);

            json.writeObjectStart();
            json.writeArrayStart("levels");
            for(String s:result){
                json.writeValue(s);
            }
            json.writeValue(jsonFile);
            json.writeArrayEnd();
            json.writeObjectEnd();

            writer.close();
        }catch (Exception e){
            System.out.println("Error: Cannot add jsonFile to levels directory");
        }
    }
}


