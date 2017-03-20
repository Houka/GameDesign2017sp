/*
 * DudeModel.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.interfaces.Shooter;
import edu.cornell.gdiac.util.obstacles.CapsuleObstacle;


/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class EnemyModel extends CapsuleObstacle implements Shooter {
    // Physics constants
    /** The density of the character */
    private static final float ENEMY_DENSITY = 500.0f;
    /** The player is a slippery one */
    private static final float ENEMY_FRICTION = 0.0f;

    public static final int SHOT_COOLDOWN = 75;

    // This is to fit the image to a tigher hitbox
    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float ENEMY_VSHRINK = 0.95f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float ENEMY_HSHRINK = 0.7f;
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
    /** How long until we can shoot again */
    private int shootCooldown;
    /** Whether we are actively shooting */
    private boolean isShooting;
    /** Which direction is the character facing */
    private boolean isFacingRight;
    /** If the enemy is OnSight or not */
    private boolean onSight;
    /** Interval that enemy shoots at (if interval) */
    private int interval = 0;
    /** Whether the enemy is stunned */
    private boolean stunned;


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
    public EnemyModel(float x, float y, float width, float height, boolean isFacingRight, boolean onSight, int interval) {
        super(x,y,width* ENEMY_HSHRINK,height* ENEMY_VSHRINK);
        setDensity(ENEMY_DENSITY);
        setFriction(ENEMY_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);
        setName("enemy");

        // Gameplay attributes
        isShooting = false;
        shootCooldown = 0;
        this.isFacingRight = isFacingRight;
        this.onSight = onSight;
        this.interval = interval;
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
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
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
    public boolean isOnSight() {
        return onSight;
    }

    public void setOnSight(boolean onSight) {
        this.onSight = onSight;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isStunned() {
        return stunned;
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    /**
     * Returns left/right movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @return left/right movement of this character.
     */
    public float getMovement() {
        return movement;
    }

    /**
     * Sets left/right movement of this character.
     *
     * This is the result of input times dude force.
     *
     * @param value left/right movement of this character.
     */
    public void setMovement(float value) {
        movement = value;
        // Change facing if appropriate
        if (movement < 0) {
            isFacingRight = false;
        } else if (movement > 0) {
            isFacingRight = true;
        }
    }

    @Override
    public boolean isFacingRight() {
        return isFacingRight;
    }

    /**
     * Sets facing direction of this character.
     *
     * @param value whether this character is facing right
     */
    public void setFacingRight(boolean value) {
        isFacingRight = value;
    }

    @Override
    public boolean isShooting() {return isShooting && shootCooldown <= 0;}

    @Override
    public void setShooting(boolean value) {isShooting = value;}

    public int getShootCooldown() {
        return shootCooldown;
    }

    public void setShootCooldown(int shootCooldown) {
        this.shootCooldown = shootCooldown;
    }

    // END: Setters and Getters

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     */
    public void update(float dt) { super.update(dt); }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        float xScale = isFacingRight ? 1.0f : -1.0f;
        canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),xScale,1.0f);
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
        canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }
}