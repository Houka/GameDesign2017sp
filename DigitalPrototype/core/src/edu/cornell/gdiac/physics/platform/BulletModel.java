package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

/**
 * Created by Lu on 2/27/2017.
 *
 * The model class for bullet objects
 *
 * TODO: make a BulletModel not a WheelObstacle. It should be something that acts like a rectangular platform
 */
public class BulletModel extends BoxObstacle {


    private float xScale = .5f;
    private float yScale = .5f;
    private float xtransform = 1f;
    private float ytransform = 1f;

    private float maxXScale = 3f;
    private float initWidth;
    private float initHeight;
    private float speed;
    private boolean growing;
    private Vector2 scale;


    /** Constructor for a bullet*/
    public BulletModel(float x, float y){
        super(x, y);
        initWidth = x;
        initHeight = y;
        growing = true;
    }

    public BulletModel(float x, float y, float w, float h, float s, Vector2 scl){
        super(x,y,w,h);
        initWidth = w*xScale;
        initHeight = h*yScale;
        setWidth(initWidth);
        setHeight(initHeight);
        resize(getWidth(),getHeight());
        createFixtures();
        speed = s;
        growing = true;
        scale = scl;
    }

    public void update(float delta) {
        if(xtransform<maxXScale) {
            xtransform += delta*speed*speed;
            this.setWidth(initWidth*xtransform);
            this.resize(getWidth(),getHeight());
            this.createFixtures();
        } else if(growing){
            //setVX(speed);
            growing = false;
        }
        this.setVY(0);
    }

    private float getScaledX() {
        return xScale*xtransform;
    }

    private float getScaledY() {
        return yScale*ytransform;
    }

    public void draw(GameCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),getScaledX(),getScaledY());
        }
    }
}
