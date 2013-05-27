package com.focused.projectf.global.actionButtons;

import java.util.List;

import com.focused.projectf.ai.UnitRunnable;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.Unit;

public class ActionButtonSet {
	public static final ActionButtonSet BuildingDefault = null;

	public static final ActionButtonSet UnitDefault = new ActionButtonSet("Default")
	.set(3, 0, new RunnableActionButton("gui/actionButtons/die.png", new UnitRunnable() {
		public void run(List<Unit> units) { if(units.size() > 0) units.get(0).die(); } }));

	public static final ActionButtonSet Villager = new ActionButtonSet("Villager Actions");	

	public static final void populateUnitTypes() {
		ActionButtonSet BuildEcon = new ActionButtonSet("Villager-Build-Econ");
		Villager.set(0, 0, new ActionSetActionButton("gui/actionButtons/build-econ.png", null, null, BuildEcon));
		BuildEcon.set(0, 0, new BuildingActionButton(BuildingType.House));
		BuildEcon.set(1, 0, new BuildingActionButton(BuildingType.Market));
		BuildEcon.set(2, 0, new BuildingActionButton(BuildingType.Farm));
		BuildEcon.set(3, 0, new BuildingActionButton(BuildingType.Resource_Deposit));
		BuildEcon.set(4, 0, new BuildingActionButton(BuildingType.Warehouse));
		BuildEcon.set(0, 1, new BuildingActionButton(BuildingType.Farm));

		ActionButtonSet BuildWar = new ActionButtonSet("Villager-Build-War");
		Villager.set(1, 0, new ActionSetActionButton("gui/actionButtons/build-war.png", null, null, BuildWar));
		BuildWar.set(0, 0, new BuildingActionButton(BuildingType.Armory));
		BuildWar.set(1, 0, new BuildingActionButton(BuildingType.Ranged_Weapons));
		BuildWar.set(2, 0, new BuildingActionButton(BuildingType.Archery_Tower));

		Villager.set(3, 0, new RunnableActionButton("gui/actionButtons/die.png", new UnitRunnable() {
			public void run(List<Unit> units) { if(units.size() > 0) units.get(0).die(); } }));
	}


	public final String SetName;
	public final ActionButton[][] Buttons;

	public ActionButtonSet(String name) {
		SetName = name;
		Buttons = new ActionButton[5][4];
	}

	public ActionButtonSet set(int x, int y, ActionButton button) {
		Buttons[x][y] = button;
		return this;
	}
}
