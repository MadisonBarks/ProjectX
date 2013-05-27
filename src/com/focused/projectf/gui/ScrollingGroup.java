package com.focused.projectf.gui;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.MouseEvent;

public class ScrollingGroup extends GUIGroup {

	public float ScrollY = 0;
	public float maxScrollY = 0;

	public ScrollingGroup(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		this(parent, top, left, bottom, right, GUIView.UNSET, GUIView.UNSET, null);
	}
	public ScrollingGroup(GUIGroup parent, float top, float left, float bottom, float right) {
		this(parent, top, left, bottom, right, GUIView.UNSET, GUIView.UNSET);
	}

	public ScrollingGroup(GUIGroup parent, float top, float left, float bottom, float right, float width, float height, Color fillColor) {
		super(parent, top, left, bottom, right, width, height, fillColor);
	}

	public void recalculate() {
		maxScrollY = 0;
		for(int i = 0; i < Children.size(); i++) {
			GUIView v = Children.get(i);
			maxScrollY = Math.max(maxScrollY, v.getHeight() + v.getPosition().Y + 10);
		}

		maxScrollY = Math.max(0, maxScrollY - region.getHeight());
	}

	public void draw(float elapsed) {
		recalculate();

		if(!Vissible) return;

		GL11.glPushMatrix();
		GL11.glTranslatef(region.getX(), region.getY() - ScrollY, 0);

		Canvas.pushClip(region.moved(getParentalOffset()));
		Canvas.fillRectangle(new Rect(0, 0, getWidth(), getHeight() + ScrollY), Color.HALF_BLACK);
		for(GUIView v : Children) 
			v.draw(elapsed);

		Canvas.popClip();
		GL11.glPopMatrix();
	}

	public void addView(GUIView view) {
		super.addView(view);
	}

	public boolean onMouseEvent(MouseEvent event) {

		if(event.ScrollWheelChange != 0) { ScrollY += event.ScrollWheel / -5f; }
		recalculate();
		ScrollY = Math.max(0, ScrollY);
		ScrollY = Math.min(maxScrollY + 5, ScrollY);

		event.Position.minusEquals(region.getTopLeft().plus(0, ScrollY));

		for(int i = Children.size() - 1; i >= 0; i--) {
			GUIView view = Children.get(i);
			if(view.getMouseEventPickupRect().contains(event.Position.minus(0, ScrollY))) {
				if(view.onMouseEvent(event))
					return true;
			}
		}

		return true;
	}
}
