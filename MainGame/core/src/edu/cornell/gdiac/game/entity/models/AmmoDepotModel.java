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
 * Model class for ammo depots.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class AmmoDepotModel extends BoxObstacle {
    /** The amount to shrink the body fixture (vertically) relative to the image */
    private static final float AMMO_DEPOT_VSHRINK = 0.95f;
    /** The amount to shrink the body fixture (horizontally) relative to the image */
    private static final float AMMO_DEPOT_HSHRINK = 0.7f;
    /** The amount of cooldown to wait for before allowing ammo depots to be used again **/
    private static final int USED_COOLDOWN = 500;

    /** The amount of ammo that this ammo depot provides on contact**/
    private int ammoAmount;
    /** Whether or not the ammo depot has been used already**/
    private boolean used;
    /** Whether or not the ammo depot has been used already**/
    private int usedCooldown;

    /**
     * Creates a new Ammo Depot at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the center
     * @param y  		Initial y position of the center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     * @param ammoAmount How much ammo the player will receive from this
     */
    public AmmoDepotModel(float x, float y, float width, float height, int ammoAmount) {
        super(x,y,width* AMMO_DEPOT_HSHRINK,height*AMMO_DEPOT_VSHRINK);
        setDensity(0);
        setSensor(true);
        setBodyType(BodyDef.BodyType.StaticBody);
        setGravityScale(0);
        setFixedRotation(true);
        setName("ammoDepot");

        // Gameplay attributes
        this.ammoAmount = ammoAmount;
        this.used = false;
        this.usedCooldown = 0;
    }

    // BEGIN: Setters and Getters
    /**
     * Returns the max amount of ammo that the depot provides
     *
     * @return the amount of ammo
     */
    public int getAmmoAmount() { return ammoAmount; }

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

    // END: Setters and Getters

    @Override
    public void update(float dt) {
        super.update(dt);
        if(isUsed() && usedCooldown == 0)
            usedCooldown = USED_COOLDOWN;
        if(usedCooldown > 0)
            usedCooldown --;
        if(usedCooldown == 0)
            setUsed(false);
    }

    @Override
    public void draw(GameCanvas canvas) {
        if (!isUsed())
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1.0f,1.0f);
    }
}
