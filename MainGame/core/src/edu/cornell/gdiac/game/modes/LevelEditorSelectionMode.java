package edu.cornell.gdiac.game.modes;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import edu.cornell.gdiac.game.GameCanvas;
import edu.cornell.gdiac.game.input.SelectionInputController;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.util.AssetRetriever;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Created by cece on 3/26/2017.
 */

public class LevelEditorSelectionMode extends Mode {
    // TODO: remove this once we have working json file loading in assetmanager
    private static final String[] NUM_LEVELS = {
            "JSON/level1.json", "JSON/level3.json", "JSON/default.json", "JSON/default.json", "JSON/level5.json",
            "JSON/default.json", "JSON/default.json", "JSON/default.json", "JSON/default.json", "JSON/default.json",
            "JSON/default.json", "JSON/default.json", "JSON/default.json", "JSON/default.json", "JSON/default.json",
            "JSON/default.json", "JSON/default.json", "JSON/default.json", "JSON/default.json", "JSON/default.json"};
    private static final int TOTAL_COLUMNS = 4;
    private static final int TOTAL_ROWS = (int) Math.ceil((float) NUM_LEVELS.length / TOTAL_COLUMNS);
    private static final int BORDER_X = 50;
    private static final int BORDER_Y = 50;

    // Textures necessary to support the loading screen
    private static final String BACKGROUND_FILE = "ui/bg/level_selection.png";
    /**
     * Retro font for displaying messages
     */
    private static String FONT_FILE = "fonts/RetroGame.ttf";
    private static int FONT_SIZE = 64;

    /**
     * The font for giving messages to the player
     */
    protected BitmapFont displayFont;

    /**
     * Input controller for menu selection
     */
    private SelectionInputController input;
    /**
     * The position of the level that the player is selecting
     */
    private int selected;
    /**
     * The Game mode where players can play
     */
    private LevelEditorMode levelEditorMode;


    /**
     * Creates a LevelSelectionMode with the default size and position.
     *
     * @param canvas  The GameCanvas to draw to
     * @param manager The AssetManager to load in the background
     */
    public LevelEditorSelectionMode(GameCanvas canvas, AssetManager manager) {
        super(canvas, manager);
        onExit = ScreenListener.EXIT_MENU;
        input = SelectionInputController.getInstance();
        levelEditorMode = new LevelEditorMode(canvas, manager);
        selected = 0;
    }

    // BEGIN: Setters and Getters

    // END: Setters and Getters
    @Override
    public void setScreenListener(ScreenListener listener) {
        super.setScreenListener(listener);
        levelEditorMode.setScreenListener(listener);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected void update(float delta) {
        input.readInput();

        if (input.didDown())
            selected = (selected + TOTAL_COLUMNS >= NUM_LEVELS.length) ? selected : selected + TOTAL_COLUMNS;
        else if (input.didUp())
            selected = (selected < TOTAL_COLUMNS) ? selected : selected - TOTAL_COLUMNS;
        else if (input.didRight())
            selected = (selected + 1) % NUM_LEVELS.length;
        else if (input.didLeft())
            selected = (selected <= 0) ? NUM_LEVELS.length - 1 : selected - 1;
        else if (input.didSelect())
            setComplete(true);
    }

    @Override
    protected void onComplete() {
        levelEditorMode.loadContent(manager);
        levelEditorMode.loadLevel(NUM_LEVELS[selected]);
        // TODO: remove, for tech demo and testing values
        Sidebar.defaultBootup();
        listener.switchScreens(this, levelEditorMode);
    }

    @Override
    protected void draw() {
        super.draw();

        for (int i = 0; i < TOTAL_COLUMNS; i++) {
            for (int j = 0; j < TOTAL_ROWS; j++) {
                if (selected == convertToIndex(i, j))
                    displayFont.setColor(Color.WHITE);
                else
                    displayFont.setColor(Color.DARK_GRAY);

                if (convertToIndex(i, j) < NUM_LEVELS.length) {
                    canvas.drawText("/" + (convertToIndex(i, j) + 1) + "\\", displayFont,
                            i * ((canvas.getWidth() - BORDER_X * 2) / TOTAL_COLUMNS) + BORDER_X,
                            canvas.getHeight() - j * displayFont.getLineHeight() - BORDER_Y);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void preLoadContent(AssetManager manager) {
        manager.load(BACKGROUND_FILE, Texture.class);

        // Load the font
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);

        levelEditorMode.preLoadContent(manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
        background = AssetRetriever.createTexture(manager, BACKGROUND_FILE, true).getTexture();

        // Allocate the font
        if (manager.isLoaded(FONT_FILE))
            displayFont = manager.get(FONT_FILE, BitmapFont.class);
        else
            displayFont = null;

        // TODO: loads all the level json files
    }

    @Override
    public void unloadContent(AssetManager manager) {
        if (manager.isLoaded(BACKGROUND_FILE)) {
            manager.unload(BACKGROUND_FILE);
        }

        levelEditorMode.unloadContent(manager);
    }

    private int convertToIndex(int x, int y) {
        return x + (y * TOTAL_COLUMNS);
    }
}