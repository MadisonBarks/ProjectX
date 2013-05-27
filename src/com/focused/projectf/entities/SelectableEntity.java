package com.focused.projectf.entities;

import com.focused.projectf.Point;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.interfaces.ISelectable;
import com.focused.projectf.players.Selection;

public abstract class SelectableEntity extends Entity implements ISelectable, IEntity {
	
	/** 
	 * A unique id for any entity that can be selected. 
	 * Note: The current assignment method assumes no more than 2^32 different entities will be 
	 * used in the same game session ever. This might be a mistake, and if so, a better system
	 * of unique id assignment lookup will be needed. It will be fine for now.
	 */
	public final int UniqueID;
	protected int nextId;
	
	public SelectableEntity(Point pos, Bounding bounds) {
		super(pos, bounds);
		UniqueID = nextId++;
		if(nextId == -1) 
			throw new Error("Unique ID system must be changed to acomedate more than 2^32 selectable entities. This will be fun");
	}
	public SelectableEntity() {
		super();
		UniqueID = nextId++;
		if(nextId == -1) 
			throw new Error("Unique ID system must be changed to acomedate more than 2^32 selectable entities. This will be fun");
	}
	
	public void onSelected() { }
	public void onDeselected() { }
	public boolean isSelected() {
		return Selection.contains(this);
	}
	/**
	 * Draw any overlays that may be needed if this entity is selected.
	 * called after all entities are drawn
	 */
	public abstract void drawSelected();
}