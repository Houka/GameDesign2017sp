package edu.cornell.gdiac.game.levelLoading;

/**
 * Created by Lu on 2/27/2017.
 *
 * The Level Parser class
 *  - loads data of a level
 *  - parses data to a standard format for our game controllers
 *  - returns the information
 *
 * TODO: figure out what the values in WALLS and PLATFORMS represent
 * TODO: figure out how to make the medium level with this level format from WALLS and PLATFORMS
 * TODO: (extra) abstract the data out into a data file that can be loaded with this class with a string of the file name
 */
public class LevelParser {
    // Since these appear only once, we do not care about the magic numbers.
    // In an actual game, this information would go in a data file.
    // Wall vertices
    private static final float[][] WALLS = {
            {16.0f, 18.0f, 16.0f, 17.0f,  1.0f, 17.0f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f, 18.0f},
            {32.0f, 18.0f, 32.0f,  0.0f, 31.0f,  0.0f, 31.0f, 17.0f, 16.0f, 17.0f, 16.0f, 18.0f}
    };

    /** The outlines of all of the platforms */
    private static final float[][] PLATFORMS = {
            // (x top left, y top left, x top right, y top right, x bottom right, y bottom left, x bottom left, y bottom right)

            { 1.0f, 1.0f, 6.0f, 1.0f, 6.0f, 0.0f, 1.0f, 0.0f}, // starting platform
            {23.0f, 0.0f,31.0f, 0.0f,31.0f, 1.0f,23.0f, 1.0f}, // right of bridge

            {0.0f, 5.5f,4.0f, 5.5f,4.0f, 5.0f,0.0f, 5.0f}, // 6th platform on the right
            {0.0f, 9.0f,7.0f, 9.0f,7.0f, 8.5f,0.0f, 8.5f}, // 5th platform on the right
            { 1.0f,12.5f, 10.0f,12.5f, 10.0f,12.0f, 1.0f,12.0f} // finish platform
    };

    /** Creates a level parser object*/
    public LevelParser(){}

    /** Returns the walls of a level. Currently we just have one level*/
    public float[][] getWalls(){
        return WALLS;
    }

    /** Returns the platforms of a level. Currently we just have one level*/
    public float[][] getPlatforms(){
        return PLATFORMS;
    }
}
