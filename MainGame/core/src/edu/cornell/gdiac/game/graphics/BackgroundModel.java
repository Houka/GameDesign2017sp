package edu.cornell.gdiac.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.interfaces.TextureDrawable;

import java.awt.geom.RectangularShape;

/**
 * Created by Lu on 3/10/2017.
 *
 * TODO: fill in class def
 * TODO: change simple implementation of Textures to better implementation of SpriteBatches
 */
public class BackgroundModel implements TextureDrawable, Disposable{
    private float x = 0;
    private float y = 0;
    private Vector2 scale;
    private Vector2 origin;

    private TextureRegion texture;

    /**
     * TODO: fill in construtor def
     *
     */
    public BackgroundModel(float x, float y, Vector2 scale){
        this.x = x;
        this.y = y;
        this.scale = scale;
        origin = new Vector2();
    }

    // BEGIN: setters and getters

    public TextureRegion getTexture(){ return texture; }

    @Override
    public void setTexture(TextureRegion value) {
        this.texture = value;
        origin.set(texture.getRegionWidth()/2.0f, texture.getRegionHeight()/2.0f);
    }

    @Override
    public Vector2 getDrawScale() {
        return scale;
    }


    public void setDrawScale(Vector2 scale) {
        setDrawScale(scale.x,scale.y);
    }

    @Override
    public void setDrawScale(float x, float y) {
        scale.x = x;
        scale.y = y;
    }

    // END: setters and getters

    @Override
    public void dispose() {
        origin = null;
    }

    @Override
    public void draw(GameCanvas canvas) {
        if (texture != null) {
            canvas.draw(texture, Color.WHITE,
                        origin.x,origin.y,x*scale.x,y*scale.x,
                        0,1,1);
        }
    }

    @Override
    public void drawDebug(GameCanvas canvas) {
        // TODO: debug draw for background
    }
}
