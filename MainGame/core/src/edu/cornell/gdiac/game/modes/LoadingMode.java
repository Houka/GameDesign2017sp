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

import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.*;

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
public class LoadingMode extends Mode {
	// Textures necessary to support the loading screen 
	private static final String BACKGROUND_FILE = "menu/bg/loading.png";
	private static final String PROGRESS_FILE = "menu/progressbar.png";

	/** Texture atlas to support a progress bar */
	private Texture statusBar;
	
	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */
	private TextureRegion statusBkgLeft;
	/** Middle portion of the status background (grey region) */
	private TextureRegion statusBkgMiddle;
	/** Right cap to the status background (grey region) */
	private TextureRegion statusBkgRight;
	/** Left cap to the status forground (colored region) */
	private TextureRegion statusFrgLeft;
	/** Middle portion of the status forground (colored region) */
	private TextureRegion statusFrgMiddle;
	/** Right cap to the status forground (colored region) */
	private TextureRegion statusFrgRight;	

	/** Ratio of the bar width to the screen */
	private static float BAR_WIDTH_RATIO  = 0.66f;
	/** Ration of the bar height to the screen */
	private static float BAR_HEIGHT_RATIO = 0.25f;	
	/** Height of the progress bar */
	private static int PROGRESS_HEIGHT = 30;
	/** Width of the rounded cap on left or right */
	private static int PROGRESS_CAP    = 15;
	/** Width of the middle portion in texture atlas */
	private static int PROGRESS_MIDDLE = 200;


	/** The width of the progress bar */	
	private int width;
	/** The y-coordinate of the center of the progress bar */
	private int centerY;
	/** The x-coordinate of the center of the progress bar */
	private int centerX;

	/** Current progress (0 to 1) of the asset manager */
	private float progress;
	/** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
	private int   budget;

	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 * @param millis The loading budget in milliseconds
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager, int millis) {
		super(canvas, manager, ScreenListener.EXIT_MENU);
		budget = millis;
		
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		// Load the next two images immediately.
		background = new Texture(BACKGROUND_FILE);
		statusBar  = new Texture(PROGRESS_FILE);
		
		// No progress so far.		
		progress   = 0;

		// Break up the status bar texture into regions
		statusBkgLeft   = new TextureRegion(statusBar,0,0,PROGRESS_CAP,PROGRESS_HEIGHT);
		statusBkgRight  = new TextureRegion(statusBar,statusBar.getWidth()-PROGRESS_CAP,0,PROGRESS_CAP,PROGRESS_HEIGHT);
		statusBkgMiddle = new TextureRegion(statusBar,PROGRESS_CAP,0,PROGRESS_MIDDLE,PROGRESS_HEIGHT);

		int offset = statusBar.getHeight()-PROGRESS_HEIGHT;
		statusFrgLeft   = new TextureRegion(statusBar,0,offset,PROGRESS_CAP,PROGRESS_HEIGHT);
		statusFrgRight  = new TextureRegion(statusBar,statusBar.getWidth()-PROGRESS_CAP,offset,PROGRESS_CAP,PROGRESS_HEIGHT);
		statusFrgMiddle = new TextureRegion(statusBar,PROGRESS_CAP,offset,PROGRESS_MIDDLE,PROGRESS_HEIGHT);

		// this mode is the current one
		show();
	}

	// BEGIN: Setters and Getters
	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param millis the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}
	// END: Setters and Getters
	
	@Override
	public void dispose() {
		super.dispose();
		statusBkgLeft = null;
		statusBkgRight = null;
		statusBkgMiddle = null;

		statusFrgLeft = null;
		statusFrgRight = null;
		statusFrgMiddle = null;

		statusBar.dispose();
		statusBar  = null;
	}
	
	@Override
	protected void update(float delta) {
		manager.update(budget);
		this.progress = manager.getProgress();

		// set completion flag
		setComplete(progress >= 1.0f);
	}

	@Override
	protected void draw() {
		super.draw();

		// other elements specific to this mode
		drawProgress(canvas);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width,height);

		// Compute the drawing scale for the progress bar
		this.width = (int)(BAR_WIDTH_RATIO*width);
		centerY = (int)(BAR_HEIGHT_RATIO*height);
		centerX = width/2;
	}
	
	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, 
	 * and a rectangle in a middle.  We adjust the size of the rectangle in
	 * the middle to represent the amount of progress.
	 *
	 * @param canvas The drawing context
	 */	
	private void drawProgress(GameCanvas canvas) {	
		canvas.draw(statusBkgLeft,   Color.WHITE, centerX-width/2, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		canvas.draw(statusBkgRight,  Color.WHITE, centerX+width/2-scale*PROGRESS_CAP, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		canvas.draw(statusBkgMiddle, Color.WHITE, centerX-width/2+scale*PROGRESS_CAP, centerY, width-2*scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);

		canvas.draw(statusFrgLeft,   Color.WHITE, centerX-width/2, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		if (progress > 0) {
			float span = progress*(width-2*scale*PROGRESS_CAP)/2.0f;
			canvas.draw(statusFrgRight,  Color.WHITE, centerX-width/2+scale*PROGRESS_CAP+span, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
			canvas.draw(statusFrgMiddle, Color.WHITE, centerX-width/2+scale*PROGRESS_CAP, centerY, span, scale*PROGRESS_HEIGHT);
		} else {
			canvas.draw(statusFrgRight,  Color.WHITE, centerX-width/2+scale*PROGRESS_CAP, centerY, scale*PROGRESS_CAP, scale*PROGRESS_HEIGHT);
		}
	}

	// Unused functions because its loading mode
	public void preLoadContent(AssetManager manager) {}
	public void loadContent(AssetManager manager) {}
	public void unloadContent(AssetManager manager) {}
}