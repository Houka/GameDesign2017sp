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
import edu.cornell.gdiac.game.Constants;
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
	private static final int TOTAL_COLUMNS = 3;
	private static final int BORDER_X = 100;
	private static final int BORDER_Y = 50;
	private static final int PADDING_Y = 90;

	/** Textures necessary to support the screen */
	private static final String BACKGROUND_FILE = "ui/bg/level_selection.png";
	private static Color UNSELECTED_COLOR = new Color(36/255f, 39/255f, 18/255f, 1f);

	/** Level file finding vars **/
	private static String[] NUM_LEVELS = FileReaderWriter.getJsonFiles();
	private static int TOTAL_ROWS = (int)Math.ceil((float)NUM_LEVELS.length/TOTAL_COLUMNS);

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
		gameMode.setLevel(NUM_LEVELS[selected],selected); // set the default level
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
		canvas.getCamera().setAutosnap(true);
		gameMode.setLevel(NUM_LEVELS[selected],selected);
		listener.switchToScreen(this, gameMode.getName());
		gameMode.reset();
	}

	@Override
	protected void draw() {
		super.draw();
		drawSelectionSingle();
	}

	private void drawSelectionSingle(){
		displayFont.setColor(UNSELECTED_COLOR);
		canvas.drawTextCentered("<[" + (selected+1) +"]>", displayFont, 0);
	}

	private void drawSelectionMatrix(){
		for (int i = 0; i < TOTAL_COLUMNS; i++){
			for (int j = 0; j < TOTAL_ROWS; j++) {
				if (selected == convertToIndex(i,j))
					displayFont.setColor(Color.RED);
				else
					displayFont.setColor(UNSELECTED_COLOR);

				if (convertToIndex(i,j) < NUM_LEVELS.length) {
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
		size2Params.fontFileName = Constants.SELECTION_FONT_FILE;
		size2Params.fontParameters.size = Constants.SELECTION_FONT_SIZE;
		manager.load(Constants.SELECTION_FONT_FILE, BitmapFont.class, size2Params);
	}

	@Override
	public void loadContent(AssetManager manager) {
		background = AssetRetriever.createTextureRegion(manager, BACKGROUND_FILE, true).getTexture();

		// Allocate the font
		if (manager.isLoaded(Constants.SELECTION_FONT_FILE)) {
			displayFont = manager.get(Constants.SELECTION_FONT_FILE, BitmapFont.class);
		}

		// load levels
		NUM_LEVELS = FileReaderWriter.getJsonFiles();
		TOTAL_ROWS = (int)Math.ceil((float)NUM_LEVELS.length/TOTAL_COLUMNS);
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