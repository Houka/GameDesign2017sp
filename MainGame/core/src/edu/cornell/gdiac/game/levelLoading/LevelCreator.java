package edu.cornell.gdiac.game.levelLoading;
import com.badlogic.gdx.files.FileHandle;
import edu.cornell.gdiac.game.entity.models.*;
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

    /** Fields and constants for the default level. For testing purposes. */
    private static final String DEFAULT_FILE = "JSON/default.json";
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
    private ArrayList<PlatformModel> defaultPlatforms;
    private ArrayList<WallModel> defaultWalls;
    private PlayerModel defaultPlayer;
    private ArrayList<EnemyModel> defaultOnSightEnemies;
    private ArrayList<EnemyModel> defaultIntervalEnemies;
    private ArrayList<AmmoDepotModel> defaultAmmoDepots;
    private GoalModel defaultTarget;
    private int defaultAmmo;

    /**
     * Fills in defaults (for testing purposes)
     */
    private void setDefaults(){
        defaultPlatforms = new ArrayList<PlatformModel>();
        defaultWalls = new ArrayList<WallModel>();
        defaultOnSightEnemies = new ArrayList<EnemyModel>();
        defaultIntervalEnemies = new ArrayList<EnemyModel>();
        defaultAmmoDepots = new ArrayList<AmmoDepotModel>();


        for (int i = 0; i < DEFAULT_PLATFORMS.length; i++)
            defaultPlatforms.add(new PlatformModel(DEFAULT_PLATFORMS[i], 0));

        for (int i = 0; i < DEFAULT_WALLS.length; i++)
            defaultWalls.add(new WallModel(DEFAULT_WALLS[i]));

        defaultPlayer = new PlayerModel(2.5f, 5.0f,38 ,95 );
        defaultOnSightEnemies.add(new EnemyModel(5.5f, 10f, 1, 1, true, true, 0));
        defaultIntervalEnemies.add(new EnemyModel(3.5f, 8f, 1, 1, true, false, 200));
        defaultAmmoDepots.add(new AmmoDepotModel(5.5f, 4f, 1, 1, 3));
        defaultTarget = new GoalModel(29.5f, 15.0f, 1, 1);
        defaultAmmo = 4;
    }

    /**
     * Writes the level given model objects
     */
    public void writeLevel(String JsonFile, ArrayList<PlatformModel> platforms, ArrayList<WallModel> walls,
                           PlayerModel player, ArrayList<EnemyModel> intervalEnemies, ArrayList<EnemyModel> onSightEnemies,
                           ArrayList<AmmoDepotModel> ammoDepots, GoalModel target, int ammo) {
        FileHandle f = new FileHandle(new File(JsonFile));
        JsonWriter writer = new JsonWriter(f.writer(false));
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setWriter(writer);

        json.writeObjectStart();

        //platforms
        json.writeObjectStart("platforms");
        json.writeArrayStart("default");
        for (int i = 0; i < platforms.size(); i ++) {
            if (platforms.get(i).getType() == 0)
                json.writeValue(platforms.get(i).getPoints(), FloatArray.class, Float.class);
        }
        json.writeArrayEnd();

        json.writeArrayStart("spikes");
        for (int i = 0; i < platforms.size(); i ++) {
            if (platforms.get(i).getType() == 1)
                json.writeValue(platforms.get(i).getPoints(), FloatArray.class, Float.class);
        }
        json.writeArrayEnd();

        json.writeObjectEnd();
        //walls
        json.writeObjectStart("walls");
        json.writeArrayStart("default");
        for (int i = 0; i < walls.size(); i++) {
            json.writeValue( walls.get(i).getPoints(), FloatArray.class, Float.class);
        }
        json.writeArrayEnd();
        json.writeObjectEnd();

        //player
        json.writeObjectStart("player");
        json.writeValue("x", player.getX());
        json.writeValue("y", player.getY());
        json.writeObjectEnd();;

        //enemies
        json.writeObjectStart("enemies");
        json.writeArrayStart("interval");
        for (int i = 0; i < intervalEnemies.size(); i++){
            json.writeObjectStart();
            json.writeValue("x", intervalEnemies.get(i).getX());
            json.writeValue("y", intervalEnemies.get(i).getY());
            json.writeValue("isFacingRight", intervalEnemies.get(i).isFacingRight());
            json.writeValue("interval", intervalEnemies.get(i).getInterval());
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        json.writeArrayStart("on_sight");
        for (int i = 0; i < onSightEnemies.size(); i++){
            json.writeObjectStart();
            json.writeValue("x", onSightEnemies.get(i).getX());
            json.writeValue("y", onSightEnemies.get(i).getY());
            json.writeValue("isFacingRight", onSightEnemies.get(i).isFacingRight());
            json.writeValue("interval", onSightEnemies.get(i).getInterval());
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        json.writeObjectEnd();

        //resources
        json.writeObjectStart("resources");
        json.writeArrayStart("ammo_depots");
        for (int i = 0; i < ammoDepots.size(); i ++) {
            json.writeObjectStart();
            json.writeValue("x", ammoDepots.get(i).getX());
            json.writeValue("y", ammoDepots.get(i).getY());
            json.writeValue("amount", ammoDepots.get(i).getAmmoAmount());
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
        json.writeObjectEnd();

        //target
        json.writeObjectStart("target");
        json.writeValue("x", target.getX());
        json.writeValue("y", target.getY());
        json.writeObjectEnd();;

        //ammo
        json.writeValue("starting ammo", ammo);

        json.writeObjectEnd();

        try{
            writer.close();
        }
        catch (Exception e){
                System.out.println("Error: Failed to close writer");
        }
    }

    /* this function is for demo purposes (technical prototype)**/
    public void writeDemoJson(){
        setDefaults();
        writeLevel(DEFAULT_FILE, defaultPlatforms, defaultWalls, defaultPlayer, defaultIntervalEnemies,
                defaultOnSightEnemies, defaultAmmoDepots, defaultTarget, defaultAmmo);
    }
}
