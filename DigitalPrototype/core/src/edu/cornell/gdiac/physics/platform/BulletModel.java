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
    private float yScale = .25f;
    private float xtransform = 1f;
    private float ytransform = 1f;

    private float maxXScale = 3f;
    private float myVY;
    private float opacity;
    private float initWidth;
    private float initHeight;
    private float speed;
    private boolean growing;
    private boolean dying;
    private float timeToDie;
    private Vector2 scale;
    boolean gravity;

    private Color paintcolor = Color.RED;


    /** Constructor for a bullet*/
    public BulletModel(float x, float y){
        super(x, y);
        initWidth = x;
        initHeight = y;
        growing = true;
        dying = false;
        myVY=0;
        opacity = 1;
        gravity = false;
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
        dying = false;
        scale = scl;
        myVY = 0;
        opacity = 1;
        gravity = false;
    }

    public void update(float delta) {
        if(xtransform<maxXScale) {
            xtransform += delta*Math.abs(speed)*1.5f;
            this.setWidth(initWidth*xtransform);
            this.resize(getWidth(),getHeight());
            this.createFixtures();
        } else if(growing){
            //setVX(speed);
            growing = false;
        }
        if(dying) {
            timeToDie-=delta;
            if(timeToDie<1) {
                enableGravity();
                opacity *= .99;
            }
            if(timeToDie<0)
                markRemoved(true);
        }
        if(!gravity)
            this.setVY(0.0f);
    }

    private float getScaledX() {
        return xScale*xtransform;
    }

    private float getScaledY() {
        return yScale*ytransform;
    }

    public void draw(GameCanvas canvas) {
        paintcolor.a = opacity;
        if (texture != null) {
            canvas.draw(texture, paintcolor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),getScaledX(),getScaledY());
        }
    }

    public void setTimeToDie(float xd) {
        if(!dying) {
            timeToDie = xd;
            dying = true;
        }
    }

    public void enableGravity() {
        gravity = true;
        this.setGravityScale(1/3f);
        //this.setVY(-2); //Uncomment and comment above line for constant falling
    }
}
