/*
 * MenuMode.java
 *
 * The main menu that players will use to navigate the different modes/screen of the game
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import edu.cornell.gdiac.game.Camera2;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.GameModeManager;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.FileReaderWriter;
import edu.cornell.gdiac.util.sidebar.Sidebar;

import java.util.ArrayList;

/**
 * Class that provides a menu screen for the state of the game.
 */
public class PauseMode extends Mode {
	/** Background texture */
	private static final String BACKGROUND_FILE = "ui/bg/pause.png";
	/** Selection menu items y offset from the center*/
	private static final int MENU_ITEM_START_OFFSET_Y = 150;
	/** Selection menu items y offset between each menu item*/
	private static final int MENU_ITEM_GAP_OFFSET_Y = 5;
	private static String[] NUM_LEVELS = FileReaderWriter.getJsonFiles();

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;

	/** Player modes that are selectable from menu mode */
	private String[] modeNames = {"Resume","Previous Level","Next Level", "Main Menu", "Level Select"};
	private int selected = 0;

	/** Input controller for menu selection */
	private SelectionInputController input;

	/** The game mode **/
	private GameMode gameMode;

	/** The camera */
	private Camera2 camera;

	/**
	 * Creates a MenuMode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public PauseMode(String name, GameCanvas canvas, AssetManager manager, GameMode gameMode) {
		super(name, canvas, manager);
		this.gameMode = gameMode;
		input = SelectionInputController.getInstance();
		camera = new Camera2(canvas.getWidth(),canvas.getHeight());
		camera.position.set(canvas.getWidth()/2, 0, 0);
		camera.setTargetLocation(canvas.getWidth()/2, canvas.getHeight()/2);
		camera.snap();
		camera.setAutosnap(true);
	}

	// BEGIN: Setters and Getters
	// END: Setters and Getters

	@Override
	public void setScreenListener(ScreenListener listener) {
		super.setScreenListener(listener);
	}

	@Override
	protected void onComplete(){
		listener.switchToScreen(this, GameModeManager.GAME_MODE);
		gameMode.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
		input = null;
	}

	@Override
	protected void update(float delta) {
		input.readInput();

		if (input.didDown())
			selected=(selected+1) % modeNames.length;
		else if (input.didUp())
			selected=(selected-1 < 0)? modeNames.length-1 : selected-1;
		else if (input.didSelect()) {
			if (selected == 1) {
				int levelNum = Math.max(gameMode.getLevelNum() - 1,0);
				gameMode.setLevel(NUM_LEVELS[levelNum],levelNum);
			    listener.switchToScreen(this, gameMode.getName());
			    gameMode.reset();
			}
			else if (selected == 2) {
				listener.switchToScreen(this, gameMode.getName());
				gameMode.nextLevel();
			}
			else if (selected == 3) {
				listener.switchToScreen(this, GameModeManager.MENU);
				gameMode.nextLevel();
			}
			else if (selected == 4) {
				listener.switchToScreen(this, GameModeManager.LEVEL_SELECTION);
				gameMode.nextLevel();
			}
			else
				setComplete(true);
		}
	}

	@Override
	protected void draw() {
		if (background != null)
			canvas.draw(background, Constants.ALPHA, 0, 0, 0,0, 0f, scale.x, scale.y);
		canvas.setColor(Constants.WHITE);
		// draw menu items
		for (int i = 0; i<modeNames.length; i++) {
			if (selected == i)
				displayFont.setColor(Constants.SELECTED_COLOR_LIGHT);
			else
				displayFont.setColor(Constants.SELECTED_COLOR);
			canvas.drawTextCentered(modeNames[i], displayFont,
					(displayFont.getLineHeight()+ MENU_ITEM_GAP_OFFSET_Y)*-i+MENU_ITEM_START_OFFSET_Y);
		}
	}

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	@Override
	public void render(float delta) {
		if (active) {
			if (preUpdate(delta))
				update(delta);

			canvas.begin();
			gameMode.draw();
			canvas.end();

			canvas.begin(camera);
			draw();
			canvas.end();

			// We are are ready, notify our listener
			if (isExit() && listener != null)
				onExit();
			if (isComplete() && listener != null)
				onComplete();
		}
	}

	@Override
	public void pauseGame() {
		listener.switchToScreen(this, GameModeManager.GAME_MODE);
		gameMode.resume();
	}

	@Override
	public void resume() {
		listener.switchToScreen(this, GameModeManager.GAME_MODE);
		gameMode.resume();
	}

	@Override
	public void preLoadContent(AssetManager manager) {
		manager.load(BACKGROUND_FILE,Texture.class);

		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = Constants.MENU_FONT_FILE;
		size2Params.fontParameters.size = Constants.MENU_FONT_SIZE;
		manager.load(Constants.MENU_FONT_FILE, BitmapFont.class, size2Params);
	}

	@Override
	public void loadContent(AssetManager manager) {
		background = AssetRetriever.createTextureRegion(manager, BACKGROUND_FILE, true).getTexture();

		// Allocate the font
		if (manager.isLoaded(Constants.FONT_FILE))
			displayFont = manager.get(Constants.MENU_FONT_FILE, BitmapFont.class);
		else
			displayFont = null;

		// load level files
		NUM_LEVELS =FileReaderWriter.getJsonFiles();
	}

	@Override
	public void unloadContent(AssetManager manager) {
		if (manager.isLoaded(BACKGROUND_FILE))
			manager.unload(BACKGROUND_FILE);
		if (manager.isLoaded(Constants.FONT_FILE))
			manager.unload(Constants.FONT_FILE);
	}
}