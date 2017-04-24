package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;

/**
 * Created by Lu on 3/17/2017.
 * 
 * The model class for the platforms.
 */
public class PlatformModel extends PolygonObstacle {
    //constants for the platforms
    public static final int NORMAL_PLATFORM = 0;
    public static final int SPIKE_PLATFORM = 1;

    /** Density of the platforms*/
    private static final float  BASIC_DENSITY = 0.0f;
    /** Friction of the platforms*/
    private static final float  BASIC_FRICTION = 0.4f;
    /** "Bounciness" of the platforms*/
    private static final float  BASIC_RESTITUTION = 0.1f;
    private float[] pointArray;
    private int type = 0;


    /**
     * Creates a new platform.
     *
     * The points is expressed in physics units NOT pixels. In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param points    Vertices outlining the platform. In form [x1, y1, x2, y2 ...]
     */
    public PlatformModel(float[] points, int type) {
        super(points);
        pointArray = points;
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(BASIC_DENSITY);
        setFriction(BASIC_FRICTION);
        setRestitution(BASIC_RESTITUTION);
        setName("platform");
        this.type = type;
    }

    // BEGIN: Setters and Getters
    public float[] getPoints(){ return pointArray; }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    // END: Setters and GEtters
}
