/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game.modes;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.controllers.CollisionController;
import edu.cornell.gdiac.game.entity.controllers.EnemyController;
import edu.cornell.gdiac.game.entity.controllers.EntityController;
import edu.cornell.gdiac.game.entity.controllers.PlayerController;
import edu.cornell.gdiac.game.entity.factories.PaintballFactory;
import edu.cornell.gdiac.game.entity.models.AmmoDepotModel;
import edu.cornell.gdiac.game.entity.models.EnemyModel;
import edu.cornell.gdiac.game.entity.models.HUDModel;
import edu.cornell.gdiac.game.entity.models.PlayerModel;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.game.interfaces.Settable;
import edu.cornell.gdiac.game.interfaces.Shooter;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.util.obstacles.*;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller.  Thus this is 
 * really a mini-GameEngine in its own right.  The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameMode extends Mode implements Settable {
	/** Retro font for displaying messages */
	private static String FONT_FILE = "fonts/RetroGame.ttf";

	/** The amount of time for a physics engine step.	 */
	public static final float WORLD_STEP = 1 / 60.0f;
	/** Number of velocity iterations for the constrain solvers	 */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers	 */
	public static final int WORLD_POSIT = 2;

	/** Width of the game world in Box2d units	 */
	private static final float DEFAULT_WIDTH = 32.0f;
	/** Height of the game world in Box2d units	 */
	private static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down)	 */
	private static final float DEFAULT_GRAVITY = -20.0f;

	/** All the objects in the world.	 */
	private PooledList<Obstacle> objects = new PooledList<Obstacle>();
	/** All the Entity Controllers in the world	 */
	private PooledList<EntityController> entityControllers = new PooledList<EntityController>();

	/** The Box2D world	 */
	private World world;
	/** The boundary of the world	 */
	private Rectangle bounds;
	/** The player	 */
	private PlayerModel player;
	/** The factory that creates projectiles	 */
	private PaintballFactory paintballFactory;
	/** The hud of this world	 */
	private HUDModel hud;

	/** The level loader	 */
	private LevelLoader levelLoader;
	/** The level this game mode loads in	 */
	private String levelFile;

	/** The world scale Vector	 */
	private Vector2 scaleVector;
	/** Whether we have completed this level	 */
	private boolean succeeded;
	/** Whether we have failed at this world (and need a reset)	 */
	private boolean failed;

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
	public GameMode(GameCanvas canvas, AssetManager manager) {
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
	public GameMode(GameCanvas canvas, AssetManager manager, float width, float height, float gravity) {
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
	public GameMode(GameCanvas canvas, AssetManager manager, Rectangle bounds, Vector2 gravity) {
		super(canvas, manager);
		onExit = ScreenListener.EXIT_MENU;
		scaleVector = new Vector2(canvas.getWidth() / bounds.getWidth(), canvas.getHeight() / bounds.getHeight());

		world = new World(gravity, false);
		world.setContactListener(new CollisionController());
		paintballFactory = new PaintballFactory(scaleVector);
		levelLoader = new LevelLoader(scaleVector);
		this.bounds = new Rectangle(bounds);
		hud = new HUDModel(canvas.getWidth(), canvas.getHeight());
		hud.setDrawScale(scaleVector);

		succeeded = false;
		failed = false;
	}

	// BEGIN: Setters and Getters

	/**
	 * TODO: write desc
	 */
	private boolean trySetPlayer() {
		for (Obstacle obj : levelLoader.getAddQueue()) {
			if (obj.getName().equals("player")) {
				player = (PlayerModel) obj;
				return true;
			}
		}

		return false;
	}
	// END: Setters and Getters

	@Override
	public void dispose() {
		for (Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		entityControllers.clear();
		world.dispose();
		levelLoader.dispose();
		hud = null;
		levelLoader = null;
		objects = null;
		bounds = null;
		scaleVector = null;
		world = null;
		canvas = null;
	}

	@Override
	public void reset() {
		super.reset();

		for (Obstacle obj : objects)
			obj.deactivatePhysics(world);
		objects.clear();
		entityControllers.clear();

		if (!levelFile.isEmpty())
			loadLevel(levelFile);

		hud.reset();
	}

	@Override
	public void update(float dt) {
		for (EntityController e : entityControllers)
			e.update(dt);

		applySettings();
		for(Obstacle obj: objects){
			if(obj instanceof Settable)
				((Settable) obj).applySettings();
			if(obj instanceof Shooter)
				updateShooter(obj);
			if(obj instanceof AmmoDepotModel && ((AmmoDepotModel) obj).isUsed()) {
				// TODO: find better solution for hud communication with other objs
				hud.addAmmo(((AmmoDepotModel) obj).getAmmoAmount());
			}
		}

		canvas.setCameraY(player.getY()*scaleVector.y);
		canvas.getCamera().update();

		postUpdate(dt);
	}

	@Override
	public void draw() {
		canvas.setCameraY(player.getY());

		for (Obstacle obj : objects) {
			obj.draw(canvas);
		}

		hud.draw(canvas);
	}

	@Override
	protected void drawDebug() {
		for (Obstacle obj : objects) {
			obj.drawDebug(canvas);
		}
	}

	@Override
	public void preLoadContent(AssetManager manager) {
		paintballFactory.preLoadContent(manager);
		levelLoader.preLoadContent(manager);
	}

	@Override
	public void loadContent(AssetManager manager) {
		paintballFactory.loadContent(manager);
		levelLoader.loadContent(manager);
		if (manager.isLoaded(FONT_FILE))
			hud.setFont(manager.get(FONT_FILE, BitmapFont.class));
	}

	@Override
	public void unloadContent(AssetManager manager) {
		paintballFactory.unloadContent(manager);
		levelLoader.unloadContent(manager);
	}

	@Override
	public void applySettings() {
		world.setGravity(new Vector2(0, Sidebar.getValue("Gravity")));
		PaintballFactory.applySettings();
	}

	/**
	 * TODO: write desc for level setting.. should populate the level
	 */
	public void loadLevel(String levelFile) {
		this.levelFile = levelFile;
		levelLoader.loadLevel(levelFile);
		bounds = levelLoader.getBounds();
		if (!trySetPlayer())
			System.out.println("Error: level file (" + levelFile + ") does not have a player");
	}

	/**
	 * TODO: write desc
	 */
	private void updateShooter(Obstacle obj) {
		if (((Shooter)obj).isShooting()) {
			if (obj.getName().equals("player") && hud.useAmmo())
				addObject(paintballFactory.createPaintball(obj.getX(), obj.getY(), ((Shooter) obj).isFacingRight()));
			else if (obj.getName().equals("enemy"))
				addObject(paintballFactory.createPaintball(obj.getX(), obj.getY(), ((Shooter) obj).isFacingRight()));
		}
	}

	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics.  The primary method is the step() method in world.  This implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	private void postUpdate(float dt) {
		// Add any objects created by actions
		while (!levelLoader.getAddQueue().isEmpty())
			addObject(levelLoader.getAddQueue().poll());

		// Turn the physics engine crank.
		world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);

		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<Obstacle>.Entry entry = iterator.next();
			Obstacle obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				obj.update(dt);
			}
		}
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

		addEntityController(obj);
	}

	/**
	 * TODO: write desc
	 * if its an enemy or player, add a new entity controller to it
	 */
	private void addEntityController(Obstacle obj) {
		if (obj.getName().equals("player"))
			entityControllers.add(new PlayerController(player));
		else if (obj.getName().equals("enemy"))
			entityControllers.add(new EnemyController(player, (EnemyModel) obj));
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