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
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;

/**
 * Class that provides a menu screen for the state of the game.
 *
 * TODO: write class desc
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
	private Mode[] modes;
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
	public MenuMode(GameCanvas canvas, AssetManager manager) {
		super(canvas, manager);
		onExit = ScreenListener.EXIT_QUIT;
		modes = new Mode[]{
			new LevelSelectionMode(canvas, manager),
			new LevelEditorMode(canvas, manager)
		};

		input = SelectionInputController.getInstance();
	}

	// BEGIN: Setters and Getters

	// END: Setters and Getters

	@Override
	public void setScreenListener(ScreenListener listener) {
		super.setScreenListener(listener);
		for(Mode m : modes)
			m.setScreenListener(listener);
	}

	@Override
	protected void onComplete(){
		if (selected == modeNames.length-1)
			super.onComplete();
		else {
			modes[selected].loadContent(manager);
			listener.switchScreens(this, modes[selected]);
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		for(int i = 0; i < modes.length; i++)	{
			modes[i].dispose();
			modes[i] = null;
		}
	}

	@Override
	protected void update(float delta) {
		input.readInput();

		if (input.didDown())
			selected=(selected+1) % modeNames.length;
		else if (input.didUp())
			selected=(selected-1 < 0)? modeNames.length-1 : selected-1;
		else if (input.didSelect())
			setComplete(true);
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

		// preload content for children
		for(Mode m : modes)
			m.preLoadContent(manager);
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
		if (manager.isLoaded(BACKGROUND_FILE))
			manager.unload(BACKGROUND_FILE);
		if (manager.isLoaded(FONT_FILE))
			manager.unload(FONT_FILE);
		for(Mode m: modes)
			m.unloadContent(manager);
	}
}