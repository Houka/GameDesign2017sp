package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;

/**
 * Created by Lu on 3/17/2017.
 * 
 * The model class for the platforms.
 */
public class PlatformModel extends PolygonObstacle {
    //constants for the platforms
    /** Density of the platforms*/
    private static final float  BASIC_DENSITY = 0.0f;
    /** Friction of the platforms*/
    private static final float  BASIC_FRICTION = 0.4f;
    /** "Bounciness" of the platforms*/
    private static final float  BASIC_RESTITUTION = 0.1f;

    /**
     * Creates a new platform.
     *
     * The points is expressed in physics units NOT pixels. In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param points    Vertices outlining the platform. In form [x1, y1, x2, y2 ...]
     */
    public PlatformModel(float[] points) {
        super(points);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(BASIC_DENSITY);
        setFriction(BASIC_FRICTION);
        setRestitution(BASIC_RESTITUTION);
        setName("platform");
    }
}
