package edu.cornell.gdiac.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

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


}


