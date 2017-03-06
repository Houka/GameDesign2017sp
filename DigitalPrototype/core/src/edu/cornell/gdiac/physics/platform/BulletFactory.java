package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

/**
 * Created by Lu on 2/27/2017.
 *
 * Factory class that provides methods to create bullet objects and adds them to the world
 *
 * TODO: restrict bullets to only travel horizontally. Bug: when you jump and shoot, bullet has a slight y velocity
 */
public class BulletFactory {
    /** Offset for bullet when firing */
    private static final float  BULLET_OFFSET = 1.5f;
    private static final float  Y_OFFSET = .5f;
    /** The density for a bullet */
    private static final float  HEAVY_DENSITY = 10.0f;

    /** The speed of the bullet after firing */
    private static float  bullet_speed = 3.5f;

    /** The world that the bullets exists in */
    private WorldController world;
    /** The scale of the bullet*/
    private Vector2 scale;

    /** constructs the bullet factory
     *
     * @param w The world the bullets exists in
     * @param s the scale of the world to draw in
     */
    public BulletFactory(WorldController w, Vector2 s){
        world = w;
        scale = s;
    }

    /**
     * Add a new bullet to the world and send it in the right direction.
     *
     * @param isFacingRight True if the bullet is facing right, false otherwise
     * @param x the x spawn point of the bullet
     * @param y the y spawn point of the bullet
     * @param bulletTexture the bullet texture
     */
    public void createBullet(boolean isFacingRight, float x, float y, TextureRegion bulletTexture) {
        float xOffset = (isFacingRight ? BULLET_OFFSET : -BULLET_OFFSET);
        float width = bulletTexture.getRegionWidth()/(scale.x);
        float height = bulletTexture.getRegionHeight()/(scale.y);
        float speed  = (isFacingRight ? bullet_speed : -bullet_speed);
        BulletModel bullet = new BulletModel(x+xOffset, y+Y_OFFSET, width, height,speed,scale);
        bullet.setName("bullet");
        bullet.setDensity(HEAVY_DENSITY);
        bullet.setDrawScale(scale);
        bullet.setTexture(bulletTexture);
        bullet.setBullet(true);
        bullet.setGravityScale(0);
        bullet.setFixedRotation(true);
        // Compute position and velocity
        bullet.setVX(speed);
        world.addQueuedObject(bullet);
    }

    /**
     * Remove a new bullet from the world.
     *
     * @param  bullet   the bullet to remove
     */
    public void removeBullet(Obstacle bullet) {
        bullet.markRemoved(true);
    }

    public float getBulletSpeed() {
        return bullet_speed;
    }
    public void setBulletSpeed(float s) {
        bullet_speed = s;
    }
    public void collideWithWall(BulletModel bullet) {
        bullet.setVX(0.0f);
        bullet.setTimeToDie(5);
    }
}
