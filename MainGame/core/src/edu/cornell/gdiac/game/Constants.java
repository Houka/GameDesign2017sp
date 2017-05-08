package edu.cornell.gdiac.game;

/**
 * Created by Lu on 4/14/2017.
 */
public class Constants {
    public static String GAME_MUSIC_FILE = "music/gameplay_background.mp3";
    /** Retro font for displaying messages */
    public static String FONT_FILE = "fonts/RetroGame.ttf";
    public static int FONT_SIZE = 64;
    public static String SELECTION_FONT_FILE = "fonts/LightPixel7.ttf";
    public static int SELECTION_FONT_SIZE = 200;
    public static String MENU_FONT_FILE = "fonts/RealGraffiti.ttf";
    public static int MENU_FONT_SIZE = 64;

    /** Textures necessary to support the loading screen */
    public static final String PLAYER_FILE = "sprites/char/char_icon.png";
    public static final String CAMERA_FILE = "sprites/fixtures/security_camera.png";
    public static final String WHITE_PIXEL_FILE = "ui/white_pixel.png";

    /** Filenames for sprites of objects */
    public static String PLATFORM_FILE = "sprites/fixtures/window_tile.png";
    public static String WALL_FILE = "sprites/fixtures/solid.png";
    public static String GOAL_FILE = CAMERA_FILE;
    public static String BACKGROUND_FILE = "sprites/bg/brick_wall_tile.png";
    public static String AMMO_DEPOT_FILE = "sprites/paint_repo.png";

    public static String ENEMY_ONSIGHT_FILE = "sprites/enemy/enemy_onsight.png";
    public static String ENEMY_INTERVAL_FILE = "sprites/enemy/enemy_interval.png";
    public static String ENEMY_SPOTTED_FILE = "sprites/enemy/enemy_spotted.png";

    public static String PAINTBALL_CHARACTER_FILE = "sprites/bullets/char_paintball_strip5.png";
    public static String PAINTBALL_ENEMY_MINE_FILE = "sprites/bullets/enemy_paintball_mine_strip5.png";
    public static String PAINTBALL_ENEMY_NORMAL_FILE = "sprites/bullets/enemy_paintball_normal_strip5.png";
    public static String PAINTBALL_MINE_TRAIL_FILE = "sprites/trails/moving_mine_strip5.png";
    public static String PAINTBALL_NORMAL_TRAIL_FILE = "sprites/trails/moving_normal_strip5.png";
    public static String PAINTBALL_SPLAT_EFFECT_FILE = "sprites/paint_splat_ef_strip10.png";
    public static String PAINTBALL_CHAR_SPLAT_EFFECT_FILE = "sprites/paint/effects/char_splatter_strip15.png";
    public static String PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE = "sprites/paint/effects/enemy_mine_splatter_strip15.png";
    public static String PAINTBALL_ENEMY_SPLAT_EFFECT_FILE = "sprites/paint/effects/enemy_normal_splatter_strip15.png";
    public static String PAINTBALL_STATIONARY_MINE_FILE = "sprites/paint/trails/stationary_mine.png";
    public static String PAINTBALL_STATIONARY_NORMAL_FILE = "sprites/paint/trails/stationary_normal.png";
    public static String PAINTBALL_STATIONARY_CHAR_FILE = "sprites/paint/trails/stationary_char.png";

    public static String CHARACTER_STILL_FILE = "sprites/char/char_icon.png";
    public static String CHARACTER_RUN_FILE = "sprites/char/char_run_strip4.png";
    public static String CHARACTER_FALLING_FILE = "sprites/char/char_fall_strip2.png";
    public static String CHARACTER_IDLE_FILE = "sprites/char/char_idle_strip5.png";
    public static String CHARACTER_MIDAIR_FILE = "sprites/char/char_midair_shoot.png";
    public static String CHARACTER_RISING_FILE = "sprites/char/char_jump_strip2.png";
    public static String CHARACTER_TRANSITION_FILE = "sprites/char/char_transition_strip2.png";
    public static String CHARACTER_SHOOT_FILE = "sprites/char/char_shoot.png";
    public static String CHARACTER_CROUCH_FILE = "sprites/char/char_crouch.png";
    public static String CHARACTER_CROUCH_SHOOT_FILE = "sprites/char/char_crouch_shoot.png";
    public static String CHARACTER_STUNNED_FILE = "sprites/char/char_stunned.png";

    public static String TUTORIAL_JUMP_FILE = "sprites/tutorials/tutor_jump.png";
    public static String TUTORIAL_CROUCH_FILE = "sprites/tutorials/tutor_crouch.png";
    public static String TUTORIAL_MOVE_FILE = "sprites/tutorials/tutor_move.png";
    public static String TUTORIAL_FORM_PLATFORM_FILE = "sprites/tutorials/tutor_form_platform.png";
    public static String TUTORIAL_RIDE_FILE = "sprites/tutorials/tutor_ride.png";
    public static String TUTORIAL_SHOOT_FILE = "sprites/tutorials/tutor_shoot.png";
    public static String TUTORIAL_SHOOT_TARGET_FILE = "sprites/tutorials/tutor_shoot_target.png";
    public static String TUTORIAL_TRAMPOLINE_FILE = "sprites/tutorials/tutor_trampoline.png";

    public static String[] TUTORIAL_FILES = {TUTORIAL_CROUCH_FILE, TUTORIAL_JUMP_FILE, TUTORIAL_FORM_PLATFORM_FILE,
            TUTORIAL_MOVE_FILE, TUTORIAL_RIDE_FILE, TUTORIAL_SHOOT_FILE, TUTORIAL_SHOOT_TARGET_FILE, TUTORIAL_TRAMPOLINE_FILE};

    /** Texture column numbers**/
    public static int PAINTBALL_TRAIL_COLUMNS = 5;

    /** Texture file */
    public static final String PAINTBALL_FILE = "sprites/paintball.png";
    public static final String SPLATTERER_FILE = "sprites/fixtures/splatterer.png";

    public static final String SPIKES_DOWN_SPIN_FILE = "sprites/fixtures/spikes/spikes_down_strip8.png";
    public static final String SPIKES_LEFT_SPIN_FILE = "sprites/fixtures/spikes/spikes_left_strip8.png";
    public static final String SPIKES_RIGHT_SPIN_FILE = "sprites/fixtures/spikes/spikes_right_strip8.png";
    public static final String SPIKES_UP_SPIN_FILE = "sprites/fixtures/spikes/spikes_up_strip8.png";
    public static final String SPIKES_DOWN_STILL_FILE = "sprites/fixtures/spikes/spikes_down.png";
    public static final String SPIKES_LEFT_STILL_FILE = "sprites/fixtures/spikes/spikes_left.png";
    public static final String SPIKES_RIGHT_STILL_FILE = "sprites/fixtures/spikes/spikes_right.png";
    public static final String SPIKES_UP_STILL_FILE = "sprites/fixtures/spikes/spikes_up.png";

}
