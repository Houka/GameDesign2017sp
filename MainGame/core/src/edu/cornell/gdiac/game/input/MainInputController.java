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

import com.badlogic.gdx.*;

/**
 * Class for reading player input. 
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we only 
 * detected the X-Box controller on start-up.  This class allows us to hot-swap in
 * a controller via the new XBox360Controller class.
 */
public class MainInputController {
    /** The singleton instance of the input controller */
    private static MainInputController theController = null;

    /**
     * Return the singleton instance of the input controller
     *
     * @return the singleton instance of the input controller
     */
    public static MainInputController getInstance() {
        if (theController == null) {
            theController = new MainInputController();
        }
        return theController;
    }

    // Fields to manage buttons
    /** Whether the reset button was pressed. */
    private boolean resetPressed;
    private boolean resetPrevious;
    /** Whether the debug toggle was pressed. */
    private boolean debugPressed;
    private boolean debugPrevious;
    /** Whether the exit button was pressed. */
    private boolean exitPressed;
    private boolean exitPrevious;

    /**
     * Creates a new input controller
     */
    private MainInputController() {}

    // BEGIN: Getters and Setters
    public boolean didReset() {
        return resetPressed && !resetPrevious;
    }
    public boolean didDebug() {
        return debugPressed && !debugPrevious;
    }
    public boolean didExit() {
        return exitPressed && !exitPrevious;
    }
    // END: Getters and Setters

    /**
     * Reads the input for the player and converts the result into game logic.
     *
     */
    public void readInput() {
        // Copy state from last animation frame
        // Helps us ignore buttons that are held down
        resetPrevious  = resetPressed;
        debugPrevious  = debugPressed;
        exitPrevious = exitPressed;

        readKeyboard(false);
    }

    /**
     * Reads input from the keyboard.
     *
     * @param secondary true if the keyboard should give priority to a gamepad
     */
    private void readKeyboard(boolean secondary) {
        // Give priority to gamepad results
        exitPressed  = (secondary && exitPressed) || (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));
        resetPressed = (secondary && resetPressed) || (Gdx.input.isKeyPressed(Input.Keys.R));
        debugPressed = (secondary && debugPressed) || (Gdx.input.isKeyPressed(Input.Keys.D));
    }
}