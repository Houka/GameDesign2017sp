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

		// Process the FSM
		changeStateIfApplicable();

		int action = CONTROL_NO_ACTION;

		// If we're attacking someone and we can shoot him now, then do so.
		if (state == FSMState.ATTACK && canShootTarget()) {
			action = CONTROL_FIRE;
		}

		// Increment the number of ticks.
		ticks++;

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
			state = FSMState.WAIT;

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
}