package com.focused.projectf.managers;


import java.util.List;

import com.focused.projectf.Map;
import com.focused.projectf.entities.Building;
import com.focused.projectf.input.keybindings.KeyAction;
import com.focused.projectf.input.keybindings.KeyActionCategory;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;

public class BuildingManager {

	public static void executeAction(KeyAction action) {
		if(action.Category == KeyActionCategory.BuildingManagement) {
			switch(action) {
				case Find_Idle_Research_Building:
					Map map = Map.get();
					List<Building> blds = map.getBuildings();
					for(Building b : blds)
						if(b.getOwner() == Player.getThisPlayer())
							//if(b.getType().isResearcher)
								Selection.setAndFocusOn(b);
					break;

				default:
					break;
			}
		}


	}

}
