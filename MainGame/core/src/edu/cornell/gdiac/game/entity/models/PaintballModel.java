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
 * The model class for paintball objects
 */
public class PaintballModel extends BoxObstacle {
    /** Scale of paintball**/
    private float xScale;
    private float yScale;

    /** Transformation of original scale **/
    private float xtransform = 1f;
    private float ytransform = 1f;

    /** Size of knockback portion of paintball**/
    private float headSize = .2f;

    /** Duration before death variables**/
    private float paintballToPaintballDuration;
    private float paintballToWallDuration;
    private float paintballToPlatformDuration;
    private float deathDuration = 1f;

    /** Time before bullet dies**/
    private float maxLifeTime;

    /** Maximum scale**/
    private float maxXScale;

    /** Opacity of paintball**/
    private float opacity;

    /** Starting width and height**/
    private float initWidth;
    private float initHeight;

    /** Starting speed**/
    private float speed;

    /** Is paintball growing?**/
    private boolean growing;
    /** Is paintball dying?**/
    private boolean dying;
    /** Is gravity enabled?**/
    boolean gravity;

    /** Counter from death until removal**/
    private float timeToDie;

    /** Screen scale**/
    private Vector2 scale;

    /** Paintball color**/
    private Color paintcolor = new Color(256f,256f,256,1f);

    /** Update time**/
    private float updateTime;
    /** Last update time**/
    private float lastUpdate;

    private boolean snapping;
    private float newX;
    private float newY;
    private float newW;
    private boolean updateReady;

    private float initY;
    private boolean recentCollision;
    private boolean recentCreation;
    private boolean isPlayerBullet;
    private boolean direction;

    /**
     * PaintballModel constructor
     * @param x         Starting x position
     * @param y         Starting y position
     * @param w         Starting width
     * @param h         Starting height
     * @param s         Starting speed
     * @param xScl      Starting x-scale
     * @param yScl      Starting y-scale
     * @param scl       Screen scale
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
        gravity = false;
        maxLifeTime = 20f;
        snapping = true;
        updateTime = .1f;
        lastUpdate = 0f;
        updateReady=false;
        initY = y;
        recentCollision = false;
        recentCreation = true;
        isPlayerBullet = false;
        direction = true;
    }

    //BEGIN: GETTERS AND SETTERS
    public boolean getDying() { return dying; }
    public boolean getGrowing() { return growing;}
    public void setGrowing(boolean value) { growing = value; }
    public boolean getDirection() { return direction; }
    public void setDirection(boolean dir) { direction = dir; }
    public void setTimeToDie(float xd) {
        if(!dying) {
            timeToDie = xd + deathDuration;
            dying = true;
            growing = false;
        }
    }

    public boolean isPlayerBullet(){
        return isPlayerBullet;
    }

    public void setPlayerBullet(boolean truth){
        isPlayerBullet = truth;
    }

    public float getScaledX() {
        return xScale*xtransform;
    }

    public float getScaledY() {
        return yScale*ytransform;
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

    public float getMaxLifeTime() {
        return maxLifeTime;
    }

    public float getYScale() {
        return yScale;
    }
    public float getXScale() {
        return xScale;
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

    public void markRecentCollision(){
        recentCollision = true;
    }

    public boolean getRecentCollision(){
        return recentCollision;
    }

    public boolean recentlyCreated() {
        return recentCreation;
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
    //END: GETTERS AND SETTERS

    /** Enable gravity**/
    public void enableGravity() {
        gravity = true;
        this.setGravityScale(1/3f);
       // this.setVY(-2); //Uncomment and comment above line for constant falling
    }

    /**
     * Fix X velocity
     * @param val       x velocity
     */
    public void fixX(float val) {
        speed=val;
    }

    /**
     * Set the time until paintball expires
     * @param val       Time until death
     */
    public void setMaxLifeTime(float val) {
        maxLifeTime = val;
    }

    /**
     * Set width to this new width
     * @param w  New width
     */
    public void newWidth(float w) {
        this.setWidth(w);
        this.resize(getWidth(),getHeight());
        this.createFixtures();
    }

    public void newSize(float x, float y, float w) {
        newX = x;
        newY = y;
        newW = w;
        updateReady=true;
    }

    public void instakill() {
        dying = true;
        timeToDie = 0;
        markRemoved(true);
    }

    @Override
    public void update(float delta) {
        lastUpdate +=delta;
        recentCollision = false;
        recentCreation = false;

        if(updateReady) {
            newWidth(newW);
            xtransform = newW/initWidth;
            setPosition(newX,newY);
            updateReady=false;
        }

        if(xtransform<maxXScale && growing) {
            xtransform += delta*Math.abs(speed)*1.5f;
            if(lastUpdate>updateTime) {
                newWidth(initWidth*xtransform);
                lastUpdate = 0;
            }
        } else if(growing){
            //setVX(speed);
            newWidth(initWidth*maxXScale);
            growing = false;
        }
        if(maxLifeTime<deathDuration) {
            dying = true;
        }
        if(dying) {
            growing= false;
            timeToDie-=delta;
            if(timeToDie<deathDuration) {
                snapping=false;
                this.setMass(0);
                enableGravity();
                opacity *= .99;
            }
            if(timeToDie<0)
                markRemoved(true);
        }
        if(!gravity) {
            this.setY(initY);
            this.setVY(0.0f);
        }

        this.setVX(speed);
        maxLifeTime-=delta;
        if(snapping)
            setPosition(getPosition().x,snapToGrid(getPosition().y));

    }

    @Override
    public void draw(GameCanvas canvas) {
        paintcolor.a = opacity;
        if (texture != null) {
            canvas.draw(texture, paintcolor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),getScaledX(),getScaledY());
        }

        //TODO Find better solution later
        paintcolor.a = 1;
        canvas.draw(texture, paintcolor,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),0,0);
    }

    private float snapToGrid(float yVal) {
        yVal =(float) Math.floor(yVal/getHeight()/2f)*getHeight()*2;
        return yVal + getHeight();
    }
}
