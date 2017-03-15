/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.MainInputController;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.game.interfaces.Completable;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public abstract class Mode implements Screen, Completable, AssetUser {
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;

	/** Scaling factor for when the student changes the resolution. */
	protected float scale;
	/** Background texture for start-up */
	protected Texture background;

	/** AssetManager to be loading in the background */
	protected AssetManager manager;
	/** Reference to GameCanvas created by the root */
	protected GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	protected ScreenListener listener;

	/** The exit code for when this screen completes */
	private int exitCode; // default it to non existant exit code
	/** Whether or not this mode is completed*/
	private boolean completed;
	/** Whether or not this mode is still active */
	private boolean active;
	/** Whether or not debug mode is active */
	private boolean debug;
	/** The main input controller */
	private MainInputController input;

	/**
	 * TODO: write description for Constructor
	 *
	 * @param canvas The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 */
	protected Mode(GameCanvas canvas, AssetManager manager) {
		this(canvas, manager, ScreenListener.EXIT_NOP);
	}

	/**
	 * TODO: write description for Constructor
	 *
	 * @param canvas The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 * @param exitCode The exit code for when this mode completes
	 */
	protected Mode(GameCanvas canvas, AssetManager manager, int exitCode) {
		this.manager = manager;
		this.canvas  = canvas;
		this.exitCode = exitCode;
		active = false;
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
		if(input.didExit()) {
			setComplete(true);
			return false;
		}else if (input.didDebug())
			debug = !debug;
		else if (input.didReset())
			reset();

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
			canvas.draw(background, 0, 0);
	}

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
			if (isComplete() && listener != null) {
				onComplete();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// Compute the drawing scale
		float sx = ((float)width)/STANDARD_WIDTH;
		float sy = ((float)height)/STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);
	}

	public void reset(){
		setComplete(false);
	}

	/***
	 * TODO: write spec
	 */
	protected void onComplete(){
		listener.exitScreen(this, exitCode);
	}

	// Unused functions for a mode
	public void pause() {}
	public void resume() {}
	public void show() { active = true;}
	public void hide() {active = false;}
}