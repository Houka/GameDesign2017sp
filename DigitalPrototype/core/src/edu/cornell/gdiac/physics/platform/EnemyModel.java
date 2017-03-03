/*
 * EnemyModel.java
 * Created by Ashton Cooper on 2/28/17.
 */
package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;

/**
 * Enemy avatar for game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class EnemyModel extends CapsuleObstacle {
    // Physics constants
    /** The density of the character */
    private static final float ENEMY_DENSITY = 1.0f;
    /** Cooldown (in animation frames) for shooting */
    private static final int SHOOT_COOLDOWN = 40;
    /** Height of the sensor attached to the player's feet */

    //private static final float SENSOR_HEIGHT = 0.05f;
    /** Identifier to allow us to track the sensor in ContactListener */
    //private static final String SENSOR_NAME = "EnemyGroundSensor";
    /** The amount to slow the character down */
    //private static final float ENEMY_DAMPING = 10.0f;
    /** The ENEMY is a slippery one */
    //private static final float ENEMY_FRICTION = 0.0f;
    /** The maximum character speed */
    //private static final float ENEMY_MAXSPEED = 5.0f;
    /** The impulse for the character jump */
    //private static final float ENEMY_JUMP = 5.5f;
    /** Cooldown (in animation frames) for jumping */
    //private static final int JUMP_COOLDOWN = 30;
    /** The factor to multiply by the input */
    //private static final float ENEMY_FORCE = 20.0f;


    // This is to fit the image to a tigher hitbox
    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float ENEMY_VSHRINK = 0.95f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float ENEMY_HSHRINK = 0.7f;

    /** The amount to shrink the sensor fixture (horizontally) relative to the image */
    //private static final float ENEMY_SSHRINK = 0.6f;


    /** Which direction is the character facing */
    private boolean faceRight;
    /** How long until we can shoot again */
    private int shootCooldown;
    /** Whether we are actively shooting */
    private boolean isShooting;
    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;
    private PolygonShape sensorShape;
    /** The id of the current enemy */
    private int enemyID = 0;
    /** The current horizontal movement of the character */
    //private float movement;
    /** Whether our feet are on the ground */
    //private boolean isGrounded;
    /** How long until we can jump again */
    //private int jumpCooldown;
    /** Whether we are actively jumping */
    //private boolean isJumping;

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();

    /**
     * Returns left/right movement of this character.
     *
     * This is the result of input times ENEMY force.
     *
     * @return left/right movement of this character.
     */
    /*public float getMovement() {
        return movement;
    }*/

    /**
     * Sets left/right movement of this character.
     *
     * This is the result of input times ENEMY force.
     *
     * @param value left/right movement of this character.
     */
    /*public void setMovement(float value) {
        movement = value;
        // Change facing if appropriate
        if (movement < 0) {
            faceRight = false;
        } else if (movement > 0) {
            faceRight = true;
        }
    }*/

    /**
     * Returns true if the ENEMY is actively firing.
     *
     * @return true if the ENEMY is actively firing.
     */
    public boolean isShooting() {
        return isShooting && shootCooldown <= 0;
    }

    /**
     * Sets whether the ENEMY is actively firing.
     *
     * @param value whether the ENEMY is actively firing.
     */
    public void setShooting(boolean value) {
        isShooting = value;
    }

    /**
     * Returns true if the ENEMY is actively jumping.
     *
     * @return true if the ENEMY is actively jumping.
     */
    /*public boolean isJumping() {
        return isJumping && isGrounded && jumpCooldown <= 0;
    }*/

    /**
     * Sets whether the ENEMY is actively jumping.
     *
     * @param value whether the ENEMY is actively jumping.
     */
    /*public void setJumping(boolean value) {
        isJumping = value;
    }*/

    /**
     * Returns true if the ENEMY is on the ground.
     *
     * @return true if the ENEMY is on the ground.
     */
    /*public boolean isGrounded() {
        return isGrounded;
    }*/

    /**
     * Sets whether the ENEMY is on the ground.
     *
     * @param value whether the ENEMY is on the ground.
     */
    /*public void setGrounded(boolean value) {
        isGrounded = value;
    }*/

    /**
     * Returns how much force to apply to get the ENEMY moving
     *
     * Multiply this by the input to get the movement value.
     *
     * @return how much force to apply to get the ENEMY moving
     */
    /*public float getForce() {
        return ENEMY_FORCE;
    }*/

    /**
     * Returns ow hard the brakes are applied to get a ENEMY to stop moving
     *
     * @return ow hard the brakes are applied to get a ENEMY to stop moving
     */
    /*public float getDamping() {
        return ENEMY_DAMPING;
    }*/

    /**
     * Returns the upper limit on ENEMY left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on ENEMY left-right movement.
     */
    /*public float getMaxSpeed() {
        return ENEMY_MAXSPEED;
    }*/

    /**
     * Returns the name of the ground sensor
     *
     * This is used by ContactListener
     *
     * @return the name of the ground sensor
     */
    /*public String getSensorName() {
        return SENSOR_NAME;
    }*/

    /**
     * Returns true if this character is facing right
     *
     * @return true if this character is facing right
     */
    public boolean isFacingRight() {
        return faceRight;
    }

    /**
     * Creates a new ENEMY at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public EnemyModel(float width, float height) {
        this(0,0,width,height);
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
     */
    public EnemyModel(float x, float y, float width, float height) {
        super(x,y,width*ENEMY_HSHRINK,height*ENEMY_VSHRINK);
        setDensity(ENEMY_DENSITY);
        //setFriction(ENEMY_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);

        // Gameplay attributes
        //isGrounded = false;
        isShooting = false;
        //isJumping = false;
        faceRight = true;

        shootCooldown = 0;
        //jumpCooldown = 0;
        setName("ENEMY"+enemyID);
        enemyID++;
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

        // Ground Sensor
        // -------------
        // We only allow the ENEMY to jump when he's on the ground.
        // Double jumping is not allowed.
        //
        // To determine whether or not the ENEMY is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        /*Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = ENEMY_DENSITY;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(ENEMY_SSHRINK*getWidth()/2.0f, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());*/

        return true;
    }


    /**
     * Applies the force to the body of this ENEMY
     *
     * This method should be called after the force attribute is set.
     */
    /*public void applyForce() {
        if (!isActive()) {
            return;
        }

        // Don't want to be moving. Damp out player motion
        if (getMovement() == 0f) {
            forceCache.set(-getDamping()*getVX(),0);
            body.applyForce(forceCache,getPosition(),true);
        }

        // Velocity too high, clamp it
        if (Math.abs(getVX()) >= getMaxSpeed()) {
            setVX(Math.signum(getVX())*getMaxSpeed());
        } else {
            forceCache.set(getMovement(),0);
            body.applyForce(forceCache,getPosition(),true);
        }

        // Jump!
        if (isJumping()) {
            forceCache.set(0, ENEMY_JUMP);
            body.applyLinearImpulse(forceCache,getPosition(),true);
        }
    }*/

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        // Apply cooldowns
        /*if (isJumping()) {
            jumpCooldown = JUMP_COOLDOWN;
        } else {
            jumpCooldown = Math.max(0, jumpCooldown - 1);
        }*/

        if (isShooting()) {
            shootCooldown = SHOOT_COOLDOWN;
        } else {
            shootCooldown = Math.max(0, shootCooldown - 1);
        }

        super.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        float effect = faceRight ? 1.0f : -1.0f;
        canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
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
