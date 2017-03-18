package edu.cornell.gdiac.game.entity.controllers;

import edu.cornell.gdiac.game.entity.models.EnemyModel;
import edu.cornell.gdiac.game.entity.models.PlayerModel;

/**
 * Created by Lu on 3/16/2017.
 *
 * TODO: write class desc
 */
public class EnemyController extends EntityController {
    private EnemyModel enemy;

    public EnemyController(PlayerModel player, EnemyModel enemyModel){
        super(player);
        enemy = enemyModel;
    }

    @Override
    public void update(float dt) {
        // TODO for testing, please remove
        enemy.setFacingRight(player.isFacingRight());
    }
}
