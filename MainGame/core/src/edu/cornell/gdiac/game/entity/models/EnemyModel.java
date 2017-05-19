package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.interfaces.Animatable;
import edu.cornell.gdiac.game.interfaces.Shooter;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;


/**
 * Enemy avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class EnemyModel extends PolygonObstacle implements Shooter, Animatable {
    // Physics constants
    /** The density of the character */
    private static final float ENEMY_DENSITY = 500.0f;
    /** How slippery the enemy is */
    private static final float ENEMY_FRICTION = 0.0f;

    /** Cooldown constants */
    private static final int DEFAULT_SHOOT_COOLDOWN = 75;
    private static final int DEFAULT_STUN_COOLDOWN = 300;

    // This is to fit the image to a tigher hitbox
    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float ENEMY_VSHRINK = 0.95f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float ENEMY_HSHRINK = 0.6f;
    /** The amount to shrink the sensor fixture (horizontally) relative to the image */
    private static final float ENEMY_SSHRINK = 0.6f;
    /** Height of the sensor attached to the player's feet */
    private static final float SENSOR_HEIGHT = 0.05f;
    /** Identifier to allow us to track the sensor in ContactListener */
    private static final String SENSOR_NAME = "EnemyGroundSensor";
    /** Width of the sensor, made small to make bottom hitbox smaller*/
    private static final float SENSOR_WIDTH = 0.1f;
    /** Offset for hitbox for upper part of arm*/
    private static final float ABOVE_ARM_SHRINK = 0.1f;
    /** Offset for hitbox for lower part of arm*/
    private static final float BELOW_ARM = 0.1f;
    /** Offset for hitbox for where the poster ends below*/
    private static final float BELOW_POSTER = 0.3f;

    private static final float VERTICAL_OFFSET = 1f;

    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;  // TODO: use sensors as ways to detect the ends of platforms (sensors in front/behind entity)
    private PolygonShape sensorShape;

    /** The current horizontal movement of the character */
    private float movement;
    /** Keeps track of how long until we can shoot again */
    private int shootCooldownCounter;
    private int stunCooldownCounter;
    /** How long we need to wait until we can shoot again */
    private int shootCooldown;
    private int stunCooldown;
    /** Whether we are actively shooting */
    private boolean isShooting;
    /** Which direction is the character facing */
    private boolean isFacingRight;
    /** If the enemy is OnSight or not */
    private boolean onSight;
    /** The type of enemy, i.e. what kind of bullet it shoots*/
    private String enemyType;

    /** The animation associated with this entity */
    private Animation animation;

    private Color drawColor;

    /**
     * Returns the name of the ground sensor
     *
     * This is used by ContactListener
     *
     * @return the name of the ground sensor
     */
    public String getSensorName() {
        return SENSOR_NAME;
    }

    /**
     * Creates a new ENEMY avatar at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the avatar center
     * @param y  		Initial y position of the avatar center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     * @param isFacingRight Whether or not the enemy is facing right
     */
    public EnemyModel(float x, float y, float width, float height, boolean isFacingRight, boolean onSight, int interval, String enemyType) {
        super(
                isFacingRight ?
                new float[]{
                        -width/2.0f, -height*BELOW_POSTER,
                        -width/2.0f, height/2.0f,
                        0, height/2.0f,
                        0, height*ABOVE_ARM_SHRINK,
                        width/2.0f, height*ABOVE_ARM_SHRINK,
                        width/2.0f, -height*BELOW_ARM,
                        0, -height*BELOW_ARM,
                        0, -height*BELOW_POSTER,
                } :
                new float[]{
                        0, -height*BELOW_ARM,
                        -width/2.0f, -height*BELOW_ARM,
                        -width/2.0f, height*ABOVE_ARM_SHRINK,
                        0, height*ABOVE_ARM_SHRINK,
                        0, height/2.0f,
                        width/2.0f, height/2.0f,
                        width/2.0f, -height*BELOW_POSTER,
                        0, -height*BELOW_POSTER
                },
                x,y);
        setDensity(ENEMY_DENSITY);
        setFriction(ENEMY_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);
        setName("enemy");
        setEnemyType(enemyType);
        setSensor(true);
        bodyinfo.type = BodyDef.BodyType.StaticBody;

        // Gameplay attributes
        isShooting = false;
        shootCooldownCounter = 0;
        stunCooldownCounter = 0;
        this.isFacingRight = isFacingRight;
        this.onSight = onSight;
        shootCooldown = onSight? DEFAULT_SHOOT_COOLDOWN : interval;
        stunCooldown = DEFAULT_STUN_COOLDOWN;

        this.drawColor = new Color(255,255,255,1);
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }

        return true;
    }

    // BEGIN: Setters and Getters
    @Override
    public void setTexture(TextureRegion region){
        super.setTexture(region);
        texture = region;
        origin.set(region.getRegionWidth()/2.0f, region.getRegionHeight()/2.0f);
    }

    @Override
    public void setAnimation(Animation animation){
        this.animation = animation;
    }

    @Override
    public Animation getAnimation(){
        return animation;
    }

    public boolean isOnSight() {
        return onSight;
    }

    public int getInterval() { return shootCooldown; }

    public void setOnSight(boolean onSight) {
        this.onSight = onSight;
    }

    public boolean isStunned() { return stunCooldownCounter > 0; }

    public void setStunned(boolean value) { stunCooldownCounter = value? stunCooldown : 0; }

    @Override
    public boolean isFacingRight() {
        return isFacingRight;
    }

    @Override
    public boolean isShooting() {return isShooting && shootCooldownCounter <= 0 && !isStunned();}

    public int getShootCooldownCounter(){return shootCooldownCounter;}

    @Override
    public void setShooting(boolean value) { isShooting = value; }

    public String getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(String type) {
        enemyType = type;
    }

    // END: Setters and Getters

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     */
    public void update(float dt) {
        super.update(dt);

        if (isShooting())
            shootCooldownCounter = shootCooldown;
        else
            shootCooldownCounter = Math.max(0, shootCooldownCounter - 1);

        if (stunCooldownCounter > 0)
            stunCooldownCounter --;

        animation.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        drawColor.a = isStunned() ? 0.6f : 1f;
        float xScale = isFacingRight ? 1.0f : -1.0f;

        if (animation == null)
            canvas.draw(texture,drawColor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),xScale,1.0f);
        else
            canvas.draw(animation.getTextureRegion(),drawColor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),xScale,1.0f);
    }

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        if (sensorShape != null)
            canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }
}