package com.focused.projectf.input;

public class KeyEvent {
	
	public final long nanoTime;
	public final ButtonState State;
	public final int KeyId;
	public static boolean Shift, Ctrl, Alt;
	public final char KeyChar;
	
	public KeyEvent(int key, char c, ButtonState state, long time) {
		KeyId = key;
		State = state;
		nanoTime = time;
		KeyChar = c;
		
		Shift = Input.getShift();  
		Ctrl  = Input.getCtrl();  
		Alt   = Input.getAlt(); 
	}
}