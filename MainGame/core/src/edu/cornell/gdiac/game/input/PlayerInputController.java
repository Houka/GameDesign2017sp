/*
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Class for reading player input.
 */
public class PlayerInputController {
    /** The singleton instance of the input controller */
    private static PlayerInputController theController = null;

    /**
     * Return the singleton instance of the input controller
     *
     * @return the singleton instance of the input controller
     */
    public static PlayerInputController getInstance() {
        if (theController == null) {
            theController = new PlayerInputController();
        }
        return theController;
    }

    // Fields to manage buttons
    private boolean upPressed;
    private boolean downPressed;
    private boolean shootPressed;
    private boolean jumpPressed;
    private boolean jumpReleased;

    private boolean upPrevious;
    private boolean downPrevious;
    private boolean shootPrevious;
    private boolean jumpPrevious;

    /** How much did we move horizontally? */
    private float horizontal;
    /** How much did we move vertically? */
    private float vertical;

    /**
     * Creates a new input controller
     */
    private PlayerInputController() {}

    // BEGIN: Getters and Setters
    public boolean didUp() {
        return upPressed && !upPrevious;
    }
    public boolean didDown() {
        return downPressed && !downPrevious;
    }
    public boolean didShoot() {
        return shootPressed && !shootPrevious;
    }
    public boolean didJump() { return jumpPressed && !jumpPrevious; }
    public boolean didStopJump() { return jumpReleased; }
    public boolean anyKeyPressed(){ return didShoot() || didJump() || didDown() || didUp() ||getHorizontal()!=0 || getVertical()!= 0;}
    /**
     * Returns the amount of sideways movement.
     *
     * -1 = left, 1 = right, 0 = still
     *
     * @return the amount of sideways movement.
     */
    public float getHorizontal() {
        return horizontal;
    }

    /**
     * Returns the amount of vertical movement.
     *
     * -1 = down, 1 = up, 0 = still
     *
     * @return the amount of vertical movement.
     */
    public float getVertical() {
        return vertical;
    }
    // END: Getters and Setters

    /**
     * Reads the input for the player and converts the result into game logic.
     *
     */
    public void readInput() {
        // Copy state from last animation frame
        // Helps us ignore buttons that are held down
        upPrevious  = upPressed;
        downPrevious  = downPressed;
        shootPrevious = shootPressed;
        jumpPrevious = jumpPressed;

        readKeyboard(false);
    }

    /**
     * Reads the input for down. Need this method to allow continuous crouching while holding button down.
     *
     */
    public boolean isDownHeld(){
        return (Gdx.input.isKeyPressed(Input.Keys.DOWN));
    }
    /**
     * Reads input from the keyboard.
     *
     * @param secondary true if the keyboard should give priority to a gamepad
     */
    private void readKeyboard(boolean secondary) {
        // Give priority to gamepad results
        upPressed  = (secondary && upPressed) || (Gdx.input.isKeyPressed(Input.Keys.UP));
        downPressed  = (secondary && downPressed) || (Gdx.input.isKeyPressed(Input.Keys.DOWN));
        shootPressed = (secondary && shootPressed) || (Gdx.input.isKeyPressed(Input.Keys.Z) || (Gdx.input.isKeyPressed(Input.Keys.SPACE)));
        jumpPressed = (secondary && jumpPressed) || (Gdx.input.isKeyPressed(Input.Keys.X) || upPressed);
        jumpReleased = (!jumpPressed && jumpPrevious);

        // Directional controls
        horizontal = (secondary ? horizontal : 0.0f);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontal += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontal -= 1.0f;
        }

        vertical = (secondary ? vertical : 0.0f);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            vertical += 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            vertical -= 1.0f;
        }

    }
}