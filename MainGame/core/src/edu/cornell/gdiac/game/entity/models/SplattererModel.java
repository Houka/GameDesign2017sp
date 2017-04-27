package edu.cornell.gdiac.game.entity.models;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by cece on 4/22/2017.
 */
public class SplattererModel extends BoxObstacle {
    private boolean used;
    private int usedCooldown;
    /**
     * Direction to shoot in
     */
    private boolean dir;
    /**
     * Whether the splatterer was shot (used for creating paintballs)
     */
    private boolean shot;

    private static final int USED_COOLDOWN = 70;

    /** Where the bullet shot into */
    private float yCoord;

    public SplattererModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setDensity(0);
        setSensor(true);
        setBodyType(BodyDef.BodyType.StaticBody);
        setFixedRotation(true);
        setName("splatterer");

        this.used = false;
        this.usedCooldown = 0;
        this.dir = false;
        this.yCoord = y;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean getDir() {
        return dir;
    }

    public void setDir(boolean value) {
        dir = value;
    }

    public boolean isShot() {
        return shot;
    }

    public void setShot(boolean value) {
        shot = value;
    }

    public void setYCoord(float value) {
        yCoord = value;
    }

    public float getYCoord() {
        return yCoord;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (isUsed() && usedCooldown == 0)
            usedCooldown = USED_COOLDOWN;
        if (usedCooldown > 0)
            usedCooldown--;
        if (usedCooldown == 0)
            setUsed(false);
    }

    @Override
    public void draw(GameCanvas canvas) {
        canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1.0f, 1.0f);
    }
}