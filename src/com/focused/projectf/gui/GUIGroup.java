package com.focused.projectf.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.MouseEvent;

public class GUIGroup extends GUIView {

	public ArrayList<GUIView> Children;	
	public Color fillColor;//	 	= Color.fromHex("777777");
	public Color borderColor		= Color.fromHex("444444");
	public float borderWidth		= 1f;
	public boolean hideOverflow 	= false;

	public GUIGroup(GUIGroup parent, float top, float left, float bottom, float right) {
		super(parent, top, left, bottom, right);
		Children = new ArrayList<GUIView>();
	}
	public GUIGroup(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		this(parent, new float[] { top, left, bottom, right }, width, height);
	}
	public GUIGroup(GUIGroup parent, float top, float left, float bottom, float right, float width, float height, Color fillColor) {
		this(parent, new float[] { top, left, bottom, right }, width, height);
		this.fillColor = fillColor;
	}
	public GUIGroup(GUIGroup parent, float[] margins, float width, float height) {
		super(parent, margins, width, height);
		Children = new ArrayList<GUIView>();
	}
	public GUIGroup(GUIGroup parent, Point position, float width, float height) {
		super(parent, position, width, height);
		Children = new ArrayList<GUIView>();
	}

	@Override
	public void draw(float time) {		
		if(!Vissible) return;

		if(fillColor != null)		Canvas.fillRectangle(region, fillColor);
		if(borderColor != null)		Canvas.drawRectangle(region, borderWidth, borderColor);
		if(hideOverflow)			Canvas.pushClip(region);

		ErrorManager.GLErrorCheck();
		GL11.glPushMatrix();
		GL11.glTranslatef(region.getX(), region.getY(), 0);
		ErrorManager.GLErrorCheck();

		for(GUIView v : Children) {
			v.draw(time);
			ErrorManager.GLErrorCheck();
		}
		GL11.glPopMatrix();
		ErrorManager.GLErrorCheck();

		if(hideOverflow) 			Canvas.popClip();		
	}

	public void update(float time) {
		for(GUIView v : Children)
			v.update(time);
	}

	public void addView(GUIView child) {
		if(Children.contains(child))
			ErrorManager.logWarning("GUIView shouldn't be added to GUIGroup more than once!", 
					new Exception("GUIView shouldn't be added to GUIGroup more than once!"));
		Children.add(child);
		child.Parent = this;
	}
	public void removeView(GUIView view) {
		Children.remove(view);
	}

	public Rect getAreaOnScreen() {
		Rect rect = region.clone();
		GUIGroup group = this;

		while(group != null) {
			rect.translate(group.region.getLeft(), group.region.getTop());
			group = group.Parent;
		}
		return rect;
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		event.Position.minusEquals(region.getTopLeft());

		for(int i = Children.size() - 1; i >= 0; i--) {
			GUIView view = Children.get(i);
			if(view.getMouseEventPickupRect().contains(event.Position))
				if(view.onMouseEvent(event))
					return true;
		}

		event.Position.plusEquals(region.getTopLeft());

		if(event.State == ButtonState.Pressed)
			getGUIManager().clearFocus();

		return false;
	}

	@Override
	public boolean canTakeFocus() { return false; }

	public void makeLastRendered(GUIView view) {
		view.Parent.removeView(view);
		view.Parent.addView(view);
	}

	public Rect Layout() {
		Rect ret = super.Layout();
		if(Children != null)
			for(GUIView view : Children)
				view.Layout();
		return ret;
	}
	protected boolean Vissible = true;
	public void setVissibility(boolean b) { Vissible = b; }

	public void setMargins(float top, float left, float bottom, float right) {
		Margins[TOP]		= top;
		Margins[LEFT]		= left;
		Margins[BOTTOM]		= bottom;
		Margins[RIGHT]		= right;
	}

	public String[] getResources() {
		return new String[] {
				"mapElements/groundElements.smap"
		};
	}
}