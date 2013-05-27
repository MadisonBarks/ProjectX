package com.focused.projectf.players;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.SelectableEntity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.global.actionButtons.ActionButtonManager;

public class Selection{
	protected static List<SelectableEntity> Selection = new Vector<SelectableEntity>();
	
	/**
	 * Removes buildings if any units are in the selection
	 */
	public static void prune() {
		if(Selection.size() > 1) {
			if(!isJustBuildings()) {
				for(int u = 0; u < Selection.size(); u++) {
					SelectableEntity ent = Selection.get(u);
					if(ent instanceof Unit) {
						Unit unit = (Unit)ent;
						if(!Player.canIControl(unit)) 
							Selection.remove(u--);
					} else 
						Selection.remove(u--);
				}
			}	
		}
	}

	public static void add(SelectableEntity entity) {
		if(!Selection.contains(entity)) {
			Selection.add(entity);
			entity.onSelected();
			ActionButtonManager.selectionChanged();
		}
	}
	public static void add(SelectableEntity... entities) {
		for(SelectableEntity ent : entities)
			add(ent);
	}
	public static void add(List<SelectableEntity> entities) {
		for(SelectableEntity ent : entities)
			add(ent);
	}

	public static void remove(SelectableEntity entity) {
		if(Selection.remove(entity))
			entity.onDeselected();
		ActionButtonManager.selectionChanged();
	}
	public static void remove(SelectableEntity... entities) {
		for(SelectableEntity ent : entities)
			remove(ent);
	}

	public static void removeOfType(Class<? extends SelectableEntity> type) {
		for(int i = 0; i < Selection.size(); i++)
			if(type.isInstance(Selection.get(i)))
				remove(Selection.get(i));
	}
	public static void removeOfType(UnitType type) {
		for(int i = 0; i < Selection.size(); i++) {
			SelectableEntity ent = Selection.get(i);
			if(ent instanceof Unit)
				if(((Unit)ent).getType() == type)
					remove(ent);
		}
	}

	public static boolean isJustBuildings() {
		if(Selection.size() == 0)
			return false;
		
		for(int i = 0; i < Selection.size(); i++) {
			SelectableEntity ent = Selection.get(i);
			if(!(ent instanceof Building))
				return false;
		}
		return true;
	}
	public static boolean isJustUnits() {
		for(int i = 0; i < Selection.size(); i++) {
			SelectableEntity ent = Selection.get(i);
			if(!(ent instanceof Unit))
				return false;
		}
		return true;
	}

	public static UnitType getDominantUnitType() {
		UnitType type = UnitType.None;
		for(int i = 0; i < Selection.size(); i++) {
			SelectableEntity ent = Selection.get(i);
			if(ent instanceof Unit)
				if(((Unit)ent).getType().ActionStackPriority > type.ActionStackPriority)
					type = ((Unit) ent).getType();
		}
		return type;
	}

	public static void clear() {
		for(int i = 0; i < Selection.size(); i++) {
			remove(Selection.get(i));
			i--;
		}
	}

	public static void setAndFocusOn(List<SelectableEntity> newSelection) {
		clear();
		add(newSelection);
		ActionButtonManager.selectionChanged();
	}
	public static void setAndFocusOn(SelectableEntity newSelection) {
		clear();
		add(newSelection);
	}
	public static void set(SelectableEntity entity) {
		clear();
		add(entity);
	}
	public static void set(List<SelectableEntity> selection) {
		clear();
		add(selection);
	}
	public static boolean contains(SelectableEntity entity) {
		return Selection.contains(entity);
	}
	public static int size() { return Selection.size(); }

	public static BuildingType getBuildingType() {
		for(SelectableEntity entity : Selection)
			if(entity instanceof Building)
				return ((Building)entity).getType();

		return BuildingType.Armory;
	}

	public static Iterator<SelectableEntity> iterator() {
		return Selection.listIterator();
	}
	public static List<Unit> getUnits() {
		List<Unit> units = new ArrayList<Unit>();
		for(SelectableEntity e :Selection)
			if(e instanceof Unit)
				units.add((Unit) e);
		return units;
	}
	/**
	 * Gets the building currently selected, or null if units or nothing is selected.
	 * @return
	 */
	public static Building getBuilding() {
		for(SelectableEntity e : Selection)
			if(e instanceof Building)
				return (Building)e;
		return null;
	}
	public static boolean isResource() {
		
		return false;
	}
	public static SelectableEntity getSingle() {
		if(Selection.size() > 0) 
			return Selection.get(0);
		return null;
	}
	public static List<SelectableEntity> getAll() {
		return Selection;
	}
}