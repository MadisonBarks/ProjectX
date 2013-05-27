package com.focused.projectf.gui;

import org.lwjgl.opengl.Display;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;

public abstract class GUIView {
	public static final int TOP 		= 0;
	public static final int LEFT 		= 1;
	public static final int BOTTOM 	= 2;
	public static final int RIGHT 		= 3;

	public static final float UNSET		= Float.NaN;

	public float[] Margins = new float[4];
	protected float widthParam, heightParam;

	public GUIGroup Parent;	
	protected Rect region;

	public GUIView(GUIGroup parent, float top, float left, float bottom, float right) {
		this(parent, top, left, bottom, right, UNSET, UNSET);
	}
	public GUIView(GUIGroup parent, Point position, float width, float height) {
		this(parent, new float[] { position.X, position.Y, UNSET, UNSET }, width, height);
	}
	public GUIView(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		this(parent, new float[] { top, left, bottom, right }, width, height);
	}
	public GUIView(GUIGroup parent, float[] margins, float width, float height) {
		Parent = parent;
		if(parent != null)
			parent.addView(this);
		Margins = margins;
		widthParam = width;
		heightParam = height;
		Layout();
	}

	public abstract void draw(float time);
	public abstract boolean onMouseEvent(MouseEvent event);
	public void onKeyEvent(KeyEvent event) { }
	public Rect getViewRegion() { return region; }

	public Rect getMouseEventPickupRect() { return getViewRegion(); }
	
	public void onFocusLost(GUIView whoHasFocusNow) { }
	public void onFocusGained() { }

	public Point getPosition() { return new Point(region.getX(), region.getY()); }
	public float getWidth() { return region.getWidth(); }
	public float getHeight() { return region.getHeight(); }
	public void setSize(int width, int height) { region.setSize(width, height); }
	public Rect Layout() {
		float pw, ph, x = 0, y = 0;
		float width = widthParam;
		float height = heightParam;
		
		if(Parent != null && !(Parent instanceof GUIRenderer)) {
			pw = Parent.getWidth();
			ph = Parent.getHeight();
		} else {
			pw = Display.getWidth();
			ph = Display.getHeight();
		}

		if(Float.isNaN(Margins[TOP])) {
			if(!Float.isNaN(Margins[BOTTOM]))
				y = ph - Margins[BOTTOM] - height;
		} else {
			y = Margins[TOP];
			if(!Float.isNaN(Margins[BOTTOM]))
				height = ph - Margins[BOTTOM] - Margins[TOP];
		}		

		if(Float.isNaN(Margins[LEFT])) {
			if(!Float.isNaN(Margins[RIGHT])) 
				x = pw - Margins[RIGHT] - width;
		} else {
			x = Margins[LEFT];
			if(!Float.isNaN(Margins[RIGHT]))
				width = pw - Margins[RIGHT] - Margins[LEFT];
		}		

		if(Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(width) || Float.isNaN(height)) 
			throw new Error("Insuficent information provided to calculate boundries.");

		region = new Rect(x, y, width, height);
		return region;
	}

	public final GUIRenderer getGUIManager() {
		GUIView group = this;
		while(!(group instanceof GUIRenderer))
			group = group.Parent;

		return (GUIRenderer)group;
	}

	public boolean hasFocus() { return getGUIManager().Focused == this; }
	public boolean getFocus() { return getGUIManager().giveFocusTo(this); }

	public abstract boolean canTakeFocus();

	public void update(float elapsedTime) { }

	public Point getParentalOffset() {
		Point offset = Point.ZERO.clone();
		GUIView group = Parent;
		while(group.Parent != null) {
			offset.plusEquals(group.getPosition());
			group = group.Parent;
		}
		return offset;
	}
	
	public void setPosition(float x, float y) {
		region.setX(x);
		region.setY(y);
	}
}
