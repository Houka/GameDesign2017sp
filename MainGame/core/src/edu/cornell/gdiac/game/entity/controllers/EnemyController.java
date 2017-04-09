package edu.cornell.gdiac.game.entity.controllers;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.entity.models.EnemyModel;
import edu.cornell.gdiac.game.entity.models.PlayerModel;

/**
 * Created by Lu on 3/16/2017.
 *
 * This class is the controller which handles the logic for an EnemyModel
 */
public class EnemyController extends EntityController {
    
    /** The enemy which this controller controls**/
    private EnemyModel enemy;

    /** The constructor for EnemyController
    *@param player      The player which this enemy keeps track of
    *@param enemyModel  The enemy model which this controller controls
    **/
    public EnemyController(PlayerModel player, EnemyModel enemyModel){
        super(player);
        enemy = enemyModel;
    }
    
    @Override
    public void update(float dt) {
        if (!enemy.isStunned()) {
            if (enemy.isOnSight())
                enemy.setShooting(getInLineOfSight());
            else
                enemy.setShooting(true);
        }

        updateAnimation();
    }

    private void updateAnimation(){
        if(enemy.isStunned())
            enemy.getAnimation().playOnce("stunned");
        else if(enemy.isShooting()) {
            if (enemy.isOnSight() && getInLineOfSight())
                enemy.getAnimation().playOnce("shoot");
            else if (!enemy.isOnSight())
                enemy.getAnimation().playOnce("shoot");
        }
        else
            enemy.getAnimation().play("still", true);
    }

    private boolean getInLineOfSight(){
        return enemy.getPosition().y < (player.getPosition().y + 1)
                && enemy.getPosition().y > (player.getPosition().y - 1);
    }
}
