package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by cece on 4/22/2017.
 */
public class SplattererModel extends BoxObstacle{


    public SplattererModel(float x, float y, float width, float height) {
        super(x,y,width,height);
        setDensity(0);
        setSensor(true);
        setGravityScale(0);
        setFixedRotation(true);
        setName("splatterer");

    }

    @Override
    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1.0f, 1.0f);
    }
}
