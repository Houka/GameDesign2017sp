package edu.cornell.gdiac.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Lu on 3/13/2017.
 *
 * Static class that helps with retrieving assests from the AssetManager
 */
public class AssetRetriever {
    /**
     * Returns a newly loaded texture region for the given file.
     *
     * This helper methods is used to set texture settings (such as scaling, and
     * whether or not the texture should repeat) after loading.
     *
     * @param manager 	Reference to global asset manager.
     * @param file		The texture (region) file
     * @param repeat	Whether the texture should be repeated
     *
     * @return a newly loaded texture region for the given file.
     */
    public static TextureRegion createTextureRegion(AssetManager manager, String file, boolean repeat) {
        if (manager.isLoaded(file)) {
            TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
            region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            if (repeat) {
                region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
            return region;
        }
        return null;
    }

    /**
     * Returns a newly loaded texture for the given file.
     *
     * This helper methods is used to set texture settings (such as scaling, and
     * whether or not the texture should repeat) after loading.
     *
     * @param manager 	Reference to global asset manager.
     * @param file		The texture (region) file
     * @param repeat	Whether the texture should be repeated
     *
     * @return a newly loaded texture region for the given file.
     */
    public static Texture createTexture(AssetManager manager, String file, boolean repeat) {
        if (manager.isLoaded(file)) {
            Texture texture = manager.get(file, Texture.class);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            if (repeat) {
                texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
            return texture;
        }
        return null;
    }
}
