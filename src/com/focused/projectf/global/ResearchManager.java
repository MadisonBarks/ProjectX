package com.focused.projectf.global;

import java.util.HashMap;
import java.util.Vector;

import com.focused.projectf.Technology;
import com.focused.projectf.ai.UnitStats;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.global.actionButtons.ActionButtonManager;
import com.focused.projectf.players.Player;

public class ResearchManager {

	private static HashMap<Player, Vector<Boolean>> Researched;
	private static HashMap<Player, HashMap<UnitType, UnitStats>> Stats;
	private static HashMap<Player, HashMap<BuildingType, BuildingStats>> BuildingStats;

	private static Vector<Technology> BeingResearched;

	public static void initialize() {
		Researched = new HashMap<Player, Vector<Boolean>>();
		Stats = new HashMap<Player, HashMap<UnitType, UnitStats>>();
		BuildingStats = new HashMap<Player, HashMap<BuildingType, BuildingStats>>();

		BeingResearched = new Vector<Technology>();

		for(Player p : Player.ConnectedPlayers) {
			HashMap<UnitType, UnitStats> hmu = new HashMap<UnitType, UnitStats>();
			Stats.put(p, hmu);
			for(UnitType type : UnitType.values())
				hmu.put(type, type.getStartingStats());

			HashMap<BuildingType, BuildingStats> hmb = new HashMap<BuildingType, BuildingStats>();
			BuildingStats.put(p, hmb);
			for(BuildingType type : BuildingType.values())
				hmb.put(type, new BuildingStats());
		}
	}

	public static void beginGame(Technology startingAge) {

		for(int i = 0; i < Player.ConnectedPlayers.size(); i++) {
			Vector<Boolean> bools = new Vector<Boolean>();
			Researched.put(Player.ConnectedPlayers.get(i), bools);

			for(int j = 0; j < Technology.values().length; j++) {
				bools.add(new Boolean(!Technology.values()[j].ComesBeforeAge(startingAge)));
			}
		}
	}

	public static boolean hasBeenResearched(Player player, Technology tech) {
		return Researched.get(player).get(tech.ordinal());
	}

	public static void onResearchComplete(Player player, Technology tech) {
		Researched.get(player).set(tech.ordinal(), true);
		ActionButtonManager.updateGUI();
	}
	public static UnitStats getStats(Player player, UnitType unitType) {
		return Stats.get(player).get(unitType);
	}
	public static UnitStats getStats( Unit unit) {
		return getStats(unit.getOwner(), unit.getType());
	}

	public static BuildingStats getStats(Player player, BuildingType unitType) {
		return BuildingStats.get(player).get(unitType);
	}
	public static BuildingStats getStats(Building site) {
		return getStats(site.getOwner(), site.getType());
	}
	
	public static void setBeingResearched(Technology tech, boolean beingResearched) {
		if(beingResearched) {
			BeingResearched.add(tech);
		} else {
			BeingResearched.remove(tech);
		}
	}

	public static boolean isBeingResearched(Technology tech) {
		return BeingResearched.contains(tech);
	}

	public static int getVillagerResourceCarryingCapacity() {
		return 10;
	}

}
