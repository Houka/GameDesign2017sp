package edu.cornell.gdiac.game.entity.controllers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import edu.cornell.gdiac.game.entity.factories.PaintballFactory;
import edu.cornell.gdiac.game.entity.models.*;
import edu.cornell.gdiac.util.PooledList;
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
    /** paintball factory for splatterers */
    private PaintballFactory paintballFactory;

    private PooledList<PaintballModel> objectsToAdd;


    /**
     *  The contructor
     * @param hud   The HUD to update
     */
    public CollisionController(HUDModel hud,PaintballFactory paintballFactory){
        this.hud = hud;
        this.paintballFactory = paintballFactory;
        this.objectsToAdd = new PooledList<PaintballModel>();
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
            obj1.setJumpForce(obj1.getPlayerJump());
            if(!(obj2 instanceof PaintballModel) ||
                    (obj2 instanceof PaintballModel && !((PaintballModel) obj2).getPaintballType().equals("trampolineComb"))) {
                obj1.setTrampGrounded(false);
            }
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
    private void handleCollision(PlayerModel obj1, SplattererModel obj2) {}
    private void handleCollision(PlayerModel obj1, EnemyModel obj2){
        hud.setLose(true);}
    private void handleCollision(PlayerModel obj1, GoalModel obj2){}
    private void handleCollision(PlayerModel obj1, PlatformModel obj2, Object userData1, Object userData2){
        touchedGround(obj1,obj2,userData1,userData2);
        obj1.setTrampGrounded(false);
        if (obj2.getType() == PlatformModel.SPIKE_PLATFORM) {hud.setLose(true);}
    }
    private void handleCollision(PlayerModel obj1, WallModel obj2){
        obj1.setKnockedBack(0);
    }
    private void handleCollision(PlayerModel obj1, PaintballModel obj2, Object userData1, Object userData2) {
        float sign = obj2.getVX() / Math.abs(obj2.getVX());
        if(obj1.getY()-obj1.getHeight()/2f>=obj2.getY() && !obj1.isGhosting()){
            touchedGround(obj1, obj2, userData1, userData2);
            obj1.setRidingVX(obj2);
        }
        else{
            obj1.setKnockedBack(0);
            if(!obj2.isPlayerBullet() && !obj2.isDying() && obj1.getX()*sign>obj2.getX()*sign+obj2.getHeadSize()*-sign+(sign>0?obj2.getWidth()/2f:0))
                obj1.setKnockedBack(sign);
        }
        if(obj2.getPaintballType().equals("trampolineComb")) {
            if(obj1.isGrounded() && !obj1.isJumping() && !obj1.isDoubleJumping()) {
                obj1.setMyPlatform(obj2);
                obj1.setTrampGrounded(true);
                obj2.setUsed(true);
            }
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
        if(obj1.recentlyCreated())
            obj1.instakill();
        if(obj2.recentlyCreated())
            obj2.instakill();

        if(obj1.isDead() || obj2.isDead() || obj1.getRecentCollision() || obj2.getRecentCollision())
            return;

        float newWidth = obj1.getWidth()+obj2.getWidth();
        float midPoint;
        if(obj1.getX()<obj2.getX())
            midPoint =obj1.getX()-obj1.getWidth()/2f + newWidth/2f;
        else
            midPoint =obj2.getX()-obj2.getWidth()/2f + newWidth/2f;

        float oneSign = obj1.getVX() / Math.abs(obj1.getVX());
        float twoSign = obj2.getVX() / Math.abs(obj2.getVX());
        if(oneSign == twoSign) {
            if(obj1.getPosition().x*oneSign<obj2.getPosition().x*oneSign) {
                obj1.pop();
            }
            return;
        }

        PaintballModel survives;
        PaintballModel dies;
        if(obj2.isDying()) {
            survives = obj2;
            dies = obj1;
        }
        else if(obj1.isDying()) {
            survives = obj1;
            dies = obj2;
        } else {
            if(obj1.isPlayerBullet()) {
                survives = obj2;
                dies = obj1;
            } else {
                survives = obj1;
                dies = obj2;
            }

            if(obj1.getPaintballType().equals("trampoline") || obj2.getPaintballType().equals("trampoline")) {
               survives.setPaintballType("trampolineComb");
            }
          
            survives.setTimeToDie(obj1.getPaintballToPaintballDuration());
            dies.instakill();
        }
      
        dies.pop();
        if(!obj1.isPlayerBullet() && !obj2.isPlayerBullet()&&!obj1.isDying()&&!obj2.isDying())
            survives.newSize(midPoint,obj2.getPosition().y,obj1.getWidth()+obj2.getWidth());
        survives.setPassThrough(true);

        obj1.fixX(0f);
        obj2.fixX(0f);
        obj1.markRecentCollision();
        obj2.markRecentCollision();
    }
    private void handleCollision(PlatformModel obj1, PaintballModel obj2){
        if(obj2.recentlyCreated())
            obj2.pop();

        obj2.setTimeToDie(obj2.getPaintballToWallDuration());
        obj2.fixX(0f);
    }
    private void handleCollision(WallModel obj1, PaintballModel obj2){
        if(obj2.recentlyCreated())
            obj2.pop();

        obj2.setTimeToDie(obj2.getPaintballToPlatformDuration());
        obj2.fixX(0f);
    }
    private void handleCollision(PlayerModel obj1, AmmoDepotModel obj2) {
        if (!obj2.isUsed()) {
            obj2.setUsed(true);
            hud.addAmmo(obj2.getAmmoAmount());
        }
    }
    private void handleCollision(SplattererModel obj1, PaintballModel obj2) {
        boolean dir = false;
        if(obj2.getX() < obj1.getX()) {
            dir = true;
        }
        if(!obj1.isUsed()) {
            obj2.instakill();

            obj1.setUsed(true);
            obj1.setShot(true);
            obj1.setDir(dir);
            obj1.setYCoord(obj2.getY());
        }
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
            else if (obj2.getName().equals("splatterer")) {
                handleCollision((PlayerModel) obj1, (SplattererModel) obj2);
            }
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
            else if (obj2.getName().equals("splatterer"))
                handleCollision((SplattererModel)obj2,(PaintballModel) obj1);
        }
        else if (obj1.getName().equals("enemy")) {
            if (obj2.getName().equals("paintball"))
                handleCollision((EnemyModel)obj1, (PaintballModel) obj2, userData1);
            else if (obj2.getName().equals("platform"))
                handleCollision((EnemyModel)obj1, (PlatformModel) obj2, userData1);
        }
    }

    public PooledList<PaintballModel> getObjsToAdd() {
        return objectsToAdd;
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
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        try {
            Obstacle bd1 = (Obstacle) body1.getUserData();
            Obstacle bd2 = (Obstacle) body2.getUserData();

            PlayerModel player = null;
            PaintballModel paintball = null;

            if(bd2.getName().equals("paintball")) {
                paintball = (PaintballModel) bd2;
                if(paintball.isDead()) {
                    contact.setEnabled(false);
                    return;
                }
            } else if (bd1.getName().equals("paintball")) {
                paintball = (PaintballModel) bd1;
                if(paintball.isDead()) {
                    contact.setEnabled(false);
                    return;
                }
            }


            if(bd1.getName().equals("player")) {
                player = (PlayerModel) bd1;
            } else if (bd2.getName().equals("player")) {
                player = (PlayerModel) bd2;
            }

            if (paintball == null ||  player == null){
                return;
            }

            if (player.isGhosting()) {
                contact.setEnabled(false);
                return;
            }


            if(player.getVY()>=0 && paintball.canPassThrough()) {
                contact.setEnabled(false);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        try {
            Obstacle bd1 = (Obstacle) body1.getUserData();
            Obstacle bd2 = (Obstacle) body2.getUserData();

            PlayerModel player;
            PaintballModel paintball;

            if(bd1.getName().equals("player") && bd2.getName().equals("paintball")) {
                player = (PlayerModel) bd1;
                paintball = (PaintballModel) bd2;
            } else if (bd2.getName().equals("player") && bd1.getName().equals("paintball")) {
                player = (PlayerModel) bd2;
                paintball = (PaintballModel) bd1;
            } else {
                return;
            }

            if (player.isGhosting()) {
                contact.setEnabled(false);
                return;
            }

            if(player.getVY()>=0 && paintball.isPlayerBullet()) {
                contact.setEnabled(false);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}