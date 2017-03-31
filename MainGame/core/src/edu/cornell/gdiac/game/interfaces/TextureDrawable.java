package edu.cornell.gdiac.game.interfaces;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.GameCanvas;

/**
 * Created by Lu on 3/10/2017.
 *
 * Contains functions for drawing textures
 */
public interface TextureDrawable {
    /**
     * Returns the object texture for drawing purposes.
     *
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @return the object texture for drawing purposes.
     */
    TextureRegion getTexture();


    /**
     * Sets the object texture for drawing purposes.
     *
     * In order for drawing to work properly, you MUST set the drawScale.
     * The drawScale converts the physics units to pixels.
     *
     * @param value  the object texture for drawing purposes.
     */
    void setTexture(TextureRegion value);


    /**
     * Returns the drawing scale for this physics object
     *
     * The drawing scale is the number of pixels to draw before Box2D unit. Because
     * mass is a function of area in Box2D, we typically want the physics objects
     * to be small.  So we decouple that scale from the physics object.  However,
     * we must track the scale difference to communicate with the scene graph.
     *
     * This method does NOT return a reference to the drawing scale. Changes to this
     * vector will not affect the body.  However, it returns the same vector each time
     * its is called, and so cannot be used as an allocator.
     * We allow for the scaling factor to be non-uniform.
     *
     * @return the drawing scale for this physics object
     */
    Vector2 getDrawScale();

    /**
     * Sets the drawing scale for this physics object
     *
     * The drawing scale is the number of pixels to draw before Box2D unit. Because
     * mass is a function of area in Box2D, we typically want the physics objects
     * to be small.  So we decouple that scale from the physics object.  However,
     * we must track the scale difference to communicate with the scene graph.
     *
     * We allow for the scaling factor to be non-uniform.
     *
     * @param x  the x-axis scale for this physics object
     * @param y  the y-axis scale for this physics object
     */
    void setDrawScale(float x, float y);

    /**
     * Draws the texture physics object.
     *
     * @param canvas Drawing context
     */
    void draw(GameCanvas canvas);


    /**
     * Draws the outline of the drawable obj.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    void drawDebug(GameCanvas canvas);
}
