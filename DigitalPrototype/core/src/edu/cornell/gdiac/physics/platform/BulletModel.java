package edu.cornell.gdiac.physics.platform;

import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

/**
 * Created by Lu on 2/27/2017.
 *
 * The model class for bullet objects
 *
 * TODO: make a BulletModel not a WheelObstacle. It should be something that acts like a rectangular platform
 */
public class BulletModel extends WheelObstacle {

    /** Constructor for a bullet*/
    public BulletModel(float x, float y, float radius){
        super(x, y, radius);
    }
}
