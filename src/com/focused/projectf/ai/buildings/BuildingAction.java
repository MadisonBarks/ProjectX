package com.focused.projectf.ai.buildings;

import com.focused.projectf.graphics.Image;


public abstract class BuildingAction {
	public abstract void begin();
	public abstract void cancel();

	
	public abstract Image getIconImage();
	public String getCornerText() { return null; }
	
	/**
	 * @return true when action is complete and the next action can be begun.
	 * This action will be removed for the queue immediatly afterwards
	 */
	public abstract boolean update();
	/**
	 * @return a float from 0 and 1 representing how complete the current action is.
	 */
	public abstract float getProgress();
}
