package edu.cornell.gdiac.game.entity.controllers;

import com.badlogic.gdx.math.Vector2;
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
        if (enemy.isStunned())
            return;

        if (enemy.isOnSight())
            enemy.setShooting(enemy.getPosition().y < (player.getPosition().y + 1)
                    && enemy.getPosition().y > (player.getPosition().y - 1));
        else
            enemy.setShooting(true);
    }
}
