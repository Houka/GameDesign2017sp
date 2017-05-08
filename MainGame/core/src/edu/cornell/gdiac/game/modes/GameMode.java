/*
 * GameMode.java
 *
 *  This class deals with the main gameplay logic and encompasses
 *  the main game engine. 
 *
 * Author: Changxu Lu
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.game.modes;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.game.Camera2;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.GameModeManager;
import edu.cornell.gdiac.game.entity.controllers.CollisionController;
import edu.cornell.gdiac.game.entity.controllers.EnemyController;
import edu.cornell.gdiac.game.entity.controllers.EntityController;
import edu.cornell.gdiac.game.entity.controllers.PlayerController;
import edu.cornell.gdiac.game.entity.factories.PaintballFactory;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.input.MainInputController;
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
	/** The amount of time for a physics engine step.	 */
	public static final float WORLD_STEP = 1 / 60.0f;
	/** Number of velocity iterations for the constrain solvers	 */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers	 */
	public static final int WORLD_POSIT = 2;
	/** World default sizes */
	public static final int WORLD_WIDTH = 1024;
	public static final int WORLD_HEIGHT = 576;


	/** Width of the game world in Box2d units	 */
	private static final float DEFAULT_WIDTH = 32.0f;
	/** Height of the game world in Box2d units	 */
	private static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down)	 */
	private static final float DEFAULT_GRAVITY = -20.0f;

	/** offset for enemies to shoot without hitting themselves in the arm*/
	private static final float SHOOT_OFFSET = 0.4f;
	/** The time until reset after loss*/
	private final float TIME_TO_RESET = 2f;
	private final float START_TIME = 2f;

	/** Timer for a race the clock situation (in seconds) **/
	private float time = 0;

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
	private GoalModel goal;
	/** The factory that creates projectiles	 */
	private PaintballFactory paintballFactory;
	/** The hud of this world	 */
	private HUDModel hud;

	/** The level loader	 */
	private LevelLoader levelLoader;
	/** The level this game mode loads in	 */
	private String levelFile;
	/** The level number this game mode loads in	 */
	private int levelNumber;

	/** Camera's used in-game**/
	private Camera2 gameCamera;
	private Camera2 hudCamera;

	/** The world scale Vector	 */
	private Vector2 scaleVector;
	/** Whether we have completed this level	 */
	private boolean succeeded;
	/** Whether we have failed at this world (and need a reset)	 */
	private boolean failed;

	/** Sound controller */
	private SoundController soundController;

	/** An array to store the levels **/
	private static final String[] NUM_LEVELS = FileReaderWriter.getJsonFiles();

	private float accumulator;
	private static final float FRAME_CAP = .25f;

	private CollisionController collisionController;

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
	public GameMode(String name, GameCanvas canvas, AssetManager manager) {
		this(name, canvas, manager, new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT),
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
	public GameMode(String name, GameCanvas canvas, AssetManager manager, float width, float height, float gravity) {
		this(name, canvas, manager, new Rectangle(0, 0, width, height), new Vector2(0, gravity));
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
	public GameMode(String name, GameCanvas canvas, AssetManager manager, Rectangle bounds, Vector2 gravity) {
		super(name, canvas, manager);
		scaleVector = new Vector2(WORLD_WIDTH / bounds.getWidth(), WORLD_HEIGHT / bounds.getHeight());

		world = new World(gravity, false);
		hud = new HUDModel(canvas.getWidth(), canvas.getHeight());
		hud.setY(hud.getHeight());
		paintballFactory = new PaintballFactory(scaleVector);
		collisionController = new CollisionController(hud,paintballFactory);
		world.setContactListener(collisionController);
		levelLoader = new LevelLoader(scaleVector);
		this.bounds = new Rectangle(bounds);
		hud.setDrawScale(scaleVector);
		gameCamera = new Camera2(WORLD_WIDTH,(int)((float)WORLD_WIDTH/canvas.getWidth()*canvas.getHeight()));
		gameCamera.setAutosnap(false);
		hudCamera = new Camera2(canvas.getWidth(),canvas.getHeight());
		hudCamera.setAutosnap(true);

		soundController = SoundController.getInstance();
		soundController.setTimeLimit(20000);

		succeeded = false;
		failed = false;
	}

	// BEGIN: Setters and Getters
	/**
	 * Sets the level of this game mode
	 */
	public void setLevel(String levelFile,int levelNumber) {
		this.levelFile = levelFile;
		this.levelNumber = levelNumber;
	}

	/**
	 * Returns the level number
	 * @return level file number
	 */
	public int getLevelNum() {
		return levelNumber;
	}

	/**
	 * Trys to set the player in the world if it exists
	 *
	 * @return true if the world has a player, false otherwise
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

	/**
	 * trys to set the goal of the world
	 */
	private boolean trySetGoal(){
		for (Obstacle obj : levelLoader.getAddQueue()) {
			if (obj.getName().equals("goal")) {
				goal = (GoalModel) obj;
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
		gameCamera.setAutosnap(true);
		hud = null;
		levelLoader = null;
		objects = null;
		bounds = null;
		scaleVector = null;
		world = null;
		canvas = null;
	}

	@Override
	public void show() {
		active = true;
		resume();
	}

	@Override
	public void reset() {
		super.reset();

		for (Obstacle obj : objects)
			obj.deactivatePhysics(world);
		objects.clear();
		entityControllers.clear();

		if (!levelFile.isEmpty())
			loadLevel();

		canvas.getCamera().setRumble(50,10,2);
		canvas.begin(gameCamera);
		canvas.setCamera(player.getX()*scaleVector.x,player.getY() * scaleVector.y, gameCamera.viewportHeight/2);
		gameCamera.snap();
		canvas.end();
		hud.reset();
		time = 0;
	}

	@Override
	public void update(float dt) {
		time+=dt;
		soundController.update();

		if (!hud.isLose() && !hud.isWin() && time > START_TIME)
			for (EntityController e : entityControllers)
				e.update(dt);

		applySettings();
		paintballFactory.applySettings();
		for(Obstacle obj: objects){
			if(obj instanceof Settable)
				((Settable) obj).applySettings();
			if(obj instanceof Shooter)
				updateShooter(obj);
			if(obj instanceof SplattererModel)  {
				if(((SplattererModel)obj).isShot()) {
					((SplattererModel)obj).setShot(false);
					PaintballModel pb;
					if(((SplattererModel) obj).getDir())
						pb = paintballFactory.createPaintball(obj.getX()+(((SplattererModel) obj).getWidth()*2),
								((SplattererModel) obj).getYCoord(),!((SplattererModel)obj).getDir(), "player");
					else
						pb = paintballFactory.createPaintball(obj.getX()-(((SplattererModel) obj).getWidth()*2),
								((SplattererModel) obj).getYCoord(),!((SplattererModel)obj).getDir(), "player");
					pb.newSize(pb.getX(),pb.getY(),3);
					pb.fixX(0f);
					pb.setTimeToDie(pb.getPaintballToPaintballDuration());
					pb.platformPop();
					addObject(pb);
				}
			}
		}
		hud.update(dt);


		//if(MainInputController.getInstance().didDebug())
		if (player.getY() < -player.getHeight())
			hud.setLose(true);

		if(hud.isLose()) {
			hud.reset();
			listener.switchToScreen(this, GameModeManager.LOSS);
		}

		if(hud.getLastStateChange()>TIME_TO_RESET && hud.isWin()) {
			hud.reset();
			listener.switchToScreen(this, GameModeManager.WIN);
		}

		postUpdate(dt);
	}


	@Override
	public void draw() {
		canvas.end();
		canvas.begin(gameCamera);
		float cameraBufferWidth = gameCamera.viewportWidth/scaleVector.x/30f;

		if (hud.isWin() || time <= START_TIME)
			canvas.setCamera(Math.max(Math.min(goal.getX()+cameraBufferWidth,gameCamera.position.x/scaleVector.x),goal.getX()-cameraBufferWidth)*scaleVector.x,
					goal.getY() * scaleVector.y, gameCamera.viewportHeight/2);
		else
			canvas.setCamera(Math.max(Math.min(player.getX()+cameraBufferWidth,gameCamera.position.x/scaleVector.x),player.getX()-cameraBufferWidth)*scaleVector.x,
					player.getY() * scaleVector.y, gameCamera.viewportHeight/2);
		for (Obstacle obj : objects) {
			obj.draw(canvas);
		}

		canvas.end();
		canvas.begin(hudCamera);
		canvas.setDefaultCamera();
		hud.draw(canvas);
	}

	@Override
	protected void drawDebug() {
		canvas.endDebug();
		canvas.beginDebug(gameCamera);
		for (Obstacle obj : objects) {
			obj.drawDebug(canvas);
		}
	}

	@Override
	public void preLoadContent(AssetManager manager) {
		paintballFactory.preLoadContent(manager);
		levelLoader.preLoadContent(manager);
		manager.load(Constants.GAME_MUSIC_FILE, Sound.class);
	}

	@Override
	public void loadContent(AssetManager manager) {
		soundController.allocate(manager, Constants.GAME_MUSIC_FILE);
		paintballFactory.loadContent(manager);
		levelLoader.loadContent(manager);
		if (manager.isLoaded(Constants.FONT_FILE))
			hud.setFont(manager.get(Constants.FONT_FILE, BitmapFont.class));
		if (!soundController.isActive("game mode")){
			soundController.stopAll();
			soundController.play("gameMode", Constants.GAME_MUSIC_FILE, true);
		}
	}

	@Override
	public void unloadContent(AssetManager manager) {
		paintballFactory.unloadContent(manager);
		levelLoader.unloadContent(manager);
	}

	@Override
	public void applySettings() {
		world.setGravity(new Vector2(0, Sidebar.getValue("Gravity")));
		gameCamera.setSpeed(Sidebar.getValue("Camera Speed"));
		gameCamera.setRumble((int)Sidebar.getValue("Rumble Intensity"),(int)Sidebar.getValue("Rumble Intensity"),(int)Sidebar.getValue("Rumble Frequency"));

		if(Sidebar.getValue("Rumble Interval")==0)
			gameCamera.disableRumble();
		else
			gameCamera.enableRumble();

	}


	public void nextLevel() {
		int nextLevel = (levelNumber+1)%NUM_LEVELS.length;
		setLevel(NUM_LEVELS[nextLevel],nextLevel);
		reset();
	}

	/**
	 * Loads the level based on a json file. Will queue up a list of objects to
	 * add to the game world and sets all starting attributes to their initial starting
	 * number.
	 *
	 */
	private void loadLevel() {
		levelLoader.loadLevel(levelFile);
		bounds = levelLoader.getBounds();
		hud.setStartingAmmo(levelLoader.getStartingAmmo());
		gameCamera.snap();
		if (!trySetPlayer() || !trySetGoal())
			System.out.println("Error: level file (" + levelFile + ") does not have a player");
	}

	/**
	 * Updates any objects that are shooters so that this class can create/add bullets
	 * to the shooting entity. Special case applies for the player involving the HUD.
	 * The player will always shoot normal paintballs.
	 *
	 * @param obj the obstacle that we are checking is a shooter
	 */
	private void updateShooter(Obstacle obj) {
		if (((Shooter)obj).isShooting()) {
			if (obj.getName().equals("player") && hud.useAmmo()) {
				if (!((PlayerModel)obj).isCrouching())
					addObject(paintballFactory.createPaintball(obj.getX(), obj.getY()+player.getHeight()/8, ((Shooter) obj).isFacingRight(), "player"));
				else
					addObject(paintballFactory.createPaintball(obj.getX(), obj.getY()-player.getHeight()/4, ((Shooter) obj).isFacingRight(), "player"));
			}
			else if (obj.getName().equals("enemy")) {
				int direction = ((Shooter) obj).isFacingRight() ? 1 : 0;
				EnemyModel enemy = (EnemyModel) obj;
				addObject(paintballFactory.createPaintball(enemy.getX()+ direction * SHOOT_OFFSET, enemy.getY()-enemy.getHeight()/16,
						enemy.isFacingRight(),enemy.getEnemyType()));
			}
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

		accumulator += (float) Math.min(dt,FRAME_CAP);
		// Turn the physics engine crank.
		if (!hud.isWin() && accumulator >=WORLD_STEP) {
			world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);
			accumulator-=WORLD_STEP;
		}

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

				// make infinite background
				if (obj instanceof BackgroundModel){
					if(goal.getX()*scaleVector.x >= ((BackgroundModel) obj).getMaxWidth())
						((BackgroundModel) obj).incBgWidth(1);
					if(goal.getY()*scaleVector.y >= ((BackgroundModel) obj).getMaxHeight())
						((BackgroundModel) obj).incBgHeight(1);
					if (player.getX()*scaleVector.x <= -((BackgroundModel) obj).getMaxWidth()||
							player.getX()*scaleVector.x >= ((BackgroundModel) obj).getMaxWidth())
						((BackgroundModel) obj).incBgWidth(1);
					if (player.getY()*scaleVector.y >= ((BackgroundModel) obj).getMaxHeight())
						((BackgroundModel) obj).incBgHeight(1);
				}
			}
		}
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * @param obj The object to add
	 */
	private void addObject(Obstacle obj) {
		//assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);

		addEntityController(obj);
	}

	/**
	 * Helper function that adds its corresponding controller class to
	 * every entity obstacle.
	 * If its an enemy or player, add a new entity controller to it.
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

	@Override
	public void pauseGame() {super.pauseGame();listener.switchToScreen(this, GameModeManager.PAUSE);}
}