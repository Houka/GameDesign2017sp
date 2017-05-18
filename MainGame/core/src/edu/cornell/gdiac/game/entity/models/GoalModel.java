package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

/**
 * Created by Lu on 3/17/2017.
 *
 * This class is the model for the target that the player must shoot in order to win. 
 */
public class GoalModel extends BoxObstacle {
    private Animation animation;
	/**
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the center
     * @param y  		Initial y position of the center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public GoalModel(float x, float y, float width, float height) {
        super(x, y, width/2, height/2);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0.0f);
        setFriction(0.0f);
        setRestitution(0.0f);
        setSensor(true);
        setName("goal");
        animation = null;
    }

    public void setAnimation(Animation animation){
        this.animation = animation;
    }

    public Animation getAnimation(){
        return animation;
    }

    @Override
    public void update(float dt){
        animation.update(dt);
    }
    @Override
    public void draw(GameCanvas canvas){
        if (texture != null) {
            if (animation == null)
                canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1f, 1.0f);
            else
                canvas.draw(animation.getTextureRegion(), Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1f, 1.0f);
        }
    }


}
