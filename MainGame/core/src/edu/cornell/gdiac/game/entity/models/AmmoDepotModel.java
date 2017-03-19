/*
 * AmmoDepotModel.java
 *
 *
 * Author: Ashton Cooper 3/18/2017
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;


/**
 * AmmoDepot for the plaform game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class AmmoDepotModel extends BoxObstacle {

    // This is to fit the image to a tighter hitbox
    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float AMMO_DEPOT_VSHRINK = 0.95f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float AMMO_DEPOT_HSHRINK = 0.7f;
    /** The amount to shrink the sensor fixture (horizontally) relative to the image */
    private static final float AMMO_DEPOT_SSHRINK = 0.6f;


    /** Height of the sensor attached to the bottom of the ammo depot */
    private static final float SENSOR_HEIGHT = 0.05f;
    /** Identifier to allow us to track the sensor in ContactListener */
    private static final String SENSOR_NAME = "AmmoDepotGroundSensor";
    /** Ground sensor to represent our feet */
    private Fixture sensorFixture;
    private PolygonShape sensorShape;

    /** The amount of ammo that this ammo depot provides on contact**/
    private int ammoAmount;
    /** Whether or not the ammo depot has been used already**/
    private boolean used;
//    /** Whether or not the player can pick this ammo depot up**/
//    private boolean canPickUp;


    // Getters and Setters
    /**
     * Returns the max amount of ammo that the depot provides
     *
     * @return the amount of ammo
     */
    public int getAmmoAmount() { return ammoAmount; }

    /**
     * Sets the amount that the ammo depot provides
     *
     * @param newAmmoAmount new amount of ammo to be set
     */
    public void setAmmoAmount(int newAmmoAmount) {
        this.ammoAmount = newAmmoAmount;
    }

    /**
     * Returns if the ammo depot has already been picked up
     *
     * @return whether the ammo has been picked up
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Sets the ammo depot to used if it has been picked up
     *
     * @param used true if the ammo depot was picked up, false otherwise
     */
    public void setUsed(boolean used) {
        this.used = used;
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

    /**
     * Creates a new Ammo Depot at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the avatar center
     * @param y  		Initial y position of the avatar center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     * @param ammoAmount How much ammo the player will receive from this
     */
    public AmmoDepotModel(float x, float y, float width, float height, int ammoAmount) {
        super(x,y,width* AMMO_DEPOT_HSHRINK,height*AMMO_DEPOT_VSHRINK);
        setDensity(0);
        setSensor(true);
        setGravityScale(0);
        setFixedRotation(true);
        setName("ammoDepot");

        // Gameplay attributes
        this.ammoAmount = ammoAmount;
//        canPickUp = true;
        this.used = false;
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

        /*// Ground Fixture
        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
        FixtureDef sensorDef = new FixtureDef();
        sensorDef.density = 0;
        sensorDef.isSensor = true;
        sensorShape = new PolygonShape();
        sensorShape.setAsBox(AMMO_DEPOT_SSHRINK *getWidth()/2.0f, SENSOR_HEIGHT, sensorCenter, 0.0f);
        sensorDef.shape = sensorShape;

        sensorFixture = body.createFixture(sensorDef);
        sensorFixture.setUserData(getSensorName());

        return true;*/
    }

    // BEGIN: Setters and Getters


    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     */
    public void update(float dt) {
        super.update(dt);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1.0f,1.0f);
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
