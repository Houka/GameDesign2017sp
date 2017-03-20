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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Class that provides a Level Selection screen for the state of the game.
 *
 * TODO: write class desc
 */
public class LevelSelectionMode extends Mode {
	// TODO: remove this once we have working json file loading in assetmanager
	private static final String[] NUM_LEVELS = {
			"JSON/defaultNoCeiling.json","JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json",
			"JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json",
			"JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json",
			"JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json","JSON/default.json"};
	private static final int TOTAL_COLUMNS = 10;
	private static final int TOTAL_ROWS = (int)Math.ceil((float)NUM_LEVELS.length/TOTAL_COLUMNS);
	private static final int BORDER_X = 20;
	private static final int BORDER_Y = 20;

	// Textures necessary to support the loading screen
	private static final String BACKGROUND_FILE = "menu/bg/levelSelection.png";
	/** Retro font for displaying messages */
	private static String FONT_FILE = "fonts/RetroGame.ttf";
	private static int FONT_SIZE = 64;

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;

	/** Input controller for menu selection */
	private SelectionInputController input;
	/** The position of the level that the player is selecting */
	private int selected;
	/** The Game mode where players can play*/
	private GameMode gameMode;


	/**
	 * Creates a LevelSelectionMode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public LevelSelectionMode(GameCanvas canvas, AssetManager manager) {
		super(canvas, manager);
		onExit = ScreenListener.EXIT_MENU;
		input = SelectionInputController.getInstance();
		gameMode = new GameMode(canvas,manager);
		selected = 0;
	}

	// BEGIN: Setters and Getters

	// END: Setters and Getters
	@Override
	public void setScreenListener(ScreenListener listener) {
		super.setScreenListener(listener);
		gameMode.setScreenListener(listener);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void update(float delta) {
		input.readInput();

		if (input.didDown())
			selected = (selected+TOTAL_COLUMNS >= NUM_LEVELS.length)?	selected : selected+TOTAL_COLUMNS;
		else if (input.didUp())
			selected = (selected < TOTAL_COLUMNS) ? selected : selected - TOTAL_COLUMNS;
		else if (input.didRight())
			selected=(selected+1) % NUM_LEVELS.length;
		else if (input.didLeft())
			selected=(selected <= 0)? NUM_LEVELS.length -1 : selected-1;
		else if (input.didSelect())
			setComplete(true);
	}

	@Override
	protected void onComplete(){
		gameMode.loadContent(manager);
		gameMode.loadLevel(NUM_LEVELS[selected]);
		// TODO: remove, for tech demo and testing values
		Sidebar.defaultBootup();
		listener.switchScreens(this, gameMode);
	}

	@Override
	protected void draw() {
		super.draw();

		for (int i = 0; i < TOTAL_COLUMNS; i++){
			for (int j = 0; j < TOTAL_ROWS; j++) {
				if (selected == convertToIndex(i,j))
					displayFont.setColor(Color.RED);
				else
					displayFont.setColor(Color.WHITE);

				if (convertToIndex(i,j) < NUM_LEVELS.length) {
					canvas.drawText("" + (convertToIndex(i, j) + 1), displayFont,
							i * ((canvas.getWidth() - BORDER_X * 2) / TOTAL_COLUMNS) + BORDER_X,
							canvas.getHeight() - j * displayFont.getLineHeight() - BORDER_Y);
				}else{
					break;
				}
			}
		}
	}

	@Override
	public void preLoadContent(AssetManager manager) {
		manager.load(BACKGROUND_FILE,Texture.class);

		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = FONT_FILE;
		size2Params.fontParameters.size = FONT_SIZE;
		manager.load(FONT_FILE, BitmapFont.class, size2Params);

		gameMode.preLoadContent(manager);
	}

	@Override
	public void loadContent(AssetManager manager) {
		background = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true).getTexture();

		// Allocate the font
		if (manager.isLoaded(FONT_FILE))
			displayFont = manager.get(FONT_FILE, BitmapFont.class);
		else
			displayFont = null;

		// TODO: loads all the level json files
	}

	@Override
	public void unloadContent(AssetManager manager) {
		if (manager.isLoaded(BACKGROUND_FILE)) {
			manager.unload(BACKGROUND_FILE);
		}

		gameMode.unloadContent(manager);
	}

	private int convertToIndex(int x, int y){
		return x + (y*TOTAL_COLUMNS);
	}
}