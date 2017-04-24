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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.Camera2;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.input.EditorInputController;
import edu.cornell.gdiac.game.levelLoading.LevelCreator;
import edu.cornell.gdiac.game.levelLoading.LevelLoader;
import edu.cornell.gdiac.util.Animation;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.FileReaderWriter;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.obstacles.Obstacle;

import javax.swing.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 * Class that provides a Level Editor screen for the state of the game.
 *
 * The level editor screen allows players to create/edit their own levels
 */
public class LevelEditorMode extends Mode {
    /** Textures necessary to support the screen */
    private static final String BACKGROUND_FILE = "ui/bg/level_editor.png";

    /** size of the grid */
    private static final int DEFAULT_GRID = 48;
    /** Width of the game world in Box2d units	 */
    private static final float DEFAULT_WIDTH = 32.0f;
    /** Height of the game world in Box2d units	 */
    private static final float DEFAULT_HEIGHT = 18.0f;

    /** Speed at which to move the camera */
    private static final int CAMERA_SPEED = DEFAULT_GRID;

    /** Texture for sidebar background*/
    private TextureRegion sidebarTexture;
    /** Texture for grid*/
    private TextureRegion whitePixelTexture;
    private Animation spikeAnimation;

    /** Input controller */
    private EditorInputController input;

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

    /** Camera's used in-game**/
    private Camera2 worldCamera;
    private Camera2 hudCamera;
    private Vector2 cameraPos;

    /** The y starting offset of the sidebar elements */
    private int startHeight= 5;
    /** Level variables */
    int ammo = 4;

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

        input = EditorInputController.getInstance();
        textureClicked = false;
        regions = new TextureRegion[12];
        startHeights = new int[12];

        worldCamera = new Camera2(canvas.getWidth(),canvas.getHeight());
        worldCamera.setAutosnap(true);
        hudCamera = new Camera2(canvas.getWidth(),canvas.getHeight());
        hudCamera.setAutosnap(true);
        cameraPos = new Vector2(canvas.getWidth()/2,canvas.getHeight()/2);

        mousePos = new Vector2();
    }

    // BEGIN: Setters and Getters
    private void setCellDimension(int dim) {
        gridCell = dim;
    }

    private float mod(float x,int n) {
        return x>0 ? x % n : x % n + n;
    }

    private Vector2 getCell(Vector2 pos) {
        int tileX = (int) (pos.x-mod(pos.x,gridCell))/gridCell;
        int tileY = (int) (pos.y-mod(pos.y,gridCell))/gridCell;
        Vector2 newPos = pos;

        newPos.x = tileX * gridCell;
        newPos.y = tileY * gridCell;
        newPos.y = canvas.getHeight()-newPos.y;

        return newPos;
    }

    private Vector2 getScaledCoordinates(Vector2 pos){
        return new Vector2(pos.x/scaleVector.x,pos.y/scaleVector.y);
    }

    private Vector2 getWorldCoordinates(Vector2 pos){
        return new Vector2(pos.x+worldCamera.position.x-canvas.getWidth()/2,
                            pos.y-worldCamera.position.y+canvas.getHeight()/2);
    }

    private String getLoadFileName(){
        setUpPopUpFrame();
        String response = JOptionPane.showInputDialog(dummyFrame,
                "What's the relative file path of the file you want to load? \n\n List of all level files:\n"+ FileReaderWriter.getJsonFiles());
        dummyFrame.dispose();
        return response;
    }

    private String getSaveFileName(){
        setUpPopUpFrame();
        String response = JOptionPane.showInputDialog(dummyFrame,"What do you want to name the save file? (ex: test.json)");
        dummyFrame.dispose();
        return response;
    }

    private String getNewAmmo(){
        setUpPopUpFrame();
        String response = JOptionPane.showInputDialog(dummyFrame,"What do you want to change the starting ammo to?");
        dummyFrame.dispose();
        return response;
    }

    private void setUpPopUpFrame(){
        if (dummyFrame == null) {
            dummyFrame = new JFrame();
        }

        dummyFrame.setVisible(true);
        dummyFrame.setLocationRelativeTo(null);
        dummyFrame.setAlwaysOnTop(true);
    }
    // END: Setters and Getters

    @Override
    public void dispose() {
        objects.clear();
        levelLoader.dispose();
        worldCamera.setAutosnap(true);
        cameraPos = null;
        levelLoader = null;
        levelCreator = null;
        objects = null;
        scaleVector = null;
        input = null;
        mousePos = null;
    }

    @Override
    protected void update(float delta) {
        input.readInput();
        updateMouseInput();
        updateKeyInput();
    }

    private String setInterval() {
        String result = JOptionPane.showInputDialog("Enter an interval time in seconds.");
        if(result != null) {
            return Integer.toString(Integer.parseInt(result)*60);
        }
        else {
            return null;
        }
    }

    private String setDir() {
        String[] values = {"left", "right"};
        String result = (String) JOptionPane.showInputDialog(null, "Choose a direction for your enemy to face", "Input",
                JOptionPane.INFORMATION_MESSAGE, null, values, values[0]);
        if(result != null) {
            return result;
        }
        else {
            return null;
        }
    }

    private String setEnemyType() {
        String[] values = {"normal", "trampoline"};
        String result = (String) JOptionPane.showInputDialog(null, "Choose a type of paintball you want your enemy to shoot", "Input",
                JOptionPane.INFORMATION_MESSAGE, null, values, values[0]);
        if(result != null) {
            return result;
        }
        else {
            return null;
        }
    }

    private void updateKeyInput(){
        // camera movement
        if(input.didUp())
            cameraPos.y+=CAMERA_SPEED;
        else if(input.didDown())
            cameraPos.y-=cameraPos.y<=canvas.getHeight()/2? 0 :CAMERA_SPEED;
        else if(input.didLeft())
            cameraPos.x-=CAMERA_SPEED;
        else if(input.didRight())
            cameraPos.x+=CAMERA_SPEED;

        // save, clear, or load the level
        if(input.didReset())
            clearLevel();
        else if (input.didLoad())
            loadLevel();
        else if (input.didSave())
            saveLevel();
        else if (input.didAmmoChange())
            changeStartingAmmo(getNewAmmo());
    }

    private void updateMouseInput(){
        int mouseX = Gdx.input.getX()+gridCell/2;
        int mouseY = Gdx.input.getY()+gridCell/2;
        mousePos = getCell(getWorldCoordinates(new Vector2(mouseX, mouseY)));

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
        if(input.didTouch() && mouseX <= canvas.getWidth()-170 && underMouse != null && textureClicked) {
            Vector2 newPos = getScaledCoordinates(mousePos);
            if(underMouse.equals(regions[0])) {
                PlayerModel newP = new PlayerModel(newPos.x,getScaledCoordinates(new Vector2(mousePos.x, mousePos.y+(underMouse.getRegionHeight()/4))).y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight());
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                objects.add(newP);
                underMouse = null;
                textureClicked = false;
            }
            else if(underMouse.equals(regions[5])) {
                try {
                    int interval = 3;
                    String dir = setDir();
                    String type = setEnemyType();
                    boolean right = false;
                    if(dir.equals("right")) { right = true; }
                    EnemyModel newE = new EnemyModel(newPos.x, newPos.y,
                            underMouse.getRegionWidth(), underMouse.getRegionHeight(), right, true, interval, type);
                    newE.setDrawScale(scaleVector);
                    newE.setTexture(underMouse);
                    objects.add(newE);
                }
                catch (NullPointerException e) {
                }
            }
            else if(underMouse.equals(regions[1])) {
                try {
                    int interval = Math.max(0, Integer.parseInt(setInterval()));
                    String dir = setDir();
                    String type = setEnemyType();
                    boolean right = false;
                    if(dir.equals("right")) { right = true; }
                    EnemyModel newE = new EnemyModel(newPos.x, newPos.y,
                            underMouse.getRegionWidth(), underMouse.getRegionHeight(), right, false, interval, type);
                    newE.setDrawScale(scaleVector);
                    newE.setTexture(underMouse);
                    objects.add(newE);
                }
                catch (NumberFormatException e) {
                }
                catch (NullPointerException e) {
                }
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
                underMouse = null;
                textureClicked = false;
            }
            else if(underMouse.equals(regions[2])) {
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                PlatformModel newP = new PlatformModel(arr, PlatformModel.NORMAL_PLATFORM);
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                objects.add(newP);
            }
            else if(underMouse.equals(regions[6])) {
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                WallModel newW = new WallModel(arr);
                newW.setDrawScale(scaleVector);
                newW.setTexture(underMouse);
                objects.add(newW);
            }
            else if(underMouse.equals(regions[7])) {
                SplattererModel newS = new SplattererModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight());
                newS.setDrawScale(scaleVector);
                newS.setTexture(underMouse);
                objects.add(newS);
                underMouse = null;
                textureClicked = false;
            }
            else if(underMouse.equals(regions[8])) {
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                PlatformModel newP = new PlatformModel(arr, PlatformModel.SPIKE_DOWN_PLATFORM);
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                newP.setAnimation(spikeAnimation);
                objects.add(newP);
            }
            else if(underMouse.equals(regions[9])) {
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                PlatformModel newP = new PlatformModel(arr, PlatformModel.SPIKE_UP_PLATFORM);
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                newP.setAnimation(spikeAnimation);
                objects.add(newP);
            }
            else if(underMouse.equals(regions[10])) {
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                PlatformModel newP = new PlatformModel(arr, PlatformModel.SPIKE_LEFT_PLATFORM);
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                newP.setAnimation(spikeAnimation);
                objects.add(newP);
            }
            else if(underMouse.equals(regions[11])) {
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                PlatformModel newP = new PlatformModel(arr, PlatformModel.SPIKE_RIGHT_PLATFORM);
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                newP.setAnimation(spikeAnimation);
                objects.add(newP);
            }
        }

        if(input.didRightClick()) {
            Rectangle bounds = new Rectangle();
            for(Obstacle o: objects) {
                Vector2 scaledMouse = getScaledCoordinates(getWorldCoordinates(new Vector2(mouseX,canvas.getHeight()-mouseY)));
                System.out.println(scaledMouse);
                if(o instanceof PlatformModel) {
                    float[] points = ((PlatformModel)o).getPoints();
                    float newW = points[2]-points[0];
                    float newH = points[3]-points[7];
                    bounds = new Rectangle(points[6]+(newW/2), points[5]-(newH/2), newW, newH);
                }
                else if(o instanceof WallModel) {
                    float[] points = ((WallModel)o).getPoints();
                    float newW = points[2]-points[0];
                    float newH = points[3]-points[7];
                    bounds = new Rectangle(points[6]+(newW/2), points[5]-(newH/2), newW, newH);
                }
                else if(o instanceof GoalModel) {
                    float newW = ((GoalModel) o).getWidth();
                    if(newW/scaleVector.x > .5) {
                        newW /= scaleVector.x;
                    }
                    float newH = ((GoalModel) o).getHeight();
                    if(newH/scaleVector.y > .5) {
                        newH /= scaleVector.y;
                    }
                    bounds = new Rectangle(o.getX(),o.getY()-newH, newW, newH);
                }
                else if(o instanceof AmmoDepotModel) {
                    float newW = ((AmmoDepotModel) o).getWidth();
                    if(newW/scaleVector.x > .5) {
                        newW /= scaleVector.x;
                    }
                    float newH = ((AmmoDepotModel) o).getHeight();
                    if(newH/scaleVector.y > .5) {
                        newH /= scaleVector.y;
                    }
                    bounds = new Rectangle(o.getX(),o.getY()-(newH), newW+(newW/2), newH);
                }
                else if(o instanceof EnemyModel) {
                    float newW = ((EnemyModel) o).getWidth();
                    if(newW/scaleVector.x > .5) {
                        newW /= scaleVector.x;
                    }
                    float newH = ((EnemyModel) o).getHeight();
                    if(newH/scaleVector.y > .5) {
                        newH /= scaleVector.y;
                    }
                    bounds = new Rectangle(o.getX()-(newW/2),o.getY()-(newH/2), newW+(newW/2), newH);
                }
                else if(o instanceof PlayerModel) {
                    float newW = ((PlayerModel) o).getWidth();
                    if(newW/scaleVector.x > .5) {
                        newW /= scaleVector.x;
                    }
                    float newH = ((PlayerModel) o).getHeight();
                    System.out.println("newH: "+newH);
                    if(newH/scaleVector.y > .5) {
                        newH /= scaleVector.y;
                    }
                    bounds = new Rectangle(o.getX(),o.getY()-(newH), newW, newH+(newH/2));
                }
                else if(o instanceof SplattererModel) {
                    float newW = ((SplattererModel) o).getWidth()/scaleVector.x;
                    float newH = ((SplattererModel) o).getHeight()/scaleVector.y;
                    bounds = new Rectangle(o.getX(),o.getY()-(newH), newW, newH);
                }
                System.out.println(bounds);
                if(bounds.contains(scaledMouse)) {
                    objects.remove(o);
                }
            }
        }

        // scrolling the sidebar
        if(mouseX >= canvas.getWidth() - 170){
            if (input.didScrolledUp())
                startHeight+=10;
            else if(input.didScrolledDown())
                startHeight-=10;
        }
    }

    @Override
    protected void draw() {
        canvas.end();
        canvas.begin(worldCamera);
        canvas.setCamera(cameraPos.x, cameraPos.y, canvas.getHeight()/2);
        super.draw();

        // Draw the objects from the loaded level
        for (Obstacle obj : objects) {
            obj.draw(canvas);
        }

        drawGrid(gridCell);

        canvas.end();
        canvas.begin(hudCamera);
        canvas.setDefaultCamera();
        // Draw the right sidebars for the editor
        canvas.draw(sidebarTexture, Color.WHITE, canvas.getWidth()-170, 0, 170, canvas.getHeight());

        // Draw the sidebar textures into the right sidebar
        int startHeight = this.startHeight;

        for (int i=0; i<regions.length; i++) {
            canvas.draw(regions[i], canvas.getWidth()-125, startHeight);
            startHeights[i] = startHeight;
            startHeight += regions[i].getRegionHeight() + 20;
        }

        if(textureClicked) {
            canvas.draw(underMouse, Gdx.input.getX()-(underMouse.getRegionWidth()/2),
                    canvas.getHeight()-Gdx.input.getY()-(underMouse.getRegionHeight()/2));
        }
    }

    /**
     * Draws the snapping grid
     * @param gridCell the length of a side of a grid
     */
    private void drawGrid(int gridCell) {
        for(int i=0; i<canvas.getWidth(); i+=gridCell)
            for (int j =0; j < canvas.getHeight(); j += gridCell)
                drawRectangle(i, j, gridCell, gridCell, Color.WHITE);

        // draw hover over cell
        drawRectangle(mousePos.x, mousePos.y, gridCell, gridCell, Color.RED);
    }

    private void drawRectangle(float x, float y, float width, float height, Color color){
        canvas.draw(whitePixelTexture, color, x-width/2, y-height/2, 1, height);
        canvas.draw(whitePixelTexture, color, x-width/2, y-height/2, width, 1);
        canvas.draw(whitePixelTexture, color, x+width/2, y-height/2, 1, height);
        canvas.draw(whitePixelTexture, color, x-width/2, y+height/2, width, 1);
    }

    @Override
    public void preLoadContent(AssetManager manager) {
        manager.load(BACKGROUND_FILE,Texture.class);
        manager.load(Constants.ENEMY_INTERVAL_FILE,Texture.class);
        manager.load(Constants.ENEMY_ONSIGHT_FILE,Texture.class);
        manager.load(Constants.PLAYER_FILE,Texture.class);
        manager.load(Constants.PLATFORM_FILE,Texture.class);
        manager.load(Constants.AMMO_DEPOT_FILE,Texture.class);
        manager.load(Constants.CAMERA_FILE,Texture.class);
        manager.load(Constants.WHITE_PIXEL_FILE,Texture.class);
        manager.load(Constants.WALL_FILE,Texture.class);
        manager.load(Constants.SPLATTERER_FILE,Texture.class);
        manager.load(Constants.SPIKES_DOWN_STILL_FILE,Texture.class);
        manager.load(Constants.SPIKES_UP_STILL_FILE,Texture.class);
        manager.load(Constants.SPIKES_LEFT_STILL_FILE,Texture.class);
        manager.load(Constants.SPIKES_RIGHT_STILL_FILE,Texture.class);
        manager.load(Constants.SPIKES_UP_SPIN_FILE,Texture.class);
        levelLoader.preLoadContent(manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        levelLoader.loadContent(manager);
        sidebarTexture = AssetRetriever.createTextureRegion(manager, BACKGROUND_FILE, true);
        whitePixelTexture = AssetRetriever.createTextureRegion(manager, Constants.WHITE_PIXEL_FILE, true);

        spikeAnimation = new Animation();
        spikeAnimation.addTexture("spin", AssetRetriever.createTexture(manager, Constants.SPIKES_UP_SPIN_FILE, false), 1 , 8);
        spikeAnimation.setPlaying(false);
        spikeAnimation.setPlayingAnimation("spin");

        regions[0] = AssetRetriever.createTextureRegion(manager, Constants.PLAYER_FILE, false);
        regions[1] = AssetRetriever.createTextureRegion(manager, Constants.ENEMY_INTERVAL_FILE, false);
        regions[2] = AssetRetriever.createTextureRegion(manager, Constants.PLATFORM_FILE, false);
        regions[3] = AssetRetriever.createTextureRegion(manager, Constants.AMMO_DEPOT_FILE, false);
        regions[4] = AssetRetriever.createTextureRegion(manager, Constants.CAMERA_FILE, false);
        regions[5] = AssetRetriever.createTextureRegion(manager, Constants.ENEMY_ONSIGHT_FILE, false);
        regions[6] = AssetRetriever.createTextureRegion(manager, Constants.WALL_FILE, false);
        regions[7] = AssetRetriever.createTextureRegion(manager, Constants.SPLATTERER_FILE, false);
        regions[8] = AssetRetriever.createTextureRegion(manager, Constants.SPIKES_DOWN_STILL_FILE, false);
        regions[9] = AssetRetriever.createTextureRegion(manager, Constants.SPIKES_UP_STILL_FILE, false);
        regions[10] = AssetRetriever.createTextureRegion(manager, Constants.SPIKES_LEFT_STILL_FILE, false);
        regions[11] = AssetRetriever.createTextureRegion(manager, Constants.SPIKES_RIGHT_STILL_FILE, false);
    }

    @Override
    public void unloadContent(AssetManager manager) {
        if (manager.isLoaded(BACKGROUND_FILE)) {
            manager.unload(BACKGROUND_FILE);
        }
        if (manager.isLoaded(Constants.ENEMY_INTERVAL_FILE)) {
            manager.unload(Constants.ENEMY_INTERVAL_FILE);
        }
        if (manager.isLoaded(Constants.ENEMY_ONSIGHT_FILE)) {
            manager.unload(Constants.ENEMY_ONSIGHT_FILE);
        }
        if (manager.isLoaded(Constants.PLAYER_FILE)) {
            manager.unload(Constants.PLAYER_FILE);
        }
        if (manager.isLoaded(Constants.AMMO_DEPOT_FILE)) {
            manager.unload(Constants.AMMO_DEPOT_FILE);
        }
        if (manager.isLoaded(Constants.PLATFORM_FILE)) {
            manager.unload(Constants.PLATFORM_FILE);
        }
        if (manager.isLoaded(Constants.WALL_FILE)) {
            manager.unload(Constants.WALL_FILE);
        }
        if (manager.isLoaded(Constants.CAMERA_FILE)) {
            manager.unload(Constants.CAMERA_FILE);
        }
        if (manager.isLoaded(Constants.WHITE_PIXEL_FILE)) {
            manager.unload(Constants.WHITE_PIXEL_FILE);
        }
        if(manager.isLoaded(Constants.SPLATTERER_FILE)) {
            manager.unload(Constants.SPLATTERER_FILE);
        }
        if(manager.isLoaded(Constants.SPIKES_DOWN_STILL_FILE)) {
            manager.unload(Constants.SPIKES_DOWN_STILL_FILE);
        }
        if(manager.isLoaded(Constants.SPIKES_UP_STILL_FILE)) {
            manager.unload(Constants.SPIKES_UP_STILL_FILE);
        }
        if(manager.isLoaded(Constants.SPIKES_LEFT_STILL_FILE)) {
            manager.unload(Constants.SPIKES_LEFT_STILL_FILE);
        }
        if(manager.isLoaded(Constants.SPIKES_RIGHT_STILL_FILE)) {
            manager.unload(Constants.SPIKES_RIGHT_STILL_FILE);
        }
    }

    private void saveLevel() {
        String saveFileName = FileReaderWriter.JSON_DIRECTORY+"/"+getSaveFileName();
        ArrayList<PlatformModel> platforms = new ArrayList<PlatformModel>();
        ArrayList<WallModel> walls = new ArrayList<WallModel>();
        PlayerModel player = null;
        ArrayList<EnemyModel> intervalEnemies = new ArrayList<EnemyModel>();
        ArrayList<EnemyModel> onSightEnemies = new ArrayList<EnemyModel>();
        ArrayList<AmmoDepotModel> ammoDepots = new ArrayList<AmmoDepotModel>();
        GoalModel target = null;
        ArrayList<SplattererModel> splatterers = new ArrayList<SplattererModel>();

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
            else if (obj instanceof SplattererModel)
                splatterers.add((SplattererModel) obj);
        }
        if (player != null && target != null) {
            levelCreator.writeLevel(saveFileName, platforms, walls, player, intervalEnemies, onSightEnemies, ammoDepots, splatterers, target, ammo);
            FileReaderWriter.addJsonFile(saveFileName);
        }
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

    private void changeGridSize(){
        // TODO: unimplemented
    }

    private void changeStartingAmmo(String ammo){
        try{
            this.ammo = Integer.valueOf(ammo);
        }catch (Exception e){
            System.out.println("ERROR: invalid ammo amount: "+ammo);
        }
    }
}