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
 * TODO: fill in class def
 * TODO: change simple implementation of Textures to better implementation of SpriteBatches
 */
public class BackgroundModel extends BoxObstacle {

    public BackgroundModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setBodyType(BodyDef.BodyType.StaticBody);
        setDensity(0.0f);
        setFriction(0.0f);
        setRestitution(0.0f);
        setSensor(true);
        setName("background");

    }
}
