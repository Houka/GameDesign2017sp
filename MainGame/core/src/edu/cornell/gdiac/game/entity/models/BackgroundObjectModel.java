package edu.cornell.gdiac.game.entity.models;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by Lu on 3/17/2017.
 *
 * Model class for the background objects(like tutorial text)
 */
public class BackgroundObjectModel extends BoxObstacle {
    //constants for the walls
    /** Density*/
    private static final float  BASIC_DENSITY = 0.0f;
    /** Friction */
    private static final float  BASIC_FRICTION = 0.4f;
    /** "Bounciness"*/
    private static final float  BASIC_RESTITUTION = 0.1f;

    /**
     * Creates a new background object.
     *
     * The points is expressed in physics units NOT pixels. In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     */
    public BackgroundObjectModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(BASIC_DENSITY);
        setFriction(BASIC_FRICTION);
        setRestitution(BASIC_RESTITUTION);
        setSensor(true);
        setName("bgObject");
    }

}
