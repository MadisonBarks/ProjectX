package com.focused.projectf.input.keybindings;

import java.io.Serializable;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import com.focused.projectf.input.KeyCombo;

public class KeyBinder implements Serializable {

	private static final long serialVersionUID = -8112098826879393422L;

	private HashMap<KeyCombo, KeyAction> keyBindings;
	
	public KeyBinder() {
		keyBindings = new HashMap<KeyCombo, KeyAction>();
		keyBindings.put(new KeyCombo(Keyboard.KEY_ESCAPE), 		KeyAction.Pause_Game);
		keyBindings.put(new KeyCombo(Keyboard.KEY_PERIOD), 		KeyAction.Find_Idle_Villager);
		keyBindings.put(new KeyCombo(Keyboard.KEY_SLASH),		KeyAction.Find_Idle_Research_Building);
		keyBindings.put(new KeyCombo(Keyboard.KEY_T), 			KeyAction.Zoom_To_Last_Event);
	}
	
	public boolean ExecuteKeyCombo(KeyCombo combo) {
		KeyAction action = keyBindings.get(combo);
		
		if(action == null) {
			//System.out.println("Unassigned key binding pressed: " + combo.toString());
			return false;
		}
		ExecuteKeyAction(action);
		return true;
	}
	
	public void loadKeyBindings(String file) {
		
	}
	
	public void ExecuteKeyAction(KeyAction action) {
		
		switch(action.Category) {
			case BuildingManagement:
				break;
			case CameraControl:
				break;
			case General:
				break;
			case Other:
				break;
			case UnitAction:
				break;
			case UnitManagement:
				break;
			case UnitSelection:
				break;
			default:
				break;
		}
	}
}
