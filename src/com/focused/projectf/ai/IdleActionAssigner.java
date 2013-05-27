package com.focused.projectf.ai;

import com.focused.projectf.Map;
import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.ai.actions.attack.AttackAction;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.players.Player.DiplomacyState;

/**
 * Assigns units not executing an action assigned by the user to certain actions.
 * Like attack enemy units and buildings that are nearby.  
 */
public final class IdleActionAssigner implements Runnable {

	protected static final int RUNNING		= 0;
	protected static final int PAUSED		= 1;
	protected static final int STOPPED		= 2;

	protected static Thread currentThread;
	protected static int State = STOPPED;

	public static int sleepMS = 1500;
	
	public static void begin() {
		State = RUNNING;
		if(currentThread == null) {
			currentThread = new Thread(new IdleActionAssigner());
			currentThread.start();
		}
	}
	/**
	 * pauses the auto task assigner thread. This should be called whenever 
	 * the game pauses reduce CPU consumption. call begin() to resume it.
	 */
	public static void pause() { State = PAUSED; }
	public static void stop() { State = STOPPED; }

	@Override
	public void run() {
		Map map = Map.get();

		do {
			do {
				try { Thread.sleep(sleepMS); } catch(Exception ex) { }
			} while(State == PAUSED);

			for(int i = 0; i < map.getUnits().size(); i++) {
				Unit u = map.getUnits().get(i);
				if(u.getActionStack().isIdle()) {
					final float viewRange = u.getStats().RangeOfSight * Map.tileWidth;
					final float viewRangeSq = viewRange * viewRange;

					switch(u.getType()) {
						default: // just about everything that can attack other things.
							Unit targ = null;
							float distSq = viewRangeSq;
							for(int j = 0; j < map.getUnits().size(); j++) {
								Unit test = map.getUnits().get(j);
								if(test.getOwner().getDiplomacyWith(u.getOwner()) == DiplomacyState.Enemy
										&& test.getPosition().distSq(u.getPosition()) < distSq) {
									targ = test;
									distSq = test.getPosition().distSq(u.getPosition());
								}
							}
							
							if(targ != null) {
								u.getActionStack().add(AttackAction.create(u, targ));
								u.getActionStack().add(new MoveToAction(u, targ));
							}
							
							break;

						case Villager:
							break;
					}
				}
			}
		} while(State != STOPPED);
	}
}
