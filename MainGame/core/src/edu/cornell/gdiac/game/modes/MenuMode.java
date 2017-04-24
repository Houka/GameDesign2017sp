/*
 * MenuMode.java
 *
 * The main menu that players will use to navigate the different modes/screen of the game
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.GameModeManager;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;

/**
 * Class that provides a menu screen for the state of the game.
 */
public class MenuMode extends Mode {
	/** Background texture */
	private static final String BACKGROUND_FILE = "ui/bg/menu.png";
	/** Selection menu items y offset from the center*/
	private static final int MENU_ITEM_START_OFFSET_Y = 150;
	/** Selection menu items y offset between each menu item*/
	private static final int MENU_ITEM_GAP_OFFSET_Y = 15;
	private static Color SELECTED_COLOR = new Color(163/255f, 61/255f, 26/255f, 1f);
	private static Color UNSELECTED_COLOR = new Color(36/255f, 39/255f, 18/255f, 1f);

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;

	/** Player modes that are selectable from menu mode */
	private String[] modes = {GameModeManager.GAME_MODE, GameModeManager.LEVEL_SELECTION, GameModeManager.LEVEL_EDITOR};
	private String[] modeNames = {"PLAY", "LEVEL SELECT","Level Editor",  "QUIT"};
	private int selected = 0;

	/** Input controller for menu selection */
	private SelectionInputController input;

	private GameMode gameMode;

	/**
	 * Creates a MenuMode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public MenuMode(String name, GameCanvas canvas, AssetManager manager, GameMode gameMode) {
		super(name, canvas, manager);
		onExit = ScreenListener.EXIT_QUIT;
		input = SelectionInputController.getInstance();
		this.gameMode = gameMode;
	}

	// BEGIN: Setters and Getters
	// END: Setters and Getters

	@Override
	public void setScreenListener(ScreenListener listener) {
		super.setScreenListener(listener);
	}

	@Override
	protected void onComplete(){
		if (selected < modes.length)
			listener.switchToScreen(this, modes[selected]);
		if (selected == 0)
			gameMode.reset();
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
			if (selected == modeNames.length-1)
				setExit(true);
			else
				setComplete(true);
		}

		if (gameMode.getLevelNum() != 0)
			modeNames[0] = "CONTINUE";
	}

	@Override
	protected void draw() {
		super.draw();

		// draw menu items
		for (int i = 0; i<modeNames.length; i++) {
			if (selected == i)
				displayFont.setColor(SELECTED_COLOR);
			else
				displayFont.setColor(UNSELECTED_COLOR);
			canvas.drawText(modeNames[i], displayFont, canvas.getWidth()/2 + 150,
					canvas.getHeight()/2 - 50 + (displayFont.getLineHeight()+ MENU_ITEM_GAP_OFFSET_Y)*-i+MENU_ITEM_START_OFFSET_Y);
		}
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
		if (manager.isLoaded(Constants.MENU_FONT_FILE))
			displayFont = manager.get(Constants.MENU_FONT_FILE, BitmapFont.class);
		else
			displayFont = null;
	}

	@Override
	public void unloadContent(AssetManager manager) {
		if (manager.isLoaded(BACKGROUND_FILE))
			manager.unload(BACKGROUND_FILE);
		if (manager.isLoaded(Constants.FONT_FILE))
			manager.unload(Constants.FONT_FILE);
	}
}