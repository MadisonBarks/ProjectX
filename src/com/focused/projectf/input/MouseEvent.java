package com.focused.projectf.input;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.focused.projectf.Point;

public class MouseEvent {

	/** The left mouse button. Used primarily for selecting entities and clicking buttons */
	public static final int BUTTON_LEFT 		= 0;
	/** The right mouse button. Used primarily for giving entities commands that include a location or other entit(y/ies) */
	public static final int BUTTON_RIGHT 		= 1;
	/** The button under the scroll wheel. Most mice have this, but not all, so try not to use it for anything super important */
	public static final int BUTTON_CENTER 		= 2;
	/** Optional additional button # 1. Only use for special functionalities that aren't required to play the game. */
	public static final int BUTTON_AUX_1 		= 3;
	/** Optional additional button # 2. Only use for special functionalities that aren't required to play the game. */
	public static final int BUTTON_AUX_2 		= 4;
	/** No buttons are pressed. This event was created to tell where the cursor is. */
	public static final int BUTTON_NONE 		= -1;

	public static final float CLICK_TIME 	= 0.3f;

	public Point Position;

	public int Button;
	public ButtonState State;

	public int ScrollWheel;
	public int ScrollWheelChange;

	public boolean consumed = false;
	/*
	public MouseEvent(MouseEvent prev, boolean button) {
		Position = new Point(Mouse.getEventX(), Display.getHeight() - Mouse.getEventY());
		
		if(button) {
			Button = Mouse.getEventButton();
			State = (Mouse.getEventButtonState()) ? 
					(buttonStates[Button].Down) ? ButtonState.Held : ButtonState.Pressed :
						(buttonStates[Button].Down) ? ButtonState.Released : ButtonState.Depressed;
		} else {
			Button = BUTTON_NONE;
			State = null;
		}
		
		if(Button != BUTTON_NONE && State != null) 
			buttonStates[Button] = State;		
		
		ScrollWheel = Mouse.getDWheel();
		ScrollWheelChange = ScrollWheel - prev.ScrollWheel;
		
		//if(!(Position.equals(prev.Position) && State == null))
		System.out.println(toString());
	}

	public MouseEvent(MouseEvent me, int j) {
		Position = me.Position;
		
		Button = j;
		State = (Mouse.isButtonDown(j)) ? 
				(buttonStates[Button].Down) ? ButtonState.Held : ButtonState.Pressed :
					(buttonStates[Button].Down) ? ButtonState.Released : ButtonState.Depressed;

		buttonStates[Button] = State;	
		
		ScrollWheel = Mouse.getDWheel();
		ScrollWheelChange = 0;
	}
	*/
	public MouseEvent() {
		while(Mouse.next()) { }
		Position = new Point(Mouse.getX(), Display.getHeight() - Mouse.getY());

		Button = 0;
		State = null;

		ScrollWheel = Mouse.getDWheel();
		ScrollWheelChange = ScrollWheel;
	}

	public void consume() { consumed = true; }	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("MouseEvent ");
		if(Button != BUTTON_NONE) {
			sb.append(Mouse.getButtonName(Button));
			sb.append(' ');
			sb.append(State.name());
			sb.append(" at ");
			sb.append(Position.toString());
		} else {
			sb.append("move to ");
			sb.append(Position.toString());
		}
		
		return sb.toString();
	}
}