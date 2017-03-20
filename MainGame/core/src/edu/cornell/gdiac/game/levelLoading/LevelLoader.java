package edu.cornell.gdiac.game.levelLoading;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;
import edu.cornell.gdiac.util.obstacles.Obstacle;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;
import java.awt.*;

/**
 * Created by Lu on 3/16/2017.
 */
public class LevelLoader implements AssetUser, Disposable{
    // TODO: remove all this constant stuff once json works
    private static String PLATFORM_FILE = "sprites/fixtures/solid.png";
    private static String GOAL_FILE = "sprites/security_camera.png";
    private static String BACKGROUND_FILE = "sprites/wall/wall_texture.png";
    private static String ENEMY_FILE = "sprites/enemy/enemy_idle.png";
    private static String CHARACTER_FILE = "sprites/char/char_idle.png";
    private static String AMMO_DEPOT_FILE = "sprites/paint_repo.png";

    private TextureRegion platformTile;
    private TextureRegion goalTile;
    private TextureRegion bgTile;
    private TextureRegion enemyTexture;
    private TextureRegion playerTexture;
    private TextureRegion depotTexture;

    // Vars that we do need for this class in the end
    /**TODO:write desc*/
    private Rectangle bounds;
    /** Queue for adding objects */
    private PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
    /**TODO:write desc*/
    private LevelParser levelParser;
    private Vector2 scale;

    public LevelLoader(Vector2 scale){
        this.scale = scale;
        levelParser = new LevelParser();
    }

    // BEGIN: Setters and Getters
    public Rectangle getBounds(){ return bounds; }
    public PooledList<Obstacle> getAddQueue(){ return addQueue; }
    public int getStartingAmmo(){ return levelParser.getStartingAmmo(); }
    // END: Setters and Getters

    /**
     * TODO: write desc... loads the level based on the json file. adds the (Obstacle) object into addQueue
     */
    public void loadLevel(String JSONFile){
        // reset queue of objects
        addQueue.clear();

        //sets the new world bounds TODO: do this with json values
        bounds = new Rectangle(0,0,32,18);
        levelParser.loadLevel(JSONFile);
        populateLevel();

        // set player
        float[] playerData = levelParser.getPlayer();
        PlayerModel player = new PlayerModel(playerData[0], playerData[1],
                playerTexture.getRegionWidth() / scale.x, playerTexture.getRegionHeight() / scale.y);
        player.setDrawScale(scale);
        player.setTexture(playerTexture);
        addQueuedObject(player);
    }

    /**
     * Lays out the game geography.
     *
     * TODO: base this function off json data
     */
    private void populateLevel() {
        // add background
        float dwidth  = bgTile.getRegionWidth()/scale.x;
        float dheight = bgTile.getRegionHeight()/scale.y;
        BoxObstacle bg = new BackgroundModel(dwidth/2,dheight/2,dwidth,dheight);
        bg.setDrawScale(scale);
        bg.setTexture(bgTile);
        addQueuedObject(bg);

        // Add level goal
        dwidth  = goalTile.getRegionWidth()/scale.x;
        dheight = goalTile.getRegionHeight()/scale.y;
        float[] target = levelParser.getTarget();
        BoxObstacle goalDoor = new GoalModel(target[0],target[1],dwidth,dheight);
        goalDoor.setDrawScale(scale);
        goalDoor.setTexture(goalTile);
        addQueuedObject(goalDoor);

        //add walls
        float[][] walls = levelParser.getWalls();
        for (int ii = 0; ii < walls.length; ii++) {
            PolygonObstacle obj = new WallModel(walls[ii]);
            obj.setDrawScale(scale);
            obj.setTexture(platformTile);
            addQueuedObject(obj);
        }

        float[][] platforms = levelParser.getPlatforms();
        for (int ii = 0; ii < platforms.length; ii++) {
            PolygonObstacle obj = new PlatformModel(platforms[ii]);
            obj.setDrawScale(scale);
            obj.setTexture(platformTile);
            addQueuedObject(obj);
        }

        // Create enemies
        float[][] enemies = levelParser.getEnemies();
        dwidth  = enemyTexture.getRegionWidth()/scale.x;
        dheight = enemyTexture.getRegionHeight()/scale.y;
        EnemyModel enemy;
        for (int ii = 0; ii < enemies.length; ii++) {
            enemy = new EnemyModel(enemies[ii][1], enemies[ii][2], dwidth, dheight, enemies[ii][3] == 1.0f,
                    enemies[ii][0] == 1.0f, (int)enemies[ii][4]);
            enemy.setDrawScale(scale);
            enemy.setTexture(enemyTexture);
            addQueuedObject(enemy);
        }


        // Create one ammo depot
        float[][] resources = levelParser.getResources();
        dheight = depotTexture.getRegionHeight()/scale.y;
        dwidth = depotTexture.getRegionWidth()/scale.x;
        for (int ii = 0; ii < resources.length; ii++) {
            //type of resource is ammo depot
            if (resources[ii][0] == 0) {
                AmmoDepotModel ammoDepot = new AmmoDepotModel(resources[ii][1], resources[ii][2], dwidth, dheight, 3);
                ammoDepot.setDrawScale(scale);
                ammoDepot.setTexture(depotTexture);
                addQueuedObject(ammoDepot);
            }

        }
    }

    /**
     *
     * Adds a physics object in to the insertion queue.
     *
     * Objects on the queue are added just before collision processing.  We do this to
     * control object creation.
     *
     * param obj The object to add
     */
    public void addQueuedObject(Obstacle obj) {
        assert inBounds(obj) : "Object is not in bounds";
        addQueue.add(obj);
    }

    @Override
    public void preLoadContent(AssetManager manager) {
        // Load the shared tiles.
        manager.load(PLATFORM_FILE,Texture.class);
        manager.load(GOAL_FILE,Texture.class);
        manager.load(BACKGROUND_FILE,Texture.class);
        manager.load(ENEMY_FILE,Texture.class);
        manager.load(CHARACTER_FILE, Texture.class);
        manager.load(AMMO_DEPOT_FILE, Texture.class);
    }

    @Override
    public void loadContent(AssetManager manager) {
        bgTile  = AssetRetriever.createTexture(manager, BACKGROUND_FILE,true);
        platformTile = AssetRetriever.createTexture(manager,PLATFORM_FILE,true);
        goalTile  = AssetRetriever.createTexture(manager,GOAL_FILE,false);
        enemyTexture  = AssetRetriever.createTexture(manager,ENEMY_FILE,false);
        playerTexture = AssetRetriever.createTexture(manager, CHARACTER_FILE, false);
        depotTexture = AssetRetriever.createTexture(manager, AMMO_DEPOT_FILE, false);
    }

    @Override
    public void unloadContent(AssetManager manager) {
        manager.unload(BACKGROUND_FILE);
        manager.unload(PLATFORM_FILE);
        manager.unload(GOAL_FILE);
        manager.unload(ENEMY_FILE);
        manager.unload(CHARACTER_FILE);
    }

    @Override
    public void dispose() {
        addQueue.clear();
        addQueue = null;
    }

    /**
     * Returns true if the object is in bounds.
     *
     * This assertion is useful for debugging the physics.
     *
     * @param obj The object to check.
     *
     * @return true if the object is in bounds.
     */
    private boolean inBounds(Obstacle obj) {
        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
        boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
        return horiz && vert;
    }

    private void printMatrix(float[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}