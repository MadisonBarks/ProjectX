package com.focused.projectf.gui;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.utilities.FMath;

public class Slider extends GUIView {

	protected float Value;

	public Slider(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		super(parent, top, left, bottom, right, width, height);
		Value = 0.5f;
	}

	public void setValue(float v) {
		Value = v;
	}

	public float getValue() { 
		return Value;
	}

	@Override
	public void draw(float time) {

		float w = getWidth();
		float h = getHeight();
		float vCenter = (getWidth() - 5) * Value;
		GL11.glPushMatrix();
		GL11.glTranslatef(region.getX(), region.getY(), 0);

		GL11.glLineWidth(5);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4f(0.5f, 0.5f, 0.5f, 1);
		GL11.glVertex2f(w - 5, h / 2);
		GL11.glVertex2f(vCenter, h / 2);
		GL11.glEnd();

		GL11.glLineWidth(7);

		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(1, 1, 1);
		GL11.glVertex2f(5, h / 2);
		GL11.glVertex2f(vCenter, h / 2);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glColor3f(0, 0, 0);
		GL11.glVertex2f(vCenter - 6, 0);		
		GL11.glVertex2f(vCenter - 6, h - 9);
		GL11.glVertex2f(vCenter, h - 3);
		GL11.glVertex2f(vCenter + 6, h - 9);
		GL11.glVertex2f(vCenter + 6, 0);
		GL11.glVertex2f(vCenter - 6, 0);
		GL11.glEnd();

		GL11.glLineWidth(2);
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex2f(vCenter - 6, 0);
		GL11.glVertex2f(vCenter + 6, 0);
		GL11.glVertex2f(vCenter + 6, h - 9);
		GL11.glVertex2f(vCenter, h - 3);
		GL11.glVertex2f(vCenter - 6, h - 9);
		GL11.glVertex2f(vCenter - 6, 0);		
		GL11.glEnd();

		GL11.glPopMatrix();
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if(event.Button == MouseEvent.BUTTON_LEFT && event.State.Down) {
			Value = FMath.clamp((event.Position.X - 6 -  region.getX()) / (getWidth() - 12), 1, 0);
			return true;
		}
		return false;
	}

	@Override
	public boolean canTakeFocus() {
		return true;
	}
}