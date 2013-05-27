package com.focused.projectf.gui;

import org.lwjgl.opengl.Display;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;

public class GUIRenderer extends GUIGroup {

	public GUIView Focused;

	public GUIRenderer() {
		super(null, new Point(0, 0), Display.getWidth(), Display.getHeight());
		Layout();
	}

	public Rect getViewRegion() { return new Rect(0, 0, Display.getWidth(), Display.getHeight()); }

	public void draw(float elapsed) {
		super.draw(elapsed);
	}

	public boolean giveFocusTo(GUIView view) {
		if(view.getGUIManager() != this)
			throw new Error("View requested focus from the wrong GUIManager. Somethings not working correctly");

		if(view.canTakeFocus()) {
			view.onFocusGained();
			if(Focused != null && Focused != view)
				Focused.onFocusLost(view);
			Focused = view;
		}

		return Focused == view;
	}

	public boolean onMouseEvent(MouseEvent event) {
		return super.onMouseEvent(event);
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
		if(Focused != null)
			Focused.onKeyEvent(event);
	}

	public void removeAllViews() {
		Children.clear();
	}

	public void clearFocus() {
		if(Focused != null)
			Focused.onFocusLost(null);
		Focused = null;
	}
	/** Dumps all elements of this GUIRenderer and it's children. */
	public void empty() {
		Children.clear();
	}
	
	public Rect Layout() {
		widthParam = Display.getWidth();
		heightParam = Display.getHeight();
		Rect ret = super.Layout();
		if(Children != null)
			for(GUIView view : Children)
				view.Layout();
		return ret;
	}
}
