package edu.cornell.gdiac.game.levelLoading;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;
import edu.cornell.gdiac.util.obstacles.Obstacle;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;
import java.util.HashMap;


/**
 * Created by Lu on 3/16/2017.
 *
 * The level loader class puts objects from a json file into the world.
 */
public class LevelLoader implements AssetUser, Disposable{
    /** Textures */
    private TextureRegion platformTile;
    private TextureRegion platformLeftTile;
    private TextureRegion platformRightTile;
    private TextureRegion platformCenterTile;
    private TextureRegion platformSingleTile;
    private TextureRegion platformVerticalTile;
    private TextureRegion wallTile;
    private TextureRegion goalTile;
    private TextureRegion bgTile;
    private TextureRegion enemyOnsightTexture;
    private TextureRegion enemyIntervalTexture;
    private TextureRegion playerTexture;
    private TextureRegion depotTexture;
    private TextureRegion splattererTexture;

    /**holds background images*/
    private HashMap<String, TextureRegion> backgroundRegions;
    /** Animations */
    private Animation playerAnimation;
    private Animation enemyOnsightAnimation;
    private Animation enemyIntervalAnimation;
    private Animation spikeAnimation;

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
        backgroundRegions = new HashMap<String, TextureRegion>();
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
        addBackgroundObjects();
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
        BoxObstacle bg = new BackgroundModel(dwidth/2, dheight/2, dwidth * 10, dheight * 10);
        bg.setDrawScale(scale);
        bgTile.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bg.setTexture(bgTile);
        addQueuedObject(bg);
    }

    private float mod(float x,int n) {
        return x>0 ? x % n : x % n + n;
    }

    /**
     * Adds the platforms to the insertion queue
     */
    public void addPlatforms(){
        JsonValue platforms = levelParser.getPlatforms();
        JsonValue dflt = platforms.get("default");
        JsonValue.JsonIterator iter = dflt.iterator();
        JsonValue vertices;
        HashMap<Vector2,float[]> platformMap = new HashMap<Vector2, float[]>();
        while (iter.hasNext()){
            vertices = iter.next();
            float[] verts = vertices.asFloatArray();
            Vector2 vec = new Vector2(verts[0]*scale.x,verts[1]*scale.y);
            vec.x = (int) (vec.x-mod(vec.x,Constants.DEFAULT_GRID))/Constants.DEFAULT_GRID;
            vec.y = (int) (vec.y-mod(vec.y,Constants.DEFAULT_GRID))/Constants.DEFAULT_GRID;
            platformMap.put(vec,verts);
        }
        Vector2 left = new Vector2();
        Vector2 right = new Vector2();
        Vector2 above = new Vector2();
        Vector2 below = new Vector2();
        for(Vector2 k : platformMap.keySet()) {
            float[] verts = platformMap.get(k);
            PolygonObstacle obj = new PlatformModel(verts, PlatformModel.NORMAL_PLATFORM);
            obj.setDrawScale(scale);
            left.set(k.x-1,k.y);
            right.set(k.x+1,k.y);
            above.set(k.x,k.y+1);
            below.set(k.x,k.y-1);

            TextureRegion texture;
            if(platformMap.containsKey(above) || platformMap.containsKey(below)) {
                texture = platformVerticalTile;
            } else if(platformMap.containsKey(left) && platformMap.containsKey(right)) {
                texture = platformCenterTile;
            } else if(platformMap.containsKey(left)) {
                texture = platformRightTile;
            } else if(platformMap.containsKey(right)) {
                texture = platformLeftTile;
            } else{
                texture = platformSingleTile;
            }

            obj.setTexture(texture);
            addQueuedObject(obj);
        }

        JsonValue spikes = platforms.get("spikes_left");
        iter = spikes.iterator();
        while (iter.hasNext()) {
            vertices = iter.next();
            PlatformModel obj = new PlatformModel(vertices.asFloatArray(), PlatformModel.SPIKE_LEFT_PLATFORM);
            obj.setDrawScale(scale);
            obj.setTexture(spikeAnimation.getTextureRegion());
            obj.setAnimation(spikeAnimation);
            addQueuedObject(obj);
        }

        spikes = platforms.get("spikes_right");
        iter = spikes.iterator();
        while (iter.hasNext()) {
            vertices = iter.next();
            PlatformModel obj = new PlatformModel(vertices.asFloatArray(), PlatformModel.SPIKE_RIGHT_PLATFORM);
            obj.setDrawScale(scale);
            obj.setTexture(spikeAnimation.getTextureRegion());
            obj.setAnimation(spikeAnimation);
            addQueuedObject(obj);
        }

        spikes = platforms.get("spikes_up");
        iter = spikes.iterator();
        while (iter.hasNext()) {
            vertices = iter.next();
            PlatformModel obj = new PlatformModel(vertices.asFloatArray(), PlatformModel.SPIKE_UP_PLATFORM);
            obj.setDrawScale(scale);
            obj.setTexture(spikeAnimation.getTextureRegion());
            obj.setAnimation(spikeAnimation);
            addQueuedObject(obj);
        }

        spikes = platforms.get("spikes_down");
        iter = spikes.iterator();
        while (iter.hasNext()) {
            vertices = iter.next();
            PlatformModel obj = new PlatformModel(vertices.asFloatArray(), PlatformModel.SPIKE_DOWN_PLATFORM);
            obj.setDrawScale(scale);
            obj.setTexture(spikeAnimation.getTextureRegion());
            obj.setAnimation(spikeAnimation);
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
            obj.setTexture(wallTile);
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
                    enemy.get("isFacingRight").asBoolean(), false, enemy.get("interval").asInt(),
                    enemy.get("enemyType").asString());
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
                    enemy.get("isFacingRight").asBoolean(), true, 0,
                    enemy.get("enemyType").asString());
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
     * Adds the splatterers to the insertion queue.
     */
    public void addSplatterers(){
        JsonValue splatterers = levelParser.getSplatterers();
        float dheight = splattererTexture.getRegionWidth()/scale.x;
        float dwidth = splattererTexture.getRegionHeight()/scale.y;

        JsonValue dflt = splatterers.get("default");
        JsonValue.JsonIterator iter = dflt.iterator();
        JsonValue splat;
        while (iter.hasNext()){
            splat = iter.next();
            SplattererModel splatterer = new SplattererModel(splat.get("x").asFloat(), splat.get("y").asFloat(), dwidth, dheight);
            splatterer.setDrawScale(scale);
            splatterer.setTexture(splattererTexture);
            addQueuedObject(splatterer);
        }
    }

    /**
     * Adds the background objects to the insertion queue.
     */
    public void addBackgroundObjects(){
        if (levelParser.backgroundObjectsExist()) {
            JsonValue bgObjects = levelParser.getBackgroundObjects();

            JsonValue.JsonIterator iter = bgObjects.iterator();
            JsonValue bgObject;
            while (iter.hasNext()){
                bgObject = iter.next();
                String id = bgObject.get("path").asString();
                TextureRegion current = backgroundRegions.get(id);
                BackgroundObjectModel bg = new BackgroundObjectModel(bgObject.get("x").asFloat(), bgObject.get("y").asFloat(),
                        current.getRegionWidth()/scale.x, current.getRegionHeight()/scale.y);
                bg.setDrawScale(scale);
                bg.setTexture(current);
                addQueuedObject(bg);
            }
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
        manager.load(Constants.PLATFORM_FILE,Texture.class);
        manager.load(Constants.PLATFORM_LEFT_CAP_FILE,Texture.class);
        manager.load(Constants.PLATFORM_RIGHT_CAP_FILE,Texture.class);
        manager.load(Constants.PLATFORM_CENTER_FILE,Texture.class);
        manager.load(Constants.PLATFORM_SINGLE_FILE,Texture.class);
        manager.load(Constants.PLATFORM_VERTICAL_FILE,Texture.class);
        manager.load(Constants.WALL_FILE,Texture.class);
        manager.load(Constants.GOAL_FILE,Texture.class);
        manager.load(Constants.BACKGROUND_FILE,Texture.class);
        manager.load(Constants.ENEMY_INTERVAL_FILE,Texture.class);
        manager.load(Constants.ENEMY_ONSIGHT_FILE,Texture.class);
        manager.load(Constants.ENEMY_SPOTTED_FILE,Texture.class);
        manager.load(Constants.CHARACTER_STILL_FILE, Texture.class);
        manager.load(Constants.CHARACTER_FALLING_FILE, Texture.class);
        manager.load(Constants.CHARACTER_IDLE_FILE, Texture.class);
        manager.load(Constants.CHARACTER_MIDAIR_FILE, Texture.class);
        manager.load(Constants.CHARACTER_RISING_FILE, Texture.class);
        manager.load(Constants.CHARACTER_TRANSITION_FILE, Texture.class);
        manager.load(Constants.CHARACTER_RUN_FILE, Texture.class);
        manager.load(Constants.CHARACTER_SHOOT_FILE, Texture.class);
        manager.load(Constants.CHARACTER_CROUCH_FILE, Texture.class);
        manager.load(Constants.CHARACTER_STUNNED_FILE, Texture.class);
        manager.load(Constants.CHARACTER_CROUCH_SHOOT_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_CHARACTER_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_ENEMY_MINE_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_ENEMY_NORMAL_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_MINE_TRAIL_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_NORMAL_TRAIL_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_STATIONARY_MINE_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_STATIONARY_NORMAL_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_STATIONARY_CHAR_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_SPLAT_EFFECT_FILE, Texture.class);
        manager.load(Constants.AMMO_DEPOT_FILE, Texture.class);
        manager.load(Constants.SPLATTERER_FILE, Texture.class);
        manager.load(Constants.SPIKES_DOWN_SPIN_FILE, Texture.class);
        manager.load(Constants.SPIKES_UP_SPIN_FILE, Texture.class);
        manager.load(Constants.SPIKES_LEFT_SPIN_FILE, Texture.class);
        manager.load(Constants.SPIKES_RIGHT_SPIN_FILE, Texture.class);
        manager.load(Constants.SPIKES_RIGHT_STILL_FILE, Texture.class);
        manager.load(Constants.SPIKES_LEFT_STILL_FILE, Texture.class);
        manager.load(Constants.SPIKES_UP_STILL_FILE, Texture.class);
        manager.load(Constants.SPIKES_DOWN_STILL_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_CHAR_SPLAT_EFFECT_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_ENEMY_SPLAT_EFFECT_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE, Texture.class);

        for (String s: Constants.TUTORIAL_FILES) {
            manager.load(s, Texture.class);
        }
    }

    @Override
    public void loadContent(AssetManager manager) {
        // static texture loading
        bgTile  = AssetRetriever.createTextureRegion(manager, Constants.BACKGROUND_FILE,true);
        platformTile = AssetRetriever.createTextureRegion(manager,Constants.PLATFORM_FILE,true);
        platformLeftTile = AssetRetriever.createTextureRegion(manager,Constants.PLATFORM_LEFT_CAP_FILE,false);
        platformRightTile = AssetRetriever.createTextureRegion(manager,Constants.PLATFORM_RIGHT_CAP_FILE,false);
        platformCenterTile = AssetRetriever.createTextureRegion(manager,Constants.PLATFORM_CENTER_FILE,false);
        platformSingleTile = AssetRetriever.createTextureRegion(manager,Constants.PLATFORM_SINGLE_FILE,false);
        platformVerticalTile = AssetRetriever.createTextureRegion(manager,Constants.PLATFORM_VERTICAL_FILE,false);
        wallTile = AssetRetriever.createTextureRegion(manager,Constants.WALL_FILE,true);
        goalTile  = AssetRetriever.createTextureRegion(manager,Constants.GOAL_FILE,false);
        enemyIntervalTexture  = AssetRetriever.createTextureRegion(manager,Constants.ENEMY_INTERVAL_FILE,false);
        enemyOnsightTexture  = AssetRetriever.createTextureRegion(manager,Constants.ENEMY_ONSIGHT_FILE,false);
        playerTexture = AssetRetriever.createTextureRegion(manager, Constants.CHARACTER_STILL_FILE, false);
        depotTexture = AssetRetriever.createTextureRegion(manager, Constants.AMMO_DEPOT_FILE, false);
        splattererTexture = AssetRetriever.createTextureRegion(manager, Constants.SPLATTERER_FILE, false);

        for (String s: Constants.TUTORIAL_FILES) {
            backgroundRegions.put(s, AssetRetriever.createTextureRegion(manager, s, false));
        }
        // animation spritesheet loading
        playerAnimation = new Animation();
        playerAnimation.addTexture("idle", AssetRetriever.createTexture(manager, Constants.CHARACTER_IDLE_FILE, false), 1,5);
        playerAnimation.addTexture("run", AssetRetriever.createTexture(manager, Constants.CHARACTER_RUN_FILE, false), 1,4);
        playerAnimation.addTexture("shoot", AssetRetriever.createTexture(manager, Constants.CHARACTER_SHOOT_FILE, false), 1,1);
        playerAnimation.addTexture("crouch", AssetRetriever.createTexture(manager, Constants.CHARACTER_CROUCH_FILE, false), 1,1);
        playerAnimation.addTexture("stunned", AssetRetriever.createTexture(manager, Constants.CHARACTER_STUNNED_FILE, false), 1,1);
        playerAnimation.addTexture("rising", AssetRetriever.createTexture(manager, Constants.CHARACTER_RISING_FILE, false), 1,2);
        playerAnimation.addTexture("falling", AssetRetriever.createTexture(manager, Constants.CHARACTER_FALLING_FILE, false), 1,2);
        playerAnimation.addTexture("peak", AssetRetriever.createTexture(manager, Constants.CHARACTER_TRANSITION_FILE, false), 1,2);
        playerAnimation.addTexture("midair shoot", AssetRetriever.createTexture(manager, Constants.CHARACTER_MIDAIR_FILE, false), 1,1);
        playerAnimation.addTexture("crouch", AssetRetriever.createTexture(manager, Constants.CHARACTER_CROUCH_FILE, false), 1,1);
        playerAnimation.addTexture("crouch_shoot", AssetRetriever.createTexture(manager, Constants.CHARACTER_CROUCH_SHOOT_FILE, false), 1,1);
        playerAnimation.addTexture("still", playerTexture.getTexture(), 1, 1);
        playerAnimation.setPlaying(false);
        playerAnimation.setPlayingAnimation("idle");

        enemyIntervalAnimation= new Animation();
        enemyIntervalAnimation.addTexture("shoot", AssetRetriever.createTexture(manager, Constants.ENEMY_INTERVAL_FILE, false), 1,1);
        enemyIntervalAnimation.addTexture("spotted", AssetRetriever.createTexture(manager, Constants.ENEMY_SPOTTED_FILE, false), 1,1);
        enemyIntervalAnimation.addTexture("still", enemyIntervalTexture.getTexture(), 1, 1);
        enemyIntervalAnimation.setPlaying(false);
        enemyIntervalAnimation.setPlayingAnimation("still");

        enemyOnsightAnimation= new Animation();
        enemyOnsightAnimation.addTexture("shoot", AssetRetriever.createTexture(manager, Constants.ENEMY_ONSIGHT_FILE, false), 1,1);
        enemyOnsightAnimation.addTexture("spotted", AssetRetriever.createTexture(manager, Constants.ENEMY_SPOTTED_FILE, false), 1,1);
        enemyOnsightAnimation.addTexture("still", enemyOnsightTexture.getTexture(), 1, 1);
        enemyOnsightAnimation.setPlaying(false);
        enemyOnsightAnimation.setPlayingAnimation("still");

        spikeAnimation = new Animation();
        spikeAnimation.addTexture("spin", AssetRetriever.createTexture(manager, Constants.SPIKES_UP_STILL_FILE, false), 1 , 1);
        spikeAnimation.setPlaying(false);
        spikeAnimation.setPlayingAnimation("spin");
    }

    @Override
    public void unloadContent(AssetManager manager) {
        manager.unload(Constants.BACKGROUND_FILE);
        manager.unload(Constants.PLATFORM_FILE);
        manager.unload(Constants.GOAL_FILE);
        manager.unload(Constants.ENEMY_ONSIGHT_FILE);
        manager.unload(Constants.ENEMY_INTERVAL_FILE);
        manager.unload(Constants.ENEMY_SPOTTED_FILE);
        manager.unload(Constants.CHARACTER_STILL_FILE);
        manager.unload(Constants.CHARACTER_FALLING_FILE);
        manager.unload(Constants.CHARACTER_IDLE_FILE);
        manager.unload(Constants.CHARACTER_MIDAIR_FILE);
        manager.unload(Constants.CHARACTER_RISING_FILE);
        manager.unload(Constants.CHARACTER_TRANSITION_FILE);
        manager.unload(Constants.CHARACTER_RUN_FILE);
        manager.unload(Constants.CHARACTER_SHOOT_FILE);
        manager.unload(Constants.CHARACTER_CROUCH_FILE);
        manager.unload(Constants.SPLATTERER_FILE);
        manager.unload(Constants.PLATFORM_LEFT_CAP_FILE);
        manager.unload(Constants.PLATFORM_RIGHT_CAP_FILE);
        manager.unload(Constants.PLATFORM_CENTER_FILE);
        manager.unload(Constants.PLATFORM_SINGLE_FILE);
        manager.unload(Constants.PLATFORM_VERTICAL_FILE);
        manager.unload(Constants.SPIKES_DOWN_SPIN_FILE);
        manager.unload(Constants.SPIKES_UP_SPIN_FILE);
        manager.unload(Constants.SPIKES_LEFT_SPIN_FILE);
        manager.unload(Constants.SPIKES_RIGHT_SPIN_FILE);
        manager.unload(Constants.SPIKES_RIGHT_STILL_FILE);
        manager.unload(Constants.SPIKES_LEFT_STILL_FILE);
        manager.unload(Constants.SPIKES_UP_STILL_FILE);
        manager.unload(Constants.SPIKES_DOWN_STILL_FILE);
        manager.unload(Constants.PAINTBALL_CHARACTER_FILE);
        manager.unload(Constants.PAINTBALL_ENEMY_MINE_FILE);
        manager.unload(Constants.PAINTBALL_ENEMY_NORMAL_FILE);
        manager.unload(Constants.PAINTBALL_MINE_TRAIL_FILE);
        manager.unload(Constants.PAINTBALL_NORMAL_TRAIL_FILE);
        manager.unload(Constants.PAINTBALL_STATIONARY_MINE_FILE);
        manager.unload(Constants.PAINTBALL_STATIONARY_NORMAL_FILE);
        manager.unload(Constants.PAINTBALL_STATIONARY_CHAR_FILE);
        manager.unload(Constants.PAINTBALL_SPLAT_EFFECT_FILE);
        manager.unload(Constants.PAINTBALL_CHAR_SPLAT_EFFECT_FILE);
        manager.unload(Constants.PAINTBALL_ENEMY_SPLAT_EFFECT_FILE);
        manager.unload(Constants.PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE);
        manager.unload(Constants.AMMO_DEPOT_FILE);
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