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

    @Override
    public void draw(GameCanvas canvas){
        if (texture != null) {
            canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),
                    texture.getRegionWidth()/getWidth(),texture.getRegionHeight()/getHeight());
        }
    }

}
