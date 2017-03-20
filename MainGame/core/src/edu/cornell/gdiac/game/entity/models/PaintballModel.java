package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by Lu on 2/27/2017.
 *
 * The model class for bullet objects
 *
 * TODO: make a PaintballModel not a WheelObstacle. It should be something that acts like a rectangular platform
 */
public class PaintballModel extends BoxObstacle {


    private float xScale;
    private float yScale;
    private float xtransform = 1f;
    private float ytransform = 1f;
    private float headSize = .2f;
    private float paintballToPaintballDuration;
    private float paintballToWallDuration;
    private float paintballToPlatformDuration;
    private float deathDuration = 1f;
    private float maxLifeTime;
    private float maxXScale;
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

    private Color paintcolor = Color.WHITE;

    /**
     *  TODO: write constructor desc
     */
    public PaintballModel(float x, float y, float w, float h, float s, float xScl, float yScl, Vector2 scl){
        super(x,y,w,h);
        setName("paintball");
        if(yScl == 0 || xScl == 0) {
            markRemoved(true);
            xScale = 1;
            yScale = 1;
            opacity = 0;
        } else {
            xScale = xScl;
            yScale = yScl;
            opacity = 1;
        }

        maxXScale = 6*xScale;
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
        gravity = false;
        maxLifeTime = 20f;
    }

    public void update(float delta) {
        if(xtransform<maxXScale && growing) {
            xtransform += delta*Math.abs(speed)*1.5f;
            this.setWidth(initWidth*xtransform);
            this.resize(getWidth(),getHeight());
            this.createFixtures();
        } else if(growing){
            //setVX(speed);
            growing = false;
        }
        if(maxLifeTime<deathDuration) {
            dying = true;
        }
        if(dying) {
            growing= false;
            timeToDie-=delta;
            if(timeToDie<deathDuration) {
                this.setMass(0);
                enableGravity();
                opacity *= .99;
            }
            if(timeToDie<0)
                markRemoved(true);
        }
        if(!gravity)
            this.setVY(0.0f);
        this.setVX(speed);
        maxLifeTime-=delta;
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

        //TODO Find better solution later
        paintcolor.a = 1;
        canvas.draw(texture, paintcolor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),0,0);
    }

    public void setTimeToDie(float xd) {
        if(!dying) {
            timeToDie = xd + deathDuration;
            dying = true;
            growing = false;
        }
    }

    public void enableGravity() {
        gravity = true;
        this.setGravityScale(1/3f);
        //this.setVY(-2); //Uncomment and comment above line for constant falling
    }


    public void fixX(float val) {
        speed=val;
    }

    public float getHeadSize() {
        return headSize;
    }

    public void setHeadSize(float headSize) {
        this.headSize = headSize;
    }

    public void setYScale(float val) {
        yScale=val;
    }
    public void setXScale(float val) {
        xScale=val;
    }
    public void setMaxXScale(float val) {
        maxXScale=val;
    }

    public void setPaintballToWallDuration(float paintballToWallDuration) {
        this.paintballToWallDuration = paintballToWallDuration;
    }

    public void setPaintballToPlatformDuration(float paintballToPlatformDuration) {
        this.paintballToPlatformDuration = paintballToPlatformDuration;
    }

    public void setPaintballToPaintballDuration(float paintballToPaintballDuration) {
        this.paintballToPaintballDuration = paintballToPaintballDuration;
    }

    public void setMaxLifeTime(float val) {
        maxLifeTime = val;
    }

    public float getTimeToDie() {
        return timeToDie-deathDuration;
    }
    public boolean isDying() {
        return dying;
    }
    public boolean isDead() {
        return dying && timeToDie<deathDuration;
    }
    public float getMaxLifeTime() {
        return maxLifeTime;
    }

    public float getYScale() {
        return yScale;
    }
    public float getXScale() {
        return yScale;
    }
    public float getMaxXScale() {
        return maxXScale;
    }
    public float getSpeed() {
        return speed;
    }

    public float getPaintballToPaintballDuration() {
        return paintballToPaintballDuration;
    }

    public float getPaintballToWallDuration() {
        return paintballToWallDuration;
    }

    public float getPaintballToPlatformDuration() {
        return paintballToPlatformDuration;
    }
}
