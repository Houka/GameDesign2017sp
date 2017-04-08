package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.factories.PaintballFactory;
import edu.cornell.gdiac.game.interfaces.Animatable;
import edu.cornell.gdiac.game.interfaces.Settable;
import edu.cornell.gdiac.game.interfaces.Shooter;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.CapsuleObstacle;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class PlayerModel extends CapsuleObstacle implements Shooter, Settable, Animatable {
    // Physics constants
    /** The density of the character */
    private static final float PLAYER_DENSITY = 1.0f;
    /** The factor to multiply by the input */
    private static final float PLAYER_FORCE = 50.0f;
    /** The player is a slippery one */
    private static final float PLAYER_FRICTION = 0.0f;
    /** The maximum character speed */
    private static final float PLAYER_MAXSPEED = 5.0f;
    /** The impulse for the character jump */
    private static final float PLAYER_JUMP = 5.5f;
    /** Cooldown (in animation frames) for jumping */
    private static final int JUMP_COOLDOWN = 10;
    /** Cooldown (in animation frames) for shooting */
    private static final int SHOOT_COOLDOWN = 50;
    /** Height of the sensor attached to the player's feet */
    private static final float SENSOR_HEIGHT = 0.05f;
    /** Identifier to allow us to track the sensor in ContactListener */
    private static final String SENSOR_NAME = "PlayerGroundSensor";

    // This is to fit the image to a tigher hitbox
    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float PLAYER_VSHRINK = 1f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float PLAYER_HSHRINK = 1f;
    /** The amount to shrink the sensor fixture (horizontally) relative to the image */
    private static final float PLAYER_SSHRINK = 0.6f;

    /** The current max speed of the player */
    private float maxSpeed;
    /** The current horizontal movement of the character */
    private float   movement;
    /** Which direction is the character facing */
    private boolean isFacingRight;
    /** How long until we can jump again */
    private int jumpCooldown;
    /** The current impulse of the jump */
    private float jumpForce;
    /** Whether we are getting knocked back*/
    private boolean isKnockedBack;
    /** Direction of said knockback**/
    private Vector2 knockbackDirection;
    /** Force of said knockback**/
    private float knockbackForce;
    private float knockbackFriction = .9f;
    private float knockbackDuration = 60;
    private float knockbackStunDuration = 5;
    private float defaultKnockbackDuration = 60;

    /** Whether we getting knockedBack jumping */
    private boolean isJumping;
    /** How long until we can shoot again */
    private int shootCooldown;
    /** Whether our feet are on the ground */
    private boolean isGrounded;
    /** Whether we've used our double jump */
    private boolean canDoubleJump;
    /** Whether we are actively shooting */
    private boolean isShooting;
    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;
    private PolygonShape sensorShape;

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();
    private Vector2 zeroVector = new Vector2(0,0);

    /** The animation associated with this entity */
    private Animation animation;

    /**
     * Creates a new player avatar at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public PlayerModel(float width, float height) {
        this(0,0,width,height);
    }

    /**
     * Creates a new player avatar at the given position.
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
    public PlayerModel(float x, float y, float width, float height) {

        super(x,y,width*PLAYER_HSHRINK,height*PLAYER_VSHRINK);
        setDensity(PLAYER_DENSITY);
        setFriction(PLAYER_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);
        setName("player");

        // Gameplay attributes
        isGrounded = false;
        isShooting = false;
        isJumping = false;
        isKnockedBack = false;
        canDoubleJump = false;
        isFacingRight = true;

        shootCooldown = 0;
        jumpCooldown = 0;
        jumpForce = PLAYER_JUMP;
        maxSpeed = PLAYER_MAXSPEED;

        knockbackForce = 0;
        knockbackDirection = new Vector2(0,0);
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

    /**
     * Returns left/right movement of this character.
     *
     * This is the result of input times player force.
     *
     * @return left/right movement of this character.
     */
    public float getMovement() {
        return movement;
    }

    /**
     * Sets left/right movement of this character.
     *
     * This is the result of input times player force.
     *
     * @param value left/right movement of this character.
     */
    public void setMovement(float value) {
        movement = value*PLAYER_FORCE;
        // Change facing if appropriate
        if (movement < 0) {
            isFacingRight = false;
        } else if (movement > 0) {
            isFacingRight = true;
        }
    }

    @Override
    public boolean isShooting() {
        return isShooting && shootCooldown <= 0;
    }

    @Override
    public void setShooting(boolean value) {
        isShooting = value;
    }

    /**
     * Returns true if the player is actively jumping.
     *
     * @return true if the player is actively jumping.
     */
    public boolean isJumping() {
        return isJumping && isGrounded && jumpCooldown <= 0;
    }
  
    /**
     * Returns true if the player is actively double jumping.
     *
     * @return true if the player is actively jumping.
     */
    public boolean isDoubleJumping(){ 
        return isJumping && !isGrounded && canDoubleJump; 
    }

    public boolean isKnockedBack() {
        return isKnockedBack && knockbackDuration > defaultKnockbackDuration-Math.max(defaultKnockbackDuration,knockbackStunDuration);
    }

    public void setKnockedBack(float dir){


        if(dir==0) {
            isKnockedBack=false;
            return;
        }

        if(isKnockedBack)
            return;

       isKnockedBack=true;
        knockbackDuration = defaultKnockbackDuration;
        if(dir>0)
            knockbackDirection.set(1,0);
        else
            knockbackDirection.set(-1,0);
    }

    /**
     * Sets whether the player is actively jumping.
     *
     * @param value whether the player is actively jumping.
     */
    public void setJumping(boolean value) {
        isJumping = value;
    }

    /** Set jump force to change the height of the jump. */
    public void setJumpForce(float value) {
        jumpForce = value;
    }

    /**
     * Sets the jump cooldown time.
     */
    public void setJumpCooldown(int value) {
        jumpCooldown = value;
    }

    /**
     * Sets whether the player can double jump.
     *
     * @param value whether the player can double jump.
     */
    public void setCanDoubleJump(boolean value) {
        canDoubleJump = value;
    }

    /**
     * Returns true if the player is on the ground.
     *
     * @return true if the player is on the ground.
     */
    public boolean isGrounded() {
        return isGrounded;
    }

    /**
     * Sets whether the player is on the ground.
     *
     * @param value whether the player is on the ground.
     */
    public void setGrounded(boolean value) {
        isGrounded = value;
    }

    /**
     * Returns the upper limit on player left-right movement.
     *
     * This does NOT apply to vertical movement.
     *
     * @return the upper limit on player left-right movement.
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Sets the player's max speed.
     *
     * @param value how fast the player can move.
     */
    public void setMaxSpeed(float value) {
        maxSpeed = value;
    }

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

    @Override
    public boolean isFacingRight() {
        return isFacingRight;
    }

    // END: Setters and Getters

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
        // We only allow the player to jump when he's on the ground.
        // Double jumping is not allowed.
        //
        // To determine whether or not the player is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = PLAYER_DENSITY;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(PLAYER_SSHRINK*getWidth()/2.0f, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        return true;
    }

    //TODO: find better solution for riding a bullet
    private PaintballModel ridingBullet = null;
    public void setRidingVX(PaintballModel b){
        ridingBullet = b;
    }

    /** Applies forces to the player*/
    public void applyForce() {
        if (!isActive()) {
            return;
        }
      
        boolean stunned = false;

        if(isKnockedBack()) {
            stunned = true;

            if(knockbackDuration>0) {
                forceCache.set(knockbackDirection.x * knockbackForce, knockbackDirection.y * knockbackForce); //TODO: use trig if we ever want y knockback
                body.applyLinearImpulse(forceCache, getPosition(), true);
            }

            if (knockbackDuration < 0) {
                setVX(getVX() * knockbackFriction);
              //  if (Math.abs(getVX()) < .01)
                    //stunned = false;

            }
        }
      
        // TODO: find better solution for riding a bullet
        if (ridingBullet!=null) {
            if(!stunned){
              // Don't want to be moving. Damp out player motion
              if (getMovement() == 0f ) {
                  setVX(ridingBullet.getVX());
              }else{
                  setVX(Math.signum(getMovement())*getMaxSpeed()+ridingBullet.getVX());
              }
            }
        }else{
          if(!stunned){
            if (getMovement() == 0f ) {
                setVX(0);
            }else{
                setVX(Math.signum(getMovement())*getMaxSpeed());
            }
          }
        }

        // Jump!
        if (isJumping() && !stunned) {
            forceCache.set(0, jumpForce);
            body.applyLinearImpulse(forceCache,getPosition(),true);
            setCanDoubleJump(true);
        }
        if (isDoubleJumping() && !stunned) {
            //dividing by sqrt 2 makes it such that from 0 velocity it goes half the height of a regular jump
            forceCache.set(0, jumpForce/((float)Math.sqrt(2)));
            //set velocity to 0 so that the jump height is independent of how the model is moving
            setLinearVelocity(zeroVector);
            body.applyLinearImpulse(forceCache,getPosition(),true);
            setCanDoubleJump(false);
        }
    }

    @Override
    public void applySettings() {
        jumpForce = Sidebar.getValue("Jump Height");
        knockbackForce = Sidebar.getValue("Knockback Force");
        defaultKnockbackDuration = Sidebar.getValue("Knockback Duration");
        knockbackStunDuration = Sidebar.getValue("Knockback Stun Duration");
        knockbackFriction = 1-Sidebar.getValue("Knockback Friction");
        maxSpeed = Sidebar.getValue("Player Speed");
    }

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        // Apply cooldowns
        if (isJumping()) {
            jumpCooldown = JUMP_COOLDOWN;
        } else {
            jumpCooldown = Math.max(0, jumpCooldown - 1);
        }

        if (isShooting()) {
            shootCooldown = SHOOT_COOLDOWN;
        } else {
            shootCooldown = Math.max(0, shootCooldown - 1);
        }

        if (isKnockedBack()) {
            knockbackDuration = knockbackDuration - 1;
        } else {
            isKnockedBack=false;
        }

        super.update(dt);
        animation.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        float effect = isFacingRight ? 1.0f : -1.0f;

        if (animation == null)
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
        else
            canvas.draw(animation.getTextureRegion(),Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
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