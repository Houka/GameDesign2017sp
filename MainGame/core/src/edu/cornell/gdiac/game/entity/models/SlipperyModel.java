package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * Created by Lu on 3/17/2017.
 * 
 * The model class for the platforms.
 */
public class SlipperyModel extends PlatformModel {
    //constants for the platforms
    /** Density of the platforms*/
    private static final float  BASIC_DENSITY = 0.0f;
    /** Friction of the platforms*/
    private static final float  SLIPPERY_FRICTION = 0.0f;
    /** "Bounciness" of the platforms*/
    private static final float  BASIC_RESTITUTION = 0.1f;
    private float[] pointArray;

    /**
     * Creates a new platform.
     *
     * The points is expressed in physics units NOT pixels. In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param points    Vertices outlining the platform. In form [x1, y1, x2, y2 ...]
     */
    public SlipperyModel(float[] points) {
        super(points);
        pointArray = points;
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(BASIC_DENSITY);
        setFriction(SLIPPERY_FRICTION);
        setRestitution(BASIC_RESTITUTION);
        setName("platform");
    }

    public float[] getPoints(){
        return pointArray;
    }
}
