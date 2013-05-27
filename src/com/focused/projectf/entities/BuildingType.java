package com.focused.projectf.entities;

import com.focused.projectf.Technology;
import com.focused.projectf.global.actionButtons.ActionButtonSet;
import com.focused.projectf.global.actionButtons.ProduceUnitButton;
import com.focused.projectf.global.actionButtons.ResearchTechButton;

public enum BuildingType {
	
	// put underscores where spaces would be for the full name.
	Town_Square			(1001, 4, 4, "buildings/house2.png", 250, 20, 50, 50, 0,
			new ActionButtonSet("")
					.set(0, 0, new ProduceUnitButton(UnitType.Villager))
					.set(0, 3, new ResearchTechButton(Technology.Age1,
								new ResearchTechButton(Technology.Age2,
								new ResearchTechButton(Technology.Age3,
								new ResearchTechButton(Technology.Age4)))))
					),
	House				(1002, 2, 2, "buildings/house1.png", 30, 0, 0, 0, 0),
	Market				(1003, 3, 3, "buildings/template.png"),
	Armory				(1004, 4, 4, "buildings/armory.png", 200, 0, 0, 0, 0,
			new ActionButtonSet("")
						.set(0, 0, new ProduceUnitButton(UnitType.Swordsman))
	),
	Archery_Tower		(1004, 1, 1, "buildings/tower1.png", 25, 0, 100, 0, 0,
			new ActionButtonSet("")
					.set(0, 0, new ProduceUnitButton(UnitType.Archer))),
	Ranged_Weapons		(1005, 4, 4, "buildings/template.png"),
	Resource_House		(1006, 2, 2, "buildings/template.png"), 
	Warehouse			(1007, 3, 3, "buildings/template.png"), 
	Farm				(1008, 3, 3, "buildings/template.png"), 
	Resource_Deposit	(1009, 2, 2, "buildings/template.png", 80, 0, 0, 0, 0),
	Harbor				(1010, 3, 3, "buildings/dock.png", 200, 0, 0, 0, 0, 
			new ActionButtonSet("Dock")
				.set(0, 0, new ProduceUnitButton(UnitType.Fishing_Boat)));

	public int typeId;
	public String texturePath;
	public String iconPath;
	public int widthInTiles, heightInTiles;
	public int WoodCost, FoodCost, StoneCost, GoldCost, RadiumCost;
	public int MaxGarrison;
	public int MaxHealth = 2000;
	public ActionButtonSet ActionButtons = ActionButtonSet.BuildingDefault;

	BuildingType(int id, String texture) {
		this(id, 2, 2, texture);
	}
	BuildingType(int id, int width, int height, String texture) {
		this(id, width, height, texture, 50, 0, 0, 0, 0);
	}
	BuildingType(int id, int width, int height, String texture, int woodCost, int foodCost, int stoneCost, int goldCost, int radiumCost) {
		this(id, width, height, texture, "gui/actionButtons/build-econ.png", woodCost, foodCost, stoneCost, goldCost, radiumCost);
	}

	BuildingType(int id, int width, int height, String texture, int woodCost, int foodCost, int stoneCost,
			int goldCost, int radiumCost, ActionButtonSet actionButtons) {
		this(id, width, height, texture, woodCost, foodCost, stoneCost, goldCost, radiumCost);
		ActionButtons = actionButtons;
	}

	BuildingType(int id, int width, int height, String texture, int woodCost, int foodCost, int stoneCost,
			int goldCost, int radiumCost, ActionButtonSet actionButtons, int maxGarrison) {
		this(id, width, height, texture, woodCost, foodCost, stoneCost, goldCost, radiumCost, actionButtons);
		MaxGarrison = maxGarrison;
	}

	BuildingType(int id, int width, int height, String texture, String icon, int woodCost, int foodCost, int stoneCost, int goldCost, int radiumCost) {
		widthInTiles 	= width;
		heightInTiles 	= height;
		typeId 			= id;
		texturePath 	= texture;
		iconPath		= icon;
		WoodCost 		= woodCost;
		FoodCost 		= foodCost;
		StoneCost 		= stoneCost;
		GoldCost 		= goldCost;
		RadiumCost 		= radiumCost;
	}


	public boolean AcceptsResources() {
		return this == Town_Square || this == Resource_Deposit;
	}
}
