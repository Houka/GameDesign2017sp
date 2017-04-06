/*
 * LevelEditorMode.java
 *
 * Mode for level editing. This class sets up an interactive level editing
 * screen so players can take any element that is avalible in game and
 * add it to a layout. This layout should look very much like a typical level
 * in game and once, completed it will pass the information off to LevelCreator
 * to have a json made of the created level. 
 *
 * Other functionalities include being able to load json levels in and editing them. 
 *
 * Author: Changxu Lu
 */
package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.game.levelLoading.LevelCreator;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.Obstacle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;

/**
 * Class that provides a Level Editor screen for the state of the game.
 *
 * The level editor screen allows players to create/edit their own levels
 */
public class LevelEditorMode extends Mode {
	// Textures necessary to support the loading screen
	private static final String BACKGROUND_FILE = "ui/bg/level_editor.png";
	private static final String PLAYER_FILE = "sprites/char/char_still.png";
	private static final String ENEMY_FILE = "sprites/enemy/enemy_still.png";
	private static final String AMMO_DEPOT_FILE = "sprites/paint_repo.png";
	private static final String PLATFORM_FILE = "sprites/fixtures/solid.png";
	private static final String CAMERA_FILE = "sprites/security_camera.png";

	/** Dimensions of the game world in Box2d units	 */
	private static final float DEFAULT_WIDTH = 32.0f;
	private static final float DEFAULT_HEIGHT = 18.0f;

	/** Retro font for displaying messages */
	private static final String FONT_FILE = "fonts/RetroGame.ttf";

	/** Director of json files */
	private static final String JSON_DIRECTORY = "JSON";

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;

	/** Texture for side-bar*/
	protected Texture editor;
	protected Texture player;
	protected Texture enemy;
	protected Texture platform;
	protected Texture ammoDepot;
	protected Texture camera;

	/** Input controller */
	private SelectionInputController input;

	/** Level loader */
	private LevelLoader levelLoader;
	/** Level creator */
	private LevelCreator levelCreator;
	/** Scale for world */
	private Vector2 scaleVector;

	/** List of string paths to all json files found */
	private PooledList<String> jsonFiles;
	/** All the objects in the world.	 */
	private PooledList<Obstacle> objects = new PooledList<Obstacle>();

	/**
	 * Creates a new game world with the default values.
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param name 	  The name of this mode
	 * @param canvas  The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 */
	public LevelEditorMode(String name, GameCanvas canvas, AssetManager manager) {
		this(name, canvas, manager, new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	/**
	 * Creates a new game world
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param name 	  The name of this mode
	 * @param canvas  The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 * @param bounds  The game bounds in Box2d coordinates
	 */
	public LevelEditorMode(String name, GameCanvas canvas, AssetManager manager, Rectangle bounds) {
		super(name ,canvas, manager);
		scaleVector = new Vector2(canvas.getWidth() / bounds.getWidth(), canvas.getHeight() / bounds.getHeight());

		levelLoader = new LevelLoader(scaleVector);
		levelCreator = new LevelCreator();

		input = SelectionInputController.getInstance();

		// get initial json files
		jsonFiles = new PooledList<String>();
		jsonFiles.addAll(getAllJsonFiles());
	}

	// BEGIN: Setters and Getters
	private ArrayList<String> getAllJsonFiles(){
		return getJsonFiles(new ArrayList<String>(), Gdx.files.local(JSON_DIRECTORY).file());
	}

	private ArrayList<String> getJsonFiles(ArrayList<String> list, File directory)
	{
		for(File file: directory.listFiles()){
			if (file.isDirectory())
			{
				getJsonFiles(list, file);
			}
			String path = file.getPath();
			list.add(path.substring(path.indexOf(JSON_DIRECTORY),path.length()));
		}

		return list;
	}
	// END: Setters and Getters

	@Override
	public void dispose() {
		objects.clear();
		jsonFiles.clear();
		levelLoader.dispose();
		levelLoader = null;
		levelCreator = null;
		objects = null;
		jsonFiles = null;
		scaleVector = null;
		input = null;
	}

	@Override
	protected void update(float delta) {
		input.readInput();

		while (!levelLoader.getAddQueue().isEmpty())
			objects.add(levelLoader.getAddQueue().poll());
	}

	@Override
	protected void draw() {
		super.draw();

		// Draw the objects from the loaded level
		for (Obstacle obj : objects) {
			obj.draw(canvas);
		}

		// Draw the top and right sidebars for the editor
		TextureRegion editorRegion = new TextureRegion(editor);
		editorRegion.setRegion(0, 0,  canvas.getWidth(), canvas.getHeight());
		canvas.draw(editorRegion, Color.WHITE, canvas.getWidth()-180, 0, 200, canvas.getHeight());
		canvas.draw(editorRegion, Color.WHITE, 0, canvas.getHeight()-100, canvas.getWidth()-180, canvas.getHeight());

		// Create the sidebar textures (MISSING WALL TEXTURE)
		TextureRegion[] regions = new TextureRegion[5];
		regions[0] = new TextureRegion(player);
		regions[0].setRegion(0, 0,  player.getWidth(), player.getHeight());
		regions[1] = new TextureRegion(enemy);
		regions[1].setRegion(0, 0,  enemy.getWidth(), enemy.getHeight());
		regions[2] = new TextureRegion(platform);
		regions[2].setRegion(0, 0,  platform.getWidth(), platform.getHeight());
		regions[3] = new TextureRegion(ammoDepot);
		regions[3].setRegion(0, 0,  ammoDepot.getWidth(), ammoDepot.getHeight());
		regions[4] = new TextureRegion(camera);
		regions[4].setRegion(0, 0,  camera.getWidth(), camera.getHeight());

		// Draw the sidebar textures into the right sidebar
		int startHeight= 10;
		for (TextureRegion region:
				regions) {
			canvas.draw(region, canvas.getWidth()-115, startHeight);
			startHeight += region.getRegionHeight() + 50;
		}

		drawMenuButtons(canvas);
	}

	/**
	 *  Draws the save/load/cancel buttons into the top sidebar
	 */
	private void drawMenuButtons(GameCanvas canvas){
		canvas.drawText("SAVE", displayFont, 10,
				canvas.getHeight() - 20);
		canvas.drawText("LOAD", displayFont, 250,
				canvas.getHeight() - 20);
		canvas.drawText("CANCEL", displayFont, 500,
				canvas.getHeight() - 20);
	}

	@Override
	public void preLoadContent(AssetManager manager) {
		manager.load(BACKGROUND_FILE,Texture.class);
		manager.load(ENEMY_FILE,Texture.class);
		manager.load(PLAYER_FILE,Texture.class);
		manager.load(AMMO_DEPOT_FILE,Texture.class);
		levelLoader.preLoadContent(manager);
	}

	@Override
	public void loadContent(AssetManager manager) {
		levelLoader.loadContent(manager);
		editor = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true);
		player = AssetRetriever.createTexture(manager, PLAYER_FILE, true);
		enemy = AssetRetriever.createTexture(manager, ENEMY_FILE, true);
		platform = AssetRetriever.createTexture(manager, PLATFORM_FILE, true);
		ammoDepot = AssetRetriever.createTexture(manager, AMMO_DEPOT_FILE, true);
		camera = AssetRetriever.createTexture(manager, CAMERA_FILE, true);
		if (manager.isLoaded(FONT_FILE)) {
			displayFont = manager.get(FONT_FILE, BitmapFont.class);
			displayFont.getData().setScale(0.5f, 0.5f);
		}
		else
			displayFont = null;
	}

	@Override
	public void unloadContent(AssetManager manager) {
		if (manager.isLoaded(BACKGROUND_FILE)) {
			manager.unload(BACKGROUND_FILE);
		}
		if (manager.isLoaded(ENEMY_FILE)) {
			manager.unload(ENEMY_FILE);
		}
		if (manager.isLoaded(PLAYER_FILE)) {
			manager.unload(PLAYER_FILE);
		}
		if (manager.isLoaded(AMMO_DEPOT_FILE)) {
			manager.unload(AMMO_DEPOT_FILE);
		}
		if (manager.isLoaded(PLATFORM_FILE)) {
			manager.unload(PLATFORM_FILE);
		}
		if (manager.isLoaded(CAMERA_FILE)) {
			manager.unload(CAMERA_FILE);
		}
	}

	private void saveLevel() {

	}

	private void loadLevel(String levelFile) {
		levelLoader.loadLevel(levelFile);
	}
}