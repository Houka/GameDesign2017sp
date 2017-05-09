package edu.cornell.gdiac.game.entity.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.interfaces.TextureDrawable;
import edu.cornell.gdiac.util.obstacles.BoxObstacle;

import java.awt.geom.RectangularShape;

/**
 * Created by Lu on 3/10/2017.
 *
 * Model class for the background. Is used to draw out the background.
 */
public class BackgroundModel extends BoxObstacle {
    private float scaleX, scaleY;
    private int addonX = 0, addonY = 0;

    /**
     * Creates a new BackgroundModel at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x         Initial x position of the center of the background 
     * @param y         Initial y position of the center of the background
     * @param width     The object width in physics units
     * @param height    The object width in physics units
     */
    public BackgroundModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0.0f);
        setFriction(0.0f);
        setRestitution(0.0f);
        setSensor(true);
        setName("background");
    }

    // BEGIN: Setter and getter
    public void incBgWidth(int value){
        addonX+=value;
    }
    public void incBgHeight(int value){
        addonY+=value;
    }
    public float getMaxWidth(){
        return getX()*drawScale.x + texture.getRegionWidth()*(scaleX+addonX-1);
    }
    public float getMaxHeight(){
        return getY()*drawScale.y + texture.getRegionHeight()*(scaleY+addonY-1);
    }
    // END: Setter and getter

    @Override
    public void setTexture(TextureRegion texture){
        super.setTexture(texture);
        scaleX = texture.getRegionWidth()/getWidth();
        scaleY = texture.getRegionHeight()/getHeight();
    }

    @Override
    public void draw(GameCanvas canvas){
        if (texture != null) {
            for (int i = (int)-(scaleX+addonX); i < scaleX+addonX; i++){
                for(int j = 0; j < scaleY+addonY; j++){
                    canvas.draw(texture,Color.WHITE,origin.x,origin.y,
                            getX()*drawScale.x + texture.getRegionWidth()*i,
                            getY()*drawScale.y + texture.getRegionHeight()*j,
                            getAngle(),1,1);
                }
            }
        }
    }

}
