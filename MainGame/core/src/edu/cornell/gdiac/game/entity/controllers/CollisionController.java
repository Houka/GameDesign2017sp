package edu.cornell.gdiac.game.entity.controllers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.util.obstacles.Obstacle;

/**
 * Created by Lu on 3/17/2017.
 *
 * This class handles collisions between game objects
 *
 * object names so far
 *  background
 *  hud
 *  goal
 *  enemy
 *  player
 *  wall
 *  platform
 *  paintball
 *
 *  EnemyGroundSensor
 *  PlayerGroundSensor
 */
public class CollisionController implements ContactListener {
    /** the hud*/
    private HUDModel hud;
    /** Mark set to handle more sophisticated collision callbacks */
    private ObjectSet<Object> sensorObjects;

    /**
     *  The contructor
     * @param hud   The HUD to update
     */
    public CollisionController(HUDModel hud){
        this.hud = hud;
        sensorObjects = new ObjectSet<Object>();
    }

    // BEGIN: helper functions

    /**
     *  This function updates the state as if the player touched the ground
     * @param obj1          The player
     * @param obj2          The obstacle being collided with
     * @param userData1     Information about the part of the player colliding
     * @param userData2     Information about the obstacle being collided with
     */
    private void touchedGround(PlayerModel obj1, Obstacle obj2, Object userData1, Object userData2){
        if (obj1.getSensorName().equals(userData1)) {
            obj1.setGrounded(true);
            if(userData2==null)
                userData2 = obj2;
            sensorObjects.add(userData2); // Could have more than one ground
        }
    }

    /**
     *  This function updates the state as if the player left the ground
     * @param obj1          The player
     * @param obj2          The obstacle being collided with
     * @param userData1     Information about the part of the player that was colliding
     * @param userData2     Information about the obstacle that had been collided with
     */
    private void leftGround(PlayerModel obj1, Obstacle obj2, Object userData1, Object userData2){
        if (obj1.getSensorName().equals(userData1)) {
            if(userData2==null)
                userData2 = obj2;
            sensorObjects.remove(userData2);
            if (sensorObjects.size == 0) {
                obj1.setGrounded(false);
                obj1.setCanDoubleJump(true);
            }
        }
    }
    // END: helper functions

    // BEGIN: Simple Collision handlers
    private void handleCollision(PlayerModel obj1, EnemyModel obj2){}
    private void handleCollision(PlayerModel obj1, GoalModel obj2){}
    private void handleCollision(PlayerModel obj1, PlatformModel obj2, Object userData1, Object userData2){
        touchedGround(obj1,obj2,userData1,userData2);
    }
    private void handleCollision(PlayerModel obj1, WallModel obj2){
        obj1.setKnockedBack(0);
    }
    private void handleCollision(PlayerModel obj1, PaintballModel obj2, Object userData1, Object userData2) {
        float sign = obj2.getVX() / Math.abs(obj2.getVX());
        if(obj1.getY()-obj1.getHeight()/2f>=obj2.getY()){
            touchedGround(obj1, obj2, userData1, userData2);
            obj1.setRidingVX(obj2);
        }
        else{
            obj1.setKnockedBack(0);
            if(!obj2.isDying() && obj1.getX()*sign>obj2.getX()*sign+obj2.getHeadSize()*-sign+(sign>0?obj2.getWidth()/2f:0))
                obj1.setKnockedBack(sign);
        }
    }
    private void handleCollision(EnemyModel obj1, PaintballModel obj2, Object userData1){
        obj2.markRemoved(true);
        obj1.setStunned(true);
    }
    private void handleCollision(EnemyModel obj1, PlatformModel obj2, Object userData1){}
    private void handleCollision(GoalModel obj1, PaintballModel obj2){
        hud.setWin(true);
        obj2.markRemoved(true);
    }
    private void handleCollision(PaintballModel obj1, PaintballModel obj2){
        if(obj1.isDead() || obj2.isDead())
            return;

        float oneSign = obj1.getVX() / Math.abs(obj1.getVX());
        float twoSign = obj2.getVX() / Math.abs(obj2.getVX());
        if(oneSign == twoSign) {
            if(obj1.getPosition().x*oneSign<obj2.getPosition().x*oneSign) {
                obj2.setTimeToDie(0);
            } else {
                obj1.setTimeToDie(0);
            }
            return;
        }

        if(obj2.isDying())
            obj1.setTimeToDie(obj2.getTimeToDie());
        else
            obj1.setTimeToDie(obj1.getPaintballToPaintballDuration());

        if(obj1.isDying())
            obj2.setTimeToDie(obj1.getTimeToDie());
        else
            obj2.setTimeToDie(obj2.getPaintballToPaintballDuration());
        obj1.fixX(0f);
        obj2.fixX(0f);
    }
    private void handleCollision(PlatformModel obj1, PaintballModel obj2){
        obj2.setTimeToDie(obj2.getPaintballToWallDuration());
        obj2.fixX(0f);
    }
    private void handleCollision(WallModel obj1, PaintballModel obj2){
        obj2.setTimeToDie(obj2.getPaintballToPlatformDuration());
        obj2.fixX(0f);
    }
    private void handleCollision(PlayerModel obj1, AmmoDepotModel obj2) {
        obj2.setUsed(true);
        hud.addAmmo(obj2.getAmmoAmount());
    }

    // Collision end handlers
    private void handleEndCollision(PlayerModel obj1,WallModel obj2){ }
    private void handleEndCollision(PlayerModel obj1,PlatformModel obj2, Object userData1, Object userData2){
        leftGround(obj1,obj2,userData1,userData2);
    }
    private void handleEndCollision(PlayerModel obj1,PaintballModel obj2, Object userData1, Object userData2){
        leftGround(obj1,obj2,userData1,userData2);
        obj1.setRidingVX(null);
    }
    private void handleEndCollision(EnemyModel obj1, PaintballModel obj2, Object userData1){}
    private void handleEndCollision(EnemyModel obj1, PlatformModel obj2, Object userData1){}
    // END: Simple Collision handlers

    /**
     * Processes a collision between two objects
     * @param obj1          The first obstacle colliding
     * @param obj2          The second obstacle colliding
     * @param userData1     The user data of the first obstacle's fixture
     * @param userData2     The user data for the second obstacle's fixture
     * @param fix1          The first fixture colliding
     * @param fix2          The second fixture colliding
     */
    private void processCollision(Obstacle obj1, Obstacle obj2, Object userData1, Object userData2,Fixture fix1, Fixture fix2){
        if (obj1.getName().equals("player")) {
            if (obj2.getName().equals("enemy"))
                handleCollision((PlayerModel)obj1, (EnemyModel) obj2);
            else if (obj2.getName().equals("goal"))
                handleCollision((PlayerModel)obj1,(GoalModel) obj2);
            else if (obj2.getName().equals("platform"))
                handleCollision((PlayerModel)obj1,(PlatformModel) obj2, userData1, fix2);
            else if (obj2.getName().equals("wall"))
                handleCollision((PlayerModel)obj1,(WallModel) obj2);
            else if (obj2.getName().equals("paintball"))
                handleCollision((PlayerModel)obj1,(PaintballModel) obj2, userData1,fix2);
            else if (obj2.getName().equals("ammoDepot"))
                handleCollision((PlayerModel)obj1, (AmmoDepotModel) obj2);
        }
        else if (obj1.getName().equals("paintball")) {
            if (obj2.getName().equals("enemy"))
                handleCollision((EnemyModel)obj2, (PaintballModel) obj1, userData2);
            else if (obj2.getName().equals("goal"))
                handleCollision((GoalModel)obj2,(PaintballModel) obj1);
            else if (obj2.getName().equals("platform"))
                handleCollision((PlatformModel)obj2,(PaintballModel) obj1);
            else if (obj2.getName().equals("wall"))
                handleCollision((WallModel)obj2,(PaintballModel) obj1);
            else if (obj2.getName().equals("paintball"))
                handleCollision((PaintballModel)obj2,(PaintballModel) obj1);
        }else if (obj1.getName().equals("enemy")) {
            if (obj2.getName().equals("paintball"))
                handleCollision((EnemyModel)obj1, (PaintballModel) obj2, userData1);
            else if (obj2.getName().equals("platform"))
                handleCollision((EnemyModel)obj1, (PlatformModel) obj2, userData1);
        }
    }

    /**
     * Processes the end of a collision between two objects
     * @param obj1          The first obstacle colliding
     * @param obj2          The second obstacle colliding
     * @param userData1     The user data of the first obstacle's fixture
     * @param userData2     The user data for the second obstacle's fixture
     * @param fix1          The first fixture colliding
     * @param fix2          The second fixture colliding
     */
    private void processEndCollision(Obstacle obj1, Obstacle obj2, Object userData1, Object userData2,Fixture fix1, Fixture fix2){
        if (obj1.getName().equals("player")) {
            if (obj2.getName().equals("platform"))
                handleEndCollision((PlayerModel)obj1,(PlatformModel) obj2, userData1, fix2);
            else if (obj2.getName().equals("wall"))
                handleEndCollision((PlayerModel)obj1,(WallModel) obj2);
            else if (obj2.getName().equals("paintball"))
                handleEndCollision((PlayerModel)obj1,(PaintballModel) obj2, userData1,fix2);
        }else if (obj1.getName().equals("enemy")) {
            if (obj2.getName().equals("paintball"))
                handleEndCollision((EnemyModel)obj1, (PaintballModel) obj2, userData1);
            else if (obj2.getName().equals("platform"))
                handleEndCollision((EnemyModel)obj1, (PlatformModel) obj2, userData1);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle) body1.getUserData();
            Obstacle bd2 = (Obstacle) body2.getUserData();

            processCollision(bd1, bd2, fd1, fd2,fix1,fix2);
            processCollision(bd2, bd1, fd2, fd1,fix2,fix1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Object fd1 = fix1.getUserData();
        Object fd2 = fix2.getUserData();

        try {
            Obstacle bd1 = (Obstacle) body1.getUserData();
            Obstacle bd2 = (Obstacle) body2.getUserData();

            processEndCollision(bd1, bd2, fd1, fd2,fix1,fix2);
            processEndCollision(bd2, bd1, fd2, fd1,fix2,fix1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
