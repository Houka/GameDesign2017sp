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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.input.EditorInputController;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.game.levelLoading.LevelCreator;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.Obstacle;

import javax.swing.*;
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

	/** size of the grid */
    private static final int DEFAULT_GRID = 50;
    /** Width of the game world in Box2d units	 */
    private static final float DEFAULT_WIDTH = 32.0f;
    /** Height of the game world in Box2d units	 */
    private static final float DEFAULT_HEIGHT = 18.0f;

	/** Retro font for displaying messages */
	private static final String FONT_FILE = "fonts/RetroGame.ttf";

	/** Director of json files */
	private static final String JSON_DIRECTORY = "JSON";

	/** The font for giving messages to the player */
	private BitmapFont displayFont;
	/** Texture for side-bar*/
 	private Texture editor;

	/** Input controller */
	private EditorInputController mouseInput;
	private SelectionInputController keyInput;

    /** Texture under mouse when object clicked in right bar */
    private TextureRegion underMouse;
    /** If a texture on the right bar has been clicked */
    private boolean textureClicked;
    /** The position of the mouse translated to the grid */
    private Vector2 mousePos;

    /** array of textures */
    private TextureRegion[] regions;
    private int[] startHeights;

    /** grid size and shape */
    private int gridCell = DEFAULT_GRID;
    private PolygonShape grid;

	/** Level loader */
	private LevelLoader levelLoader;
	/** Level creator */
	private LevelCreator levelCreator;
	/** Scale for world */
	private Vector2 scaleVector;

	/** Dummy JFrame in order to have input message box show in front */
	private JFrame dummyFrame;

	/** All the objects in the world.	 */
	private PooledList<Obstacle> objects = new PooledList<Obstacle>();

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
	 */
	public LevelEditorMode(String name, GameCanvas canvas, AssetManager manager) {
		super(name ,canvas, manager);
        scaleVector = new Vector2(canvas.getWidth() / DEFAULT_WIDTH, canvas.getHeight() / DEFAULT_HEIGHT);
		debug = true;

		levelLoader = new LevelLoader(scaleVector);
		levelCreator = new LevelCreator();

		mouseInput = EditorInputController.getInstance();
		Gdx.input.setInputProcessor(mouseInput);
		keyInput = SelectionInputController.getInstance();
        textureClicked = false;
        regions = new TextureRegion[5];

        grid = new PolygonShape();
        grid.setAsBox(gridCell/2, gridCell/2);

        mousePos = new Vector2();
	}

	// BEGIN: Setters and Getters
    private void setCellDimension(int dim) {
	    gridCell = dim;
	    grid.setAsBox(gridCell,gridCell);
    }

    /**
	 * Gets a list of all files in the JSON directory in the assets folder.
	 * @return Pretty formated string of the list of all current files in the JSON directory
	 */
	private String getAllJsonFiles(){
		String result = "";
		for(Object s:getJsonFiles(new ArrayList<String>(), Gdx.files.local(JSON_DIRECTORY).file()).toArray()){
		    result+="        "+s+"\n";
        }
        return result+"\n";
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
        newPos.y = tileY * gridCell + (gridCell/2);
        newPos.y = canvas.getHeight()-newPos.y;

        return newPos;
    }

    private Vector2 getWorldCoordinates(Vector2 pos){
	    return new Vector2(pos.x/scaleVector.x,pos.y/scaleVector.y);
    }

    private String getLoadFileName(){
        if (dummyFrame == null) {
            dummyFrame = new JFrame();
        }

        dummyFrame.setVisible(true);
        dummyFrame.setLocationRelativeTo(null);
        dummyFrame.setAlwaysOnTop(true);
        String response = JOptionPane.showInputDialog(dummyFrame,
                "What's the relative file path of the file you want to load? \n\n List of all level files:\n"+getAllJsonFiles());
        dummyFrame.dispose();
        return response;
    }

    private String getSaveFileName(){
        if (dummyFrame == null) {
            dummyFrame = new JFrame();
        }

        dummyFrame.setVisible(true);
        dummyFrame.setLocationRelativeTo(null);
        dummyFrame.setAlwaysOnTop(true);
        String response = JOptionPane.showInputDialog(dummyFrame,"What do you want to name the save file? (ex: test.json)");
        dummyFrame.dispose();
        return response;
    }
	// END: Setters and Getters

	@Override
	public void dispose() {
		objects.clear();
		levelLoader.dispose();
        grid.dispose();
        grid = null;
        levelLoader = null;
        levelCreator = null;
        objects = null;
        scaleVector = null;
        mouseInput = null;
        mousePos = null;
	}

	@Override
	protected void update(float delta) {
		mouseInput.readInput();
		keyInput.readInput();

        updateKeyInput();
        updateMouseInput();
	}

	private void updateKeyInput(){
	    if(keyInput.didUp())
	        System.out.println("TEST: key up");
	    else if(keyInput.didDown())
            System.out.println("TEST: key down");
        else if(keyInput.didLeft())
            System.out.println("TEST: key left");
        else if(keyInput.didRight())
            System.out.println("TEST: key right");

        if(keyInput.didSelect())
            loadLevel();
    }

	private void updateMouseInput(){
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        mousePos = getCell(new Vector2(mouseX, mouseY));

        if(mouseInput.didTouch() && mouseX >= canvas.getWidth()-125) {
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
        if(!mouseInput.didTouch()) {
            textureClicked = false;
        }
        if(!mouseInput.didTouch() &&  mouseX <= canvas.getWidth()-200 && underMouse != null) {
            Vector2 newPos = getWorldCoordinates(getCell(mouseInput.getLastPos()));
            if(underMouse.equals(regions[0])) {
                PlayerModel newP = new PlayerModel(newPos.x,newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight());
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                objects.add(newP);
            }
            else if(underMouse.equals(regions[1])) {
                int interval = 3;
                EnemyModel newE = new EnemyModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight(), true, true, interval);
                newE.setDrawScale(scaleVector);
                newE.setTexture(underMouse);
                objects.add(newE);
            }
            else if(underMouse.equals(regions[3])) {
                int ammoAmount = 3;
                AmmoDepotModel newA = new AmmoDepotModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight(), ammoAmount);
                newA.setDrawScale(scaleVector);
                newA.setTexture(underMouse);
                objects.add(newA);
            }
            else if(underMouse.equals(regions[4])) {
                GoalModel newG = new GoalModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight());
                newG.setDrawScale(scaleVector);
                newG.setTexture(underMouse);
                objects.add(newG);
            }
            else if(underMouse.equals(regions[2])) {
                float[] arr = {newPos.x-1f, newPos.y+1f, newPos.x+1f, newPos.y+1f,
                        newPos.x+1f, newPos.y-1f, newPos.x-1f, newPos.y-1f};
                PlatformModel newP = new PlatformModel(arr);
                newP.setDrawScale(scaleVector);
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
		canvas.drawText("CLEAR", displayFont, 500,
				canvas.getHeight() - 20);
	}

    @Override
    protected void drawDebug() {
        drawGrid(gridCell);
        for (Obstacle obj : objects) {
            obj.drawDebug(canvas);
        }
    }

    /**
     * Draws the snapping grid
     * @param gridCell the length of a side of a grid
     */
    private void drawGrid(int gridCell) {
        for(int i=gridCell/2; i<canvas.getWidth()-200; i+=gridCell) {
            for (int j =0; j < canvas.getHeight(); j += gridCell) {
                canvas.drawPhysics(grid, Color.WHITE, i, j);
            }
        }
        canvas.drawPhysics(grid, Color.RED, mousePos.x, mousePos.y);
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
		if (manager.isLoaded(FONT_FILE))
			displayFont = manager.get(FONT_FILE, BitmapFont.class);
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
        String saveFileName = JSON_DIRECTORY+"/"+getSaveFileName();
        ArrayList<PlatformModel> platforms = new ArrayList<PlatformModel>();
        ArrayList<WallModel> walls = new ArrayList<WallModel>();
        PlayerModel player = null;
        ArrayList<EnemyModel> intervalEnemies = new ArrayList<EnemyModel>();
        ArrayList<EnemyModel> onSightEnemies = new ArrayList<EnemyModel>();
        ArrayList<AmmoDepotModel> ammoDepots = new ArrayList<AmmoDepotModel>();
        GoalModel target = null;
        int ammo = 4; // testing code TODO: replace with variable

        for (Obstacle obj: objects){
            if (obj instanceof PlatformModel)
                platforms.add((PlatformModel) obj);
            else if (obj instanceof WallModel)
                walls.add((WallModel) obj);
            else if (obj instanceof EnemyModel && ((EnemyModel) obj).isOnSight())
                onSightEnemies.add((EnemyModel) obj);
            else if (obj instanceof EnemyModel && !((EnemyModel) obj).isOnSight())
                intervalEnemies.add((EnemyModel) obj);
            else if (obj instanceof AmmoDepotModel)
                ammoDepots.add((AmmoDepotModel) obj);
            else if (obj instanceof PlayerModel)
                player = (PlayerModel) obj;
            else if (obj instanceof GoalModel)
                target = (GoalModel) obj;
        }

        if (player != null || target != null)
            levelCreator.writeLevel(saveFileName, platforms, walls, player, intervalEnemies, onSightEnemies, ammoDepots, target, ammo);
	    else{
	        System.out.println("ERROR: cannot create JSON without a player or goal in the map or file name is invalid");
        }
    }

	private void loadLevel() {
        String filename = getLoadFileName();
        if (!filename.isEmpty()) {
            levelLoader.loadLevel(filename);
            while (!levelLoader.getAddQueue().isEmpty()) {
                Obstacle obj = levelLoader.getAddQueue().poll();
                obj.setDrawScale(scaleVector);
                objects.add(obj);
            }
        }else{
            System.out.println("ERROR: invalid file path");
        }
	}

	private void clearLevel(){
        objects.clear();
    }
}