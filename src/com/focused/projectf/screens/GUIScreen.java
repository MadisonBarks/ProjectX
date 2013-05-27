package com.focused.projectf.screens;

import com.focused.projectf.gui.GUIRenderer;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.interfaces.IInputReciever;

public abstract class GUIScreen extends Screen implements IInputReciever {

	public GUIRenderer GUI;
	private boolean guiBuilt;
	
	public GUIScreen(Screen parent) {
		super(parent);
		GUI = new GUIRenderer();
		Active = false;
	}

	/**
	 * Generates the GUI elements. Called after required resources are loaded.
	 */
	protected abstract void buildGUI();
	public final void build() {
		this.buildGUI();
		guiBuilt = true;
	}
	public void Layout() {
		GUI.Layout();
	}
	
	@Override
	public void draw(float elapsedTime) {
		GUI.draw(elapsedTime);
	}

	@Override
	public void update(float elapsedTime) {
		GUI.update(elapsedTime);
	}

	@Override
	public boolean onKeyEvent(KeyEvent event) {
		GUI.onKeyEvent(event);
		return false;
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		return GUI.onMouseEvent(event);
	}
	
	public String[] getRequiredResources() {
		return new String[] { };
	}
	
	public boolean isReadyToShow() {
		return guiBuilt && super.isReadyToShow();
	}
}