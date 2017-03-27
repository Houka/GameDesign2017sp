/*
 * Mode.java
 *
 * The Abstract class that all modes adhere to.
 * This class implements all the under the hood stuff for each mode/screen.
 * It takes care of updating/drawing calls, resizing, pausing, and 
 * what to do when exiting/switching the screen over to another screen
 *
 * Author: Changxu Lu
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.MainInputController;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.game.interfaces.Completable;
import edu.cornell.gdiac.game.interfaces.Exitable;
import edu.cornell.gdiac.game.interfaces.ScreenListener;

/**
 * Class that provides the fundamental mode functionalities
 */
public abstract class Mode implements Screen, Completable, AssetUser, Exitable {
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 1024;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 576;
	/** Scaling factor for when the student changes the resolution. */
	protected Vector2 scale;
	/** Background texture for start-up */
	protected Texture background;

	/** AssetManager to be loading in the background */
	protected AssetManager manager;
	/** Reference to GameCanvas created by the root */
	protected GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	protected ScreenListener listener;

	/** The exit code for when this screen completes */
	protected int onExit = ScreenListener.EXIT_NOP; // default it to non existant exit code
	/** Whether or not this mode is completed*/
	private boolean exit;
	/** Whether or not this mode is completed*/
	private boolean completed;
	/** Whether or not this mode is still active */
	protected boolean active;
	/** Whether or not debug mode is active */
	protected boolean debug;
	/** The main input controller */
	private MainInputController input;

	/**
	 * TODO: write description for Constructor
	 *
	 * @param canvas The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 */
	protected Mode(GameCanvas canvas, AssetManager manager) {
		this.manager = manager;
		this.canvas  = canvas;
		scale = new Vector2(1,1);
		active = false;
		exit = false;
		completed = false;
		debug  = false;
		input = MainInputController.getInstance();
	}

	// BEGIN: Getters and Setters
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	@Override
	public void setComplete(boolean value) {
		if (value)
			active = false;
		completed = value;
	}

	@Override
	public boolean isComplete() {
		return completed;
	}

	@Override
	public void setExit(boolean value){ exit = value; }

	@Override
	public boolean isExit(){ return exit; }
	// END: Getters and Setters

	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		if (background != null)
			background.dispose();
		background = null;
	}

	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check the input and
	 * determine if we should still update
	 *
	 * @param dt Number of seconds since last animation frame
	 *
	 * @return whether to process the update loop
	 */
	protected boolean preUpdate(float dt) {
		input.readInput();
		if (input.didDebug())
			debug = !debug;
		else if (input.didReset())
			reset();
		else if(input.didExit()) {
			setExit(true);
			return false;
		}

		return true;
	}

	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	abstract void update(float delta);

	/**
	 * Draw the background of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	protected void draw() {
		if (background != null)
			canvas.draw(background, Color.WHITE, 0, 0, 0,0, 0f, scale.x, scale.y);
	}

    /**
     * Draw the outlines and bounding boxes of each object for debuging purposes
     */
	protected void drawDebug(){};

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	@Override
	public void render(float delta) {
		if (active) {
			if (preUpdate(delta))
				update(delta);

			canvas.clear();
			canvas.begin();
			draw();
			canvas.end();

			if (debug) {
				canvas.beginDebug();
				drawDebug();
				canvas.endDebug();
			}

			// We are are ready, notify our listener
			if (isExit() && listener != null)
				onExit();
			if (isComplete() && listener != null)
				onComplete();
		}
	}

	@Override
	public void resize(int width, int height) {
		// Compute the drawing scale
		scale.set(Math.max(1,(float) width/STANDARD_WIDTH), Math.max(1,(float) height/STANDARD_HEIGHT));
	}

    /**
     * Resets all variables in the mode so when the screen switches back, the screen
     * won't hold previous data.
     */
	public void reset(){
		setExit(false);
		setComplete(false);
	}

	/**
	 * Default behavior for modes that have completed their task
	 */
	protected void onComplete(){
		onExit();
	}
    
    /**
     * Behavior for when the mode wishes to exit
     */
	protected void onExit(){ listener.exitScreen(this, onExit); }

    /**
     * Behavor for when the mode is paused
     */
	public void pause() {}
	
    /**
     * Behavor for when the mode resumes from a pause state
     */
    public void resume() {}
    
    /**
     * Behavor for when the mode is shown/active
     */
	public void show() { active = true;}
    
    /**
     * Behavor for when the mode is set to stop showing/ is inactive
     */
	public void hide() {
		reset();
		active = false;
	}
}