package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.Animation;
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
  
    /** How much the paintballs snap. The larger the value, the more dramatic the snapping effect */
    private static final int GRID_SNAP = 2;
  
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

    private float initDir;

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
    private float initX;
    private boolean recentCollision;
    private boolean recentCreation;
    private boolean isPlayerBullet;
    private boolean direction;
    private boolean trailEnabled;
    private boolean passThrough;
    private boolean popped;
    private boolean platformPopped;

    private Animation headTexture;
    private Animation splatEffectTexture;
    private Animation trailTexture;
    private TextureRegion platformTexture;
    private Animation platformSplatEffectTexture;

    private int currTrailFrame;

    private Vector2 platformOrigin;

    /** If the paintball is being stood on*/
    private boolean isUsed;

    /** Type of paintball */
    private String paintballType;

    private static final float FLASHING_RATE = .2f;
    private static final float FLASHING_TIME = 1.5f;

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
    public PaintballModel(float x, float y, float w, float h, float s, float xScl, float yScl, Vector2 scl, String type){
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
        popped = false;
        platformPopped = false;
        scale = scl;
        gravity = false;
        maxLifeTime = 20f;
//        snapping = false;
        updateTime = .25f;

        lastUpdate = 0f;
        updateReady=false;
        initY = y;
        initX = x;
        initDir = speed>0 ? 1f : -1f;
        recentCollision = false;
        recentCreation = true;
        isPlayerBullet = false;
        paintballType = type;
        isUsed = false;
        headTexture = new Animation();
        splatEffectTexture = new Animation();
        platformSplatEffectTexture = new Animation();
        trailTexture = new Animation();
        trailEnabled=true;
        currTrailFrame = 0;
        passThrough = false;
        platformOrigin=new Vector2();
        direction = true;
    }

    //BEGIN: GETTERS AND SETTERS
    public boolean getDying() { return dying; }
    public boolean getGrowing() { return growing;}
    public void setGrowing(boolean value) { growing = value; }
    public boolean getDirection() { return direction; }
    public void setDirection(boolean dir) { direction = dir; }
    public boolean isUsed() {
        return isUsed;
    }
    public void setUsed(boolean value) {
        isUsed = value;
    }
    public void setTimeToDie(float xd) {
        fixX(0);
        if(!dying) {
            timeToDie = xd + deathDuration;
            dying = true;
            growing = false;
            platformOrigin.x=origin.x-texture.getRegionWidth()/2f + platformTexture.getRegionWidth()/2f;
            platformOrigin.y=origin.y;
        }
    }

    public boolean isPlayerBullet(){
        return isPlayerBullet;
    }

    public void setPlayerBullet(boolean truth){
        isPlayerBullet = truth;
        trailEnabled=false;
        growing=false;
        passThrough = true;
        xtransform*=-1; //TODO maybe add actual fixture
    }

    public float getScaledX() {
        return xScale*xtransform;
    }

    public float getScaledY() {
        return yScale*ytransform;
    }

    public float getScaledPlatformX() {
        return xScale*xtransform*texture.getRegionWidth() / platformTexture.getRegionWidth()*(176f/127f);
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

    public void setHeadTexture(TextureRegion tex) {
        headTexture.addTexture("head",tex.getTexture(),1, Constants.PAINTBALL_TRAIL_COLUMNS);
        headTexture.play("head",true);
    }

    public void setPlatformTexture(TextureRegion tex) {
        platformTexture = tex;
    }

    public void setSplatEffectTexture(TextureRegion tex) {
       splatEffectTexture.addTexture("splat",tex.getTexture(),1,10);
    }

    public void setPlatformSplatEffectTexture(TextureRegion tex) {
        platformSplatEffectTexture.addTexture("platform splat", tex.getTexture(),1, 15);
    }

    public void setTrailTexture(TextureRegion tex) {
        trailTexture.addTexture("trail",tex.getTexture(),1,5);
        trailTexture.playOnce("trail");
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
    public float getMaxScaledX() {
        return maxXScale*xScale;
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

    public String getPaintballType() { return paintballType; }
    public void setPaintballType(String type) { paintballType = type; }
    public boolean canPassThrough() {
        return passThrough;
    }
    public void setPassThrough(boolean val) {
        passThrough = val;
    }
  
    //END: GETTERS AND SETTERS

    /** Enable gravity**/
    public void enableGravity() {
        gravity = true;
        this.setGravityScale(1/3f);
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

    public boolean isPopped(){return popped;}
    public boolean isPlatformPopped(){return platformPopped;}

    public void pop() {
        popped = true;
        timeToDie = 0;
        fixX(0);
        splatEffectTexture.playOnce("splat");
    }

    public void platformPop() {
        platformPopped = true;
        fixX(0);
        platformSplatEffectTexture.playOnce("platform splat");
    }

    public void snap(){
        setPosition(getPosition().x,snapToGrid(getPosition().y));
    }

    @Override
    public void update(float delta) {
        lastUpdate +=delta;
        recentCollision = false;
        recentCreation = false;

        if(popped){
            releaseFixtures();
            if(!splatEffectTexture.isPlaying())
                markRemoved(true);
        }

        if(updateReady) {
            newWidth(newW);
            xtransform = newW/initWidth;
            setPosition(newX,newY);
            updateReady=false;
        }

        if(xtransform<maxXScale && growing) {
            xtransform = initDir*2*(getX()-initX)-initWidth; //The right way is +initWidth but I like how this looks
            if(lastUpdate>updateTime) {
                newWidth(initWidth*xtransform);
                lastUpdate = 0;
            }
        } else if(growing){
            xtransform = maxXScale;
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
                if(opacity==1)
                    opacity = .6f;
                this.setMass(0);
                opacity *= .97;
            } else if (timeToDie<deathDuration+FLASHING_TIME){
                if(((int)(timeToDie/FLASHING_RATE))%2 == 0)
                    opacity = .75f;
                else
                    opacity = 1;
            }

            if(timeToDie<0)
                markRemoved(true);
        }
        if(!gravity) {
            this.setY(initY);
            this.setVY(0.0f);
        }

        if(growing)
            setVX(speed/1.5f);
        else
            setVX(speed);

        maxLifeTime-=delta;
        if(snapping)
            snap();

        headTexture.update(delta);
        splatEffectTexture.update(delta);
        platformSplatEffectTexture.update(delta);
        if((int)(xtransform/maxXScale*Constants.PAINTBALL_TRAIL_COLUMNS)>currTrailFrame && trailTexture.isPlaying()) {
            trailTexture.advanceFrame();
            currTrailFrame++;
        }
    }

    @Override
    public void draw(GameCanvas canvas) {
        paintcolor.a = opacity;
        if (platformSplatEffectTexture.getTextureRegion() != null && platformPopped) {
            float xPos = (getX() + getWidth()/2f*initDir) * drawScale.x - initDir*platformSplatEffectTexture.getTextureRegion().getRegionWidth()/4f;
            float yPos = getY()*drawScale.y-platformSplatEffectTexture.getTextureRegion().getRegionHeight()*getScaledY()/2f;
            canvas.draw(platformSplatEffectTexture.getTextureRegion(), paintcolor, origin.x, origin.y, xPos,yPos, getAngle(), -initDir, 1.0f);
        }
        if(!popped) {
            if (dying) {
                float vscale = (texture.getRegionHeight() * getScaledY()) / platformTexture.getRegionHeight();
                canvas.draw(platformTexture, paintcolor, platformOrigin.x, platformOrigin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), getScaledPlatformX(), 1/vscale);
            } else {
                if (texture != null && trailEnabled) {
                    float xPos = getX();
                    // canvas.draw(texture, paintcolor, origin.x, origin.y, xPos * drawScale.x, getY() * drawScale.y, getAngle(), getScaledX(), getScaledY());

                    if (trailTexture.getTextureRegion() != null && xtransform > 0) {
                        xPos = (initX) * drawScale.x + initDir * trailTexture.getTextureRegion().getRegionWidth() / 2f;
                        if (xPos * initDir < getX() * drawScale.x * initDir)
                            xPos = getX() * drawScale.x;
                        float hscale = (texture.getRegionWidth() * getMaxScaledX()) / trailTexture.getTextureRegion().getRegionWidth();
                        float vscale = (texture.getRegionHeight() * getScaledY()) / trailTexture.getTextureRegion().getRegionHeight();
                        canvas.draw(trailTexture.getTextureRegion(), paintcolor, trailTexture.getTextureRegion().getRegionWidth() / 2f,
                                trailTexture.getTextureRegion().getRegionHeight() / 2f, xPos, getY() * drawScale.y, getAngle(), -initDir * hscale, vscale);
                    }
                }

                if (headTexture != null && !dying && !platformPopped) {
                    float xPos = (getX() + initDir * getScaledX() / 2f) * drawScale.x + initDir * headTexture.getTextureRegion().getRegionWidth() / 4.0f;
                    canvas.draw(headTexture.getTextureRegion(), paintcolor, headTexture.getTextureRegion().getRegionWidth() / 2f,
                            headTexture.getTextureRegion().getRegionHeight() / 2f,
                            xPos, getY() * drawScale.y, getAngle(), initDir, 1.0f);

                }
            }
        }

        if (splatEffectTexture.getTextureRegion() != null && splatEffectTexture.isPlaying()) {
            float xPos = (getX() + getWidth()/2f*initDir) * drawScale.x - initDir*splatEffectTexture.getTextureRegion().getRegionWidth()/4f;
            float yPos = getY()*drawScale.y-splatEffectTexture.getTextureRegion().getRegionHeight()*getScaledY()/2f;
            canvas.draw(splatEffectTexture.getTextureRegion(), paintcolor, origin.x, origin.y, xPos,yPos, getAngle(), initDir, 1.0f);

        }
    }

    private float snapToGrid(float yVal) {
        float size = 48/drawScale.y;
        yVal =(float) Math.floor((size/2+yVal)/size);
        return size*yVal;
    }
}