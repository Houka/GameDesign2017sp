package edu.cornell.gdiac.game.levelLoading;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
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
    private static String EARTH_FILE = "character/earthtile.png";
    private static String GOAL_FILE = "character/securityCamera.png";
    private static String BG_FILE = "character/facade.png";
    private static String ENEMY_FILE = "character/dude.png";
    private static String CHARACTER_FILE = "character/charStatic.png";

    private static Vector2 GOAL_POS = new Vector2(29.5f,15.0f); // x = 4.0f, y = 14.0f
    private static Vector2 DUDE_POS = new Vector2(2.5f, 5.0f);

    private TextureRegion earthTile;
    private TextureRegion goalTile;
    private TextureRegion bgTile;
    private TextureRegion enemyTexture;
    private TextureRegion playerTexture;

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
    // END: Setters and Getters

    /**
     * TODO: write desc... loads the level based on the json file. adds the (Obstacle) object into addQueue
     */
    public void loadLevel(String JSONFile){
        // reset queue of objects
        addQueue.clear();

        //sets the new world bounds TODO: do this with json values
        bounds = new Rectangle(0,0,32,18);

        populateLevel();

        // set player
        PlayerModel player = new PlayerModel(DUDE_POS.x, DUDE_POS.y, playerTexture.getRegionWidth() / scale.x,
                playerTexture.getRegionHeight() / scale.y);
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
        BoxObstacle goalDoor = new GoalModel(GOAL_POS.x,GOAL_POS.y,dwidth,dheight);
        goalDoor.setDrawScale(scale);
        goalDoor.setTexture(goalTile);
        addQueuedObject(goalDoor);

        for (int ii = 0; ii < levelParser.getWalls().length; ii++) {
            PolygonObstacle obj = new WallModel(levelParser.getWalls()[ii]);
            obj.setDrawScale(scale);
            obj.setTexture(earthTile);
            addQueuedObject(obj);
        }

        for (int ii = 0; ii < levelParser.getPlatforms().length; ii++) {
            PolygonObstacle obj = new PlatformModel(levelParser.getPlatforms()[ii]);
            obj.setDrawScale(scale);
            obj.setTexture(earthTile);
            addQueuedObject(obj);
        }

        // Create 2 enemies
        dwidth  = enemyTexture.getRegionWidth()/scale.x;
        dheight = enemyTexture.getRegionHeight()/scale.y;
        EnemyModel enemy = new EnemyModel(DUDE_POS.x+1, DUDE_POS.y + 3, dwidth, dheight, true);
        enemy.setDrawScale(scale);
        enemy.setTexture(enemyTexture);
        addQueuedObject(enemy);

        enemy = new EnemyModel(DUDE_POS.x+4, DUDE_POS.y + 5, dwidth, dheight, true);
        enemy.setDrawScale(scale);
        enemy.setTexture(enemyTexture);
        addQueuedObject(enemy);
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
        manager.load(EARTH_FILE,Texture.class);
        manager.load(GOAL_FILE,Texture.class);
        manager.load(BG_FILE,Texture.class);
        manager.load(ENEMY_FILE,Texture.class);
        manager.load(CHARACTER_FILE, Texture.class);
    }

    @Override
    public void loadContent(AssetManager manager) {
        bgTile  = AssetRetriever.createTexture(manager,BG_FILE,true);
        earthTile = AssetRetriever.createTexture(manager,EARTH_FILE,true);
        goalTile  = AssetRetriever.createTexture(manager,GOAL_FILE,true);
        enemyTexture  = AssetRetriever.createTexture(manager,ENEMY_FILE,true);
        playerTexture = AssetRetriever.createTexture(manager, CHARACTER_FILE, false);
    }

    @Override
    public void unloadContent(AssetManager manager) {
        manager.unload(BG_FILE);
        manager.unload(EARTH_FILE);
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
}
