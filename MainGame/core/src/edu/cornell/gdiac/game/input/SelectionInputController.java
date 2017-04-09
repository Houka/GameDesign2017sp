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
public class SelectionInputController {
    /** The singleton instance of the input controller */
    private static SelectionInputController theController = null;

    /**
     * Return the singleton instance of the input controller
     *
     * @return the singleton instance of the input controller
     */
    public static SelectionInputController getInstance() {
        if (theController == null)
            theController = new SelectionInputController();
        return theController;
    }

    // Fields to manage buttons
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean selectPressed;

    private boolean upPrevious;
    private boolean downPrevious;
    private boolean leftPrevious;
    private boolean rightPrevious;
    private boolean selectPrevious;

    /**
     * Creates a new input controller
     */
    private SelectionInputController() {}

    // BEGIN: Getters and Setters
    public boolean didUp() {
        return upPressed && !upPrevious;
    }
    public boolean didDown() {
        return downPressed && !downPrevious;
    }
    public boolean didLeft() {
        return leftPressed && !leftPrevious;
    }
    public boolean didRight() {
        return rightPressed && !rightPrevious;
    }
    public boolean didSelect() {
        return selectPressed && !selectPrevious;
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
        leftPrevious = leftPressed;
        rightPrevious = rightPressed;
        selectPrevious = selectPressed;

        readKeyboard(false);
    }

    /**
     * Reads input from the keyboard.
     *
     * @param secondary true if the keyboard should give priority to a gamepad
     */
    private void readKeyboard(boolean secondary) {
        // Give priority to gamepad results
        upPressed  = (secondary && upPressed) || (Gdx.input.isKeyPressed(Input.Keys.UP))|| (Gdx.input.isKeyPressed(Input.Keys.W));
        downPressed  = (secondary && downPressed) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))|| (Gdx.input.isKeyPressed(Input.Keys.S));
        leftPressed  = (secondary && leftPressed) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))|| (Gdx.input.isKeyPressed(Input.Keys.A));
        rightPressed  = (secondary && rightPressed) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))|| (Gdx.input.isKeyPressed(Input.Keys.D));
        selectPressed  = (secondary && selectPressed) || (Gdx.input.isKeyPressed(Input.Keys.ENTER));
    }
}