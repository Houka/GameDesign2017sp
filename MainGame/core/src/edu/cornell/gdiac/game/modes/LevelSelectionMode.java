/*
 * LevelSelectionMode.java
 *
 * Mode for level selecting. This class manages all the json levels that exists in the game
 * and gives the players control over which level to load and play. It limits what level
 * the player can choose to play and controls what level GameMode needs to load.
 *
 * Author: Changxu Lu
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.util.FileReaderWriter;

import java.util.ArrayList;

/**
 * Class that provides a Level Selection screen for the state of the game.
 */
public class LevelSelectionMode extends Mode {
	// TODO: remove this once we have working json file loading in assetmanager
	private static final ArrayList<String> NUM_LEVELS = FileReaderWriter.getJsonFiles();
	private static final int TOTAL_COLUMNS = 3;
	private static final int TOTAL_ROWS = (int)Math.ceil((float)NUM_LEVELS.size()/TOTAL_COLUMNS);
	private static final int BORDER_X = 100;
	private static final int BORDER_Y = 50;
	private static final int PADDING_Y = 90;

	/** Textures necessary to support the loading screen */
	private static final String BACKGROUND_FILE = "ui/bg/level_selection.png";
	/** Retro font for displaying messages */
	private static String FONT_FILE = "fonts/LightPixel7.ttf";
	private static int FONT_SIZE = 64;
	private static Color DARK_PURPLE = new Color(123/255f, 118/255f, 131/255f, 1f);

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
	public LevelSelectionMode(String name, GameCanvas canvas, AssetManager manager, GameMode gameMode) {
		super(name, canvas, manager);
		this.gameMode = gameMode;
		input = SelectionInputController.getInstance();
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
			selected = (selected+TOTAL_COLUMNS >= NUM_LEVELS.size())?	selected : selected+TOTAL_COLUMNS;
		else if (input.didUp())
			selected = (selected < TOTAL_COLUMNS) ? selected : selected - TOTAL_COLUMNS;
		else if (input.didRight())
			selected=(selected+1) % NUM_LEVELS.size();
		else if (input.didLeft())
			selected=(selected <= 0)? NUM_LEVELS.size() -1 : selected-1;
		else if (input.didSelect())
			setComplete(true);
	}

	@Override
	protected void onComplete(){
		canvas.getCamera().setAutosnap(true);
		System.out.println(NUM_LEVELS.get(selected));
		gameMode.setLevel(NUM_LEVELS.get(selected));
		listener.switchToScreen(this, gameMode.getName());
	}

	@Override
	protected void draw() {
		super.draw();

		for (int i = 0; i < TOTAL_COLUMNS; i++){
			for (int j = 0; j < TOTAL_ROWS; j++) {
				if (selected == convertToIndex(i,j))
					displayFont.setColor(Color.DARK_GRAY);
				else
					displayFont.setColor(DARK_PURPLE);

				if (convertToIndex(i,j) < NUM_LEVELS.size()) {
					canvas.drawText("[" + (convertToIndex(i, j) + 1)+"]", displayFont,
							i * ((canvas.getWidth() - BORDER_X * 2) / TOTAL_COLUMNS) + BORDER_X,
							canvas.getHeight() - j * PADDING_Y - BORDER_Y);
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
	}

	@Override
	public void loadContent(AssetManager manager) {
		background = AssetRetriever.createTextureRegion(manager, BACKGROUND_FILE, true).getTexture();

		// Allocate the font
		if (manager.isLoaded(FONT_FILE))
			displayFont = manager.get(FONT_FILE, BitmapFont.class);
		else
			displayFont = null;
	}

	@Override
	public void unloadContent(AssetManager manager) {
		if (manager.isLoaded(BACKGROUND_FILE)) {
			manager.unload(BACKGROUND_FILE);
		}
	}

    /**
     *  Converts a 2 dimensional array index mapping into the index of 
     *  a 1 dimensional array.
     *
     * @return the index in a 1d array that corresponds to the 2d array index
     */
	private int convertToIndex(int x, int y){
		return x + (y*TOTAL_COLUMNS);
	}
}