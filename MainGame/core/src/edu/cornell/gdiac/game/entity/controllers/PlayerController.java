package edu.cornell.gdiac.game.entity.controllers;

import com.sun.scenario.Settings;
import edu.cornell.gdiac.game.entity.models.PlayerModel;
import edu.cornell.gdiac.game.input.PlayerInputController;
import edu.cornell.gdiac.util.sidebar.Sidebar;

/**
 * Created by Lu on 3/16/2017.
 *
 * This class handles the PlayerModel's logic
 *
 */
public class PlayerController extends EntityController {
    /** Threshold to play Animation when player is off the ground **/
    private static final int OFF_GROUND_THRESHOLD = 5;
    /** The input controller associated with the PlayerModel **/
    private PlayerInputController input;
    private boolean superJumpEnabled;

    /**
     * PlayerController's contructor
     * @player The PlayerModel that this PlayerController controls
     **/
    public PlayerController(PlayerModel player){
        super(player);
        input = PlayerInputController.getInstance();

    }

    @Override
    public void update(float dt) {
        input.readInput();

        player.setMovement(input.getHorizontal());
        player.setJumping(input.didJump());
        player.setShooting(input.didShoot());
        player.setCrouching(input.isDownHeld());
        player.applyForce();

        updateAnimation();
    }

    public void superJumpEnabled(boolean value) {
        superJumpEnabled = value;
    }

    private void updateAnimation(){
        if (player.isShooting())
            player.getAnimation().playOnce("shoot");
        else if(player.isGrounded() && player.isCrouching())
            player.getAnimation().play("crouch",false);
        else if (!player.isGrounded() && player.getVY() < -OFF_GROUND_THRESHOLD)
            player.getAnimation().play("falling", true);
        else if (!player.isGrounded() && player.getVY() > OFF_GROUND_THRESHOLD) {
            if (player.isDoubleJumping())
                player.getAnimation().setPlayingAnimation("still");
            player.getAnimation().play("rising", true);
        }
        else if (!player.isGrounded() &&
                player.getVY() <= OFF_GROUND_THRESHOLD + 1 && player.getVY() >= OFF_GROUND_THRESHOLD-1)
            player.getAnimation().playOnce("peak");
        else if (!player.isGrounded() && player.isKnockedBack())
            player.getAnimation().playOnce("stunned");
        else if (player.isGrounded() && input.getHorizontal() != 0)
            player.getAnimation().play("run", true);
        else if (player.isGrounded())
            player.getAnimation().play("idle", true);
    }
}