package com.focused.projectf.gui;


import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.MouseEvent;

public class Switch extends GUIView {

	protected Runnable OnOn, OnOff;
	protected Rect Slider;
	public boolean State = true;

	private float SlidePos = 0.0f;
	
	public static final float EDGE_OFFSET = 5;

	public Switch(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		this(parent, top, left, bottom, right, width, height, true);
	}
	public Switch(GUIGroup parent, float top, float left, float bottom, float right, float width, float height, boolean on) {
		this(parent, top, left, bottom, right, width, height, on, null, null);
	}
	public Switch(GUIGroup parent, float top, float left, float bottom, float right, float width, float height, boolean on, Runnable switchOn, Runnable switchOff) {
		super(parent, top, left, bottom, right, width, height);
		OnOn = switchOn;
		OnOff = switchOff;
		State = on;
		recalc();
	}
	@Override
	public void draw(float time) {

		Rect smaller = new Rect(region.getX() + EDGE_OFFSET, region.getY() + EDGE_OFFSET, region.getWidth() - EDGE_OFFSET * 2, region.getHeight() - EDGE_OFFSET * 2);
		Canvas.fillRectangle(smaller, (State)? Color.GREEN : Color.BLACK);
		Canvas.drawRectangle(smaller, 3, Color.GRAY);

		if(State) {
			if(SlidePos < 1) SlidePos += Math.min(getWidth() * time, 1 - SlidePos);
		} else {
			if(SlidePos > 0) SlidePos -= Math.min(getWidth() * time, SlidePos);
		}
		
		Slider = new Rect(region.getX() + SlidePos * getWidth() / 2, 
				region.getY(), region.getWidth() / 2 + EDGE_OFFSET, region.getHeight());
		Canvas.fillRectangle(Slider, Color.BLACK);
		Canvas.drawRectangle(Slider, 3, Color.GRAY);
	}
	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if(event.Button == MouseEvent.BUTTON_LEFT && event.State == ButtonState.Pressed) {
			if(Slider.contains(event.Position))
				State = !State;
			recalc();
			return true;
		}
		return false;
	}
	
	private void recalc() {
		Slider = new Rect((State)? region.getX() + getWidth() / 2 : region.getX(), 
				region.getY(), region.getWidth() / 2 + EDGE_OFFSET, region.getHeight());
	}
	@Override
	public boolean canTakeFocus() {
		return true;
	}
}
