package edu.cornell.gdiac.physics.platform;

import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

/**
 * Created by Lu on 2/27/2017.
 *
 * The model class for bullet objects
 *
 * TODO: make a BulletModel not a WheelObstacle. It should be something that acts like a rectangular platform
 */
public class BulletModel extends BoxObstacle {

    /** Constructor for a bullet*/
    public BulletModel(float x, float y){
        super(x, y);
    }

    public BulletModel(float x, float y, float w, float h){
        super(x, y,w,h);
    }
}
