package com.focused.projectf.input;

import org.lwjgl.input.Keyboard;


public class KeyCombo {

	public static final int SHIFT		= 0x1;
	public static final int CTRL		= 0x2;
	public static final int ALT		= 0x4;
	
	public final int keyCode;
	public final boolean Shift, Ctrl, Alt;

	public KeyCombo(int key, boolean shift, boolean ctrl, boolean alt) {
		this.keyCode = key;
		Shift = shift;
		Ctrl = ctrl;
		Alt = alt;
	}

	public KeyCombo(int key, int shiftKeys) {
		this.keyCode = key;
		Shift	= (shiftKeys & SHIFT) > 0;
		Ctrl	= (shiftKeys & CTRL) > 0;
		Alt 	= (shiftKeys & ALT) > 0;
	}
	public KeyCombo(int key) {
		this.keyCode = key;
		Shift = false;
		Ctrl = false;
		Alt = false;
	}

	public KeyCombo(KeyEvent keyEvent) {
		keyCode	= keyEvent.KeyId;
		Shift	= KeyEvent.Shift;
		Ctrl	= KeyEvent.Ctrl;
		Alt		= KeyEvent.Alt;
	}

	public boolean equals(KeyCombo combo) {
		return keyCode == combo.keyCode && Shift == combo.Shift &&
				Alt == combo.Alt && Ctrl == combo.Ctrl;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(Ctrl)
			sb.append("Ctrl+");
		if(Alt)
			sb.append("Alt+");
		if(Shift)
			sb.append("Shift+");
		
		sb.append(Keyboard.getKeyName(keyCode));
		
		return sb.toString();
	}
}
