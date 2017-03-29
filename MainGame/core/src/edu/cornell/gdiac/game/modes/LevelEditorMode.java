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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.controllers.CollisionController;
import edu.cornell.gdiac.game.entity.models.PlayerModel;
import edu.cornell.gdiac.game.input.EditorInputController;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.game.interfaces.Settable;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.Obstacle;
import edu.cornell.gdiac.util.sidebar.Sidebar;
import javafx.scene.image.*;
import javafx.scene.image.Image;

import javax.xml.soap.Text;
import java.awt.*;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import static edu.cornell.gdiac.game.entity.factories.PaintballFactory.applySettings;

/**
 * Class that provides a Level Editor screen for the state of the game.
 *
 * TODO: write class desc
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
	private EditorInputController input;
	/** The position of the level that the player is selecting */
	private int selected;
	/** Level loader */
	private LevelLoader levelLoader;
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

	private TextureRegion underMouse;
	private boolean textureClicked;
	private boolean justTouched;

	private PlayerModel player;


	/** Width of the game world in Box2d units	 */
	private static final float DEFAULT_WIDTH = 32.0f;
	/** Height of the game world in Box2d units	 */
	private static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down)	 */
	private static final float DEFAULT_GRAVITY = -20.0f;

	/** All the objects in the world.	 */
	private PooledList<Obstacle> objects = new PooledList<Obstacle>();

	/** array of textures */
	private TextureRegion[] regions;
	private int[] startHeights;

	/** The stage for drag and drop */
	Stage stage;

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
		super(canvas, manager);
		input = EditorInputController.getInstance();
		Gdx.input.setInputProcessor(input);
		textureClicked = false;
		onExit = ScreenListener.EXIT_MENU;
		scaleVector = new Vector2(canvas.getWidth() / bounds.getWidth(), canvas.getHeight() / bounds.getHeight());

		world = new World(gravity, false);
		levelLoader = new LevelLoader(scaleVector);
		this.bounds = new Rectangle(bounds);
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
		stage.dispose();

	}

	public void applySettings() {
		world.setGravity(new Vector2(0, -20.0f));
	}

	@Override
	protected void update(float delta) {
		input.readInput();
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();

		if(input.didTouch() && mouseX >= canvas.getWidth()-125) {
			for(int i=0; i<regions.length; i++) {
				Rectangle textureBounds=new Rectangle
						(canvas.getWidth()-125, canvas.getHeight() - startHeights[i] - (regions[i].getRegionHeight()),
								regions[i].getRegionWidth(),regions[i].getRegionHeight());

				if(textureBounds.contains(mouseX, mouseY)) {
					System.out.println("region y: "+ (canvas.getHeight()-startHeights[i]));
					System.out.println("mouse y: "+ mouseY);
					//System.out.println("clicked region " + i);
					underMouse = regions[i];
					textureClicked = true;
				}
			}
		}
		if(!input.didTouch()) {
			textureClicked = false;
		}

		applySettings();
		for(Obstacle obj: objects){
			if(obj instanceof Settable)
				((Settable) obj).applySettings();
		}

	}

	@Override
	protected void draw() {
		super.draw();

		TextureRegion editorRegion = new TextureRegion(editor);
		editorRegion.setRegion(0, 0,  canvas.getWidth(), canvas.getHeight());
		canvas.draw(editorRegion, Color.WHITE, canvas.getWidth()-200, 0, 200, canvas.getHeight());

		regions = new TextureRegion[5];
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

		int startHeight= 10;
		startHeights = new int[5];
		for (int i=0; i<regions.length; i++) {
			canvas.draw(regions[i], canvas.getWidth()-125, startHeight);
			startHeights[i] = startHeight;
			startHeight += regions[i].getRegionHeight() + 20;
		}
		if(textureClicked) {
			canvas.draw(underMouse, Gdx.input.getX(), canvas.getHeight()-Gdx.input.getY());
		}
		for(Obstacle o: objects) {
			o.draw(canvas);
		}
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
		editor = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true).getTexture();
		player = AssetRetriever.createTexture(manager, PLAYER_FILE, true).getTexture();
		enemy = AssetRetriever.createTexture(manager, ENEMY_FILE, true).getTexture();
		platform = AssetRetriever.createTexture(manager, PLATFORM_FILE, true).getTexture();
		ammoDepot = AssetRetriever.createTexture(manager, AMMO_DEPOT_FILE, true).getTexture();
		camera = AssetRetriever.createTexture(manager, CAMERA_FILE, true).getTexture();
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
}