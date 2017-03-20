 /*
 * ScreenListener.java
 *
 * This interface is necessary because we are now using the Game/Screen pattern instead
 * of the ApplicationAdapter/GameMode pattern of the first lab.  We switched to this
 * patterns because of graphical artifacts that we found in that pattern.
 *
 * In this pattern, the root class is no longer explicitly rendering the player modes.
 * This means that we cannot poll when it is time to exit a player mode (e.g. the 
 * loading screen is done).  Instead, the player mode (Screen) must notify the root
 * class (Game).
 *
 * It is a big rule in Software Engineering that now child controller retains a reference
 * to its parent class.  That would result in tight coupling.  Instead, we decouple this
 * relationship with a listener interface.  This type of decoupling is taught in CS 2112.
 *
 * We have moved this class to a util package so that you do not waste time looking
 * at the code.
 *
 * Author: Walker M. White
 * Based on original Optimization Lab by Don Holden, 2007
 * LibGDX version, 2/2/2015
 */
 package edu.cornell.gdiac.game.interfaces;

import com.badlogic.gdx.Screen;

/**
 * A listener class for responding to a screen's request to exit.
 *
 * This interface is almost always implemented by the root Game class.
 */
public interface ScreenListener {
	// Exit Codes
	/** Default exit code for a NOP */
	int EXIT_NOP = -1;
	/** Exit code for quitting the game */
	int EXIT_QUIT = 0;
	/** Exit code for jumping back to previous screen */
	int EXIT_MENU = 1;
	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value onExit can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	void exitScreen(Screen screen, int exitCode);

	/**
	 * The given screen has made a request to change its screen.
	 *
	 * @param from  The screen requesting to exit
	 * @param to 	The screen to change to
	 */
	void switchScreens(Screen from, Screen to);
}
