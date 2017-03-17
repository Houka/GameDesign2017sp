package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.entity.models.PlayerModel;

/**
 * Created by Lu on 3/16/2017.
 *
 * TODO: write class desc
 */
public abstract class EntityController {
    private PlayerModel player;

    public EntityController(){}

    // BEGIN: Setters and Getters
    public void setPlayer(PlayerModel player){this.player = player;}
    public PlayerModel getPlayer(){ return player; }
    // END: Setters and Getters

    public abstract void update(float dt);
}
