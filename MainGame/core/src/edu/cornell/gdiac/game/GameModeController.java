/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
 package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.modes.LevelEditorMode;
import edu.cornell.gdiac.game.modes.LevelSelectionMode;
import edu.cornell.gdiac.game.modes.MenuMode;
import edu.cornell.gdiac.game.modes.Mode;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * TODO: class definition
 */
public class GameModeController implements ScreenListener, Disposable {
	// Modes
	/** Player modes*/
	private Mode[] modes;

	/** The current player mode screen*/
	private Screen currentScreen;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/**
	 * Creates a new mode controller from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 *
	 * TODO: param definitions
	 */
	public GameModeController(GameCanvas canvas, AssetManager manager) {
		modes = new Mode[]{new MenuMode(canvas, manager), new LevelSelectionMode(canvas, manager),
				new LevelEditorMode(canvas, manager)};

		// set screen listener of all modes to this
		for (Mode m : modes)
			m.setScreenListener(this);
	}

	// BEGIN: Getters and Setters
	/**
	 * TODO: fill out function definition
	 */
	public Screen getCurrentScreen(){ return currentScreen; }

	/**
	 * TODO: fill out function definition
	 */
	public void setScreenListener(ScreenListener listener){ this.listener = listener; }

	// END: Getters and Setters

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		for (Mode m : modes)
			m.dispose();
		currentScreen = null;
	}
	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		switch (exitCode){
			case EXIT_MENU:
				currentScreen = modes[0];
				break;
			case EXIT_LEVEL_SELECTION:
				currentScreen = modes[1];
				break;
			case EXIT_LEVEL_EDITOR:
				currentScreen = modes[2];
				break;
			default:
				break;
		}

		// propagate upwards
		currentScreen.show();
		listener.exitScreen(screen, exitCode);
	}

}
