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
    private static final float X_OFFSET = 1.7f;
    private static final float Y_OFFSET = 0.25f;
    /** The density for a Paintball */
    private static final float HEAVY_DENSITY = 10.0f;
    /** The speed of the Paintball after firing */
    private static final float INITIAL_SPEED = 3.2f;

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
        float xOffset = (direction ? X_OFFSET : -X_OFFSET);
        float width = texture.getRegionWidth()/(scale.x);
        float height = texture.getRegionHeight()/(scale.y);
        float speed  = (direction ? INITIAL_SPEED : -INITIAL_SPEED);
        PaintballModel paintball = new PaintballModel(x+xOffset, y+Y_OFFSET, width, height,speed,scale);
        paintball.setDensity(HEAVY_DENSITY);
        paintball.setDrawScale(scale);
        paintball.setTexture(texture);
        paintball.setBullet(true);
        paintball.setGravityScale(0);
        paintball.setFixedRotation(true);
        paintball.setVX(speed);

        return paintball;
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
