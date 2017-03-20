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
    private Vector2 hitPoint;
    private int ticks;
    private int shotTicks=0;
    private int stunTicks=0;
    private static final int stunTime = 200;

    public EnemyController(PlayerModel player, EnemyModel enemyModel){
        super(player);
        enemy = enemyModel;
        this.ticks = 0;
        this.shotTicks = enemyModel.getInterval();
    }

    private boolean canShoot(EnemyModel enemy, PlayerModel player) {
        if (enemy.isOnSight()) {
            return (enemy.getPosition().y < (player.getPosition().y + 1)
                    && enemy.getPosition().y > (player.getPosition().y - 1));
        }
        else {
            return (shotTicks > enemy.getInterval());
        }
    }

    @Override
    public void update(float dt) {
        if (enemy.isShooting()) {
            shotTicks = 0;
            enemy.setShootCooldown(enemy.SHOT_COOLDOWN);
        }

        ticks++;
        shotTicks++;
        if (enemy.isStunned()) {
            stunTicks++;
        }
        if (stunTicks > stunTime) {
            enemy.setStunned(false);
            stunTicks = 0;
        }

        enemy.setShootCooldown(enemy.getShootCooldown() - 1);
        enemy.setShooting(canShoot(enemy, player) && !enemy.isStunned());
    }
}
