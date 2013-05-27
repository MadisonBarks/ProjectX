package com.focused.projectf.input.keybindings;

/**
 * A enum listing all key bindable actions.
 * This will allow users to create custom key bindings to suit their preferences
 * @author josh
 */
public enum KeyAction {

	Find_Idle_Villager				(KeyActionCategory.UnitSelection),
	Kill_Selection					(KeyActionCategory.UnitManagement),
	
	Find_Idle_Research_Building		(KeyActionCategory.BuildingManagement),
	Zoom_To_Last_Event				(KeyActionCategory.CameraControl), 
	Pause_Game						(KeyActionCategory.General),
	;
	public final KeyActionCategory Category;
	KeyAction(KeyActionCategory category) {
		Category = category;
	}
	
}