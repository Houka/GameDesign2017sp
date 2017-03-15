package edu.cornell.gdiac.game.interfaces;

/**
 * Created by Lu on 3/10/2017.
 *
 * Interface that specifies if the current Screen is complete or not.
 *  If complete, the implementation will act accordingly
 */
public interface Completable {

    /**
     * @return true if the screen is completed.
     */
    boolean isComplete();

    /**
     * Sets whether the screen is completed.
     *
     * If true, the screen will advance to the next state specified by the exitCode
     *
     * @param value whether the level is completed.
     */
    void setComplete(boolean value);
}
