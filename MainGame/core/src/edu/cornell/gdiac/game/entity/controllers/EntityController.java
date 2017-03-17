package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.entity.models.PlayerModel;

/**
 * Created by Lu on 3/16/2017.
 *
 * TODO: write class desc
 */
public abstract class EntityController {
    protected PlayerModel player;

    public EntityController(PlayerModel player){ this.player = player;}

    public abstract void update(float dt);
}
