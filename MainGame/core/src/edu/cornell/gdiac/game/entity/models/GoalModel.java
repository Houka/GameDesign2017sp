package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by Lu on 3/17/2017.
 *
 * This class is the model for the target that the player must shoot in order to win. 
 */
public class GoalModel extends BoxObstacle {

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
     */
    public GoalModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0.0f);
        setFriction(0.0f);
        setRestitution(0.0f);
        setSensor(true);
        setName("goal");
    }
}
