package edu.cornell.gdiac.game.entity.factories;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.entity.models.PaintballModel;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.util.AssetRetriever;

/**
 * Created by Lu on 3/17/2017.
 */
public class PaintballFactory implements AssetUser {
    /** Texture file */
    private static final String PAINTBALL_FILE = "character/paintball.png";
    /** Offset for Paintball when firing */
    private static float x_offset = 1.7f;
    private static float y_offset = 0.25f;
    /** The density for a Paintball */
    private static float heavy_density = 10.0f;
    /** The speed of the Paintball after firing */
    private static float initial_speed = 4.0f;
    /** Duration for paintball to paintball sticking**/
    private static float paintballToPaintballDuration = 5f;
    /** Duration for paintball to wall sticking**/
    private static float paintballToWallDuration = 3f;
    /** Duration for paintball to platform sticking**/
    private static float paintballToPlatformDuration = 3f;

    /**The starting width scale of the bullet**/
    private static float xScale =.5f;
    /**The maximum width scale of the bullet**/
    private static float maxXScale = 3f;
    /**The height scale of the bullet**/
    private static float yScale =.25f;

    /** The drawing scale of the Paintball*/
    private Vector2 scale;
    /** Texture of the paintball */
    private TextureRegion texture;

    public PaintballFactory(Vector2 scale){
        this.scale = scale;
    }

    /**
     *  TODO: write desc
     *  @param direction the traveling direction of the bullet (true for right, left otherwise)
     */
    public PaintballModel createPaintball(float x, float y, boolean direction){
        float xOffset = (direction ? x_offset : -x_offset);
        float width = texture.getRegionWidth()/(scale.x);
        float height = texture.getRegionHeight()/(scale.y);
        float speed  = (direction ? initial_speed : -initial_speed);
        PaintballModel paintball = new PaintballModel(x+xOffset, y+y_offset, width, height,speed,xScale,yScale,scale);
        paintball.setDensity(heavy_density);
        paintball.setDrawScale(scale);
        paintball.setTexture(texture);
        paintball.setPaintballToPaintballDuration(paintballToPaintballDuration);
        paintball.setPaintballToWallDuration(paintballToWallDuration);
        paintball.setPaintballToPlatformDuration(paintballToPlatformDuration);
        paintball.setMaxXScale(maxXScale);
        paintball.setBullet(true);
        paintball.setGravityScale(0);
        paintball.setFixedRotation(true);
        paintball.setVX(speed);

        return paintball;
    }

    public static float getHeavy_density() {
        return heavy_density;
    }

    public static float getX_offset() {
        return x_offset;
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

    public static void setX_offset(float x_offset) {
        PaintballFactory.x_offset = x_offset;
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

    @Override
    public void preLoadContent(AssetManager manager) {
        manager.load(PAINTBALL_FILE,Texture.class);
    }

    @Override
    public void loadContent(AssetManager manager) {
        texture = AssetRetriever.createTexture(manager, PAINTBALL_FILE, false);
    }

    @Override
    public void unloadContent(AssetManager manager) {
        if(manager.isLoaded(PAINTBALL_FILE))
            manager.unload(PAINTBALL_FILE);
    }
}
