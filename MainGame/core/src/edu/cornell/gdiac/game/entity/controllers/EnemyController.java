package edu.cornell.gdiac.game.entity.controllers;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.game.Constants;
import edu.cornell.gdiac.game.entity.models.EnemyModel;
import edu.cornell.gdiac.game.entity.models.PlayerModel;
import edu.cornell.gdiac.util.SoundController;

/**
 * Created by Lu on 3/16/2017.
 *
 * This class is the controller which handles the logic for an EnemyModel
 */
public class EnemyController extends EntityController {
    
    /** The enemy which this controller controls**/
    private EnemyModel enemy;
    private int lastFrame = 0;

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
        //TODO: romove this logic and incorportate to animation
//        if (!enemy.isStunned()) {
//            if (enemy.isOnSight())
//                enemy.setShooting(getInLineOfSight());
//            else
//                enemy.setShooting(true);
//        }

        updateAnimation();
    }

    private void updateOnSightAnimation(){
        if (getInLineOfSight() || lastFrame!=0){
            enemy.getAnimation().play("alert",false);
            if(lastFrame != 0 && enemy.getAnimation().getCurrentFrame() == 0){
                enemy.setShooting(true);
                enemy.getAnimation().playOnce("shooting");
                lastFrame=0;
            }
            lastFrame = enemy.getAnimation().getCurrentFrame();
        }else{
            enemy.getAnimation().play("still", true);
            enemy.setShooting(false);
            lastFrame=0;
        }
    }

    private void updateIntervalAnimation(){
        if(enemy.getShootCooldownCounter() < 40) {
            enemy.getAnimation().play("shooting", false);
        }
    }

    private void updateAnimation() {
        if (!enemy.isStunned()) {
            if (enemy.isOnSight()) {
                updateOnSightAnimation();
            } else {
                updateIntervalAnimation();
                enemy.setShooting(true);
            }
        }
    }

    private void oldUpdateAnimation(){
        if(enemy.isShooting() || enemy.getShootCooldownCounter() < 10) {
            if (enemy.isOnSight() && getInLineOfSight()) {
                //SoundController.getSFXInstance().play("gameMode", Constants.SFX_ENEMY_ALERT, false);
                enemy.getAnimation().playOnce("shoot");
            }
            else if (!enemy.isOnSight())
                enemy.getAnimation().playOnce("shoot");
        }
        else
            enemy.getAnimation().play("still", true);
    }

    private boolean getInLineOfSight(){
        boolean right = enemy.isFacingRight() && enemy.getPosition().x < (player.getPosition().x);
        boolean left = !enemy.isFacingRight() && enemy.getPosition().x > (player.getPosition().x);
        return enemy.getPosition().y < (player.getPosition().y + 1)
                && enemy.getPosition().y > (player.getPosition().y - 1) && (right||left);
    }
}
