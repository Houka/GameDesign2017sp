/*
 * LevelEditorMode.java
 *
 * Mode for level editing. This class sets up an interactive level editing
 * screen so players can take any element that is avalible in game and
 * add it to a layout. This layout should look very much like a typical level
 * in game and once, completed it will pass the information off to LevelCreator
 * to have a json made of the created level. 
 *
 * Other functionalities include being able to load json levels in and editing them. 
 *
 * Author: Changxu Lu
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;

/**
 * Class that provides a Level Editor screen for the state of the game.
 * 
 * The level editor screen allows players to create/edit their own levels
 */
public class LevelEditorMode extends Mode {
	// Textures necessary to support the loading screen
	private static final String BACKGROUND_FILE = "ui/bg/level_editor.png";

	/**
	 * Creates a Level Editor Mode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public LevelEditorMode(GameCanvas canvas, AssetManager manager) {
		super(canvas, manager);
		onExit = ScreenListener.EXIT_MENU;
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

	@Override
	public void preLoadContent(AssetManager manager) {
		manager.load(BACKGROUND_FILE,Texture.class);
	}

	@Override
	public void loadContent(AssetManager manager) {
		background = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true).getTexture();
	}

	@Override
	public void unloadContent(AssetManager manager) {
		if (manager.isLoaded(BACKGROUND_FILE)) {
			manager.unload(BACKGROUND_FILE);
		}
	}
}