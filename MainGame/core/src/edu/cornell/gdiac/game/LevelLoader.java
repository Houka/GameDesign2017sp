package edu.cornell.gdiac.game;

import com.badlogic.gdx.assets.AssetManager;
import edu.cornell.gdiac.game.interfaces.AssetUser;

/**
 * Created by Lu on 3/14/2017.
 *
 * TODO: write class desc
 */
public class LevelLoader implements AssetUser {

    /**
     *  TODO: write desc
     */
    public LevelLoader(){

    }

    // BEGIN: Setters and Getters

    // END: Setters and Getters

    @Override
    public void preLoadContent(AssetManager manager) {
        // TODO: this should load all the models we will ever use in any level
    }

    @Override
    public void loadContent(AssetManager manager) {
        // not used
    }

    @Override
    public void unloadContent(AssetManager manager) {
        // TODO: unload all models used in any level
    }
}
