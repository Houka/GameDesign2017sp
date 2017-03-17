package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.input.PlayerInputController;

/**
 * Created by Lu on 3/16/2017.
 *
 * TODO: write class desc
 */
public class PlayerController extends EntityController {
    private PlayerInputController input;

    public PlayerController(){
        input = PlayerInputController.getInstance();
    }

    @Override
    public void update(float dt) {
        input.readInput();

        getPlayer().setMovement(input.getHorizontal());
        getPlayer().setJumping(input.didJump());
        getPlayer().setShooting(input.didShoot());
        getPlayer().applyForce();

        //TODO remove this and put it in collision controller
        getPlayer().setGrounded(true);
    }
}
