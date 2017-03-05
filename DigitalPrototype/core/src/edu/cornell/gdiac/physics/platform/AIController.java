/*
 * AIController.java
 *
 * Reimplementation of the below AIController by Ashton Cooper 3/2/17
 *
 * Author: Walker M. White, Cristian Zaloj
 * Based on original AI Game Lab by Yi Xu and Don Holden, 2007
 * LibGDX version, 1/24/2015
 */
package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.InputController;

import java.util.*;

/**
 * InputController corresponding to AI control.
 *
 *
 */
public class AIController {
	/**
	 * Enumeration to encode the finite state machine.
	 */
	private static enum FSMState {
		/** The enemy just spawned */
		SPAWN,
		/** The ship is patrolling around without a target */
		WAIT,
		/** The ship has a target and is attacking it */
		ATTACK
	}

	/** The singleton instance of the input controller */
	private static AIController theController = null;

	/**
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static AIController getInstance() {
		return theController;
	}

	// Constants for the control codes
	// We would normally use an enum here, but Java enums do not bitmask nicely
	/** Do not do anything */
	public static final int CONTROL_NO_ACTION  = 0x00;
	/** Fire the ship weapon */
	public static final int CONTROL_FIRE 	   = 0x10;

	// Constants

	// Instance Attributes
	/** The enemy being controlled by this AIController */
	private EnemyModel enemy;
	/** The enemy interval (null if type is onSight) */
	private int interval = 0;
	/** The ship's current state in the FSM */
	private FSMState state;
	/** The target dude (to chase or attack). */
	private DudeModel target;
	/** Whether the enemy is firing. */
	private int move; // A ControlCode
	/** The number of ticks since we started this controller */
	private long ticks;

	// Custom fields for AI algorithms
	/** The number of ticks that a specific target has been selected */
	//private long targetTicks;
	/** The maximum number of ticks that a specific target can be selected */
	//private long maxTicks = 100;
	//private int randomFour;

	/**
	 * Creates an AIController for the ship with the given id.
	 *
	 * @param enemy The enemy being added
	 * @param avatar The dudeModel being targeted
	 * @param interval The interval that the enemy shoots on
	 */
	public AIController(EnemyModel enemy, DudeModel avatar, int interval) {
		this.enemy = enemy;
		if (!this.enemy.getOnSight()) {
			this.interval = interval;
		}
		state = FSMState.SPAWN;
		move  = CONTROL_NO_ACTION;
		ticks = 0;

		// Select an initial target
		target = avatar;
		theController = this;
	}

	/**
	 * Returns the action selected by this AIController
	 *
	 * The returned int is a bit-vector of more than one possible input
	 * option. This is why we do not use an enumeration of Control Codes;
	 * Java does not (nicely) provide bitwise operation support for enums.
	 *
	 * This function tests the environment and uses the FSM to chose the next
	 * action of the ship. This function SHOULD NOT need to be modified.  It
	 * just contains code that drives the functions that you need to implement.
	 *
	 * @return the action selected by this InputController
	 */
	public int getAction() {
		// Increment the number of ticks.
		ticks++;

		// Process the FSM
		changeStateIfApplicable();

		int action = CONTROL_NO_ACTION;

		// If we're attacking someone and we can shoot him now, then do so.
		if (state == FSMState.ATTACK && canShootTarget()) {
			action = CONTROL_FIRE;
		}

		return action;
	}

	// FSM Code for Targeting (MODIFY ALL THE FOLLOWING METHODS)

	/**
	 * Change the state of the ship.
	 *
	 * A Finite State Machine (FSM) is just a collection of rules that,
	 * given a current state, and given certain observations about the
	 * environment, chooses a new state. For example, if we are currently
	 * in the ATTACK state, we may want to switch to the CHASE state if the
	 * target gets out of range.
	 */
	private void changeStateIfApplicable() {

		// Next state depends on current state.
		switch (state) {
		case SPAWN:
			// Insert checks and spawning-to-??? transition code here
			if (ticks > 2) {
				state = FSMState.WAIT;
			}
			break;

		case WAIT:
			// Insert checks and moving-to-??? transition code here
			if (!this.enemy.getOnSight()) {
				state = FSMState.ATTACK;
			}
			else if (this.target.getY()+1 >= this.enemy.getY() && this.target.getY()-1 <= this.enemy.getY() &&
					((this.target.getX() < this.enemy.getX() && !this.enemy.isFacingRight()) || (this.target.getX() > this.enemy.getX() && this.enemy.isFacingRight()))) {
				state = FSMState.ATTACK;
			}
			break;

		case ATTACK:
			// insert checks and attacking-to-??? transition code here
			if (this.enemy.getOnSight() &&
					(this.target.getY()+1 <= this.enemy.getY() || this.target.getY()-1 >= this.enemy.getY())) {
				state = FSMState.WAIT;
			}
			break;

		default:
			// Unknown or unhandled state, should never get here
			assert (false);
			state = FSMState.WAIT; // If debugging is off
			break;
		}
	}

	/**
	 * Acquire a target to attack (and put it in field target).
	 *
	 * Insert your checking and target selection code here. Note that this
	 * code does not need to reassign <c>target</c> every single time it is
	 * called. Like all other methods, make sure it works with any number
	 * of players (between 0 and 32 players will be checked). Also, it is a
	 * good idea to make sure the ship does not target itself or an
	 * already-fallen (e.g. inactive) ship.
	 */
	/*private void selectTarget() {
		//#region PUT YOUR CODE HERE
		Ship newTarget;
		float targetDist;
		if (target == null || !target.isActive() || !target.isAlive()) {
			//No current viable target so search
			Iterator<Ship> targetIterator = fleet.iterator();
			//Initiate Values
			if (fleet.get(0) != this.ship) {
				newTarget = fleet.get(0);
				targetDist = (Math.abs(fleet.get(0).getX() - this.ship.getX()) +
						Math.abs(fleet.get(0).getY() - this.ship.getY()));
			}
			else {
				newTarget = null;
				targetDist = 5000f;
			}
			for (Ship s : fleet) {
				if (s != this.ship) {
					if ((Math.abs(s.getX() - this.ship.getX()) +
							Math.abs(s.getY() - this.ship.getY())) < targetDist) {
						newTarget = s;
						targetDist = (Math.abs(newTarget.getX() - this.ship.getX()) +
								Math.abs(newTarget.getY() - this.ship.getY()));
					}
				}
			}
			target = newTarget;
			targetTicks = 1;
		}
		if (targetTicks > maxTicks) {
			//Target has been selected for too long
			Iterator<Ship> targetIterator = fleet.iterator();
			newTarget = null;
			targetDist = 5000f;
			for (Ship s : fleet) {
				if (s != this.ship && s != target) {
					if ((Math.abs(s.getX() - this.ship.getX()) +
							Math.abs(s.getY() - this.ship.getY())) < targetDist) {
						newTarget = s;
						targetDist = (Math.abs(newTarget.getX() - this.ship.getX()) +
								Math.abs(newTarget.getY() - this.ship.getY()));
					}
				}
			}
			target = newTarget;
			targetTicks = 1;
		}
		else {
			targetTicks++;
		}
		//#endregion
	}*/

	/**
	 * Returns true if we can hit a target from here.
	 *
	 * Insert code to return true if a shot fired from the given (x,y) would
	 * be likely to hit the target. We can hit a target if it is in a straight
	 * line from this tile and within attack range. The implementation must take
	 * into consideration whether or not the source tile is a Power Tile.
	 *
	 * @param x The x-index of the source tile
	 * @param y The y-index of the source tile
	 *
	 * @return true if we can hit a target from here.
	 */
	/*private boolean canShootTargetFrom(int x, int y) {
		//#region PUT YOUR CODE HERE
		if (this.target == null) return false;
		if (!board.isSafeAt(x,y)) return false;
		Vector2 targetPos = this.target.getPosition();
		if ((board.screenToBoard(targetPos.x) <= x+ATTACK_DIST && board.screenToBoard(targetPos.x) >= x-ATTACK_DIST && board.screenToBoard(targetPos.y) == y) ||
				(board.screenToBoard(targetPos.y) <= y+ATTACK_DIST && board.screenToBoard(targetPos.y) >= y-ATTACK_DIST && board.screenToBoard(targetPos.x) == x))
			return true;

		else if (board.isPowerTileAt(x,y)) {
			int diag = (x<y)?(y-x):(x-y);
			int targetDiag = board.screenToBoard((targetPos.x<targetPos.y)?(targetPos.y - targetPos.x):(targetPos.x - targetPos.y));
			if ((board.screenToBoard(targetPos.x) <= x+ATTACK_DIST && board.screenToBoard(targetPos.x) >= x-ATTACK_DIST) &&
					(board.screenToBoard(targetPos.y) <= y+ATTACK_DIST && board.screenToBoard(targetPos.y) >= y-ATTACK_DIST) && (targetDiag == diag || (targetDiag - diag == 0)))
				return true;
		}
		return false;
		//#endregion
	}*/

	/**
	 * Returns true if we can both fire and hit our target
	 *
	 * If we can fire now, and we could hit the target from where we are,
	 * we should hit the target now.
	 *
	 * @return true if we can both fire and hit our target
	 */
	private boolean canShootTarget() {
		if (!this.enemy.getOnSight() && this.ticks%interval==0) {
			return true;
		}
		else if (this.enemy.getOnSight()) {
			return true;
		}
		return false;
	}

	// Pathfinding Code (MODIFY ALL THE FOLLOWING METHODS)

	/**
	 * Mark all desirable tiles to move to.
	 *
	 * This method implements pathfinding through the use of goal tiles.
	 * It searches for all desirable tiles to move to (there may be more than
	 * one), and marks each one as a goal. Then, the pathfinding method
	 * getMoveAlongPathToGoalTile() moves the ship towards the closest one.
	 *
	 * POSTCONDITION: There is guaranteed to be at least one goal tile
     * when completed.
     */
	/*private void markGoalTiles() {
		// Clear out previous pathfinding data.
		board.clearMarks();
		boolean setGoal = false; // Until we find a goal

		// Add initialization code as necessary
		//#region PUT YOUR CODE HERE

		//#endregion

		switch (state) {
		case SPAWN: // Do not pre-empt with FSMState in a case
			// insert code here to mark tiles (if any) that spawning ships
			// want to go to, and set setGoal to true if we marked any.
			// Ships in the spawning state will immediately move to another
			// state, so there is no need for goal tiles here.
			//#region PUT YOUR CODE HERE

			//No code needed
			//#endregion
			break;

		case WANDER: // Do not pre-empt with FSMState in a case
			// Insert code to mark tiles that will cause us to move around;
			// set setGoal to true if we marked any tiles.
			// NOTE: this case must work even if the ship has no target
			// (and changeStateIfApplicable should make sure we are never
			// in a state that won't work at the time)

			//#region PUT YOUR CODE HERE
			if (randomFour == 1) {
				Vector2 possibleGoal = new Vector2(board.screenToBoard(this.ship.getX())+5, board.screenToBoard(this.ship.getY()));
				if (board.isSafeAt((int)possibleGoal.x, (int)possibleGoal.y)) {
					board.setGoal((int)possibleGoal.x, (int)possibleGoal.y);
					setGoal = true;
				}
			}
			else if (randomFour == 2) {
				Vector2 possibleGoal = new Vector2(board.screenToBoard(this.ship.getX())-5, board.screenToBoard(this.ship.getY()));
				if (board.isSafeAt((int)possibleGoal.x, (int)possibleGoal.y)) {
					board.setGoal((int)possibleGoal.x, (int) possibleGoal.y);
					setGoal = true;
				}
			}
			else if (randomFour == 3) {
				Vector2 possibleGoal = new Vector2(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY())+5);
				if (board.isSafeAt((int)possibleGoal.x, (int)possibleGoal.y)) {
					board.setGoal((int)possibleGoal.x, (int) possibleGoal.y);
					setGoal = true;
				}
			}
			else if (randomFour == 4) {
				Vector2 possibleGoal = new Vector2(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY())-5);
				if (board.isSafeAt((int)possibleGoal.x, (int) possibleGoal.y)) {
					board.setGoal((int)possibleGoal.x, (int)possibleGoal.y);
					setGoal = true;
				}
			}
			//#endregion
			break;

		case CHASE: // Do not pre-empt with FSMState in a case
			// Insert code to mark tiles that will cause us to chase the target;
			// set setGoal to true if we marked any tiles.

			//#region PUT YOUR CODE HERE
			board.setGoal(board.screenToBoard(this.target.getX()), board.screenToBoard(this.target.getY()));
			setGoal = true;
			//#endregion
			break;

		case ATTACK: // Do not pre-empt with FSMState in a case
			// Insert code here to mark tiles we can attack from, (see
			// canShootTargetFrom); set setGoal to true if we marked any tiles.

			//#region PUT YOUR CODE HERE
			Vector2 newTile = this.ship.getPosition();
			float targetDist = (Math.abs(this.ship.getX()-this.target.getX()
					+ Math.abs(this.ship.getY()-this.target.getY())));
			Vector2 up = new Vector2(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY())-1);
			Vector2 down = new Vector2(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY())+1);
			Vector2 left = new Vector2(board.screenToBoard(this.ship.getX())-1, board.screenToBoard(this.ship.getY()));
			Vector2 right = new Vector2(board.screenToBoard(this.ship.getX())+1, board.screenToBoard(this.ship.getY()));
			Vector2[] options = {up, down, left, right};
			for (Vector2 option: options) {
				float newDist = (Math.abs(option.x - this.target.getX()) + Math.abs(option.y - this.target.getY()));
				if (newDist < targetDist) {
					if (board.isSafeAt(board.screenToBoard(option.x), board.screenToBoard(option.y))) {
						newTile = option;
						targetDist = newDist;
					}
				}
				if (newDist == targetDist && board.isPowerTileAtScreen(option.x, option.y)) {
					if (board.isSafeAt(board.screenToBoard(option.x), board.screenToBoard(option.y))) newTile = option;
				}
			}
			board.setGoal(board.screenToBoard(newTile.x), board.screenToBoard(newTile.y));
			setGoal = true;
			//#endregion
			break;
		}

		// If we have no goals, mark current position as a goal
		// so we do not spend time looking for nothing:
		if (!setGoal) {
			int sx = board.screenToBoard(ship.getX());
			int sy = board.screenToBoard(ship.getY());
			board.setGoal(sx, sy);
		}
	}*/

	/**
 	 * Returns a movement direction that moves towards a goal tile.
 	 *
 	 * This is one of the longest parts of the assignment. Implement
	 * breadth-first search (from 2110) to find the best goal tile
	 * to move to. However, just return the movement direction for
	 * the next step, not the entire path.
	 *
	 * The value returned should be a control code.  See PlayerController
	 * for more information on how to use control codes.
	 *
 	 * @return a movement direction that moves towards a goal tile.
 	 */
	/*private int getMoveAlongPathToGoalTile() {
		*//**BFS Algorithm implementation roughly cited from
		http://www.geeksforgeeks.org/breadth-first-traversal-for-a-graph/ *//*
		//#region PUT YOUR CODE HERE
		if (board.isGoal(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY())))
			return CONTROL_NO_ACTION;
		LinkedList<Vector2> queue = new LinkedList<Vector2>();
		Vector2[][] order = new Vector2[board.getHeight()][board.getWidth()];
		board.setVisited(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY()));
		Vector2 root = new Vector2(board.screenToBoard(this.ship.getX()), board.screenToBoard(this.ship.getY()));
		boolean foundGoal = false;
		Vector2 goalTile = null;
		queue.add(root);
		order[(int)root.x][(int)root.y] = null;
		while (queue.size() != 0 && foundGoal == false) {
			Vector2 a = queue.poll();

			Vector2 up = new Vector2((int)a.x, (int)a.y - 1);
			Vector2 down = new Vector2((int)a.x, (int)a.y + 1);
			Vector2 left = new Vector2((int)a.x -1, (int)a.y);
			Vector2 right = new Vector2((int)a.x + 1, (int)a.y);
			Vector2[] options = {up, down, left, right};
			for (Vector2 option : options) {
				if (!board.isVisited((int)option.x, (int)option.y)
						&& board.inBounds((int)option.x, (int)option.y)
						&& board.isSafeAt((int)option.x, (int)option.y)) {
					board.setVisited((int) option.x, (int) option.y);
					queue.add(option);
					order[(int)option.x][(int)option.y] = a;
				}
				else if (board.isGoal((int) option.x, (int)option.y) && board.isSafeAt((int) option.x, (int) option.y)) {
					foundGoal = true;
					goalTile = option;
				}
			}
		}
		boolean foundRoot = false;
		Vector2 prev = goalTile;
		if (prev == null) {
			return CONTROL_NO_ACTION;
		}
		while (foundRoot == false) {
			Vector2 newPrev = order[(int)prev.x][(int)prev.y];
			if ((newPrev.x == root.x) && (newPrev.y == root.y)) {
				foundRoot = true;
			} else {
				prev = newPrev;
			}

		}
		if ((prev.x - root.x) > 0) {  return CONTROL_MOVE_RIGHT;  }
		else if ((prev.x - root.x) < 0) { return CONTROL_MOVE_LEFT;  }
		else if ((prev.y - root.y) > 0) {  return CONTROL_MOVE_DOWN; }
		else if ((prev.y - root.y) < 0) { return CONTROL_MOVE_UP;  }
		else { return CONTROL_NO_ACTION;  }
		//#endregion
	}*/
}