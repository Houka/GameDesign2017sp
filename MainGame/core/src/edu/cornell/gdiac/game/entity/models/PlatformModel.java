package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.PolygonObstacle;

/**
 * Created by Lu on 3/17/2017.
 * 
 * The model class for the platforms.
 */
public class PlatformModel extends PolygonObstacle {
    //constants for the platforms
    public static final int NORMAL_PLATFORM = 0;
    public static final int SPIKE_DOWN_PLATFORM = 1;
    public static final int SPIKE_UP_PLATFORM = 2;
    public static final int SPIKE_LEFT_PLATFORM = 3;
    public static final int SPIKE_RIGHT_PLATFORM = 4;

    /** how much we are shrinking for spikes*/
    public static final float SPIKE_SHRINK = 0.375f;
    /** Density of the platforms*/
    private static final float  BASIC_DENSITY = 0.0f;
    /** Friction of the platforms*/
    private static final float  BASIC_FRICTION = 0.4f;
    /** "Bounciness" of the platforms*/
    private static final float  BASIC_RESTITUTION = 0.1f;
    private float[] pointArray;
    private int type;
    /** The animation associated with this entity */
    private Animation animation;
    private float angle = 0;

    /**
     * Creates a new platform.
     *
     * The points is expressed in physics units NOT pixels. In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param points    Vertices outlining the platform. In form [x1, y1, x2, y2 ...]
     */
    public PlatformModel(float[] points, int type) {
        super(type== NORMAL_PLATFORM ? points : halveHitbox(points));
        pointArray = points;
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(BASIC_DENSITY);
        setFriction(BASIC_FRICTION);
        setRestitution(BASIC_RESTITUTION);
        setName("platform");
        if(type != NORMAL_PLATFORM)
            setSensor(true);
        this.type = type;
        animation = null;
    }

    // BEGIN: Setters and Getters
    public float[] getPoints(){ return pointArray; }
    public int getType() { return type; }
    public void setType(int type) {
        this.type = type;
    }
    public void setAnimation(Animation animation){
        this.animation = animation;

        if (type == SPIKE_LEFT_PLATFORM) {
            angle = ((float)Math.PI/2);
        }
        else if (type == SPIKE_RIGHT_PLATFORM){
            angle = ((float)Math.PI * 3f/2f);
        }
        else if (type == SPIKE_UP_PLATFORM) {
            angle = (0f);
        }
        else if (type == SPIKE_DOWN_PLATFORM){
            angle = ((float)Math.PI);
        }
        animation.play("spin", true);
    }
    // END: Setters and GEtters

    public static float[] halveHitbox(float[] f){
        float[] g = f;
        g[0] += SPIKE_SHRINK;
        g[1] -= SPIKE_SHRINK;
        g[2] -= SPIKE_SHRINK;
        g[3] -= SPIKE_SHRINK;
        g[4] -= SPIKE_SHRINK;
        g[5] += SPIKE_SHRINK;
        g[6] += SPIKE_SHRINK;
        g[7] += SPIKE_SHRINK;
        return g;
    }


    @Override
    public void update(float dt){
        super.update(dt);
        if (animation != null)
            animation.update(dt);
    }

    @Override
    public void draw(GameCanvas canvas){
        if (region != null) {
            if (animation == null)
                canvas.draw(region,Color.WHITE,0,0,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1,1);
            else
                canvas.draw(animation.getTextureRegion(),Color.WHITE,origin.x,origin.y,(pointArray[6]-SPIKE_SHRINK)*
                        drawScale.x+origin.x,(pointArray[7]-SPIKE_SHRINK)*drawScale.y+origin.y,angle,1,1.0f);


        }
    }
}
