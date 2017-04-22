package edu.cornell.gdiac.game.entity.models;

import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by cece on 4/22/2017.
 */
public class SplattererModel extends BoxObstacle{

    public SplattererModel(float x, float y, float width, float height) {
        super(x,y,width,height);
        setName("splatterer");
    }
}
