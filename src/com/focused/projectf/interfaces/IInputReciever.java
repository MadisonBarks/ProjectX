package com.focused.projectf.interfaces;

import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;

public interface IInputReciever {
	/**
	 * return true to absorb this mouse event so other receivers don't get it
	 */
	public boolean onKeyEvent(KeyEvent event);
	/**
	 * return true to absorb this mouse event so other receivers don't get it
	 */
	public boolean onMouseEvent(MouseEvent event);
}
