package edu.cornell.gdiac.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.entity.factories.PaintballFactory;

/**
 * Created by Bucc on 3/27/2017.
 */
public class Camera2 extends OrthographicCamera{
    private Vector2 targetLocation;
    private Vector2 temp;
    private boolean autosnap = true;
    private boolean rumble = false;
    private int rumbleX;
    private int rumbleY;
    private int rumbleFrequency;
    private int rumbleCountdown;
    private float speed = .1f;

    public Camera2(int width, int height) {
        super(width,height);
        temp = new Vector2();
        targetLocation = new Vector2();
        autosnap = true;
        this.snap();
    }

    public void update() {
        super.update();
        if(temp != null && targetLocation != null) {

            if (autosnap) {
                this.snap();
                return;
            }
            if (targetLocation == null) {
                targetLocation = new Vector2();
                targetLocation.set(position.x, position.y);
            }

            temp.set(-this.position.x, -this.position.y);
            temp.add(targetLocation).scl(speed);
            this.translate(temp);

            if(rumble) {
                rumbleCountdown--;
                if(rumbleCountdown<=0) {
                    rumbleCountdown = rumbleFrequency;
                    temp.set((float)(Math.random()-.5)*rumbleX*2,(float)(Math.random()-.5)*rumbleY*2f);
                    this.translate(temp);
                }
            }
            super.update();
        }

    }

    public void setLocation(Vector2 l) {
        this.position.x=l.x;
        this.position.y=l.y;
        update();
    }

    public void setTargetLocation(float x, float y) {
        targetLocation.set(x,y);
        if(autosnap)
            snap();
    }

    public void setAutosnap(boolean bool) {
        autosnap = bool;
    }

    public void setTargetLocation(Vector2 tl){
        targetLocation.set(tl);
    }

    public Vector2 getTargetLocation() {
        return targetLocation;
    }

    public void snap() {
        translate(targetLocation.x-position.x,targetLocation.y-position.y);
        super.update();
    }

    public void setSpeed(float s) {
        speed = s;
    }

    public void disableRumble() {
        rumble = false;
    }

    public void enableRumble() {
        rumble = true;
    }

    public void toggleRumble() {
        rumble = !rumble;
    }

    public void setRumble(int rumX, int rumY, int freq) {
        rumbleX = rumX;
        rumbleY = rumY;
        rumbleFrequency = freq;
        //rumbleCountdown = freq; TODO Probably should ad back once sidebar option for this is gone
    }


}
