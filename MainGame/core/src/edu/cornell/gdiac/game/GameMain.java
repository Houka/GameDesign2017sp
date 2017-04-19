/*
 * GameMain.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX. 
 *
 * Author: Changxu Lu
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

import edu.cornell.gdiac.game.interfaces.Nameable;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.game.modes.LoadingMode;
import edu.cornell.gdiac.game.modes.MenuMode;
import edu.cornell.gdiac.util.sidebar.Sidebar;

import javax.swing.*;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GameMain extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loadingMode screen (CONTROLLER CLASS) */
	private LoadingMode loadingMode;

	/** Mode Controller that keeps track of different player modes and how to switch between them (CONTROLLER CLASS) */
	private GameModeManager gameModeManager;
	
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GameMain() {
		// Start loadingMode with the asset manager
		manager = new AssetManager();
		
		// Add font support to the asset manager
		FileHandleResolver resolver = new LocalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loadingMode screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loadingMode = new LoadingMode("loading", canvas,manager,1);
		gameModeManager = new GameModeManager(canvas, manager);

		loadingMode.setScreenListener(this);
		gameModeManager.setScreenListener(this);

		gameModeManager.preLoadContent(manager);

		setScreen(loadingMode);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		if (loadingMode != null)
			loadingMode.dispose();
		loadingMode = null;

		gameModeManager.dispose();
		gameModeManager = null;

		setScreen(null);

		canvas.dispose();
		canvas = null;
	
		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.setSize(width,height);
		canvas.resize();
		super.resize(width,height);
	}
	
	@Override
	public void exitScreen(Screen screen, int exitCode) {
		switch (exitCode){
			case EXIT_QUIT:
				exit();
				break;
			case EXIT_ESC:
				if (loadingMode != null) {
					loadingMode.dispose();
					loadingMode = null;
				}

				Screen to = gameModeManager.getExitToMode(((Nameable) screen).getName());
				if (to == null)
					exit();
				else
					setScreen(to);

				// TODO: function to reset camera position?
				canvas.setCameraY(canvas.getHeight()/2, 0);
				break;
			default:
				break;
		}
	}

	@Override
	public void switchToScreen(Screen from, String to){
		setScreen(gameModeManager.getMode(to));
	}

	private void exit(){
		Gdx.app.exit();
	}
}
