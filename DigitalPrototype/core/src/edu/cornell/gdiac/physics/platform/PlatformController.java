/*
 * PlatformController.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to 
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

import java.util.logging.Level;

/**
 * Gameplay specific controller for the platformer game.  
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class PlatformController extends WorldController implements ContactListener {
	/** The texture file for the character avatar (no animation) */
	private static final String DUDE_FILE  = "platform/dude.png";
	/** The texture file for the bullet */
	private static final String BULLET_FILE  = "platform/bullet.png";
	/** The texture file for the bridge plank */
	private static final String ROPE_FILE  = "platform/ropebridge.png";
	
	/** The sound file for a jump */
	private static final String JUMP_FILE = "platform/jump.mp3";
	/** The sound file for a bullet fire */
	private static final String PEW_FILE = "platform/pew.mp3";
	/** The sound file for a bullet collision */
	private static final String POP_FILE = "platform/plop.mp3";

	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture asset for the bullet */
	private TextureRegion bulletTexture;
	/** Texture asset for the bridge plank */
	private TextureRegion bridgeTexture;
	
	/** Track asset loading from all instances and subclasses */
	private AssetState platformAssetState = AssetState.EMPTY;
	
	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */	
	public void preLoadContent(AssetManager manager) {
		if (platformAssetState != AssetState.EMPTY) {
			return;
		}
		
		platformAssetState = AssetState.LOADING;
		manager.load(DUDE_FILE, Texture.class);
		assets.add(DUDE_FILE);
		manager.load(BULLET_FILE, Texture.class);
		assets.add(BULLET_FILE);
		manager.load(ROPE_FILE, Texture.class);
		assets.add(ROPE_FILE);
		
		manager.load(JUMP_FILE, Sound.class);
		assets.add(JUMP_FILE);
		manager.load(PEW_FILE, Sound.class);
		assets.add(PEW_FILE);
		manager.load(POP_FILE, Sound.class);
		assets.add(POP_FILE);
		
		super.preLoadContent(manager);
	}

	/**
	 * Load the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (platformAssetState != AssetState.LOADING) {
			return;
		}
		
		avatarTexture = createTexture(manager,DUDE_FILE,false);
		bulletTexture = createTexture(manager,BULLET_FILE,false);
		bridgeTexture = createTexture(manager,ROPE_FILE,false);

		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, JUMP_FILE);
		sounds.allocate(manager, PEW_FILE);
		sounds.allocate(manager, POP_FILE);
		super.loadContent(manager);
		platformAssetState = AssetState.COMPLETE;
	}
	
	// Physics constants for initialization
	/** The new heavier gravity for this world (so it is not so floaty) */
	private static final float  DEFAULT_GRAVITY = -14.7f;
	/** The density for most physics objects */
	private static final float  BASIC_DENSITY = 0.0f;
	/** Friction of most platforms */
	private static final float  BASIC_FRICTION = 0.4f;
	/** The restitution for all physics objects */
	private static final float  BASIC_RESTITUTION = 0.1f;
	/** The width of the rope bridge */
	private static final float  BRIDGE_WIDTH = 14.0f;
	/** The volume for sound effects */
	private static final float EFFECT_VOLUME = 0.8f;

	// Other game objects
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2(4.0f,14.0f);
	/** The position of the spinning barrier */
	private static Vector2 SPIN_POS = new Vector2(13.0f,12.5f);
	/** The initial position of the dude */
	private static Vector2 DUDE_POS = new Vector2(2.5f, 5.0f);
	/** The position of the rope bridge */
	private static Vector2 BRIDGE_POS  = new Vector2(9.0f, 3.8f);

	// Physics objects for the game
	/** Reference to the character avatar */
	private DudeModel avatar;
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;

	/** The factory that manages the bullets*/
	private BulletFactory bulletFactory;

	/**
	 * Creates and initialize a new instance of the platformer game
	 *
	 * The game has default gravity and other settings
	 */
	public PlatformController() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
		bulletFactory = new BulletFactory(this, scale);
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity() );
		
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		
		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;
		goalDoor = new BoxObstacle(GOAL_POS.x,GOAL_POS.y,dwidth,dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(0.0f);
		goalDoor.setFriction(0.0f);
		goalDoor.setRestitution(0.0f);
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		goalDoor.setName("goal");
		addObject(goalDoor);

	    String wname = "wall";
	    for (int ii = 0; ii < LevelParser.levelParserSingleton.getWalls().length; ii++) {
	        PolygonObstacle obj;
	    	obj = new PolygonObstacle(LevelParser.levelParserSingleton.getWalls()[ii], 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(wname+ii);
			addObject(obj);
	    }
	    
	    String pname = "platform";
	    for (int ii = 0; ii < LevelParser.levelParserSingleton.getPlatforms().length; ii++) {
	        PolygonObstacle obj;
	    	obj = new PolygonObstacle(LevelParser.levelParserSingleton.getPlatforms()[ii], 0, 0);
			obj.setBodyType(BodyDef.BodyType.StaticBody);
			obj.setDensity(BASIC_DENSITY);
			obj.setFriction(BASIC_FRICTION);
			obj.setRestitution(BASIC_RESTITUTION);
			obj.setDrawScale(scale);
			obj.setTexture(earthTile);
			obj.setName(pname+ii);
			addObject(obj);
	    }

		// Create dude
		dwidth  = avatarTexture.getRegionWidth()/scale.x;
		dheight = avatarTexture.getRegionHeight()/scale.y;
		avatar = new DudeModel(DUDE_POS.x, DUDE_POS.y, dwidth, dheight);
		avatar.setDrawScale(scale);
		avatar.setTexture(avatarTexture);
		addObject(avatar);

		// Create rope bridge
		dwidth  = bridgeTexture.getRegionWidth()/scale.x;
		dheight = bridgeTexture.getRegionHeight()/scale.y;
		RopeBridge bridge = new RopeBridge(BRIDGE_POS.x, BRIDGE_POS.y, BRIDGE_WIDTH, dwidth, dheight);
		bridge.setTexture(bridgeTexture);
		bridge.setDrawScale(scale);
		addObject(bridge);
	}
	
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		if (!super.preUpdate(dt)) {
			return false;
		}
		
		if (!isFailure() && avatar.getY() < -1) {
			setFailure(true);
			return false;
		}
		
		return true;
	}


	public float getAdjustment() {
		return (InputController.getInstance().didDecrease()?-.25f:0) +
				(InputController.getInstance().didIncrease()?.25f:0);

	}
	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {
		// Process actions in object model
		avatar.setMovement(InputController.getInstance().getHorizontal() *avatar.getForce());
		avatar.setJumping(InputController.getInstance().didPrimary());
		avatar.setShooting(InputController.getInstance().didSecondary());

		//Allow for adjustments
		float adjustment = getAdjustment();
		if(adjustment!=0f) {
			float newSpeed = bulletFactory.getBulletSpeed()+adjustment;
			//System.out.println(bulletFactory.getBulletSpeed() + " + " + adjustment + " = " + newSpeed);
			System.out.println("Speed: " + newSpeed);
			bulletFactory.setBulletSpeed(newSpeed);
		}

		// Add a bullet if we fire
		if (avatar.isShooting()) {
			bulletFactory.createBullet(avatar.isFacingRight(), avatar.getX(), avatar.getY(), earthTile);
			SoundController.getInstance().play(PEW_FILE, PEW_FILE, false, EFFECT_VOLUME);
		}
		
		avatar.applyForce();
	    if (avatar.isJumping()) {
	        SoundController.getInstance().play(JUMP_FILE,JUMP_FILE,false,EFFECT_VOLUME);
	    }
		
	    // If we use sound, we must remember this.
	    SoundController.getInstance().update();
	}
	
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use 
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		
		try {
			Obstacle bd1 = (Obstacle)body1.getUserData();
			Obstacle bd2 = (Obstacle)body2.getUserData();

			// Test bullet collision with world
			if (bd1.getName().equals("bullet") && bd2 != avatar) {
				BulletModel bullet = (BulletModel) bd2;
				bullet.setVX(0.0f);
				bullet.setTimeToDie(5);
				SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
			}

			if (bd2.getName().equals("bullet") && bd1 != avatar) {
				BulletModel bullet = (BulletModel) bd2;
			    bullet.setVX(0.0f);
			    bullet.setTimeToDie(5);
				SoundController.getInstance().play(POP_FILE,POP_FILE,false,EFFECT_VOLUME);
			}
			//Riding own projectile
			if (bd2.getName().equals("bullet") && bd1 == avatar) {
			    //avatar.setMovement(bd2.getVX());
			}
			if (bd1.getName().equals("bullet") && bd2 == avatar) {
				//avatar.setMovement(bd1.getVX());
			}
			// See if we have landed on the ground.
			if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
				(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
				avatar.setGrounded(true);
				sensorFixtures.add(avatar == bd1 ? fix2 : fix1); // Could have more than one ground
			}
			
			// Check for win condition
			if ((bd1 == avatar   && bd2 == goalDoor) ||
				(bd1 == goalDoor && bd2 == avatar)) {
				setComplete(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch.  The main use of this method
	 * is to determine when the characer is NOT on the ground.  This is how we prevent
	 * double jumping.
	 */ 
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		
		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((avatar.getSensorName().equals(fd2) && avatar != bd1) ||
			(avatar.getSensorName().equals(fd1) && avatar != bd2)) {
			sensorFixtures.remove(avatar == bd1 ? fix2 : fix1);
			if (sensorFixtures.size == 0) {
				avatar.setGrounded(false);
			}
		}
	}
	
	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {}
}