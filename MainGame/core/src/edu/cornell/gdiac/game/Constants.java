package edu.cornell.gdiac.game;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Lu on 4/14/2017.
 */
public class Constants {
    /** Game music*/
    public static String GAME_MUSIC_FILE = "music/gameplay_background.mp3";
    public static String MENU_MUSIC_FILE = "music/menu_background.mp3";

    /** Sound effects*/
    public static String SFX_CAMERA_EXPLODE = "sfx/camera_explode.wav";
    public static String SFX_ENEMY_ALERT = "sfx/enemy_alert.wav";
    public static String SFX_ENEMY_SHOT = "sfx/enemy_shot.wav";
    public static String SFX_ENEMY_STUN= "sfx/enemy_stun.wav";
    public static String SFX_PAINT_HIT_PAINT = "sfx/paint_hit_paint.wav";
    public static String SFX_PAINT_JUMP = "sfx/paint_jump.wav";
    public static String SFX_PAINT_JUMP_CHARGE = "sfx/paint_jump_charge.wav";
    public static String SFX_PAINT_PLATFORM_SPWAN= "sfx/paint_platform_spawn.wav";
    public static String SFX_PAINT_POP= "sfx/paint_pop.wav";
    public static String SFX_PAINT_POP_LIGHT= "sfx/paint_pop_light.wav";
    public static String SFX_PAINT_RELOAD = "sfx/paint_reload.wav";
    public static String SFX_PLAYER_DEATH = "sfx/player_death.wav";
    public static String SFX_PLAYER_DRY_FIRE = "sfx/player_dry_fire.wav";
    public static String SFX_PLAYER_JUMP_LONG= "sfx/player_jump_long.wav";
    public static String SFX_PLAYER_JUMP_SHORT= "sfx/player_jump_short.wav";
    public static String SFX_PLAYER_LAND= "sfx/player_stun.wav";
    public static String SFX_PLAYER_SHOT = "sfx/player_shot.wav";
    public static String SFX_PLAYER_STUN= "sfx/player_land.wav";
    public static String SFX_UI_HOVER = "sfx/UI_hover.wav";
    public static String SFX_UI_SELECT = "sfx/UI_select.wav";

    /** Retro font for displaying messages */
    public static String SELECTION_FONT_FILE = "fonts/LightPixel7.ttf";
    public static int SELECTION_FONT_SIZE = 104;
    public static String MENU_FONT_FILE = "fonts/RealGraffiti.ttf";
    public static int MENU_FONT_SIZE = 64;
    public static String FONT_FILE = MENU_FONT_FILE;
    public static int FONT_SIZE = MENU_FONT_SIZE;

    public static Color SELECTED_COLOR = new Color(183/255f, 81/255f, 46/255f, 1f);
    public static Color SELECTED_COLOR_LIGHT = new Color(236/255f, 239/255f, 218/255f, 1f);
    public static Color UNSELECTED_COLOR = new Color(36/255f, 39/255f, 18/255f, 1f);
    public static Color ALPHA = new Color(1f,1f,1f,0.5f);
    public static Color WHITE = new Color(1f,1f,1f,1f);

    /** Textures necessary to support the loading screen */
    public static final String PLAYER_FILE = "sprites/char/char_icon.png";
    public static final String CAMERA_FILE = "sprites/enemy/insignia.png";
    public static final String WHITE_PIXEL_FILE = "ui/white_pixel.png";

    /** Filenames for sprites of objects */
    public static String PLATFORM_FILE = "sprites/fixtures/window_tile.png";
    public static String PLATFORM_LEFT_CAP_FILE = "sprites/fixtures/beams/horizontal_l.png";
    public static String PLATFORM_RIGHT_CAP_FILE = "sprites/fixtures/beams/horizontal_r.png";
    public static String PLATFORM_CENTER_FILE = "sprites/fixtures/beams/horizontal_m.png";
    public static String PLATFORM_SINGLE_FILE = "sprites/fixtures/beams/horizontal_single.png";
    public static String PLATFORM_BLOCK_1_FILE = "sprites/fixtures/block_tile1.png";
    public static String PLATFORM_BLOCK_2_FILE = "sprites/fixtures/block_tile2.png";
    public static String PLATFORM_BLOCK_3_FILE = "sprites/fixtures/block_tile3.png";
    public static String PLATFORM_BLOCK_4_FILE = "sprites/fixtures/block_tile4.png";
    public static String WALL_FILE = "sprites/fixtures/solid.png";
    public static String GOAL_FILE = CAMERA_FILE;
    public static String GOAL_EXPLOSION = "sprites/enemy/insignia_explode_strip8.png";
    public static String BACKGROUND_FILE = "sprites/bg/brick_wall_tile.png";
    public static String AMMO_DEPOT_FILE = "sprites/paint_repo.png";

    public static String ENEMY_ONSIGHT_FILE = "sprites/enemy/enemy_onsight2.png";
    public static String ENEMY_ONSIGHT_ALERTED_FILE = "sprites/enemy/enemy_onsight_alerted_strip5.png";
    public static String ENEMY_ONSIGHT_SHOOTING_FILE = "sprites/enemy/enemy_onsight_shooting_strip6.png";
    public static String ENEMY_ONSIGHT_SHOOT_FILE = "sprites/enemy/enemy_onsight.png";
    public static String ENEMY_INTERVAL_FILE = "sprites/enemy/enemy_interval2.png";
    public static String ENEMY_INTERVAL_SHOOT_FILE = "sprites/enemy/enemy_interval.png";
    public static String ENEMY_INTERVAL_SHOOTING_FILE = "sprites/enemy/enemy_interval_shoot_strip10.png";
    public static String ENEMY_SPOTTED_FILE = "sprites/enemy/enemy_spotted.png";

    public static String PAINTBALL_CHARACTER_FILE = "sprites/bullets/char_paintball_strip5.png";
    public static String PAINTBALL_ENEMY_MINE_FILE = "sprites/bullets/enemy_paintball_mine_strip5.png";
    public static String PAINTBALL_ENEMY_NORMAL_FILE = "sprites/bullets/enemy_paintball_normal_strip5.png";
    public static String PAINTBALL_MINE_TRAIL_FILE = "sprites/trails/moving_mine_strip5.png";
    public static String PAINTBALL_MOVING_MINE_FILE = "sprites/paint/trails/moving_mine_strip5.png";
    public static String PAINTBALL_ARMED_MINE_FILE = "sprites/paint/trails/armed_mine_strip4.png";
    public static String PAINTBALL_ARMED_MINE_WARNING_FILE = "sprites/paint/trails/armed_mine_warning_strip4.png";
    public static String PAINTBALL_PRIMED_MINE_FILE = "sprites/paint/trails/primed_mine_strip6.png";
    public static String PAINTBALL_PRIMED_MINE_WARNING_FILE = "sprites/paint/trails/primed_mine_warning_strip6.png";
    public static String PAINTBALL_NORMAL_TRAIL_FILE = "sprites/trails/moving_normal_strip5.png";
    public static String PAINTBALL_SPLAT_EFFECT_FILE = "sprites/paint_splat_ef_strip10.png";
    public static String PAINTBALL_CHAR_SPLAT_EFFECT_FILE = "sprites/paint/effects/char_splatter_strip15.png";
    public static String PAINTBALL_MINE_ENEMY_SPLAT_EFFECT_FILE = "sprites/paint/effects/enemy_mine_splatter_strip15.png";
    public static String PAINTBALL_ENEMY_SPLAT_EFFECT_FILE = "sprites/paint/effects/enemy_normal_splatter_strip15.png";
    public static String PAINTBALL_STATIONARY_MINE_FILE = "sprites/paint/trails/stationary_mine_strip2.png";
    public static String PAINTBALL_STATIONARY_NORMAL_FILE = "sprites/paint/trails/stationary_normal_strip2.png";
    public static String PAINTBALL_STATIONARY_CHAR_FILE = "sprites/paint/trails/stationary_char_strip2.png";

    public static String CHARACTER_STILL_FILE = "sprites/char/char_icon.png";
    public static String CHARACTER_RUN_FILE = "sprites/char/char_run_shoot_strip4.png";
    public static String CHARACTER_FALLING_FILE = "sprites/char/char_fall_shoot_strip2.png";
    public static String CHARACTER_IDLE_FILE = "sprites/char/char_idle_strip5.png";
    public static String CHARACTER_MIDAIR_FILE = "sprites/char/char_midair_shoot.png";
    public static String CHARACTER_RISING_FILE = "sprites/char/char_jump_shoot_strip2.png";
    public static String CHARACTER_TRANSITION_FILE = "sprites/char/char_transition_shoot_strip2.png";
    public static String CHARACTER_SHOOT_FILE = "sprites/char/char_shoot_strip2.png";
    public static String CHARACTER_CROUCH_FILE = "sprites/char/char_crouch.png";
    public static String CHARACTER_CROUCH_SHOOT_FILE = "sprites/char/char_crouch_shoot.png";
    public static String CHARACTER_STUNNED_FILE = "sprites/char/char_stunned.png";
    public static String CHARACTER_DEATH_FILE = "sprites/char/char_death_strip8.png";

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

    public static final int DEFAULT_GRID = 48;

    /**Ammo bar for hud*/
    public static final String AMMO_BAR = "ui/hud/ammo_bar.png";
    public static final String AMMO_EMPTY= "ui/hud/ammo_empty.png";
    public static final String AMMO_FILLED= "ui/hud/ammo_filled_strip4.png";
}
