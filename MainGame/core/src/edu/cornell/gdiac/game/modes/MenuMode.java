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
	private static final int MENU_ITEM_START_OFFSET_Y = 100;
	/** Selection menu items y offset between each menu item*/
	private static final int MENU_ITEM_GAP_OFFSET_Y = 20;
	/** Retro font for displaying messages */
	private static String FONT_FILE = "fonts/RetroGame.ttf";
	private static int FONT_SIZE = 64;

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;

	/** Player modes that are selectable from menu mode */
	private String[] modes = {GameModeManager.LEVEL_SELECTION, GameModeManager.LEVEL_EDITOR};
	private String[] modeNames = {"Select Level","Level Editor", "Quit"};
	private int selected = 0;

	/** Input controller for menu selection */
	private SelectionInputController input;

	/**
	 * Creates a MenuMode with the default size and position.
	 *
	 * @param canvas The GameCanvas to draw to
	 * @param manager The AssetManager to load in the background
	 */
	public MenuMode(String name, GameCanvas canvas, AssetManager manager) {
		super(name, canvas, manager);
		onExit = ScreenListener.EXIT_QUIT;
		input = SelectionInputController.getInstance();
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
			setComplete(true);
		}
	}

	@Override
	protected void draw() {
		super.draw();

		// draw menu items
		for (int i = 0; i<modeNames.length; i++) {
			if (selected == i)
				displayFont.setColor(Color.WHITE);
			else
				displayFont.setColor(Color.DARK_GRAY);
			canvas.drawTextCentered(modeNames[i], displayFont,
					(displayFont.getLineHeight()+ MENU_ITEM_GAP_OFFSET_Y)*-i+MENU_ITEM_START_OFFSET_Y);
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
		if (manager.isLoaded(BACKGROUND_FILE))
			manager.unload(BACKGROUND_FILE);
		if (manager.isLoaded(FONT_FILE))
			manager.unload(FONT_FILE);
	}
}