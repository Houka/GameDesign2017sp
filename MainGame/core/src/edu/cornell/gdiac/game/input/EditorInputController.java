package edu.cornell.gdiac.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by cece on 3/26/2017.
 */
public class EditorInputController implements InputProcessor {
    /** The singleton instance of the input controller */
    private static EditorInputController theController = null;
    /** */
    private boolean didTouch;
    private boolean didDrag;
    private boolean justTouched;
    private Vector2 lastPos;

    /**
     * Return the singleton instance of the input controller
     *
     * @return the singleton instance of the input controller
     */
    public static EditorInputController getInstance() {
        if (theController == null) {
            theController = new EditorInputController();
        }
        return theController;
    }

    public boolean didTouch() { return didTouch; }

    public boolean didDrag() { return didDrag; }

    // public boolean justTouched() { return justTouched; }

    public Vector2 getLastPos() {
        return lastPos;
    }


    /**
     * Creates a new input controller
     */
    private EditorInputController() {}

    /**
     * Reads the input for the player and converts the result into game logic.
     *
     */
    public void readInput() {
        // Copy state from last animation frame\
    }

    // INPUTPROCESSOR METHODS

    public boolean keyDown (int keycode) {
        return false;
    }

    public boolean keyUp (int keycode) {
        return false;
    }

    public boolean keyTyped (char character) {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            didTouch = true;
        }
        return false;
    }

    public boolean touchUp (int x, int y, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            didTouch = false;
            didDrag = false;
            lastPos = new Vector2(x,y);
        }
        return false;
    }

    public boolean touchDragged (int x, int y, int pointer) {
        didDrag = true;
        return false;
    }

    public boolean mouseMoved (int x, int y) {
        return false;
    }

    public boolean scrolled (int amount) {
        return false;
    }
}
