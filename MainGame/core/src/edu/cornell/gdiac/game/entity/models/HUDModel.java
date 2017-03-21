package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by Lu on 3/17/2017.
 */
public class HUDModel extends BoxObstacle {
    private static final int DEFAULT_STARTING_AMMO = 10;
    private static final int STATE_PLAYING = 0;
    private static final int STATE_WIN = 1;
    private static final int STATE_LOSE = 2;

    private BitmapFont font;
    private int ammoLeft;
    private int startingAmmo;

    private int state = STATE_PLAYING;

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
    }

    // BEGIN: Setters and Getters
    public void setWin(boolean value){ state = value? STATE_WIN: state; }
    public boolean isWin(){ return state == STATE_WIN; }
    public void setLose(boolean value){ state = value? STATE_LOSE: state; }
    public boolean isLose(){ return state == STATE_LOSE; }
    public void setFont(BitmapFont font){
        this.font = font;
    }
    public void setStartingAmmo(int value){
        startingAmmo = value;
        ammoLeft = startingAmmo;
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


    public void reset(){
        ammoLeft = startingAmmo;
    }

    @Override
    public void draw(GameCanvas canvas){
        font.setColor(Color.DARK_GRAY);
        canvas.drawText("Ammo:"+ammoLeft, font, 30, getY()-30);

        if (state == STATE_WIN)
            canvas.drawTextCentered("VICTORY", font, getY()-canvas.getHeight());
        else if (state == STATE_LOSE)
            canvas.drawTextCentered("FAILURE", font, getY()-canvas.getHeight());
    }
}
