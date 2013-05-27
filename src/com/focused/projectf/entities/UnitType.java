package com.focused.projectf.entities;

import com.focused.projectf.ai.UnitStats;
import com.focused.projectf.global.actionButtons.ActionButtonSet;
import com.focused.projectf.interfaces.IDamageable.DamageType;

/**
 * Lists every possible type of unit for base stats lookup and identification
 */
public enum UnitType {


	Villager				(UnitCategory.Utility, 			false,	true, 	false,	false,	"units/utility/villager", 18, 5, ActionButtonSet.Villager),
			
	Archer					(UnitCategory.RangedInfantry, 	false,	true, 	false,	true,	"untis/ranged/archer", 20, 7),
	Musketer				(UnitCategory.GunnerInfantry, 	false,	true, 	false,	true,	"untis/ranged/musketer", 24, 8),
	Swordsman				(UnitCategory.Infantry,			false,	true,	false,	false,	"units/infantry/swordsman", 20, 6),
	
	None					(),
	Fishing_Boat			(UnitCategory.Ship,				true,	false,	false,	false, "units/ships/fishing", 30, 2),
	/** can generate food from any pocket of deep water. Less effective than a Fishing boat but good for long term food income. */
	Deap_Sea_Fishing_Boat	(UnitCategory.Ship,				true,	false,	false,	false, "units/ships/deapSeaFishing", 30, 2),
	
	Transport_Ship			(UnitCategory.Ship,				true,	false,	false,	false, "units/ships/transport", 40, 2),
	/** A slower transport ship with a much higher unit capacity and a stronger hull.  */
	Mass_Transport_Ship		(UnitCategory.Ship,				true,	false,	false,	false, "units/ships/massTransport", 40, 2),
	
	
	;

	public final String ResourceFolder; 
	public final boolean OverWater;
	public final boolean OverLand;
	public final boolean IsAirborn;
	public int FoodCost = 50, WoodCost = 0, GoldCost = 0, StoneCost = 0, RadiumCost = 0;
	public final int Size;
	public final int ActionStackPriority;
	public final boolean isRanged;
	public final int index;
	public final String Name;
	public final String IconPath = "gui/actionButtons/die.png";

	public final UnitCategory Category;
	
	public final ActionButtonSet ActionButtons;
	
	UnitType() {
		this(UnitCategory.Other, false, true, false, "other");
	}

	UnitType(UnitCategory category, boolean overWater, boolean overLand, boolean isAirborn, String folder) {
		this(category, overWater, overLand, isAirborn, false, folder, 20, 2);
	}

	UnitType(UnitCategory category, boolean overWater, boolean overLand, boolean airborn, boolean ranged, String folder, int size, int actionStackPriority) {
		this(category, overWater, overLand, airborn, ranged, folder, size, actionStackPriority, ActionButtonSet.UnitDefault);
	}
	UnitType(UnitCategory category, boolean overWater, boolean overLand, boolean airborn, boolean ranged, String folder, int size, int actionStackPriority, ActionButtonSet actionButtons) {
		if(folder.charAt(folder.length() - 1) == '/')
			ResourceFolder = folder;
		else
			ResourceFolder = folder + '/';
		Category = category;
		OverWater = overWater;
		OverLand = overLand;
		IsAirborn = airborn;
		Size = size;
		ActionStackPriority = actionStackPriority;
		isRanged = ranged;
		index = ordinal();		
		String[] split = folder.split("/");
		Name = split[split.length - 1];
		ActionButtons = actionButtons;
	}

	public UnitStats getStartingStats() {
		// TODO: different stats for different unit types
		return new UnitStats();
	}

	public DamageType getDamageType() {
		return Category.AttackDamageType;
	}
}
