package edu.cornell.gdiac.game.levelLoading;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;
import edu.cornell.gdiac.util.obstacles.Obstacle;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;


/**
 * Created by Lu on 3/16/2017.
 *
 * The level loader class puts objects from a json file into the world.
 */
public class LevelLoader implements AssetUser, Disposable{
    /** Filenames for sprites of objects */
    private static String PLATFORM_FILE = "sprites/fixtures/window_tile.png";
    private static String GOAL_FILE = "sprites/security_camera.png";
    private static String BACKGROUND_FILE = "sprites/wall/brick_wall_tile.png";
    private static String ENEMY_ONSIGHT_FILE = "sprites/enemy/enemy_onsight.png";
    private static String ENEMY_INTERVAL_FILE = "sprites/enemy/enemy_interval.png";
    private static String ENEMY_SPOTTED_FILE = "sprites/enemy/enemy_spotted.png";
    private static String CHARACTER_STILL_FILE = "sprites/char/char_still.png";
    private static String CHARACTER_RUN_FILE = "sprites/char/char_run_strip4.png";
    private static String CHARACTER_FALLING_FILE = "sprites/char/char_fall.png";
    private static String CHARACTER_IDLE_FILE = "sprites/char/char_idle_strip5.png";
    private static String CHARACTER_MIDAIR_FILE = "sprites/char/char_midair_shoot.png";
    private static String CHARACTER_RISING_FILE = "sprites/char/char_jump.png";
    private static String CHARACTER_SHOOT_FILE = "sprites/char/char_shoot.png";
    private static String AMMO_DEPOT_FILE = "sprites/paint_repo.png";

    /** Textures */
    private TextureRegion platformTile;
    private TextureRegion goalTile;
    private TextureRegion bgTile;
    private TextureRegion enemyOnsightTexture;
    private TextureRegion enemyIntervalTexture;
    private TextureRegion playerTexture;
    private TextureRegion depotTexture;

    /** Animations */
    private Animation playerAnimation;
    private Animation enemyOnsightAnimation;
    private Animation enemyIntervalAnimation;

    /** Bounds of the window*/
    private Rectangle bounds;
    /** Queue for adding objects */
    private PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
    /** LevelParser object we get object data from*/
    private LevelParser levelParser;
    /** LevelParser scale at which everything is drawn*/
    private Vector2 scale;

    /** Constructor that specifies scale.
     * @param scale Scale at which the level is drawn*/
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
     * loads the level based on the json file.
     */
    public void loadLevel(String JSONFile){
        // reset queue of objects
        addQueue.clear();
        //sets the new world bounds
        bounds = new Rectangle(0,0,32,18*3);
        levelParser.loadLevel(JSONFile);
        populateLevel();
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {
        addBackground();
        addPlatforms();
        addWalls();
        addPlayer();
        addEnemies();
        addResources();
        addTarget();
        addSplatterers();
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

    /**
     * Adds the background to the insertion queue
     */
    public void addBackground() {
        float dwidth = bgTile.getRegionWidth() / scale.x;
        float dheight = bgTile.getRegionHeight() / scale.y;
        BoxObstacle bg = new BackgroundModel(dwidth / 2, dheight / 2, dwidth * 2, dheight * 3);
        bg.setDrawScale(scale);
        bgTile.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bg.setTexture(bgTile);
        addQueuedObject(bg);
    }

    /**
     * Adds the platforms to the insertion queue
     */
    public void addPlatforms(){
        JsonValue platforms = levelParser.getPlatforms();
        JsonValue dflt = platforms.get("default");
        JsonValue.JsonIterator iter = dflt.iterator();
        JsonValue vertices;
        while (iter.hasNext()){
            vertices = iter.next();
            PolygonObstacle obj = new PlatformModel(vertices.asFloatArray());
            obj.setDrawScale(scale);
            obj.setTexture(platformTile);
            addQueuedObject(obj);
        }
    }

    /**
     * Adds the walls to the insertion queue
     */
    public void addWalls(){
        JsonValue walls = levelParser.getWalls();
        JsonValue dflt = walls.get("default");
        JsonValue.JsonIterator iter = dflt.iterator();
        JsonValue vertices;
        while (iter.hasNext()){
            vertices = iter.next();
            PolygonObstacle obj = new WallModel(vertices.asFloatArray());
            obj.setDrawScale(scale);
            obj.setTexture(platformTile);
            addQueuedObject(obj);
        }
    }

    /**
     * Adds the player to the insertion queue
     */
    public void addPlayer(){
        JsonValue playerData = levelParser.getPlayer();
        PlayerModel player = new PlayerModel(playerData.get("x").asFloat(), playerData.get("y").asFloat(),
                playerTexture.getRegionWidth() / scale.x, playerTexture.getRegionHeight() / scale.y);
        player.setDrawScale(scale);
        player.setTexture(playerTexture);
        player.setAnimation(playerAnimation);
        addQueuedObject(player);
    }

    /**
     * Adds the enemies to the insertion queue. Currently handles on sight and interval shooters.
     */
    public void addEnemies(){
        float dwidth  = enemyIntervalTexture.getRegionWidth()/scale.x;
        float dheight = enemyIntervalTexture.getRegionHeight()/scale.y;
        JsonValue enemies = levelParser.getEnemies();

        //add interval shooters
        JsonValue interval = enemies.get("interval");
        JsonValue.JsonIterator iter = interval.iterator();
        JsonValue enemy;
        while (iter.hasNext()){
            enemy = iter.next();
            EnemyModel obj = new EnemyModel(enemy.get("x").asInt(), enemy.get("y").asFloat(), dwidth, dheight,
                            enemy.get("isFacingRight").asBoolean(), false, enemy.get("interval").asInt());
            obj.setDrawScale(scale);
            obj.setTexture(enemyIntervalTexture);
            obj.setAnimation(enemyIntervalAnimation);
            addQueuedObject(obj);
        }

        dwidth  = enemyOnsightTexture.getRegionWidth()/scale.x;
        dheight = enemyOnsightTexture.getRegionHeight()/scale.y;
        //add on sight shooters
        JsonValue onSight = enemies.get("on_sight");
        iter = onSight.iterator();
        while (iter.hasNext()){
            enemy = iter.next();
            EnemyModel obj = new EnemyModel(enemy.get("x").asInt(), enemy.get("y").asFloat(), dwidth, dheight,
                    enemy.get("isFacingRight").asBoolean(), true, 0);
            obj.setDrawScale(scale);
            obj.setTexture(enemyOnsightTexture);
            obj.setAnimation(enemyOnsightAnimation);
            addQueuedObject(obj);
        }
    }

    /**
     * Adds the resources to the insertion queue. Currently only handles ammo depots.
     */
    public void addResources(){
        JsonValue resources = levelParser.getResources();
        float dheight = depotTexture.getRegionHeight()/scale.y;
        float dwidth = depotTexture.getRegionWidth()/scale.x;

        JsonValue ammoDepots = resources.get("ammo_depots");
        JsonValue.JsonIterator iter = ammoDepots.iterator();
        JsonValue depot;
        while (iter.hasNext()){
            depot = iter.next();
            AmmoDepotModel ammoDepot = new AmmoDepotModel(depot.get("x").asFloat(), depot.get("y").asFloat(), dwidth,
                    dheight, depot.get("amount").asInt());
            ammoDepot.setDrawScale(scale);
            ammoDepot.setTexture(depotTexture);
            addQueuedObject(ammoDepot);
        }
    }

    /**
     * Adds the resources to the insertion queue. Currently only handles ammo depots.
     */
    public void addSplatterers(){
        JsonValue splatterers = levelParser.getSplatterers();
        // TODO: change after texture
        float dheight = 48;
        float dwidth = 48;

        JsonValue dflt = splatterers.get("default");
        JsonValue.JsonIterator iter = dflt.iterator();
        JsonValue splat;
        while (iter.hasNext()){
            splat = iter.next();
            SplattererModel splatterer = new SplattererModel(splat.get("x").asFloat(), splat.get("y").asFloat(), dwidth, dheight);
            splatterer.setDrawScale(scale);
            // TODO: change from depot texture
            splatterer.setTexture(depotTexture);
            addQueuedObject(splatterer);
        }
    }

    /**
     * Adds the target to the insertion queue
     */
    public void addTarget(){
        float dwidth  = goalTile.getRegionWidth()/scale.x;
        float dheight = goalTile.getRegionHeight()/scale.y;
        JsonValue target = levelParser.getTarget();
        BoxObstacle goalDoor = new GoalModel(target.get("x").asFloat(),target.get("y").asFloat(),dwidth, dheight);
        goalDoor.setDrawScale(scale);
        goalDoor.setTexture(goalTile);
        addQueuedObject(goalDoor);
    }

    @Override
    public void preLoadContent(AssetManager manager) {
        // Load the shared tiles.
        manager.load(PLATFORM_FILE,Texture.class);
        manager.load(GOAL_FILE,Texture.class);
        manager.load(BACKGROUND_FILE,Texture.class);
        manager.load(ENEMY_INTERVAL_FILE,Texture.class);
        manager.load(ENEMY_ONSIGHT_FILE,Texture.class);
        manager.load(ENEMY_SPOTTED_FILE,Texture.class);
        manager.load(CHARACTER_STILL_FILE, Texture.class);
        manager.load(CHARACTER_FALLING_FILE, Texture.class);
        manager.load(CHARACTER_IDLE_FILE, Texture.class);
        manager.load(CHARACTER_MIDAIR_FILE, Texture.class);
        manager.load(CHARACTER_RISING_FILE, Texture.class);
        manager.load(CHARACTER_RUN_FILE, Texture.class);
        manager.load(CHARACTER_SHOOT_FILE, Texture.class);
        manager.load(AMMO_DEPOT_FILE, Texture.class);
    }

    @Override
    public void loadContent(AssetManager manager) {
        // static texture loading
        bgTile  = AssetRetriever.createTextureRegion(manager, BACKGROUND_FILE,true);
        platformTile = AssetRetriever.createTextureRegion(manager,PLATFORM_FILE,true);
        goalTile  = AssetRetriever.createTextureRegion(manager,GOAL_FILE,false);
        enemyIntervalTexture  = AssetRetriever.createTextureRegion(manager,ENEMY_INTERVAL_FILE,false);
        enemyOnsightTexture  = AssetRetriever.createTextureRegion(manager,ENEMY_ONSIGHT_FILE,false);
        playerTexture = AssetRetriever.createTextureRegion(manager, CHARACTER_STILL_FILE, false);
        depotTexture = AssetRetriever.createTextureRegion(manager, AMMO_DEPOT_FILE, false);

        // animation spritesheet loading
        playerAnimation = new Animation();
        playerAnimation.addTexture("idle", AssetRetriever.createTexture(manager, CHARACTER_IDLE_FILE, false), 1,5);
        playerAnimation.addTexture("run", AssetRetriever.createTexture(manager, CHARACTER_RUN_FILE, false), 1,4);
        playerAnimation.addTexture("shoot", AssetRetriever.createTexture(manager, CHARACTER_SHOOT_FILE, false), 1,1);
        playerAnimation.addTexture("rising", AssetRetriever.createTexture(manager, CHARACTER_RISING_FILE, false), 1,1);
        playerAnimation.addTexture("falling", AssetRetriever.createTexture(manager, CHARACTER_FALLING_FILE, false), 1,1);
        playerAnimation.addTexture("midair shoot", AssetRetriever.createTexture(manager, CHARACTER_MIDAIR_FILE, false), 1,1);
        playerAnimation.addTexture("still", playerTexture.getTexture(), 1, 1);
        playerAnimation.setPlaying(false);
        playerAnimation.setPlayingAnimation("idle");

        enemyIntervalAnimation= new Animation();
        enemyIntervalAnimation.addTexture("shoot", AssetRetriever.createTexture(manager, ENEMY_INTERVAL_FILE, false), 1,1);
        enemyIntervalAnimation.addTexture("spotted", AssetRetriever.createTexture(manager, ENEMY_SPOTTED_FILE, false), 1,1);
        enemyIntervalAnimation.addTexture("still", enemyIntervalTexture.getTexture(), 1, 1);
        enemyIntervalAnimation.setPlaying(false);
        enemyIntervalAnimation.setPlayingAnimation("still");

        enemyOnsightAnimation= new Animation();
        enemyOnsightAnimation.addTexture("shoot", AssetRetriever.createTexture(manager, ENEMY_ONSIGHT_FILE, false), 1,1);
        enemyOnsightAnimation.addTexture("spotted", AssetRetriever.createTexture(manager, ENEMY_SPOTTED_FILE, false), 1,1);
        enemyOnsightAnimation.addTexture("still", enemyOnsightTexture.getTexture(), 1, 1);
        enemyOnsightAnimation.setPlaying(false);
        enemyOnsightAnimation.setPlayingAnimation("still");
    }

    @Override
    public void unloadContent(AssetManager manager) {
        manager.unload(BACKGROUND_FILE);
        manager.unload(PLATFORM_FILE);
        manager.unload(GOAL_FILE);
        manager.unload(ENEMY_ONSIGHT_FILE);
        manager.unload(ENEMY_INTERVAL_FILE);
        manager.unload(ENEMY_SPOTTED_FILE);
        manager.unload(CHARACTER_STILL_FILE);
        manager.unload(CHARACTER_FALLING_FILE);
        manager.unload(CHARACTER_IDLE_FILE);
        manager.unload(CHARACTER_MIDAIR_FILE);
        manager.unload(CHARACTER_RISING_FILE);
        manager.unload(CHARACTER_RUN_FILE);
        manager.unload(CHARACTER_SHOOT_FILE);
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

    /**
    * Debugging method
     */
    private void printMatrix(float[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
