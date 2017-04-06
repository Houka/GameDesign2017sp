package edu.cornell.gdiac.game.interfaces;

import edu.cornell.gdiac.util.Animation;

/**
 * Created by Lu on 4/5/2017.
 */
public interface Animatable {
    /**
     *  Sets the animation for this entity
     */
    void setAnimation(Animation animation);

    /**
     * @return the animation for this entity
     */
    Animation getAnimation();
}
