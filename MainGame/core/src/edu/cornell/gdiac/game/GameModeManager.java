package edu.cornell.gdiac.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import edu.cornell.gdiac.game.interfaces.AssetUser;
import edu.cornell.gdiac.game.interfaces.ScreenListener;
import edu.cornell.gdiac.game.modes.*;
import edu.cornell.gdiac.util.SoundController;
import edu.cornell.gdiac.util.sidebar.Sidebar;

import java.util.HashMap;

/**
 * Created by Lu on 4/4/2017.
 */
public class GameModeManager implements Disposable, AssetUser{
    public static String MENU = "menu";
    public static String LEVEL_SELECTION = "levelSelection";
    public static String LEVEL_EDITOR = "levelEditor";
    public static String GAME_MODE = "gameMode";
    public static String LOADING = "loading";

    /** AssetManager to be loading in the background */
    private AssetManager manager;
    /** Reference to GameCanvas created by the root */
    private GameCanvas canvas;

    /** Hashmap of all the modes with their name as the key */
    HashMap<String, Mode> modes;
    /** Mapping of what mode exits to what mode, if no mapping exists then its an empty string*/
    HashMap<String, String> modeExitMapping;

    public GameModeManager(GameCanvas canvas, AssetManager manager){
        this.manager = manager;
        this.canvas = canvas;
        modes = new HashMap<String, Mode>();
        modeExitMapping = new HashMap<String, String>();
        initModes();
    }

    // BEGIN: setters and getters
    public void setScreenListener(ScreenListener listener) {
        for(Mode m: modes.values())
            m.setScreenListener(listener);
    }

    /**
     * Returns the mode that we deem to be the mode we go to given the current mode
     *
     * i.e. we mapped it so that LoadingMode will always go to MenuMode if it exits
     *
     * If a mode does not have a mode to exit to, we return null
     *
     * @param currentMode the name mode that is exiting
     */
    public Screen getExitToMode(String currentMode){
        Mode m = modes.get(modeExitMapping.get(currentMode));
        m.loadContent(manager);
        return m;
    }

    /**
     * Returns the mode
     *
     * @param modeName the name of the mode to get
     */
    public Screen getMode(String modeName){
        Mode m = modes.get(modeName);
        m.loadContent(manager);
        return m;
    }

    // END: setters and getters

    public void dispose() {
        for(Mode m : modes.values()){
            m.unloadContent(manager);
            m.dispose();
        }

        modes.clear();
        modeExitMapping.clear();

        modes = null;
        modeExitMapping = null;
    }

    private void initModes(){
        Sidebar.initializeVariables();

        GameMode gameMode = new GameMode(GAME_MODE, canvas,manager);
        modes.put(MENU, new MenuMode(MENU, canvas, manager));
        modes.put(LEVEL_EDITOR, new LevelEditorMode(LEVEL_EDITOR, canvas, manager));
        modes.put(GAME_MODE, gameMode);
        modes.put(LEVEL_SELECTION, new LevelSelectionMode(LEVEL_SELECTION, canvas, manager, gameMode));

        modeExitMapping.put(MENU, ""); // if menu exits we want to exit the game
        modeExitMapping.put(LOADING, MENU);
        modeExitMapping.put(LEVEL_SELECTION, MENU);
        modeExitMapping.put(LEVEL_EDITOR, MENU);
        modeExitMapping.put(GAME_MODE, LEVEL_SELECTION);
    }

    @Override
    public void preLoadContent(AssetManager manager) {
        for (Mode m:modes.values())
            m.preLoadContent(manager);
    }

    @Override
    public void loadContent(AssetManager manager) {
    }

    @Override
    public void unloadContent(AssetManager manager) {
        for (Mode m:modes.values())
            m.unloadContent(manager);
    }
}
