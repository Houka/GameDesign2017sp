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
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Enemy avatar for game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class EnemyModel extends CapsuleObstacle {
    // Physics constants
    /** The density of the character */
    private static final float ENEMY_DENSITY = 500.0f;
    /** Cooldown (in animation frames) for shooting */
    private static final int SHOOT_COOLDOWN = 200;
    /** Height of the sensor attached to the player's feet */

    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float ENEMY_VSHRINK = 0.95f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float ENEMY_HSHRINK = 0.7f;



    /** Which direction is the character facing */
    private boolean faceRight;
    /** How long until we can shoot again */
    private int shootCooldown;
    /** Whether we are actively shooting */
    private boolean isShooting;
    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;
    private PolygonShape sensorShape;
    /** The type of the current enemy (onSight or interval) */
    private boolean onSight;
    /** AIController for this enemy */
    private AIController aiController;
    /** The enemy interval (null if type is onSight) */
    private int interval = 0;

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
     * Returns id of this enemy.
     *
     * @return id of this enemy
     */
    public int getID() {
        return enemyID;
    }

    /**
     * Returns type of this enemy.
     *
     * @return type of this enemy
     */
    public boolean getOnSight() {
        return onSight;
    }

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
     * Returns true if this character is facing right
     *
     * @return true if this character is facing right
     */
    public boolean isFacingRight() {
        return faceRight;
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
     * @param type      The type of the enemy (onSight or interval)
     */
    public EnemyModel(float x, float y, float width, float height, boolean type, boolean faceRight, DudeModel avatar) {
        super(x,y,width*ENEMY_HSHRINK,height*ENEMY_VSHRINK);
        setDensity(ENEMY_DENSITY);
        setFixedRotation(true);

        // Gameplay attributes
        isShooting = false;

        shootCooldown = 0;
        this.aiController = new AIController(this, avatar, 200);
        setName("ENEMY"+enemyID);
        enemyID++;
        this.onSight = type;
        this.faceRight = faceRight;

        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = 1f;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.6f*getWidth()/2.0f, 0.05f, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;
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

    public AIController getAiController() {
        return aiController;
    }

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
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
        canvas.draw(texture, Color.RED,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
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
