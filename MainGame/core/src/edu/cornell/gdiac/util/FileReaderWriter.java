package edu.cornell.gdiac.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

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

    /**
     * loads all level data
     * populates platforms, walls, enemies, player, target, resources
     */
    public String readJson(String JsonFile){
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
    public static ArrayList<String> getJsonFiles(){
        return getJsonFiles(new ArrayList<String>(), Gdx.files.local(JSON_DIRECTORY).file());
    }

    /**
     * Gets a list of all files in the JSON directory in the assets folder.
     * @return Pretty formated string of the list of all current files in the JSON directory
     */
    public static String getJsonFilesString(){
        String result = "";
        for(Object s:getJsonFiles(new ArrayList<String>(), Gdx.files.local(JSON_DIRECTORY).file()).toArray()){
            result+="        "+s+"\n";
        }
        return result+"\n";
    }

    private static ArrayList<String> getJsonFiles(ArrayList<String> list, File directory)
    {
        for(File file: directory.listFiles()){
            if (file.isDirectory())
            {
                getJsonFiles(list, file);
            }
            String path = file.getPath();
            list.add(path.substring(path.indexOf(JSON_DIRECTORY),path.length()));
        }

        return list;
    }

}


