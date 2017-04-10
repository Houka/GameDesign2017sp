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
import edu.cornell.gdiac.game.Camera2;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.game.input.EditorInputController;
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
    private static final String WHITE_PIXEL_FILE = "ui/white_pixel.png";

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

    /** Speed at which to move the camera */
    private static final int CAMERA_SPEED = DEFAULT_GRID;

    /** The font for giving messages to the player */
    private BitmapFont displayFont;

    /** Texture for sidebar background*/
    private TextureRegion sidebarTexture;
    /** Texture for grid*/
    private TextureRegion whitePixelTexture;

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
        regions = new TextureRegion[6];
        startHeights = new int[6];

        worldCamera = new Camera2(canvas.getWidth(),canvas.getHeight());
        worldCamera.setAutosnap(false);
        hudCamera = new Camera2(canvas.getWidth(),canvas.getHeight());
        hudCamera.setAutosnap(true);
        cameraPos = new Vector2(canvas.getWidth()/2,canvas.getHeight()/2);

        mousePos = new Vector2();
    }

    // BEGIN: Setters and Getters
    private void setCellDimension(int dim) {
        gridCell = dim;
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

        newPos.x = tileX * gridCell;
        newPos.y = tileY * gridCell + gridCell/2;
        newPos.y = canvas.getHeight()-newPos.y;

        return newPos;
    }

    private Vector2 getScaledCoordinates(Vector2 pos){
        return new Vector2(pos.x/scaleVector.x,pos.y/scaleVector.y);
    }

    private Vector2 getWorldCoordinates(Vector2 pos){
        return new Vector2(pos.x+worldCamera.getTargetLocation().x-canvas.getWidth()/2,
                            pos.y-worldCamera.getTargetLocation().y+canvas.getHeight()/2);
    }

    private String getLoadFileName(){
        setUpPopUpFrame();
        String response = JOptionPane.showInputDialog(dummyFrame,
                "What's the relative file path of the file you want to load? \n\n List of all level files:\n"+getAllJsonFiles());
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
        int mouseY = Gdx.input.getY();
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
        if(!input.didTouch()) {
            textureClicked = false;
        }
        if(!input.didTouch() &&  mouseX <= canvas.getWidth()-170 && underMouse != null) {
            Vector2 newPos = getScaledCoordinates(mousePos);
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
            else if(underMouse.equals(regions[5])) {
                int interval = 200;
                EnemyModel newE = new EnemyModel(newPos.x, newPos.y,
                        underMouse.getRegionWidth(), underMouse.getRegionHeight(), true, false, interval);
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
                float offset = .75f;
                float[] arr = {newPos.x-offset, newPos.y+offset, newPos.x+offset, newPos.y+offset,
                        newPos.x+offset, newPos.y-offset, newPos.x-offset, newPos.y-offset};
                PlatformModel newP = new PlatformModel(arr);
                newP.setDrawScale(scaleVector);
                newP.setTexture(underMouse);
                objects.add(newP);
            }
            underMouse = null;
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
        manager.load(ENEMY_FILE,Texture.class);
        manager.load(PLAYER_FILE,Texture.class);
        manager.load(PLATFORM_FILE,Texture.class);
        manager.load(AMMO_DEPOT_FILE,Texture.class);
        manager.load(CAMERA_FILE,Texture.class);
        manager.load(WHITE_PIXEL_FILE,Texture.class);
        levelLoader.preLoadContent(manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        levelLoader.loadContent(manager);
        sidebarTexture = AssetRetriever.createTextureRegion(manager, BACKGROUND_FILE, true);
        whitePixelTexture = AssetRetriever.createTextureRegion(manager, WHITE_PIXEL_FILE, true);
        if (manager.isLoaded(FONT_FILE))
            displayFont = manager.get(FONT_FILE, BitmapFont.class);
        else
            displayFont = null;

        regions[0] = AssetRetriever.createTextureRegion(manager, PLAYER_FILE, false);
        regions[1] = AssetRetriever.createTextureRegion(manager, ENEMY_FILE, false);
        regions[2] = AssetRetriever.createTextureRegion(manager, PLATFORM_FILE, false);
        regions[3] = AssetRetriever.createTextureRegion(manager, AMMO_DEPOT_FILE, false);
        regions[4] = AssetRetriever.createTextureRegion(manager, CAMERA_FILE, false);
        regions[5] = AssetRetriever.createTextureRegion(manager, ENEMY_FILE, false);
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
        if (manager.isLoaded(WHITE_PIXEL_FILE)) {
            manager.unload(WHITE_PIXEL_FILE);
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

        if (player != null && target != null)
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