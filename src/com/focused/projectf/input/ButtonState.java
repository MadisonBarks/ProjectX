package com.focused.projectf.input;

public enum ButtonState {
	/** The button was just pressed since the last frame was rendered  */
	Pressed		(true,	true),
	/** The button is being held down but was not just pressed since the last frame  */
	Held		(true,	false),
	/** The button was just released since the last frame was rendered  */
	Released	(false,	true),
	/** The button is not being held down but was not just released since the last frame  */
	Depressed	(false,	false),
	;
	public final boolean Down;
	public final boolean Change;
	ButtonState(boolean down, boolean change) {
		Down = down;
		Change = change;
	}
	public static ButtonState get(boolean now, boolean before) {
		if(now) {
			if(before)	return Held;
			else		return Pressed;
		} else {
			if(before)	return Released;
			else		return Depressed;
		}
	}
}
