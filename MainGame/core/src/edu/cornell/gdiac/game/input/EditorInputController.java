package edu.cornell.gdiac.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

/**
 * Created by cece on 3/26/2017.
 */
public class EditorInputController {
    /** The singleton instance of the input controller */
    private static EditorInputController theController = null;

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

    // Fields to manage buttons
    private boolean mouseClicked;

    /**
     * Creates a new input controller
     */
    private EditorInputController() {}

    /**
     * Reads the input for the player and converts the result into game logic.
     *
     */
    public void readInput() {
        // Copy state from last animation frame
        // Helps us ignore buttons that are held down
    }
}
