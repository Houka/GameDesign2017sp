package edu.cornell.gdiac.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/**
 * Created by Lu on 4/5/2017.
 */
public class Animation implements Disposable{
    /** Frames per second we want to play in */
    private static final int FPS = 10;

    /** All the filmstrips in this animation with a string key that's the name of the strip*/
    private HashMap<String, FilmStrip> filmStrips;

    /** Frame control variables */
    private float framesPassed = 0f;

    /** animation variables */
    private int currentFrame = 0;
    private String currentStrip = "";
    private boolean isPlaying = false;
    private boolean isLooping = false;
    private boolean hasPlayedOnce = true;

    /**
     *  Constructor
     */
    public Animation() {
        filmStrips = new HashMap<String, FilmStrip>();
    }

    // BEGIN: setters and getters

    /**
     * @return the currently playing strip in the specific frame
     */
    public TextureRegion getTextureRegion(){
        return filmStrips.get(currentStrip);
    }

    /**
     * Sets the current filmstrip this animation should play
     *
     * @param stripName the name that corresponds to the filmstrip we want to play
     */
    public void setPlayingAnimation(String stripName){
        if (!currentStrip.equals(stripName))
            currentFrame = 0;
        currentStrip = stripName;
    }

    /**
     * Whether or not to play the current strip
     *
     * @param value to play or not to play
     */
    public void setPlaying(boolean value){
        isPlaying = value;
    }

    /**
     * Whether or not the current strip is playing
     *
     * @return  whether animation is playing or not
     */
    public boolean isPlaying(){
        return isPlaying;
    }

        // END: setters and getters

    /**
     * Add a spritesheet and converts it to a filmstrip to be included in this animation group
     */
    public void addTexture(String name, Texture texture, int rows, int cols){
        filmStrips.put(name, new FilmStrip(texture, rows, cols));
    }

    /**
     * Plays the filmstrip, but waits until the last animation can be interrupted
     *
     * @param stripName the filmstrip to be played
     * @param loop whether or not to loop this animation
     */
    public void play(String stripName, boolean loop){
        if (hasPlayedOnce) {
            setPlayingAnimation(stripName);
            setPlaying(true);
            isLooping = loop;
        }
    }

    /**
     * Plays the filmstrip completely through once
     *
     * @param stripName the filmstrip to be played
     */
    public void playOnce(String stripName){
        if (stripName != currentStrip) {
            play(stripName, false);
            hasPlayedOnce = false;
        }
    }

    /**
     * Updates the animation.
     *
     * This method is only used to update what frame we are on
     * while playing the current strip.
     *
     * @param delta Number of seconds since last animation frame
     */
    public void update(double delta){
        // update frames
        framesPassed += delta;
        if (framesPassed < 1f/FPS)
            return;
        framesPassed = 0f;

        advanceFrame();
    }

    /**
     * Advance the animation one frame.
     */
    public void advanceFrame() {
        if (isPlaying){
            FilmStrip filmStrip = filmStrips.get(currentStrip);
            filmStrip.setFrame(currentFrame);
            currentFrame = (currentFrame+1) % filmStrip.getSize();
            if (currentFrame == 0 && !isLooping) {
                isPlaying = false;
                hasPlayedOnce = true;
            }
        }
    }

    /**
     *  Return the current strip's name.
     *  @return the name of the strip that is currently playing
     */
    public String getCurrentStrip(){
        return currentStrip;
    }

    @Override
    public void dispose(){
        filmStrips.clear();
        filmStrips = null;
    }
}
