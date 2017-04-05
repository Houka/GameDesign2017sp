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
import edu.cornell.gdiac.game.interfaces.*;

/**
 * Class that provides the fundamental mode functionalities
 */
public abstract class Mode implements Screen, Completable, AssetUser, Exitable, Nameable {
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
	/** The name of this mode */
	protected String name;

	/** The exit code for when this screen completes */
	protected int onExit = ScreenListener.EXIT_ESC;
	/** Whether or not this mode is completed*/
	private boolean exit;
	/** Whether or not this mode is completed*/
	private boolean completed;
	/** Whether or not this mode is still active */
	private boolean active;
	/** Whether or not this mode is paused */
	private boolean paused;
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
	protected Mode(String name, GameCanvas canvas, AssetManager manager) {
		this.name = name;
		this.manager = manager;
		this.canvas  = canvas;
		scale = new Vector2(1,1);
		paused = false;
		active = false;
		exit = false;
		completed = false;
		debug  = false;
		input = MainInputController.getInstance();
	}

	// BEGIN: Getters and Setters
	public void setName(String value){name = value;}
	public String getName(){ return name;}
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
		else if (input.didPause())
			processPause();
		else if(input.didExit()) {
			setExit(true);
			return false;
		}
		return !paused;
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

	/***
	 * Handles a pause button press
	 */
	public void processPause() {
		if(!paused)
			pauseGame();
		else
			resume();
	}
	protected void onComplete(){
		onExit();
	}
  
    /**
     * Behavior for when the mode wishes to exit
     */
	private void onExit(){ listener.exitScreen(this, onExit); }

    /**
     * Behavor for when the mode is paused
     */
	public void pauseGame() {paused = true;}
  
    /**
     * Behavor for when the mode is paused
     */
	public void pause() {}
	
    /**
     * Behavor for when the mode resumes from a pause state
     */
	public void resume() {paused = false;}
	  
    /**
     * Behavor for when the mode is shown/active
     */
	public void show() {
		reset();
		active = true;
	}
    
    /**
     * Behavor for when the mode is set to stop showing/ is inactive
     */
	public void hide() {
		active = false;
	}
}