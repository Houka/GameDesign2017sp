package edu.cornell.gdiac.game.levelLoading;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import java.util.ArrayList;
import java.io.*;


/**
 * Created by Alex on 3/18/2017.
 *
 * The LevelCreator class
 *  - Takes objects and level info and writes to json
 */
public class LevelCreator {

    private static final String DEFAULT_FILE = "JSON/default.json";

    /** The outlines of all of the platforms */
    private static final float[][] DEFAULT_PLATFORMS = {
            // (x top left, y top left, x top right, y top right, x bottom right, y bottom left, x bottom left, y bottom right)

            { 1.0f, 1.0f, 6.0f, 1.0f, 6.0f, 0.0f, 1.0f, 0.0f}, // starting platform
            {23.0f, 0.0f,31.0f, 0.0f,31.0f, 1.0f,23.0f, 1.0f}, // right of bridge

            {0.0f, 5.5f,4.0f, 5.5f,4.0f, 5.0f,0.0f, 5.0f}, // 6th platform on the right
            {0.0f, 9.0f,7.0f, 9.0f,7.0f, 8.5f,0.0f, 8.5f}, // 5th platform on the right
            { 1.0f,12.5f, 10.0f,12.5f, 10.0f,12.0f, 1.0f,12.0f} // finish platform
    };

    private static final float[][] DEFAULT_WALLS = {
            {16.0f, 18.0f, 16.0f, 17.0f,  1.0f, 17.0f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f, 18.0f},
            {32.0f, 18.0f, 32.0f,  0.0f, 31.0f,  0.0f, 31.0f, 17.0f, 16.0f, 17.0f, 16.0f, 18.0f}
    };

    private static final float[] DEFAULT_PLAYER = {2.5f, 5.0f};

    private static final float[][] DEFAULT_ENEMIES = {
            {0f, 3.5f, 8.0f, 1.0f, 200f},
            {1f, 5.5f, 10f, 1.0f, 0f}
    };

    private static final float[][] DEFAULT_RESOURCES = {
            {0f, 5.5f, 4.0f}
    };

    private static final float[] DEFAULT_TARGET = {29.5f, 15.0f};

    private static final int DEFAULT_AMMO = 4;

    private static final String DEFAULT_NEXT = "NONE";



    public void writeLevel(String JsonFile, float[][] platforms, float[][] walls, float[] player,
                            float[][] enemies, float[][] resources, float[] target, int ammo, String nextLevel) {
        FileHandle f = new FileHandle(new File(JsonFile));
        JsonWriter writer = new JsonWriter(f.writer(false));
        Json json = new Json();

        json.setOutputType(JsonWriter.OutputType.json);
        json.setWriter(writer);

        json.writeObjectStart();
        json.writeObjectStart("objects");

        //platforms
        json.writeArrayStart("platforms");
        for (int i = 0; i < platforms.length; i ++) {
            json.writeObjectStart();
            json.writeValue("vertices", platforms[i], FloatArray.class, Float.class);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        //walls
        json.writeArrayStart("walls");
        for (int i = 0; i < walls.length; i++) {
            json.writeObjectStart();
            json.writeValue("vertices", walls[i], FloatArray.class, Float.class);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        //player
        json.writeObjectStart("player");
        if (player.length > 0) {
            json.writeValue("x", player[0]);
            json.writeValue("y", player[1]);
        }
        json.writeObjectEnd();;

        //enemies
        json.writeArrayStart("enemies");
        for (int i = 0; i < enemies.length; i ++) {
            json.writeObjectStart();
            float[] enemy = enemies[i];
            json.writeValue("type", enemy[0]);
            json.writeValue("x", enemy[1]);
            json.writeValue("y", enemy[2]);
            json.writeValue("isFacingRight", enemy[3]);
            json.writeValue("interval", enemy[4]);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        //resources
        json.writeArrayStart("resources");
        for (int i = 0; i < resources.length; i ++) {
            json.writeObjectStart();
            float[] resource = resources[i];
            json.writeValue("type", resource[0]);
            json.writeValue("x", resource[1]);
            json.writeValue("y", resource[2]);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        //target
        json.writeObjectStart("target");
        if (target.length > 0) {
            json.writeValue("x", target[0]);
            json.writeValue("y", target[1]);
        }
        json.writeObjectEnd();;

        //end of objects
        json.writeObjectEnd();

        //state variables
        json.writeObjectStart("state");
        json.writeValue("starting ammo", ammo);
        json.writeValue("next level", nextLevel);
        json.writeObjectEnd();

        json.writeObjectEnd();

        try{
            writer.close();
        }
        catch (Exception e){
                System.out.println("Failed to close writer");
        }
    }

    /* this function is for demo purposes (technical prototype)**/
    public void writeDemoJson(){
        writeLevel(DEFAULT_FILE, DEFAULT_PLATFORMS, DEFAULT_WALLS, DEFAULT_PLAYER, DEFAULT_ENEMIES,
                    DEFAULT_RESOURCES, DEFAULT_TARGET, DEFAULT_AMMO, DEFAULT_NEXT);
    }
}
