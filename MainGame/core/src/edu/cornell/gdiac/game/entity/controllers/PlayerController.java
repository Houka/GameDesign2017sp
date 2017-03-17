package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.entity.models.PlayerModel;
import edu.cornell.gdiac.game.input.PlayerInputController;

/**
 * Created by Lu on 3/16/2017.
 *
 * TODO: write class desc
 */
public class PlayerController extends EntityController {
    private PlayerInputController input;

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

        //TODO remove this and put it in collision controller
        player.setGrounded(true);
    }
}
