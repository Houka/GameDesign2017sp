package edu.cornell.gdiac.game.interfaces;

/**
 * Created by Lu on 3/17/2017.
 */
public interface Shooter {
    /**
     * Returns true if the entity is actively firing.
     *
     * @return true if the entity is actively firing.
     */
    boolean isShooting();

    /**
     * Sets whether the entity is actively firing.
     *
     * @param value whether the entity is actively firing.
     */
    void setShooting(boolean value);

    /**
     * Returns true if this character is facing right
     *
     * @return true if this character is facing right
     */
    boolean isFacingRight();
}
