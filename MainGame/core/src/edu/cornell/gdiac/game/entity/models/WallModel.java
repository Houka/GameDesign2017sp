package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;

/**
 * Created by Lu on 3/17/2017.
 */
public class WallModel extends PolygonObstacle {
    private static final float  BASIC_DENSITY = 0.0f;
    private static final float  BASIC_FRICTION = 0.4f;
    private static final float  BASIC_RESTITUTION = 0.1f;

    public WallModel(float[] points) {
        super(points);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(BASIC_DENSITY);
        setFriction(BASIC_FRICTION);
        setRestitution(BASIC_RESTITUTION);
        setName("wall");
    }
}
