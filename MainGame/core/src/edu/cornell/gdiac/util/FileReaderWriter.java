package edu.cornell.gdiac.util;

import java.util.Scanner;
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



    /**
     * loads all level data
     * populates platforms, walls, enemies, player, target, resources
     */
    public String readJson(String JsonFile){
        try {
            System.out.println(JsonFile);
            String content = new Scanner(new File(JsonFile)).useDelimiter("\\Z").next();
            return content;
        }
        catch(FileNotFoundException e){
            System.out.println("Json file not found.");
            return null;
        }

    }


}


