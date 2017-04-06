package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.entity.models.PlayerModel;
import edu.cornell.gdiac.game.input.PlayerInputController;

/**
 * Created by Lu on 3/16/2017.
 *
 * This class handles the PlayerModel's logic
 * 
 */
public class PlayerController extends EntityController {

    /** The input controller associated with the PlayerModel **/
    private PlayerInputController input;

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
        player.applyForce();
    }
}
