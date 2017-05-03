package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.factories.PaintballFactory;
import edu.cornell.gdiac.game.interfaces.Animatable;
import edu.cornell.gdiac.game.interfaces.Settable;
import edu.cornell.gdiac.game.interfaces.Shooter;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;
import edu.cornell.gdiac.util.obstacles.CapsuleObstacle;
import edu.cornell.gdiac.util.obstacles.ComplexObstacle;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Player avatar for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class PlayerModel extends PolygonObstacle implements Shooter, Settable, Animatable {
    // Physics constants
    /** The density of the character */
    private static final float PLAYER_DENSITY = 0.0f;
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
    private static final int SHOOT_COOLDOWN = 30;
    /** Height of the sensor attached to the player's feet */
    private static final float SENSOR_HEIGHT = 0.05f;
    /** Identifier to allow us to track the sensor in ContactListener */
    private static final String SENSOR_NAME = "PlayerGroundSensor";
    private static final String RUNNING_SENSOR_NAME = "PlayerGroundRunningSensor";
    /** Ratio of jump force to double jump force */
    private static final float DOUBLE_JUMP_MULTIPLIER = 1.2f;
    /** Mass of the player */
    private static final float PLAYER_MASS = 40f;

    // This is to fit the image to a tigher hitbox
    /** The amount to shrink  head space of the texture to remove*/
    private static final float PLAYER_HEAD_SPACE= .15f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float PLAYER_HSHRINK = 0.325f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float PLAYER_HSHRINK_RUNNING = 0.7f;
    /** The amount to shrink the sensor fixture (horizontally) relative to the image */
    private static final float PLAYER_SSHRINK = 0.75f;
    private static final float PLAYER_RUNNING_SSHRINK = 0.95f;
    /** The amount to shrink the feet relative to the top */
    private static final float PLAYER_FOOTSHRINK = .75f;
    private static final float PLAYER_RUNNING_FOOTSHRINK = .95f;
    private static final float PLAYER_HEELSHRINK = .97f;
    /** The position in physics units where the sensor ground should be at*/
    private float sensorX = 0f;
    /** The maximum Y velocity we let the player jump at (in case of some slight bouncing)*/

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
    /** Whether we are crouching*/
    private boolean isCrouching;
    /** Direction of said knockback**/
    private Vector2 knockbackDirection;
    /** Force of said knockback**/
    private float knockbackForce;
    private float knockbackFriction = .9f;
    private float knockbackDuration = 60;
    private float knockbackStunDuration = 5;
    private float defaultKnockbackDuration = 60;

    private static final float FREE_JUMP_FRAMES = 9;
    private float freeJumpFrame;

    /** Duration that player will pass through bullets**/
    private float passThroughDuration;
    private final float GO_THROUGH_TIME = 0.5f;

    /** Whether we getting knockedBack jumping */
    public boolean isJumping;
    /** How long until we can shoot again */
    private int shootCooldown;
    /** Whether our feet are on the ground */
    private boolean isGrounded;
    /** If we are on player */
    private boolean isTrampGrounded;
    /** Whether we've used our double jump */
    private boolean canDoubleJump;
    /** Whether we are actively shooting */
    private boolean isShooting;
    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;
    private Fixture runningSensorFixture;
    private PolygonShape sensorShape;
    private PolygonShape runningSensorShape;

    /** Fixtures for different hitboxes*/
    private Fixture playerFixture;
    private Fixture crouchFixture;
    private Fixture runningFixture;
    private PolygonShape playerShape;
    private PolygonShape crouchShape;
    private PolygonShape runningShape;

    private float playerHeight;
    private float playerWidth;

    /**How many frames ago was the last grounding*/
    private float lastGrounding;

    /** Cache for internal force calculations */
    private Vector2 forceCache = new Vector2();
    private Vector2 zeroVector = new Vector2(0,0);

    /** The animation associated with this entity */
    private Animation animation;
    /** The color associated with this entity */
    private Color drawColor;

    /** Store hitboxes depending on state of the player*/
    private float[] defaultBox;
    private float[] runningBox;
    private float[] crouchingBox;

    private PaintballModel myPlatform;

    private ObjectSet sensorObjects;
    private ObjectSet runningSensorObjects;

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
        super(
                new float[]{
                        -width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, -height/2.0f,
                        -width/2.0f*PLAYER_HSHRINK, height/2.0f - 2.5f*height*PLAYER_HEAD_SPACE,
                        width/2.0f*PLAYER_HSHRINK, height/2.0f - 2.5f*height*PLAYER_HEAD_SPACE,
                        width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, -height/2.0f
                },
                x,y);
        defaultBox = new float[]{
                -width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, -height/2.0f,
                width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, -height/2.0f,
                -width/2.0f*PLAYER_HSHRINK,0,
                width/2.0f*PLAYER_HSHRINK, 0,
                width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, height/2.0f - height*PLAYER_HEAD_SPACE,
                -width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, height/2.0f - height*PLAYER_HEAD_SPACE,
                -width/2.0f*PLAYER_HSHRINK*PLAYER_HEELSHRINK, -height/2.0f+SENSOR_HEIGHT,
                width/2.0f*PLAYER_HSHRINK*PLAYER_HEELSHRINK, -height/2.0f+SENSOR_HEIGHT
        };
        runningBox = new float[]{
                -width/2.0f*PLAYER_HSHRINK_RUNNING*PLAYER_RUNNING_FOOTSHRINK, -height/2.0f,
                width/2.0f*PLAYER_HSHRINK_RUNNING*PLAYER_RUNNING_FOOTSHRINK, -height/2.0f,
                -width/2.0f*PLAYER_HSHRINK_RUNNING,0,
                width/2.0f*PLAYER_HSHRINK_RUNNING, 0,
                width/2.0f*PLAYER_HSHRINK_RUNNING*PLAYER_RUNNING_FOOTSHRINK, height/2.0f - 2f*height*PLAYER_HEAD_SPACE,
                -width/2.0f*PLAYER_HSHRINK_RUNNING*PLAYER_RUNNING_FOOTSHRINK, height/2.0f - 2f*height*PLAYER_HEAD_SPACE,
                -width/2.0f*PLAYER_HSHRINK_RUNNING*PLAYER_RUNNING_FOOTSHRINK, -height/2.0f+SENSOR_HEIGHT,
                width/2.0f*PLAYER_HSHRINK_RUNNING*PLAYER_RUNNING_FOOTSHRINK, -height/2.0f+SENSOR_HEIGHT
        };
        crouchingBox = new float[]{
                -width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, -height/2.0f,
                -width/2.0f*PLAYER_HSHRINK, height/2.0f - 2.6f*height*PLAYER_HEAD_SPACE,
                width/2.0f*PLAYER_HSHRINK, height/2.0f - 2.6f*height*PLAYER_HEAD_SPACE,
                width/2.0f*PLAYER_HSHRINK*PLAYER_FOOTSHRINK, -height/2.0f
        };
        drawColor = new Color(256f,256f,256f,1f);

        playerWidth = width;
        playerHeight = height;

        setDensity(PLAYER_DENSITY);
        setFriction(PLAYER_FRICTION);  /// HE WILL STICK TO WALLS IF YOU FORGET
        setFixedRotation(true);
        setName("player");
        setMass(PLAYER_MASS);
        sensorX = -height/2;

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
        freeJumpFrame = 0;

        knockbackForce = 0;
        knockbackDirection = new Vector2(0,0);

        myPlatform = null;
        lastGrounding=0;

        sensorObjects = new ObjectSet();
        runningSensorObjects = new ObjectSet();
    }

    // BEGIN: Setters and Getters
    public float getPlayerJump() {
        return Sidebar.getValue("Jump Height");
    }

    private float getMaxDiff(float a, float b, float c) {
        return Math.max(Math.max(Math.abs(a-b),Math.abs(b-c)),Math.abs(a-c));
    }

    @Override
    public float getHeight(){
        if(crouchFixture != null && fixtureIsActive(crouchFixture.getUserData()))
            return getMaxDiff(crouchingBox[1],crouchingBox[3],crouchingBox[5]);
        if(runningFixture != null && fixtureIsActive(runningFixture.getUserData()))
            return getMaxDiff(runningBox[1],runningBox[3],runningBox[5]);
        return playerHeight;
    }
    @Override
    public float getWidth(){
        if(crouchFixture != null && fixtureIsActive(crouchFixture.getUserData()))
            return getMaxDiff(crouchingBox[0],crouchingBox[2],crouchingBox[4]);
        if(runningFixture != null && fixtureIsActive(runningFixture.getUserData()))
            return getMaxDiff(runningBox[0],runningBox[2],runningBox[4]);
        return playerWidth;
    }

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

        if (isCrouching()){
            movement = 0;
        }
    }

    @Override
    public boolean isShooting() {
        return isShooting && shootCooldown <= 0 && !isCrouching();
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
        return isJumping && (isGrounded || freeJumpFrame>0) && jumpCooldown <= 0;
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

    public boolean isCrouching(){
        return isGrounded() && isCrouching;
    }

    public void setKnockedBack(float dir){

        if(dir==0) {
            isKnockedBack=false;
            return;
        }

        if(isKnockedBack)
            return;

        passThroughDuration = GO_THROUGH_TIME;
        canDoubleJump = false;
        setVY(0.0f);

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
        if(isGrounded() && value == false)
            freeJumpFrame = FREE_JUMP_FRAMES;
        if(value == true)
            lastGrounding = 0;
        isGrounded = value;
    }

    public void setCrouching(boolean value) {
        isCrouching = value;
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
    public String getRunningSensorName() {
        return RUNNING_SENSOR_NAME;
    }
    public boolean isGroundSensor(Object s) { return SENSOR_NAME.equals(s) || RUNNING_SENSOR_NAME.equals(s);}

    public boolean isGhosting() {
        return passThroughDuration>0;
    }

    public boolean recentlyGrounded(){return lastGrounding<1;}
    public boolean semirecentlyUngrounded(){return freeJumpFrame>0;}
    @Override
    public boolean isFacingRight() {
        return isFacingRight;
    }

    public boolean isTrampGrounded() { return isTrampGrounded; }
    public void setTrampGrounded(boolean value) { isTrampGrounded = value; }

    public void setMyPlatform(PaintballModel p) {
        myPlatform = p;
    }

    public boolean fixtureIsActive(Object fixData) {
        if(fixData == null)
            return false;

        if(fixData.equals(sensorFixture.getUserData())) {
            if (animation.getCurrentStrip().equals("run"))
                return false;
            else
                return true;
        }

        if(fixData.equals(runningSensorFixture.getUserData())) {
            if (animation.getCurrentStrip().equals("run"))
                return true;
            else
                return false;
        }

        if(animation.getCurrentStrip().equals("crouch")) {
            if(fixData.equals(crouchFixture.getUserData()))
                return true;
        } else if (animation.getCurrentStrip().equals("run")){
            if(fixData.equals(runningFixture.getUserData()))
                return true;
        } else if (fixData.equals(playerFixture.getUserData())) {
            return true;
        }
        return false;
    }

    // END: Setters and Getters

    public boolean addSensorCollision(Object a, Object b) {
        if(a.equals(sensorFixture.getUserData())) {
            sensorObjects.add(b);
            return true;
        }
        if(a.equals(runningSensorFixture.getUserData())) {
            runningSensorObjects.add(b);
            return true;
        }
        return false;

    }

    public boolean removeSensorCollision(Object a, Object b) {
        if(a.equals(sensorFixture.getUserData())) {
            sensorObjects.remove(b);
            return true;
        }
        if(a.equals(runningSensorFixture.getUserData())) {
            runningSensorObjects.remove(b);
            return true;
        }
        return false;
    }

    public boolean isColliding() {
        return (sensorObjects.size!=0 && fixtureIsActive(sensorFixture.getUserData())) ||
                (runningSensorObjects.size!=0 && fixtureIsActive(runningSensorFixture.getUserData()));
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
        // We only allow the player to jump when he's on the ground.
        // Double jumping is not allowed.
        //
        // To determine whether or not the player is on the ground,
        // we create a thin sensor under his feet, which reports
        // collisions with the world but has no collision response.
        Vector2 sensorCenter = new Vector2(0, sensorX);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = PLAYER_DENSITY;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(PLAYER_SSHRINK*super.getWidth()/2.0f, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        runningSensorShape = new PolygonShape();
        runningSensorShape.setAsBox(PLAYER_RUNNING_SSHRINK*playerWidth/2.0f*PLAYER_HSHRINK_RUNNING, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = runningSensorShape;

        runningSensorFixture = body.createFixture(sensorDef);
        runningSensorFixture.setUserData(RUNNING_SENSOR_NAME);

        //player default and crouching hitboxes
        FixtureDef playerDef = new FixtureDef();
        playerDef.density = PLAYER_DENSITY;
        playerDef.friction = PLAYER_FRICTION;
        playerShape = new PolygonShape();
        playerShape.set(defaultBox);
        playerDef.shape = playerShape;

        playerFixture = body.createFixture(playerDef);
        playerFixture.setUserData("Default hitbox");

        FixtureDef crouchDef = new FixtureDef();
        crouchDef.density = PLAYER_DENSITY;
        crouchDef.friction = PLAYER_FRICTION;
        crouchShape = new PolygonShape();
        crouchShape.set(crouchingBox);
        crouchDef.shape = crouchShape;

        crouchFixture = body.createFixture(crouchDef);
        crouchFixture.setUserData("Crouching hitbox");

        FixtureDef runningDef = new FixtureDef();
        runningDef.density = PLAYER_DENSITY;
        runningDef.friction = PLAYER_FRICTION;
        runningShape = new PolygonShape();
        runningShape.set(runningBox);
        runningDef.shape = runningShape;

        runningFixture = body.createFixture(runningDef);
        runningFixture.setUserData("Running hitbox");

        return true;
    }

    //TODO: find better solution for riding a bullet
    private PaintballModel ridingBullet = null;
    public void setRidingVX(PaintballModel b){
        ridingBullet = b;
    }
    public PaintballModel getRidingBullet(){return ridingBullet;}

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

        if (isDoubleJumping() && !stunned) {
            //dividing by sqrt 2 makes it such that from 0 velocity it goes half the height of a regular jump
            forceCache.set(0, jumpForce/(DOUBLE_JUMP_MULTIPLIER));
            //set velocity to 0 so that the jump height is independent of how the model is moving
            setLinearVelocity(zeroVector);
            body.applyLinearImpulse(forceCache,getPosition(),true);
            setCanDoubleJump(false);
        }

        // Jump!
        if (isJumping() && !stunned) {
            float mod = 1.0f;
            if(isTrampGrounded) {
                mod = 2.0f;
                if(myPlatform!=null) {
                    myPlatform.instakill();
                    myPlatform = null;
                }
            }
            if(freeJumpFrame>0)
                mod*=.2;
            forceCache.set(0, mod*jumpForce);
            body.applyLinearImpulse(forceCache,getPosition(),true);
            setCanDoubleJump(true);
            freeJumpFrame = 0;
            jumpCooldown = JUMP_COOLDOWN;
        }

        lastGrounding=Math.min(lastGrounding,Float.MAX_VALUE-1)+1;

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
            freeJumpFrame = 0;
        } else {
            jumpCooldown = Math.max(0, jumpCooldown - 1);
            freeJumpFrame=Math.max(0,freeJumpFrame-1);
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

        if(isGhosting())
            passThroughDuration= Math.max(passThroughDuration-dt,0);

        if(!isGrounded)
            ridingBullet=null;

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

        drawColor.a = isGhosting() ? .6f : 1.0f;

        if (animation == null)
            canvas.draw(texture,drawColor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
        else
            canvas.draw(animation.getTextureRegion(),drawColor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),effect,1.0f);
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
        if (sensorShape != null) {
            if(fixtureIsActive(crouchFixture.getUserData()))
                canvas.drawPhysics(crouchShape, Color.PINK, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
            if(fixtureIsActive(playerFixture.getUserData()))
                canvas.drawPhysics(playerShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
            if(fixtureIsActive(runningFixture.getUserData())) {
                canvas.drawPhysics(runningShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
                canvas.drawPhysics(runningSensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);
            } else {
                canvas.drawPhysics(sensorShape, Color.RED, getX(), getY(), getAngle(), drawScale.x, drawScale.y);

            }
        }
    }
}