package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.entity.models.PlayerModel;

/**
 * Created by Lu on 3/16/2017.
 *
 * Abstract class for game entity controllers
 */
public abstract class EntityController {
    /** The model associated with this controller **/
    protected PlayerModel player;

    /** The contructor for EntityController
    *@param player      The model to be associated with this controller
    **/
    public EntityController(PlayerModel player){ this.player = player;}

    /**
    * This method will update the EntityController's model
    @param dt       The time-step delta
    **/
    public abstract void update(float dt);
}
