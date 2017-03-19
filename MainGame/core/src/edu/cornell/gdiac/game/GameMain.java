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

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

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
	/** Mode Controller that loads and unloads different player modes (CONTROLLER CLASS) */
	private MenuMode menuMode;
	
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
		FileHandleResolver resolver = new InternalFileHandleResolver();
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
		loadingMode = new LoadingMode(canvas,manager,1);
		menuMode = new MenuMode(canvas, manager);
		//Sidebar sidebar = new Sidebar(this); //TODO: remove
		loadingMode.setScreenListener(this);
		menuMode.setScreenListener(this);

		menuMode.preLoadContent(manager);

		setScreen(loadingMode);
		bootupSidebarTool();

	}

	/**
	 * Call this to boot up the SidebarTool
	 */
	public void bootupSidebarTool(){

		Sidebar.bootUp();
		//Below fields are just samples
		Sidebar.addSlider("Gravity",-2.5f,2.5f,0);
		Sidebar.addSlider("Jump Height",0f,5f,1);
		Sidebar.addSlider("Player Speed",5f,7.5f,6);
		Sidebar.addSlider("Bullet Width",1f,6.5f,5.5f);

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

		menuMode.unloadContent(manager);
		menuMode.dispose();
		menuMode = null;

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
		canvas.resize();
		super.resize(width,height);
	}
	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value onExit can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		switch (exitCode){
			case EXIT_NOP:
				break;
			case EXIT_QUIT:
				// We quit the main application
				Gdx.app.exit();
				break;
			case EXIT_MENU:
				if (loadingMode != null) {
					loadingMode.dispose();
					loadingMode = null;
					menuMode.loadContent(manager);
				}

				setScreen(menuMode);
			default:
				break;
		}
	}

	/**
	 * The given screen has made a request to change its screen.
	 *
	 * @param from  The screen requesting to exit
	 * @param to 	The screen to change to
	 */
	public void switchScreens(Screen from, Screen to){
		setScreen(to);
	}
}
