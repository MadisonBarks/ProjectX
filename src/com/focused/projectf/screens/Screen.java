package com.focused.projectf.screens;

import com.focused.projectf.interfaces.IInputReciever;
import com.focused.projectf.resources.Content;


public abstract class Screen implements IInputReciever {
	
	public Screen Parent;
	public boolean Active;
	public Screen(Screen parent) {
		Parent = parent;
		Active = false;
	}
	
	public void close() {
		ScreenManager.remove(this);
	}
	/** return true to force this screen to not be removed when close() is called. Only use this if absolutely necessary */
	public boolean onClose() { return false; }
	public void onFocusLost(Screen hasFocus) { }
	public void onGainFocus(Screen lostFocus) { }
	/** 
	 * Returns all resources required before this screen can be rendered. 
	 * The CMS will load these resources before this screen is rendered
	 */
	public abstract String[] getRequiredResources();
	/**
	 * Returns all the resources that should be queried for background loading after 
	 * this screen is shown for the first time 
	 */
	public String[] getQueryResources() { return null; }
	/** 
	 * return true if screens underneath this one should not be rendered because 
	 * no part of them will be visible.
	 */
	public abstract boolean fillsScreen();
	
	/**
	 * updates all information for rendering the screen. Called ~30 times per seconds
	 * @param elapsedTime
	 * 		the amount of time that has passed since the last update in seconds
	 */
	public abstract void update(float elapsedTime);
	
	/**
	 * rendering the screen. Called as often as possible without interfering with the update cycle
	 * @param elapsedTime
	 * 		the amount of time that has passed since the last draw call in seconds
	 */
	public abstract void draw(float elapsedTime);
	
	public boolean Vissible = true;
	public void setVissible(boolean isVissible) { Vissible = isVissible; }
	
	public boolean isReadyToShow() {
		return Content.areLoaded(getRequiredResources());
	}
	
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
