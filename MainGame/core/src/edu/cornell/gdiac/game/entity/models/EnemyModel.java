package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.interfaces.Animatable;
import edu.cornell.gdiac.game.interfaces.Shooter;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.CapsuleObstacle;


/**
 * Enemy avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class EnemyModel extends CapsuleObstacle implements Shooter, Animatable {
    // Physics constants
    /** The density of the character */
    private static final float ENEMY_DENSITY = 500.0f;
    /** How slippery the enemy is */
    private static final float ENEMY_FRICTION = 0.0f;

    /** Cooldown constants */
    private static final int DEFAULT_SHOOT_COOLDOWN = 75;
    private static final int DEFAULT_STUN_COOLDOWN = 500;

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
    public EnemyModel(float x, float y, float width, float height, boolean isFacingRight, boolean onSight,
                      int interval, String enemyType) {
        super(x,y,width* ENEMY_HSHRINK,height* ENEMY_VSHRINK);
        setDensity(ENEMY_DENSITY);
        setFriction(ENEMY_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);
        setName("enemy");
        setEnemyType(enemyType);

        // Gameplay attributes
        isShooting = false;
        shootCooldownCounter = 0;
        stunCooldownCounter = 0;
        this.isFacingRight = isFacingRight;
        this.onSight = onSight;
        shootCooldown = onSight? DEFAULT_SHOOT_COOLDOWN : interval;
        stunCooldown = DEFAULT_STUN_COOLDOWN;
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

        // Ground Fixture
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2 );
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = ENEMY_DENSITY;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(ENEMY_SSHRINK *getWidth()/2.0f, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        return true;
    }

    // BEGIN: Setters and Getters
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
        float xScale = isFacingRight ? 1.0f : -1.0f;

        if (animation == null)
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),xScale,1.0f);
        else
            canvas.draw(animation.getTextureRegion(),Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),xScale,1.0f);
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