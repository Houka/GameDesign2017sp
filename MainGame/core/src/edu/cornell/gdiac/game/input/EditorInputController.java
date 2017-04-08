package edu.cornell.gdiac.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by cece on 3/26/2017.
 */
public class EditorInputController implements InputProcessor{
    /** The singleton instance of the input controller */
    private static EditorInputController theController = null;
    /** Mouse input vars */
    private boolean didTouch;
    private boolean didDrag;
    private boolean justTouched;
    private Vector2 lastPos;
    private int scrollAmount = 0;
    private int scrollTick = 0;
    private int scrollTickPrev = 0;

    /** keyboard input vars */
    private boolean savePressed;
    private boolean loadPressed;
    private boolean resetPressed;
    private boolean ammoPressed;

    /** keyboard prev states */
    private boolean savePrevious;
    private boolean loadPrevious;
    private boolean resetPrevious;
    private boolean ammoPrevious;

    /**
     * Return the singleton instance of the input controller
     *
     * @return the singleton instance of the input controller
     */
    public static EditorInputController getInstance() {
        if (theController == null) {
            theController = new EditorInputController();
            Gdx.input.setInputProcessor(theController);
        }
        return theController;
    }


    // BEGIN: Getters and Setters
    public boolean didSave() {
        return savePressed && !savePrevious;
    }
    public boolean didLoad() {
        return loadPressed && !loadPrevious;
    }
    public boolean didReset() { return resetPressed && !resetPrevious; }
    public boolean didAmmoChange() { return ammoPressed&& !ammoPrevious; }
    public boolean didTouch() { return didTouch; }
    public boolean didDrag() { return didDrag; }
    public Vector2 getLastPos() {
        return lastPos;
    }
    public boolean didScrolledUp() {
        return scrollAmount > 0;
    }
    public boolean didScrolledDown() {
        return scrollAmount<0;
    }
    // END: Getters and Setters


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
        savePrevious = savePressed;
        loadPrevious = loadPressed;
        resetPrevious = resetPressed;
        ammoPrevious = ammoPressed;

        boolean ctrlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        savePressed = ctrlPressed&& (Gdx.input.isKeyPressed(Input.Keys.S));
        loadPressed = ctrlPressed&& (Gdx.input.isKeyPressed(Input.Keys.O));
        resetPressed = (Gdx.input.isKeyPressed(Input.Keys.R));
        ammoPressed = ctrlPressed&& (Gdx.input.isKeyPressed(Input.Keys.A));

        // mouse scrolling
        if (scrollTickPrev == scrollTick){
            scrollAmount = 0;
            scrollTick = 0;
        }
        scrollTickPrev = scrollTick;
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
        scrollTick++;
        scrollAmount = amount;
        return false;
    }
}
