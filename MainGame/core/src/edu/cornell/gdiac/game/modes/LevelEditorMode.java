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

import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.controllers.CollisionController;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.game.levelLoading.LevelCreator;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.Obstacle;

import javax.xml.soap.Text;

/**
 * Class that provides a Level Editor screen for the state of the game.
 * 
 * The level editor screen allows players to create/edit their own levels
 */
public class LevelEditorMode extends Mode {
	// Textures necessary to support the loading screen
	private static final String BACKGROUND_FILE = "ui/bg/level_editor.png";
	private static final String PLAYER_FILE = "sprites/char/char_idle.png";
	private static final String ENEMY_FILE = "sprites/enemy/enemy_idle.png";
	private static final String AMMO_DEPOT_FILE = "sprites/paint_repo.png";
	private static final String PLATFORM_FILE = "sprites/fixtures/solid.png";
	private static final String CAMERA_FILE = "sprites/security_camera.png";

	/** Input controller */
	private SelectionInputController input;
	/** Level loader */
	private LevelLoader levelLoader;
	/** Level creator */
	private LevelCreator levelCreator;
	/** File of level */
	private String levelFile;
	/** World */
	private World world;
	/** World bounds */
	private Rectangle bounds;
	/** Scale for world */
	private Vector2 scaleVector;
	/** Texture for side-bar*/
	protected Texture editor;
	/** Texture for side-bar*/
	protected Texture player;
	/** Texture for side-bar*/
	protected Texture enemy;
	/** Texture for side-bar*/
	protected Texture platform;
	/** Texture for side-bar*/
	protected Texture ammoDepot;
	/** Texture for side-bar*/
	protected Texture camera;
	private GameCanvas canvas;
	/** Retro font for displaying messages */
	private static String FONT_FILE = "fonts/RetroGame.ttf";
	private static int FONT_SIZE = 16;

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;


	/** Width of the game world in Box2d units	 */
	private static final float DEFAULT_WIDTH = 32.0f;
	/** Height of the game world in Box2d units	 */
	private static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down)	 */
	private static final float DEFAULT_GRAVITY = -20.0f;

	/** The amount of time for a physics engine step.	 */
	public static final float WORLD_STEP = 1 / 60.0f;
	/** Number of velocity iterations for the constrain solvers	 */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers	 */
	public static final int WORLD_POSIT = 2;

	/** All the objects in the world.	 */
	private PooledList<Obstacle> objects = new PooledList<Obstacle>();

	/**
	 * Creates a new game world with the default values.
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param canvas  The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 */
	public LevelEditorMode(GameCanvas canvas, AssetManager manager) {
		this(canvas, manager, new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT),
				new Vector2(0, DEFAULT_GRAVITY));
	}

	/**
	 * Creates a new game world
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param canvas  The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 * @param width   The width in Box2d coordinates
	 * @param height  The height in Box2d coordinates
	 * @param gravity The downward gravity
	 */
	public LevelEditorMode(GameCanvas canvas, AssetManager manager, float width, float height, float gravity) {
		this(canvas, manager, new Rectangle(0, 0, width, height), new Vector2(0, gravity));
	}

	/**
	 * Creates a new game world
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param canvas  The GameCanvas to draw the textures to
	 * @param manager The AssetManager to load in the background
	 * @param bounds  The game bounds in Box2d coordinates
	 * @param gravity The gravitational force on this Box2d world
	 */
	public LevelEditorMode(GameCanvas canvas, AssetManager manager, Rectangle bounds, Vector2 gravity) {
		super(name ,canvas, manager);
		onExit = ScreenListener.EXIT_MENU;
		scaleVector = new Vector2(canvas.getWidth() / bounds.getWidth(), canvas.getHeight() / bounds.getHeight());

		world = new World(gravity, false);
		levelLoader = new LevelLoader(scaleVector);
		levelCreator = new LevelCreator();
		this.bounds = new Rectangle(bounds);
	}

	public LevelEditorMode(String name, GameCanvas canvas, AssetManager manager) {
		super(name, canvas, manager);
	}

	// BEGIN: Setters and Getters
	// END: Setters and Getters

	@Override
	public void dispose() {
		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		world.dispose();
		levelLoader.dispose();
		levelLoader = null;
		objects = null;
		bounds = null;
		scaleVector = null;
		world = null;
		canvas = null;

	}

	@Override
	protected void update(float delta) {
		while (!levelLoader.getAddQueue().isEmpty())
			addObject(levelLoader.getAddQueue().poll());
		world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);
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

		// Draw the save/load/cancel buttons into the top sidebar
		canvas.drawText("SAVE", displayFont, 10,
				canvas.getHeight() - 20);
		canvas.drawText("LOAD", displayFont, 250,
				canvas.getHeight() - 20);
		canvas.drawText("CANCEL", displayFont, 500,
				canvas.getHeight() - 20);
	}

	@Override
	public void preLoadContent(AssetManager manager) {
//		InternalFileHandleResolver resolver = new InternalFileHandleResolver();
//		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
//		manager.setLoader(BitmapFont.class, ".png", new FreetypeFontLoader(resolver));

		manager.load(BACKGROUND_FILE,Texture.class);
		manager.load(ENEMY_FILE,Texture.class);
		manager.load(PLAYER_FILE,Texture.class);
		manager.load(AMMO_DEPOT_FILE,Texture.class);
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = FONT_FILE;
		size2Params.fontParameters.size = FONT_SIZE;
		manager.load(FONT_FILE, BitmapFont.class, size2Params);
		levelLoader.preLoadContent(manager);
	}

	@Override
	public void loadContent(AssetManager manager) {
		levelLoader.loadContent(manager);
		editor = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true).getTexture();
		player = AssetRetriever.createTexture(manager, PLAYER_FILE, true).getTexture();
		enemy = AssetRetriever.createTexture(manager, ENEMY_FILE, true).getTexture();
		platform = AssetRetriever.createTexture(manager, PLATFORM_FILE, true).getTexture();
		ammoDepot = AssetRetriever.createTexture(manager, AMMO_DEPOT_FILE, true).getTexture();
		camera = AssetRetriever.createTexture(manager, CAMERA_FILE, true).getTexture();
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

	public void loadLevel(String levelFile) {
		this.levelFile = levelFile;
		levelLoader.loadLevel(levelFile);
		bounds = levelLoader.getBounds();
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * @param obj The object to add
	 */
	private void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);

		//This gives the objects in the game screen physics (possibly remove)
		obj.activatePhysics(world);

		//addEntityController(obj);
	}

	/**
	 * Returns true if the object is in bounds.
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 * @return true if the object is in bounds.
	 */
	private boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x + bounds.width);
		boolean vert = (bounds.y <= obj.getY() && obj.getY() <= bounds.y + bounds.height);
		return horiz && vert;
	}

	private void saveLevel() {

	}
}