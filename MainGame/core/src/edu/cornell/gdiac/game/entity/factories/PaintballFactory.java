package edu.cornell.gdiac.game.entity.factories;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.entity.models.PaintballModel;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.game.interfaces.Settable;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Created by Lu on 3/17/2017.
 *
 * Factory class for paintballs. Handles creation and removal of paintballs.
 */
public class PaintballFactory implements AssetUser, Settable {
    /** Offset for Paintball when firing */
    private static float right_x_offset = 1.5f;
    private static float left_x_offset = -2.0f;
    private static float y_offset = 0.25f;
    /** The density for a Paintball */
    private static float heavy_density = 100.0f;
    /** The speed of the Paintball after firing */
    private static float initial_speed = 4.0f;
    /** The speed of the Paintball after firing for player */
    private static float player_initial_speed = 8.0f;
    /** Duration for paintball to paintball sticking**/
    private static float paintballToPaintballDuration = 5f;
    /** Duration for paintball to wall sticking**/
    private static float paintballToWallDuration = 3f;
    /** Duration for paintball to platform sticking**/
    private static float paintballToPlatformDuration = 3f;
    /** How long a paintball can exist before forced death*/
    private final float MAX_LIFE_TIME = 20f;
    /**The starting width scale of the paintball**/
    private static float xScale =.5f;
    /**The maximum width scale of the paintball**/
    private static float maxXScale = 3f;
    /**The height scale of the paintball**/
    private static float yScale =.25f;
    /** The drawing scale of the Paintball*/
    private Vector2 scale;
    /** Texture of the paintball */
    private TextureRegion enemyTexture;
    private TextureRegion enemyTextureHead;
    private TextureRegion playerTextureHead;
    private TextureRegion normalPlatformTexture;
    private TextureRegion charPlatformTexture;
    private TextureRegion splatEffectTexture;
    private TextureRegion trailTexture;
    private TextureRegion enemyMineHeadTexture;
    private TextureRegion minePlatformTexture;
    private TextureRegion mineTrailTexture;
    private TextureRegion charSplatEffectTexture;
    private TextureRegion enemySplatEffectTexture;
    private TextureRegion mineSplatEffectTexture;
    private TextureRegion timerTexture;

    public PaintballFactory(Vector2 scale){
        this.scale = scale;
    }


    /**
     *  Creates a paintball at the given location moving in the given direction
     *  @param x            Initial x position of the paintball
     *  @param y            Initial y position of the paintball
     *  @param direction    Initial traveling direction of the paintball (true for right, left otherwise)
     */
    public PaintballModel createPaintball(float x, float y, boolean direction, String paintballType){
        float xOffset = (direction ? right_x_offset : left_x_offset);
        float width = enemyTexture.getRegionWidth()/(scale.x);
        float height = enemyTexture.getRegionHeight()/(scale.y);
        float speed  = (direction ? initial_speed : -initial_speed);
        PaintballModel paintball = new PaintballModel(x+xOffset, y+y_offset, width, height,speed,xScale,yScale,scale,paintballType);

        paintball.setMaxXScale(maxXScale);
        paintball.setBullet(true);
        paintball.setGravityScale(0);
        paintball.setFixedRotation(true);
        paintball.setVX(speed);
        paintball.setMaxLifeTime(MAX_LIFE_TIME);
        paintball.setDirection(direction);
        paintball.setPaintballType(paintballType);
        paintball.setDensity(heavy_density);
        paintball.setDrawScale(scale);
        paintball.setTexture(enemyTexture);
        paintball.setSplatEffectTexture(splatEffectTexture);
        paintball.setPaintballToPaintballDuration(paintballToPaintballDuration);
        paintball.setPaintballToWallDuration(paintballToWallDuration);
        paintball.setPaintballToPlatformDuration(paintballToPlatformDuration);
        paintball.setTimerTexture(timerTexture);

        if(paintballType.equals("trampoline")) {
            paintball.setTrailTexture(mineTrailTexture);
            paintball.setHeadTexture(enemyMineHeadTexture);
            paintball.setPlatformTexture(minePlatformTexture);
            paintball.setPlatformSplatEffectTexture(mineSplatEffectTexture);
        } else if(paintballType.equals("normal")) {
            paintball.setTrailTexture(trailTexture);
            paintball.setHeadTexture(enemyTextureHead);
            paintball.setPlatformTexture(normalPlatformTexture);
            paintball.setPlatformSplatEffectTexture(enemySplatEffectTexture);
        } else if(paintballType.equals("player")){
            paintball.setPlayerBullet(true);
            paintball.setHeadTexture(playerTextureHead);
            paintball.setPlatformSplatEffectTexture(charSplatEffectTexture);
            paintball.setPlatformTexture(charPlatformTexture);
            float pSpeed  = (direction ? player_initial_speed : -player_initial_speed);
            paintball.fixX(pSpeed);
        } else {
            assert(false);
        }
        //paintball.snap();
        return paintball;
    }

    // BEGIN: Setters and Getters


    public static float getHeavy_density() {
        return heavy_density;
    }

    public static float getRightX_offset() {
        return right_x_offset;
    }

    public static float getLeftX_offset() {
        return left_x_offset;
    }

    public static float getY_offset() {
        return y_offset;
    }

    public static float getInitial_speed() {
        return initial_speed;
    }

    public static float getPaintballToPaintballDuration() {
        return paintballToPaintballDuration;
    }

    public static float getPaintballToPlatformDuration() {
        return paintballToPlatformDuration;
    }

    public static float getPaintballToWallDuration() {
        return paintballToWallDuration;
    }

    public static float getMaxXScale() {
        return maxXScale;
    }

    public static float getxScale() {
        return xScale;
    }

    public static float getyScale() {
        return yScale;
    }


    public static void setY_offset(float y_offset) {
        PaintballFactory.y_offset = y_offset;
    }

    public static void setHeavy_density(float heavy_density) {
        PaintballFactory.heavy_density = heavy_density;
    }

    public static void setInitial_speed(float initial_speed) {
        PaintballFactory.initial_speed = initial_speed;
    }

    public static void setPaintballToPaintballDuration(float paintballToPaintballDuration) {
        PaintballFactory.paintballToPaintballDuration = paintballToPaintballDuration;
    }

    public static void setPaintballToWallDuration(float paintballToWallDuration) {
        PaintballFactory.paintballToWallDuration = paintballToWallDuration;
    }

    public static void setPaintballToPlatformDuration(float paintballToPlatformDuration) {
        PaintballFactory.paintballToPlatformDuration = paintballToPlatformDuration;
    }

    public static void setxScale(float xScale) {
        PaintballFactory.xScale = xScale;
    }

    public static void setMaxXScale(float maxXScale) {
        PaintballFactory.maxXScale = maxXScale;
    }

    public static void setyScale(float yScale) {
        PaintballFactory.yScale = yScale;
    }

    // END: Setters and Getters

    @Override
    public void applySettings() {
        maxXScale = Sidebar.getValue("Paintball Width");
        initial_speed = Sidebar.getValue("Paintball Speed");
        player_initial_speed = 2*Sidebar.getValue("Paintball Speed");
        yScale = Sidebar.getValue("Paintball Height");
        paintballToPaintballDuration = Sidebar.getValue("Paintball-paintball Stick Time");
        paintballToWallDuration = Sidebar.getValue("Paintball-Wall Stick Time");
        paintballToPlatformDuration = Sidebar.getValue("Paintball-Wall Stick Time");
    }

    @Override
    public void preLoadContent(AssetManager manager)
    {
        manager.load(Constants.PAINTBALL_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_STATIONARY_NORMAL_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_STATIONARY_CHAR_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_NORMAL_TRAIL_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_MINE_TRAIL_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_ENEMY_NORMAL_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_ENEMY_MINE_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_CHARACTER_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_SPLAT_EFFECT_FILE,Texture.class);
        manager.load(Constants.PAINTBALL_CHAR_SPLAT_EFFECT_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_ENEMY_SPLAT_EFFECT_FILE, Texture.class);
        manager.load(Constants.PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE, Texture.class);
    }

    @Override
    public void loadContent(AssetManager manager) {
        enemyTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_FILE, false);
        enemyTextureHead = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_ENEMY_NORMAL_FILE, false);
        enemyMineHeadTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_ENEMY_MINE_FILE, false);
        minePlatformTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_STATIONARY_MINE_FILE, false);
        charPlatformTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_STATIONARY_CHAR_FILE, false);
        mineTrailTexture =  AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_MINE_TRAIL_FILE, false);
        playerTextureHead = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_CHARACTER_FILE, false);
        normalPlatformTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_STATIONARY_NORMAL_FILE, false);
        splatEffectTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_SPLAT_EFFECT_FILE, false);
        trailTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_NORMAL_TRAIL_FILE, false);
        charSplatEffectTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_CHAR_SPLAT_EFFECT_FILE, false);
        enemySplatEffectTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_ENEMY_SPLAT_EFFECT_FILE, false);
        mineSplatEffectTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE, false);
        timerTexture = AssetRetriever.createTextureRegion(manager, Constants.PAINTBALL_TIMER_FILE, false);

    }

    @Override
    public void unloadContent(AssetManager manager) {
        if(manager.isLoaded(Constants.PAINTBALL_FILE)) {
            manager.unload(Constants.PAINTBALL_FILE);
            manager.unload(Constants.PAINTBALL_STATIONARY_NORMAL_FILE);
            manager.unload(Constants.PAINTBALL_STATIONARY_CHAR_FILE);
            manager.unload(Constants.PAINTBALL_STATIONARY_MINE_FILE);
            manager.unload(Constants.PAINTBALL_NORMAL_TRAIL_FILE);
            manager.unload(Constants.PAINTBALL_MINE_TRAIL_FILE);
            manager.unload(Constants.PAINTBALL_ENEMY_NORMAL_FILE);
            manager.unload(Constants.PAINTBALL_ENEMY_MINE_FILE);
            manager.unload(Constants.PAINTBALL_CHARACTER_FILE);
            manager.unload(Constants.PAINTBALL_SPLAT_EFFECT_FILE);
            manager.unload(Constants.PAINTBALL_CHAR_SPLAT_EFFECT_FILE);
            manager.unload(Constants.PAINTBALL_ENEMY_SPLAT_EFFECT_FILE);
            manager.unload(Constants.PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE);
            manager.unload(Constants.PAINTBALL_TIMER_FILE);
        }
    }
}