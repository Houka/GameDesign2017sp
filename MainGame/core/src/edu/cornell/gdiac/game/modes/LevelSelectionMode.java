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
import edu.cornell.gdiac.game.GameCanvas;

/**
 * Class that provides a Level Selection screen for the state of the game.
 *
 * TODO: write class desc
 */
public class LevelSelectionMode extends Mode {
	// Textures necessary to support the loading screen
	private static final String BACKGROUND_FILE = "menu/bg/menu.png";

	/**
	 * Creates a LevelSelectionMode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public LevelSelectionMode(GameCanvas canvas, AssetManager manager) {
		super(canvas, manager);

		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		background = new Texture(BACKGROUND_FILE);
	}

	// BEGIN: Setters and Getters

	// END: Setters and Getters

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	protected void update(float delta) {

	}

	@Override
	protected void draw() {
		super.draw();
	}
}