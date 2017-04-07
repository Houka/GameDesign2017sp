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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.input.EditorInputController;
import edu.cornell.gdiac.game.levelLoading.LevelCreator;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.Obstacle;
import edu.cornell.gdiac.util.sidebar.Sidebar;

import java.io.File;
import java.util.ArrayList;

/**
 * Class that provides a Level Editor screen for the state of the game.
 *
 * The level editor screen allows players to create/edit their own levels
 */
public class LevelEditorMode extends Mode {
	/** Textures necessary to support the loading screen */
	private static final String BACKGROUND_FILE = "ui/bg/level_editor.png";
	private static final String PLAYER_FILE = "sprites/char/char_still.png";
	private static final String ENEMY_FILE = "sprites/enemy/enemy_still.png";
	private static final String AMMO_DEPOT_FILE = "sprites/paint_repo.png";
	private static final String PLATFORM_FILE = "sprites/fixtures/solid.png";
	private static final String CAMERA_FILE = "sprites/security_camera.png";
    private static final int DEFAULT_GRID = 50;

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
	private EditorInputController input;

    /** Texture under mouse when object clicked in right bar */
    private TextureRegion underMouse;
    /** If a texture on the right bar has been clicked */
    private boolean textureClicked;

    /** array of textures */
    private TextureRegion[] regions;
    private int[] startHeights;
    private int gridCell = DEFAULT_GRID;

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

		input = EditorInputController.getInstance();
        Gdx.input.setInputProcessor(input);
        textureClicked = false;
        regions = new TextureRegion[5];

		// get initial json files
		jsonFiles = new PooledList<String>();
	}

	// BEGIN: Setters and Getters
    private int getCellDim() {
        return gridCell;
    }

    private void setCellDim(int dim) {
        gridCell = dim;
    }

    /**
	 * Gets a list of all files in the JSON directory in the assets folder.
	 * @return list of all current files in the JSON directory
	 */
	private PooledList<String> getAllJsonFiles(){
		jsonFiles.clear();
		jsonFiles.addAll(getJsonFiles(new ArrayList<String>(), Gdx.files.local(JSON_DIRECTORY).file()));
		return jsonFiles;
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

    private Vector2 getCell(Vector2 pos) {
        int tileX = (int) pos.x/gridCell;
        int tileY = (int) pos.y/gridCell;
        Vector2 newPos = pos;

        newPos.x = tileX * gridCell + (gridCell/2);
        newPos.y = tileY * gridCell;

        return newPos;
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

		updateMouseInput();

		while (!levelLoader.getAddQueue().isEmpty())
			objects.add(levelLoader.getAddQueue().poll());
	}

	private void updateMouseInput(){
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        if(input.didTouch() && mouseX >= canvas.getWidth()-125) {
            for(int i=0; i<regions.length; i++) {
                Rectangle textureBounds=new Rectangle
                        (canvas.getWidth()-125, canvas.getHeight() - startHeights[i] - (regions[i].getRegionHeight()),
                                regions[i].getRegionWidth(),regions[i].getRegionHeight());

                if(textureBounds.contains(mouseX, mouseY)) {
                    underMouse = regions[i];
                    textureClicked = true;

                }
            }
        }
        if(!input.didTouch()) {
            textureClicked = false;
        }
        if(!input.didTouch() &&  mouseX <= canvas.getWidth()-200 && underMouse != null) {
            Vector2 newPos = getCell(input.getLastPos());
            newPos.y = canvas.getHeight()-newPos.y;
            if(underMouse.getTexture().equals(player)) {
                PlayerModel newP = new PlayerModel(newPos.x,newPos.y-(gridCell/2),
                        underMouse.getRegionWidth(), underMouse.getRegionHeight());
                newP.setDrawScale(1,1);
                newP.setTexture(underMouse);
                objects.add(newP);
            }
            else if(underMouse.getTexture().equals(enemy)) {
                int interval = 3;
                EnemyModel newE = new EnemyModel(newPos.x+(gridCell/2), newPos.y-(gridCell/2),
                        underMouse.getRegionWidth(), underMouse.getRegionHeight(), true, true, interval);
                newE.setDrawScale(1,1);
                newE.setTexture(underMouse);
                objects.add(newE);
            }
            else if(underMouse.getTexture().equals(ammoDepot)) {
                int ammoAmount = 3;
                AmmoDepotModel newA = new AmmoDepotModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight(), ammoAmount);
                newA.setDrawScale(1,1);
                newA.setTexture(underMouse);
                objects.add(newA);
            }
            else if(underMouse.getTexture().equals(camera)) {
                GoalModel newG = new GoalModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight());
                newG.setDrawScale(1,1);
                newG.setTexture(underMouse);
                objects.add(newG);
            }
            else if(underMouse.getTexture().equals(platform)) {
                float[] arr = {newPos.x-(gridCell/2), newPos.y+(gridCell/2), newPos.x+(gridCell/2), newPos.y+(gridCell/2),
                        newPos.x+(gridCell/2), newPos.y, newPos.x-(gridCell/2), newPos.y};
                PlatformModel newP = new PlatformModel(arr);
                newP.setDrawScale(1,1);
                newP.setTexture(underMouse);
                objects.add(newP);
            }
            underMouse = null;
        }
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
        startHeights = new int[5];
        for (int i=0; i<regions.length; i++) {
            canvas.draw(regions[i], canvas.getWidth()-125, startHeight);
            startHeights[i] = startHeight;
            startHeight += regions[i].getRegionHeight() + 20;
        }
        if(textureClicked) {
            canvas.draw(underMouse, Gdx.input.getX()-(underMouse.getRegionWidth()/2),
                    canvas.getHeight()-Gdx.input.getY()-(underMouse.getRegionHeight()/2));
        }

        drawGrid(gridCell);
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

    /**
     * Draws the snapping grid
     * @param gridCell the length of a side of a grid
     */
    private void drawGrid(int gridCell) {
        for(int i=0; i<canvas.getWidth()-200; i+=gridCell) {
            canvas.draw(platform,Color.WHITE,i,0,1, canvas.getHeight());
        }
        for(int i=0; i<canvas.getHeight(); i+=gridCell) {
            canvas.draw(platform, Color.WHITE, 0, i, canvas.getWidth() - 200, 1);
        }
    }

    @Override
    protected void drawDebug() {
        for (Obstacle obj : objects) {
            obj.drawDebug(canvas);
        }
    }

	@Override
	public void preLoadContent(AssetManager manager) {
		manager.load(BACKGROUND_FILE,Texture.class);
		manager.load(ENEMY_FILE,Texture.class);
		manager.load(PLAYER_FILE,Texture.class);
		manager.load(PLATFORM_FILE,Texture.class);
		manager.load(AMMO_DEPOT_FILE,Texture.class);
		manager.load(CAMERA_FILE,Texture.class);
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

        regions[0] = AssetRetriever.createTextureRegion(manager, PLAYER_FILE, false);
        regions[1] = AssetRetriever.createTextureRegion(manager, ENEMY_FILE, false);
        regions[2] = AssetRetriever.createTextureRegion(manager, PLATFORM_FILE, false);
        regions[3] = AssetRetriever.createTextureRegion(manager, AMMO_DEPOT_FILE, false);
        regions[4] = AssetRetriever.createTextureRegion(manager, CAMERA_FILE, false);
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

    private void applySettings() {
        setCellDim((int) Sidebar.getValue("Grid Size"));
    }
}