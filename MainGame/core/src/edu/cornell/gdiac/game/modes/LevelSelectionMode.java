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

/**
 * Class that provides a Level Selection screen for the state of the game.
 *
 * TODO: write class desc
 */
public class LevelSelectionMode extends Mode {
	// TODO: remove this once we have working json file loading in assetmanager
	private static final int NUM_LEVELS = 20;
	private static final int TOTAL_COLUMNS = 10;
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
	private Vector2 selected;

	/**
	 * Creates a LevelSelectionMode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public LevelSelectionMode(GameCanvas canvas, AssetManager manager) {
		super(canvas, manager);
		onExit = ScreenListener.EXIT_MENU;

		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		input = SelectionInputController.getInstance();
		selected = new Vector2(0,0);
	}

	// BEGIN: Setters and Getters

	// END: Setters and Getters

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	protected boolean preUpdate(float dt){
		if (super.preUpdate(dt)) {
			input.readInput();
			return true;
		}

		return false;
	}

	@Override
	protected void update(float delta) {
		if (input.didUp())
			selected.y=(selected.y >= NUM_LEVELS/TOTAL_COLUMNS - 1)? 0:selected.y+1;
		else if (input.didDown())
			selected.y=(selected.y <= 0)? NUM_LEVELS/TOTAL_COLUMNS-1 : selected.y-1;
		else if (input.didRight())
			selected.x=(selected.x+1) % TOTAL_COLUMNS;
		else if (input.didLeft())
			selected.x=(selected.x <= 0)? TOTAL_COLUMNS-1 : selected.x-1;
		else if (input.didSelect())
			setComplete(true);
	}

	@Override
	protected void draw() {
		super.draw();

		for (int i = 0; i < TOTAL_COLUMNS; i++){
			for (int j = 0; j < NUM_LEVELS/TOTAL_COLUMNS; j++) {
				if ((selected.x + (selected.y*TOTAL_COLUMNS)) == (i + (j*TOTAL_COLUMNS)))
					displayFont.setColor(Color.RED);
				else
					displayFont.setColor(Color.WHITE);
				canvas.drawText("" + ((i+1) + (j*TOTAL_COLUMNS)), displayFont,
						i * ((canvas.getWidth()-BORDER_X*2)/TOTAL_COLUMNS)+BORDER_X,
						canvas.getHeight() - j * displayFont.getLineHeight() - BORDER_Y);
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
		background = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true).getTexture();

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

	@Override
	public void reset(){
		super.reset();
		selected.set(0,0);
	}
}