package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

import java.sql.Time;

/**
 * Created by Lu on 3/17/2017.
 *
 * This class is the model for the HUD display, which displays information about the player during the game.
 */
public class HUDModel extends BoxObstacle {
    /** Default amount of ammo the player starts with*/
    private static final int DEFAULT_STARTING_AMMO = 10;
    /** Default y location for ammo bar*/
    private static final int AMMO_BAR_Y = 75;

    //Constants for states
    /** We are playing the game*/
    private static final int STATE_PLAYING = 0;
    /** The player has won*/
    private static final int STATE_WIN = 1;
    /** The player has lost*/
    private static final int STATE_LOSE = 2;

    /** Font the HUD text is written in*/
    private BitmapFont font;
    /** Stores remaining ammo*/
    private int ammoLeft;
    /** Starting ammo*/
    private int startingAmmo;
    /** Start in the playing state*/
    private int state = STATE_PLAYING;

    /** Time since last state change*/
    private float lastStateChange;

    /** Timer for a race the clock situation (in seconds) **/
    private Time time;

    private Animation ammoFilledAnimation;
    private TextureRegion ammoEmpty;
    private TextureRegion ammoBar;

    /**
     * Creates a new HUD at the given position.
     *
     * The size is expressed in physics units NOT pixels. In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     * @param width     The object width in physics units
     * @param height    The object width in physics units
     */
    public HUDModel(float width, float height) {
        super(width/2,height/2, width, height);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0.0f);
        setFriction(0.0f);
        setRestitution(0.0f);
        setSensor(true);
        setName("hud");

        ammoLeft = DEFAULT_STARTING_AMMO;
        startingAmmo = DEFAULT_STARTING_AMMO;
        lastStateChange = 0.0f;
        time = new Time(0);
    }

    // BEGIN: Setters and Getters
    public void setWin(boolean value){
        if(state!=STATE_WIN)
            lastStateChange=0;
        state = value? STATE_WIN: state;
    }

    public boolean isWin(){ return state == STATE_WIN; }

    public void setLose(boolean value){
        if(state!=STATE_LOSE)
            lastStateChange=0;
        state = value? STATE_LOSE: state;
    }

    public boolean isLose(){ return state == STATE_LOSE; }

    public float getLastStateChange() {return lastStateChange;}

    public void setFont(BitmapFont font){ this.font = font; }

    public void setStartingAmmo(int value){
        startingAmmo = value;
        ammoLeft = startingAmmo;
    }

    public void setAnimationAndTexture(Animation a, TextureRegion bar, TextureRegion empty){
        ammoFilledAnimation = a;
        ammoBar = bar;
        ammoEmpty = empty;
    }

    public void setAmmoLeft(int value){ ammoLeft = Math.min(value, startingAmmo); }

    public int getAmmoLeft(){ return ammoLeft; }

    public boolean useAmmo(){
        if (ammoLeft > 0) {
            ammoLeft--;
            return true;
        }

        return false;
    }

    public void addAmmo(int value){ setAmmoLeft(value+ammoLeft); }

    // END: Setters and Getters

    /** Resets the level to the start state*/
    public void reset(){
        ammoLeft = startingAmmo;
        state = STATE_PLAYING;
        lastStateChange = 0;
        time.setTime(0);
    }

    @Override
    public void update(float delta) {
        lastStateChange+=delta;
        time.setTime((int)lastStateChange * 1000);
        if(ammoFilledAnimation != null)
            ammoFilledAnimation.update(delta);
    }

    @Override
    public void draw(GameCanvas canvas){
        if (ammoFilledAnimation != null && ammoBar != null){
            if (startingAmmo > 0)
                canvas.draw(ammoBar, Color.WHITE,0,0, 30, getY()-AMMO_BAR_Y,0,1.5f,1.5f);
            for(int i = 0; i<startingAmmo; i++){
                canvas.draw(ammoEmpty, Color.WHITE,0,0, 30+(10+ammoEmpty.getRegionWidth())*i, getY()-ammoBar.getRegionHeight()*1.5f-AMMO_BAR_Y,
                        0,1.5f,1.5f);
                if (i<ammoLeft){
                    canvas.draw(ammoFilledAnimation.getTextureRegion(), Color.WHITE,0,0, 30+(10+ammoEmpty.getRegionWidth())*i,
                            getY()-ammoBar.getRegionHeight()*1.5f-AMMO_BAR_Y, 0,1.5f,1.5f);
                }
            }
        }
    }
}
