package com.focused.projectf.ai;

import com.focused.projectf.entities.Unit;
import com.focused.projectf.resources.images.FlareUnitAnimation;

public abstract class Action<T extends Unit> {

	public float pauseTime;

	public abstract boolean holdPosition();
	
	protected final T Unit;
	public Action(T unit) {
		Unit = unit;
	}
	public abstract void startAction();
	public void stopAction() { }
	public abstract void updateUnit(float elapsed);
	public void secondThreadUpdate() { }
	public void draw(Unit unit) { }
	
	/** 
	 * Makes the unit stop performing this action for a given time. 
	 */
	public void pause(float time) { pauseTime += time; }
	public boolean isPaused() { return pauseTime > 0; }
	public abstract void setState(FlareUnitAnimation img);
}