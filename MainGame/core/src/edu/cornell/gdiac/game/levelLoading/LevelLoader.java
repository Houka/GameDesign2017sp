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
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;
import edu.cornell.gdiac.util.obstacles.Obstacle;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;


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
        bounds = new Rectangle(0,0,32,18*3);
        LevelCreator c = new LevelCreator();
        c.writeDemoJson();
        levelParser.loadLevel(JSONFile);
        populateLevel();


    }

    /**
     * Lays out the game geography.
     *
     * TODO: base this function off json data
     */
    private void populateLevel() {
        addBackground();
        addPlatforms();
        addWalls();
        addPlayer();
        addEnemies();
        addResources();
        addTarget();

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


    public void addPlayer(){
        JsonValue playerData = levelParser.getPlayer();
        PlayerModel player = new PlayerModel(playerData.get("x").asFloat(), playerData.get("y").asFloat(),
                playerTexture.getRegionWidth() / scale.x, playerTexture.getRegionHeight() / scale.y);
        player.setDrawScale(scale);
        player.setTexture(playerTexture);
        addQueuedObject(player);
    }

    public void addEnemies(){
        float dwidth  = enemyTexture.getRegionWidth()/scale.x;
        float dheight = enemyTexture.getRegionHeight()/scale.y;
        JsonValue enemies = levelParser.getEnemies();
        JsonValue interval = enemies.get("interval");
        JsonValue.JsonIterator iter = interval.iterator();
        JsonValue enemy;
        while (iter.hasNext()){
            enemy = iter.next();
            EnemyModel obj = new EnemyModel(enemy.get("x").asInt(), enemy.get("y").asFloat(), dwidth, dheight,
                            enemy.get("isFacingRight").asBoolean(), false, enemy.get("interval").asInt());
            obj.setDrawScale(scale);
            obj.setTexture(enemyTexture);
            addQueuedObject(obj);
        }

        JsonValue onSight = enemies.get("on_sight");
        iter = onSight.iterator();
        while (iter.hasNext()){
            enemy = iter.next();
            EnemyModel obj = new EnemyModel(enemy.get("x").asInt(), enemy.get("y").asFloat(), dwidth, dheight,
                    enemy.get("isFacingRight").asBoolean(), true, 0);
            obj.setDrawScale(scale);
            obj.setTexture(enemyTexture);
            addQueuedObject(obj);
        }
    }

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
